package de.fhwedel.pimpl.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity class for "Zimmerkategorie"
 * @author Christoph / inf103518
 *
 */
@Entity
public class RoomCategory {
	
	private Integer id;
	private String description;
	private Integer bedCount;
	private Integer pricePerNight;
	private Integer minPrice;
	
	public RoomCategory() {
	}
	
	public RoomCategory(String description, Integer bedCount, Integer pricePerNight, Integer minPrice) {
		super();
		this.description = description;
		this.bedCount = bedCount;
		this.pricePerNight = pricePerNight;
		this.minPrice = minPrice;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "zimmerkat_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "bezeichnung", unique = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@Min(value = 1, message = "Bettanzahl kann nicht kleiner als 0 sein")
	@Column(name = "bettanzahl")
	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	@NotNull
	@Min(value = 0, message = "Uebernachtungspreis kann nicht kleiner als 0 sein")
	@Column(name = "uebernachtungspreis")
	public Integer getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(Integer pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	@NotNull
	@Min(value = 0, message = "Mindestpreis kann nicht kleiner als 0 sein")
	@Column(name = "Mindestpreis")
	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}


	@Override
	public int hashCode() {
		return Objects.hash(bedCount, description, id, minPrice, pricePerNight);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomCategory other = (RoomCategory) obj;
		return Objects.equals(bedCount, other.bedCount) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(minPrice, other.minPrice)
				&& Objects.equals(pricePerNight, other.pricePerNight);
	}
	
	

}
