package de.fhwedel.pimpl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Room {

	private Integer id;
	
	private String roomNumber;
	
	
	
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
	
	
	
	
	
}
