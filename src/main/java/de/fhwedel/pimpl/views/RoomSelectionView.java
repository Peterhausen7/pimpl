package de.fhwedel.pimpl.views;

import java.awt.Window;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.DateValidationAndFilterHelper;
import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;
import de.fhwedel.pimpl.model.Room;
import de.fhwedel.pimpl.model.RoomCategory;
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.repos.RoomCategoryRepo;
import de.fhwedel.pimpl.repos.RoomRepo;


@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class RoomSelectionView extends Composite<Component> {
	
	
	
	//FormLayout for searching 
	private TextField queryDescr = new TextField();
	private IntegerField queryBedCount = new IntegerField();
	private FormLayout searchForm = new FormLayout();
	
	//Button to initiate search
	private Button btnSearch = new Button("Suche Raumkategorie", this::onSearchClick);
	
	//Layout for search related components
	private VerticalLayout searchLayout = new VerticalLayout(searchForm, btnSearch);
	
	//RadioButtonGroup of matching RoomCategory's, shows all RoomCategory's by default
	private RadioButtonGroup<RoomCategory> roomCats = new RadioButtonGroup<>();
	
	//Layout for the booking time/available room search
	private DatePicker estimatedArrival = new DatePicker("Geplantes Annreisedatum");
	private DatePicker estimatedDeparture = new DatePicker("Geplantes Abreisedatum");
	private Button btnFindAvailRooms = new Button("Passendes Zimmer suchen", this::onFindAvailRoomClick);
	private VerticalLayout dateLayout = new VerticalLayout(estimatedArrival, estimatedDeparture, btnFindAvailRooms);
		
	private ListBox<Room> roomList = new ListBox<>();
	
	private HorizontalLayout roomView = new HorizontalLayout(dateLayout, roomList);
	
	
	
	//View of this class
	private HorizontalLayout roomCatView = new HorizontalLayout(searchLayout, roomCats);
	
	private IntegerField price = new IntegerField("Preis");
	private Button btnCreateBooking = new Button("Buchung anlegen");
	private TextField comment = new TextField("Kommentar");
	
	private HorizontalLayout createLayout = new HorizontalLayout(price, comment);
	private VerticalLayout priceLayout = new VerticalLayout(createLayout, btnCreateBooking);
	
	private VerticalLayout view = new VerticalLayout();
	
	//CRUD access to RoomCategory
	private RoomCategoryRepo roomCatRepo;
	private RoomRepo roomRepo;
	private BookingRepo bookingRepo;
	
	private LocalDate globalDate = LocalDate.now();
	private boolean supervisor = false;
	
	private Optional<RoomCategory> selectedRoomCat = Optional.empty();
	private Optional<Room> selectedRoom = Optional.empty();
	
	private Binder<Booking> binder = new BeanValidationBinder<>(Booking.class);
	
	Notification notification = new Notification();
	
	HorizontalLayout errorLayout = new HorizontalLayout();
	
	public RoomSelectionView(RoomCategoryRepo roomCatRepo, RoomRepo roomRepo, BookingRepo bookingRepo) {
		this.roomCatRepo = roomCatRepo;
		this.roomRepo = roomRepo;
		this.bookingRepo = bookingRepo;
	}
	
	@Override
	protected Component initContent() {
		//Setup search layout
		searchForm.addFormItem(queryDescr, "Bezeichnung");
		searchForm.addFormItem(queryBedCount, "Bettanzahl");
		
		//setup RoomCategory RadioButtonGroup
		roomCats.setItems(new ListDataProvider<RoomCategory>(roomCatRepo.findAll()));
		roomCats.setItemLabelGenerator(RoomCategory::getDescription);
		roomCats.setLabel("Passende Zimmerkategorien");
		roomCats.addValueChangeListener(this::onRoomCatsValueChange);
		
		dateLayout.setVisible(false);
		
		estimatedArrival.setMin(globalDate);
		estimatedDeparture.setMin(globalDate);
		
		
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		Div text = new Div(new Text("Buchungszeitraum verletzt Geschaeftsregel"));
		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event -> {
		  notification.close();
		  refresh();
		});
		
		comment.setValue("");
		
		errorLayout.add(text, closeButton);
		errorLayout.setAlignItems(Alignment.CENTER);
		
		notification.add(errorLayout);
		notification.setPosition(Notification.Position.MIDDLE);
		
		roomList.setItemLabelGenerator(Room::getRoomNumber);
		roomList.addValueChangeListener(this::onRoomsValueChange);
		
		priceLayout.setVisible(false);
		price.addValueChangeListener(this::onPriceValueChange);
	
		view.add(roomCatView, roomView, priceLayout);
		
		return view;
	}
	
	void bindCreateBookingBtn(ComponentEventListener<ClickEvent<Button>> listener) {
		btnCreateBooking.addClickListener(listener);
	}
	
	Optional<Room> getRoom() {
		return selectedRoom;
	}
	
	Integer getPrice() {
		return price.getValue();
	}
	
	Date getEstimatedArrival() {
		return Date.from(estimatedArrival.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	
	Date getEstimatedDeparture() {
		return Date.from(estimatedDeparture.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	
	String getComment() {
		return comment.getValue();
	}
	
	
	
	private void refresh() {
		dateLayout.setVisible(selectedRoomCat.isPresent() && !notification.isOpened());
		priceLayout.setVisible(selectedRoom.isPresent() && !notification.isOpened());
		estimatedArrival.setMin(globalDate);
		estimatedDeparture.setMin(globalDate);
		btnCreateBooking.setEnabled(!price.isInvalid());
		//price.valid
		
	}
	
	private void onSearchClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		ListDataProvider<RoomCategory> foundCats;
		if (queryBedCount.getValue() != null) {
			foundCats = new ListDataProvider<RoomCategory>(
					roomCatRepo.findByDescriptionContainingAndBedCount(
							queryDescr.getValue(), queryBedCount.getValue()));
		} else {
			foundCats = new ListDataProvider<RoomCategory>(
					roomCatRepo.findByDescriptionContaining(queryDescr.getValue()));
			
		}
		roomCats.setItems(foundCats);
		if (foundCats.getItems().size() == 1) {
			roomCats.setValue(foundCats.getItems().iterator().next());
		}	
		
		System.out.println(globalDate.toString() + supervisor);
	}
	
	private void onFindAvailRoomClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		if (DateValidationAndFilterHelper.validateBookingTimeframe(estimatedArrival.getValue(), estimatedDeparture.getValue(), 
				supervisor, globalDate)) {
			ListDataProvider<Room> foundRooms = new ListDataProvider<Room>(
					roomRepo.findByRoomCategory(selectedRoomCat.get()));
			foundRooms.getItems().stream().forEach(r -> System.out.println(r.getRoomNumber()));
			
			Collection<Booking> foundBookings = new ListDataProvider<Booking>(
					bookingRepo.findByStatusOrStatus(ContractState.RESERVED, ContractState.CHECKED_IN)).getItems();
			foundBookings.stream().forEach(b -> System.out.println(b.getBookingNr() + " buchung mit status"));
			
		//	foundBookings = foundBookings.stream().filter(b -> b.getRoom().getId());
			Collection<Booking> filteredBookings = new ArrayList<>();
			foundRooms.getItems().stream()
			.forEach(room -> {
				filteredBookings.addAll(foundBookings.stream()
						.filter(b -> b.getRoom().getId().equals(room.getId()))
						.toList());
			});
			filteredBookings.stream().forEach(b -> System.out.println(b.getBookingNr() + " buchung filtered by raum"));
			
			Collection<Room> notAvailRooms = filteredBookings.stream()
					.filter(b -> !DateValidationAndFilterHelper.filterRoomAvailability(b, estimatedArrival.getValue(), estimatedDeparture.getValue()))
					.map(b -> b.getRoom())
					.collect(Collectors.toSet());
			notAvailRooms.forEach(r -> System.out.println("not avail rooms " + r.getRoomNumber()));
			//Collection<Room> finalAvailRooms = foundRooms.getItems().removeAll(notAvailRooms);
			Collection<Room> finalAvailRooms = foundRooms.getItems();
			finalAvailRooms.removeAll(notAvailRooms);
			finalAvailRooms.forEach(r -> System.out.println("FinalAvail rooms " + r.getRoomNumber()));
			roomList.setItems(finalAvailRooms);
			//test comment
			if (finalAvailRooms.size() == 1) {
				roomList.setValue(finalAvailRooms.iterator().next()); 
			}
			
			
		} else {
			notification.open();
			refresh();
		}
	}
	
	private void reset() {
		//price.setValue(price.getEmptyValue());
		selectedRoom = Optional.empty();
		roomList.setItems(Collections.emptyList());
	}
	
	void clear() {
		roomCats.setItems(new ListDataProvider<RoomCategory>(roomCatRepo.findAll()));
		reset();
		queryDescr.setValue(queryDescr.getEmptyValue());
		queryBedCount.setValue(queryBedCount.getEmptyValue());
		estimatedArrival.setValue(estimatedArrival.getEmptyValue());
		estimatedDeparture.setValue(estimatedDeparture.getEmptyValue());
		comment.setValue("");
	}
	
	private void onRoomCatsValueChange(ComponentValueChangeEvent<RadioButtonGroup<RoomCategory>,RoomCategory> event) {
		reset();
		if (!event.getHasValue().isEmpty()) {
			selectedRoomCat = Optional.of(event.getValue());
		} else {
			selectedRoomCat = Optional.empty();
		}
		refresh();
	}
	
	private void onRoomsValueChange(ComponentValueChangeEvent<ListBox<Room>, Room> event) {
		if (!event.getHasValue().isEmpty()) {
			selectedRoom = Optional.of(event.getValue());
			price.setValue(DateValidationAndFilterHelper.calcPrice(selectedRoomCat.get().getPricePerNight(),
					0, selectedRoomCat.get().getMinPrice()));
		} else {
			selectedRoom = Optional.empty();
		}
		refresh();
	}
	
	private void onPriceValueChange(ComponentValueChangeEvent<IntegerField, Integer> event) {
	//	System.out.println("Min price " + selectedRoomCat.get().getMinPrice());
	//	System.out.println("Field Value " + event.getValue());
		price.setErrorMessage("Preis muss mindestens " + selectedRoomCat.get().getMinPrice() + " sein");
		event.getSource().setInvalid(!(event.getValue() >= selectedRoomCat.get().getMinPrice()));
		
		refresh();
	}
	
	
	protected void setGlobalDate(LocalDate date) {
		globalDate = date;
		refresh();
	}
	
	protected void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
	}
	
}
