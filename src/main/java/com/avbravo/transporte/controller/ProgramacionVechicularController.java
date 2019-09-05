/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporte.beans.ProgramacionVehicular;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.ViajeServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class ProgramacionVechicularController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Date fechaDesde;
    private Date fechaHasta;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Viaje viaje = new Viaje();
    Viaje viajeSelected;
    Viaje viajeSearch = new Viaje();

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<ProgramacionVehicular> programacionVehicular = new ArrayList<>();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    ViajeRepository viajeRepository;

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    EstatusServices estatusServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    ViajeServices viajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    // </editor-fold>  

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return viajeRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ProgramacionVechicularController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(viajeRepository)
                    .withEntity(viaje)
                    .withService(viajeServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/viaje/details.jasper")
                    .withPathReportAll("/resources/reportes/viaje/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

            String action = "gonew";
            if (getAction() != null) {
                action = getAction();
            }

            if (action == null || action.equals("gonew") || action.equals("new") || action.equals("golist")) {
                //inicializar

            }
            if (action.equals("view")) {
                //view
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            programacionVehicular = new ArrayList<>();

            this.page = page;
            viajeDataModel = new ViajeDataModel(viajeList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage);
                    break;

                case "activo":
                    if (getValueSearch() != null) {
                        viajeSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", viajeSearch.getActivo());
                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage);
                    }
                    break;

                case "_betweendates":

                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si",
                            "fechahorainicioreserva", fechaDesde,
                            "fechahorafinreserva", fechaHasta,
                            page, rowPage, new Document("idviaje", -1));
                    break;

                default:
                    viajeList = viajeRepository.findPagination(page, rowPage);
                    break;
            }
            if (viajeList == null || viajeList.isEmpty()) {

            } else {
                for (Viaje v : viajeList) {
                    ProgramacionVehicular pv = new ProgramacionVehicular();
                    pv.setConductor(v.getConductor().getNombre());
                    pv.setFechahoraregreso(v.getFechahorainicioreserva());
                    pv.setFechahorasalida(v.getFechahorainicioreserva());
                    pv.setIdviaje(v.getIdviaje());
                    pv.setMarca(v.getVehiculo().getMarca());
                    pv.setModelo(v.getVehiculo().getModelo());
                    pv.setPlaca(v.getVehiculo().getPlaca());
                    pv.setNombredia(DateUtil.nameOfDay(pv.getFechahorasalida()));
                    pv.setResponsable(v.getRealizado());
                    pv.setActivo(v.getActivo());
                      pv.setMision(v.getMision());
                    //Datos de la solicitud
                    pv.setFechasolicitud(v.getFechahorainicioreserva());
                    pv.setNumerosolicitudes("");
                  
                    pv.setUnidad("");
                    pv.setResponsable("");
                    pv.setSolicita("");
                    Solicitud solicitud = new Solicitud();

                    Document search = new Document("viaje.idviaje", v.getIdviaje());
                    List<Solicitud> list = solicitudRepository.findBy(search);
                    if (list == null || list.isEmpty()) {

                    } else {
                        String unidad = "";
        
                        String numeroSolicitudes = "";
                        String responsable = "";
                        String solicita = "";
                        //Se recorren todas las solicitudes que tengan ese viaje asignado.
                        for (Solicitud s : list) {
                            pv.setFechasolicitud(s.getFecha());
                            for (Unidad u : s.getUnidad()) {
                                unidad += " " + u.getIdunidad();
                            }
                          
                            numeroSolicitudes += " " + String.valueOf(s.getIdsolicitud());
                            solicita += " " + s.getUsuario().get(0).getNombre();
                            responsable += " " + s.getUsuario().get(1).getNombre();
                        }
                        pv.setUnidad(unidad.trim());
              
                        pv.setNumerosolicitudes(numeroSolicitudes.trim());
                        pv.setResponsable(responsable.trim());
                        pv.setSolicita(solicita.trim());
                       
                    }
 programacionVehicular.add(pv);
                }
            }
            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return viajeServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return viajeServices.showHour(date);

    }// </editor-fold>

    public String columnColor(String realizado, String activo) {
        return viajeServices.columnColor(realizado, activo);
    }
}
