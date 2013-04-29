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
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.application.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * JSF managed bean used to provide custom the look and feel of the UI in a
 * league specific manner
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@SessionScoped
public class LookAndFeel extends BaseManagedBean {

    private String adminServerURL;

    private String getAdminServerURL() {
        if (this.adminServerURL == null) {
            ExternalContext ctxt = FacesContext.getCurrentInstance().getExternalContext();

            this.adminServerURL = ctxt.getRequestScheme() + "://" + ctxt.getRequestServerName() + ":" + ctxt.getRequestServerPort();
            this.adminServerURL += "/MySportsAdmin/rest/league"; // TODO
        }
        return this.adminServerURL;
    }

    public String getCss() {
        return getAdminServerURL() + "/" + getLeagueId() + ".css";
    }

    public String getLogo() {
        return getAdminServerURL() + "/" + getLeagueId() + ".png";
    }

}
