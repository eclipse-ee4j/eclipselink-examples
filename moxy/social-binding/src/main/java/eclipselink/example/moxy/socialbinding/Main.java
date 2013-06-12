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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicEntity;

import eclipselink.example.moxy.socialbinding.util.KeywordExtractor;

/**
 * Main example execution.
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class Main {

    private static Logger logger = Logger.getLogger("eclipselink.example.moxy.socialbinding");

    public static void main(String[] args) throws JAXBException {
        String topic = "technology";
        
        if (args.length == 1) {
            topic = args[0];
        }
        
        File file = create(topic, "target/classes/output.html", 5, 5);

        launchSystemBrowser(file);
    }

    public static File create(String topic, String targetFile, int maxResults, int imageLimit) throws JAXBException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Map<String, DynamicEntity> redditResults = new RedditReader(cl).readRedditPosts(topic);
        Map<String, DynamicEntity> flickrResults = new HashMap<String, DynamicEntity>();
        FlickrReader flickrReader = new FlickrReader(cl);

        for (String postUrl : redditResults.keySet()) {
            DynamicEntity post = redditResults.get(postUrl);

            logger.log(Level.INFO, "Headline: [" + post.get("title") + "]");

            String keywords = KeywordExtractor.extractKeywords(post.get("title").toString());
            DynamicEntity flickrResult = flickrReader.readFlickrResult(keywords);

            flickrResults.put(postUrl, flickrResult);

            ArrayList<DynamicEntity> flickerItems = flickrResult.get("items");
            if (flickerItems != null) {
                for (int i = 0; i < flickerItems.size() && i <= imageLimit; i++) {
                    logger.log(Level.INFO, flickerItems.get(i).get("imageUrl").toString());
                }
            } else {
                logger.log(Level.WARNING, "No results found.");
            }

        }

        return new HTMLWriter().write(topic, targetFile, maxResults, redditResults, flickrResults);
    }

    public static void launchSystemBrowser(File file) {
        try {
            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}