package de.fhwedel.pimpl.views;

import java.util.Optional;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;

import de.fhwedel.pimpl.repos.CustomerRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerSearchView extends Composite<Component> {
	
	public class CustomerSearchHelper {
		private String cnr;
		private String surname;
		private String prename;
		
		public CustomerSearchHelper(String cnr, String surname, String prename) {
			this.cnr = cnr;
			this.surname = surname;
			this.prename = prename;
		}

		public String getCnr() {
			return cnr;
		}

		public String getSurname() {
			return surname;
		}

		public String getPrename() {
			return prename;
		}
	}
	
	private TextField queryCustSur = new TextField();
	private TextField queryCustPre = new TextField();
	private TextField queryCustCnr = new TextField();
	
	private Button searchCustBtn = new Button("Kunde suchen");
	//private Button searchCustPre = new Button();
	//private Button searchCustCnr = new Button();
	
	private FormLayout searchForm = new FormLayout();
	
	private VerticalLayout view = new VerticalLayout(searchForm, searchCustBtn);
	
	
	public CustomerSearchView() {
	}
	
	@Override
	protected Component initContent() {
		searchForm.addFormItem(queryCustCnr, "Kundennummer");
		searchForm.addFormItem(queryCustSur, "Name");
		searchForm.addFormItem(queryCustPre, "Vorname");
		
		searchForm.setResponsiveSteps(new ResponsiveStep("0", 1));
		view.setWidth(null);
		return view;	
	}
	
	
	public CustomerSearchHelper searchHelper() {
		
		return new CustomerSearchHelper(queryCustCnr.getValue(), queryCustSur.getValue(), queryCustPre.getValue());
	}
	
	
	public void bindSearchButton(ComponentEventListener<ClickEvent<Button>> listener) {
		searchCustBtn.addClickListener(listener);
	}
}
