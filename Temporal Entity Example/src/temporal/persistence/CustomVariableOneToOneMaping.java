package temporal.persistence;

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

import java.util.Set;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;

/**
 * Custom {@link VariableOneToOneMapping} added to work-around bug in base
 * mapping with respect to field caching.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class CustomVariableOneToOneMaping extends VariableOneToOneMapping {

    public CustomVariableOneToOneMaping(VariableOneToOneMapping original) {
        setAttributeAccessor(original.getAttributeAccessor());
        setAttributeName(original.getAttributeName());
        setDescriptor(original.getDescriptor());
        setIndirectionPolicy(original.getIndirectionPolicy());
        setFields(original.getFields());
        setForeignKeyFields(original.getForeignKeyFields());
        setClassIndicatorAssociations(original.getClassIndicatorAssociations());
        setReferenceClass(original.getReferenceClass());
        setIsLazy(original.isLazy());
        setTypeField(original.getTypeField());
        setSourceToTargetQueryKeyFields(original.getSourceToTargetQueryKeyNames());
    }

    @Override
    public void collectQueryParameters(Set<DatabaseField> cacheFields) {
        super.collectQueryParameters(cacheFields);
        cacheFields.add(getTypeField());
    }

    private static final long serialVersionUID = 1L;
}
