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

import static temporal.Effectivity.BOT;

import org.junit.Assert;
import org.junit.Test;

import temporal.BaseTemporalEntity;
import temporal.TemporalEntity;

/**
 * Simple tests verifying the functionality of the interfaces and base classes
 * used in this temporal extension framework.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */

public class TemporalEntityTests {

    @Test
    public void verifyTestEntityConstructor() {
        TestEntity entity = new TestEntityImpl();

        Assert.assertNotNull(entity.getEffectivity());
        Assert.assertNull(entity.getContinuity());
        Assert.assertNull(entity.getPreviousEdition());
        Assert.assertEquals(0, entity.getVersion());
        Assert.assertEquals(BOT, entity.getEffectivity().getStart());
    }

    @Test
    public void verifyTestEntityEditionConstructor() {
        TestEntity entity = new TestEntityEdition();

        Assert.assertNotNull(entity.getEffectivity());
        Assert.assertNull(entity.getContinuity());
        Assert.assertNull(entity.getPreviousEdition());
        Assert.assertEquals(0, entity.getVersion());
        Assert.assertEquals(BOT, entity.getEffectivity().getStart());
    }

    @Test
    public void verifyTestEntityEditionViewConstructor() {
        TestEntity entity = new TestEntityEditionView();

        Assert.assertNotNull(entity.getEffectivity());
        Assert.assertNull(entity.getContinuity());
        Assert.assertNull(entity.getPreviousEdition());
        Assert.assertEquals(0, entity.getVersion());
        Assert.assertEquals(BOT, entity.getEffectivity().getStart());
    }

    @Test
    public void testIsContinuity() {
        TestEntity entity = new TestEntityEditionView();

        Assert.assertFalse(entity.isContinuity());

        entity.setId(1);
        entity.setContinuity(entity);

        Assert.assertTrue(entity.isContinuity());
    }

    /**
     * Static test classes
     */

    public static interface TestEntity extends TemporalEntity<TestEntity> {
        void setId(int id);
    }

    public static class TestEntityImpl extends BaseTemporalEntity<TestEntity> implements TestEntity {

        @Override
        public void setId(int id) {
            super.setId(id);
        }

    }

    public static class TestEntityEdition extends TestEntityImpl {

    }

    public static class TestEntityEditionView extends TestEntityEdition {

    }
}
