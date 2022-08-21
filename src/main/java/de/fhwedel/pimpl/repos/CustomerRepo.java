package de.fhwedel.pimpl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fhwedel.pimpl.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
	List<Customer> findBySurnameContaining(String name);
	List<Customer> findByPrenameContaining(String name);
	List<Customer> findByCnr(String cnr);
	List<Customer> findBySurnameContainingAndPrenameContaining(
			String surname, String prename);
	List<Customer> findBySurnameContainingAndPrenameContainingAndCnr(
			String surname, String prename, String cnr);
}
