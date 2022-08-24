package de.fhwedel.pimpl.views;

import java.util.Optional;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.fhwedel.pimpl.model.Address;
import de.fhwedel.pimpl.model.Customer;
import de.fhwedel.pimpl.repos.AddressRepo;
import de.fhwedel.pimpl.repos.CustomerRepo;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class AddressEditView extends VerticalLayout {

	private TextField street = new TextField();
	private TextField zip = new TextField();
	private TextField city = new TextField();

	private FormLayout addr_form = new FormLayout();
	private Button addr_new = new Button("Neue Adresse", this::onAddrNewClick);
	private Button addr_safe = new Button("Adresse sichern", this::onAddrSafeClick);
	private Button addr_reset = new Button("Reset", this::onAddrResetClick);
	private Button addr_delete = new Button("Adresse löschen", this::onAddrDeleteClick);
	private HorizontalLayout addr_ctrl = new HorizontalLayout(addr_new, addr_safe, addr_reset, addr_delete);

	private CustomerRepo cust_repo;
	private AddressRepo addr_repo;
	private Optional<Address> addr = Optional.empty();
	private Optional<Customer> cust = Optional.empty();
	private Binder<Address> binder = new BeanValidationBinder<>(Address.class);

	private Optional<Runnable> listener = Optional.empty();

	public AddressEditView(CustomerRepo cust_repo, AddressRepo addr_repo) {
		this.cust_repo = cust_repo;
		this.addr_repo = addr_repo;

		this.addr_form.addFormItem(street, "Straße");
		this.addr_form.addFormItem(zip, "PLZ");
		this.addr_form.addFormItem(city, "Ort");

		this.binder.bindInstanceFields(this);

		this.add(addr_form, addr_ctrl);

		refresh();
	}

	public void listenToChange(Optional<Runnable> listener) {
		this.listener = listener;
	}

	private void refresh() {
		binder.readBean(addr.orElse(null));
		addr_form.setEnabled(addr.isPresent());
		addr_new.setEnabled(cust.map(c -> c.getId() != null).orElse(false));
		addr_safe.setEnabled(addr.isPresent());
		addr_reset.setEnabled(addr.isPresent());
		addr_delete.setEnabled(addr.map(a -> a.getId() != null).orElse(false));
	}

	public void setAddress(Optional<Address> addr) {
		this.addr = addr;
		addr.ifPresent(a -> cust = Optional.of(a.getCust()));
		refresh();
	}

	public void setCustomer(Optional<Customer> cust) {
		this.cust = cust;
		addr = Optional.empty();
		refresh();
	}

	public Optional<Address> getAddress() {
		return addr;
	}

	private void onAddrNewClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		cust.ifPresent(c -> {
			addr = Optional.of(new Address());
			refresh();
		});
	}

	private void onAddrSafeClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		addr.ifPresent(a -> {
			cust.ifPresent(c -> {
				if (binder.writeBeanIfValid(a)) {
					addr = Optional.of(addr_repo.save(a));
					if (a.getCust() == null) {
						c.addAddress(a);
						cust = Optional.of(cust_repo.save(c));
					}
					refresh();
					listener.ifPresent(Runnable::run);
				}
			});
		});
	}

	private void onAddrResetClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		setAddress(addr);
	}

	private void onAddrDeleteClick(com.vaadin.flow.component.ClickEvent<Button> event) {
		addr.ifPresent(a -> {
			Dialog d = new Dialog();
			d.setCloseOnEsc(true);
			d.add(new Label("Adresse wirklich löschen?"));
			d.add(new HorizontalLayout(new Button("Ja, wirklich", ev -> {
				d.close();
				Customer c = a.getCust();
				c.removeAddress(a);
				cust = Optional.of(cust_repo.save(c));
				setAddress(Optional.empty());
				listener.ifPresent(Runnable::run);
			}), new Button("Oops, lieber doch nicht", ev -> d.close())));
			d.open();
		});
	}

}
