package de.fhwedel.pimpl.views;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.repos.CustomerRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerEditView extends Composite<Component> {

	private TextField cnr = new TextField();
	private TextField surname = new TextField();
	private TextField prename = new TextField();
	private TextField address = new TextField();
	private TextField zip = new TextField();
	private TextField city = new TextField();
	private IntegerField discount = new IntegerField();

	private FormLayout cust_form = new FormLayout();
	private Button cust_new = new Button("Neuer Kunde", this::onCustNewClick);
	private Button cust_safe = new Button("Kunde sichern", this::onCustSafeClick);
	private Button cust_reset = new Button("Reset", this::onCustResetClick);
	private Button cust_delete = new Button("Kunde löschen", this::onCustDeleteClick);
	private HorizontalLayout cust_ctrl = new HorizontalLayout(cust_new, cust_safe, cust_reset, cust_delete);

	private VerticalLayout view = new VerticalLayout(cust_form, cust_ctrl);

	private CustomerRepo cust_repo;
	private Optional<Customer> cust;
	private Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);

	private Optional<Runnable> listener = Optional.empty();
	
	private Grid<Customer> custs = new Grid<>();
	
	public CustomerEditView(CustomerRepo repo) {
		this.cust_repo = repo;
	}

	@Override
	protected Component initContent() {
		this.cust_form.addFormItem(cnr, "Kundennummer");
		this.cust_form.addFormItem(discount, "Rabatt");
		this.cust_form.addFormItem(surname, "Nachname");
		this.cust_form.addFormItem(prename, "Vorname");
		this.cust_form.addFormItem(address, "Strasse");
		this.cust_form.addFormItem(zip, "PLZ");
		this.cust_form.addFormItem(city, "Ort");
		
		
		
		discount.setMin(0);
		discount.setMax(100);
		discount.setStep(5);
		discount.setHasControls(true);
		discount.setHelperText("Maximal 100% Rabatt");
		
		cnr.setReadOnly(true);
		discount.setReadOnly(true);

//		this.binder.forField(cnum)
//				.withValidator(custnum -> custnum != null && custnum.toString().startsWith("1"),
//						"Fehlerhafte Kundennummer, muss mit 1 beginnen")
//				.asRequired().bind(Customer::getCustnum, Customer::setCustnum);
//		this.binder.forField(discount).withValidator(disc -> disc != null && disc <= 100 && disc >= 0, "Rabatt kann nicht groesser als 100 sein")
//		.bind(Customer::getDiscount, Customer::setDiscount);
		this.binder.bindInstanceFields(this);

		return this.view;
	}
	
	public void setGrid(Grid<Customer> custs) {
		this.custs = custs;
	}

	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}
	
	public void supervisorChange(boolean superStatus) {
		discount.setReadOnly(!superStatus);
	}

	private void refresh() {
		binder.readBean(cust.orElse(null));
		cust_form.setEnabled(cust.isPresent());
		cust_safe.setEnabled(cust.isPresent());
		cust_reset.setEnabled(cust.isPresent());
		cust_delete.setEnabled(cust.map(a -> a.getId() != null).orElse(false));
	}

	public void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
		refresh();
	}

	public Optional<Customer> getCustomer() {
		return cust;
	}

	/**
	 * Gets next possible cnr
	 * @return
	 */
	public String getNextCnr() {
		Optional<Integer> highestCnr = cust_repo.findAll().stream().map(c -> Integer.valueOf(c.getCnr())).max(Integer::compare);
		return highestCnr.isPresent() ? (highestCnr.get() + 1) + "" : "1";
	}
	
	
	private void onCustNewClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		Optional<Customer> new_cust = Optional.of(new Customer());
		if (new_cust.isPresent()) {
			new_cust.get().setCnr(getNextCnr());
			new_cust.get().setDiscount(0);
		}
		
		setCustomer(new_cust);
		listener.ifPresent(Runnable::run);
	}

	private void onCustSafeClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		cust.ifPresent(c -> {
			if (binder.writeBeanIfValid(c)) {
				Customer customer = cust_repo.save(c);
				//setCustomer(Optional.of(cust_repo.save(c)));
		
				setCustomer(Optional.of(customer));
			
				custs.setItems(new ListDataProvider<Customer>(cust_repo.findAll()));
				custs.select(customer);
			}
		});
	}

	private void onCustResetClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		cust.ifPresent(c -> binder.readBean(c));
	}

	private void onCustDeleteClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		cust.ifPresent(c -> {
			Dialog d = new Dialog();
			d.setCloseOnEsc(true);
			d.add(new Label("Kunde löschen?"));
			d.add(new HorizontalLayout(new Button("Ja", ev -> {
				d.close();
				cust_repo.delete(c);
				setCustomer(Optional.empty());
				//listener.ifPresent(Runnable::run);
				custs.setItems(new ListDataProvider<Customer>(cust_repo.findAll()));
			}), new Button("Oops, lieber doch nicht", ev -> d.close())));
			d.open();
		});
	}

}
