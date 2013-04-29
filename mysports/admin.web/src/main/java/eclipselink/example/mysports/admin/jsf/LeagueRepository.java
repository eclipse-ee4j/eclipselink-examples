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

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

@ManagedBean
@SessionScoped
public class LeagueRepository {

    @EJB
    private HostedLeagueRepository repository;

    public HostedLeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(HostedLeagueRepository repository) {
        this.repository = repository;
    }

    public List<HostedLeague> getLeagues() {
        return getRepository().allLeagues();
    }

    public List<HostedLeague> getSharedLeagues() {
        return getRepository().allSharedLeagues();
    }

    public HostedLeague getLeague(String id) {
        return getRepository().getLeague(id);
    }

}
