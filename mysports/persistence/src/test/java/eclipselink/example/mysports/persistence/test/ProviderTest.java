package eclipselink.example.mysports.persistence.test;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;

import eclipselink.example.mysports.persistence.TenantPerEMFProvider;

/**
 * Unit test for simple App.
 */
public class ProviderTest {

	@Test
	public void testNullName() {
		new TenantPerEMFProvider().createEntityManagerFactory(null, null);
	}

	@Test
	public void testEmptyName() {
		new TenantPerEMFProvider().createEntityManagerFactory("", null);
	}

	@Test
	public void testJustSeparator() {
		try {
			new TenantPerEMFProvider().createEntityManagerFactory(";", null);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("IllegalArgumentException expected");
	}

	@Test
	public void testSeparatorEquals() {
		try {
			new TenantPerEMFProvider().createEntityManagerFactory(";=", null);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("IllegalArgumentException expected");

	}

	@Test
	public void testXSeparatorEquals() {
		try {
			new TenantPerEMFProvider().createEntityManagerFactory(";X=", null);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("IllegalArgumentException expected");

	}

	@Test
	public void testContextX() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML,
				"META-INF/test-persistence.xml");
		EntityManagerFactory emf = new TenantPerEMFProvider()
				.createEntityManagerFactory("test;context=X", props);

		Assert.assertNotNull(emf);
		Server session = JpaHelper.getServerSession(emf);

		Assert.assertEquals("test;context=X", session.getName());

		String context = (String) emf.getProperties().get("context");
		Assert.assertEquals(context, "X");
	}

}
