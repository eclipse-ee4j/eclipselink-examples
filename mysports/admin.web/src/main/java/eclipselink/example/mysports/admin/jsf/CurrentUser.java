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
 *  dclarke - EclipseLink 2.4 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.admin.jsf;

import javax.faces.bean.ManagedBean;


@ManagedBean
public class CurrentUser {

    private String name = "Doug@" + System.currentTimeMillis();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String doit() {
        setName("Doug@" + System.currentTimeMillis());
        return null;
    }
}
