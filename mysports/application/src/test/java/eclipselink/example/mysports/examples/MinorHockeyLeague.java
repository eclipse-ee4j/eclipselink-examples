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

public class MinorHockeyLeague extends ExampleLeague {
    
    public MinorHockeyLeague() {
        super(ExampleLeagueDefinition.MHL);
    }

    private final static String[][] teamNames = { { "Novice", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Atom", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "PeeWee", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Bantam", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Midget", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, { "Juvenile", "Aces", "Bruins", "Capitals", "Devils", "Eagles", "Flyers", "Giants", "" }, };

    public void populate(LeagueRepository repository) {
    	repository.setLeagueId(getId(), null);

    	for (int divIndex = 0; divIndex < teamNames.length; divIndex++) {
            Division div = repository.addDivision(teamNames[divIndex][0]);
            for (int teamIndex = 1; teamIndex < teamNames[divIndex].length; teamIndex++) {
                repository.addTeam(teamNames[divIndex][teamIndex], createPlayers(16), div);
            }
            getDivisions().add(div);
        }
    }

    private static final String[] POSITIONS = new String[] { "R", "L", "C", "D" };

    private List<Player> createPlayers(int num) {
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

}
