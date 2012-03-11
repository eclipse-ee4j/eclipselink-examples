/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
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
package example.mysports.admin.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Entity
@Table(name = "mys_admin_league")
@NamedQueries({
@NamedQuery(name = "HostedLeague.findAll", query = "SELECT l FROM HostedLeague l ORDER BY l.name", hints = { @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = HintValues.FALSE) }),
@NamedQuery(name = "HostedLeague.findAllVisible", query = "SELECT l FROM HostedLeague l WHERE l.visible = true ORDER BY l.name", hints = { @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = HintValues.FALSE) })
})
@XmlRootElement
public class HostedLeague {

    @Id
    private String id;

    private String name;

    private String colourScheme = "default";

    private String logoUrl;

    private String uri;

    private String datasource = "jdbc/MySports";

    private boolean multitenant = true;

    @XmlTransient
    private boolean visible = true;

    /**
     * Custom table names that override the default names specified in the
     * entity's @Table annotation. These values will be used in the return ORM
     * file only if specified .
     */
    @ElementCollection
    @CollectionTable(name = "mys_admin_tablenames")
    @XmlTransient
    private Map<String, String> tableNames = new HashMap<String, String>();

    @Version
    private long version;

    public HostedLeague() {
    }

    public HostedLeague(String id, String name, String scheme) {
        this.id = id;
        this.name = name;
        this.colourScheme = scheme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColourScheme() {
        return colourScheme;
    }

    public void setColourScheme(String colourScheme) {
        this.colourScheme = colourScheme;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getVersion() {
        return version;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<String, String> getTableNames() {
        return tableNames;
    }

    public void addTableName(String entity, String tableName) {
        getTableNames().put(entity, tableName);
    }

    public boolean isMultitenant() {
        return multitenant;
    }

    public void setMultitenant(boolean multitenant) {
        this.multitenant = multitenant;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
