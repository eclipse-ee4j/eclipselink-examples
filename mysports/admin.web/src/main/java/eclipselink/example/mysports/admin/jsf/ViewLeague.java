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

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import eclipselink.example.mysports.admin.model.DataIsolation;
import eclipselink.example.mysports.admin.model.Extension;
import eclipselink.example.mysports.admin.model.HostedLeague;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@ManagedBean
@SessionScoped
public class ViewLeague {

    protected static final String PAGE = "league?faces-redirect=true";

    private HostedLeague league;

    private List<ExtensionDefinition> playerExtensions;

    private String datasourceName;

    @ManagedProperty("#{leagueRepository}")
    private LeagueRepository repository;

    public LeagueRepository getRepository() {
        return repository;
    }

    public void setRepository(LeagueRepository repository) {
        this.repository = repository;
    }

    public HostedLeague getLeague() {
        return league;
    }

    public String view() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest myRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String value = myRequest.getParameter("league-id");
        return view(value);
    }

    public String view(String id) {
        this.league = getRepository().getLeague(id);

        if (getLeague() != null ) {
            this.datasourceName = getLeague().getDatasourceName();
        }

        if (this.league == null) {
            this.datasourceName = null;
            return "index";
        }

        List<ExtensionDefinition> extensions = new ArrayList<ExtensionDefinition>(5);
        for (int index = 1; index <= 5; index++) {
            Extension ext = getLeague().getPlayerExtension("flex_" + index);
            extensions.add(new ExtensionDefinition(ext, "flex_" + index));
        }
        this.playerExtensions = extensions;

        return PAGE;
    }

    public String create() {
        this.league = new HostedLeague();
        this.datasourceName = null;
        return PAGE;
    }

    public DataIsolation[] getDataIsolations() {
        return DataIsolation.values();
    }

    public String[] getColourSchemes() {
        // TODO: Load from database using query on Styles
        return new String[] { "default", "red", "black", "blue", "green" };
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
        getLeague().setDatasourceName(getDatasourceName());
    }

    public String apply() {
        if (getPlayerExtensions() != null) {
            for (ExtensionDefinition ed : getPlayerExtensions()) {
                if (ed.isWriteable() && !ed.getName().isEmpty()) {
                    getLeague().addPlayerExtension(ed.getName(), "java.lang.String", ed.getColumn());
                    ed.setWriteable(false);
                }
            }
        }

        this.league = getRepository().getRepository().merge(getLeague());

        return null;
    }

    public String cancel() {
        this.league = null;
        return "index";
    }

    public boolean isNew() {
        return getLeague() != null && getLeague().getVersion() < 1;
    }

    public boolean isIdReadOnly() {
        return getLeague() != null && getLeague().getVersion() > 1;
    }

    public List<ExtensionDefinition> getPlayerExtensions() {
        return this.playerExtensions;
    }

    public class ExtensionDefinition {
        private String column;
        private String javaType;
        private String name;
        private boolean writeable = false;

        public ExtensionDefinition(Extension ext, String column) {
            super();
            if (ext == null) {
                this.writeable = true;
                this.column = column;
                this.javaType = "java.lang.String";
                this.name = "";
            } else {
                this.writeable = false;
                this.column = ext.getColumnName();
                this.javaType = ext.getJavaType();
                this.name = ext.getName();
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColumn() {
            return column;
        }

        public String getJavaType() {
            return javaType;
        }

        public boolean isWriteable() {
            return writeable;
        }

        public void setWriteable(boolean writeable) {
            this.writeable = writeable;
        }
    }
}
