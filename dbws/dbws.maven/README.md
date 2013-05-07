EclipseLink DBWS Builder Maven Install
======================================

In order to use EclipseLink's DBWS Builder in a maven project you must first install it into your local maven repo. 
This pom.xml and its config.properties file simplifies this. Simply edit the config.properties file to match the version 
and install location of the EclipseLink release and execute mvn.

After this has completed successfully other projects can reference these artifacts using:

		<dependency>
			<groupId>org.eclipse.persistence</groupId>
			<artifactId>oracleddlparser</artifactId>
			<version>2.4.2-RC1</version>
		</dependency>

		<dependency>
			<groupId>org.eclipse.persistence</groupId>
			<artifactId>dbws.builder</artifactId>
			<version>2.4.2-RC1</version>
		</dependency>

		<dependency>
			<groupId>org.eclipse.persistence</groupId>
			<artifactId>javax.wsdl</artifactId>
			<version>1.6.2</version>
		</dependency>
		
		<dependency>
			<groupId>org.eclipse.persistence</groupId>
			<artifactId>javax.servlet</artifactId>
			<version>2.4.0</version>
		</dependency>
		