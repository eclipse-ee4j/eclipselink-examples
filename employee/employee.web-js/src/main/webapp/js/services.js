/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: shsmith - EclipseLink 2.4
 ******************************************************************************/

'use strict';

/* Services */

var serverHost = document.location.host;
var serverPort = document.location.port;
var serverUrl = 'http://' + serverHost;
if (serverPort) {
	serverUrl = serverUrl + ":" + serverPort;
}

var employeeServices = angular.module('employeeServices', ['ngResource']);

/*
 * AngularJS as of 1.0.4 doesn't support overriding 'headers:' but is a 
 * pending feature.  Workaround in function below.
 */
employeeServices.config(function($httpProvider){
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/json';
    $httpProvider.defaults.headers.post['Accept'] = 'application/json';
});


employeeServices.factory('Employees', function($resource) {
    return $resource(
            serverUrl + '/employee/persistence/employee/query/Employee.findAll',
            {},
            {method: 'GET', isArray: true, headers: {'Accept': 'application/json'}}
            );
});

employeeServices.factory('Employee', function($resource) {
	return $resource(
			serverUrl + '/employee/persistence/employee/entity/Employee/:id',
			{},
			{
				get: {method: 'GET', isArray: false, headers: {'Accept': 'application/json'}},
				save: {method: 'POST', isArray: false, headers: 
					{'Content-Type': 'application/json', 'Accept': 'application/json'}}
			});
});


