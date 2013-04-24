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
package eclipselink.example.mysports.admin.services.glassfish;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ JDBCResource.class })
public class Result {

    private String message;

    private String command;

    @XmlElement(name = "exit_code")
    private String exitCode;

    private List<Message> children;

    private Properties properties;

    @XmlPath("extraProperties/properties")
    private List<Property> extraProperties;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public List<Message> getChildren() {
        return children;
    }
    
    public String[] getChildrenMessages() {
        String[] messages = new String[getChildren().size()];
        for (int index = 0; index < getChildren().size(); index++) {
            messages[index] = getChildren().get(index).getMessage();
        }
        return messages;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Property> getExtraProperties() {
        return extraProperties;
    }

    public String toString() {
        return "RESTResult(" + getCommand() + ")";
    }
}
