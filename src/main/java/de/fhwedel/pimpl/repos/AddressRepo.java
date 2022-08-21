package de.fhwedel.pimpl.repos;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import de.fhwedel.pimpl.model.Address;

public interface AddressRepo extends JpaRepositoryImplementation<Address, Integer> {
}
