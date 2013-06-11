For details see http://wiki.eclipse.org/EclipseLink/Examples/PolyglotPersistence

To run the examples:
1) run mvn to build the four modules
2) cd into the polyglot.client folder
3) create the relational database with: java -classpath "target/*" example.CreateDatabase
4) run the example with: java -classpath "target/*" example.PolyglotDemo
5) reset both databases with: java -classpath "target/*" example.CleanDatabases