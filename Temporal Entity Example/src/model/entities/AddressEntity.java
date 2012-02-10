/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package model.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import temporal.BaseEntity;
import temporal.Effectivity;

import model.Address;

@Entity(name = "Address")
@Table(name = "TADDRESS")
public class AddressEntity extends BaseEntity implements Address {

    private String street;

    private String city;

    private String state;

    @Embedded
    private Effectivity effectivity = new Effectivity();

    /**
     * M:1 relationship to continuity.
     */
    @Transient
    private Address continuity;

    public AddressEntity() {
        super();
        setContinuity(this);
    }

    public AddressEntity(String street, String city, String state) {
        this();
        this.street = street;
        this.city = city;
        this.state = state;
    }

    public AddressEntity(String street, String city, String state, long start, long end) {
        this(street, state, city);
        getEffectivity().setStart(start);
        getEffectivity().setEnd(end);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public Address getContinuity() {
        return this.continuity;
    }

    @Override
    public void setContinuity(Address continuity) {
        this.continuity = continuity;
    }

    @Override
    public Effectivity getEffectivity() {
        return this.effectivity;
    }

    public String toString() {
        return getEffectivity().toString(this);
    }

}
