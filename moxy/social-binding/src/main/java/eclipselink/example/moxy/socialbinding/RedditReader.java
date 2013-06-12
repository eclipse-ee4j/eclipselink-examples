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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;

/**
 * Example of using EclipseLink MOXy to read (unmarshall) the JSON returned from
 * Reddit (http://www.reddit.com/). It uses the available interface that returns
 * JSON based on a provided topic.
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class RedditReader {

    private DynamicJAXBContext context;

    public RedditReader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream redditBindings = loader.getResourceAsStream(REDDIT_BINDINGS);

        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(3);
        dataBindings.add(redditBindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);
        try {
            context = DynamicJAXBContextFactory.createContextFromOXM(loader, properties);
        } catch (JAXBException e) {
            throw new RuntimeException("Context creation failed", e);
        }
    }

    public Map<String, DynamicEntity> readRedditPosts(String topic) {
        DynamicEntity redditResults = null;

        try {
            Unmarshaller u = context.createUnmarshaller();
            u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);

            DynamicType resultType = this.context.getDynamicType("RedditResults");
            Class<? extends DynamicEntity> redditResultsClass = resultType.getJavaClass();

            System.out.print("\nReading Today's Hot Topics from Reddit r/" + topic + "... ");

            redditResults = u.unmarshal(new StreamSource(getRedditURL(topic)), redditResultsClass).getValue();
        } catch (JAXBException e) {
            throw new RuntimeException("Unmarshall from REDDIT failed", e);
        }

        System.out.println("Done.");

        List<DynamicEntity> posts = redditResults.get("posts");
        Map<String, DynamicEntity> results = new HashMap<String, DynamicEntity>();

        for (DynamicEntity post : posts) {
            results.put(post.get("url").toString(), post);
        }

        return results;
    }

    private String getRedditURL(String topic) {
        return "http://www.reddit.com/r/" + topic + "/top/.json?sort=top&t=today&limit=" + REDDIT_LIMIT;
    }

    // See http://www.reddit.com/dev/api
    private final int REDDIT_LIMIT = 5;

    private final String REDDIT_BINDINGS = "META-INF/bindings-reddit.json";
}