package de.fhwedel.pimpl.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entity class for "Zimmer"
 * @author Christoph / inf103518
 *
 */
@Entity
public class Room {

	private Integer id;
	private String roomNumber;
	
	//* Foreign keys *//
	private RoomCategory roomCategory;
	
	public static Room createExampleRooms(String roomNum) {
		return new Room(roomNum);
	}
	
	public Room() {
	}

	public Room(String roomNumber) {
		super();
		this.roomNumber = roomNumber;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "zimmer_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "zimmer_nr", unique = true)
	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	@NotNull
	@ManyToOne
	public RoomCategory getRoomCategory() {
		return roomCategory;
	}

	public void setRoomCategory(RoomCategory roomCategory) {
		this.roomCategory = roomCategory;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, roomCategory, roomNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		return Objects.equals(id, other.id) && Objects.equals(roomCategory, other.roomCategory)
				&& Objects.equals(roomNumber, other.roomNumber);
	}
	
	
	
	
	
	
	
}
