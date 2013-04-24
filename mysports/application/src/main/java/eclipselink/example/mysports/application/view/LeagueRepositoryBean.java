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
package eclipselink.example.mysports.application.view;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import eclipselink.example.mysports.application.services.LeagueRepository;

/**
 * JSF session scoped managed bean which looks after access to the stateful
 * {@link LeagueRepository} EJB.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@SessionScoped
public class LeagueRepositoryBean {

    @EJB
    private LeagueRepository repository;

    public LeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(LeagueRepository repository) {
        this.repository = repository;
    }

    public String setLeague() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest myRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String value = myRequest.getParameter("league");

        getRepository().setLeagueId(value, null);
        if (!getRepository().hasLeague()) {
            return "index?faces-redirect=true";
        }

        return "view-league?faces-redirect=true";
    }

    public String getLeagueId() {
        return getRepository().getLeagueId();
    }
    
    public String getLeagueName() {
        return getRepository().getName();
    }

    public boolean hasLeague() {
        return getRepository().hasLeague();
    }

}
