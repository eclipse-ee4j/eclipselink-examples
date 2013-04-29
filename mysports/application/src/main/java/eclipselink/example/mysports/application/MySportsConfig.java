/**
 * *****************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 *****************************************************************************
 */
package eclipselink.example.mysports.application;

import org.eclipse.persistence.annotations.Multitenant;

import eclipselink.example.mysports.application.services.LeagueRepository;

/**
 * Responsible for managing application context configuration. For now this is
 * hard coded for local deployment but will be made more flexible in the future.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MySportsConfig {

    /**
     * Context property used to specify the tenant configuration within the
     * persistence unit. A value for this property must be available within the
     * persistence context to access the {@link Multitenant} types.
     */
    public static final String LEAGUE_CONTEXT = "league";

    /**
     * Template persistence unit name. This is the base PU which is used as a
     * template for each tenant's persistence unit.
     * 
     * @see LeagueRepository#setLeagueId(String, java.util.Map) for details of
     *      how the PUs are created from template.
     */
    public static final String PU_NAME = "mysports";

}
