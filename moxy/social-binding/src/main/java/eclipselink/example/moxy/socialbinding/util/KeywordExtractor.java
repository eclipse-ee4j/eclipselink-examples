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
package eclipselink.example.moxy.socialbinding.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    /**
     * Return the longest word in the title (preferably a capitalized word).
     */
    public static String extractKeywords(String postTitle) {
        ArrayList<String> allWords = new WordList();
        ArrayList<String> upperCaseWords = new WordList();        

        ArrayList<String> excludeWords = buildExlucdeWordsList();

        StringTokenizer tokenizer = new StringTokenizer(postTitle, ",.!?():;-[]'\" \t\n\r\f/");
        while (tokenizer.hasMoreElements()) {
            String word = tokenizer.nextToken();
            if (!excludeWords.contains(word)) {
                allWords.add(word);
                if (Character.isUpperCase(word.toCharArray()[0])) {
                    upperCaseWords.add(word);
                }
            }
        }
        
        StringLengthComparator comparator = new StringLengthComparator();
        Collections.sort(allWords, comparator);
        Collections.sort(upperCaseWords, comparator);

        if (upperCaseWords.size() > 1) {
            return upperCaseWords.get(0);
        } else {
            return allWords.get(0);
        }
   }
    
    private static ArrayList<String> buildExlucdeWordsList() {
        ArrayList<String> excludeWords = new WordList();

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream("META-INF/exclude-words.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                excludeWords.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return excludeWords;
    }

    private static class StringLengthComparator implements Comparator<String> {
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
    
    private static class WordList extends ArrayList<String> {
        private static final long serialVersionUID = 4780991427891054829L;

        @Override
        public boolean contains(Object o) {
            String s = (String) o;
            for (String string : this) {
                if (s.equalsIgnoreCase(string)) return true;
            }
            return false;
        }
    }
    
}