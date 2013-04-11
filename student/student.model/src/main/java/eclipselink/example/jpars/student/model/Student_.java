package eclipselink.example.jpars.student.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-10T19:07:10.593-0400")
@StaticMetamodel(Student.class)
public class Student_ {
	public static volatile SingularAttribute<Student, Long> id;
	public static volatile SingularAttribute<Student, String> name;
	public static volatile CollectionAttribute<Student, Course> courses;
}
