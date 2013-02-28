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
package eclipselink.example.jpars.booking.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_BOOKING_CAR_RESERVATION")
public class CarReservation {
    private String carPlate;
    private Calendar rentStartDate;
    private Calendar rentEndDate;

    /**
     * Instantiates a new car reservation.
     */
    public CarReservation() {
    }

    /**
     * Gets the car plate.
     *
     * @return the car plate
     */
    public String getCarPlate() {
        return carPlate;
    }

    /**
     * Sets the car plate.
     *
     * @param carPlate the new car plate
     */
    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    /**
     * Gets the rent start date.
     *
     * @return the rent start date
     */
    public Calendar getRentStartDate() {
        return rentStartDate;
    }

    /**
     * Sets the rent start date.
     *
     * @param rentStartDate the new rent start date
     */
    public void setRentStartDate(Calendar rentStartDate) {
        this.rentStartDate = rentStartDate;
    }

    /**
     * Gets the rent end date.
     *
     * @return the rent end date
     */
    public Calendar getRentEndDate() {
        return rentEndDate;
    }

    /**
     * Sets the rent end date.
     *
     * @param rentEndDate the new rent end date
     */
    public void setRentEndDate(Calendar rentEndDate) {
        this.rentEndDate = rentEndDate;
    }
}
