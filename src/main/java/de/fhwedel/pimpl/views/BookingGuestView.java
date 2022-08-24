package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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
import de.fhwedel.pimpl.model.RoomCategory;
import de.fhwedel.pimpl.repos.GuestRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingGuestView extends Composite<Component> {

	//List of guests of a booking
	private ListBox<Guest> guests = new ListBox<>();
	//Label of the list
	private Label guestLabel = new Label("Gäste");
	
	//Fields of guest class
	private TextField surname = new TextField();
	private TextField prename = new TextField();
	private DatePicker birthDate = new DatePicker();
	private DatePicker checkedIn = new DatePicker();
	private DatePicker checkedOut = new DatePicker();
	private FormLayout guestInfo = new FormLayout();
	
	//Buttons
	private Button btnAddGuest = new Button("Neuer Gast", this::onGuestNewClick);
	private Button btnSaveGuest = new Button("Gast speichern", this::onGuestSaveClick);
	private Button btnDeleteGuest = new Button("Gast loeschen", this::onGuestDeleteClick);
	private Button btnCheckIn = new Button("Gast einchecken", this::onGuestCheckInClick);
	
	//Layouts
	private HorizontalLayout guestCtrl = new HorizontalLayout(btnAddGuest, btnSaveGuest, btnDeleteGuest, btnCheckIn);
	private VerticalLayout guestLayout = new VerticalLayout(guestInfo, guestCtrl);
	private VerticalLayout guestListLayout = new VerticalLayout(guestLabel, guests);
	
	//Main view of this class
	private HorizontalLayout view = new HorizontalLayout();
	
	//supervisor flag
	private boolean supervisor = false;
	//the global date field
	private LocalDate globalDate = LocalDate.now();
	
	//The guest to work with
	private Optional<Guest> guest = Optional.empty();
	
	//The booking of the guests
	private Optional<Booking> booking = Optional.empty();
	
	//listener to refresh parent class
	private Optional<Runnable> listener = Optional.empty();
	
	//binder for guest class
	private Binder<Guest> binder = new BeanValidationBinder<>(Guest.class);
	
	//CRUD access
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
		
		guests.setItemLabelGenerator(Guest::getSurname);
		guests.addValueChangeListener(this::onSelectedGuestChange);
		
		guestInfo.setEnabled(false);
		checkedIn.setReadOnly(true);
		checkedOut.setReadOnly(true);
		btnSaveGuest.setEnabled(false);
		btnDeleteGuest.setEnabled(false);
		btnAddGuest.setEnabled(false);
		btnCheckIn.setEnabled(false);
		
		guestListLayout.setWidth(null);
		
		view.add(guestListLayout, guestLayout);
		
		//bind fields
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
		refresh(false);
	}
	
	void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
		refresh(false);
	}
	
	void setBooking(Optional<Booking> booking) {
		this.booking = booking;
		guest = Optional.empty();
		refresh(true);
	}
	
	/**
	 * Refreshes this class
	 * @param refreshList if true includes the guest list box in the refresh
	 */
	private void refresh(boolean refreshList) {
		binder.readBean(guest.orElse(null));
		
		//Sets visibility/availability of components according to business rules etc.
		guestInfo.setEnabled(booking.isPresent() && guest.isPresent());
		
		checkedIn.setReadOnly(!supervisor);
		checkedOut.setReadOnly(!supervisor);
		
		btnSaveGuest.setEnabled(guest.isPresent() && booking.isPresent());
		btnDeleteGuest.setEnabled(guest.map(g -> g.getId() != null).orElse(false));
		btnCheckIn.setEnabled(guest.map(g -> g.getId() != null).orElse(false) && booking.isPresent());
		
		btnAddGuest.setEnabled(false);
		booking.ifPresent(b -> {
			ListDataProvider<Guest> guestList = new ListDataProvider<Guest>(guestRepo.findByBooking(b));
			if (refreshList) {
				guests.setItems(guestList);
			}
			btnAddGuest.setEnabled(supervisor || guestList.getItems().size() < b.getRoom().getRoomCategory().getBedCount());
		});
	}
	
	/**
	 * On click listener of the btnAddGuest button
	 * Makes it possible to create a new guest
	 * @param event the click event
	 */
	private void onGuestNewClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		Optional<Guest> newGuest = Optional.of(new Guest());
		guest = newGuest;
		refresh(false);
	}
	
	/**
	 * On click listener of the btnSaveGuest button
	 * Saves the guest
	 * @param event the click event
	 */
	private void onGuestSaveClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		guest.ifPresent(g -> {
			booking.ifPresent(b -> {
				if (binder.writeBeanIfValid(g)) {
					g.setBooking(b);
					guestRepo.save(g);
					listener.ifPresent(Runnable::run);
					}
				});
			});	
	}
	
	/**
	 * On click listener of the btnDeleteGuest button
	 * Deletes the guest
	 * @param event
	 */
	private void onGuestDeleteClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		guest.ifPresent(g -> {
			Dialog d = new Dialog();
			d.setCloseOnEsc(true);
			d.add(new Label("Gast löschen?"));
			d.add(new HorizontalLayout(new Button("Ja", ev -> {
				d.close();
				guestRepo.delete(g);
				guest = Optional.empty();
				
				refresh(true);
			}), new Button("Nein", ev -> d.close())));
			d.open();
		});
	}
	
	/**
	 * On click listener of the btnCheckIn button
	 * Checks the guest in
	 * @param event
	 */
	private void onGuestCheckInClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		guest.ifPresent(g -> {
			Dialog d = new Dialog();
			d.setCloseOnEsc(true);
			d.add(new Label("Gast Personaldaten geprüft?"));
			d.add(new HorizontalLayout(new Button("Ja", ev -> {
				d.close();
				//Can only check in guests that haven't been checked in yet
				if (g.getCheckedIn() == null) {
					checkedIn.setValue(globalDate);
					btnSaveGuest.click();
				} else {
					Notification notification = Notification.show("Gast bereits eingecheckt!");
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					notification.setPosition(Notification.Position.MIDDLE);
				}
			}), new Button("Nein", ev -> d.close())));
			d.open();
		});
	}
	
	/**
	 * On value change listener of the guest list box
	 * Sets the selected guest 
	 * @param event
	 */
	private void onSelectedGuestChange(ComponentValueChangeEvent<ListBox<Guest>,Guest> event) {
		if (!event.getHasValue().isEmpty()) {
			guest = Optional.of(event.getValue());
		} else {
			guest = Optional.empty();
		}
		refresh(false);
	}
}
