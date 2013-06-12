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

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

/**
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class FlikrReader {

    private DynamicJAXBContext context;

    public FlikrReader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream flickrBindings = loader.getResourceAsStream(FLICKR_BINDINGS);

        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(3);
        dataBindings.add(flickrBindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);
        try {
            context = DynamicJAXBContextFactory.createContextFromOXM(loader, properties);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Map<String, DynamicEntity> readFlickrResults(Map<String, DynamicEntity> redditMap) {
        Map<String, DynamicEntity> results = new HashMap<String, DynamicEntity>();

        for (String postUrl : redditMap.keySet()) {
            DynamicEntity post = redditMap.get(postUrl);
            DynamicEntity flickrResults = readFlickrResult(postUrl, post);


            results.put(postUrl, flickrResults);

            ArrayList<DynamicEntity> flickerItems = flickrResults.get("items");
            if (flickerItems != null) {
                int size = flickerItems.size();
                if (size >= IMAGE_LIMIT) {
                    size = IMAGE_LIMIT;
                }
                for (int i = 0; i < size; i++) {
                    System.out.println("\t" + flickerItems.get(i).get("imageUrl"));
                }
            } else {
                System.out.println("\tNo results found.");
            }
        }

        return results;

    }

    public DynamicEntity readFlickrResult(String postUrl, DynamicEntity post) {

        System.out.println("\nHeadline: [" + post.get("title") + "]");

        String keywords = new KeywordExtractor().extractKeywords(post.get("title").toString());

        String flickrUrlString = FLICKR_URL + keywords;
        System.out.print("Searching Flickr: [" + flickrUrlString + "]... ");

        InputStream flickrStream = null;

        try {
            flickrStream = new URL(flickrUrlString).openConnection().getInputStream();

            System.out.println("Done.");

            Unmarshaller u = context.createUnmarshaller();
            return (DynamicEntity) u.unmarshal(flickrStream);
        } catch (IOException | JAXBException e) {
            throw new RuntimeException("FLIKR access failed", e);
        } finally {
            if (flickrStream != null) {
                try {
                    flickrStream.close();
                } catch (IOException e) {// ignore in this example }
                }
            }
        }

    }

    // ========================================================================

    // See http://www.flickr.com/services/feeds/
    private final int IMAGE_LIMIT = 6;
    private final String FLICKR_URL = "http://api.flickr.com/services/feeds/photos_public.gne?safe_search=1&tags=";
    private final String FLICKR_BINDINGS = "META-INF/bindings-flickr.json";

}