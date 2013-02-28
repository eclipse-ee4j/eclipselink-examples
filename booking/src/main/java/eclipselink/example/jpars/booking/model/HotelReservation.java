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

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_BOOKING_HOTEL_RESERVATION")
public class HotelReservation {
    private Calendar checkIn;
    private Calendar checkOut;

    /**
     * Instantiates a new hotel reservation.
     */
    public HotelReservation() {
    }

    /**
     * Gets the check in.
     * 
     * @return the check in
     */
    public Calendar getCheckIn() {
        return checkIn;
    }

    /**
     * Sets the check in.
     * 
     * @param checkIn
     *            the new check in
     */
    public void setCheckIn(Calendar checkIn) {
        this.checkIn = checkIn;
    }

    /**
     * Gets the check out.
     * 
     * @return the check out
     */
    public Calendar getCheckOut() {
        return checkOut;
    }

    /**
     * Sets the check out.
     * 
     * @param checkOut
     *            the new check out
     */
    public void setCheckOut(Calendar checkOut) {
        this.checkOut = checkOut;
    }
}
