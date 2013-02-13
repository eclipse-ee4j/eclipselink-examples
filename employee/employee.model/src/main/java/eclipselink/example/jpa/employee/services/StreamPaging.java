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
package eclipselink.example.jpa.employee.services;

import java.util.Collections;
import java.util.List;

import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.ScrollableCursor;

/**
 * Example of using an EclipseLink {@link CursoredStream} to page query results.
 * Generally a stream is used to process query results in memory avoiding the
 * cost of loading all results before starting. This example of using a stream
 * for paging may not be
 * 
 * @since EclipseLink 2.4.2
 */
public class StreamPaging<T> extends EntityPaging<T> {

    private ScrollableCursor stream;

    public StreamPaging(TypedQuery<T> query, int pageSize) {
        super(null, pageSize);

        query.setHint(QueryHints.SCROLLABLE_CURSOR, HintValues.TRUE);

        this.stream = (ScrollableCursor) query.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> next() {
        if (!this.stream.hasNext()) {
            return Collections.emptyList();
        }
        int quantity = getPageSize();
        if ((quantity + this.stream.getPosition() - 1) > this.stream.size()) {
            quantity = this.stream.size() - this.stream.getPosition() + 1;
        }
        List<T> entities = (List<T>) this.stream.next(quantity);
        this.currentPage++;
        return entities;
    }

    @Override
    public List<T> previous() {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public List<T> get(int page) {
        if (page == getCurrentPage() + 1) {
            return next();
        }
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public int size() {
        return this.stream.size();
    }

    @Override
    public int getNumPages() {
        return (size() / getPageSize()) + (size() % getPageSize() > 0 ? 1 : 0);
    }

    /**
     * Release the stream and its corresponding resources.
     */
    public void close() {
        if (this.stream != null) {
            this.stream.close();
            this.stream = null;
        }
    }

}
