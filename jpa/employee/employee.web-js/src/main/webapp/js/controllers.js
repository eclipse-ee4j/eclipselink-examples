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

/* Controllers */

function EmployeeListCtrl($scope, $location, Employee, Employees) {
	$scope.firstName = '%';
	$scope.lastName = '%';

	function resetEmployees() {
		$scope.employees = [];
	}

	function resetFilter() {
		$scope.query = null;
	}

	$scope.search = function() {
		if ($scope.pageResults) {
			resetPaging();
			Employees
					.count($scope.firstName, $scope.lastName)
					.then(
							function(response) {
								$scope.count = response.data.COUNT;
								$scope.totalPages = Math.floor($scope.count
										/ $scope.pageSize)
										+ ($scope.count % $scope.pageSize > 0 ? 1
												: 0);
								fetchPage();
							},
							function(response) {
								$scope.error = 'Error retrieving number of resources. Server response code '
										+ response.status;
							});

		} else {
			$scope.employees = Employees
					.getAll(
							{
								firstName : $scope.firstName,
								lastName : $scope.lastName
							},
							function() {
							},
							function(response) {
								$scope.error = 'Error retrieving resources. Server response code '
										+ response.status;
							});
		}
	};

	$scope.remove = function(employeeId) {
		$location.path("/employee/delete/" + employeeId);
	};

	$scope.edit = function(employeeId) {
		$location.path("/employee/edit/" + employeeId);
	};

	/* Paging Functionality */

	$scope.pageResults = false;
	function resetPaging() {
		$scope.pageNum = 1;
		$scope.totalPages = 0;
		$scope.pageSize = 10;
		$scope.first = 0;
		$scope.max = $scope.pageSize;
	}

	/* Respond to toggling of paging. */
	$scope.$watch('pageResults', function() {
		resetEmployees();
		resetFilter();
		if ($scope.pageResults) {
			resetPaging();
		}
	});

	function fetchPage() {
		$scope.employees = Employees.getPage({
			firstName : $scope.firstName,
			lastName : $scope.lastName
		}, {
			first : $scope.first,
			max : $scope.max
		});

		$scope.nextAvailable = $scope.count > $scope.max;
		$scope.prevAvailable = $scope.first - $scope.pageSize >= 0;
	}

	$scope.pageNext = function() {
		if ($scope.nextAvailable) {
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

function EmployeeDeleteCtrl($scope, $routeParams, $location, Employee) {
	$scope.employee = Employee.get({
		id : $routeParams.id
	}, function() {
		// success
	}, function(response) {
		$scope.error = 'Resource not found.  Server response code ' + response.status;
	});

	$scope.confirm = function() {
		Employee.remove({}, {
			id : $scope.employee.id
		}, function() {
			$location.path("/home");
		}, function(response) {
			$scope.error = 'Error deleting resource. Server response code '
					+ response.status;
		});

	};

}

function EmployeeCommon($scope, $location) {

	$scope.save = function() {
		// Attach sub objects for pass by value update
		// TODO: Figure out why assignment to $scope.employee properties doesn't
		// work
		$scope.employee.address = $scope.address;
		$scope.employee.phoneNumbers = $scope.phoneNumbers;
		$scope.employee.$save(function() {
			$location.path("/home");
		}, function(response) {
			$scope.error = 'Error saving resource. Server response code '
					+ response.status;
		});

	};

	$scope.remove = function() {
		$location.path("/employee/delete/" + $scope.employee.id);
	};

	$scope.addAddress = function() {
		$scope.address = {};
	};

	$scope.removeAddress = function() {
		delete $scope.address;
	};

	$scope.addPhone = function() {
		if ($scope.phoneNumbers) {
			$scope.phoneNumbers.push({});
		} else {
			$scope.phoneNumbers = [ {} ];
		}
	};

	$scope.removePhone = function(index) {
		var phoneNumbers = $scope.phoneNumbers;
		if (index == 0) {
			phoneNumbers.shift();
		} else if (index == phoneNumbers.length - 1) {
			phoneNumbers.pop();
		} else {
			var front = phoneNumbers.slice(0, index);
			var back = phoneNumbers.slice(index + 1, phoneNumbers.length);
			$scope.phoneNumbers = front.concat(back);
		}
	};

}

function EmployeeEditCtrl($scope, $routeParams, $location, $resource, Employee,
		EmployeePhones, EmployeeAddress) {

	var that = EmployeeCommon($scope, $location);

	$scope.removeEnabled = true;

	$scope.employee = Employee.get({
		id : $routeParams.id
	}, function() {
		// replace links with actual data for editing
		// TODO: Figure out why assignment to $scope.employee properties
		// doesn't work

		EmployeePhones.get($scope.employee, function(phones) {
			$scope.phoneNumbers = phones;
		});

		EmployeeAddress.get($scope.employee, function(address) {
			$scope.address = address;
		});
	}, function(response) {
		$scope.error = 'Error retrieving resource. Server response code '
				+ response.status;
	});

	return that;
}

function EmployeeCreateCtrl($scope, $location, Employee) {

	var that = EmployeeCommon($scope, $location);

	$scope.employee = new Employee();
	$scope.employee.gender = 'Female';

	return that;

}