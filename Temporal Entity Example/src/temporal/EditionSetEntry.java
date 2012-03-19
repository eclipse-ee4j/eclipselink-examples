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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.VariableOneToOne;

/**
 * An EditionSet represents a proposed set of future changes that should be
 * performed together at the same effective time. This is an optimisation in the
 * model to collect all changes for a future point and simplify committing them
 * all together as the current.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@Entity
@Table(name = "TEDITIONSET_ENTRY")
public class EditionSetEntry {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "EDITION_SET_ID", referencedColumnName = "ID")
    private EditionSet editionSet;

    /**
     * The {@link Temporal} or {@link TemporalEntity} that has been created or
     * modified
     */
    @VariableOneToOne(fetch = FetchType.LAZY)
    private Temporal temporal;

    /**
     * Set of attributes that have been modified in this edition.
     */
    @ElementCollection
    @CollectionTable(name = "TEDITIONSET_ENTRY_ATTR", joinColumns = @JoinColumn(name = "ID", referencedColumnName = "ID"))
    @Column(name = "ATTRIBUTE")
    private Set<String> attributes = new HashSet<String>();

    private EditionSetEntry() {
        super();
    }

    public EditionSetEntry(EditionSet editionSet, Temporal temporal) {
        this();
        this.editionSet = editionSet;
        this.temporal = temporal;
    }

    public long getId() {
        return id;
    }

    public Temporal getTemporal() {
        return temporal;
    }

    public TemporalEntity<?> getTemporalEntity() {
        return (TemporalEntity<?>) temporal;
    }

    public EditionSet getEditionSet() {
        return editionSet;
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String attr) {
        if (!getAttributes().contains(attr)) {
            getAttributes().add(attr);
        }
    }

    @Override
    public String toString() {
        return "EditionSetEntry[" + getTemporal() + "]";
    }

}
