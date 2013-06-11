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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.RankedConceptsCall;
import com.likethecolor.alchemy.api.call.type.CallTypeText;
import com.likethecolor.alchemy.api.entity.ConceptAlchemyEntity;
import com.likethecolor.alchemy.api.entity.Response;
import com.likethecolor.alchemy.api.params.ConceptParams;

public class AlchemyAccessor {

    /**
     * This method uses the AlchemyAPI web service to determine the "concepts"
     * contained in the headline. It does not use EclipseLink and is used solely
     * for the purpose of this example application.
     * 
     * @see <a href="http://www.alchemyapi.com">AlchemyAPI</a>
     */
    @SuppressWarnings("unchecked")
    public String extractConcepts(String postTitle) {
        try {
            Client client = new Client(ALCHEMY_KEY);

            ConceptParams params = new ConceptParams();
            params.setMaxRetrieve(ALCHEMY_CONCEPTS_TO_RETURN);

            CallTypeText callType = new CallTypeText(postTitle);
            RankedConceptsCall call = new RankedConceptsCall(callType, params);
            Response<ConceptAlchemyEntity> response = client.call(call);

            String concepts = "";

            Iterator<ConceptAlchemyEntity> it = response.iterator();
            if (!it.hasNext()) {
                // If Alchemy didn't find any concepts, use string length
                // algorithm
                return extractKeywords(postTitle);
            } else {
                while (it.hasNext()) {
                    ConceptAlchemyEntity alchemyEntity = it.next();
                    concepts += alchemyEntity.getConcept();
                    if (it.hasNext()) {
                        concepts += ",";
                    }
                }
            }

            concepts = concepts.replace(" ", "+");

            return concepts;
        } catch (Exception e) {
            return extractKeywords(postTitle);
        }
    }

    private String extractKeywords(String postTitle) {
        StringTokenizer tokenizer = new StringTokenizer(postTitle, ",.!?()[]'\"- \t\n\r\f");

        ArrayList<String> words = new ArrayList<String>();

        while (tokenizer.hasMoreElements()) {
            words.add(tokenizer.nextToken());
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

    private final int ALCHEMY_CONCEPTS_TO_RETURN = 2;
    private final String ALCHEMY_KEY = "/META-INF/a.key";

}