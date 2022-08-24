package de.fhwedel.pimpl.views;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@SuppressWarnings("serial")
@Route("manage")
@UIScope
public class MainView extends Composite<Component> {
	
	
	private boolean supervisor = false;
	private Tab t1 = new Tab("Kunden");
	private Tab t2 = new Tab("Aufträgee");
	private Tabs ts = new Tabs(false, t1, t2);
	private Button toggleSuper = new Button("Supervisor aktivieren");
	private DatePicker globalDate = new DatePicker();
	private Label supervisorStatus = new Label();
	private HorizontalLayout supervisorLayout = new HorizontalLayout(toggleSuper);
	private AppLayout view = new AppLayout();

	private Map<Tab, Component> tabs = new HashMap<>();

	public MainView(CustomerView cv, BookingView bv) {
		cv.listenToNavigate(() -> ts.setSelectedTab(t2));
		bv.listenToNavigate(() -> ts.setSelectedTab(t1));
		tabs.put(t1, cv);
		tabs.put(t2, bv);
		ts.setOrientation(Orientation.HORIZONTAL);
		ts.addSelectedChangeListener(this::tabChanged);
		view.addToNavbar(ts);
		
		toggleSuper.addClickListener(e -> {
			supervisor = !supervisor;
			supervisorStatus.setText(supervisor ? "Supervisor aktiv" : "");
			cv.supervisorChange(supervisor);
			bv.setSupervisor(supervisor);
		});
		
		supervisorLayout.add(supervisorStatus, globalDate);
		view.addToNavbar(supervisorLayout);
		
		globalDate.setValue(LocalDate.now());
		
		globalDate.addValueChangeListener(e -> {
			bv.setGlobalDate(e.getValue());
		});
		
		
	
		ts.setSelectedTab(t1);
		
		
	}

	@Override
	protected Component initContent() {
		return view;
	}
	
	

	private void tabChanged(SelectedChangeEvent event) {
		view.setContent(tabs.get(event.getSelectedTab()));
	}
	
}
