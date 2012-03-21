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

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import model.Address;
import model.Hobby;
import model.Person;
import model.PersonHobby;
import model.Phone;

import org.eclipse.persistence.annotations.Property;

import temporal.BaseTemporalEntity;
import temporal.TemporalEntity;
import temporal.TemporalHelper;

@Entity(name = "Person")
@Table(name = "TPERSON")
@NamedQueries({ @NamedQuery(name = "Person.currentById", query = "SELECT p FROM Person p WHERE p.cid = :ID") })
public class PersonEntity extends BaseTemporalEntity<Person> implements Person {

    @Column(name = "P_NAMES")
    private String name;

    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AddressEntity.class)
    @JoinColumn(name = "ADDR_ID")
    private Address address;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @MapKey(name = "type")
    private Map<String, Phone> phones = new HashMap<String, Phone>();

    @ElementCollection
    @CollectionTable(name = "TPERSON_NNAMES", joinColumns = @JoinColumn(name = "OID", referencedColumnName = "OID"))
    @Column(name = "NAME")
    private Set<String> nicknames = new HashSet<String>();

    @OneToMany(mappedBy = "person", cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    @MapKey(name = "name")
    private Map<String, PersonHobby> hobbies = new HashMap<String, PersonHobby>();

    @Basic
    @Property(name = TemporalHelper.NON_TEMPORAL, value = "true")
    private Date dateOfBirth;

    public PersonEntity() {
        setContinuity(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Map<String, Phone> getPhones() {
        return phones;
    }

    public Phone getPhone(String type) {
        return getPhones().get(type);
    }

    public Phone addPhone(Phone phone) {
        getPhones().put(phone.getType(), phone);
        phone.setPerson(this);
        return phone;
    }

    public Phone addPhone(String type, String number) {
        Phone phone = new PhoneEntity();
        phone.setNumber(number);
        phone.setType(type);
        return addPhone(phone);
    }

    public Phone removePhone(String type) {
        return getPhones().remove(type);
    }

    public Map<String, PersonHobby> getPersonHobbies() {
        return this.hobbies;
    }

    @Override
    public PersonHobby addHobby(PersonHobby personHobby) {
        personHobby.setPerson(this);
        this.hobbies.put(personHobby.getName(), personHobby);
        return personHobby;
    }

    @Override
    public PersonHobby addHobby(Hobby hobby, long asOf) {
       PersonHobby ph = new PersonHobby();
       ph.setHobby(hobby);
       ph.getEffectivity().setStart(asOf);
       return addHobby(ph);
    }

    @Override
    public PersonHobby removeHobby(Hobby hobby, long asOf, long current) {
        PersonHobby personHobby = getPersonHobbies().remove(hobby.getName());
        if (personHobby == null) {
            throw new IllegalArgumentException("Hobby not found: " + hobby);
        }
        if (current >= asOf) {
            this.hobbies.remove(hobby.getName());
        }
        personHobby.getEffectivity().setEnd(asOf);
        return personHobby;
    }

    public Set<String> getNicknames() {
        return nicknames;
    }

    public boolean addNickname(String name) {
        return getNicknames().add(name);
    }

    public boolean removeNickname(String name) {
        return getNicknames().remove(name);
    }

    public Date getDateOfBirth() {
        return isContinuity() ? this.dateOfBirth : getContinuity().getDateOfBirth();
    }

    public void setDateOfBirth(Date dateOfBirth) {
        if (isContinuity()) {
            this.dateOfBirth = dateOfBirth;
        } else {
            getContinuity().setDateOfBirth(dateOfBirth);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void applyEdition(TemporalEntity edition) {
       Person personEdition = (Person) edition;
       
       for (PersonHobby ph: personEdition.getPersonHobbies().values()) {
           PersonHobby newPh = new PersonHobby();
           newPh.setHobby(ph.getHobby());
           newPh.getEffectivity().setStart(ph.getEffectivity().getStart());
           newPh.getEffectivity().setEnd(ph.getEffectivity().getEnd());
           addHobby(newPh);
       }
       personEdition.getPersonHobbies().clear();
    }

    public String toString() {
        return getEffectivity().toString(this);
    }

}
