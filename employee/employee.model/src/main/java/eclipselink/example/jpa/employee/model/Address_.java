package eclipselink.example.jpa.employee.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-14T13:15:29.490-0500")
@StaticMetamodel(Address.class)
public class Address_ {
	public static volatile SingularAttribute<Address, Integer> id;
	public static volatile SingularAttribute<Address, String> city;
	public static volatile SingularAttribute<Address, String> country;
	public static volatile SingularAttribute<Address, String> province;
	public static volatile SingularAttribute<Address, String> postalCode;
	public static volatile SingularAttribute<Address, String> street;
}
