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

var employeeServices = angular.module('employeeServices', [ 'ngResource' ]);

employeeServices
		.factory(
				'Employees',
				function($resource, $http) {
					var resource = $resource(
							'persistence/employee/query/Employee.findByName;firstName=:firstName;lastName=:lastName',
							{},
							{
								getPage : {
									method : 'GET',
									params : {
										'eclipselink.jdbc.first-result' : '@first',
										'eclipselink.jdbc.max-rows' : '@max'
									},
									isArray : true
								},
								getAll : {
									method : 'GET',
									params : {},
									isArray : true
								}
							});
					/*
					 * Add method to resource to obtain total number of
					 * Employees.
					 */
					resource.count = function(firstName, lastName) {
						return $http
								.get('persistence/employee/singleResultQuery/Employee.countByName;firstName='
										+ firstName + ';lastName=' + lastName);
					};
					return resource;
				});

employeeServices.factory('Employee', function($resource) {
	return $resource('persistence/employee/entity/Employee/:id', {}, {
		get : {
			method : 'GET',
			isArray : false
		},
		save : {
			method : 'POST',
			isArray : false
		},
		remove : {
			method : 'DELETE',
			isArray : false,
			params : {
				'id' : '@id'
			}
		}
	});
});

function findHref(name, relationships) {
	for ( var index in relationships) {
		var link = relationships[index];
		if (link._link.rel == name) {
			// return URL with :'s escaped
			return link._link.href.replace(/\:/g, '\\:');
		}
	}
};

employeeServices.factory('EmployeePhones', function($resource) {
	return {
		get : function(employee, callback) {

			// PhoneNumbers
			var phonesUri = findHref('phoneNumbers', employee._relationships);
			var phonesResource = $resource(phonesUri, {}, {
				get : {
					method : 'GET',
					isArray : true
				}
			});
			var phones = phonesResource.get({}, function() {
				callback(phones);
			});
		}
	};
});

employeeServices.factory('EmployeeAddress', function($resource) {
	return {
		get : function(employee, callback) {
			// Address
			var addressUri = findHref('address', employee._relationships);
			var addressResource = $resource(addressUri);
			var address = addressResource.get({}, function() {
				callback(address);
			});
		}
	};
});
