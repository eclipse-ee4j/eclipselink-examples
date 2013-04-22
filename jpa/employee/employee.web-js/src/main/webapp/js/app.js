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

/* App Module */

angular.module('employee', [ 'employeeServices' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider
				.when('/employee/edit/:id', {
					templateUrl : 'employee/edit.html',
					controller : EmployeeEditCtrl
				}).when('/employee/delete/:id', {
					templateUrl : 'employee/delete.html',
					controller : EmployeeDeleteCtrl
				}).when('/employee/create', {
					templateUrl : 'employee/edit.html',
					controller : EmployeeCreateCtrl
				}).when('/employee/search', {
					templateUrl : 'employee/search.html',
					controller: EmployeeListCtrl
				}).when('/home', {
					templateUrl : 'partials/home.html'
				}).when('/about', {
					templateUrl : 'partials/about.html'
				}).otherwise({
					redirectTo : '/home'
				});
		} ]);
