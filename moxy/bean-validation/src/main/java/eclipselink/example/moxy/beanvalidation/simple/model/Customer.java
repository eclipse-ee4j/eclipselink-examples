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
package eclipselink.example.moxy.beanvalidation.simple.model;

import java.util.List;
import javax.xml.bind.annotation.*;
import javax.validation.constraints.Size;

/*
 * Customer class with Bean Validation constraints
 *
 * @author johnclingan
 * @since EclipseLink 2.6.0
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    @XmlAttribute
    private int id;

    @Size(min=3, max=10)
    @XmlElement
    private String firstName;

    @XmlElement(nillable = true)
    private String lastName;
}
