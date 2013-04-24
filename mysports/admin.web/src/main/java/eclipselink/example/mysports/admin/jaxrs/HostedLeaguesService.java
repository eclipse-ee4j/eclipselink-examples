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
package eclipselink.example.mysports.admin.jaxrs;

import java.io.InputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

/**
 * Session bean exposing JAX-RS methods for defining and usage of league
 * specific metadata.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Stateless
@LocalBean
@Path("league")
public class HostedLeaguesService {

    @EJB
    private HostedLeagueRepository repository;

    public HostedLeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(HostedLeagueRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<HostedLeague> allLeagues(@Context UriInfo context) {
        List<HostedLeague> allLeagues = getRepository().allSharedLeagues();

        for (HostedLeague hl : allLeagues) {
            hl.setUri(context.getAbsolutePath() + "/" + hl.getId());
        }
        return allLeagues;
    }

    @GET
    @Path("{league}")
    @Produces(MediaType.APPLICATION_XML)
    public HostedLeague getLeague(@Context UriInfo context, @PathParam("league") String leagueId) {
        System.err.println("HostedLeagueService::GET/"+leagueId);
        HostedLeague league = getRepository().getLeague(leagueId);
        if (league != null && context != null) {
            league.setUri(context.getAbsolutePath() + "/" + league.getId());
        }
        return league;

    }

    @GET
    @Path("{league}/orm")
    @Produces(MediaType.APPLICATION_XML)
    public String getORM(@PathParam("league") String leagueId) {
        System.err.println("HostedLeagueService::orm-GET/"+leagueId);
        return getRepository().getORM(leagueId);
    }

    @GET
    @Path("{league}.css")
    @Produces("text/css")
    public String getCSS(@PathParam("league") String leagueId) {
        System.err.println("HostedLeagueService::css-GET/"+leagueId);
        return getRepository().getCSS(leagueId);
    }

    @GET
    @Path("{league}.png")
    @Produces("image/jpeg")
    public InputStream getLogo(@PathParam("league") String leagueId) {
        System.err.println("HostedLeagueService::logo-GET/"+leagueId);
        return getRepository().getLogo(leagueId);
    }

}