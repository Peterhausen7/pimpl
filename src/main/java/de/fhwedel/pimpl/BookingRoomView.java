package de.fhwedel.pimpl;

import java.util.Optional;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Room;
import de.fhwedel.pimpl.model.RoomCategory;
import de.fhwedel.pimpl.repos.RoomRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingRoomView extends Composite<Component> {

	private ListBox<Room> rooms = new ListBox<>();
	
	private TextField description = new TextField("Bezeichnung");
	private IntegerField bedCount = new IntegerField("Bettanzahl");
	private IntegerField pricePerNight = new IntegerField("Uebernachtungspreis");
	private IntegerField minPrice = new IntegerField("Mindestpreis");
	private FormLayout roomCatForm = new FormLayout(description, bedCount, pricePerNight, minPrice);
	
	
	private VerticalLayout view = new VerticalLayout();
	
	private Binder<RoomCategory> binder = new BeanValidationBinder<>(RoomCategory.class);
	
	private RoomRepo roomRepo;
	
	
	private Optional<Room> room = Optional.empty();
	
	private Optional<Runnable> listener = Optional.empty();
	
	
	public BookingRoomView(RoomRepo roomRepo) {
		this.roomRepo = roomRepo;
	}
	
	@Override
	protected Component initContent() {
		rooms.setItems(new ListDataProvider<Room>(roomRepo.findAll())); 
		rooms.setItemLabelGenerator(Room::getRoomNumber);
		rooms.setHeight("150px");
		rooms.addValueChangeListener(this::onRoomSelect);
		
		description.setReadOnly(true);
		bedCount.setReadOnly(true);
		pricePerNight.setReadOnly(true);
		minPrice.setReadOnly(true);
		
		
		view.add(rooms);
		view.add(roomCatForm);
		
		binder.bindInstanceFields(this);
		
		return view;
	}
	
	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	private void refresh() {
		//Optional<Room> room = this.room.flatMap(r -> r.getId() != null ? roomRepo.findById(r.getId()) : Optional.of(r));
		Optional<RoomCategory> roomCat = Optional.empty();
		if (room.isPresent()) {
			roomCat = Optional.of(room.get().getRoomCategory());
		}
		 
		binder.readBean(roomCat.orElse(null));
	}
	
	private void onRoomSelect(ComponentValueChangeEvent<ListBox<Room>, Room> event) {
		room = Optional.of(event.getValue());
		refresh();
		listener.ifPresent(Runnable::run);
	}

	public Optional<Room> getRoom() {
		return room;
	}

	public void setRoom(Optional<Room> room) {
		this.room = room;
		refresh();
	}
	
	
}
