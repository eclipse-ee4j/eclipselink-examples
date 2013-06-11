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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Customer;
import model.Order;
import model.OrderLine;
import relational.model.Product;

public class PolyglotDemo {

    EntityManagerFactory emf;
    String orderId;
    
    public static void main(String[] args) throws Exception {
        PolyglotDemo demo = new PolyglotDemo();
        demo.emf = Persistence.createEntityManagerFactory("composite-pu");

        demo.run();
        demo.emf.close();
    }

	public void run() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Create Three Products--RELATIONAL
        Product installation = new Product("installation");
        em.persist(installation);
        Product shipping = new Product("shipping");
        em.persist(shipping);
        Product machine = new Product("machine");
        em.persist(machine);
        Product net = new Product("net");
        em.persist(net);

        // Customer--NOSQL
        Customer customer = new Customer();
        customer.setName("AMCE");
        em.persist(customer);
        // Order
        Order order = new Order();
        order.setCustomer(customer);
        order.setDescription("Pinball machine");

        // Order Lines--NOSQL->RELATIONAL
        order.addOrderLine(new OrderLine(machine, 2999));
        order.addOrderLine(new OrderLine(shipping, 129));
        order.addOrderLine(new OrderLine(installation, 59));
        em.persist(order);
        
        em.getTransaction().commit();
        orderId = order.getId();
        em.close();
        emf.getCache().evictAll();
        
        em = emf.createEntityManager();
        Order queriedOrder = em.find(Order.class, orderId);
        System.out.println(queriedOrder.getDescription());
        for (OrderLine orderLine : queriedOrder.getOrderLines()) {
			System.out.println("  " + orderLine.getProduct().getDescription());
		}
        
        System.out.flush();
        
        em.close();
    }
}
