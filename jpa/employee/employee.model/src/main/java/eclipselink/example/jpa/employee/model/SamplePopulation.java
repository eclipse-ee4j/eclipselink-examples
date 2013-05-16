/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - Employee Demo 2.4
 ******************************************************************************/
package eclipselink.example.jpa.employee.model;

import java.util.Random;

import javax.persistence.EntityManager;

/**
 * Examples illustrating the use of JPA with the employee domain
 * eclipselink.example.jpa.employee.model.
 * 
 * @see eclipselink.example.jpa.employee.test.model.JavaSEExampleTest
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class SamplePopulation {

    /**
     * Create the specified number of random sample employees.  
     */
    public void createNewEmployees(EntityManager em, int quantity) {
        for (int index = 0; index < quantity; index++) {
            em.persist(createRandomEmployee());
        }
    }

    private static final String[] MALE_FIRST_NAMES = { "Jacob", "Ethan", "Michael", "Alexander", "William", "Joshua", "Daniel", "Jayden", "Noah", "Anthony" };
    private static final String[] FEMALE_FIRST_NAMES = { "Isabella", "Emma", "Olivia", "Sophia", "Ava", "Emily", "Madison", "Abigail", "Chloe", "Mia" };
    private static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson" };

    public Employee createRandomEmployee() {
        Random r = new Random();

        Employee emp = new Employee();
        emp.setGender(Gender.values()[r.nextInt(2)]);
        if (Gender.Male.equals(emp.getGender())) {
            emp.setFirstName(MALE_FIRST_NAMES[r.nextInt(MALE_FIRST_NAMES.length)]);
        } else {
            emp.setFirstName(FEMALE_FIRST_NAMES[r.nextInt(FEMALE_FIRST_NAMES.length)]);
        }
        emp.setLastName(LAST_NAMES[r.nextInt(LAST_NAMES.length)]);
        emp.addPhoneNumber("HOME", "111", "5552222");
        emp.addPhoneNumber("WORK", "222", "5552222");

        return emp;
    }
}
