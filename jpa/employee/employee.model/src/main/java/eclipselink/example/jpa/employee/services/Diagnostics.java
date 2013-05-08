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
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.server.Server;

/**
 * SessionLog proxy {@link InvocationHandler} used to intercept SQL logging
 * messages so this sample application can display the SQL executed by the most
 * recent operations.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Local
public class Diagnostics implements InvocationHandler {

    /**
     * property name to store {@link Diagnostics} instance for a given
     * {@link EntityManagerFactory} within its {@link Server} session's
     * properties.
     */
    private static final String DIAGNOSTICS = Diagnostics.class.getName();

    private ThreadLocal<SQLTrace> traces = new ThreadLocal<SQLTrace>();
    private SessionLog log;

    /**
     * Lookup the Diagnostics instance and registered as the SessionLog (proxy).
     * If one does not exist then create one.
     */
    public static Diagnostics getInstance(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            return getInstance(em);
        } finally {
            em.close();
        }
    }

    public static Diagnostics getInstance(EntityManager em) {
        Server session = em.unwrap(Server.class);

        Diagnostics diagnostics = (Diagnostics) session.getProperty(DIAGNOSTICS);

        if (diagnostics == null) {
            synchronized (em.unwrap(Server.class)) {
                diagnostics = (Diagnostics) session.getProperty(DIAGNOSTICS);
                if (diagnostics == null) {
                    diagnostics = new Diagnostics(session);
                    session.setProperty(DIAGNOSTICS, diagnostics);
                }
            }
        }

        return diagnostics;

    }

    protected Diagnostics(Server session) {
        this.log = session.getSessionLog();
        SessionLog logProxy = (SessionLog) Proxy.newProxyInstance(session.getPlatform().getConversionManager().getLoader(), new Class[] { SessionLog.class }, this);
        session.setSessionLog(logProxy);
    }

    public SessionLog getLog() {
        return this.log;
    }

    public SQLTrace getTrace() {
        return this.traces.get();
    }

    public SQLTrace start() {
        SQLTrace trace = getTrace();

        if (trace == null) {
            trace = new SQLTrace();
            this.traces.set(trace);
        }
        return trace;
    }

    public SQLTrace stop() {
        SQLTrace trace = getTrace();
        this.traces.set(null);
        return trace;
    }

    @Override
    public Object invoke(Object source, Method method, Object[] args) throws Throwable {
        SQLTrace trace = getTrace();

        if (trace != null && "log".equals(method.getName()) && args.length == 1) {
            SessionLogEntry entry = (SessionLogEntry) args[0];
            if (SessionLog.SQL.equals(entry.getNameSpace())) {
                trace.add(entry.getMessage());
            }
        }

        return method.invoke(getLog(), args);

    }

    public static class SQLTrace {

        private List<String> entries = new ArrayList<String>();

        protected void add(String entry) {
            this.entries.add(entry);
        }

        public List<String> getEntries() {
            return entries;
        }

    }
}
