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
 * TODO
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
     * TODO
     * 
     * @param emf
     * @return
     */
    public static Diagnostics getInstance(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        Server session = em.unwrap(Server.class);
        em.close();

        Diagnostics diagnostics = (Diagnostics) session.getProperty(DIAGNOSTICS);

        if (diagnostics == null) {
            synchronized (emf) {
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
            trace.add(entry);
        }

        return method.invoke(getLog(), args);

    }

    public static class SQLTrace {

        private List<SessionLogEntry> entries = new ArrayList<SessionLogEntry>();

        protected void add(SessionLogEntry entry) {
            if (SessionLog.SQL.equals(entry.getNameSpace())) {
                this.entries.add(entry);
            }
        }

        public List<SessionLogEntry> getEntries() {
            return entries;
        }

    }
}
