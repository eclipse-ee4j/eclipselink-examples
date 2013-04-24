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
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.application.view;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class MySportsPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        if(facesContext.getViewRoot()==null){   
          try{   
              facesContext.getExternalContext().redirect("/");   
              facesContext.responseComplete();   
          } catch (IOException e){   
              e.printStackTrace();   
          }   
        }
    }

    @Override
    public void beforePhase(PhaseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public PhaseId getPhaseId() {
        // TODO Auto-generated method stub
        return null;
    }

}
