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
package eclipselink.example.mysports.admin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Customizer;

import eclipselink.example.mysports.admin.model.persistence.ExtensionMappingsCustomizer;

/**
 * A HostedLeague represents a sports league that is configured in the MySports
 * Admin application. It contains the league's (tenant) configuration with
 * respect to EclipseLink customization of data source, multi-tenancy, table
 * names, and extended attributes. It also contains Look-and-Feel configuration
 * for color scheme (CSS) and logo.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@Entity
@Table(name = "MYS_ADMIN_LEAGUE")
@NamedQueries({ @NamedQuery(name = "HostedLeague.findAll", query = "SELECT l FROM HostedLeague l ORDER BY l.name"), @NamedQuery(name = "HostedLeague.findAllShared", query = "SELECT l FROM HostedLeague l WHERE l.shared = TRUE ORDER BY l.name") })
@Customizer(ExtensionMappingsCustomizer.class)
@XmlRootElement
public class HostedLeague {

    public static final String PLAYER = "Player";
    public static final String TEAM = "Team";
    public static final String DIVISION = "Division";

    /**
     * Unique identifier for the league. This must be a simple string with just
     * characters that can be used within a URI.
     */
    @Id
    private String id;

    /**
     * Descriptive name of league. This string is used for display purposes only
     * and can contain spaces and punctuation.
     */
    private String name;

    private String colourScheme = "default";

    private String logoUrl;

    @Transient
    private String uri;

    @Column(name = "DATA_SOURCE")
    private String datasourceName;

    /**
     * Indicates that the league is hosted on a shared application instance
     * using a SaaS approach. If False the league requires its own dedicated
     * application instances to be packaged and deployed.
     */
    private boolean shared = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_ISOLATION")
    private DataIsolation dataIsolation = DataIsolation.ROW;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    @XmlTransient
    private List<Extension> playerExtensions = new ArrayList<Extension>();

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    @XmlTransient
    private List<Extension> teamExtensions = new ArrayList<Extension>();

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    @XmlTransient
    private List<Extension> divisionExtensions = new ArrayList<Extension>();

    @Version
    private long version;

    public HostedLeague() {
    }

    public HostedLeague(String id, String name, String scheme) {
        this();

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

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public void addPlayerExtension(Extension extension) {

    }

    public Collection<Extension> getPlayerExtensions() {
        return this.playerExtensions;
    }

    public void addPlayerExtension(String name, String javaType, String columnName) {
        Extension ext = new Extension(name, javaType, columnName);
        ext.setLeague(this, "Player");
        getPlayerExtensions().add(ext);
    }

    public Extension getPlayerExtension(String columnName) {
        for (Extension ext : getPlayerExtensions()) {
            if (ext.getColumnName().equals(columnName)) {
                return ext;
            }
        }
        return null;
    }

    public Collection<Extension> getTeamExtensions() {
        return this.teamExtensions;
    }

    public void addTeamExtension(String name, String javaType, String columnName) {
        Extension ext = new Extension(name, javaType, columnName);
        ext.setLeague(this, "Team");
        getTeamExtensions().add(ext);
    }

    public Extension getTeamExtension(String columnName) {
        for (Extension ext : getTeamExtensions()) {
            if (ext.getColumnName().equals(columnName)) {
                return ext;
            }
        }
        return null;
    }

    public Collection<Extension> getDivisionExtensions() {
        return this.divisionExtensions;
    }

    public void addDivisionExtension(String name, String javaType, String columnName) {
        Extension ext = new Extension(name, javaType, columnName);
        ext.setLeague(this, "Division");
        getDivisionExtensions().add(ext);
    }

    public Extension getDivisionExtension(String columnName) {
        for (Extension ext : getPlayerExtensions()) {
            if (ext.getColumnName().equals(columnName)) {
                return ext;
            }
        }
        return null;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public DataIsolation getDataIsolation() {
        return dataIsolation;
    }

    public void setDataIsolation(DataIsolation dataIsolation) {
        this.dataIsolation = dataIsolation;
    }

    protected void setVersion(long version) {
        this.version = version;
    }

    public Collection<Extension> getExtensions(String type) {
        if (PLAYER.equals(type)) {
            return getPlayerExtensions();
        }
        if (TEAM.equals(type)) {
            return getTeamExtensions();
        }
        if (DIVISION.equals(type)) {
            return getDivisionExtensions();
        }
        throw new IllegalArgumentException("No HostedLeague extensions for type: " + type);
    }

}
