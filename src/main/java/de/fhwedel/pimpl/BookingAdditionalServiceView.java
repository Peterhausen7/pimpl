package de.fhwedel.pimpl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.AdditionalService;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingAdditionalServiceView extends Composite<Component> {
	
	private IntegerField count = new IntegerField();
	private IntegerField price = new IntegerField();
	private IntegerField turnoverTax = new IntegerField();
	private DatePicker date = new DatePicker();
	private FormLayout demandForm = new FormLayout();
	
	private Button btnNewDemand = new Button("Neue Inanspruchname");
	private Button btnSaveDemand = new Button("Speichern");
	private HorizontalLayout demandCtrl = new HorizontalLayout(btnNewDemand, btnSaveDemand);
	
	private ListBox<AdditionalService> serviceBox = new ListBox<>();
	
	private VerticalLayout demandLayout = new VerticalLayout(demandForm, demandCtrl);
	private HorizontalLayout view = new HorizontalLayout(demandLayout, serviceBox);
	
	
	public BookingAdditionalServiceView() {
		
	}
	
	@Override 
	protected Component initContent() {
		
		demandForm.addFormItem(count, "Menge");
		demandForm.addFormItem(price, "Preis");
		demandForm.addFormItem(turnoverTax, "USt");
		demandForm.addFormItem(date, "Datum");
		
	
		
		
		
		return view;
	}
	
	
	

}
