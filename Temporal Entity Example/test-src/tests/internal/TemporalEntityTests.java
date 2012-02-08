/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package tests.internal;



import org.junit.Assert;
import org.junit.Test;

import temporal.BaseEntity;
import temporal.Effectivity;
import temporal.TemporalEntity;
import static temporal.Effectivity.*;

/**
 * TODO
 *
 * @author dclarke
 * @since EclipseLink 2.3.1
 */

public class TemporalEntityTests {

    @Test
    public void verifyTestEntityConstructor() {
        TestEntity entity = new TestEntity();

        Assert.assertEquals(BOT, entity.getEffectivity().getStart());
    }

    @Test
    public void verifyTestEntityEditionConstructor() {
        TestEntityEdition entity = new TestEntityEdition();

        Assert.assertEquals(BOT, entity.getEffectivity().getStart());
    }

    public static class TestEntity extends BaseEntity implements TemporalEntity<TestEntity> {

        private Effectivity effectivity = new Effectivity();
        
        private TestEntity continuity;
        
        @Override
        public Effectivity getEffectivity() {
            return this.effectivity;
        }

        @Override
        public TestEntity getContinuity() {
            return this.continuity;
        }

        @Override
        public void setContinuity(TestEntity continuity) {
            this.continuity = continuity;
        }

    }

    public static class TestEntityEdition extends TestEntity {

    }
}
