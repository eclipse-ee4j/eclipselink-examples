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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

/**
 * HTML writer using dynamic MOXy to marshall into HTML.
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class HTMLWriter {

    private DynamicJAXBContext context;

    /**
     * Create the {@link JAXBContext} that MOXy will use for HTML writing
     * (Marshalling)
     */
    public HTMLWriter() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream htmlBindings = loader.getResourceAsStream(HTML_BINDINGS);

        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(3);
        dataBindings.add(htmlBindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);
        try {
            context = DynamicJAXBContextFactory.createContextFromOXM(loader, properties);
        } catch (JAXBException e) {
            throw new RuntimeException("Context creation failed", e);
        }
    }

    public File write(String topic, String fileLocation, int imageLimit, Map<String, DynamicEntity> redditMap, Map<String, DynamicEntity> flickrMap) {
        DynamicEntity html = context.newDynamicEntity("HtmlPage");
        html.set("title", "EclipseLink Social Binding Example");
        html.set("css", "style.css");
        html.set("rel", "stylesheet");
        html.set("type", "text/css");
        html.set("media", "screen");

        DynamicEntity body = context.newDynamicEntity("HtmlBody");
        body.set("title", "Reddit /" + topic + " - Today's Top Posts");

        ArrayList<DynamicEntity> divs = new ArrayList<DynamicEntity>();

        for (Object postUrl : redditMap.keySet()) {
            DynamicEntity post = redditMap.get(postUrl);
            DynamicEntity flickrResults = flickrMap.get(postUrl);

            // Article Link
            // ================================================================

            DynamicEntity redditDiv = context.newDynamicEntity("HtmlDiv");
            redditDiv.set("id", "redditPost");

            ArrayList<DynamicEntity> divContent = new ArrayList<DynamicEntity>();

            DynamicEntity redditLink = context.newDynamicEntity("HtmlTextLink");
            redditLink.set("url", post.get("url"));
            redditLink.set("title", post.get("title"));
            divContent.add(redditLink);

            redditDiv.set("span", divContent);

            // Flickr description and images
            // ================================================================

            DynamicEntity flickrDiv = context.newDynamicEntity("HtmlDiv");
            flickrDiv.set("id", "flickrResults");

            divContent = new ArrayList<DynamicEntity>();

            DynamicEntity flickrDescription = context.newDynamicEntity("HtmlText");
            flickrDescription.set("text", flickrResults.get("description"));
            divContent.add(flickrDescription);

            ArrayList<DynamicEntity> flickrItems = flickrResults.get("items");

            int counter = 0;
            if (flickrItems != null) {
                Collections.shuffle(flickrItems, new Random(System.nanoTime()));
                for (DynamicEntity flickrItem : flickrItems) {
                    DynamicEntity flickrImageLink = context.newDynamicEntity("HtmlImageLink");
                    flickrImageLink.set("url", flickrItem.get("flickrPage"));
                    flickrImageLink.set("image", flickrItem.get("imageUrl"));
                    flickrImageLink.set("height", HTML_IMAGE_HEIGHT);
                    divContent.add(flickrImageLink);
                    counter++;
                    if (counter == imageLimit) {
                        break;
                    }
                }
            } else {
                DynamicEntity noneText = context.newDynamicEntity("HtmlText");
                noneText.set("text", "No results found.");
                divContent.add(noneText);
            }
            flickrDiv.set("span", divContent);

            divs.add(redditDiv);
            divs.add(flickrDiv);
        }

        body.set("divs", divs);

        html.set("body", body);

        Marshaller m;
        try {
            m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            File outputFile = new File(fileLocation);
            m.marshal(html, outputFile);
            return outputFile;
        } catch (JAXBException e) {
            throw new RuntimeException("HTML marshall failed", e);
        }
    }

    // ========================================================================

    private final String HTML_BINDINGS = "META-INF/bindings-html.xml";
    private final Integer HTML_IMAGE_HEIGHT = 80;
}