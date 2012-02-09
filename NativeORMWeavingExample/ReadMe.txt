This example illustrates the use of byte code weaving in a 
native ORM (non-JPA) EclipseLink application.

Configuration
=============

1. Database Login and JDBC Driver

To run the example in Eclipse you'll need to define a user
library name "EclipseLink" which must include at a minimum
eclipselink.jar and the javax.persistence API jar.  You'll
also need a database driver user library.  The example comes
expecting "Oracle Database 11g Driver Default" to include
an Oracle database driver--this is the name of the Oracle 11g
library provided by the Oracle Enterprise Pack for Eclipse 
(OEPE) but you can configure any JDBC driver as long as you
update the sessions.xml with the appropriate database login
information.  You can edit the sessions.xml with the
EclipseLink Mapping Workbench.

Note: The Mapping Workbench project is located in the 
mwproject folder of the project.

2. Run Configuration

The provided run configurations include the necessary Java
VM -javaagent argument to enable byte code weaving.  The 
required argument to -javaagent is the full path to 
eclipselink.jar.  Rather than hard wiring it in each run 
configuration eclipselink.jar is referenced through a variable.
Open Window>Preference>Run/Debug>String Substitution and define
a variable "eclipselink.jar" to be the full explicit path to
the actual jar. 

Running the Example
===================
Once your database login, project classpath, and run configuration
variable is defined:

1. Run example.CreateDatabase to create the example tables.
2. Run example.InsertExample to insert a collection of objects.
3. Run example.QueryExample and examine the console.  Note
   that the SELECT for Address occurs only when the Address is
   required and not when the Employee is read.  This illustrates
   that EclipseLink has transparently woven a ValueHolder into
   the Employee for Address, something that had to be done 
   manually prior to the availability of byte code weaving. 
