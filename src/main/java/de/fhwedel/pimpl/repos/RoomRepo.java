package de.fhwedel.pimpl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fhwedel.pimpl.model.Room;
import de.fhwedel.pimpl.model.RoomCategory;

public interface RoomRepo extends JpaRepository<Room, Integer> {
	List<Room> findByRoomCategory(RoomCategory roomCat);

}
