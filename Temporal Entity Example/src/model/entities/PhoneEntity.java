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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import model.Person;
import model.Phone;
import temporal.BaseTemporalEntity;

@Entity(name = "Phone")
@Table(name = "TPHONE")
public class PhoneEntity extends BaseTemporalEntity<Phone> implements Phone {

    @Column(name = "PTYPE")
    private String type;

    @Column(name = "PNUM")
    private String number;

    @ManyToOne
    private Person person;

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

    public String toString() {
        return getEffectivity().toString(this);
    }

}
