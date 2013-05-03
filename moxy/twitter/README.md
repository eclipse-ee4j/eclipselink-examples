<h1>EclipseLink MOXy Twitter Example</h1>

<p>This example application demonstrates how to read JSON data into objects mapped with EclipseLink JAXB.</p>

<ul>
    <li>Using standard JAXB APIs, <tt>Result</tt> and <tt>SearchResult</tt> classes are mapped to a subset of the JSON data returned from a Twitter query
    <li>Query is executed by using the Twitter Search URL (e.g. <tt><a href="http://search.twitter.com/search.json?q=jaxb">http://search.twitter.com/search.json?q=jaxb</a></tt>)
    <li>Incoming JSON is unmarshalled to a <tt>SearchResult</tt> object
    <li>An example <tt>Result</tt> is added to the query results, which are then marshalled to <tt>System.out</tt>
</ul>

<tt><a href="http://wiki.eclipse.org/EclipseLink/Examples/MOXy/JSON_Twitter">http://wiki.eclipse.org/EclipseLink/Examples/MOXy/JSON_Twitter</a></tt>
