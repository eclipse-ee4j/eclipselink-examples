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
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package example.mysports.admin.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import example.mysports.admin.model.HostedLeague;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@ManagedBean
public class LeagueAdminRepository {

    private List<HostedLeague> leagues;

    public LeagueAdminRepository() {
    }

    public List<HostedLeague> getLeagues() {
        if (this.leagues == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysports-admin");
            EntityManager em = emf.createEntityManager();

            try {
                this.leagues = em.createNamedQuery("HostedLeague.findAllVisible", HostedLeague.class).getResultList();
            } finally {
                em.close();
                emf.close();
            }
        }
        return this.leagues;
    }

}
