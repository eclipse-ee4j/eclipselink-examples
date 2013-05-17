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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Backing bean to edit or create an {@link Employee}.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@RequestScoped
public class Navigation {

    protected static final String INDEX_REDIRECT = "/index?faces-redirect=true";
    protected static final String ABOUT_REDIRECT = "/about?faces-redirect=true";
    protected static final String ADMIN_REDIRECT = "/admin?faces-redirect=true";
    protected static final String EDIT = "/employee/edit";
    protected static final String EDIT_REDIRECT = EDIT + "?faces-redirect=true";
    protected static final String SEARCH_REDIRECT = "/employee/search?faces-redirect=true";
    protected static final String DELETE = "/employee/delete";

    public String home() {
        return INDEX_REDIRECT;
    }

    public String about() {
        return ABOUT_REDIRECT;
    }

    public String admin() {
        return ADMIN_REDIRECT;
    }

    public String edit() {
        return EDIT_REDIRECT;
    }
    
    @ManagedProperty("#{searchEmployees}")
    private SearchEmployees search;

    public SearchEmployees getSearch() {
        return search;
    }

    public void setSearch(SearchEmployees search) {
        this.search = search;
    }

    public String search() {
        getSearch().getCriteria().reset();
        return SEARCH_REDIRECT;
    }

}
