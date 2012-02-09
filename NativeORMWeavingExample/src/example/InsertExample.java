/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: ssmith - Bug 371099: Native ORM Byte Code Weaving Example
 ******************************************************************************/
package example;

import model.Address;
import model.Employee;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.SessionManager;

public class InsertExample {

    public static void main(String[] args) {
        DatabaseSession session = (DatabaseSession) SessionManager.getManager().getSession("employee");

        // Create an Employee with an Address
        UnitOfWork uow = session.acquireUnitOfWork();
        Employee employee = createEmployee();
        uow.registerObject(employee);
        uow.commit();
        SessionManager.getManager().destroyAllSessions();
    }

    public static Employee createEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");

        Address address = new Address();
        address.setCity("Toronto");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., Suite 4");
        address.setCountry("Canada");
        employee.setAddress(address);

        employee.addPhoneNumber("Work", "613", "5558812");
        employee.addPhoneNumber("Mobile", "613", "4441234");

        return employee;
    }
}
