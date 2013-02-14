/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  shsmith - EclipseLink 2.4
 ******************************************************************************/

'use strict';

/* Controllers */

function EmployeeListCtrl($scope, $http, Employees) {
	$scope.pageNum = 1;
	$scope.totalPages = 0;
	$scope.pageSize = 10;
	$scope.first = 0;
	$scope.max = $scope.pageSize;

	Employees.count().then(function (response) {
		$scope.count = response.data.COUNT;
		$scope.totalPages = Math.floor($scope.count / $scope.pageSize) + ($scope.count % $scope.pageSize > 0 ? 1 : 0);
		fetchPage();
	});
	
	function fetchPage() {
		$scope.employees = Employees.getPage({}, {
			first : $scope.first,
			max : $scope.max
		});
		$scope.nextAvailable = $scope.count > $scope.max;
		$scope.prevAvailable = $scope.first - $scope.pageSize >= 0;
	}
	
	$scope.pageNext = function () {
		if ($scope.nextAvailable)  {
			$scope.first = $scope.max;
			$scope.max = $scope.max + $scope.pageSize;
			$scope.pageNum++;
			fetchPage();
		}
	};
	
	$scope.pagePrevious = function() {
		if ($scope.prevAvailable) {
			$scope.max = $scope.first;
			$scope.first = $scope.first - $scope.pageSize;
			$scope.pageNum--;
			fetchPage();
		}
	};
	
}

function EmployeeEditCtrl($scope, $routeParams, $location, Employee) {
	$scope.employee = Employee.get({
		id : $routeParams.id
	});

	$scope.save = function() {
		$scope.employee.$save();
		$scope.cancel();
	};

	$scope.cancel = function() {
		$location.path("/home");
	};

}

function EmployeeCreateCtrl($scope, $location, Employee) {
	$scope.employee = new Employee();
	$scope.employee.gender = 'Male';
	
	$scope.save = function() {
		$scope.employee.$save();
		$scope.cancel();
	};

	$scope.cancel = function() {
		$location.path("/home");
	};
}