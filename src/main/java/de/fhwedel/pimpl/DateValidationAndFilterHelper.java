package de.fhwedel.pimpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import de.fhwedel.pimpl.model.Booking;
import de.fhwedel.pimpl.model.Booking.ContractState;

public class DateValidationAndFilterHelper {

	public static boolean validateBookingTimeframe(LocalDate arrival, LocalDate departure, boolean supervisor, LocalDate currDate) {
		
		boolean result = false;
		
		if (arrival != null && departure != null && currDate != null) {
			result = arrival.isAfter(currDate) || arrival.isEqual(currDate);
			System.out.println(result + " arrival nach curr oder equal");
			result = result && departure.isAfter(arrival);
			System.out.println(result + " departure nach arrival");
			
			
			if (!supervisor) {
				result = result && (departure.isBefore(arrival.plusDays(14)) || departure.isEqual(arrival.plusDays(14)));
				System.out.println(result + " departure within 14 days");
			}
		}
		
		return result;
	}
	
	public static boolean isBetweenIncluding(Date start, Date end, Date x) {
		LocalDate startL = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endL = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate xL = x.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return (startL.isBefore(xL) || startL.isEqual(xL)) && (endL.isAfter(xL) || endL.isEqual(xL));
	}
	
	public static boolean validateBookingTimeframe(Date arrival, Date departure, boolean supervisor, LocalDate currDate) {
		
		return DateValidationAndFilterHelper.validateBookingTimeframe(arrival.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
				departure.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), supervisor, currDate);
	}
	
	public static boolean filterRoomAvailability(Booking booking, LocalDate plannedArrival, LocalDate plannedDeparture) {
		boolean result = false;
		
		if (booking != null && plannedArrival != null && plannedDeparture != null) {
			LocalDate bArrival = booking.getArrived() != null ? 
					booking.getArrived().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : 
						booking.getEstimatedArrival().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate bDeparture = booking.getDeparted() != null ? 
					booking.getDeparted().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : 
						booking.getEstimatedDeparture().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			System.out.println("performing room check with");
			System.out.println("arrival " + bArrival);
			System.out.println("departure " + bDeparture);
			System.out.println("geplante anreise" + plannedArrival);
			System.out.println("geplante abreise" + plannedDeparture);
			
			
			
			result = (bDeparture.isBefore(plannedArrival) || bDeparture.isEqual(plannedArrival)) || 
					(bArrival.isAfter(plannedDeparture) || bArrival.isEqual(plannedDeparture));
		}
		
		return result;
	}
	
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
