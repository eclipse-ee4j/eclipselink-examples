mvn install:install-file -Dpackaging=jar -Dfile=eclipselink-dbwsutils.jar -DgroupId=org.eclipse.persistence -DartifactId=dbws.builder -Dversion="2.4.2-RC1"

mvn install:install-file -Dpackaging=jar -Dfile=org.eclipse.persistence.oracleddlparser_1.0.0.v20121122.jar -DgroupId=org.eclipse.persistence -DartifactId=oracleddlparser -Dversion="2.4.2-RC1" -DsourceFile=org.eclipse.persistence.oracleddlparser.source_1.0.0.v20121122.jar

mvn install:install-file -Dpackaging=jar -Dfile=javax.wsdl_1.6.2.v201012040545.jar -DgroupId=org.eclipse.persistence -DartifactId=javax.wsdl -Dversion="1.6.2"
