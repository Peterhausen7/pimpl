package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.model.Guest;
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.repos.CustomerRepo;
import de.fhwedel.pimpl.repos.GuestRepo;
import de.fhwedel.pimpl.DateValidationAndFilterHelper;
import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingEditView extends Composite<Component> {
	
	//Fields of booking class
	private TextField bookingNr = new TextField("Buchungsnummer");
	private DatePicker reservation = new DatePicker("Reservierungsdatum");
	private Select<ContractState> status = new Select<>();
	private TextField comment = new TextField("Kommentar");
	private DatePicker estimatedArrival = new DatePicker("Anreise Soll");
	private DatePicker arrived = new DatePicker("Anreise Ist");
	private DatePicker estimatedDeparture = new DatePicker("Abreise Soll");
	private DatePicker departed = new DatePicker("Abreise Ist");
	private IntegerField price = new IntegerField("Zimmerpreis");
	private TextField licensePlate = new TextField("Kfz-Kennzeichen");
	//Form layout of the booking class fields
	private FormLayout bookingForm = new FormLayout();
	
	//Buttons
	private Button btnSaveBooking = new Button("Buchung speichern", this::onBookingSaveClick);
	private Button btnResetBooking = new Button("Reset", this::onResetBtnClick);
	private Button btnSendBookingConfirmation = new Button("Sende Buchungsbestätigung", this::onConfirmBtnClick);
	private Button btnCheckInBooking = new Button("Buchung einchecken", this::onCheckInBtnClick);
	private Button btnCheckOutBooking = new Button("Buchung auschecken");
	
	//Layouts
	private HorizontalLayout bookingButtons = new HorizontalLayout(btnSaveBooking, btnResetBooking, btnSendBookingConfirmation);
	private HorizontalLayout checkInOutButtons = new HorizontalLayout(btnCheckInBooking, btnCheckOutBooking);
	
	//Main view of this class
	private VerticalLayout view = new VerticalLayout();
	
	//Binder
	private Binder<Booking> binder = new BeanValidationBinder<>(Booking.class);
	
	//The booking to work with
	private Optional<Booking> booking = Optional.empty();
	
	//The customer of the booking
	private Optional<Customer> cust = Optional.empty();
	
	//supervisor flag
	private boolean supervisor = false;
	//The global date field
	private LocalDate globalDate = LocalDate.now();
	
	//CRUD access 
	private BookingRepo bookingRepo;
	private CustomerRepo custRepo;
	private GuestRepo guestRepo;
	
	//listener to refresh parent class
	private Optional<Runnable> listener = Optional.empty();
	
	public BookingEditView(BookingRepo bookingRepo, CustomerRepo custRepo, GuestRepo guestRepo) {
		this.bookingRepo = bookingRepo;
		this.custRepo = custRepo;
		this.guestRepo = guestRepo;
	}
	
	@Override
	protected Component initContent() {
		status.setItems(ContractState.values());
		status.setLabel("Status");
		
		bookingForm.add(bookingNr, reservation, status, comment, estimatedArrival, arrived,
				estimatedDeparture, departed, price, licensePlate);
		bookingForm.setResponsiveSteps(new ResponsiveStep("0", 2));
		
		bookingNr.setReadOnly(true);
		reservation.setReadOnly(true);
		status.setReadOnly(true);
		comment.setReadOnly(true);
		estimatedArrival.setReadOnly(true);
		arrived.setReadOnly(true);
		estimatedDeparture.setReadOnly(true);
		departed.setReadOnly(true);
		price.setReadOnly(true);
		licensePlate.setReadOnly(true);
		btnCheckInBooking.setEnabled(false);
		btnCheckOutBooking.setEnabled(false);
		bookingButtons.setEnabled(false);
	
		//validating the fields
		binder.forField(reservation).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getReservation, Booking::setReservation);
		
		binder.forField(estimatedArrival).withConverter(
				new LocalDateToDateConverter())
		.withValidator(arr -> DateValidationAndFilterHelper.validateArrival(arr, reservation.getValue())
				,"Anreise muss am oder nach Reservierungstag sein")
		.asRequired("Kann nicht Leer sein")
		.bind(Booking::getEstimatedArrival, Booking::setEstimatedArrival);
		
		binder.forField(arrived).withConverter(
				new LocalDateToDateConverter())
		.withValidator(arr -> DateValidationAndFilterHelper.validateArrival(arr, reservation.getValue())
				, "Anreise muss am oder nach Reservierungstag sein")
		.bind(Booking::getArrived, Booking::setArrived);
		
		binder.forField(estimatedDeparture).withConverter(
				new LocalDateToDateConverter())
		.withValidator(dep -> DateValidationAndFilterHelper.validateDeparture(dep, estimatedArrival.getValue())
				,"Abreise muss nach Anreise sein")
		.asRequired("Kann nicht Leer sein")
		.bind(Booking::getEstimatedDeparture, Booking::setEstimatedDeparture);
		
		binder.forField(departed).withConverter(
				new LocalDateToDateConverter())
		.withValidator(dep -> DateValidationAndFilterHelper.validateDeparture(dep, arrived.getValue())
				,"Abreise muss nach Anreise sein")
		.bind(Booking::getDeparted, Booking::setDeparted);
		
		binder.bindInstanceFields(this);
		
		
		view.add(bookingForm, bookingButtons, checkInOutButtons);
		return view;
	}
	
	void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	private void refresh() {
		binder.readBean(booking.orElse(null));
		
		//Sets fields/buttons availability according to business rules etc.
		bookingButtons.setEnabled(booking.isPresent());
		booking.ifPresent(b -> {
			Collection<Guest> guests = guestRepo.findByBooking(b);
			btnSendBookingConfirmation.setEnabled(guests.size() > 0);
		});
		reservation.setReadOnly(!(supervisor && booking.isPresent()));
		status.setReadOnly(!(supervisor && booking.isPresent()));
		comment.setReadOnly(!(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN))));
		
		estimatedArrival.setReadOnly(!(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.RESERVED))));
		
		arrived.setReadOnly(!(booking.isPresent() && supervisor));
		estimatedDeparture.setReadOnly(!(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN))));
		
		departed.setReadOnly(!(booking.isPresent() && supervisor));
		price.setReadOnly(!(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.RESERVED))));
		
		licensePlate.setReadOnly(!(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN))));
		
		btnCheckInBooking.setEnabled(canBeCheckedIn());
	}
	
	void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
		refresh();
	}
	
	void setGlobalDate(LocalDate globalDate) {
		this.globalDate = globalDate;
		refresh();
	}
	
	Optional<Booking> getBooking() {
		return booking;
	}
	
	void setBooking(Optional<Booking> booking) {
		this.booking = booking;
		refresh();
	}
	
	Optional<Customer> getCustomer() {
		return cust;
	}
	
	void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
	}
	
	/**
	 * Checks if the booking is in a state where it can be checked in
	 * @return true if the booking can be checked in
	 */
	private boolean canBeCheckedIn() {
		boolean result = false;
		if (booking.isPresent() && booking.get().getStatus().equals(ContractState.RESERVED)) {
			Collection<Guest> guests = guestRepo.findByBooking(booking.get());
			guests = guests.stream().filter(g -> g.getCheckedIn() != null).toList();
			result = guests.size() >= 1;	
		}
		return result;
	}
	
	/**
	 * Saves the booking by simulating a click on the corresponding button. 
	 */
	void saveBooking() {
		btnSaveBooking.click();
	}
	
	/**
	 * On click event listener of the btnSaveBooking button
	 * Saves a booking according to the set fields
	 * @param event click event
	 */
	private void onBookingSaveClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		booking.ifPresent(b -> {
			cust.ifPresent(c -> {
				if (binder.writeBeanIfValid(b)) {
					booking = Optional.of(bookingRepo.save(b));
					if (b.getCustomer() == null) {
						c.addBooking(b);
						cust = Optional.of(custRepo.save(c));
					}
					listener.ifPresent(Runnable::run);
					
					//If any of the fields are invalid, a notification reminds the user
				} else {
					Notification notification = Notification.show("Invalides Feld");
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					notification.setPosition(Notification.Position.MIDDLE); 
				}
			});
		});
	}
	
	/**
	 * On click event listener of the btnSendBookingConfirmation button
	 * Sends an artificial booking confirmation 
	 * @param event the click event
	 */
	private void onConfirmBtnClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		Notification notification = Notification.show("Buchungsbestätigung verschickt!");
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification.setPosition(Notification.Position.MIDDLE);
	}
	
	/**
	 * On click event listener of the btnCheckInBooking button
	 * Checks the booking in
	 * @param event the click event
	 */
	private void onCheckInBtnClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		booking.ifPresent(b -> {
			//check in can only be performed in the reserved state
			if (b.getStatus().equals(ContractState.RESERVED)) {
				status.setValue(ContractState.CHECKED_IN);
				arrived.setValue(globalDate);
				saveBooking();
				Notification notification = Notification.show("Buchung eingecheckt");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setPosition(Notification.Position.MIDDLE);
			}
		});
	}
	
	/**
	 * On click event listener of the btnResetBooking button
	 * Resets the fields of the booking
	 * @param event the click event
	 */
	private void onResetBtnClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		booking.ifPresent(b -> binder.readBean(b));
	}
}
