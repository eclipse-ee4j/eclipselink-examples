package eclipselink.example.jpa.employee.js;

import javax.ejb.Startup;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Startup
@Singleton
public class PersistenceWeavingBean {
	@PersistenceUnit
	private EntityManagerFactory emf;
}
