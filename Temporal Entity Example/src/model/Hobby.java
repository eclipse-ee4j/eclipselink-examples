/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a Hobby that a person participates in. This entity type is not temporal but is referred to 
 *
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@Entity
@Table(name="THOBBY")
public class Hobby {

    @Id
    private String name;
    
    private String description;

    private Hobby() {
        super();
    }

    public Hobby(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
