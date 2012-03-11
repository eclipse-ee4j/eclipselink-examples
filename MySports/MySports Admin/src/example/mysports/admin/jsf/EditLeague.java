/*******************************************************************************
 * Copyright (c) 2010-2012 Oracle. All rights reserved.
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
package example.mysports.admin.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import example.mysports.admin.model.HostedLeague;

@ManagedBean
public class EditLeague {

    private HostedLeague league;

    public EditLeague() {
        this.league = new HostedLeague();
        this.league.setId("TEST");
        this.league.setName("Test League");
        this.league.setMultitenant(false);
        this.league.setDatasource("jdbc/TESTDS");
        this.league.addTableName("Player", "TEST_player");
        this.league.addTableName("Team", "TEST_team");
        this.league.addTableName("Division", "TEST_div");
    }

    public HostedLeague getLeague() {
        return league;
    }

    public void setLeague(HostedLeague league) {
        this.league = league;
    }

    public String create() {
        return "created";
    }

    public String cancel() {
        return "cancel";
    }
}
