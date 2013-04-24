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
package eclipselink.example.mysports.admin.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import eclipselink.example.mysports.admin.model.HostedLeague;

/**
 * 
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@ManagedBean
@RequestScoped
public class CreateLeague {
    
    private HostedLeague league = new HostedLeague("", "", "default");
    
    @ManagedProperty("#{leagueRepository}")
    private LeagueRepository repository;

    @ManagedProperty("#{viewLeague}")
    private ViewLeague viewLeague;

    public LeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(LeagueRepository repository) {
        this.repository = repository;
    }

    public HostedLeague getLeague() {
        return league;
    }

    public String getId() {
        return getLeague().getId();
    }

    public void setId(String id) {
       getLeague().setId(id);
    }

    public String getName() {
        return getLeague().getName();
    }

    public void setName(String name) {
        getLeague().setName(name);
    }

    public ViewLeague getViewLeague() {
        return viewLeague;
    }

    public void setViewLeague(ViewLeague viewLeague) {
        this.viewLeague = viewLeague;
    }

    public String create() {
        getRepository().getRepository().merge(getLeague());
               
        return getViewLeague().view(getId());
    }
    
}
