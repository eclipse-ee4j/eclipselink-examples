EclipseLink Social-Binding Example
---------------------------------------

This example will demonstrate how to use EclipseLink MOXyto work with public JSON and XML feeds using a declarative binding approach. 

The following concepts are demonstrated:

* Mapping to JSON and XML data without writing or generating concrete Java classes
* Using multiple MOXy metadata files to modularize your metadata
* Bootstrapping a DynamicJAXBContext from multiple MOXy metadata files
* Using a single DynamicJAXBContext to read both JSON and XML data
* Using DynamicEntity APIs to interact with mapped data
* Using XPath Predicates to map to an attribute based on another attribute value
* Using UnmarshallerProperties.JSON_INCLUDE_ROOT to read JSON that does not have a root element
* Using Marshaller.JAXB_FRAGMENT to omit the XML preamble when writing

The example application uses Dynamic MOXy to read a JSON stream from Reddit, an XML stream from Flickr, and uses the data from both to create an HTML file.

[http://wiki.eclipse.org/EclipseLink/Examples/MOXy/Dynamic/Flickr](http://wiki.eclipse.org/EclipseLink/Examples/MOXy/Dynamic/Flickr)
