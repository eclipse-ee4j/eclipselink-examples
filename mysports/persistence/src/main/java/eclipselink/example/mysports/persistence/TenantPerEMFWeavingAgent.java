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
 *****************************************************************************
 */
package eclipselink.example.mysports.persistence;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom agent to support weaving with custom provider:
 * {@link TenantPerEMFProvider}.
 *
 * @see TenantPerEMFProvider
 * @see TenantPerEMFProviderInitializer
 *
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class TenantPerEMFWeavingAgent {

    private static final String INITIALIZER_CLASS = "eclipselink.example.mysports.persistence.TenantPerEMFProviderInitializer";
    public static final String INITIALIZE_FROM_AGENT = "initializeFromAgent";
    public static final String GLOBAL_INSTRUMENTATION = "globalInstrumentation";

    public static void premain(String agentArgs, Instrumentation instr) throws Throwable {
        // Reflection allows:
        //  JavaSECMPInitializerAgent to be the *ONLY* class is the jar file specified in -javaagent;
        //  Loading JavaSECMPInitializer class using SystemClassLoader.
        if ((agentArgs != null) && agentArgs.equals("main")) {
            initializeFromMain(instr);
        } else {
            initializeFromAgent(instr);
        }
    }

    public static void initializeFromAgent(Instrumentation instr) throws Throwable {
        Class<?> cls = Class.forName(INITIALIZER_CLASS);
        Method method = cls.getDeclaredMethod(INITIALIZE_FROM_AGENT, new Class[]{Instrumentation.class});
        try {
            method.invoke(null, new Object[]{instr});
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }

    public static void initializeFromMain(Instrumentation instr) throws Exception {
        Class<?> cls = Class.forName(INITIALIZER_CLASS);
        Field field = cls.getField(GLOBAL_INSTRUMENTATION);
        field.set(null, instr);
    }
}
