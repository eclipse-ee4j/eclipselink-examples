/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Oracle - initial impl
 ******************************************************************************/
package example;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CreateDatabase {

	public static void main(String[] args) throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
		properties.put("eclipselink.ddl-generation.output-mode", "database");
		properties.put("eclipselink.logging.level", "FINE");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				"relational-pu", properties);
		// Creating an EntityManager will trigger database login
		// and schema generation (because of the properties passed above)
		EntityManager em = emf.createEntityManager();
		em.close();
		emf.close();
	}
}
