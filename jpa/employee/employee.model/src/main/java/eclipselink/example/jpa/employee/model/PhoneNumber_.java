package eclipselink.example.jpa.employee.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-14T13:15:29.498-0500")
@StaticMetamodel(PhoneNumber.class)
public class PhoneNumber_ {
	public static volatile SingularAttribute<PhoneNumber, Integer> id;
	public static volatile SingularAttribute<PhoneNumber, String> type;
	public static volatile SingularAttribute<PhoneNumber, String> areaCode;
	public static volatile SingularAttribute<PhoneNumber, String> number;
	public static volatile SingularAttribute<PhoneNumber, Employee> owner;
}
