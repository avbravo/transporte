/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.repository.SolicitudRepository;


import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.bson.Document;
// </editor-fold>

/**
 *
 * @author avbravo
 */
@Named
@ViewScoped
public class DashboardIndexController implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    

    @Inject
    SolicitudRepository solicitudRepository;
   

    @Inject
    ResourcesFiles rf;

    Integer totalSolicitado;
    Integer totalAprobado;
    Integer totalRechazado;
    Integer totalCancelado;
    Integer totales;
    // </editor-fold>

    public Integer getTotalCancelado() {
        return totalCancelado;
    }

    public void setTotalCancelado(Integer totalCancelado) {
        this.totalCancelado = totalCancelado;
    }

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardIndexController() {
    }

  
    public Integer getTotales() {
        return totales;
    }

    public void setTotales(Integer totales) {
        this.totales = totales;
    }

    
    
    
    
    public Integer getTotalSolicitado() {
        return totalSolicitado;
    }

    public void setTotalSolicitado(Integer totalSolicitado) {
        this.totalSolicitado = totalSolicitado;
    }

    public Integer getTotalAprobado() {
        return totalAprobado;
    }

    public void setTotalAprobado(Integer totalAprobado) {
        this.totalAprobado = totalAprobado;
    }

    public Integer getTotalRechazado() {
        return totalRechazado;
    }

    public void setTotalRechazado(Integer totalRechazado) {
        this.totalRechazado = totalRechazado;
    }

   
      // <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
       
calcularTotales();
    } // </editor-fold>

    

    // <editor-fold defaultstate="collapsed" desc="calcularTotales()">
    public void calcularTotales(){
        try {
            totalSolicitado = solicitudRepository.count(new Document("activo","si").append("estatus.idestatus","SOLICITADO"));
            totalAprobado = solicitudRepository.count(new Document("activo","si").append("estatus.idestatus","APROBADO"));
            totalRechazado =solicitudRepository.count(new Document("activo","si").append("estatus.idestatus","RECHAZADO"));
            totalCancelado =solicitudRepository.count(new Document("activo","si").append("estatus.idestatus","CANCELADO"));
            totales = totalAprobado+totalCancelado+totalRechazado+totalSolicitado;
        } catch (Exception e) {
            JsfUtil.errorMessage("calcularTotales() "+e.getLocalizedMessage());
        }
    }
    // </editor-fold>

}
