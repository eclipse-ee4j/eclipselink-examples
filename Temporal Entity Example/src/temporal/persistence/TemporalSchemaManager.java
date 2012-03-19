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
package temporal.persistence;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import temporal.TemporalHelper;

/**
 * TODO
 *
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalSchemaManager extends SchemaManager {

    public TemporalSchemaManager(Server session) {
        super(session);
    }

    protected TableCreator getDefaultTableCreator(boolean generateFKConstraints) {
        if (defaultTableCreator == null) {
            defaultTableCreator = new TemporalTableGenerator(session.getProject(), generateFKConstraints).generateDefaultTableCreator();
            // defaultTableCreator.setIgnoreDatabaseException(true);
            fixTemporalFKConstraints();
        }
        return defaultTableCreator;
    }
    
    public TableCreator getTableCreator() {
        return defaultTableCreator;
    }

    public TableDefinition getTableDefinition(String tableName) {
        for (@SuppressWarnings("unchecked")
        Iterator<Object> i = getTableCreator().getTableDefinitions().iterator(); i.hasNext();) {
            TableDefinition td = (TableDefinition) i.next();
            
            if (td.getName().equals(tableName)) {
                return td;
            }
        }
        return null;
    }
    
    /**
     * Replace all FK constraints referencing CID with OID
     */
    private void fixTemporalFKConstraints() {
        for (ClassDescriptor desc: getSession().getDescriptors().values()) {
            if (desc.getTableName() != null &&  TemporalHelper.isTemporal(desc.getJavaClass(), true)) {
                TableDefinition td = getTableDefinition(desc.getTableName());
                for (ForeignKeyConstraint fkc: td.getForeignKeys()) {
                    if (fkc.getTargetFields().size() == 1 && fkc.getTargetFields().get(0).equals("CID")) {
                        fkc.getTargetFields().set(0, "OID");
                    }
                }
            }
        }
    }
    

    class TemporalTableGenerator extends DefaultTableGenerator {

        public TemporalTableGenerator(Project project, boolean generateFKConstraints) {
            super(project, generateFKConstraints);
        }

        /**
         * Build direct collection table definitions in a EclipseLink descriptor
         */
        protected void buildDirectCollectionTableDefinition(DirectCollectionMapping mapping, ClassDescriptor descriptor) {
            TableDefinition tableDefinition = this.tableMap.get(mapping.getReferenceTable().getName());
            
            if (tableDefinition == null) {
                super.buildDirectCollectionTableDefinition(mapping, descriptor);
            }

        }

    }
}
