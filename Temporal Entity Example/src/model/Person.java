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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import temporal.TemporalEntity;

public interface Person extends TemporalEntity<Person> {

    public String getName();

    public void setName(String name);

    public Address getAddress();

    public void setAddress(Address address);

    public Map<String, Phone> getPhones();

    public Phone getPhone(String type);

    public Phone addPhone(Phone phone);

    public Phone addPhone(String type, String number);

    public Phone removePhone(String type);

    public Map<String, PersonHobby> getPersonHobbies();

    public Iterator<Hobby> getHobbies();

    public PersonHobby addHobby(Hobby hobby);

    public Set<String> getNicknames();

    public boolean addNickname(String name);

    public boolean removeNickname(String name);

}
