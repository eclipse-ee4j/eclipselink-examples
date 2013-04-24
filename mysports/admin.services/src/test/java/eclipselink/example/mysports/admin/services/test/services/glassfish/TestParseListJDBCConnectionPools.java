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

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import eclipselink.example.mysports.admin.services.glassfish.Message;
import eclipselink.example.mysports.admin.services.glassfish.Result;
import eclipselink.example.mysports.admin.services.test.services.ServerTests;

@Category(ServerTests.class)
public class TestParseListJDBCConnectionPools extends TestBaseJsonRest {

    @Test
    public void parse() throws Exception {
        Unmarshaller unmarshaller = createJsonUnmarshaller();
        Marshaller marshaller = createJsonMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Message message = unmarshaller.unmarshal(openSource("list-jdbc-connection-pools.json"), Message.class).getValue();

        marshaller.marshal(message, System.out);
    }

    @Test
    public void getJDBCResourceFromServer() throws Exception {
        Result result = getResultFromServer("localhost", "/domain/resources/list-jdbc-connection-pools");

        System.out.println("RESULT:\n" + result);

        for (String name : result.getChildrenMessages()) {
            System.out.println("JDBC Connection Pool: \"" + name + "\"");
        }
    }

}