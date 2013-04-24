package eclipselink.example.mysports.application.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-01-08T16:39:46.140-0500")
@StaticMetamodel(Team.class)
public class Team_ {
	public static volatile SingularAttribute<Team, Integer> id;
	public static volatile SingularAttribute<Team, String> name;
	public static volatile SingularAttribute<Team, Division> division;
	public static volatile ListAttribute<Team, Player> players;
	public static volatile SingularAttribute<Team, Long> version;
}
