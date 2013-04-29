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
package eclipselink.example.mysports.admin.services.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import eclipselink.example.mysports.admin.services.HostedLeagueRepositoryBean;

/**
 * TODO
 *
 * @since EclipseLink 2.4.2
 * @author dclarke
 */
class HostedLeagueRepositoryHandler implements InvocationHandler {
    
    private EntityManagerFactory emf;
    
    private HostedLeagueRepositoryBean repositoryBean;

    protected HostedLeagueRepositoryHandler(EntityManagerFactory emf) {
        super();
        this.emf = emf;
        this.repositoryBean = new HostedLeagueRepositoryBean();
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    HostedLeagueRepositoryBean getRepositoryBean() {
        return repositoryBean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EntityManager em = getEmf().createEntityManager();
        
        try {
            em.getTransaction().begin();
            getRepositoryBean().setEntityManager(em);
            return method.invoke(getRepositoryBean(), args);
        } catch (InvocationTargetException ite) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ite.getTargetException();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            em.close();
            getRepositoryBean().setEntityManager(null);
        }
    }

}
