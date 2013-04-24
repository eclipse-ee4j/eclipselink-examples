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
package eclipselink.example.mysports.application.model;

import static org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE;
import static org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_INCLUDE_ROOT;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;

public class MOXyHelper {

    private static JAXBContext context;

    public static JAXBContext getContext() throws JAXBException {
        if (context == null) {
            context = JAXBContextFactory.createContext(new Class[] { Division.class, Team.class, Player.class }, null);
        }
        return context;
    }

    public static Marshaller createMarshaller(MediaType mediaType) throws JAXBException {
        Marshaller marshaller = getContext().createMarshaller();
        marshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        return marshaller;
    }

    public static Unmarshaller createUnmarshaller(MediaType mediaType) throws JAXBException {
        Unmarshaller unmarshaller = getContext().createUnmarshaller();
        unmarshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        unmarshaller.setProperty(JSON_INCLUDE_ROOT, false);
        return unmarshaller;
    }

}
