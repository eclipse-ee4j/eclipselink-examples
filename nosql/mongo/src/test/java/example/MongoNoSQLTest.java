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
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import model.Address;
import model.Customer;
import model.Order;
import model.OrderLine;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnection;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DB;

/**
 * Test performs CRUD operations on Orders persisting to a Mongo database.  For ease of use
 * with Maven this demo was converted into a Junit test.
 * 
 * @author James Sutherland
 * @author Shaun Smith
 */
public class MongoNoSQLTest {

    protected static EntityManagerFactory emf;
    protected String oid;
   
    @BeforeClass
    public static void initEMF() throws Exception {
        emf = Persistence.createEntityManagerFactory("mongo");
    }
    
    @AfterClass
    public static void closeEMF() throws Exception {
    	emf.close();
    }
    
    @Before
    public void initDatabase() throws Exception {
        EntityManager em = emf.createEntityManager();        
        // First clear old database.
        em.getTransaction().begin();
        DB db = ((MongoConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.dropDatabase();
        em.getTransaction().commit();
        em.close();
    }        

    @Test
    public void test() {
    	testPersist();
    	testFind();
    	testQuery();
    	testUpdate();
    	testRemove();
    }
    
    public void testPersist() {
        System.out.println("\nTesting persist() of orders and customers.\n"); 
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Customer customer = new Customer();
        customer.setName("AMCE");
        em.persist(customer);        
        Order order = new Order();
        order.setCustomer(customer);
        order.setDescription("Pinball machine");
        Address address = new Address();
        address.setStreet("17 Jane St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H7");
        order.setBillingAddress(address);
        address = new Address();
        address.setStreet("17 Jane St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H7");
        order.setShippingAddress(address);
        order.addOrderLine(new OrderLine("machine", 2999));
        order.addOrderLine(new OrderLine("shipping", 129));
        order.addOrderLine(new OrderLine("installation", 59));
        em.persist(order);
        
        order = new Order();
        order.setCustomer(customer);
        order.setDescription("Foosball");
        address = new Address();
        address.setStreet("7 Bank St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H8");
        order.setBillingAddress(address);
        address = new Address();
        address.setStreet("17 Jane St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H7");
        order.setShippingAddress(address);
        order.addOrderLine(new OrderLine("machine", 500));
        order.addOrderLine(new OrderLine("balls", 5));
        order.addOrderLine(new OrderLine("shipping", 60));
        em.persist(order);
        
        customer = new Customer();
        customer.setName("Smith");
        em.persist(customer);
        order = new Order();
        order.setCustomer(customer);
        order.setDescription("Pingpong table");
        address = new Address();
        address.setStreet("7 Bank St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H8");
        order.setBillingAddress(address);
        address = new Address();
        address.setStreet("17 Jane St.");
        address.setCity("Ottawa");
        address.setProvince("ON");
        address.setCountry("Canada");
        address.setPostalCode("L5J1H7");
        order.setShippingAddress(address);
        order.addOrderLine(new OrderLine("table", 300));
        order.addOrderLine(new OrderLine("balls", 5));
        order.addOrderLine(new OrderLine("rackets", 15));
        order.addOrderLine(new OrderLine("net", 2));
        order.addOrderLine(new OrderLine("shipping", 80));
        em.persist(order);
        
        em.getTransaction().commit();
        oid = order.getId();
        em.close();
    }

    public void testFind() {
        System.out.println("\nTesting find() by Id.\n");
        EntityManager em = emf.createEntityManager();
        Order order = em.find(Order.class, oid);
        System.out.println("Found order:" + order + " by its oid: " + oid);
        em.close();
    }

    public void testQuery() {
        System.out.println("\nTesting querying.\n");
        EntityManager em = emf.createEntityManager();
        TypedQuery<Order> query = em.createQuery("Select o from Order o where o.totalCost > 1000", Order.class);
        List<Order> orders = query.getResultList();
        System.out.println("\nFound orders with cost > 1,000: " + orders + "\n");
        query = em.createQuery("Select o from Order o where o.description like 'Pinball%'", Order.class);
        orders = query.getResultList();
        System.out.println("\nFound orders for pinball: " + orders + "\n");
        query = em.createQuery("Select o from Order o join o.orderLines l where l.description = :desc", Order.class);
        query.setParameter("desc", "shipping");
        orders = query.getResultList();
        System.out.println("\nFound orders with shipping charges: " + orders + "\n");

        Query nativeQuery = em.createNativeQuery("db.ORDER.findOne({\"_id\":\"" + oid + "\"})", Order.class);
        Order order = (Order)nativeQuery.getSingleResult();
        System.out.println("\nFound order using a native query: " + order + "\n");
        
        em.close();
    }

    public void testUpdate() {
        System.out.println("\nTesting update of order.\n");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Order order = em.find(Order.class, oid);
        order.addOrderLine(new OrderLine("handling", 55));
        order.addOrderLine(new OrderLine("tax", 300));
        em.getTransaction().commit();
        em.close();
    }

    public void testRemove() {
        System.out.println("\nTesting remove of order.\n");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Order order = em.find(Order.class, oid);
        em.remove(order);
        em.getTransaction().commit();
        em.close();
    }
}
