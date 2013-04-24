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
public class OttawaSoccerLeague extends ExampleLeague {

    public Division u6;
    public Division u8;

	public OttawaSoccerLeague() {
        super(ExampleLeagueDefinition.OSL);
    }

    public void populate(LeagueRepository repository) {
    	repository.setLeagueId(getId(), null);

    	u6 = repository.addDivision("U6");
        repository.addTeam("Scorpions", createPlayers(11), u6);
        repository.addTeam("Bears", createPlayers(11), u6);
        repository.addTeam("Dragons", createPlayers(11), u6);
        repository.addTeam("Gorillas", createPlayers(11), u6);
        repository.addTeam("Peaches", createPlayers(11), u6);
        getDivisions().add(u6);

         u8 = repository.addDivision("U8");
        repository.addTeam("Blazers", createPlayers(12), u8);
        repository.addTeam("Gladiators", createPlayers(12), u8);
        repository.addTeam("Ducks", createPlayers(12), u8);
        repository.addTeam("Pirates", createPlayers(12), u8);
        repository.addTeam("Crusaders", createPlayers(12), u8);
        getDivisions().add(u8);
    }

    private static final String[] ALLERGIES = new String[] { "NONE", "NONE", "NONE", "PEANUT", "MILK" };

    private List<Player> createPlayers(int num) {
        List<Player> players = new ArrayList<Player>();
        Random random = new Random();

        for (int index = 0; index < num; index++) {
            Player p = PlayerFactory.createPlayer();
            p.setEmail(p.getFirstName() + "." + p.getLastName() + "@email.com");
            p.set("allergies", ALLERGIES[random.nextInt(ALLERGIES.length)]);
            players.add(p);
        }

        return players;
    }

}
