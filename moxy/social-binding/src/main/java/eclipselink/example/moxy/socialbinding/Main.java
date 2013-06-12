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
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicEntity;

/**
 * Main example execution.
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class Main {

    public static void main(String[] args) {
        File file = create("technology", "target/classes/output.html", 5);

        launchSystemBrowser(file);
    }

    public static File create(String topic, String targetFile, int maxResults) {
        Map<String, DynamicEntity> redditResults = new RedditReader().readRedditPosts(topic);
        Map<String, DynamicEntity> flickrResults = new FlikrReader().readFlickrResults(redditResults);

        HTMLWriter writer = new HTMLWriter();
        return writer.write(topic, targetFile, maxResults, redditResults, flickrResults);
    }

    public static void launchSystemBrowser(File file) {
        try {
            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}