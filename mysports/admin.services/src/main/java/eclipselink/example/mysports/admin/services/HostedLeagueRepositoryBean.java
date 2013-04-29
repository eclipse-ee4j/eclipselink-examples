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

import java.io.InputStream;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.Server;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.model.Style;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@Stateless
public class HostedLeagueRepositoryBean implements HostedLeagueRepository {

    @PersistenceContext(unitName = "MySportsAdmin")
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public List<HostedLeague> allLeagues() {
        return getEntityManager().createNamedQuery("HostedLeague.findAll", HostedLeague.class).getResultList();
    }

    public List<HostedLeague> allSharedLeagues() {
        return getEntityManager().createNamedQuery("HostedLeague.findAllShared", HostedLeague.class).getResultList();
    }

    public HostedLeague getLeague(String leagueId) {
        getEntityManager().unwrap(Server.class).getSessionLog().log(SessionLog.FINE, "HostedLeagueRepositoryBean.getLeague::" + leagueId);
        return getEntityManager().find(HostedLeague.class, leagueId);
    }

    public HostedLeague merge(HostedLeague league) {
    	HostedLeague existingLeague = getEntityManager().find(HostedLeague.class, league.getId());
    	
    	if (existingLeague == null) {
    		throw new IllegalStateException("Could not find league: " + league.getId());
    	}
        return getEntityManager().merge(league);
    }

    @Override
	public void create(HostedLeague league) {
    	getEntityManager().persist(league);
	}

	@Override
    public Style createStyle(String name, String css) {
        Style style = new Style(name, css);
        return getEntityManager().merge(style);
    }

    public String getORM(String leagueId) {
        return MappingsLoader.getORMapping(getEntityManager(), leagueId);
    }

    public String getCSS(String leagueId) {
        EntityManager em = getEntityManager();

        HostedLeague league = em.find(HostedLeague.class, leagueId);

        if (league == null) {
            throw new IllegalArgumentException("Unknown League: " + leagueId);
        }

        Style style = null;

        if (league != null && league.getColourScheme() != null) {
            style = em.find(Style.class, league.getColourScheme());
        }
        if (style == null) {
            style = em.find(Style.class, "default");
        }

        return style.getCss();
    }

    public InputStream getLogo(String leagueId) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(leagueId.toLowerCase() + ".png");
    }

}
