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
 *  dclarke - EclipseLink 2.4 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.admin.model;

import java.util.Collection;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents a target environment where the MySports application can be
 * deployed to. This could be a specific web or application server, a
 * cluster/grid of servers, or a cloud implementation.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@Entity
@Table(name="MYS_ADMIN_ENV")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="ENV_TYPE")
@NamedQuery(name="HostEnvironment.all", query="SELECT he FROM HostEnvironment he ORDER BY he.id")
public abstract class HostEnvironment {
    
    @Id
    @GeneratedValue
    private int id;
    
    private String name;
    
    private String description;

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

    public int getId() {
        return id;
    }
    
    public abstract Collection<Datasource> getDatasources();


}
