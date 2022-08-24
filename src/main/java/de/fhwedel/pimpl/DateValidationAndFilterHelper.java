package de.fhwedel.pimpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;

public class DateValidationAndFilterHelper {

	/**
	 * Validates the booking time frame according to the business rules
	 * @param arrival the arrival date of the booking
	 * @param departure the departure date of the booking
	 * @param supervisor flag if the rule is to be checked as a supervisor
	 * @param currDate the reservation date
	 * @return true if the booking time frame is valid according to the business rules
	 */
	public static boolean validateBookingTimeframe(LocalDate arrival, LocalDate departure, boolean supervisor, LocalDate currDate) {
		boolean result = false;
		if (arrival != null && departure != null && currDate != null) {
			result = arrival.isAfter(currDate) || arrival.isEqual(currDate);
			result = result && departure.isAfter(arrival);
			if (!supervisor) {
				result = result && (departure.isBefore(arrival.plusDays(14)) || departure.isEqual(arrival.plusDays(14)));
				System.out.println(result + " departure within 14 days");
			}
		}
		return result;
	}
	
	/**
	 * Validates the booking time frame according to the business rules
	 * @param arrival the arrival date of the booking
	 * @param departure the departure date of the booking
	 * @param supervisor flag if the rule is to be checked as a supervisor
	 * @param currDate the reservation date
	 * @return true if the booking time frame is valid according to the business rules
	 */
	public static boolean validateBookingTimeframe(Date arrival, Date departure, boolean supervisor, LocalDate currDate) {
		return DateValidationAndFilterHelper.validateBookingTimeframe(arrival.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
				departure.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), supervisor, currDate);
	}
	
	/**
	 * Checks if a given date x is between the dates start and end, inclusive for both
	 * @param start the earlier date
	 * @param end the later date
	 * @param x the date to check
	 * @return true if x is between inclusive start and end
	 */
	public static boolean isBetweenIncluding(Date start, Date end, Date x) {
		LocalDate startL = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endL = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate xL = x.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return (startL.isBefore(xL) || startL.isEqual(xL)) && (endL.isAfter(xL) || endL.isEqual(xL));
	}
	
	/**
	 * Validates the arrival date 
	 * @param arrival the arrival date
	 * @param reservationDate the reservation date
	 * @return true if arrival is after or same as reservation. Also returns true if arrival is null 
	 */
	public static boolean validateArrival(Date arrival, LocalDate reservationDate) {
		if (arrival == null) {
			return true;
		}
		if (reservationDate == null) {
			return false;
		}
		
		LocalDate arrivalL = arrival.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return arrivalL.isAfter(reservationDate) || arrivalL.isEqual(reservationDate);
	}
	
	/**
	 * Validates the departure date
	 * @param departure the departure date
	 * @param arrival the arrival date
	 * @return true if departure is after the arrival date. also returns true if the departure is null. 
	 */
	public static boolean validateDeparture(Date departure, LocalDate arrival) {
		if (departure == null) {
			return true;
		}
		if (arrival == null) {
			return false;
		}
		
		LocalDate departureL = departure.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return departureL.isAfter(arrival);
	}
	
	/**
	 * Helper method to filter. Checks if the passed arrival/departure dates do not overlap with the passed booking 
	 * @param booking the booking to check against
	 * @param plannedArrival the arrival date to check
	 * @param plannedDeparture the planned date check
	 * @return true if the booking doesn't overlap with the arrival/departure
	 */
	public static boolean filterRoomAvailability(Booking booking, LocalDate plannedArrival, LocalDate plannedDeparture) {
		boolean result = false;
		if (booking != null && plannedArrival != null && plannedDeparture != null) {
			LocalDate bArrival = booking.getArrived() != null ? 
					booking.getArrived().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : 
						booking.getEstimatedArrival().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			LocalDate bDeparture = booking.getDeparted() != null ? 
					booking.getDeparted().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : 
						booking.getEstimatedDeparture().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			result = (bDeparture.isBefore(plannedArrival) || bDeparture.isEqual(plannedArrival)) || 
					(bArrival.isAfter(plannedDeparture) || bArrival.isEqual(plannedDeparture));
		}
		return result;
	}
	
	/**
	 * Calculates the default price of a booking according to the business rules
	 * @param pricePerNight the default price per night of the room
	 * @param discount the discount ratio of the customer
	 * @param minPrice the minimum price of the room
	 * @return the default price of the booking
	 */
	public static int calcPrice(int pricePerNight, int discount, int minPrice) {
		return Math.max(pricePerNight * (1 - (discount / 100)), minPrice);
	}
	
	/**
	 * Checks if the passed status is equal or lower in "hierarchy" then the passed statusTarget
	 * The hierarchy is reserved < checked in < checked out < canceled_pending < finished == canceled
	 * @param status the status to check
	 * @param statusTarget the target to compare to
	 * @return true if status is lower or equal to statusTarget in "hierarchy"
	 */
	public static boolean checkStatusHierarchy(ContractState status, ContractState statusTarget) {
		boolean result = false;
		switch(statusTarget) {
		case FINISHED:
		case CANCELED:
			result = true;	
			break;
		case CANCELED_PENDING: 
			result = status.equals(ContractState.CANCELED_PENDING);
		case CHECKED_OUT:
			result = result || status.equals(ContractState.CHECKED_OUT);
		case CHECKED_IN:
			result = result || status.equals(ContractState.CHECKED_IN);
		case RESERVED:
			result = result || status.equals(ContractState.RESERVED);
			break;
		}
		return result;
	}
	
	
}
