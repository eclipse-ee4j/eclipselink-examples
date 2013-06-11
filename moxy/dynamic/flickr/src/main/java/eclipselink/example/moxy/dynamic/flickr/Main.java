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
package eclipselink.example.moxy.dynamic.flickr;

import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicEntity;

public class Main {

    public static void main(String[] args) {
        String topic = "software";
        
        try {
            Map<Object, DynamicEntity> redditResults = new RedditReader().readRedditPosts(topic);
            Map<Object, DynamicEntity> flickrResults = new FlikrReader().readFlickrResults(redditResults);

            HTMLWriter writer = new HTMLWriter();
            writer.write(topic, 6, redditResults, flickrResults);
            writer.launchSystemBrowser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}