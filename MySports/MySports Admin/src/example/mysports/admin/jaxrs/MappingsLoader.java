/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
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
package example.mysports.admin.jaxrs;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.MultitenantMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;

import example.mysports.admin.model.Extension;
import example.mysports.admin.model.HostedLeague;

/**
 * Load the {@link Extension} from the database and generate the ORM and OXM
 * mapping files for the extensions.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MappingsLoader {

    private static final String MODEL_PACKAGE = "example.mysports.model";

    public static XMLEntityMappings getXMLEntityMappings(EntityManager em, String leagueId) {
        HostedLeague league = em.find(HostedLeague.class, leagueId);

        XMLEntityMappings mappings = new XMLEntityMappings();
        mappings.setVersion("2.3");
        mappings.setEntities(new ArrayList<EntityAccessor>());
        mappings.setEmbeddables(new ArrayList<EmbeddableAccessor>());
        mappings.setMappedSuperclasses(new ArrayList<MappedSuperclassAccessor>());

        mappings.setPackage(MODEL_PACKAGE);

        addEntity(em, league, "Division", mappings);
        addEntity(em, league, "Team", mappings);
        addEntity(em, league, "Player", mappings);

        return mappings;
    }

    public static String getORMapping(EntityManager em, String leagueId) {
        XMLEntityMappings mappings = getXMLEntityMappings(em, leagueId);

        StringWriter writer = new StringWriter();
        XMLEntityMappingsWriter.write(mappings, writer);

        return writer.toString();
    }

    public static void addEntity(EntityManager em, HostedLeague league, String entityType, XMLEntityMappings mappings) {

        EntityAccessor accessor = new EntityAccessor();
        accessor.setClassName(entityType);
        mappings.getEntities().add(accessor);

        // Override table name if specified
        if (league.getTableNames().containsKey(entityType)) {
            TableMetadata table = new TableMetadata();
            table.setName(league.getTableNames().get(entityType));
            accessor.setTable(table);
        }

        // Add multi-tenancy
        if (league.isMultitenant()) {
            MultitenantMetadata multitenant = new MultitenantMetadata();
            multitenant.setType(MultitenantType.SINGLE_TABLE.name());
            List<TenantDiscriminatorColumnMetadata> tenantDiscriminatorColumns = new ArrayList<TenantDiscriminatorColumnMetadata>(1);
            TenantDiscriminatorColumnMetadata tenantDiscColumn = new TenantDiscriminatorColumnMetadata();
            tenantDiscColumn.setName("LEAGUE_ID");
            tenantDiscColumn.setContextProperty("mysports.league");
            tenantDiscColumn.setLength(5);
            tenantDiscriminatorColumns.add(tenantDiscColumn);
            multitenant.setTenantDiscriminatorColumns(tenantDiscriminatorColumns);
            accessor.setMultitenant(multitenant);
        }

        if (league != null) {
            TypedQuery<Extension> q = em.createNamedQuery("Extension.findByLeagueAndEntity", Extension.class);
            q.setParameter("LEAGUE", league.getId());
            q.setParameter("ENTITY", entityType);
            List<Extension> extensions = q.getResultList();

            if (!extensions.isEmpty()) {

                for (Extension ext : extensions) {
                    BasicAccessor allergies = new BasicAccessor();
                    allergies.setName(ext.getName());
                    allergies.setAttributeType(ext.getJavaType());
                    allergies.setAccess("VIRTUAL");

                    ColumnMetadata column = new ColumnMetadata();
                    column.setName(ext.getColumnName());
                    allergies.setColumn(column);

                    if (accessor.getAttributes() == null) {
                        accessor.setAttributes(new XMLAttributes());
                    }
                    if (accessor.getAttributes().getBasics() == null) {
                        accessor.getAttributes().setBasics(new ArrayList<BasicAccessor>());
                    }
                    accessor.getAttributes().getBasics().add(allergies);
                }
            }
        }
    }

    public static XmlBindings getXMLBindings(EntityManager em, String leagueId) {
        XmlBindings xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(MODEL_PACKAGE);
        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);
        ObjectFactory objectFactory = new ObjectFactory();

        addXMLExtensions(em, leagueId, "Division", xmlBindings, objectFactory);
        addXMLExtensions(em, leagueId, "Team", xmlBindings, objectFactory);
        addXMLExtensions(em, leagueId, "Player", xmlBindings, objectFactory);

        return xmlBindings;
    }

    private static void addXMLExtensions(EntityManager em, String leagueId, String entityType, XmlBindings xmlBindings, ObjectFactory objectFactory) {
        JavaType javaType = new JavaType();
        javaType.setName(entityType);
        javaType.setJavaAttributes(new JavaAttributes());
        xmlBindings.getJavaTypes().getJavaType().add(javaType);

        TypedQuery<Extension> q = em.createNamedQuery("Extension.findByLeagueAndEntity", Extension.class);
        q.setParameter("LEAGUE", leagueId);
        q.setParameter("ENTITY", entityType);
        List<Extension> extensions = q.getResultList();

        if (!extensions.isEmpty()) {

            for (Extension ext : extensions) {
                XmlElement xmlElement = new XmlElement();
                xmlElement.setJavaAttribute(ext.getName());
                xmlElement.setXmlPath(ext.getXmlPath());
                xmlElement.setType(ext.getJavaType());
                javaType.getJavaAttributes().getJavaAttribute().add(objectFactory.createXmlElement(xmlElement));

            }
        }
    }
}
