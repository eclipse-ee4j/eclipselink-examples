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
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.web;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.eclipse.persistence.logging.SessionLogEntry;

import eclipselink.example.jpa.employee.persistence.SQLTrace;
import eclipselink.example.jpa.employee.services.PersistenceHelper;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@ApplicationScoped
public class SqlTraceAccessor {

    private PersistenceHelper persistence;

    public PersistenceHelper getPersistence() {
        return persistence;
    }

    @EJB
    public void setPersistence(PersistenceHelper persistence) {
        this.persistence = persistence;
    }

    public List<String> getSql() {
        List<String> strings = new ArrayList<String>();

        SQLTrace sqlTrace = getPersistence().endSQLTrace();
        if (sqlTrace != null) {
            for (SessionLogEntry entry : sqlTrace.getEntries()) {
                strings.add(entry.getMessage());
            }
        }

        return strings;
    }

    public String create() {
        return CreateEmployee.PAGE;
    }
}
