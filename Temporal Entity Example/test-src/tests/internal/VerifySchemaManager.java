/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package tests.internal;

import junit.framework.Assert;

import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.Test;

import temporal.persistence.TemporalSchemaManager;
import tests.BaseTestCase;

/**
 * Verify the schema manager's table definitions
 */
public class VerifySchemaManager extends BaseTestCase {

    private SchemaManager getSchemaManager() {
        return (SchemaManager) getEMF().getProperties().get(SchemaManager.class.getName());
    }

    private TemporalSchemaManager getTemporalSchemaManager() {
        return (TemporalSchemaManager) getSchemaManager();
    }

    @Test
    public void verifySchemaManager() {
        SchemaManager sm = getSchemaManager();
        Assert.assertNotNull(sm);
        Assert.assertTrue(sm instanceof TemporalSchemaManager);
        Assert.assertSame(sm, getTemporalSchemaManager());
        Assert.assertNotNull(getTemporalSchemaManager().getTableCreator());
    }

    @Test
    public void verifyPhoneTable() {
        TableDefinition td = getTemporalSchemaManager().getTableDefinition("TPHONE");
        Assert.assertNotNull(td);

        ForeignKeyConstraint fkc = td.getForeignKeyMap().get("FK_TPHONE_PERSON_ID");
        Assert.assertNotNull(fkc);
        Assert.assertEquals("OID", fkc.getTargetFields().get(0));
    }

    @Test
    public void verifyPersonHobbyTable() {
        TableDefinition td = getTemporalSchemaManager().getTableDefinition("TPERSON_HOBBY");
        Assert.assertNotNull(td);

        ForeignKeyConstraint fkc = td.getForeignKeyMap().get("FK_TPERSON_HOBBY_PERSON_ID");
        Assert.assertNotNull(fkc);
        Assert.assertEquals("OID", fkc.getTargetFields().get(0));
    }

    @Test
    public void verifyPersonTable() {
        TableDefinition td = getTemporalSchemaManager().getTableDefinition("TPERSON");
        Assert.assertNotNull(td);

        ForeignKeyConstraint fkc = td.getForeignKeyMap().get("FK_TPERSON_ADDR_ID");
        Assert.assertNotNull(fkc);
        Assert.assertEquals("OID", fkc.getTargetFields().get(0));
    }
}
