/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package eclipselink.example.moxy.json.dynamic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;

/**
 * Main example execution.
 * 
 * @author bdoughan
 * @author rbarkhous
 * @since EclipseLink 2.4.2
 */
public class Main {

    private static final String BINDINGS = "META-INF/bindings.xml";
    private static final String INPUT_XML = "META-INF/input.xml";

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("Running EclipseLink MOXy Dynamic JSON Example");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream bindings = classLoader.getResourceAsStream(BINDINGS);
        ArrayList<InputStream> dataBindings = new ArrayList<InputStream>(1);
        dataBindings.add(bindings);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, dataBindings);
        properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

        DynamicJAXBContext jc = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);

        InputStream input = classLoader.getResourceAsStream(INPUT_XML);
        DynamicEntity customer = (DynamicEntity) unmarshaller.unmarshal(input);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        marshaller.marshal(customer, System.out);

        System.out.println();
        System.out.println();
    }

}