package de.fhwedel.pimpl.model;

import java.time.LocalDate;
import java.time.ZoneId;
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
public class Booking {
	
	public enum ContractState {
		RESERVED, CHECKED_IN, CHECKED_OUT, FINISHED, CANCELED_PENDING, CANCELED 
	}
	
	private Integer id;
	
	private String bookingNr;
	
	private Date reservation;
	
	private ContractState status;
	
	private String comment;
	
	private Date estimatedArrival;
	
	private Date arrived;
	
	private Date estimatedDeparture;
	
	private Date departed; 
	
	private Integer price;
	
	private String licensePlate;
	
	//* Foreign Keys*//
	
	private Customer customer;
	
	private Room room;
	
	
	public Booking() {
	}
	
	public Booking(String bookingNr, Date reservation, ContractState status, String comment, Date estimatedArrival,
			Date arrived, Date estimatedDeparture, Date departed, Integer price, String licensePlate) {
		super();
		this.bookingNr = bookingNr;
		this.reservation = reservation;
		this.status = status;
		this.comment = comment;
		this.estimatedArrival = estimatedArrival;
		this.arrived = arrived;
		this.estimatedDeparture = estimatedDeparture;
		this.departed = departed;
		this.price = price;
		this.licensePlate = licensePlate;
	}
	
	public String toLabel() {
		LocalDate arrival = estimatedArrival.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate departure = estimatedDeparture.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return "Buchung " + bookingNr + " Ankunft " + arrival + " Abfahrt " + departure;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "buchung_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "buchungs_nr", unique = true)
	public String getBookingNr() {
		return bookingNr;
	}

	public void setBookingNr(String bookingNr) {
		this.bookingNr = bookingNr;
	}

	@NotNull
	@Column(name = "reservierung")
	public Date getReservation() {
		return reservation;
	}

	public void setReservation(Date reservation) {
		this.reservation = reservation;
	}

	@NotNull
	@Column(name = "status")
	public ContractState getStatus() {
		return status;
	}

	public void setStatus(ContractState status) {
		this.status = status;
	}

	@NotNull
	@Column(name = "kommentar")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@NotNull
	@Column(name = "anreise_soll")
	public Date getEstimatedArrival() {
		return estimatedArrival;
	}

	public void setEstimatedArrival(Date estimatedArrival) {
		this.estimatedArrival = estimatedArrival;
	}

	@Column(name = "anreise_ist")
	public Date getArrived() {
		return arrived;
	}

	public void setArrived(Date arrived) {
		this.arrived = arrived;
	}

	@NotNull
	@Column(name = "abreise_soll")
	public Date getEstimatedDeparture() {
		return estimatedDeparture;
	}

	public void setEstimatedDeparture(Date estimatedDeparture) {
		this.estimatedDeparture = estimatedDeparture;
	}

	@Column(name = "abreise_ist")
	public Date getDeparted() {
		return departed;
	}

	public void setDeparted(Date departed) {
		this.departed = departed;
	}

	@NotNull
	@Min(value = 0, message = "Zimmerpreis kann nicht kleiner als 0 sein")
	@Column(name = "zimmerpreis")
	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	@Column(name = "kfz_keinzeichen")
	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	@ManyToOne
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@NotNull
	@ManyToOne
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	
	
	
	
	
	
	
	

}
