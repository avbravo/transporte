/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

import com.avbravo.jmoordb.interfaces.IError;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author avbravo
 */
@Named
@RequestScoped
@Getter
@Setter
public class DisponiblesController implements Serializable, IError{
    // <editor-fold defaultstate="collapsed" desc="field">
      private static final long serialVersionUID = 1L;
    // </editor-fold>  
      
      // <editor-fold defaultstate="collapsed" desc="services">  
    @Inject
    ErrorInfoServices errorServices;

    // </editor-fold>  
    
      
      
        // <editor-fold defaultstate="collapsed" desc="init">
      
      
    @PostConstruct
    public void init() {
        try {
            //   loadTimeLineVehiculo();
//    timelineModel.toString(); 
//          timelineConductorModel.toString();
        } catch (Exception e) {
           errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
    
}
