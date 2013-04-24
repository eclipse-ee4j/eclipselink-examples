/**
 * *****************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: dclarke - EclipseLink 2.4 - MySports Demo Bug 344608
 * ****************************************************************************
 */
package eclipselink.example.mysports.persistence;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.jpa.PersistenceProvider;

/**
 * Custom {@link javax.persistence.spi.PersistenceProvider} to support the
 * TODO
 *
 * @author dclarke
 * @since EclipseLink 2.4
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TenantPerEMFProvider extends PersistenceProvider {

    /**
     * TODO
     */
    public static final String PROVIDER_CLASS = "eclipselink.example.mysports.persistence.TenantPerEMFProvider";
    
    /**
     * TODO
     */
    public static final String SEPARATOR = ";";
    /**
     * Tenant PU name prefix used to identify the tenant specific PU creation
     * request.
     */
    public static final String EQUALS = "=";

    @Override
    public EntityManagerFactory createEntityManagerFactory(String name, Map properties) {
        int sepIndex = name == null ? 0 : name.indexOf(SEPARATOR);

        if (name != null && !name.isEmpty() && sepIndex >= 0) {
            if (sepIndex == 0) {
                throw new IllegalArgumentException("Invalid tenant PU name");
            }

            String puName = name.substring(0, sepIndex);

            int equalsIndex = name.indexOf(EQUALS);

            if (equalsIndex <= sepIndex + 1) {
                throw new IllegalArgumentException("Invalid tenant PU name");
            }

            String contextProperty = name.substring(sepIndex + 1, equalsIndex);
            String contextValue = name.substring(equalsIndex + 1);

            Map props = properties == null ? new HashMap() : properties;

            if (!props.containsKey(PersistenceUnitProperties.SESSION_NAME)) {
                props.put(PersistenceUnitProperties.SESSION_NAME, name);
            }
            props.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, Boolean.FALSE.toString());
            props.put(contextProperty, contextValue);

            return super.createEntityManagerFactory(puName, props);
        }

        return super.createEntityManagerFactory(name, properties);
    }

    @Override
    public JPAInitializer getInitializer(String emName, Map m) {
        ClassLoader classLoader = getClassLoader(emName, m);
        return TenantPerEMFProviderInitializer.getJavaSECMPInitializer(classLoader);
    }
}
