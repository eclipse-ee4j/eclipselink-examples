/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *       dclarke - initial
 *       dmccann - May 06, 2013 - added resource dir 'META-INF' for xml/xsd
 *       dmccann - May 09, 2013 - tokens for app server specific values
 *                 May 09, 2013 - resource/webinf dir, sessionsxml, path prefix
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.sessions.JNDIConnector;

public class MavenPackager extends IDEPackager {
	File resourceDir;
	static final String resourceDirName = "META-INF";
	static final String projectPathPrefix = "/META-INF/";
    static final String serverClassNameToken = "SERVER_CLASS_NAME_TOKEN";
    static final String extTransactionControllerClassToken = "EXT_TRANS_CONTROLLER_CLASS_TOKEN";
	
    public MavenPackager() {
        super(null, "maven", noArchive);
        srcDirname = "java";
        publicHTMLDirname = "webapp";
    }
    
    protected void buildResourceDir() throws FileNotFoundException {
        buildSrcDir();
    	resourceDir = new File(srcDir, resourceDirName);
        if (!resourceDir.exists()) {
            boolean worked = resourceDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + resourceDirName + " under " + srcDirname);
            }
        }
    }
    
    @Override
    protected void buildWebInfDir() throws FileNotFoundException {
        buildPublicHTMLDir();
        webInfDir = new File(publicHTMLDir, WEB_INF_DIR);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + WEB_INF_DIR + " under " + publicHTMLDirname);
            }
        }
    }
    
    @Override
    public String getOxProjectPathPrefix() {
        return projectPathPrefix;
    }
    
    @Override
    public String getOrProjectPathPrefix() {
        return projectPathPrefix;
    }
    
    @Override
    public OutputStream getOxStream() throws FileNotFoundException {
    	buildResourceDir();
        return new FileOutputStream(new File(resourceDir, DBWS_OX_XML));
    }
    
    @Override
    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
    	buildResourceDir();
        return new FileOutputStream(new File(resourceDir, sessionsFileName));
    }
    
    @Override
    public OutputStream getServiceStream() throws FileNotFoundException {
    	buildResourceDir();
        return new FileOutputStream(new File(resourceDir, DBWS_SERVICE_XML));
    }
    
    @Override
    public OutputStream getOrStream() throws FileNotFoundException {
    	buildResourceDir();
        return new FileOutputStream(new File(resourceDir, DBWS_OR_XML));
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder) {
        SessionConfigs ts = super.buildSessionsXML(dbwsSessionsStream, builder);
        String dataSource = builder.getDataSource();
        if (dataSource != null) {
            DatabaseSessionConfig tmpConfig =
            (DatabaseSessionConfig)ts.getSessionConfigs().firstElement();
            ProjectConfig orProject = tmpConfig.getPrimaryProject();
            LogConfig logConfig = tmpConfig.getLogConfig();
            String sessionName = tmpConfig.getName();
            DatabaseSessionConfig orSessionConfig = new ServerSessionConfig();
            orSessionConfig.setPrimaryProject(orProject);
            orSessionConfig.setName(sessionName);
            orSessionConfig.setLogConfig(logConfig);
            CustomServerPlatformConfig customServerPlatformConfig = new CustomServerPlatformConfig();
            customServerPlatformConfig.setEnableJTA(true);
            customServerPlatformConfig.setEnableRuntimeServices(true);
            customServerPlatformConfig.setServerClassName(serverClassNameToken);
            customServerPlatformConfig.setExternalTransactionControllerClass(extTransactionControllerClassToken);
            orSessionConfig.setServerPlatformConfig(customServerPlatformConfig);
            DatabaseLoginConfig dlc = new DatabaseLoginConfig();
            dlc.setPlatformClass(builder.getPlatformClassname());
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(builder.getDataSource());
            // TODO: the following is for JBoss
            //dlc.setLookupType(JNDIConnector.STRING_LOOKUP);
            dlc.setBindAllParameters(true);
            dlc.setStreamsForBinding(true);
            orSessionConfig.setLoginConfig(dlc);
            ts.getSessionConfigs().set(0, orSessionConfig);
        }
        return ts;
    }
}