/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: ssmith - Bug 371099: Native ORM Byte Code Weaving Example
 ******************************************************************************/
package example;

import java.util.List;

import model.Employee;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.SessionManager;

public class QueryExample {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        DatabaseSession session = (DatabaseSession) SessionManager.getManager().getSession("employee");

        UnitOfWork uow = session.acquireUnitOfWork();
        ExpressionBuilder employee = new ExpressionBuilder();
        Expression where = employee.get("lastName").like("Smit%");
        System.err.println(">>>>>>>>>> Should see select from EMPLOYEE--NOT ADDRESS or PHONE tables");
        List<Employee> employees = uow.readAllObjects(Employee.class, where);

        for (Employee eachEmployee : employees) {
            System.err.println(eachEmployee);
            System.err.println(">>>>>>>>> Should see select from ADDRESS table");
            System.err.println(eachEmployee.getAddress().getCity());
            System.err.println(">>>>>>>>> Should see select from PHONE table");
            System.err.println(eachEmployee.getPhoneNumbers().size());
        }

        uow.release(); // discard any changes
        SessionManager.getManager().destroyAllSessions();
    }
}
