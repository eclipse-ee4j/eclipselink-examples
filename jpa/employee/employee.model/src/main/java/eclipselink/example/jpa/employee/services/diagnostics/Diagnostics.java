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
package eclipselink.example.jpa.employee.services.diagnostics;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

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
@Singleton
@Startup
public class Diagnostics {

    private boolean enabled = true;

    private EntityManagerFactory emf;

    private ThreadLocal<SQLTrace> traces = new ThreadLocal<SQLTrace>();

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;

        EntityManager em = emf.createEntityManager();
        Server session = em.unwrap(Server.class);
        SessionLog original = session.getSessionLog();
        SessionLog logProxy = (SessionLog) Proxy.newProxyInstance(session.getPlatform().getConversionManager().getLoader(), new Class[] { SessionLog.class }, new SessionLogHandler(original));
        session.setSessionLog(logProxy);

        em.close();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SQLTrace getTrace() {
        return getTrace(false);
    }

    public SQLTrace getTrace(boolean close) {
        SQLTrace trace = this.traces.get();

        if (close) {
            clear();
        } else if (trace == null) {
            trace = new SQLTrace();
            this.traces.set(trace);
        }
        return trace;
    }

    public void clear() {
        this.traces.set(null);
    }

    private class SessionLogHandler implements InvocationHandler {

        private SessionLog sessionLog;

        private SessionLogHandler(SessionLog log) {
            this.sessionLog = log;
        }

        @Override
        public Object invoke(Object source, Method method, Object[] args) throws Throwable {
            if (isEnabled() && "log".equals(method.getName()) && args.length == 1) {
                SessionLogEntry entry = (SessionLogEntry) args[0];
                if (SessionLog.SQL.equals(entry.getNameSpace())) {
                    getTrace(false).add(entry.getMessage());
                }
            }

            return method.invoke(this.sessionLog, args);
        }

    }

    public static class SQLTrace {

        private List<String> entries = new ArrayList<String>();

        protected void add(String entry) {
            this.entries.add(entry);
        }

        public List<String> getEntries() {
            return entries;
        }

        public void truncate(int length, String message) {
            if (getEntries().size() > length) {
                this.entries = this.entries.subList(0, length - 1);
                this.entries.add(message);
            }
        }

    }
}
