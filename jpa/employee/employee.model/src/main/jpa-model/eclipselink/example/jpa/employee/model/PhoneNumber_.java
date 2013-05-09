package eclipselink.example.jpa.employee.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-05-09T07:19:48.958-0400")
@StaticMetamodel(PhoneNumber.class)
public class PhoneNumber_ {
	public static volatile SingularAttribute<PhoneNumber, Integer> id;
	public static volatile SingularAttribute<PhoneNumber, String> type;
	public static volatile SingularAttribute<PhoneNumber, String> areaCode;
	public static volatile SingularAttribute<PhoneNumber, String> number;
	public static volatile SingularAttribute<PhoneNumber, Employee> owner;
}
