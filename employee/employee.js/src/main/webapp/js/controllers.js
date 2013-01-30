'use strict';

/* Controllers */

function EmployeeListCtrl($scope, Employees) {
	$scope.employees = Employees.query();
}

function EmployeeDetailsCtrl($scope, $routeParams, Employee) {
	$scope.employee = Employee.get({id:$routeParams.id});
}