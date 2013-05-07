#!/bin/sh

if [ $# -eq 0 ] 
	then
    echo "No arguments supplied: Must provide directory where WLS 12.1.2 is installed"
    echo " USAGE: ./createDataSource.sh ~/java/wls_1212"
    exit
fi

pushd $1/wlserver/server/bin

. ./setWLSEnv.sh

popd

java weblogic.WLST createDataSource.py
