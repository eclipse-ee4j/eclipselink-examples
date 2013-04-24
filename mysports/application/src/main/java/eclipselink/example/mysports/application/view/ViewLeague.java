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

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import eclipselink.example.mysports.application.model.Division;
import eclipselink.example.mysports.application.services.LeagueRepository;

/**
 * JSF managed bean used to view a league's divisions.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@RequestScoped
public class ViewLeague extends BaseManagedBean {

    protected static final String PAGE = "view-league";

    public LeagueRepository getRepository() {
        return getRepositoryBean().getRepository();
    }

    public String getLeagueId() {
        return getRepository().getLeagueId();
    }

    public List<Division> getDivisions() {
        return getRepository().getDivisions();
    }

    public Division getCurrentDivision() {
        return getRepository().getCurrentDivision();
    }
    
    public String getLeagueName() {
        return getRepository().getName();
    }
    
    public String view() {
        return PAGE;
    }

}
