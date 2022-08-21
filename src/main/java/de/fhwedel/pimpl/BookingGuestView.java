package de.fhwedel.pimpl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Guest;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingGuestView extends Composite<Component> {

	private ListBox<String> guests = new ListBox<>();
	
	private TextField surname = new TextField();
	private TextField prename = new TextField();
	private DatePicker birthDate = new DatePicker();
	private DatePicker checkedIn = new DatePicker();
	private DatePicker checkedOut = new DatePicker();
	private FormLayout guestInfo = new FormLayout();
	
	private Button btnAddGuest = new Button("Neuer Gast");
	private Button btnSaveGuest = new Button("Gast speichern");
	private Button btnDeleteGuest = new Button("Gast loeschen");
	private HorizontalLayout guestCtrl = new HorizontalLayout(btnAddGuest, btnSaveGuest, btnDeleteGuest);
	
	private VerticalLayout guestLayout = new VerticalLayout(guestInfo, guestCtrl);
	private HorizontalLayout view = new HorizontalLayout();
	
	public BookingGuestView() {
		
	}
	
	@Override
	protected Component initContent() {
		guestInfo.addFormItem(surname, "Name");
		guestInfo.addFormItem(prename, "Vorname");
		guestInfo.addFormItem(birthDate, "Geburtsdatum");
		guestInfo.addFormItem(checkedIn, "Checked_IN");
		guestInfo.addFormItem(checkedOut, "Chcekd_OUT");
		
		guests.setItems("test", "test");
		
		
		view.add(guests, guestLayout);
		
		return view;
	}
}
