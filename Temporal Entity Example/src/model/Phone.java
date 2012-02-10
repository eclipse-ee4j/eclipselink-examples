/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package model;

import temporal.TemporalEntity;

/**
 * TODO
 *
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public interface Phone extends TemporalEntity<Phone> {

    public String getType();

    public void setType(String type);

    public String getNumber();

    public void setNumber(String number);

    public Person getPerson();

    public void setPerson(Person person);

}
