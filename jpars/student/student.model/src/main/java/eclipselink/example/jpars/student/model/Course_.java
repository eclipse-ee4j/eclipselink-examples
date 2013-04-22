package eclipselink.example.jpars.student.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-10T19:07:10.592-0400")
@StaticMetamodel(Course.class)
public class Course_ {
	public static volatile SingularAttribute<Course, Long> id;
	public static volatile SingularAttribute<Course, String> name;
	public static volatile CollectionAttribute<Course, Student> students;
}
