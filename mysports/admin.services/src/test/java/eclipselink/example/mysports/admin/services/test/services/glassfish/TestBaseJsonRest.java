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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.ConnectException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;

import org.eclipse.persistence.oxm.MediaType;

import com.sun.jersey.api.client.ClientHandlerException;

import eclipselink.example.mysports.admin.services.glassfish.MOXyContextHelper;
import eclipselink.example.mysports.admin.services.glassfish.RESTOperations;
import eclipselink.example.mysports.admin.services.glassfish.Result;

/**
 * Base class to simplify file and GlassFish admin REST calls.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public abstract class TestBaseJsonRest {

    protected Unmarshaller createJsonUnmarshaller() throws JAXBException {
        return MOXyContextHelper.createUnmarshaller(MediaType.APPLICATION_JSON);
    }

    protected Marshaller createJsonMarshaller() throws JAXBException {
        return MOXyContextHelper.createMarshaller(MediaType.APPLICATION_JSON);
    }

    protected StreamSource openSource(String fileName) throws FileNotFoundException {
        String filePath = "src/" + getClass().getPackage().getName().replace(".", "/") + "/" + fileName;
        return new StreamSource(new FileInputStream(filePath));
    }

    protected <T> T getResultFromServer(String server, String resourceURL, Class<T> resultClass) throws JAXBException {
        RESTOperations ops = new RESTOperations(server);

        try {
            return ops.get(resourceURL, MediaType.APPLICATION_JSON, resultClass);
        } catch (ClientHandlerException e) {
            if (e.getCause() instanceof ConnectException) {
                Assert.fail("GlassFish server on localhost not running");
            }
            throw e;
        }
    }

    protected Result getResultFromServer(String server, String resourceURL) throws JAXBException {
        return getResultFromServer(server, resourceURL, Result.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void printAsJson(Result result) throws JAXBException {
        Marshaller marshaller = createJsonMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        JAXBElement<?> element = new JAXBElement(new QName(""), result.getClass(), result);
        marshaller.marshal(element, System.out);
    }
}