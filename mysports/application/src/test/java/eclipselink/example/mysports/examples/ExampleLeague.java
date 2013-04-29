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

import eclipselink.example.mysports.admin.examples.ExampleLeagueDefinition;
import eclipselink.example.mysports.application.model.Division;
import eclipselink.example.mysports.application.services.LeagueRepository;

public abstract class ExampleLeague {

    private ExampleLeagueDefinition definition;
    
    private List<Division> divisions = new ArrayList<Division>();

    public ExampleLeague(ExampleLeagueDefinition definition) {
        super();
        this.definition = definition;
    }

    public ExampleLeagueDefinition getDefinition() {
        return definition;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public abstract void populate(LeagueRepository repository);

    public boolean usesSharedSchema() {
        return getDefinition().getLeague().isShared();
    }
    
    public String getId() {
        return getDefinition().getLeague().getId();
    }
}