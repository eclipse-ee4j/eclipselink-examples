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
package example.mysports.ejb;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import example.mysports.model.Division;
import example.mysports.model.Divisions;
import example.mysports.model.Player;
import example.mysports.model.Team;

/**
 * Session bean exposing JAX-RS methods to allow RESTful access to the MySports
 * multitenant model.
 * 
 * This is a simplified REST interface for this release, which will be enhanced
 * going forward.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Stateless
@Path("/")
public class RESTLeagueView {

    @Context
    private UriInfo context;

    @EJB
    private LeagueRepository repository;

    public LeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(LeagueRepository repository) {
        this.repository = repository;
    }

    public UriInfo getContext() {
        return context;
    }

    @GET
    @Path("{league}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StreamingOutput getDivsions(@PathParam("league") String leagueId, @Context HttpHeaders headers) {
        getRepository().setLeagueId(leagueId, null);

        List<Division> divs = getRepository().getDivisions();

        return new StreamingOutputMarshaller(getRepository().getJAXBContext(), new Divisions(divs), headers.getAcceptableMediaTypes());
    }

    @GET
    @Path("{league}/{division}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StreamingOutput getDivision(@PathParam("league") String leagueId, @PathParam("division") String division, @Context HttpHeaders headers) {
        getRepository().setLeagueId(leagueId, null);

        Division div = getRepository().getDivision(division);

        return new StreamingOutputMarshaller(getRepository().getJAXBContext(), div, headers.getAcceptableMediaTypes());
    }

    @GET
    @Path("{league}/{division}/{team}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StreamingOutput getTeam(@PathParam("league") String leagueId, @PathParam("division") String division, @PathParam("team") String teamId, @Context HttpHeaders headers) {
        getRepository().setLeagueId(leagueId, null);

        Team team = getRepository().getTeam(division, teamId);

        return new StreamingOutputMarshaller(getRepository().getJAXBContext(), team, headers.getAcceptableMediaTypes());
    }

    @GET
    @Path("{league}/{division}/{team}/{number}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StreamingOutput getPlayerByNumber(@PathParam("league") String leagueId, @PathParam("division") String division, @PathParam("team") String teamId, @PathParam("number") int number, @Context HttpHeaders headers) {
        getRepository().setLeagueId(leagueId, null);

        Player player = getRepository().getPlayerByNumber(division, teamId, number);

        return new StreamingOutputMarshaller(getRepository().getJAXBContext(), player, headers.getAcceptableMediaTypes());
    }

    /**
     * Simple {@link StreamingOutput} implementation that uses the provided
     * {@link JAXBContext} to marshal the result when requested.
     */
    static class StreamingOutputMarshaller implements StreamingOutput {
        private JAXBContext jaxbContext;
        private Object result;
        private MediaType mediaType;

        StreamingOutputMarshaller(JAXBContext jaxbContext, Object result, MediaType mediaType) {
            this.jaxbContext = jaxbContext;
            this.result = result;
            this.mediaType = mediaType;
        }
        

        public StreamingOutputMarshaller(JAXBContext jaxbContext, Object result, List<MediaType> acceptedTypes) {
            this(jaxbContext, result, mediaType(acceptedTypes));
        }


        public void write(OutputStream output) throws IOException, WebApplicationException {
            if (this.jaxbContext != null && this.result != null) {
                try {
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty("eclipselink.media-type", this.mediaType.toString());
                    marshaller.marshal(this.result, output);
                } catch (JAXBException e) {
                    throw new RuntimeException("JAXB Failure to marshal: " + this.result, e);
                }
            }
        }

        /**
         * Identify the preferred {@link MediaType} from the list provided. This
         * will check for JSON string or {@link MediaType} first then XML.
         * 
         * @param types
         *            List of {@link String} or {@link MediaType} values;
         * @return selected {@link MediaType}
         * @throws WebApplicationException
         *             with Status.UNSUPPORTED_MEDIA_TYPE if neither the JSON or
         *             XML values are found.
         */
        private static MediaType mediaType(List<?> types) {
            if (contains(types, MediaType.APPLICATION_JSON_TYPE)) {
                return MediaType.APPLICATION_JSON_TYPE;
            }
            if (contains(types, MediaType.APPLICATION_XML_TYPE)) {
                return MediaType.APPLICATION_XML_TYPE;
            }
            return MediaType.APPLICATION_XML_TYPE;
        }

        private static boolean contains(List<?> types, MediaType type) {
            for (Object mt : types) {
                if (mt instanceof String) {
                    if (((String) mt).contains(type.toString())) {
                        return true;
                    }
                } else if (((MediaType) mt).equals(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}