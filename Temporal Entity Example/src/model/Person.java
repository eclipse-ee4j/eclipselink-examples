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
package model;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

import temporal.TemporalEntity;

public interface Person extends TemporalEntity<Person> {

    String getName();

    void setName(String name);

    Address getAddress();

    void setAddress(Address address);

    Map<String, Phone> getPhones();

    Phone getPhone(String type);

    Phone addPhone(Phone phone);

    Phone addPhone(String type, String number);

    Phone removePhone(String type);

    Map<String, PersonHobby> getPersonHobbies();

    PersonHobby addHobby(Hobby hobby, long asOf);
    PersonHobby addHobby(PersonHobby personHobby);

    PersonHobby removeHobby(Hobby hobby, long asOf, long current);

    Set<String> getNicknames();

    boolean addNickname(String name);

    boolean removeNickname(String name);

    Date getDateOfBirth();

    void setDateOfBirth(Date dateOfBirth);

    String getEmail();

    void setEmail(String email);

}
