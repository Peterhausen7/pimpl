package de.fhwedel.pimpl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fhwedel.pimpl.model.RoomCategory;

public interface RoomCategoryRepo extends JpaRepository<RoomCategory, Integer> {
	List<RoomCategory> findByDescriptionContainingAndBedCount(String descrption, Integer bedCount);
	List<RoomCategory> findByDescriptionContaining(String descrption);
}
