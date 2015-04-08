/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package eclipselink.example.moxy.beanvalidation.simple;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;


import eclipselink.example.moxy.beanvalidation.simple.model.Customer;

/**
 * Main example execution.
 * 
 * @author johnclingan
 * @since EclipseLink 2.6.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("Running EclipseLink MOXy Bean Validation Example");
        System.out.println("See target/schema1.xsd for XML Element constraints");

        JavaModelImpl javaModel = new JavaModelImpl(Thread.currentThread().getContextClassLoader());
        JavaModelInputImpl modelInput = new JavaModelInputImpl(new Class[] { Customer.class}, javaModel);
        modelInput.setFacets(true);
        Generator gen = new Generator(modelInput);
        gen.generateSchemaFiles("target", null);

        System.out.println();
        System.out.println();
    }

}
