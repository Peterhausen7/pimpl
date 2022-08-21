package de.fhwedel.pimpl;

import java.util.Optional;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Customer;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerBookingView extends Composite<Component> {

	private Button btnNewBooking = new Button("Neue Buchung");
	private Button btnSelectBooking = new Button("Zur Buchung");
	private HorizontalLayout bookingButtons = new HorizontalLayout(btnNewBooking, btnSelectBooking);
	
	private Optional<Booking> booking = Optional.empty();
	private Optional<Customer> cust = Optional.empty();
	
	private Optional<Runnable> listener = Optional.empty();
	
	
	
	public CustomerBookingView() {
		
	}
	
	@Override
	protected Component initContent() {
		
		
		
		
		return bookingButtons;
	}
	
	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	public void setBooking(Optional<Booking> booking) {
		this.booking = booking;
		refresh();
	}
	
	public void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
		booking = Optional.empty();
		refresh();
	}
	
	public void setNewBookingBtn(ComponentEventListener<ClickEvent<Button>> listener) {
		btnNewBooking.addClickListener(listener);
	}
	
	private void refresh() {
		btnNewBooking.setEnabled(cust.isPresent());
		btnSelectBooking.setEnabled(booking.isPresent());
		
	}
	
	
}
