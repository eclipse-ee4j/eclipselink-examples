package eclipselink.example.jpamoxy.simple;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import eclipselink.example.jpamoxy.simple.model.Address;
import eclipselink.example.jpamoxy.simple.model.Customer;
import eclipselink.example.jpamoxy.simple.model.PhoneNumber;

public class InitDB {

	public static void main(String[] args) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
		properties.put("eclipselink.ddl-generation.output-mode", "database");
		properties.put("eclipselink.logging.level", "FINE");
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("customer", properties);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Customer customer = new Customer();
		customer.setId(1);
		em.persist(customer);
		customer.setFirstName("Woody");
		customer.setLastName("Allen");
		Address address = new Address();
		address.setCity("New York");
		address.setStreet("Central Park East");
		customer.setAddress(address);
		address.setCustomer(customer);
		PhoneNumber phone1 = new PhoneNumber();
		phone1.setType("home");
		phone1.setNum("512-555-1234");
		customer.addPhoneNumber(phone1);
		PhoneNumber phone2 = new PhoneNumber();
		phone2.setType("mobile");
		phone2.setNum("512-555-9999");
		customer.addPhoneNumber(phone2);
		em.getTransaction().commit();
		em.close();
		emf.close();
	}

}
