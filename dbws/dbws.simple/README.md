EclipseLink DBWS: Simple Example
================================

This simple example illustrates how EclipseLink DBWS can be used to generate a JAX-WS web service based on a very simple database schema. This example is based on:

http://wiki.eclipse.org/EclipseLink/Examples/DBWS/DBWSBasicTable

For more DBWS examples please see: http://wiki.eclipse.org/EclipseLink/Examples/DBWS

This example consists of three phases: 

1.  WAR generation
2.  WAR deployment
3.  Test the generated JAX-WS web service


Running the Example
-------------------

1.  Download the example - Clone the examples GIT repo
2.  Configure database and application server information for use by the DBWS builder (${project.basedir}/dbws-builder.xml)
3.  Configure application server information for use by the DBWS runtime (${examples.home}/dbws/config.properties)
    The following two properties must be set according to the target application server:
        - server.name
        - external.txn.controller.name
    Valid values for each are:
        - WebLogic
            - org.eclipse.persistence.platform.server.wls.WebLogic_10_Platform
            - org.eclipse.persistence.transaction.wls.WebLogicTransactionController
        - GlassFish
            - org.eclipse.persistence.platform.server.sunas.SunAS9ServerPlatform
            - org.eclipse.persistence.transaction.sunas.SunAS9TransactionController
        - JBoss
            - org.eclipse.persistence.platform.server.jboss.JBossPlatform
            - org.eclipse.persistence.transaction.jboss.JBossTransactionController
        - WebSphere
            - org.eclipse.persistence.platform.server.was.WebSphere_7_Platform
            - org.eclipse.persistence.transaction.was.WebSphereTransactionController
4.  Run the packaging: run.builder/mvn
5.  Deploy the generated WAR to a compatible target server
6.  Test the generated web service: service.test/mvn