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
import com.avbravo.transporteejb.repository.VehiculoRepository;

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
    VehiculoRepository vehiculoRepository;

    @Inject
    LoginController loginController;
    @Inject
    ResourcesFiles rf;

    Integer totalSolicitado;
    Integer totalAprobado;
    Integer totalRechazado;
    Integer totalCancelado;
    Integer totales;
    Integer totalVehiculos;
    Integer totalVehiculosActivos;
    Integer totalVehiculosInActivos;
    Integer totalVehiculosEnReparacion;
    // </editor-fold>

    public Integer getTotalCancelado() {
        return totalCancelado;
    }

    public void setTotalCancelado(Integer totalCancelado) {
        this.totalCancelado = totalCancelado;
    }

    public Integer getTotalVehiculos() {
        return totalVehiculos;
    }

    public void setTotalVehiculos(Integer totalVehiculos) {
        this.totalVehiculos = totalVehiculos;
    }

    public Integer getTotalVehiculosActivos() {
        return totalVehiculosActivos;
    }

    public Integer getTotalVehiculosInActivos() {
        return totalVehiculosInActivos;
    }

    public void setTotalVehiculosInActivos(Integer totalVehiculosInActivos) {
        this.totalVehiculosInActivos = totalVehiculosInActivos;
    }
    
    

    public void setTotalVehiculosActivos(Integer totalVehiculosActivos) {
        this.totalVehiculosActivos = totalVehiculosActivos;
    }

    public Integer getTotalVehiculosEnReparacion() {
        return totalVehiculosEnReparacion;
    }

    public void setTotalVehiculosEnReparacion(Integer totalVehiculosEnReparacion) {
        this.totalVehiculosEnReparacion = totalVehiculosEnReparacion;
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
    public void calcularTotales() {
        try {
            switch (loginController.getRol().getIdrol()) {
                case "ADMINISTRADOR":
                case "SECRETARIA":
                    totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO"));
                    totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO"));
                    totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO"));
                    totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO"));

                    break;

                default:

                    totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO").append("usuario.username", loginController.getUsuario().getUsername()));

            }
            totales = totalAprobado + totalCancelado + totalRechazado + totalSolicitado;
            //Vehiculos
            totalVehiculos = vehiculoRepository.findAll().size();
            totalVehiculosActivos = vehiculoRepository.count(new Document("activo","si"));
            totalVehiculosInActivos = vehiculoRepository.count(new Document("activo","no"));
            totalVehiculosEnReparacion = vehiculoRepository.count(new Document("enreparacion","si"));
            

        } catch (Exception e) {
            JsfUtil.errorMessage("calcularTotales() " + e.getLocalizedMessage());
        }
    }
    // </editor-fold>

}
