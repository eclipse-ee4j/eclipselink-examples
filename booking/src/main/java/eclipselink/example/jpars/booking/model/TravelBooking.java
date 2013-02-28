/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gonural - initial JPA-RS Booking Employee
 * 
 ******************************************************************************/
package eclipselink.examples.jpars.booking.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_BOOKING_TRAVEL")
public class TravelBooking {
    private FlightReservation flight;
    private HotelReservation hotel;
    private CarReservation car;

    /**
     * Instantiates a new travel booking.
     */
    public TravelBooking() {
    }

    /**
     * Gets the flight.
     * 
     * @return the flight
     */
    public FlightReservation getFlight() {
        return flight;
    }

    /**
     * Sets the flight.
     * 
     * @param flight
     *            the new flight
     */
    public void setFlight(FlightReservation flight) {
        this.flight = flight;
    }

    /**
     * Gets the hotel.
     * 
     * @return the hotel
     */
    public HotelReservation getHotel() {
        return hotel;
    }

    /**
     * Sets the hotel.
     * 
     * @param hotel
     *            the new hotel
     */
    public void setHotel(HotelReservation hotel) {
        this.hotel = hotel;
    }

    /**
     * Gets the car.
     * 
     * @return the car
     */
    public CarReservation getCar() {
        return car;
    }

    /**
     * Sets the car.
     * 
     * @param car
     *            the new car
     */
    public void setCar(CarReservation car) {
        this.car = car;
    }
}
