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
package eclipselink.example.mysports.persistence;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;


/**
 * Custom initializer to support {@link TenantPerEMFProvider} usage.
 * This subclass is required to ensure
 * {@link #isPersistenceProviderSupported(String)} passes when using the custom
 * provider as well as storing its own singlton instance of this initializer and
 * avoid conflicting with the default EclipseLink JPA provider's initializer.
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
@SuppressWarnings("rawtypes")
public class TenantPerEMFProviderInitializer extends JavaSECMPInitializer {

    // Indicates whether has been initialized - that could be done only once.
    protected static boolean isInitialized;
    // Singleton corresponding to the main class loader. Created only if agent
    // is used.
    protected static JavaSECMPInitializer initializer;

    public static boolean isInContainer() {
        return isInContainer;
    }

    public static void setIsInContainer(boolean isInContainer) {
        JavaSECMPInitializer.isInContainer = isInContainer;
    }

    /**
     * Get the singleton entityContainer.
     */
    public static JavaSECMPInitializer getJavaSECMPInitializer() {
        return getJavaSECMPInitializer(Thread.currentThread().getContextClassLoader(), null, false);
    }

    public static JavaSECMPInitializer getJavaSECMPInitializer(ClassLoader classLoader) {
        return getJavaSECMPInitializer(classLoader, null, false);
    }

    public static JavaSECMPInitializer getJavaSECMPInitializerFromAgent() {
        return getJavaSECMPInitializer(Thread.currentThread().getContextClassLoader(), null, true);
    }

    public static JavaSECMPInitializer getJavaSECMPInitializerFromMain(Map m) {
        return getJavaSECMPInitializer(Thread.currentThread().getContextClassLoader(), m, false);
    }

    public static JavaSECMPInitializer getJavaSECMPInitializer(ClassLoader classLoader, Map m, boolean fromAgent) {
        if (!isInitialized) {
            if (globalInstrumentation != null) {
                synchronized (initializationLock) {
                    if (!isInitialized) {
                        initializeTopLinkLoggingFile();
                        if (fromAgent) {
                            AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.WEAVER, "cmp_init_initialize_from_agent", (Object[]) null);
                        }
                        usesAgent = true;
                        initializer = new TenantPerEMFProviderInitializer(classLoader);
                        initializer.initialize(m != null ? m : new HashMap(0));
                        // all the transformers have been added to
                        // instrumentation, don't need it any more.
                        globalInstrumentation = null;
                    }
                }
            }
            isInitialized = true;
        }
        if (initializer != null && initializer.getInitializationClassLoader() == classLoader) {
            return initializer;
        } else {
            // when agent is not used initializer does not need to be
            // initialized.
            return new TenantPerEMFProviderInitializer(classLoader);
        }
    }

    /**
     * INTERNAL: Should be called only by the agent. (when weaving classes) If
     * succeeded return true, false otherwise.
     */
    protected static void initializeFromAgent(Instrumentation instrumentation) throws Exception {
        // Squirrel away the instrumentation for later
        globalInstrumentation = instrumentation;
        getJavaSECMPInitializerFromAgent();
    }

    protected TenantPerEMFProviderInitializer(ClassLoader loader) {
        super(loader);
    }

    @Override
    public boolean isPersistenceProviderSupported(String providerClassName) {
        return providerClassName != null && providerClassName.equals(TenantPerEMFProvider.PROVIDER_CLASS);
    }
}
