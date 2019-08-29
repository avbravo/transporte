/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.interfaces.IError;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@RequestScoped
@Getter

@Setter

public class TimeLineController implements Serializable, IError {

    // <editor-fold defaultstate="collapsed" desc="fields">  
    private TimelineModel timelineModel;
    private TimelineModel timelineConductorModel;
    private Date fechaDesde;
    private Date fechaHasta;
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="repository">
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ViajeRepository viajeRepository;
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


    // <editor-fold defaultstate="collapsed" desc="String loadTimeLine()">
    public String loadTimeLineVehiculo() {
        try {
            timelineModel = new TimelineModel();
            // groups  
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("activo", "si"));

            if (list == null || list.isEmpty()) {

            } else {
                for (Vehiculo v : list) {
                    fechaDesde = DateUtil.primeraFechaAnio();
                    fechaHasta = DateUtil.ultimaFechaAnio();
//            Date end = new Date(fechaDesde.getTime() - 12 * 60 * 60 * 1000);  
                    Document doc = new Document("activo", "si").append("vehiculo.idvehiculo", v.getIdvehiculo());
                    List<Viaje> viajeList = viajeRepository.findBy(doc, new Document("idviaje", -1));
                    if (viajeList == null || viajeList.isEmpty()) {

                    } else {
                        for (Viaje vi : viajeList) {
                            String availability = (vi.getRealizado().equals("si") ? "Realizado" : (vi.getRealizado().equals("no") ? "NoRealizado" : "Cancelado"));
                            TimelineEvent event = new TimelineEvent(availability, vi.getFechahorainicioreserva(), vi.getFechahorafinreserva(), true, v.getMarca() + " " + v.getPlaca(), availability.toLowerCase());
                            timelineModel.add(event);
                        }
                    }
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
         return "/pages/timeline/timelinevehiculo.xhtml";
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="loadTimeLineConductor()">
    public String loadTimeLineConductor() {
        try {
            timelineConductorModel = new TimelineModel();
            // groups  
            List<Conductor> list = conductorRepository.findBy(new Document("activo", "si"));

            if (list == null || list.isEmpty()) {

            } else {
                for (Conductor c : list) {
                    fechaDesde = DateUtil.primeraFechaAnio();
                    fechaHasta = DateUtil.ultimaFechaAnio();
//            Date end = new Date(fechaDesde.getTime() - 12 * 60 * 60 * 1000);  
                    Document doc = new Document("activo", "si").append("conductor.idconductor", c.getIdconductor());
                    List<Viaje> viajeList = viajeRepository.findBy(doc, new Document("idviaje", -1));
                    if (viajeList == null || viajeList.isEmpty()) {

                    } else {
                        for (Viaje vi : viajeList) {
                            String availability = (vi.getRealizado().equals("si") ? "Realizado" : (vi.getRealizado().equals("no") ? "NoRealizado" : "Cancelado"));
                            TimelineEvent event = new TimelineEvent(availability, vi.getFechahorainicioreserva(), vi.getFechahorafinreserva(), true, c.getNombre() + " " + c.getCedula(), availability.toLowerCase());
                            timelineConductorModel.add(event);
                        }
                    }
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
     return "/pages/timeline/timelineconductor.xhtml";
    }

    // </editor-fold>  
}
