/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - JPARS Student Example 
 ******************************************************************************/
package eclipselink.example.jpars.student.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.ManyToMany;

@NamedQueries({ @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s ORDER BY s.id") })
@Entity
@Table(name = "JPARS_STUDENT")
public class Student implements Serializable {
    private static final long serialVersionUID = -7123217923215797455L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    
    @ManyToMany
    private Collection<Course> courses;

    /**
     * Gets the courses.
     * 
     * @return the courses
     */
    public Collection<Course> getCourses() {
	return this.courses;
    }

    /**
     * Sets the courses.
     * 
     * @param courses
     *            the new courses
     */
    public void setCourses(Collection<Course> courses) {
	this.courses = courses;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public Long getId() {
	return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
	this.name = name;
    }
}