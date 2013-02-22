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

var employeeServices = angular.module('employeeServices', ['ngResource']);

employeeServices.factory('Employees', function($resource, $http) {
	var resource = $resource('persistence/employee/query/Employee.findByName;firstName=:firstName;lastName=:lastName', {}, {
		getPage: {
			method: 'GET',
			params: {
				'eclipselink.jdbc.first-result': '@first',
				'eclipselink.jdbc.max-rows': '@max'
			},
			isArray: true
		},
		getAll: {
			method: 'GET',
			params: {},
			isArray: true
		}
	});
	/*
	 * Add method to resource to obtain total number of Employees.
	 */
	resource.count = function(firstName, lastName) {
		return $http.get('persistence/employee/singleResultQuery/Employee.countByName;firstName=' + firstName + ';lastName=' + lastName);
	};
	return resource;
});

employeeServices.factory('Employee', function($resource) {
	return $resource('persistence/employee/entity/Employee/:id', {}, {
		get: {
			method: 'GET',
			isArray: false
		},
		save: {
			method: 'POST',
			isArray: false
		},
		remove: {
			method: 'DELETE',
			isArray: false,
			params: {
				'id': '@id'
			}
		}
	});
});
