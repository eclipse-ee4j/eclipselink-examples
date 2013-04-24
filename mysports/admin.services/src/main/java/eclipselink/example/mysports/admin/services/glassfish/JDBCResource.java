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
package eclipselink.example.mysports.admin.services.glassfish;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Represents a JDBC Resource in GlassFish. Used to interact with GlassFish over
 * REST.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JDBCResource extends Result {
    
    /**
     * Management URI extension to get list of JDBC resources 
     */
    public static final String LIST_URI = "/domain/resources/list-jdbc-resources";
    
    /**
     * Management URI extension to get details of a specific JDBC resource
     */
    public static final String RESOURCE_URI = "/domain/resources/jdbc-resource/";

    @XmlPath("extraProperties/entity/description/text()")
    private String description;

    @XmlPath("extraProperties/entity/enabled/text()")
    private String enabled;

    @XmlPath("extraProperties/entity/jndiName/text()")
    private String jndiName;

    @XmlPath("extraProperties/entity/objectType/text()")
    private String objectType;

    @XmlPath("extraProperties/entity/poolName/text()")
    private String poolName;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }
    
    /**
     * TODO 
     */
    public static String[] getJDBCResourceNames(RESTOperations ops) {
        try {
            Result result = ops.get(LIST_URI, MediaType.APPLICATION_JSON, Result.class);
            return result.getChildrenMessages();
        } catch (JAXBException e) {
            throw new RuntimeException("Failure to get JDBC resource from GlassFish@" + ops.getServer(), e);
        }
    }
    
    /**
     * TODO
     */
    public static JDBCResource getJDBResource(RESTOperations ops, String name) {
        try {
            return ops.get(RESOURCE_URI + name.replace("/", "%2F"), MediaType.APPLICATION_JSON, JDBCResource.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failure to get JDBC resource from GlassFish@" + ops.getServer(), e);
        }
    }
}
