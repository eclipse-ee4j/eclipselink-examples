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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_BOOKING_ADDRESS")
public class Address {
    @Id
    @Column(name = "ADDRESS_ID")
    @GeneratedValue
    private int id;

    @Basic
    private String street;

    @Basic
    private String city;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String country;

    @Basic
    private String province;

    @Basic
    @Column(name = "P_CODE")
    private String postalCode;

    /**
     * Instantiates a new address.
     */
    public Address() {
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the street.
     * 
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street.
     * 
     * @param street
     *            the new street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the city.
     * 
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     * 
     * @param city
     *            the new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the country.
     * 
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     * 
     * @param country
     *            the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the province.
     * 
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the province.
     * 
     * @param province
     *            the new province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Gets the postal code.
     * 
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code.
     * 
     * @param postalCode
     *            the new postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}