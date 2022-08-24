package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.model.Guest;
import de.fhwedel.pimpl.repos.GuestRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingGuestView extends Composite<Component> {

	private ListBox<Guest> guests = new ListBox<>();
	
	private TextField surname = new TextField();
	private TextField prename = new TextField();
	private DatePicker birthDate = new DatePicker();
	private DatePicker checkedIn = new DatePicker();
	private DatePicker checkedOut = new DatePicker();
	private FormLayout guestInfo = new FormLayout();
	
	private Button btnAddGuest = new Button("Neuer Gast", this::onGuestNewClick);
	private Button btnSaveGuest = new Button("Gast speichern", this::onGuestSafeClick);
	private Button btnDeleteGuest = new Button("Gast loeschen");
	private HorizontalLayout guestCtrl = new HorizontalLayout(btnAddGuest, btnSaveGuest, btnDeleteGuest);
	
	private VerticalLayout guestLayout = new VerticalLayout(guestInfo, guestCtrl);
	private HorizontalLayout view = new HorizontalLayout();
	
	private Optional<Guest> guest = Optional.empty();
	private Optional<Booking> booking = Optional.empty();
	private boolean supervisor = false;
	private LocalDate globalDate = LocalDate.now();
	private Optional<Runnable> listener = Optional.empty();
	
	private Binder<Guest> binder = new BeanValidationBinder<>(Guest.class);
	
	private GuestRepo guestRepo;
	
	public BookingGuestView(GuestRepo guestRepo) {
		this.guestRepo = guestRepo;
	}
	
	@Override
	protected Component initContent() {
		guestInfo.addFormItem(surname, "Name");
		guestInfo.addFormItem(prename, "Vorname");
		guestInfo.addFormItem(checkedIn, "Checked_IN");
		guestInfo.addFormItem(checkedOut, "Chcekd_OUT");
		guestInfo.addFormItem(birthDate, "Geburtsdatum");
		
		guestInfo.setEnabled(false);
		checkedIn.setEnabled(false);
		checkedOut.setEnabled(false);
		btnSaveGuest.setEnabled(false);
		btnDeleteGuest.setEnabled(false);
		btnAddGuest.setEnabled(false);
		
		view.add(guests, guestLayout);
		
		binder.forField(birthDate).withConverter(
				new LocalDateToDateConverter()).bind(Guest::getBirthDate, Guest::setBirthDate);
		binder.forField(checkedIn).withConverter(
				new LocalDateToDateConverter()).bind(Guest::getCheckedIn, Guest::setCheckedIn);
		binder.forField(checkedOut).withConverter(
				new LocalDateToDateConverter()).bind(Guest::getCheckedOut, Guest::setCheckedOut);
		binder.bindInstanceFields(this);
		
		return view;
	}
	
	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	void setGlobalDate(LocalDate date) {
		globalDate = date;
		refresh();
	}
	
	void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
		refresh();
	}
	
	void setBooking(Optional<Booking> booking) {
		this.booking = booking;
		guest = Optional.empty();
		
		refresh();
	}
	
	
	private void refresh() {
		binder.readBean(guest.orElse(null));
		guestInfo.setEnabled(booking.isPresent() && guest.isPresent());
		
		
		checkedIn.setEnabled(supervisor);
		checkedOut.setEnabled(supervisor);
		btnAddGuest.setEnabled(booking.isPresent());
		btnSaveGuest.setEnabled(guest.isPresent() && booking.isPresent());
		btnDeleteGuest.setEnabled(guest.map(g -> g.getId() != null).orElse(false));
		
		booking.ifPresent(b -> guests.setItems(new ListDataProvider<Guest>(guestRepo.findByBooking(b))));
		
		//surname.setEnabled(guest.isPresent());
		//prename.setEnabled(guest.isPresent());
		//birthDate.setEnabled(guest.isPresent());
	}
	
	private void onGuestNewClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		Optional<Guest> newGuest = Optional.of(new Guest());
		guest = newGuest;
		refresh();
	}
	
	private void onGuestSafeClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		guest.ifPresent(g -> {
			booking.ifPresent(b -> {
				if (binder.writeBeanIfValid(g)) {
					g.setBooking(b);
					try {
					Guest guest = guestRepo.save(g);
					} catch (ConstraintViolationException e) {
						System.out.println(e.getMessage());
						
						e.getConstraintViolations().forEach(c -> System.out.println(c));
					}
					//setCustomer(Optional.of(cust_repo.save(c)));
					
					listener.ifPresent(Runnable::run);
					//this.guest = (Optional.of(guest));
					}
				});
			});	
		//refresh();
	}
	

}
