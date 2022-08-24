package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.component.html.Label;

import de.fhwedel.pimpl.DateValidationAndFilterHelper;
import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.repos.BookingRepo;
import de.fhwedel.pimpl.views.CustomerSearchView.CustomerSearchHelper;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingSearchView extends Composite<Component> {
	
	private TextField queryBookingNr = new TextField("Buchungsnummer");
	private DatePicker queryDate = new DatePicker("Datum");
	
	private Button btnBookingNr = new Button("Suchen", this::onSearchNrClick);
	private Button btnDate = new Button("Suchen", this::onSearchDateClick);
	
	private ListBox<Booking> bookingsGrid = new ListBox<>();
	private Label foundLabel = new Label("Gefundene Buchungen:");
	
	private VerticalLayout view = new VerticalLayout(queryBookingNr, btnBookingNr, queryDate, btnDate, foundLabel, bookingsGrid);
	
	private BookingRepo bookingRepo;
	
	private BookingView bookingView = null;
	
	public BookingSearchView(BookingRepo bookingRepo) {
		this.bookingRepo = bookingRepo;
	}
	
	@Override
	protected Component initContent() {
		//bookings.setItemLabelGenerator("Buchung " + Booking::getBookingNr);
//		bookingsGrid.addColumn(Booking::getBookingNr).setHeader("Nummer");
//		bookingsGrid.addColumn(Booking::getEstimatedArrival).setHeader("Anreise").setSortable(true).setAutoWidth(true);
//		bookingsGrid.addColumn(Booking::getEstimatedDeparture).setHeader("Abreise").setSortable(true).setAutoWidth(true);
		bookingsGrid.setItemLabelGenerator(Booking::toLabel);
		bookingsGrid.addValueChangeListener(this::onBookingSelect);
		view.setWidth(null);
		return view;
	}
	
	String getBookingNrQuery() {
		return queryBookingNr.getValue();
	}
	
	LocalDate getDateQuery() {
		return queryDate.getValue();
	}
	
	void setParentView(BookingView bookingView) {
		this.bookingView = bookingView;
	}
	

	private void onSearchNrClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		bookingsGrid.setItems(new ListDataProvider<Booking>(
				bookingRepo.findByBookingNr(queryBookingNr.getValue())));
	}
	
	private void onSearchDateClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		if (!queryDate.isEmpty()) {
			Date date = Date.from(queryDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			List<Booking> bookings = bookingRepo.findAll();
			
			bookings = bookings.stream()
			.filter(b -> DateValidationAndFilterHelper.isBetweenIncluding(b.getEstimatedArrival(),
					b.getEstimatedDeparture(), date)).toList();
			bookingsGrid.setItems(bookings);	
		} else {
			bookingsGrid.setItems(new ListDataProvider<Booking>(bookingRepo.findAll()));
		}
	}
	
	void clear() {
		bookingsGrid.setItems(Collections.emptyList());
	}
	
	void onBookingSelect(ComponentValueChangeEvent<ListBox<Booking>, Booking> event) {
		if (bookingView != null) {
			bookingView.setMainAreaVisibility(true);
			//System.out.println(event.getValue().toLabel());
			if (event.getHasValue().isEmpty()) {
				bookingView.setBooking(Optional.empty());
			} else {
				bookingView.setBooking(Optional.of(event.getValue()));
			}
			
		}
	}
	
		
}
