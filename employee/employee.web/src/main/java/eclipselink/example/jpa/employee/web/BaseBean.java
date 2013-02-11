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

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.logging.SessionLogEntry;

import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public abstract class BaseBean {

    private EntityManagerFactory emf;

    private Diagnostics diagnostics;

    private List<String> sql;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    protected void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        this.diagnostics = Diagnostics.getInstance(emf);
    }

    protected void startSqlCapture() {
        this.sql = null;
        this.diagnostics.start();
    }

    protected void stopSqlCapture() {
        setSql(this.diagnostics.stop());
    }

    private void setSql(SQLTrace sqlTrace) {
        if (sqlTrace != null) {
            List<String> strings = new ArrayList<String>();
            for (SessionLogEntry entry : sqlTrace.getEntries()) {
                String val = entry.getMessage().trim();
                while (!val.isEmpty() && (val.endsWith("\n") || val.endsWith("\r"))) {
                    val = val.substring(0, val.length());
                }
                if (!val.isEmpty()) {
                    System.out.println("SQL: '" + val + "'");
                    strings.add(val);
                }
            }
            this.sql = strings;
        } else {
            this.sql = null;
        }
    }

    public List<String> getSql() {
        return sql;
    }

}
