package eclipselink.example.jpamoxy.simple;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import eclipselink.example.jpamoxy.simple.model.Customer;


public class Unmarshall {

	public static void main(String[] args) throws JAXBException {
		if (args.length != 1) {
			System.out.println("Error media type not specified.  Should be either 'json' or 'xml'");
		}
		
		String mediaType = args[0];
		// JAXB Unmarshall
		JAXBContext context = JAXBContext.newInstance(Customer.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty("eclipselink.media-type", "application/" + mediaType);
        unmarshaller.setProperty("eclipselink.json.include-root", false);
        unmarshaller.setProperty("eclipselink.json.wrapper-as-array-name", true);
		File file = new File("target/customer." + mediaType);
        StreamSource source = new StreamSource(file);
        JAXBElement<Customer> jaxbElement = unmarshaller.unmarshal(source, Customer.class);

		Customer customer = jaxbElement.getValue();

		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("customer");
		EntityManager em = emf.createEntityManager();

		em.getTransaction().begin();
		em.merge(customer);		
		em.getTransaction().commit();
		
		em.close();
		emf.close();
	}

}
