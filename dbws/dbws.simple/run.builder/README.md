EclipseLink DBWS: Simple Example - Generate WAR
===============================================

EclipseLink DBWS can be used to generate a JAX-WS web service based on a very simple database schema.  The DBWS builder will be used to generate a deployable WAR file based on a simple database schema.

Running the DBWSBuilder
-----------------------

1.  Configure database and application server information for used by the DBWS builder as outlined in ${examples.home}/dbws/dbws.simple/README.md
2.  Configure application server information for use by the DBWS runtime as outlined in ${examples.home}/dbws/dbws.simple/README.md
3.  Run the Package goal to generate the WAR file: mvn
4.  Deploy the WAR file to a compatable application server