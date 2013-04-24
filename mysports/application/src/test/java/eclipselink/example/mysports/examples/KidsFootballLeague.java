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

/**
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class KidsFootballLeague extends ExampleLeague {

    public KidsFootballLeague() {
        super(ExampleLeagueDefinition.KFL);
    }

    public void populate(LeagueRepository repository) {
    	repository.setLeagueId(getId(), null);

    	Division mite = repository.addDivision("Mite");
        repository.addTeam("Scorpions", createPlayers(15), mite);
        repository.addTeam("Bears", createPlayers(15), mite);
        repository.addTeam("Dragons", createPlayers(15), mite);
        repository.addTeam("Gorillas", createPlayers(15), mite);
        repository.addTeam("Peaches", createPlayers(15), mite);
        getDivisions().add(mite);

        Division tyke = repository.addDivision("Tyke");
        repository.addTeam("Blazers", createPlayers(28), tyke);
        repository.addTeam("Gladiators", createPlayers(28), tyke);
        repository.addTeam("Ducks", createPlayers(28), tyke);
        repository.addTeam("Pirates", createPlayers(28), tyke);
        repository.addTeam("Crusaders", createPlayers(28), tyke);
        getDivisions().add(tyke);

        Division mosquito = repository.addDivision("Mosquito");
        repository.addTeam("Blazers", createPlayers(28), mosquito);
        repository.addTeam("Gladiators", createPlayers(28), mosquito);
        repository.addTeam("Ducks", createPlayers(28), mosquito);
        repository.addTeam("Pirates", createPlayers(28), mosquito);
        repository.addTeam("Crusaders", createPlayers(28), mosquito);
        getDivisions().add(mosquito);
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

}
