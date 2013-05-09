/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.services.diagnostics;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Interceptor
public class DiagnosticsInterceptor {

    private Diagnostics diagnostics;

    @EJB
    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        System.out.println("*** DiagnosticsInterceptor intercepting " + ctx.getMethod().getName());

        try {
            return ctx.proceed();
        } finally {
            if (!ctx.getMethod().getName().equals("getDiagnostics")) {
                System.out.println("*** DiagnosticsInterceptor exiting");
            }
        }
    }

}
