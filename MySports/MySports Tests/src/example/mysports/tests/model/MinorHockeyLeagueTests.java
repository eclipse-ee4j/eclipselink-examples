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

public class MinorHockeyLeagueTests {

    public static final String LEAGUE = "MHL";

    private static LeagueRepository repository;

    private static MySportsConfig config;

    @Test
    public void verifyDivisions() {
        
    }
    
    @Test
    public void verifyTeams() {
        
    }
    
    
    private final static String[][] teamNames = { { "Novice", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Atom", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "PeeWee", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Bantam", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Midget", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Juvenile", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, };

    private static void populate() {
        for (int divIndex = 0; divIndex < teamNames.length; divIndex++) {
            Division div = repository.addDivision(teamNames[divIndex][0]);
            for (int teamIndex = 1; teamIndex < teamNames[divIndex].length; teamIndex++) {
                repository.addTeam(teamNames[divIndex][teamIndex], createPlayers(16), div);
            }
        }
    }

    private static final String[] POSITIONS = new String[] { "R", "L", "C", "D" };

    private static List<Player> createPlayers(int num) {
        List<Player> players = new ArrayList<Player>();
        Random random = new Random();

        for (int index = 0; index < num; index++) {
            Player p = PlayerFactory.createPlayer();
            p.set("penaltyMinutes", random.nextInt(100));
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

        populate();
    }

    @AfterClass
    public static void closeEMF() {
        repository.close();
        ((MockAdminServerConnector) config.getAdminConnector()).getEMF().close();
    }
}
