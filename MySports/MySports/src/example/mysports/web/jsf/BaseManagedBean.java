/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
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
package example.mysports.web.jsf;

import javax.faces.bean.ManagedProperty;

import example.mysports.MySportsConfig;
import example.mysports.ejb.LeagueRepository;

/**
 * Common managed bean to provide access required across the application.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public abstract class BaseManagedBean {

    @ManagedProperty(value = "#{leagueRepositoryBean}")
    private LeagueRepositoryBean repositoryBean;

    public LeagueRepositoryBean getRepositoryBean() {
        return repositoryBean;
    }

    public void setRepositoryBean(LeagueRepositoryBean repositoryBean) {
        this.repositoryBean = repositoryBean;
    }

    public LeagueRepository getRepository() {
        return getRepositoryBean().getRepository();
    }

    public MySportsConfig getMySportsConfig() {
        return getRepository().getConfig();
    }

    public String getLeagueId() {
        return getRepository().getLeagueId();
    }

    public boolean isMultitenant() {
        return getMySportsConfig().isMultitenant();
    }
}
