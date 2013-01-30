'use strict';

/* Services */

var employeeServices = angular.module('employeeServices', ['ngResource']);

employeeServices.factory('Employees', function($resource) {
    return $resource(
            'http://localhost\\:7001/employee/persistence/employee/query/Employee.findAll',
            {},
            {method: 'GET', isArray: true, headers: {'Accept': 'application/json'}});
});

employeeServices.factory('Employee', function($resource) {
	return $resource(
			'http://localhost\\:7001/employee/persistence/employee/entity/Employee/:id',
			{},
			{method: 'GET', isArray: false, headers: {'Accept': 'application/json'}});
});
