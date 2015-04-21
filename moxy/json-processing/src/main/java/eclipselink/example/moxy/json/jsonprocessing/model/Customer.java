/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package eclipselink.example.moxy.json.jsonprocessing.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private int id;
	private String firstName;
    private String lastName;

    private List<PhoneNumber> phoneNumbers;

    public Customer() {
	phoneNumbers = new ArrayList<PhoneNumber>();
    }

    public Customer(int id, String firstName, String lastName, List<PhoneNumber> phoneNumbers) {
	this.id = id;
	this.firstName = firstName;
	this.lastName = lastName;
	this.phoneNumbers = phoneNumbers;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	@Override
	public String toString() {
		String string = id + ": " + firstName + " " + lastName;

		for (PhoneNumber phoneNumber : phoneNumbers) {
			string += ", " + phoneNumber;
		}

		return string;
	}

}