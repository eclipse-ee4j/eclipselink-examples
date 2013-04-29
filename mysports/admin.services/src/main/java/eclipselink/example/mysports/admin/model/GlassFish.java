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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eclipselink.example.mysports.admin.services.glassfish.JDBCResource;
import eclipselink.example.mysports.admin.services.glassfish.RESTOperations;

/**
 * Represents a single GlassFish instance.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@Entity
@DiscriminatorValue("GlassFish")
public class GlassFish extends HostEnvironment {

    public List<Datasource> getDatasources() {
        List<Datasource> datasources = new ArrayList<Datasource>();
        RESTOperations ops = new RESTOperations("localhost");
        String[] resourceNames = JDBCResource.getJDBCResourceNames(ops);

        for (String name : resourceNames) {
            Datasource ds = new Datasource();
            ds.setName(name);
            datasources.add(ds);
        }
        return datasources;
    }
}
