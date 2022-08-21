package de.fhwedel.pimpl;

import java.awt.Window;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

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
	
	private VerticalLayout view = new VerticalLayout();
	
	//CRUD access to RoomCategory
	private RoomCategoryRepo roomCatRepo;
	private RoomRepo roomRepo;
	private BookingRepo bookingRepo;
	
	private LocalDate globalDate = LocalDate.now();
	private boolean supervisor = false;
	
	private Optional<RoomCategory> selectedRoomCat = Optional.empty();
	
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
		
		dateLayout.setEnabled(false);
		
		
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		Div text = new Div(new Text("Buchungszeitraum verletzt Geschaeftsregel"));
		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event -> {
		  notification.close();
		  refresh();
		});
		
		errorLayout.add(text, closeButton);
		errorLayout.setAlignItems(Alignment.CENTER);
		
		notification.add(errorLayout);
		notification.setPosition(Notification.Position.MIDDLE);
		
		roomList.setItemLabelGenerator(Room::getRoomNumber);
		
//		//view.setFlexGrow(1, searchLayout);
//		view.setFlexGrow(2, roomCats);
//		searchLayout.setWidth(null);
		
		view.add(roomCatView, roomView);
		
		return view;
	}
	
	private void refresh() {
		dateLayout.setEnabled(selectedRoomCat.isPresent() && !notification.isOpened());
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
			foundRooms.getItems().stream().forEach(room -> {
				filteredBookings.addAll(foundBookings.stream().filter(b -> b.getRoom().getId().equals(room.getId())).toList());
			});
			filteredBookings.stream().forEach(b -> System.out.println(b.getBookingNr() + " buchung filtered by raum"));
			
			Collection<Room> availRooms = filteredBookings.stream()
					.filter(b -> DateValidationAndFilterHelper.filterRoomAvailability(b, estimatedArrival.getValue(), estimatedDeparture.getValue()))
					.map(b -> b.getRoom()).collect(Collectors.toSet());
			roomList.setItems(availRooms);
			if (availRooms.size() == 1) {
				roomList.setValue(availRooms.iterator().next()); 
			}
			
			
		} else {
			notification.open();
			refresh();
		}
	}
	
	private void onRoomCatsValueChange(ComponentValueChangeEvent<RadioButtonGroup<RoomCategory>,RoomCategory> event) {
		if (!event.getHasValue().isEmpty()) {
			selectedRoomCat = Optional.of(event.getValue());
		} else {
			selectedRoomCat = Optional.empty();
		}
		refresh();
	}
	
	protected void setGlobalDate(LocalDate date) {
		globalDate = date;
	}
	
	protected void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
	}
	
}
