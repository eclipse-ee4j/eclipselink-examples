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
package eclipselink.example.mysports.application.admin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.oxm.MediaType;

/**
 * Utility class that provides access to the admin server using JAX-RS calls.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class RESTAdminServerConnector implements AdminServerConnector {

    /**
     * Default Root URI for the Admin application. Assumed to be hosted on the same container.
     */
    public static final String DEFAULT_ROOT_URI = "http://localhost:7001/mysportsadmin/rest/league";
    
    private String rootURI = DEFAULT_ROOT_URI;
    
    String getRootURI() {
        return rootURI;
    }

    void setRootURI(String rootURI) {
        this.rootURI = rootURI;
    }

    public HostedLeagues getLeagues() {
        InputStream in = null;

        try {
            in = open(null, MediaType.APPLICATION_XML.getMediaType(), null);
            return (HostedLeagues) MoxyContextHelper.createUnmarshaller(MediaType.APPLICATION_XML).unmarshal(in);
        } catch (IOException e) {
            throw new RuntimeException("Failure to retieve Leagues", e);
        } catch (JAXBException e) {
            throw new RuntimeException("Failure to unmarshal Leagues", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public League getLeague(String leagueId) {
        InputStream in = null;

        try {
            in = open(leagueId, MediaType.APPLICATION_XML.getMediaType(), null);
            return (League) MoxyContextHelper.createUnmarshaller(org.eclipse.persistence.oxm.MediaType.APPLICATION_XML).unmarshal(in);
        } catch (IOException e) {
            throw new RuntimeException("Failure to retieve League", e);
        } catch (JAXBException e) {
            throw new RuntimeException("Failure to unmarshal League", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Construct a URL to access the admin server for a specified league and
     * resource result type.
     * 
     * @param leagueId
     *            represents an expected league or null if requesting all
     *            leagues.
     * @param result
     *            result resource type being requested. If null with a valid
     *            league identifier then the league is being accessed. If
     *            leagueId is null this should be null as well.
     */
    private String buildURL(String leagueId, String result) {
        String urlString = getRootURI();

        if (leagueId != null) {
            urlString = urlString + "/" + leagueId;
        }
        if (result != null) {
            urlString = urlString + "/" + result.toLowerCase();
        }

        return urlString;
    }

    private InputStream open(String leagueId, String mediaType, String result) throws IOException {
        URL url = new URL(buildURL(leagueId, result));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", mediaType);

        return connection.getInputStream();
    }

    public InputStream getCss(String leagueId) throws IOException {
        return open(leagueId, "text/css", "css");
    }

    public String getOrmURL(String leagueId) {
        return buildURL(leagueId, "orm");
    }

    public InputStream getLogo(String leagueId) throws IOException {
        return open(leagueId, "?", "logo");
    }

}
