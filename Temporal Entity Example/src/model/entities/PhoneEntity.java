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

import static org.eclipse.persistence.annotations.ChangeTrackingType.ATTRIBUTE;

import javax.persistence.*;

import model.Person;
import model.Phone;

import org.eclipse.persistence.annotations.ChangeTracking;

import temporal.BaseEntity;
import temporal.Effectivity;

@Entity(name = "Phone")
@Table(name = "TPHONE")
@ChangeTracking(ATTRIBUTE)
public class PhoneEntity extends BaseEntity implements Phone {

    @Column(name = "PTYPE")
    private String type;

    @Column(name = "PNUM")
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = PersonEntity.class)
    private Person person;

    @Embedded
    private Effectivity effectivity = new Effectivity();

    /**
     * M:1 relationship to continuity.
     */
    @Transient
    private Phone continuity;

    public PhoneEntity() {
        setContinuity(this);
    }

    public PhoneEntity(String type, String number) {
        setType(type);
        setNumber(number);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public Phone getContinuity() {
        return this.continuity;
    }

    @Override
    public void setContinuity(Phone continuity) {
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
