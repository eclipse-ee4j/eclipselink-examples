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
package eclipselink.example.mysports.admin.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.MultitenantMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.TenantTableDiscriminatorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;

import eclipselink.example.mysports.admin.model.Extension;
import eclipselink.example.mysports.admin.model.HostedLeague;

/**
 * Load the {@link Extension} from the database and generate the ORM and OXM
 * mapping files for the extensions.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MappingsLoader {

    private static final String MODEL_PACKAGE = "eclipselink.example.mysports.application.model";

    public static XMLEntityMappings getXMLEntityMappings(EntityManager em, String leagueId) {
        HostedLeague league = em.find(HostedLeague.class, leagueId);

        if (league == null) {
            throw new IllegalArgumentException("Unknown League: " + leagueId);
        }

        return getXMLEntityMappings(league);
    }

    public static XMLEntityMappings getXMLEntityMappings(HostedLeague league) {
        XMLEntityMappings mappings = new XMLEntityMappings();
        mappings.setVersion(Version.getVersion());
        mappings.setEntities(new ArrayList<EntityAccessor>());
        mappings.setEmbeddables(new ArrayList<EmbeddableAccessor>());
        mappings.setMappedSuperclasses(new ArrayList<MappedSuperclassAccessor>());

        mappings.setPackage(MODEL_PACKAGE);

        addEntity(league, "Division", mappings);
        addEntity(league, "Team", mappings);
        addEntity(league, "Player", mappings);

        return mappings;
    }

    public static String getORMapping(EntityManager em, String leagueId) {
        XMLEntityMappings mappings = getXMLEntityMappings(em, leagueId);

        StringWriter writer = new StringWriter();
        XMLEntityMappingsWriter.write(mappings, writer);

        return writer.toString();
    }

    public static void addEntity(HostedLeague league, String entityType, XMLEntityMappings mappings) {

        EntityAccessor accessor = new EntityAccessor();
        accessor.setClassName(entityType);
        mappings.getEntities().add(accessor);
        
        switch (league.getDataIsolation()) {
        case ROW:
            // Add @Multitenant(SINGLE_TABLE)
            MultitenantMetadata multitenant = new MultitenantMetadata();
            multitenant.setType(MultitenantType.SINGLE_TABLE.name());
            List<TenantDiscriminatorColumnMetadata> tenantDiscriminatorColumns = new ArrayList<TenantDiscriminatorColumnMetadata>(1);
            TenantDiscriminatorColumnMetadata tenantDiscColumn = new TenantDiscriminatorColumnMetadata();
            tenantDiscColumn.setName("LEAGUE_ID");
            tenantDiscColumn.setContextProperty("league");
            tenantDiscColumn.setLength(5);
            tenantDiscriminatorColumns.add(tenantDiscColumn);
            multitenant.setTenantDiscriminatorColumns(tenantDiscriminatorColumns);
            accessor.setMultitenant(multitenant);
            break;
        case TABLE:
            // Add @Multitenant(TABLE_PER_TENANT) with
            // @TenenatTableDisciminator(SUFFIX)
            multitenant = new MultitenantMetadata();
            multitenant.setType(MultitenantType.TABLE_PER_TENANT.name());
            TenantTableDiscriminatorMetadata tenantTableDiscriminator = new TenantTableDiscriminatorMetadata();
            tenantTableDiscriminator.setType(TenantTableDiscriminatorType.SUFFIX.name());
            tenantTableDiscriminator.setContextProperty("league");
            multitenant.setTenantTableDiscriminator(tenantTableDiscriminator);
            accessor.setMultitenant(multitenant);
            break;
        case SCHEMA:
            // Add @Multitenant(TABLE_PER_TENANT) with
            // @TenenatTableDisciminator(SCHEMA)
            multitenant = new MultitenantMetadata();
            multitenant.setType(MultitenantType.TABLE_PER_TENANT.name());
            tenantTableDiscriminator = new TenantTableDiscriminatorMetadata();
            tenantTableDiscriminator.setType(TenantTableDiscriminatorType.SCHEMA.name());
            tenantTableDiscriminator.setContextProperty("league");
            multitenant.setTenantTableDiscriminator(tenantTableDiscriminator);
            accessor.setMultitenant(multitenant);
            break;

        // TODO
        case DATABASE:
            throw new RuntimeException("DataIsolation.DATABASE::NOT YET SUPPORTED");
        }

        if (league != null) {

            for (Extension ext : league.getExtensions(entityType)) {
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
