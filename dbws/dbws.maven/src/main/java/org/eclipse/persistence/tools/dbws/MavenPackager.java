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
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MavenPackager extends IDEPackager {
	File resourceDir;
	String resourceDirName;
	
    public MavenPackager() {
        super(null, "maven", noArchive);
        srcDirname = "java";
        publicHTMLDirname = "webapp";
        resourceDirName = "META-INF";
    }
    
    protected void buildResourceDir() throws FileNotFoundException {
        buildSrcDir();
    	resourceDir = new File(srcDir, resourceDirName);
        if (!resourceDir.exists()) {
            boolean worked = resourceDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + resourceDirName + " under " + srcDir);
            }
        }
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
}