package eclipselink.example.jpa.employee.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-14T13:15:29.499-0500")
@StaticMetamodel(Project.class)
public class Project_ {
	public static volatile SingularAttribute<Project, Integer> id;
	public static volatile SingularAttribute<Project, String> name;
	public static volatile SingularAttribute<Project, String> description;
	public static volatile SingularAttribute<Project, Long> version;
	public static volatile SingularAttribute<Project, Employee> teamLeader;
}
