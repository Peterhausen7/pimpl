package de.fhwedel.pimpl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Guest;

public interface GuestRepo extends JpaRepository<Guest, Integer>{
	List<Guest> findByBooking(Booking b);
}
