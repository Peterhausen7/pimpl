package de.fhwedel.pimpl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingSearchView extends Composite<Component> {
	
	private TextField queryBookingNr = new TextField();
	private DatePicker queryDate = new DatePicker();
	
	private Button btnBookingNr = new Button("Buchung suchen nach Nr");
	private Button btnDate = new Button("Buchung suchen nach Datum");
	
	private VerticalLayout view = new VerticalLayout(queryBookingNr, btnBookingNr, queryDate, btnDate);
	
	public BookingSearchView() {
	}
	
	@Override
	protected Component initContent() {
		
		view.setWidth(null);
		return view;
	}
	
	
		
}
