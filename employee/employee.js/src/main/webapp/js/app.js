'use strict';

/* App Module */

angular.module('employee', [ 'employeeServices' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider
				.when('/employee', {
					templateUrl : 'partials/employee-list.html',
					controller : EmployeeListCtrl
				}).when('/employee/:id', {
					templateUrl : 'partials/employee-details.html',
					controller : EmployeeDetailsCtrl
				}).otherwise({
					redirectTo : '/employee'
				});
		} ]);
