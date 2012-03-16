/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/

package temporal.persistence;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

import temporal.TemporalEntityManager;
import temporal.TemporalHelper;

/**
 * This {@link QueryRedirector} intercepts read queries on the current types and
 * if there is an effective time property (
 * {@link TemporalHelper#EFF_TS_PROPERTY}) set on the persistence context then
 * the query is executed against the edition descriptor.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@SuppressWarnings("serial")
public class TemporalQueryRedirector implements QueryRedirector {

    private ClassDescriptor currentDescriptor;

    private ClassDescriptor editionDescriptor;

    public TemporalQueryRedirector(ClassDescriptor current, ClassDescriptor editionDesc) {
        this.currentDescriptor = current;
        this.editionDescriptor = editionDesc;
    }

    @Override
    public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
        DatabaseQuery queryToExecute = query;
        TemporalEntityManager tem = (TemporalEntityManager) session.getProperty(TemporalEntityManager.TEMPORAL_EM_PROPERTY);

        if (tem != null && tem.getEffectiveTime() != null && query.isObjectLevelReadQuery() && query.getReferenceClass().equals(this.currentDescriptor.getJavaClass())) {
            if (query.isJPQLCallQuery()) {
                queryToExecute = EJBQueryImpl.buildEJBQLDatabaseQuery(convertJPQL(query.getJPQLString()), session);
            }
/*
            if (query.isReadObjectQuery() && ((ReadObjectQuery) query).isPrimaryKeyQuery()) {
                queryToExecute.setDoNotRedirect(true);
                Object current = ((AbstractSession) session).executeQuery(queryToExecute, (AbstractRecord) arguments);
                return tem.getEdition((TemporalEntity<?>) current);
            }
*/
        }
        queryToExecute.setDoNotRedirect(true);
        return ((AbstractSession) session).executeQuery(queryToExecute, (AbstractRecord) arguments);
    }

    // TODO: Replace the JPQL approach with a proper query construction
    private String convertJPQL(String jpql) {
        int index = jpql.indexOf(" " + this.currentDescriptor.getAlias() + " ");
        if (index > 0) {
            int endReplace = index + this.currentDescriptor.getAlias().length() + 1;
            String startJPQL = jpql.substring(0, index + 1);
            String endJPQL = jpql.substring(endReplace);
            return startJPQL + this.editionDescriptor.getAlias() + endJPQL;
        }
        return jpql;
    }

}
