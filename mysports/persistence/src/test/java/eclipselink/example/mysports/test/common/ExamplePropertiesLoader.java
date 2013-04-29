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
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.test.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Helper class that will load persistence unit overrides from a properties file
 * in both the current running folder and the current user's home folder. The
 * goal is to enable developers and users of the example to customize its
 * behavior without having to modify the source of the example.
 * 
 * @author dclarke
 * @since EclipseLink 2.0.0
 */
public class ExamplePropertiesLoader {

    public static final String DEFAULT_FILENAME = "eclipselink-example-mysports.properties";

    /**
     * 
     * @param properties
     */
    public static void loadProperties(Map<String, Object> properties) {
        loadProperties(properties, DEFAULT_FILENAME);
    }

    /**
     * 
     * @param properties
     */
    public static void loadProperties(Map<String, Object> properties, String filename) {
        loadProperties(properties, Thread.currentThread().getContextClassLoader(), filename);
        loadProperties(properties, new File(filename));

        String home = System.getProperty("user.home");
        loadProperties(properties, new File(home + System.getProperty("file.separator") + filename));

        for (Object key : System.getProperties().keySet()) {
            String keyName = (String) key;

            if (keyName.startsWith("javax.persistence") || keyName.startsWith("eclipselink")) {
                String value = System.getProperty(keyName);
                properties.put(keyName, value);
            }
        }
    }

    public static void loadProperties(Map<String, Object> properties, File file) {
        if (file.exists()) {
            InputStream in;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return;
            }
            loadProperties(properties, in);
        }
    }

    public static void loadProperties(Map<String, Object> properties, ClassLoader cl, String resource) {
        InputStream in = cl.getResourceAsStream(resource);
        loadProperties(properties, in);
    }

    public static void loadProperties(Map<String, Object> properties, InputStream in) {
        try {
            if (in != null) {
                Properties exampleProps = new Properties();
                exampleProps.load(in);
                in.close();

                for (Map.Entry<Object, Object> entry : exampleProps.entrySet()) {
                    properties.put((String) entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            // ignore
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
