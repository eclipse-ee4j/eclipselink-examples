/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
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
package example.mysports.tests.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Attribute;

import junit.framework.Assert;

import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import example.mysports.MySportsConfig;
import example.mysports.ejb.LeagueRepository;
import example.mysports.model.Division;
import example.mysports.model.Player;
import example.mysports.tests.TestingLeagueRepository;
import example.mysports.tests.admin.MockAdminServerConnector;
import example.mysports.tests.model.util.PlayerFactory;

/**
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class KidsFootballLeagueTests {

    public static final String LEAGUE = "KFL";

    private static LeagueRepository repository;
    
    private static MySportsConfig config;

    @Test
    public void verifyAdditionalAttributes() {
        List<Attribute<?, ?>> attrs = repository.getAdditionalAttributes(Player.class);

        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());

        Attribute<?, ?> mnAttr = attrs.get(0);
        Assert.assertEquals("position", mnAttr.getName());
        Assert.assertFalse(mnAttr.isAssociation());
        Assert.assertEquals(String.class, mnAttr.getJavaType());
        Assert.assertTrue(((AttributeImpl<?, ?>) mnAttr).getMapping().getAttributeAccessor().isVirtualAttributeAccessor());
    }

    @Test
    public void createDivisions() {
        Division mite = repository.addDivision("Mite");
        repository.addTeam("Scorpions", createPlayers(15), mite);
        repository.addTeam("Bears", createPlayers(15), mite);
        repository.addTeam("Dragons", createPlayers(15), mite);
        repository.addTeam("Gorillas", createPlayers(15), mite);
        repository.addTeam("Peaches", createPlayers(15), mite);

        Division tyke = repository.addDivision("Tyke");
        repository.addTeam("Blazers", createPlayers(28), tyke);
        repository.addTeam("Gladiators", createPlayers(28), tyke);
        repository.addTeam("Ducks", createPlayers(28), tyke);
        repository.addTeam("Pirates", createPlayers(28), tyke);
        repository.addTeam("Crusaders", createPlayers(28), tyke);

        Division mosquito = repository.addDivision("Mosquito");
        repository.addTeam("Blazers", createPlayers(28), mosquito);
        repository.addTeam("Gladiators", createPlayers(28), mosquito);
        repository.addTeam("Ducks", createPlayers(28), mosquito);
        repository.addTeam("Pirates", createPlayers(28), mosquito);
        repository.addTeam("Crusaders", createPlayers(28), mosquito);
}

    private static final String[] POSITIONS = new String[] { "Center", "Offensive Guard", "Offensive Tackle", "Tight End", "Wide Receiver", "Full Back", "Running Back", "Quarter Back", "Defensive End", "Defensive Tackle", "Nose Guard", "Linebacker", "Cornerback", "Safety" };

    private List<Player> createPlayers(int num) {
        List<Player> players = new ArrayList<Player>();
        Random random = new Random();

        for (int index = 0; index < num; index++) {
            Player p = PlayerFactory.createPlayer();
            p.setEmail(p.getFirstName() + "." + p.getLastName() + "@email.com");
            p.set("position", POSITIONS[random.nextInt(POSITIONS.length)]);
            players.add(p);
        }

        return players;
    }

    @BeforeClass
    public static void createEMF() {
        EntityManagerFactory adminEMF = Persistence.createEntityManagerFactory("mysports-admin", TestingLeagueRepository.get());
        config = new MySportsConfig();
        ((MockAdminServerConnector) config.getAdminConnector()).setEMF(adminEMF);

        repository = new TestingLeagueRepository(config);
        repository.setLeagueId(LEAGUE, null);

        List<Division> divisions = repository.getDivisions();

        if (!divisions.isEmpty()) {
            for (Division division : divisions) {
                repository.remove(division);
            }
        }
    }

    @AfterClass
    public static void closeEMF() {
        repository.close();
        ((MockAdminServerConnector) config.getAdminConnector()).getEMF().close();
    }
}
