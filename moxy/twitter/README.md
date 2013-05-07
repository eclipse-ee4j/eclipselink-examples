EclipseLink MOXy Twitter Example
--------------------------------

This example application demonstrates how to read JSON data into objects mapped with EclipseLink JAXB.

* Using standard JAXB APIs, `Result` and `SearchResults` classes are mapped to a subset of the JSON data returned from a Twitter query
* Query is executed by using a Twitter Search URL (e.g. [http://search.twitter.com/search.json?q=jaxb](http://search.twitter.com/search.json?q=jaxb))
* Incoming JSON is unmarshalled to a `SearchResult` object
* An new `Result` is manually created and added to the query results, which are then marshalled to `System.out`
* This example also uses a `DateAdapter` to convert Twitter's timestamp format to a `java.util.Date`

To run this example, simply execute `mvn` from this directory, or run a Maven build in Eclipse.

[http://wiki.eclipse.org/EclipseLink/Examples/MOXy/JSON_Twitter](http://wiki.eclipse.org/EclipseLink/Examples/MOXy/JSON_Twitter)