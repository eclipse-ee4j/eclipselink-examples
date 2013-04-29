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
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.admin.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * An extension represents an additional 'virtual' attribute that an entity in
 * the MySports application can store.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Entity
@IdClass(Extension.ID.class)
@Table(name = "MYS_ADMIN_EXT")
public class Extension {

    @Id
    private HostedLeague league;

    @Id
    private String type;

    @Id
    private String name = "";

    @Column(name = "JTYPE")
    private String javaType = "java.lang.String";

    private String columnName;

    public Extension() {
    }

    public Extension(String name, String javaType, String columnName) {
        setName(name);
        setJavaType(javaType);
        setColumnName(columnName);
    }

    public HostedLeague getLeague() {
        return league;
    }

    public String getType() {
        return type;
    }

    protected void setLeague(HostedLeague league, String type) {
        this.league = league;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public static class ID implements Serializable {
        private static final long serialVersionUID = 1L;
        private String league;
        private String type;
        private String name;

        public ID() {
            super();
        }

        public ID(String league, String type, String name) {
            super();
            this.league = league;
            this.type = type;
            this.name = name;
        }

        public String getLeague() {
            return league;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return getLeague().hashCode() + getType().hashCode() + getName().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
                ID id = (ID) obj;
                return getLeague().equals(id.getLeague()) && getType().equals(id.getType()) && getName().equals(id.getName());
            }
            return false;
        }

    }
}
