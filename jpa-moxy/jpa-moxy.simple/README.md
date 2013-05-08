Simple JPA-MOXY Example
-----------------------

This example illustrates JPA-JAXB fidelity with EclipseLink JPA and MOXy:

It demonstrates the use of XmlInverseReference to manage the ManyToOne 'backpointer' of a ManyToOne relationship.  EclipseLink MOXy will marshall a cyclical JPA entity graph to XML or JSON and upon unmarshall, will restore the backpointer.

It also demonstrates how EclipseLink MOXy and JPA can be used to obtain database data in either XML or JSON format and how that data can be modified and any changes returned to the database.  This ability underlies JPA-RS.

Running the Example
-------------------

Using Maven, create and populate the sample database with:

	mvn exec:java -P initdb
	
You can run the Marshall class to produce either an XML or JSON version of the sole Customer object in the database with either:

	mvn exec:java -P marshall-json
	
or

	mvn exec:java -P marshall-xml
	
The result will be a  customer.[xml|json] file in the target folder.  Feel free to edit the contents of the file to say, change the name of the customer.

To apply your changes to the database run the appropriate command, either:

	mvn exec:java -P unmarshall-json
	
or 

	mvn exec:java -P unmarshall-xml
	
With EclipseLink logging set to FINE in the example you'll see UPDATE statements that correspond to the changes you made to the XML or JSON file.