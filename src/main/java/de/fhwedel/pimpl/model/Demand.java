package de.fhwedel.pimpl.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class Demand {

	private Integer id;
	
	private Integer count;
	
	private Integer price;
	
	private Integer turnoverTax;
	
	private Date date;
	
	
	
	private Booking booking;
	
	private AdditionalService additionalService;
	
	public Demand() {
	}

	public Demand(Integer count, Integer price, Integer turnoverTax, Date date) {
		super();
		this.count = count;
		this.price = price;
		this.turnoverTax = turnoverTax;
		this.date = date;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "inanspruchname_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Min(value = 1, message = "Menge kann nicht kleiner als 1 sein")
	@Column(name = "menge")
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@NotNull
	@Min(value = 0, message = "Preis kann nicht kleiner als 0 sein")
	@Column(name = "preis")
	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	@NotNull
	@Min(value = 0, message = "USt kann nicht kleiner als 0 sein")
	@Column(name = "ust")
	public Integer getTurnoverTax() {
		return turnoverTax;
	}

	public void setTurnoverTax(Integer turnoverTax) {
		this.turnoverTax = turnoverTax;
	}

	@NotNull
	@Column(name = "datum")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@NotNull
	@ManyToOne
	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	@NotNull
	@ManyToOne
	public AdditionalService getAdditionalService() {
		return additionalService;
	}

	public void setAdditionalService(AdditionalService additionalService) {
		this.additionalService = additionalService;
	}
	
	
	
	
	
	
}
