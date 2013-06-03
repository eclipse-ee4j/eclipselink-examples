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

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;

import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.RankedConceptsCall;
import com.likethecolor.alchemy.api.call.type.CallTypeText;
import com.likethecolor.alchemy.api.entity.ConceptAlchemyEntity;
import com.likethecolor.alchemy.api.entity.Response;
import com.likethecolor.alchemy.api.params.ConceptParams;

public class Main {

    // The JAXBContext that we will use to read Reddit / Flickr data, and write HTML
    private DynamicJAXBContext context;

    // RedditPost:url -> RedditPost
    HashMap<Object, DynamicEntity> redditMap;
    // RedditPost:url -> FlickrResults
    HashMap<Object, DynamicEntity> flickrMap;

    File outputFile;

    // ========================================================================

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try {
            init();

            readRedditPosts();
            findFlickrResults();
            writeHtml();
            launchSystemBrowser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream redditBindings = loader.getResourceAsStream(REDDIT_BINDINGS);
        InputStream flickrBindings = loader.getResourceAsStream(FLICKR_BINDINGS);
        InputStream htmlBindings = loader.getResourceAsStream(HTML_BINDINGS);

        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(3);
        dataBindings.add(redditBindings);
        dataBindings.add(flickrBindings);
        dataBindings.add(htmlBindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);
        context = DynamicJAXBContextFactory.createContextFromOXM(loader, properties);

        redditMap = new HashMap<Object, DynamicEntity>();
        flickrMap = new HashMap<Object, DynamicEntity>();

        outputFile = new File("output.html");
    }

    private void readRedditPosts() throws Exception {
        Unmarshaller u = context.createUnmarshaller();
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);

        Class<? extends DynamicEntity> redditResultsClass = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.RedditResults").getClass();

        System.out.println();
        System.out.print("Reading Today's Hot Topics from Reddit r/" + SUBREDDIT + "... ");
        DynamicEntity redditResults = u.unmarshal(new StreamSource(REDDIT_URL), redditResultsClass).getValue();
        System.out.println("Done.");

        ArrayList<DynamicEntity> posts = redditResults.get("posts");
        for (DynamicEntity post : posts) {
            redditMap.put(post.get("url"), post);
        }
    }

    private void findFlickrResults() throws Exception {
        for (Object postUrl : redditMap.keySet()) {
            DynamicEntity post = redditMap.get(postUrl);

            System.out.println();
            System.out.println("Headline: [" + post.get("title") + "]");

            String keywords = extractConcepts(post.get("title").toString());

            String flickrUrlString = FLICKR_URL + keywords;
            System.out.print("Searching Flickr: [" + flickrUrlString + "]... ");
            InputStream flickrStream = new URL(flickrUrlString).openConnection().getInputStream();
            System.out.println("Done.");

            Unmarshaller u = context.createUnmarshaller();
            DynamicEntity flickrResults = (DynamicEntity) u.unmarshal(flickrStream);

            flickrMap.put(postUrl, flickrResults);

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
    }

    private void writeHtml() throws Exception {
        DynamicEntity html = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlPage");
        html.set("title", "EclipseLink MOXy - Dynamic JAXB");
        html.set("css", "style.css");
        html.set("rel", "stylesheet");
        html.set("type", "text/css");
        html.set("media", "screen");

        DynamicEntity body = context.newDynamicEntity("eclipselink.example.moxy.dynamic.flickr.HtmlBody");
        body.set("title", "Reddit /" + SUBREDDIT + " - Today's Top Posts");

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
                    if (counter == IMAGE_LIMIT) {
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

    // ========================================================================

    /**
     * This method uses the AlchemyAPI web service to determine the "concepts"
     * contained in the headline.  It does not use EclipseLink and is used solely for
     * the purpose of this example application.
     *
     * @see <a href="http://www.alchemyapi.com">AlchemyAPI</a>
     */
    @SuppressWarnings("unchecked")
    private String extractConcepts(String postTitle) {
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
                // If Alchemy didn't find any concepts, use string length algorithm
                return extractKeywords(postTitle);
            } else {
                while (it.hasNext()) {
                    ConceptAlchemyEntity alchemyEntity = it.next();
                    concepts +=  alchemyEntity.getConcept();
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

    private void launchSystemBrowser() {
        try {
            Desktop.getDesktop().browse(outputFile.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeJsonBindings() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream bindingsXml = loader.getResourceAsStream("META-INF/bindings-flickr.xml");
        JAXBContext ctx = JAXBContext.newInstance("org.eclipse.persistence.jaxb.xmlmodel");
        Object bindings = ctx.createUnmarshaller().unmarshal(bindingsXml);

        Marshaller m = ctx.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        m.marshal(bindings, System.out);
    }

    // ========================================================================

    public class StringLengthComparator implements Comparator<String> {
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

    // ========================================================================

    // See http://www.reddit.com/dev/api
    private final int REDDIT_LIMIT = 5;
    private final String SUBREDDIT = "technology";
    private final String REDDIT_URL = "http://www.reddit.com/r/" + SUBREDDIT + "/top/.json?sort=top&t=today&limit=" + REDDIT_LIMIT;

    // See http://www.flickr.com/services/feeds/
    private final int IMAGE_LIMIT = 6;
    private final String FLICKR_URL = "http://api.flickr.com/services/feeds/photos_public.gne?safe_search=1&tags=";

    private final String REDDIT_BINDINGS = "META-INF/bindings-reddit.xml";
    private final String FLICKR_BINDINGS = "META-INF/bindings-flickr.json";
    private final String HTML_BINDINGS = "META-INF/bindings-html.xml";

    private final int ALCHEMY_CONCEPTS_TO_RETURN = 2;
    private final String ALCHEMY_KEY = "/META-INF/a.key";

    private final Integer HTML_IMAGE_HEIGHT = Integer.valueOf(80);

}