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
package eclipselink.example.mysports.application.admin;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to access the admin server. This abstraction is provided so the
 * runtime can use the default {@link RESTAdminServerConnector} while testing
 * outside the container can provide a local version.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public interface AdminServerConnector {
    
    /**
     * Return the list of leagues (tenants) available.
     */
    HostedLeagues getLeagues();

    /**
     * Retrieve the league (name and version) from the admin server.
     */
    League getLeague(String leagueId);

    String getOrmURL(String leagueId);
    
    InputStream getCss(String leagueId) throws IOException;

    InputStream getLogo(String leagueId) throws IOException;
    
}