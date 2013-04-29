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

import java.net.ConnectException;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.jersey.api.client.ClientHandlerException;

import eclipselink.example.mysports.admin.services.glassfish.JDBCResource;
import eclipselink.example.mysports.admin.services.glassfish.RESTOperations;
import eclipselink.example.mysports.admin.services.glassfish.Result;
import eclipselink.example.mysports.admin.services.test.services.ServerTests;

@Category(ServerTests.class)
public class TestParseListJDBCResources extends TestBaseJsonRest {

    @Test
    public void unmarshalMarshal() throws Exception {
        Unmarshaller unmarshaller = createJsonUnmarshaller();
        Marshaller marshaller = createJsonMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Result resources = unmarshaller.unmarshal(openSource("list-jdbc-resources.json"), Result.class).getValue();

        Assert.assertNotNull(resources);
        marshaller.marshal(resources, System.out);
    }

    @Test
    public void getJDBCResourceNames() throws Exception {
        Unmarshaller unmarshaller = createJsonUnmarshaller();
        Marshaller marshaller = createJsonMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Result resources = unmarshaller.unmarshal(openSource("list-jdbc-resources.json"), Result.class).getValue();

        Assert.assertNotNull(resources);

        String[] names = resources.getChildrenMessages();

        Assert.assertNotNull(names);
        Assert.assertEquals(3, names.length);

        for (String name : names) {
            System.out.println("JDBC Resource: \"" + name + "\"");
        }
    }

    @Test
    public void getJDBCResourceListFromServer() throws Exception {
        Result result = getResultFromServer("localhost", JDBCResource.LIST_URI);

        Assert.assertNotNull(result);

        System.out.println("RESULT:\n" + result);
        for (String name : result.getChildrenMessages()) {
            System.out.println("JDBC Resource: \"" + name + "\"");
        }
    }

    @Test
    public void getJDBCResourceNamesFromServer() throws Exception {
        RESTOperations ops = new RESTOperations("localhost");
        String[] names = null;

        try {
            names = JDBCResource.getJDBCResourceNames(ops);
        } catch (ClientHandlerException e) {
            if (e.getCause() instanceof ConnectException) {
                Assert.fail("GlassFish server on localhost not running");
            }
            throw e;
        }

        Assert.assertNotNull(names);
        Assert.assertTrue(names.length > 0);

        for (String name : names) {
            System.out.println("JDBC Resource: \"" + name + "\"");
        }
    }
}