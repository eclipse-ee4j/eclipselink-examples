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
package eclipselink.example.jpa.employee.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.server.Server;

/**
 * {@link SessionLog} wrapper used to capture SQL.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class SQLCaptureSessionLog {

    public static SessionLogHandler getHandler(EntityManager em) {
        Server session = em.unwrap(Server.class);
        SessionLog log = session.getSessionLog();

        if (Proxy.isProxyClass(log.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(log);
            if (invocationHandler instanceof SessionLogHandler)
                return (SessionLogHandler) invocationHandler;
        }

        SessionLogHandler sessionLogHandler = new SessionLogHandler(log);
        SessionLog logProxy = (SessionLog) Proxy.newProxyInstance(session.getPlatform().getConversionManager().getLoader(), new Class[] { SessionLog.class }, sessionLogHandler);
        session.setSessionLog(logProxy);

        return sessionLogHandler;
    }

    public static class SessionLogHandler implements InvocationHandler {

        private SessionLog log;

        private ThreadLocal<SQLTrace> traces = new ThreadLocal<SQLTrace>();

        public SessionLogHandler(SessionLog log) {
            super();
            this.log = log;
        }

        public SessionLog getLog() {
            return log;
        }

        public SQLTrace getTrace() {
            return this.traces.get();
        }

        public SQLTrace start() {
            SQLTrace trace = new SQLTrace();
            this.traces.set(trace);
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

    }
}
