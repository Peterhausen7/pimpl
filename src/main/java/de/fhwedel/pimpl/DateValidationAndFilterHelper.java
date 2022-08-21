package de.fhwedel.pimpl;

import java.time.LocalDate;
import java.time.ZoneId;

import de.fhwedel.pimpl.model.Booking;

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
	
	
}
