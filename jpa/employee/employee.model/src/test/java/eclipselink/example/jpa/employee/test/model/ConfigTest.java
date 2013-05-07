/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - Employee Demo 2.4
 ******************************************************************************/
package eclipselink.example.jpa.employee.test.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.test.PersistenceTesting;

public class ConfigTest {

    @Test
    public void bootstrap() {
        EntityManager em = getEmf().createEntityManager();
        
        em.close();
    }

  private static EntityManagerFactory emf;

  public static EntityManagerFactory getEmf() {
      return emf;
  }

  @BeforeClass
  public static void createEMF() {
      emf = PersistenceTesting.createEMF(true);
  }

  @AfterClass
  public static void closeEMF() {
      if (emf != null && emf.isOpen()) {
          emf.close();
      }
      emf = null;
  }

}
