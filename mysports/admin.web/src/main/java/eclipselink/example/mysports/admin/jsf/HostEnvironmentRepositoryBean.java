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

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import eclipselink.example.mysports.admin.model.HostEnvironment;
import eclipselink.example.mysports.admin.services.HostEnvironmentRepository;

@ManagedBean
@RequestScoped
public class HostEnvironmentRepositoryBean {

    @Inject
    private HostEnvironmentRepository repository;

    public HostEnvironmentRepository getRepository() {
        return repository;
    }

    public void setRepository(HostEnvironmentRepository repository) {
        this.repository = repository;
    }
    
    public List<HostEnvironment> getEnvironments() {
        return getRepository().getEnvironments();
    }
}
