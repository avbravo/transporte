/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.services;

import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import static com.avbravo.jmoordbutils.JsfUtil.nameOfClass;
import static com.avbravo.jmoordbutils.JsfUtil.nameOfMethod;
import com.avbravo.transporte.beans.DisponiblesBeans;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author avbravo
 */
@Stateless
public class DisponiblesServices {
  @Inject
    ErrorInfoServices errorServices;  
 // <editor-fold defaultstate="collapsed" desc="columnColorDisponibles(DisponiblesBeans disponiblesBeans) ">

    public String columnColorDisponibles(DisponiblesBeans disponiblesBeans) {
        String color = "black";
        try {
            if ((disponiblesBeans.getNumeroVehiculosSolicitados() > disponiblesBeans.getNumeroBuses()) || disponiblesBeans.getNumeroPasajerosSolicitados() > disponiblesBeans.getNumeroPasajeros()) {
                color = "red";
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return color;
    } // </editor-fold>
    
    
     // <editor-fold defaultstate="collapsed" desc="Boolean columnTieneBusesDisponibles(DisponiblesBeans disponiblesBeans)">

    /**
     * Indica si los buses disponibles coindicen con los recomendados
     *
     * @param disponiblesBeans
     * @return
     */
    public Boolean columnTieneBusesDisponibles(DisponiblesBeans disponiblesBeans) {
        Boolean disponible = true;
        try {
            if (disponiblesBeans.getBusesRecomendados() > disponiblesBeans.getNumeroBuses()) {
                disponible = false;
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return disponible;
    } // </editor-fold>
}
