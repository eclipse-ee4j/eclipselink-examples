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
package eclipselink.example.mysports.admin.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import eclipselink.example.mysports.admin.model.Datasource;
import eclipselink.example.mysports.admin.services.glassfish.JDBCResource;
import eclipselink.example.mysports.admin.services.glassfish.RESTOperations;

@ManagedBean
@RequestScoped
public class DatasourceRepositoryBean {

    private List<Datasource> datasources;

    public List<Datasource> getDatasources() {
        if (this.datasources == null) {
            this.datasources = new ArrayList<Datasource>();
            RESTOperations ops = new RESTOperations("localhost");
            String[] resourceNames = JDBCResource.getJDBCResourceNames(ops);

            for (String name : resourceNames) {
                Datasource ds = new Datasource();
                ds.setName(name);
                this.datasources.add(ds);

            }
        }
        return datasources;
    }
}
