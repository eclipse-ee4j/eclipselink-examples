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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Customer;
import model.Order;

/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
public class CleanDatabases {

    EntityManagerFactory emf;
    String oid;
    
    public static void main(String[] args) throws Exception {
        CleanDatabases example = new CleanDatabases();
        example.emf = Persistence.createEntityManagerFactory("composite-pu");

        example.clean();
        example.emf.close();
    }

    private void clean() {
        EntityManager em = emf.createEntityManager();        
        // First clear old database.
        em.getTransaction().begin();
        List<Customer> customers = em.createQuery("select c from Customer c", Customer.class).getResultList();
        for (Customer customer : customers) {
			em.remove(customer);
		}
        List<Order> orders = em.createQuery("select o from Order o", Order.class).getResultList();
        for (Order order : orders) {
			em.remove(order);
		}
        em.createQuery("DELETE from Product p").executeUpdate();
        em.createQuery("DELETE from Discount d").executeUpdate();
        em.getTransaction().commit();
        em.close();
		
	}
}
