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
	
	private TextField cnr = new TextField("Kundennummer");
	private TextField surname = new TextField("Name");
	private TextField prename = new TextField("Vorname");
	private TextField address = new TextField("Strasse");
	private TextField zip = new TextField("PLZ");
	private TextField city = new TextField("Ort");
	private IntegerField discount = new IntegerField("Rabatt");
	
	private FormLayout view = new FormLayout();
	
	private Optional<Customer> cust = Optional.empty();
	
	private Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);
	
	@Override
	protected Component initContent() {
		this.view.add(cnr, discount, surname, prename, address, zip, city);
		this.view.setResponsiveSteps(new ResponsiveStep("0", 2));
		//this.view.setSizeUndefined();
		cnr.setReadOnly(true);
		discount.setReadOnly(true);
		surname.setReadOnly(true);
		prename.setReadOnly(true);
		address.setReadOnly(true);
		zip.setReadOnly(true);
		city.setReadOnly(true);
		
		this.binder.bindInstanceFields(this);
		
		return this.view;
	}
	
	private void refresh() {
		binder.readBean(cust.orElse(null));
	}
	
	public void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
		refresh();
	}

}
