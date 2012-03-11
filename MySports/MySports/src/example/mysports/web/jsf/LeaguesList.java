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

import javax.faces.bean.ManagedBean;

import example.mysports.admin.Leagues;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
public class LeaguesList extends BaseManagedBean {

    private Leagues leagues;

    public Leagues getLeagues() {
        if (this.leagues == null) {
            this.leagues = getRepositoryBean().getRepository().getConfig().getAdminConnector().getLeagues();
        }
        return this.leagues;
    }
}
