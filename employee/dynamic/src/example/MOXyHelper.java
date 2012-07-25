/*******************************************************************************
 * Copyright (c) 2010-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - TODO
 ******************************************************************************/
package example;

import static org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE;
import static org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_INCLUDE_ROOT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.server.Server;

public class MOXyHelper {

    private static JAXBContext context;

    public static JAXBContext getContext(EntityManager em) throws JAXBException {
        if (context == null) {
            Set<Class> classes = new HashSet<Class>();

            Server serverSession = em.unwrap(Server.class);
            
            //classes.addAll(serverSession.getDescriptors().keySet());
            
            for (List<DatabaseQuery> queryList: serverSession.getQueries().values()) {
                for (DatabaseQuery query: queryList) {
                    if (query.isReportQuery()) {
                        ReportQuery rq = (ReportQuery) query;
                        for (ReportItem item: rq.getItems()) {
                           if (item.isConstructorItem()) {
                               classes.add(((ConstructorReportItem) item).getResultType());
                           }
                        }
                    }
                }
            }

            context = JAXBContextFactory.createContext(classes.toArray(new Class[classes.size()]), null);
        }
        return context;
    }

    public static Marshaller createMarshaller(EntityManager em, MediaType mediaType) throws JAXBException {
        Marshaller marshaller = getContext(em).createMarshaller();
        marshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        return marshaller;
    }

    public static Unmarshaller createUnmarshaller(EntityManager em, MediaType mediaType) throws JAXBException {
        Unmarshaller unmarshaller = getContext(em).createUnmarshaller();
        unmarshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        unmarshaller.setProperty(JSON_INCLUDE_ROOT, false);
        return unmarshaller;
    }

}
