/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.application.test;

import java.util.List;
import java.util.Vector;

import javax.persistence.metamodel.Attribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.admin.model.Extension;
import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.application.model.Division;
import eclipselink.example.mysports.application.model.Player;
import eclipselink.example.mysports.examples.ExampleLeague;
import eclipselink.example.mysports.examples.HighTechHockey;
import eclipselink.example.mysports.examples.KidsFootballLeague;
import eclipselink.example.mysports.examples.MinorHockeyLeague;
import eclipselink.example.mysports.examples.OttawaSoccerLeague;

/**
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class TestExampleLeagues {

    static ExampleLeague HTHL = new HighTechHockey();
    static ExampleLeague KFL = new KidsFootballLeague();
    static ExampleLeague MHL = new MinorHockeyLeague();
    static OttawaSoccerLeague OSL = new OttawaSoccerLeague();

    @Test
    public void verifyHTHLDivisions() {
        repository.setLeagueId(HTHL.getId(), null);

        List<Division> divisions = repository.getDivisions();

        Assert.assertNotNull(divisions);
        Assert.assertEquals(2, divisions.size());

    }

    @Test
    public void verifyHTHLAdditionalAttributes() {
        repository.setLeagueId(HTHL.getId(), null);

        List<Attribute<?, ?>> attrs = repository.getAdditionalAttributes(Player.class);

        Assert.assertNotNull(attrs);
        Assert.assertEquals(HTHL.getDefinition().getLeague().getPlayerExtensions().size(), attrs.size());

        int index = 0;
        for (Extension ext : HTHL.getDefinition().getLeague().getPlayerExtensions()) {
            Attribute<?, ?> mnAttr = attrs.get(index++);
            Assert.assertEquals(ext.getName(), mnAttr.getName());
            Assert.assertFalse(mnAttr.isAssociation());
            Assert.assertEquals(ext.getJavaType(), mnAttr.getJavaType().getName());
            Assert.assertTrue(((AttributeImpl<?, ?>) mnAttr).getMapping().getAttributeAccessor().isVirtualAttributeAccessor());
        }
    }

    @Test
    public void verifyKFLMySportsSchema() throws Exception {
        repository.setLeagueId(KFL.getId(), null);
        Server session = repository.unwrap(Server.class);

        for (ClassDescriptor desc : session.getDescriptors().values()) {
            Object result = session.executeSQL("SELECT COUNT(*) FROM " + desc.getTableName());
            Assert.assertNotNull(result);
            @SuppressWarnings("unchecked")
            ArrayRecord record = (ArrayRecord) ((Vector<Object>) result).get(0);
            int intValue = ((Number) record.getValues().get(0)).intValue();
            Assert.assertTrue(intValue >= 0);
        }
    }

    @Test
    public void verifyKFLAdditionalAttributes() {
        repository.setLeagueId(KFL.getId(), null);
        HostedLeague leagueDef = KFL.getDefinition().getLeague();

        List<Attribute<?, ?>> attrs = repository.getAdditionalAttributes(Player.class);

        Assert.assertNotNull(attrs);
        Assert.assertEquals(leagueDef.getPlayerExtensions().size(), attrs.size());

        int index = 0;
        for (Extension ext : leagueDef.getPlayerExtensions()) {
            Attribute<?, ?> mnAttr = attrs.get(index++);
            Assert.assertEquals(ext.getName(), mnAttr.getName());
            Assert.assertFalse(mnAttr.isAssociation());
            Assert.assertEquals(String.class, mnAttr.getJavaType());
            Assert.assertTrue(((AttributeImpl<?, ?>) mnAttr).getMapping().getAttributeAccessor().isVirtualAttributeAccessor());
        }

    }

    @Test
    public void verifyMHLAdditionalAttributes() {
        repository.setLeagueId(MHL.getId(), null);
        HostedLeague leagueDef = MHL.getDefinition().getLeague();

        List<Attribute<?, ?>> attrs = repository.getAdditionalAttributes(Player.class);

        Assert.assertNotNull(attrs);
        Assert.assertEquals(leagueDef.getPlayerExtensions().size(), attrs.size());

        int index = 0;
        for (Extension ext : leagueDef.getPlayerExtensions()) {
            Attribute<?, ?> mnAttr = attrs.get(index++);
            Assert.assertEquals(ext.getName(), mnAttr.getName());
            Assert.assertFalse(mnAttr.isAssociation());
            Assert.assertEquals(String.class, mnAttr.getJavaType());
            Assert.assertTrue(((AttributeImpl<?, ?>) mnAttr).getMapping().getAttributeAccessor().isVirtualAttributeAccessor());
        }

    }

    @Test
    public void verifyMHLDivisions() {
        repository.setLeagueId(MHL.getId(), null);

    }

    @Test
    public void verifyMHLTeams() {
        repository.setLeagueId(MHL.getId(), null);

    }

    @Test
    public void verifyOSLAdditionalAttributes() {
        repository.setLeagueId(OSL.getId(), null);
        HostedLeague leagueDef = OSL.getDefinition().getLeague();

        List<Attribute<?, ?>> attrs = repository.getAdditionalAttributes(Player.class);

        Assert.assertNotNull(attrs);
        Assert.assertEquals(leagueDef.getPlayerExtensions().size(), attrs.size());

        int index = 0;
        for (Extension ext : leagueDef.getPlayerExtensions()) {
            Attribute<?, ?> mnAttr = attrs.get(index++);
            Assert.assertEquals(ext.getName(), mnAttr.getName());
            Assert.assertFalse(mnAttr.isAssociation());
            Assert.assertEquals(String.class, mnAttr.getJavaType());
            Assert.assertTrue(((AttributeImpl<?, ?>) mnAttr).getMapping().getAttributeAccessor().isVirtualAttributeAccessor());
        }
        
    }

    @Test
    public void loadOSL_U6Division() {
        repository.setLeagueId(OSL.getId(), null);
        
        Division division = repository.getDivision(OSL.u6.getName());

        Assert.assertNotNull(division);
        Assert.assertEquals(division.getName(), "U6");

    }

    private static TestingLeagueRepository repository;
    
    @BeforeClass
    public static void initialize() {
        repository = new TestingLeagueRepository();

        repository.createSharedMySportsSchema();
        repository.createLeagueTables(KFL.getId());

        HTHL.populate(repository);
        KFL.populate(repository);
        MHL.populate(repository);
        OSL.populate(repository);
    }
    
    @AfterClass
    public static void close() {
        repository.close();
    }
    
}
