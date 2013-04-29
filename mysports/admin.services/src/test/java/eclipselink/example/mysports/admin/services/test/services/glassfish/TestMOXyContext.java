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
package eclipselink.example.mysports.admin.services.test.services.glassfish;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;

import org.eclipse.persistence.oxm.MediaType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eclipselink.example.mysports.admin.services.glassfish.JDBCResource;
import eclipselink.example.mysports.admin.services.glassfish.MOXyContextHelper;
import eclipselink.example.mysports.admin.services.test.services.ServerTests;

/**
 * @author dclarke
 * @since EclipseLink 2.4
 */
@Category(ServerTests.class)
public class TestMOXyContext {

    @Test
    public void createContext() throws JAXBException {
        JAXBContext context = MOXyContextHelper.createContext();

        Assert.assertNotNull(context);
        Assert.assertTrue(context instanceof org.eclipse.persistence.jaxb.JAXBContext);
    }

   // @Test
    public void marshalJDBCResourceAsJSON() throws JAXBException {
        JDBCResource resource = new JDBCResource();
        resource.setJndiName("jdbc/MySportsX");
        resource.setPoolName("mysports");

        Marshaller marshaller = MOXyContextHelper.createMarshaller(MediaType.APPLICATION_JSON);

        StringWriter writer = new StringWriter();
        marshaller.marshal(resource, writer);

        Assert.assertEquals("{\"id\":\"jdbc/MySportsX\",\"poolName\":\"mysports\"}", writer.toString());
    }

   // @Test
    public void unmarshalJDBCResourceAsJSON() throws JAXBException {
        Unmarshaller unmarshaller = MOXyContextHelper.createUnmarshaller(MediaType.APPLICATION_JSON);
        unmarshaller.setProperty("eclipselink.json.include-root", false);

        JAXBElement<JDBCResource> resourceElement = unmarshaller.unmarshal(new StreamSource(new StringReader("{\"id\":\"jdbc/MySportsX\",\"poolName\":\"mysports\"}}")), JDBCResource.class);
        JDBCResource resource = resourceElement.getValue();

        Assert.assertNotNull(resource);
        Assert.assertEquals("jdbc/MySportsX", resource.getJndiName());
        Assert.assertEquals("mysports", resource.getPoolName());
    }
}
