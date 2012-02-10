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

import java.util.*;

import javax.persistence.*;

import model.*;

import org.eclipse.persistence.annotations.ChangeTracking;

import temporal.BaseEntity;
import temporal.Effectivity;

@Entity(name = "Person")
@Table(name = "TPERSON")
@NamedQueries({ @NamedQuery(name = "Person.currentById", query = "SELECT p FROM Person p WHERE p.cid = :ID") })
@ChangeTracking(ATTRIBUTE)
public class PersonEntity extends BaseEntity implements Person {

    @Column(name = "P_NAMES")
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AddressEntity.class)
    @JoinColumn(name = "ADDR_ID")
    private Address address;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @MapKey(name = "type")
    private Map<String, Phone> phones = new HashMap<String, Phone>();

    @ElementCollection
    @CollectionTable(name="TPERSON_NNAMES", joinColumns=@JoinColumn(name="OID", referencedColumnName="OID"))
    @Column(name="NAME")
    private Set<String> nicknames = new HashSet<String>();
    
    @Embedded
    private Effectivity effectivity = new Effectivity();

    /**
     * M:1 relationship to continuity.
     */
    @Transient
    private Person continuity;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @MapKey(name = "name")
    private Map<String, PersonHobby> hobbies = new HashMap<String, PersonHobby>();

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

    @Override
    public Person getContinuity() {
        return this.continuity;
    }

    @Override
    public void setContinuity(Person continuity) {
        this.continuity = continuity;
    }

    @Override
    public Effectivity getEffectivity() {
        return this.effectivity;
    }

    public Map<String, PersonHobby> getPersonHobbies() {
        return this.hobbies;
    }

    @Override
    public PersonHobby addHobby(Hobby hobby) {
        PersonHobby personHobby = new PersonHobby(hobby, this);
        this.hobbies.put(hobby.getName(), personHobby);
        return personHobby;
    }

    @Override
    public Iterator<Hobby> getHobbies() {
        return new HobbyIterator(getPersonHobbies().values().iterator());
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

    public String toString() {
        return getEffectivity().toString(this);
    }

    class HobbyIterator implements Iterator<Hobby> {
        Iterator<PersonHobby> personHobbies;

        public HobbyIterator(Iterator<PersonHobby> iterator) {
            personHobbies = iterator;
        }

        @Override
        public boolean hasNext() {
            return personHobbies.hasNext();
        }

        @Override
        public Hobby next() {
            return personHobbies.next().getHobby();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
