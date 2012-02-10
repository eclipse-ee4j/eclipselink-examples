/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package temporal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Base entity class which contains the identifier and optimistic locking
 * version state.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * The OID represents the unique identifier
     */
    @Id
    @GeneratedValue
    @Column(name = "OID")
    private int oid;

    @Version
    private long version;

    /**
     * Create new current
     */
    public BaseEntity() {
    }

    public int getId() {
        return oid;
    }

    public void setId(int id) {
        this.oid = id;
    }

    public long getVersion() {
        return version;
    }

}