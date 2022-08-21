package de.fhwedel.pimpl.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Guest {
	
	private Integer id;
	
	private String surname;
	
	private String prename;
	
	private Date birthDate;
	
	private Date checkedIn;
	
	private Date checkedOut;
	
	
	private Booking booking;
	
	public Guest() {
	}

	public Guest(String surname, String prename, Date birthDate, Date checkedIn, Date checkedOut) {
		super();
		this.surname = surname;
		this.prename = prename;
		this.birthDate = birthDate;
		this.checkedIn = checkedIn;
		this.checkedOut = checkedOut;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "gast_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "nachname")
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@NotNull
	@Column(name = "vorname")
	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	@NotNull
	@Column(name = "geburtsdatum")
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = "check_in")
	public Date getCheckedIn() {
		return checkedIn;
	}

	public void setCheckedIn(Date checkedIn) {
		this.checkedIn = checkedIn;
	}

	@Column(name = "check_out")
	public Date getCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(Date checkedOut) {
		this.checkedOut = checkedOut;
	}

	@NotNull
	@ManyToOne
	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}
	
	
	
	
	
	
}