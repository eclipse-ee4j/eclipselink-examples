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

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

/**
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class HTMLWriter {

    private DynamicJAXBContext context;

    private File outputFile;

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

        outputFile = new File("output.html");
    }

    public void write(String topic, int imageLimit, Map<Object, DynamicEntity> redditMap, Map<Object, DynamicEntity> flickrMap) throws Exception {
        DynamicEntity html = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlPage");
        html.set("title", "EclipseLink MOXy - Dynamic JAXB");
        html.set("css", "style.css");
        html.set("rel", "stylesheet");
        html.set("type", "text/css");
        html.set("media", "screen");

        DynamicEntity body = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlBody");
        body.set("title", "Reddit /" + topic + " - Today's Top Posts");

        ArrayList<DynamicEntity> divs = new ArrayList<DynamicEntity>();

        for (Object postUrl : redditMap.keySet()) {
            DynamicEntity post = redditMap.get(postUrl);
            DynamicEntity flickrResults = flickrMap.get(postUrl);

            // Article Link
            // ================================================================

            DynamicEntity redditDiv = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlDiv");
            redditDiv.set("id", "redditPost");

            ArrayList<DynamicEntity> divContent = new ArrayList<DynamicEntity>();

            DynamicEntity redditLink = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlTextLink");
            redditLink.set("url", post.get("url"));
            redditLink.set("title", post.get("title"));
            divContent.add(redditLink);

            redditDiv.set("span", divContent);

            // Flickr description and images
            // ================================================================

            DynamicEntity flickrDiv = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlDiv");
            flickrDiv.set("id", "flickrResults");

            divContent = new ArrayList<DynamicEntity>();

            DynamicEntity flickrDescription = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlText");
            flickrDescription.set("text", flickrResults.get("description"));
            divContent.add(flickrDescription);

            ArrayList<DynamicEntity> flickrItems = flickrResults.get("items");

            int counter = 0;
            if (flickrItems != null) {
                Collections.shuffle(flickrItems, new Random(System.nanoTime()));
                for (DynamicEntity flickrItem : flickrItems) {
                    DynamicEntity flickrImageLink = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlImageLink");
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
                DynamicEntity noneText = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlText");
                noneText.set("text", "No results found.");
                divContent.add(noneText);
            }
            flickrDiv.set("span", divContent);

            divs.add(redditDiv);
            divs.add(flickrDiv);
        }

        body.set("divs", divs);

        html.set("body", body);

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(html, outputFile);
    }

    public void launchSystemBrowser() {
        try {
            Desktop.getDesktop().browse(outputFile.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================================================================

    private final String HTML_BINDINGS = "META-INF/bindings-html.xml";

    private final Integer HTML_IMAGE_HEIGHT = Integer.valueOf(80);

}