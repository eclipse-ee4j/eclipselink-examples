package eclipselink.example.mysports.application.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-01-08T16:39:46.138-0500")
@StaticMetamodel(Player.class)
public class Player_ {
	public static volatile SingularAttribute<Player, Integer> id;
	public static volatile SingularAttribute<Player, String> userid;
	public static volatile SingularAttribute<Player, String> firstName;
	public static volatile SingularAttribute<Player, String> lastName;
	public static volatile SingularAttribute<Player, String> email;
	public static volatile SingularAttribute<Player, Team> team;
	public static volatile SingularAttribute<Player, Integer> number;
	public static volatile SingularAttribute<Player, Long> version;
}
