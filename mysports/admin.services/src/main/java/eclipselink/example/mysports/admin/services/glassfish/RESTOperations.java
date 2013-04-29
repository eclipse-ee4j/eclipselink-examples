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
package eclipselink.example.mysports.admin.services.glassfish;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.oxm.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Helper to invoke management REST operations against a GlassFish instance
 * using the Jersey client library.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class RESTOperations {

    private String server;

    private int port = 4848;

    public RESTOperations(String server) {
        super();
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    private static final String ADMINISTRATION_URL = "/management";

    protected WebResource getWebResource(Client client, String resourcePath) {
        return client.resource("http://" + getServer() + ":" + getPort() + ADMINISTRATION_URL + resourcePath);
    }

    public <T> T get(String resourcePath, MediaType mediaType, Class<T> resultClass) throws JAXBException {
        Client client = Client.create();
        WebResource webResource = getWebResource(client, resourcePath);
        ClientResponse response = webResource.accept(mediaType.getMediaType()).get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        Unmarshaller unmarshaller = MOXyContextHelper.createUnmarshaller(mediaType);
        return unmarshaller.unmarshal(new StreamSource(response.getEntityInputStream()), resultClass).getValue();
    }

}
