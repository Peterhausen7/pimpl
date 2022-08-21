package de.fhwedel.pimpl;

import java.time.LocalDate;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.model.Room;
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.repos.CustomerRepo;
import de.fhwedel.pimpl.repos.RoomRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingView extends Composite<Component> {
	
	
	private CustomerRepo custRepo;
	private Optional<Runnable> listener = Optional.empty();
	
	
	private BookingSearchView searchView;
	private BookingEditView editView;
	private BookingCustomerView customerView;
	private BookingRoomView roomView;
	private BookingGuestView guestView;
	private BookingAdditionalServiceView serviceView;
	private RoomSelectionView roomSelectView;
	
	private BookingRepo bookingRepo;
	private RoomRepo roomRepo;
	
	
	//private VerticalLayout bookingInfoView = new VerticalLayout();
	private VerticalLayout customerAndRoomView = new VerticalLayout();
	private HorizontalLayout bookingAndCustomerInfoView = new HorizontalLayout();
	private VerticalLayout mainAreaView = new VerticalLayout(bookingAndCustomerInfoView);
	private HorizontalLayout view = new HorizontalLayout();
	
	private Optional<Booking> booking = Optional.empty();
	
	private LocalDate globalDate = LocalDate.now();
	private boolean supervisor = false;
	
	

	
	public BookingView(CustomerRepo custRepo, BookingRepo bookingRepo, RoomRepo roomRepo, BookingSearchView searchView, BookingEditView editView,
			BookingCustomerView customerView, BookingRoomView roomView, 
			BookingGuestView guestView, BookingAdditionalServiceView serviceView, RoomSelectionView roomSelectView) {
		this.custRepo = custRepo;
		this.bookingRepo = bookingRepo;
		this.roomRepo = roomRepo;
		
		this.searchView = searchView;
		
		this.editView = editView;
		this.editView.listenToChange(Optional.of(this::refresh));
		
		this.customerView = customerView;
		
		this.roomView = roomView;
		this.roomView.listenToChange(Optional.of(this::refresh));
		
		this.guestView = guestView;
		this.serviceView = serviceView;
		
		this.roomSelectView = roomSelectView;
	}
	
	@Override
	protected Component initContent() {
		customerAndRoomView.add(customerView, roomView);
		bookingAndCustomerInfoView.add(editView, customerAndRoomView);
		mainAreaView.add(guestView, serviceView);
		
		roomSelectView.setVisible(false);
		
		view.add(searchView);
		view.add(roomSelectView);
		view.add(mainAreaView);
		
		
		
		return view;
		
	}
	
	private void refresh() {
		Optional<Booking> booking = editView.getBooking().flatMap(b -> b.getId() != null ? bookingRepo.findById(b.getId()) : Optional.of(b));
		Optional<Customer> cust = editView.getCustomer().flatMap(c -> c.getId() != null ? custRepo.findById(c.getId()) : Optional.of(c));
		Optional<Room> room = roomView.getRoom().flatMap(r -> r.getId() != null ? roomRepo.findById(r.getId()) : Optional.of(r));
	
		System.out.println(room.isPresent());
		//editView.setVisible(room.isPresent());
		
		roomView.setRoom(room);
		editView.setCustomer(cust);
		customerView.setCustomer(cust);
		editView.setBooking(booking);
	}
	
	public void setCustomer(Optional<Customer> cust) {
		cust = cust.flatMap(c -> custRepo.findById(c.getId()));
		editView.setCustomer(cust);
		customerView.setCustomer(cust);
		refresh();
	}
	
	
	public void listenToNavigate(Runnable listener) {
		this.listener = Optional.of(listener);
	}
	
	public void setBooking(Optional<Booking> booking) {
		booking = booking.flatMap(b -> bookingRepo.findById(b.getId()));
		editView.setBooking(booking);
		refresh();
	}
	
	/**
	 * Swaps visibility of mainAreaView/roomSelectView according to parameter status
	 * If true mainAreaView will be visible and roomSelectView invisible, vice versa for false
	 * @param status true if mainAreaView needs to be visible, false if roomSelectView needs to be visible
	 */
	private void setMainAreaVisibility(boolean status) {
		mainAreaView.setVisible(status);
		roomSelectView.setVisible(!status);
		
	}
	
	private String getNextBookingNr() {
		Optional<Integer> highestBookingNr = bookingRepo.findAll().stream().map(b -> Integer.valueOf(b.getBookingNr())).max(Integer::compare);
		return highestBookingNr.isPresent() ? (highestBookingNr.get() + 1) + "" : "1";
	}
	
	public void newBooking(Optional<Customer> cust) {
		Optional<Booking> newBooking = Optional.of(new Booking());
		if (newBooking.isPresent()) {
			newBooking.get().setBookingNr(getNextBookingNr());
			System.out.println("Yep");
			System.out.println(newBooking.get().getBookingNr());
		}
		setMainAreaVisibility(false);
		editView.setBooking(newBooking);
		setCustomer(cust);
		
		//refresh();
	}
	
	public void setGlobalDate(LocalDate date) {
		globalDate = date;
		roomSelectView.setGlobalDate(date);
	}
	
	protected void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
		roomSelectView.setSupervisor(supervisor);
	}
		
	

}
