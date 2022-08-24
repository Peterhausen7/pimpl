package de.fhwedel.pimpl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * Entity class for "Zusatzleistung"
 * @author Christoph / inf103518
 *
 */
@Entity
public class AdditionalService {
	
	private Integer id;
	private String descrption;
	private Integer price;	
	private Integer turnoverTax;
	
	public AdditionalService() {
	}

	public AdditionalService(String descrption, Integer price, Integer turnoverTax) {
		super();
		this.descrption = descrption;
		this.price = price;
		this.turnoverTax = turnoverTax;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "zusatzleistung_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "bezeichnung", unique = true)
	public String getDescrption() {
		return descrption;
	}

	public void setDescrption(String descrption) {
		this.descrption = descrption;
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
	@Column(name = "ust_satz")
	public Integer getTurnoverTax() {
		return turnoverTax;
	}

	public void setTurnoverTax(Integer turnoverTax) {
		this.turnoverTax = turnoverTax;
	}
	
	

}
