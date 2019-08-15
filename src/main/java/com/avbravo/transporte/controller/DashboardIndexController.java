/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.transporte.security.LoginController;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.bson.Document;
import org.primefaces.model.chart.PieChartModel;

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

    private PieChartModel pieModelSolicitud;
    private PieChartModel pieModelVistoBueno;
    @Inject
    FacultadRepository facultadRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    LoginController loginController;
    @Inject
    JmoordbResourcesFiles rf;

    Integer totalSolicitado;
    Integer totalAprobado;
    Integer totalRechazado;
    Integer totalCancelado;
    Integer totales;
    Integer totalVehiculos;
    Integer totalVehiculosActivos;
    Integer totalVehiculosInActivos;
    Integer totalVehiculosEnReparacion;
    
    Integer totalVistoBuenoPendiente;
    Integer totalVistoBuenoAprobado;
    Integer totalVistoBuenoCancelado;
    // </editor-fold>

    public PieChartModel getPieModelSolicitud() {
        return pieModelSolicitud;
    }

    
    public void setPieModelSolicitud(PieChartModel pieModelSolicitud) {
        this.pieModelSolicitud = pieModelSolicitud;
    }

    public PieChartModel getPieModelVistoBueno() {
        return pieModelVistoBueno;
    }

    public void setPieModelVistoBueno(PieChartModel pieModelVistoBueno) {
        this.pieModelVistoBueno = pieModelVistoBueno;
    }
    
    
    

    public VehiculoRepository getVehiculoRepository() {
        return vehiculoRepository;
    }

    public void setVehiculoRepository(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public Integer getTotalVistoBuenoPendiente() {
        return totalVistoBuenoPendiente;
    }

    public void setTotalVistoBuenoPendiente(Integer totalVistoBuenoPendiente) {
        this.totalVistoBuenoPendiente = totalVistoBuenoPendiente;
    }

    public Integer getTotalVistoBuenoAprobado() {
        return totalVistoBuenoAprobado;
    }

    public void setTotalVistoBuenoAprobado(Integer totalVistoBuenoAprobado) {
        this.totalVistoBuenoAprobado = totalVistoBuenoAprobado;
    }

    public Integer getTotalVistoBuenoCancelado() {
        return totalVistoBuenoCancelado;
    }

    public void setTotalVistoBuenoCancelado(Integer totalVistoBuenoCancelado) {
        this.totalVistoBuenoCancelado = totalVistoBuenoCancelado;
    }

    
    
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

    /**
     * Creates a new instance of DashboardController
     */
    public DashboardIndexController() {
    }

    // <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {

        calcularTotales();
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calcularTotales()">
    public void calcularTotales() {
        try {
            pieModelSolicitud = new PieChartModel();
            pieModelVistoBueno = new PieChartModel();
            switch (loginController.getRol().getIdrol()) {
                case "ADMINISTRADOR":
                case "SECRETARIA":
                case "SECRETARIO ADMINISTRATIVO":
                    totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO"));
                    totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO"));
                    totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO"));
                    totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO"));

                    break;
                case "COORDINADOR":
                    String descripcion = loginController.getUsuario().getUnidad().getIdunidad();
                    Document doc = new Document("descripcion", descripcion).append("activo", "si");
                    List<Facultad> list = facultadRepository.findBy(doc);
                    if (list == null || list.isEmpty()) {
                        totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO").append("usuario.username", loginController.getUsuario().getUsername()));
                        totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO").append("usuario.username", loginController.getUsuario().getUsername()));
                        totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO").append("usuario.username", loginController.getUsuario().getUsername()));
                        totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    //
                        totalVistoBuenoAprobado = 0;
                       totalVistoBuenoCancelado = 0;
                   totalVistoBuenoPendiente = 0;
                    

                    } else {
                        Facultad facultad = list.get(0);
                        totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO").append("facultad.idfacultad", facultad.getIdfacultad()));
                        totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO").append("facultad.idfacultad", facultad.getIdfacultad()));;
                        totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO").append("facultad.idfacultad", facultad.getIdfacultad()));
                        totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO").append("facultad.idfacultad", facultad.getIdfacultad()));
                        //
                        totalVistoBuenoAprobado = solicitudRepository.count(new Document("activo", "si").append("vistoBueno.aprobado", "si").append("facultad.idfacultad", facultad.getIdfacultad()));;
                        totalVistoBuenoCancelado = solicitudRepository.count(new Document("activo", "si").append("vistoBueno.aprobado", "no").append("facultad.idfacultad", facultad.getIdfacultad()));
                        totalVistoBuenoPendiente = solicitudRepository.count(new Document("activo", "si").append("vistoBueno.aprobado", "pe").append("facultad.idfacultad", facultad.getIdfacultad()));

                    }

                    break;

                default:

                    totalSolicitado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "SOLICITADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalAprobado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "APROBADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalRechazado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "RECHAZADO").append("usuario.username", loginController.getUsuario().getUsername()));
                    totalCancelado = solicitudRepository.count(new Document("activo", "si").append("estatus.idestatus", "CANCELADO").append("usuario.username", loginController.getUsuario().getUsername()));

            }
            totales = totalAprobado + totalCancelado + totalRechazado + totalSolicitado;

            //Grafica de solicitudes
            pieModelSolicitud.set("Solicitado", totalSolicitado);
            pieModelSolicitud.set("Aprobado", totalAprobado);
            pieModelSolicitud.set("Rechazado", totalRechazado);
            pieModelSolicitud.set("Cancelado", totalCancelado);

            pieModelSolicitud.setTitle("Solicitudes");
            pieModelSolicitud.setLegendPosition("w");
            pieModelSolicitud.setShowDatatip(true);
            //    pieModelSolicitud.setFill(false);
            pieModelSolicitud.setShowDataLabels(true);
            //  pieModelSolicitud.setDiameter(150);
            pieModelSolicitud.setShadow(false);
            
            //Grafica de Visto Bueno
 pieModelVistoBueno.set("Pendiente", totalVistoBuenoPendiente);
            pieModelVistoBueno.set("Aprobado", totalVistoBuenoAprobado);
            pieModelVistoBueno.set("Cancelado", totalVistoBuenoCancelado);
        

            pieModelVistoBueno.setTitle("Visto Bueno");
            pieModelVistoBueno.setLegendPosition("w");
            pieModelVistoBueno.setShowDatatip(true);
            //    pieModelSolicitud.setFill(false);
            pieModelVistoBueno.setShowDataLabels(true);
            //  pieModelSolicitud.setDiameter(150);
            pieModelVistoBueno.setShadow(false);
            
            //Vehiculos
            totalVehiculos = vehiculoRepository.findAll().size();
            totalVehiculosActivos = vehiculoRepository.count(new Document("activo", "si"));
            totalVehiculosInActivos = vehiculoRepository.count(new Document("activo", "no"));
            totalVehiculosEnReparacion = vehiculoRepository.count(new Document("enreparacion", "si"));
            totalVehiculosActivos -= totalVehiculosEnReparacion;
            
            
            
            

        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
    }
    // </editor-fold>

}
