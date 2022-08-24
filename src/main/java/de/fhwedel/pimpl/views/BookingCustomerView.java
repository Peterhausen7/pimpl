package de.fhwedel.pimpl.views;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Customer;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingCustomerView extends Composite<Component> {
	
	//Fields of customer class
	private TextField cnr = new TextField("Kundennummer");
	private TextField surname = new TextField("Name");
	private TextField prename = new TextField("Vorname");
	private TextField address = new TextField("Strasse");
	private TextField zip = new TextField("PLZ");
	private TextField city = new TextField("Ort");
	private IntegerField discount = new IntegerField("Rabatt");
	
	//The main view of this class - costumer information
	private FormLayout view = new FormLayout();
	
	//The customers information to display
	private Optional<Customer> cust = Optional.empty();
	
	//Binder for customer class
	private Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);
	
	@Override
	protected Component initContent() {
		view.add(cnr, discount, surname, prename, address, zip, city);
		view.setResponsiveSteps(new ResponsiveStep("0", 2));
		
		cnr.setReadOnly(true);
		discount.setReadOnly(true);
		surname.setReadOnly(true);
		prename.setReadOnly(true);
		address.setReadOnly(true);
		zip.setReadOnly(true);
		city.setReadOnly(true);
		
		binder.bindInstanceFields(this);
		
		return view;
	}
	
	private void refresh() {
		binder.readBean(cust.orElse(null));
	}
	
	void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
		refresh();
	}
}
