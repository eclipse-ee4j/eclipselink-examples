EclipseLink MOXy Bean Validation Example
----------------------------------------

This example will demonstrate how to use EclipseLink MOXy to generate an XML schema with element constraints from a Java Class with Bean Validation constraints.In particular, see the firstName field in Customer.java

This example will also demonstrate the reverse - generating a Java Class with Bean Validation constraints from an XML schema with element constraints.

Support for Bean Validation constraints with XML Schema is a feature specific
to EclipseLink.

Running the Example
-------------------
To generate an XML Schema from a Java class, execute:

        mvn install

The generated XML schema (schema1.xsd) will be in the target directory:
        target/schema1.xsd

To generate Java classes from an XML schema, follow these two steps:
        1) Set ECLIPSELINK_HOME to your EclipseLink 2.6.0+ installation
        2) Execute GenerateJavaClasses.sh (or .cmd on Windows) to generate the
           model from the schema1.xsd (generated from 'mvn install' above). The
           generated class, Customer.java will be under the 'out' directory
