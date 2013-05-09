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
package eclipselink.example.jpa.employee.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import eclipselink.example.jpa.employee.services.diagnostics.Diagnostics;
import eclipselink.example.jpa.employee.services.diagnostics.Diagnostics.SQLTrace;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@RequestScoped
public class DiagnosticsTrace {

    private Diagnostics diagnostics;

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    @EJB
    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public String getMessages() {
        SQLTrace trace = getDiagnostics().getTrace(true);

        if (trace != null) {
            for (String entry : trace.getEntries()) {
                FacesContext.getCurrentInstance().addMessage("SQL", new FacesMessage(entry));
            }
        }
        return null;
    }

}
