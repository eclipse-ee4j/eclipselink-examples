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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.junit.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import eclipselink.example.mysports.admin.services.glassfish.JDBCResource;
import eclipselink.example.mysports.admin.services.test.services.ServerTests;

@Category(ServerTests.class)
public class TestParseJDBCResources extends TestBaseJsonRest {

    @Test
    public void parseFromFile() throws Exception {
        Unmarshaller unmarshaller = createJsonUnmarshaller();
        Marshaller marshaller = createJsonMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JDBCResource resource = unmarshaller.unmarshal(openSource("jdbc-resource.json"), JDBCResource.class).getValue();

        Assert.assertNotNull(resource);

        JAXBElement<JDBCResource> element = new JAXBElement<JDBCResource>(new QName(""), JDBCResource.class, resource);
        marshaller.marshal(element, System.out);
    }

    @Test
    public void getJDBCResourceFromServer() throws Exception {
        JDBCResource result = getResultFromServer("localhost", JDBCResource.RESOURCE_URI + "jdbc%2FMySports", JDBCResource.class);

        Assert.assertNotNull(result);

        System.out.println("RESULT:\n" + result);

       printAsJson(result);
    }

    @Test
    public void getJDBCResourceFromServer2() throws Exception {
        JDBCResource result = getResultFromServer("localhost", JDBCResource.RESOURCE_URI + "jdbc%2FMySports", JDBCResource.class);

        Assert.assertNotNull(result);

        System.out.println("RESULT:\n" + result);

       printAsJson(result);
    }
}