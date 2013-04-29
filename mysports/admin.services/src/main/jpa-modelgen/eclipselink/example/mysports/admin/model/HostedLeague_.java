package eclipselink.example.mysports.admin.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-22T10:11:27.055-0500")
@StaticMetamodel(HostedLeague.class)
public class HostedLeague_ {
	public static volatile SingularAttribute<HostedLeague, String> id;
	public static volatile SingularAttribute<HostedLeague, String> name;
	public static volatile SingularAttribute<HostedLeague, String> colourScheme;
	public static volatile SingularAttribute<HostedLeague, String> logoUrl;
	public static volatile SingularAttribute<HostedLeague, String> datasourceName;
	public static volatile SingularAttribute<HostedLeague, Boolean> shared;
	public static volatile SingularAttribute<HostedLeague, DataIsolation> dataIsolation;
	public static volatile ListAttribute<HostedLeague, Extension> playerExtensions;
	public static volatile ListAttribute<HostedLeague, Extension> teamExtensions;
	public static volatile ListAttribute<HostedLeague, Extension> divisionExtensions;
	public static volatile SingularAttribute<HostedLeague, Long> version;
}
