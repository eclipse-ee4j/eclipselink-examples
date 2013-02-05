/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - initial 
 ******************************************************************************/
package services;

import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import model.Employee;

import org.eclipse.persistence.queries.CursoredStream;

/**
 * TODO
 * 
 * @since EclipseLink 2.4.2
 */
@Stateful
@LocalBean
public class EmployeeStream {

    private CursoredStream stream;
    
    public EmployeeStream() {
    }
    
    public void initialize() {
        
    }
    
    @SuppressWarnings("unchecked")
    public List<Employee> next(int size) {
        if (this.stream != null) {
            return this.stream.nextElements(size);
        }
        return Collections.emptyList();
    }
    
    public boolean hasNext() {
        return this.stream != null && this.stream.hasNext();
    }
    
    @Remove
    public void close() {
        if (this.stream != null) {
            this.stream.close();
        }
    }

}
