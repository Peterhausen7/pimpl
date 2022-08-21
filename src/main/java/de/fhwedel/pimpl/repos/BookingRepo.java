package de.fhwedel.pimpl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
	List<Booking> findByStatusOrStatus(ContractState state, ContractState state2);
}
