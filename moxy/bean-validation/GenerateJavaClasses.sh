#!/bin/bash

if [ ! -f target/schema1.xsd ];
then
        echo
        echo "First run 'mvn install' to generate target/schema1.xsd"
        echo
        exit 1
fi

OUTPUT_DIR=target/out
rm -rf ${OUTPUT_DIR}
mkdir ${OUTPUT_DIR}

if [ -z ${ECLIPSELINK_HOME} ]  || [ ! -f ${ECLIPSELINK_HOME}/bin/jaxb-compiler.sh ];
then
        echo
        echo "Please set ECLIPSELINK_HOME to top-level eclipselink directory"
        echo
        exit 2
fi

if [ -z ${JAVA_HOME} ] || ! [ -d ${JAVA_HOME} ];
then
        echo
        echo "Please set JAVA_HOME to top-level java directory"
        echo
        exit 3
fi

$ECLIPSELINK_HOME/bin/jaxb-compiler.sh \
	-XBeanVal \
	-d ${OUTPUT_DIR} \
	-p eclipselink.example.moxy.beanvalidation.simple.model \
	target/schema1.xsd

echo 
echo "View ${OUTPUT_DIR}/eclipselink/example/moxy/beanvalidation/simple/model/Customer.java to see the generated Customer.java with bean validation constraints"
echo 
