#!/bin/sh
CLASSPATH=
for i in `ls target/*.jar`
do
CLASSPATH=${CLASSPATH}:${i}
done
java -classpath ${CLASSPATH} $1