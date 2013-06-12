/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package eclipselink.example.moxy.socialbinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

/**
 * Example of how EclipseLink MOXy can be used
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class FlickrReader {

    private static Logger logger = Logger.getLogger("eclipselink.example.moxy.socialbinding");
    
    private DynamicJAXBContext context;

    /**
     * Initialize the MOXy context that will be used to unmarshal the Flickr results.
     */
    public FlickrReader(ClassLoader cl) throws JAXBException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream flickrBindings = loader.getResourceAsStream(FLICKR_BINDINGS);

        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(3);
        dataBindings.add(flickrBindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);

        context = DynamicJAXBContextFactory.createContextFromOXM(loader, properties);
    }

    public DynamicEntity readFlickrResult(String keywords) {
        String flickrUrlString = FLICKR_URL + keywords;
        logger.log(Level.INFO, "Searching Flickr: [" + flickrUrlString + "]... ");

        InputStream flickrStream = null;

        try {
            Unmarshaller u = context.createUnmarshaller();

            flickrStream = new URL(flickrUrlString).openConnection().getInputStream();

            return (DynamicEntity) u.unmarshal(flickrStream);
        } catch (IOException | JAXBException e) {
            throw new RuntimeException("FLICKR access failed", e);
        } finally {
            if (flickrStream != null) {
                try {
                    flickrStream.close();
                } catch (IOException e) {// ignore in this example }
                }
            }
        }

    }

    // See http://www.flickr.com/services/feeds/
    private final String FLICKR_URL = "http://api.flickr.com/services/feeds/photos_public.gne?safe_search=1&tags=";
    private final String FLICKR_BINDINGS = "META-INF/bindings-flickr.json";

}