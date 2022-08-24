package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
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
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.repos.CustomerRepo;
import de.fhwedel.pimpl.DateValidationAndFilterHelper;
import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingEditView extends Composite<Component> {
	
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
	private FormLayout bookingForm = new FormLayout();
	
	private Button btnEditBooking = new Button("Buchung speichern", this::onBookingSafeClick);
	private Button btnResetBooking = new Button("Reset");
	
	private HorizontalLayout buttons = new HorizontalLayout(btnEditBooking, btnResetBooking);
	private VerticalLayout view = new VerticalLayout();
	
	private Binder<Booking> binder = new BeanValidationBinder<>(Booking.class);
	
	private Optional<Booking> booking = Optional.empty();
	private Optional<Customer> cust = Optional.empty();
	
	private boolean supervisor = false;
	private LocalDate globalDate = LocalDate.now();
	
	private BookingRepo bookingRepo;
	private CustomerRepo custRepo;
	
	private Optional<Runnable> listener = Optional.empty();
	
	public BookingEditView(BookingRepo bookingRepo, CustomerRepo custRepo) {
		this.bookingRepo = bookingRepo;
		this.custRepo = custRepo;
	}
	
	@Override
	protected Component initContent() {
		status.setItems(ContractState.values());
		status.setLabel("Status");
		
		bookingForm.add(bookingNr, reservation, status, comment, estimatedArrival, arrived,
				estimatedDeparture, departed, price, licensePlate);
		
		bookingForm.setResponsiveSteps(new ResponsiveStep("0", 2));
		
		bookingNr.setEnabled(false);
		reservation.setEnabled(false);
		status.setEnabled(false);
		comment.setEnabled(false);
		estimatedArrival.setEnabled(false);
		arrived.setEnabled(false);
		estimatedDeparture.setEnabled(false);
		departed.setEnabled(false);
		price.setEnabled(false);
		licensePlate.setEnabled(false);
		
		
		
		
		
		buttons.setEnabled(false);
	
		binder.forField(reservation).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getReservation, Booking::setReservation);
		binder.forField(estimatedArrival).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getEstimatedArrival, Booking::setEstimatedArrival);
		binder.forField(arrived).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getArrived, Booking::setArrived);
		binder.forField(estimatedDeparture).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getEstimatedDeparture, Booking::setEstimatedDeparture);
		binder.forField(departed).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getDeparted, Booking::setDeparted);
		binder.bindInstanceFields(this);
		
		
		view.add(bookingForm, buttons);
		return view;
		
	}
	
	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	private void refresh() {
		binder.readBean(booking.orElse(null));
		//bookingForm.setEnabled(booking.isPresent());
		buttons.setEnabled(booking.isPresent());
		reservation.setEnabled(supervisor && booking.isPresent());
		status.setEnabled(supervisor && booking.isPresent());
		comment.setEnabled(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN)));
		estimatedArrival.setEnabled(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.RESERVED))) ;
//								&& DateValidationAndFilterHelper.validateBookingTimeframe(booking.get().getEstimatedArrival(), 
//										booking.get().getEstimatedArrival(), supervisor, globalDate))));
		arrived.setEnabled(booking.isPresent() && supervisor);
		estimatedDeparture.setEnabled(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN)));
		departed.setEnabled(booking.isPresent() && supervisor);
		price.setEnabled(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.RESERVED)));
		licensePlate.setEnabled(booking.isPresent() 
				&& (supervisor 
						|| DateValidationAndFilterHelper.checkStatusHierarchy(booking.get().getStatus(), ContractState.CHECKED_IN)));
		
	}
	
	
	
	void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
		refresh();
	}
	
	void setGlobalDate(LocalDate globalDate) {
		this.globalDate = globalDate;
		refresh();
	}
	
	public Optional<Booking> getBooking() {
		return booking;
	}
	
	public void setBooking(Optional<Booking> booking) {
		this.booking = booking;
		refresh();
	}
	
	public void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
	}
	
//	public void enableView(boolean enable) {
//		
//	}
	
	void saveBooking() {
		btnEditBooking.click();
	}
	
	private void onBookingSafeClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		System.out.println("buchung vorhanden " + booking.isPresent() 
		+ "kunde vorhanden " + cust.isPresent());
		booking.ifPresent(b -> {
			cust.ifPresent(c -> {
				if (binder.writeBeanIfValid(b)) {
					booking = Optional.of(bookingRepo.save(b));
					if (b.getCustomer() == null) {
						c.addBooking(b);
						cust = Optional.of(custRepo.save(c));
					}
					
					
					//refresh();
					listener.ifPresent(Runnable::run);
				}
			});
		});
	}

	public Optional<Customer> getCustomer() {
		return cust;
	}
}
