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
import com.avbravo.jmoordbutils.jmoordbjsf.CSSTimeLine;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.EstatusRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
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
    private TimelineModel timelineSolicitudModel;
    private Date fechaDesde;
    private Date fechaHasta;
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="repository">
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    EstatusRepository estatusRepository;
    @Inject
    SolicitudRepository solicitudRepository;
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

    // <editor-fold defaultstate="collapsed" desc="String loadTimeLineVehiculo()">
    public String loadTimeLineVehiculo() {
        try {
            timelineModel = new TimelineModel();
            fechaDesde = DateUtil.dateFirtsOfMonth(DateUtil.anioActual(), DateUtil.mesActual());

            fechaHasta = DateUtil.dateLastOfMonth(DateUtil.anioActual(), DateUtil.mesActual());
// 
            // groups  
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("activo", "si"));

            if (list == null || list.isEmpty()) {

            } else {
                for (Vehiculo v : list) {

                    Document doc = new Document("activo", "si").append("vehiculo.idvehiculo", v.getIdvehiculo());
                    List<Viaje> viajeList = viajeRepository.findBy(doc, new Document("idviaje", -1));
                    if (viajeList == null || viajeList.isEmpty()) {

                    } else {
                         String color =CSSTimeLine.verde;;
                        for (Viaje vi : viajeList) {
                            String availability = (vi.getRealizado().equals("si") ? "Realizado" : (vi.getRealizado().equals("no") ? "NoRealizado" : "Cancelado"));
                            switch(vi.getRealizado()){
                                case "si":
                                   availability="Realizado";
                                   color =CSSTimeLine.verde;
                                   break;
                                case "no":
                                     availability="No Realizado";
                                   color =CSSTimeLine.naranja;
                                   break;
                                case "ca":
                                     availability="Cancelado";
                                  color =CSSTimeLine.rojo;
                                   break;
                            }
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
            fechaDesde = DateUtil.dateFirtsOfMonth(DateUtil.anioActual(), DateUtil.mesActual());

            fechaHasta = DateUtil.dateLastOfMonth(DateUtil.anioActual(), DateUtil.mesActual());
            // groups  
            List<Conductor> list = conductorRepository.findBy(new Document("activo", "si"));

            if (list == null || list.isEmpty()) {

            } else {
                for (Conductor c : list) {

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
    // <editor-fold defaultstate="collapsed" desc="String loadTimeLineSolicitud()">
    public String loadTimeLineSolicitud() {
         try {
            timelineSolicitudModel = new TimelineModel();
            fechaDesde = DateUtil.dateFirtsOfMonth(DateUtil.anioActual(), DateUtil.mesActual());

            fechaHasta = DateUtil.dateLastOfMonth(DateUtil.anioActual(), DateUtil.mesActual());
// 
            // groups  
            List<Estatus> list = estatusRepository.findBy(new Document("activo", "si"));

            if (list == null || list.isEmpty()) {

            } else {
                for (Estatus e : list) {

                    Document doc = new Document("activo", "si").append("estatus.idestatus", e.getIdestatus());
                    List<Solicitud> solicitudList = solicitudRepository.findBy(doc, new Document("idsolicitud", -1));
                    if (solicitudList == null || solicitudList.isEmpty()) {

                    } else {
                        for (Solicitud s : solicitudList) {
                            String availability = s.getEstatus().getIdestatus();
                            TimelineEvent event = new TimelineEvent(availability, s.getFechahorapartida(), s.getFechahoraregreso(), true, s.getEstatus().getIdestatus(), availability.toLowerCase());
                            timelineSolicitudModel.add(event);
                        }
                    }
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "/pages/timeline/timelinesolicitud.xhtml";
    }
    // </editor-fold>  
}
