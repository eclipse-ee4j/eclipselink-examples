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

import org.eclipse.persistence.annotations.Multitenant;

/**
 * This enum describes the various data isolation modes the MySports example
 * application supports. It leverages the {@link Multitenant} types supported by
 * EclipseLink combined with application specific usage.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public enum DataIsolation {

    /**
     * The data for this league in the same table as other leagues.
     * 
     * @Multitenant(SINGLE_TABLE)
     */
    ROW,

    /**
     * The data for this league is stored in its own table within a shared
     * schema using the league identifier as a suffix to the default table name
     * 
     * @Multitenant(TABLE_PER_TENANT)
     * @TenantTableDiscriminator(type=SUFFIX)
     */
    TABLE,

    /**
     * The data for this league is stored in its own table within a shared
     * schema using the league identifier as the schema name
     * 
     * @Multitenant(TABLE_PER_TENANT)
     * @TenantTableDiscriminator(type=SCHEMA)
     */
    SCHEMA,

    /**
     * The data for this league is stored in a dedicated database. It is the
     * responsibility of the deployer to ensure that a data source is specified
     * that is dedicated to this tenant.
     */
    DATABASE
}
