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
package eclipselink.example.mysports.admin.services;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.model.Style;

import java.io.InputStream;
import java.util.List;
import javax.ejb.Local;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@Local
public interface HostedLeagueRepository {

    List<HostedLeague> allLeagues();

    List<HostedLeague> allSharedLeagues();

    HostedLeague getLeague(String leagueId);

    String getORM(String leagueId);

    String getCSS(String leagueId);

    InputStream getLogo(String leagueId);

    HostedLeague merge(HostedLeague league);

    Style createStyle(String name, String css);

	void create(HostedLeague league);
}
