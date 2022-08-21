package de.fhwedel.pimpl;

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
	
	private BookingRepo bookingRepo;
	private CustomerRepo custRepo;
	
	private Optional<Runnable> listener = Optional.empty();
	
	public BookingEditView(BookingRepo bookingRepo, CustomerRepo custRepo) {
		this.bookingRepo = bookingRepo;
		this.custRepo = custRepo;
	}
	
	@Override
	protected Component initContent() {
		this.status.setItems(ContractState.values());
		this.status.setLabel("Status");
		
//		this.bookingForm.addFormItem(bookingNr, "Buchungsnummer");
//		this.bookingForm.addFormItem(reservation, "Reservierungsdatum");
//		this.bookingForm.addFormItem(status, "Status");
//		this.bookingForm.addFormItem(comment, "Kommentar");
//		this.bookingForm.addFormItem(estimatedArrival, "Anreise Soll");
//		this.bookingForm.addFormItem(arrived, "Anreise Ist");
//		this.bookingForm.addFormItem(estimatedDeparture, "Abreise Soll");
//		this.bookingForm.addFormItem(departed, "Abreise Ist");
//		this.bookingForm.addFormItem(price, "Zimmerpreis");
//		this.bookingForm.addFormItem(licensePlate, "Kfz-Kennzeichen");
		this.bookingForm.add(bookingNr, reservation, status, comment, estimatedArrival, arrived,
				estimatedDeparture, departed, price, licensePlate);
		
		
		
		this.bookingForm.setResponsiveSteps(new ResponsiveStep("0", 2));
		
		buttons.setEnabled(booking.isPresent());
		//bookingForm.setEnabled(booking.isPresent());
		
		
		view.add(bookingForm, buttons);
		
		
		this.binder.forField(reservation).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getReservation, Booking::setReservation);
		this.binder.forField(estimatedArrival).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getEstimatedArrival, Booking::setEstimatedArrival);
		this.binder.forField(arrived).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getArrived, Booking::setArrived);
		this.binder.forField(estimatedDeparture).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getEstimatedDeparture, Booking::setEstimatedDeparture);
		this.binder.forField(departed).withConverter(
				new LocalDateToDateConverter()).bind(Booking::getDeparted, Booking::setDeparted);
		
		this.binder.bindInstanceFields(this);
		
		return view;
		
	}
	
	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	private void refresh() {
		binder.readBean(booking.orElse(null));
		//bookingForm.setEnabled(booking.isPresent());
		buttons.setEnabled(booking.isPresent());
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
	
	private void onBookingSafeClick(com.vaadin.flow.component.ClickEvent<Button> event) {
//		booking.ifPresent(b -> {
//			cust.ifPresent(c -> {
//				
//			});
//			if (binder.writeBeanIfValid(b)) {
//				Booking booking = bookingRepo.save(b);
//				
//				
//				setBooking(Optional.of(booking));
//				
//			}
//		});
		
		booking.ifPresent(b -> {
			cust.ifPresent(c -> {
				if (binder.writeBeanIfValid(b)) {
					c.addBooking(b);
					cust = Optional.of(custRepo.save(c));
				//	booking = Optional.of(bookingRepo.save(b));
					
					refresh();
					listener.ifPresent(Runnable::run);
				}
			});
		});
	}

	public Optional<Customer> getCustomer() {
		return cust;
	}
}
