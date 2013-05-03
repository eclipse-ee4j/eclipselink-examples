package eclipselink.example.jpamoxy.roundtrip;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eclipselink.example.jpamoxy.roundtrip.model.Customer;


public class Marshall {

	public static void main(String[] args) throws JAXBException {
		if (args.length != 1) {
			System.out.println("Error media type not specified.  Should be either 'json' or 'xml'");
		}
		String mediaType = args[0];
		
		// JPA Query
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("customer");
		EntityManager em = emf.createEntityManager();
		Customer customer = em.find(Customer.class, 1l);
		em.close();
		emf.close();

		// JAXB Marshall
		JAXBContext context = JAXBContext.newInstance(Customer.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
        marshaller.setProperty("eclipselink.media-type", "application/" + mediaType);
        marshaller.setProperty("eclipselink.json.include-root", false);
        marshaller.setProperty("eclipselink.json.wrapper-as-array-name", true);
        File file = new File("target/customer." + mediaType);
		marshaller.marshal(customer, file);

	}

}
