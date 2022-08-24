package de.fhwedel.pimpl.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity class for "Kunde"
 * @author Christoph / inf103518
 *
 */
@Entity
public class Customer {

	private static int runnumber = 1;

	private static String createRandomName() {
		Random r = new Random();
		int len = r.nextInt(8) + 2;
		StringBuilder sb = new StringBuilder();

		sb.append((char) (r.nextInt(26) + 'A'));
		while (len-- > 0) {
			sb.append((char) (r.nextInt(26) + 'a'));
		}

		return sb.toString();
	}

	public static Customer createRandomCustomer() {
		return new Customer(createRandomName(),
				createRandomName(), runnumber++ + "", "Teststreet 3", "24232", "Teststadt", 0);
	}
	
	private Integer id;
	private String surname;
	private String prename;
	private String address;
	private String zip;
	private String city;
	private Integer discount;
	private String cnr;
	
	//* Foreign keys *//
	private Set<Booking> bookings;
	private Set<Address> addresses;

	public Customer() {
		this.addresses = new HashSet<>();
		this.bookings = new HashSet<>();
	}

	public Customer(String surname, String prename, String cnr, String address, String zip, String city, Integer discount) {
		this();
		this.surname = surname;
		this.prename = prename;
		this.cnr = cnr;
		this.address = address;
		this.zip = zip;
		this.city = city;
		this.discount = discount;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "kunde_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotNull
	@Column(name = "kdr", unique = true)
	public String getCnr() {
		return cnr;
	}
	
	public void setCnr(String cnr) {
		this.cnr = cnr;
	}
	
	@Size(min = 2, message = "Mindestens zwei Zeichen Länge")
	@Column(name = "nachname")
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Size(min = 2, message = "Mindestens zwei Zeichen Länge")
	@Column(name = "vorname")
	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}
	
	@Size(min = 2, message = "Mindestens zwei Zeichen Länge")
	@Column(name = "strasse")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Size(min = 2, message = "Mindestens zwei Zeichen Länge")
	@Column(name = "plz")
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Size(min = 2, message = "Mindestens zwei Zeichen Länge")
	@Column(name = "stadt")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@NotNull
	@Min(value = 0, message = "Rabatt kann nicht kleiner als 0 sein")
	@Max(value = 100, message = "Rabatt kann maximal 100 sein")
	@Column(name = "rabatt")
	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	@NotNull
	@OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
	public Set<Booking> getBookings() {
		return bookings;
	}
	
	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}
	
	public void addBooking(Booking booking) {
		booking.setCustomer(this);
		bookings.add(booking);
	}
	
	public void removeBooking(Booking booking) {
		bookings.remove(booking);
		booking.setCustomer(null);
	}
	
	@NotNull
	@OneToMany(mappedBy = "cust", orphanRemoval = true, cascade = CascadeType.ALL)
	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public void addAddress(Address addr) {
		addr.setCust(this);
		this.addresses.add(addr);
	}

	public void removeAddress(Address addr) {
		this.addresses.remove(addr);
		addr.setCust(null);
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", surname=" + surname + ", prename=" + prename
				+ ", addresses=" + addresses + ", kdr=" + cnr + "]";
	}

	

	
}