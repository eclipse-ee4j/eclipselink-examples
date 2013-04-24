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
package eclipselink.example.mysports.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eclipselink.example.mysports.admin.examples.ExampleLeagueDefinition;
import eclipselink.example.mysports.application.model.Division;
import eclipselink.example.mysports.application.model.Player;
import eclipselink.example.mysports.application.services.LeagueRepository;

public class HighTechHockey extends ExampleLeague {
    
    public HighTechHockey() {
        super(ExampleLeagueDefinition.HTHL);
    }

    public static final String LEAGUE = "HTHL";

    private static final String[] POSITIONS = new String[] { "R", "L", "C", "D" };

    public void populate(LeagueRepository repository) {
    	repository.setLeagueId(LEAGUE, null);
    	
        Division div1 = repository.addDivision("Div_1");
        repository.addTeam("Aces", createPlayers(12), div1);
        repository.addTeam("Leafs", createPlayers(12), div1);
        repository.addTeam("Pucks", createPlayers(12), div1);
        repository.addTeam("Meteors", createPlayers(12), div1);
        repository.addTeam("Rockets", createPlayers(12), div1);
        repository.addTeam("Jets", createPlayers(12), div1);
        repository.addTeam("Tigers", createPlayers(12), div1);
        repository.addTeam("Barons", createPlayers(12), div1);
        getDivisions().add(div1);
        
        Division div2 = repository.addDivision("Div_2");
        repository.addTeam("Slapshots", createPlayers(9), div2);
        repository.addTeam("Dragons", createPlayers(12), div2);
        repository.addTeam("Fury", createPlayers(12), div2);
        repository.addTeam("Cougars", createPlayers(12), div2);
        repository.addTeam("Flyers", createPlayers(12), div2);
        repository.addTeam("Lizards", createPlayers(12), div2);
        getDivisions().add(div2);
    }

    List<Player> createPlayers(int num) {
        List<Player> players = new ArrayList<Player>();
        Random random = new Random();

        for (int index = 0; index < num; index++) {
            Player p = PlayerFactory.createPlayer();
            p.set("penaltyMinutes", String.valueOf(random.nextInt(100)));
            p.set("position", POSITIONS[random.nextInt(POSITIONS.length)]);
            players.add(p);
        }

        return players;
    }

}
