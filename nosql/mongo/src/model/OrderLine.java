/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package model;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 * OrderLine, stored as part of the Order document.
 * @author James Sutherland
 */
@Embeddable
@NoSql(dataFormat=DataFormatType.MAPPED)
public class OrderLine implements Serializable {
    @Basic
    private int lineNumber;
    @Basic
    private String description;
    @Basic
    private double cost = 0;

    public OrderLine() {
    }
    
    public OrderLine(String description, double cost) {
        this.description = description;
        this.cost = cost;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
