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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * Simple utility class to extract and prioritize a set of keywords based on the
 * title provided.
 * 
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class KeywordExtractor {

    public String extractKeywords(String postTitle) {
        StringTokenizer tokenizer = new StringTokenizer(postTitle, ",.!?()[]'\" \t\n\r\f/");

        ArrayList<String> words = new ArrayList<String>();

        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.length() > 3 || token.toUpperCase().equals(token)) {
                words.add(token);
            }
        }

        // Sort words, longest one first
        Collections.sort(words, new StringLengthComparator());

        String keywords = null;
        if (words.size() > 1) {
            keywords = words.get(0) + "," + words.get(1);
        } else {
            keywords = words.get(0);
        }

        return keywords;
    }

    private class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (o1.length() > o2.length()) {
                return -1;
            } else if (o1.length() < o2.length()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}