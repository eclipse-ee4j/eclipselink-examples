/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - initial 
 ******************************************************************************/
package eclipselink.example.jpa.employee.services.paging;

import java.util.List;

import javax.persistence.EntityManagerFactory;

/**
 * Base class offering common paging solution for 
 * 
 * @since EclipseLink 2.4.2
 */
public abstract class EntityPaging<T> {

    private EntityManagerFactory emf;

    private int pageSize;

    protected int currentPage = 0;

    protected EntityPaging(EntityManagerFactory emf, int pageSize) {
        super();
        this.emf = emf;
        this.pageSize = pageSize;
    }

    protected EntityManagerFactory getEmf() {
        return this.emf;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public abstract int getNumPages();

    /**
     * Retrieve a page of Employee instances.
     */
    public abstract List<T> get(int page);

    public abstract int size();
    
    public boolean hasNext() {
        return getCurrentPage() < getNumPages();
    }

    public List<T> next() {
        if (!hasNext()) {
            throw new IllegalStateException("Next page not available");
        }
        return get(++this.currentPage);
    }
    
    public boolean hasPrevious() {
        return getCurrentPage() > 1;
    }

    public List<T> previous() {
        if (!hasPrevious()) {
            throw new IllegalStateException("Previous page not available");
        }
        return get(--this.currentPage);
    }
    
    public enum Type { NONE, PAGE, PAGE_IN }
}
