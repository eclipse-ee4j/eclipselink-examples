EclipseLink DBWS: Simple Example - Service Test
===============================================

This test will execute a FindAll request against the generated empService.

Testing the generated JAX-WS web service
----------------------------------------

1.  Deploy WAR file to compatible target server
2.  Run the test: mvn

An HTML report will be generated to'target/site/surefilre-report.html'.  Note that executing 'mvn site surefire-report:report-only' after running the test (via 'mvn' or 'mvn test') will produce a more readable report.