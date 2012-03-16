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
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base entity class which contains the identifier and optimistic locking
 * version state.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@MappedSuperclass
public abstract class BaseTemporalEntity<T extends TemporalEntity<?>> extends BaseEntity {

    public BaseTemporalEntity() {
    }

    /**
     * M:1 relationship to continuity.
     */
    @Transient
    private T continuity;

    /**
     * M:1 relationship to continuity.
     */
    @Transient
    private T previousEdition;

    @Embedded
    private Effectivity effectivity = new Effectivity();
    
    @Column(name="CID", insertable=false, updatable=false)
    private int continuityId;

    public T getContinuity() {
        return this.continuity;
    }

    public void setContinuity(T continuity) {
        this.continuity = continuity;
        this.continuityId = continuity.getId();
    }

    public int getContinuityId() {
        return continuityId;
    }

    public Effectivity getEffectivity() {
        return this.effectivity;
    }

    public T getPreviousEdition() {
        return previousEdition;
    }

    public void setPreviousEdition(T previousEdition) {
        this.previousEdition = previousEdition;
    }
    
    public boolean isContinuity() {
        return getContinuity() != null &&  getId() == getContinuity().getId();
    }
    
    /**
     * TODO
     */
    @SuppressWarnings("rawtypes")
    public void applyEdition(TemporalEntity edition) {
        
    }
}