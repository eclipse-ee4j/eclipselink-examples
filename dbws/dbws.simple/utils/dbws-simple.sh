# !/bin/sh
#****************************************************************************************
# Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
# which accompanies this distribution.
#
# The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
# and the Eclipse Distribution License is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# Contributors:
#  - egwin   - May 2013 - Initial implementation
#  - dmccann - May 2013 - Modified paths since the script will live in dbws.simple
#****************************************************************************************

#==========================
#   Basic Env Setup
#

#Define common variables
THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
PARAM=$1

#==========================
#   Functions Definitions
#
unset usage
usage() {
    echo "Usage: ${PROGNAME} PARAM"
    echo "   PARAM     - exec parameter: can be 'clean', 'build', or 'test'"
}

unset clean
clean() {
        cd ..
		mvn clean
        cd dbws.maven
        mvn clean
		cd ../dbws.simple
		mvn clean
		cd service
		mvn clean
}

unset build
build() {
        cd ..
        mvn
        cd dbws.maven
        mvn
        cd ../dbws.simple
		mvn
        echo " "
		echo "**** Build complete. Deploy the generated war file, then run '$PROGNAME test'. ****"
}

unset test
test() {
		cd service
		mvn
        mvn site surefire-report:report-only
}

#==========================
#   Main Begins

#==========================
#   Validate run parameters
if [ "${PARAM}" = "" ] ; then
    usage
    echo " "
    echo "No parameter specified! Exiting..."
    exit 1
fi
if [ "${PARAM}" = "clean" ] ; then
    clean
    echo " "
else if [ "${PARAM}" = "build" ] ; then
    build
    echo " "
    else if [ "${PARAM}" = "test" ] ; then
        test
        echo " "
        else
            usage
            echo "${PARAM} not recognized as a valid option."
        fi
    fi
fi