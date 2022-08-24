package de.fhwedel.pimpl.views;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Address;
import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.repos.CustomerRepo;
import de.fhwedel.pimpl.views.CustomerSearchView.CustomerSearchHelper;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerView extends Composite<Component> {

	private Grid<Customer> custs = new Grid<>();
	
	private Button placeholder = new Button("Placeholder");
	

	private CustomerEditView cev;
	private CustomerSearchView custSearchView;
	private CustomerBookingView custBookingView;
	private BookingView bookingView;
	
	private VerticalLayout cust_layout = new VerticalLayout(custs);

	private Grid<Booking> bookings = new Grid<>();

//	private Button btnNewBooking = new Button("Neue Buchung", this::onNewBookingClick);
//	private Button btnSelectBooking = new Button("Zur Buchung");
//	private HorizontalLayout bookingButtons = new HorizontalLayout(btnNewBooking, btnSelectBooking);
	
	private VerticalLayout bookingLayout = new VerticalLayout(bookings);

	private HorizontalLayout view = new HorizontalLayout();

	private CustomerRepo custRepo;
	private BookingRepo bookingRepo;

	private Optional<Runnable> listener = Optional.empty();
	
	private Optional<Booking> booking = Optional.empty();
	

	public CustomerView(CustomerRepo custRepo, BookingRepo bookingRepo, CustomerEditView cev, CustomerSearchView custSearchView, 
			CustomerBookingView custBookingView, BookingView bookingView) {
		this.custRepo = custRepo;
		this.bookingRepo = bookingRepo;
		this.cev = cev;
		this.cev.listenToChange(Optional.of(this::refresh));
		this.cev.setGrid(custs);
		this.custSearchView = custSearchView;
		
		this.custBookingView = custBookingView;
		this.custBookingView.listenToChange(Optional.of(this::refresh));
		this.custBookingView.setNewBookingBtn(this::onNewBookingClick);
		
		this.bookingView = bookingView;
		
	}

	@Override
	protected Component initContent() {
		custs.addColumn(Customer::getCnr).setHeader("Kundennummer").setSortable(true);
		custs.addColumn(Customer::getSurname).setHeader("Name").setSortable(true);
		custs.addColumn(Customer::getPrename).setHeader("Vorname").setSortable(true);
		custs.setSelectionMode(SelectionMode.SINGLE);
		custs.addSelectionListener(this::onCustsSelect);
		//custs.addItemDoubleClickListener(this::onCustsNavigate);
		
		custs.setItems(new ListDataProvider<Customer>(custRepo.findAll()));

		bookings.addColumn(Booking::getBookingNr).setHeader("Buchungsnummer");
		bookings.addColumn(Booking::getReservation).setHeader("Reservierungsdatum");
		bookings.addColumn(Booking::getStatus).setHeader("Status");
		bookings.setSelectionMode(SelectionMode.SINGLE);
		bookings.addSelectionListener(this::onBookingSelect);
		
		cust_layout.add(cev);
		bookingLayout.add(custBookingView);

		custSearchView.bindSearchButton(this::onSearchClick);
		
		view.add(custSearchView);
		view.add(cust_layout);
		view.add(bookingLayout);
		
		//custs_form.setWidth("100%");
		//view.setFlexGrow(0, custSearchView);
	//	view.setFlexGrow(5, custs_form);
		

		setCustomer(Optional.empty());

		return view;
	}
	
	
	public void listenToNavigate(Runnable listener) {
		this.listener = Optional.of(listener);
	}
	
	public void supervisorChange(boolean superStatus) {
		cev.supervisorChange(superStatus);
	}

	public void setCustomer(Optional<Customer> cust) {
		cust = cust.flatMap(c -> custRepo.findById(c.getId()));
		cev.setCustomer(cust);
		custBookingView.setCustomer(cust);
		refresh();
	}
	
//	public void setBooking(Optional<Booking> booking) {
//		booking = booking.flatMap(b -> bookingRepo.findById(b.getId()));
//		refresh();
//	}
	
	
	
	
	private void refresh() {
		Optional<Customer> customer = cev.getCustomer().flatMap(c -> c.getId() != null ? custRepo.findById(c.getId()) : Optional.of(c));
		
//		btnSelectBooking.setEnabled(booking.isPresent());
//		btnNewBooking.setEnabled(customer.isPresent());
		
		bookings.setItems(DataProvider.ofCollection(customer.map(c -> c.getBookings()).orElse(Collections.emptySet())));
		
		cev.setCustomer(
				customer);
		

		//aev.setCustomer(cev.getCustomer());
//		addrs.setDataProvider(
//				DataProvider.ofCollection(cev.getCustomer().map(c -> c.getAddresses()).orElse(Collections.emptySet())));
//		bookings.setItems(
//				DataProvider.ofCollection(cev.getCustomer().map(c -> c.getAddresses()).orElse(Collections.emptySet())));
	}
	
	
//	private void onCustsSearchClick(com.vaadin.flow.component.ClickEvent<Button> event) {
//		List<Customer> result = cust_repo.findBySurnameContaining(custs_query.getValue());
////		custs.setDataProvider(new ListDataProvider<Customer>(result));
//		custs.setItems(new ListDataProvider<Customer>(result));
//	}

	private void onCustsSelect(SelectionEvent<Grid<Customer>, Customer> event) {
		setCustomer(event.getFirstSelectedItem());
	}

//	private void onCustsNavigate(ItemDoubleClickEvent<Customer> event) {
//		listener.ifPresent(l -> l.run());
//	}

	private void onBookingSelect(SelectionEvent<Grid<Booking>,Booking> event) {
		//aev.setAddress(event.getFirstSelectedItem());
		//setBooking(event.getFirstSelectedItem());
	}
	
//	private String getNextBookingNr() {
//		Optional<Integer> highestBookingNr = bookingRepo.findAll().stream().map(b -> Integer.valueOf(b.getBookingNr())).max(Integer::compare);
//		return highestBookingNr.isPresent() ? (highestBookingNr.get() + 1) + "" : "1";
//	}
	
	private void onNewBookingClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		//setBooking(Optional.of(new Booking()));
//		Optional<Booking> newBooking = Optional.of(new Booking());
//		if (newBooking.isPresent()) {
//			newBooking.get().setBookingNr(getNextBookingNr());
//		}
		listener.ifPresent(l -> l.run());
		bookingView.newBooking(cev.getCustomer());
		
	}
	
	private void onSearchClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		CustomerSearchHelper helper = custSearchView.searchHelper();
		
		if (helper.getCnr().isEmpty()) {
			custs.setItems(new ListDataProvider<Customer>(
					custRepo.findBySurnameContainingAndPrenameContaining(
					helper.getSurname(), helper.getPrename())));
		} else {
			custs.setItems(new ListDataProvider<Customer>(
					custRepo.findBySurnameContainingAndPrenameContainingAndCnr(
							helper.getSurname(), helper.getPrename(), helper.getCnr())));
		}
	}

}
