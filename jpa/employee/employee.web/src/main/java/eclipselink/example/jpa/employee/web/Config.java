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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.Server;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@SessionScoped
public class Config {

    private List<String> typeNames;

    private boolean displaySql = false;

    public Config() {
        this.typeNames = new ArrayList<String>();
        this.typeNames.add("Employee");
        this.typeNames.add("Address");
        this.typeNames.add("PhoneNumber");
        this.typeNames.add("Project");
    }

    private EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public boolean isDisplaySql() {
        return displaySql;
    }

    public void setDisplaySql(boolean displaySql) {
        this.displaySql = displaySql;
    }

    public void toggleSqlDisplay() {
        this.displaySql = !this.displaySql;
    }

    public String getToggleSqlDisplayButton() {
        return isDisplaySql() ? "Disable SQL Display" : "Enable SQL Display";
    }

    public String getCacheSize(String typeName) {
        System.out.println("Config.getCacheSize('" + typeName + "')");
        EntityManager em = getEmf().createEntityManager();
        Server session = em.unwrap(Server.class);
        ClassDescriptor descriptor = session.getDescriptorForAlias(typeName);
        if (descriptor != null) {
            int size = ((IdentityMapAccessor) session.getIdentityMapAccessor()).getIdentityMap(descriptor.getJavaClass()).getSize();
            return Integer.toString(size);
        } else {
            return "N/A";
        }
    }
}
