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
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.services.RevisionHistoryServices;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.ConductorServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class ViajeController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Boolean validFechas=false;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    Date fechaDesde = new Date();
    Date fechaHasta = new Date();
    String lugarDestino = "";
    String comentarios = "";
    String activo = "";
    String realizado = "";
    Date fechaPartida = new Date();
    Date fechaHoraInicioReservaanterior = new Date();
    Date fechaHoraFinReservaAnterior = new Date();

    //Entity
    Viaje viaje = new Viaje();
    Viaje viajeSelected;
    Viaje viajeSearch = new Viaje();
    Vehiculo vehiculoSelected;
    Conductor conductorSelected;
    Boolean iseditable = false;

    Conductor conductor = new Conductor();
    Vehiculo vehiculo = new Vehiculo();

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<Conductor> suggestionsConductor = new ArrayList<>();
        List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();

// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">
    //Repository
    @Inject
    ConductorRepository conductorRepository;
       @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ViajeRepository viajeRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ConductorServices conductorServices;
    @Inject
    ErrorInfoServices errorServices;
     @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    ViajeServices viajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="schedule">
    //Schedule
    private ScheduleModel vehiculoScheduleModel;
    private ScheduleModel conductorScheduleModel;
    private ScheduleModel solicitudScheduleModel;
    private ScheduleModel viajeScheduleModel;

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return viajeRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajeController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            vehiculoScheduleModel = new DefaultScheduleModel();
            conductorScheduleModel = new DefaultScheduleModel();
            solicitudScheduleModel = new DefaultScheduleModel();
            viajeScheduleModel = new DefaultScheduleModel();
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
            String action = getAction();

            if (action == null || action.equals("gonew") || action.equals("new") || action.equals("golist")) {
             //  inicializar();
            }
            if (action.equals("view")) {
                view();
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

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = viajeServices.isDeleted(viaje);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = viajeServices.isDeleted(viaje);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return viajeServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return viajeServices.showHour(date);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="columnColor(String descripcion )"> 
    public String columnColor(String realizado, String activo) {
        return viajeServices.columnColor(realizado, activo);

    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleSelectedConductor(SelectEvent event) ">
    public void handleSelectedConductor(SelectEvent event) {
        try {

            setSearchAndValue("conductor", conductor);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="handleSelectedVehiculo(SelectEvent event) ">

    public void handleSelectedVehiculo(SelectEvent event) {
        try {

            setSearchAndValue("vehiculo", vehiculo);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            viajeDataModel = new ViajeDataModel(viajeList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;

                case "idviaje":
                    if (getValueSearch() != null) {
                        viajeSearch.setIdviaje(Integer.parseInt(getValueSearch().toString()));
                        doc = new Document("idviaje", viajeSearch.getIdviaje());
                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    }

                    break;
                case "activo":

                    String activo = getValueSearch().toString();
                    doc = new Document("activo", activo);
                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;

                case "realizado":

                    String realizado = (String) getValueSearch().toString();
                    viajeList = viajeRepository.findPagination(new Document("realizado", realizado), page, rowPage, new Document("idviaje", -1));

                    break;

                case "conductor":

                    Conductor conductor = (Conductor) getValueSearch();
                    doc = new Document("activo", "si");
                    doc.append("conductor.idconductor", conductor.getIdconductor());
                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;
                case "vehiculo":

                    Vehiculo vehiculo = (Vehiculo) getValueSearch();
                    doc = new Document("activo", "si");
                    doc.append("vehiculo.idvehiculo", vehiculo.getIdvehiculo());
                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;
                case "_betweendates":
                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si", "fechahorainicioreserva", fechaDesde, "fechahorafinreserva", fechaHasta, page, rowPage, new Document("idviaje", -1));

                    break;
                case "fechapartida":

                    viajeList = viajeRepository.filterDayWithoutHourPagination("activo", "si", "fechahorainicioreserva", fechaPartida, page, rowPage, new Document("idviaje", -1));

                case "lugardestino":
                    String lugarDestino = (String) getValueSearch();
                    viajeList = viajeRepository.findRegexInTextPagination("lugardestino", lugarDestino, true, page, rowPage, new Document("idviaje", -1));

                    break;
                case "comentarios":
                    String comentarios = (String) getValueSearch();
                    viajeList = viajeRepository.findRegexInTextPagination("comentarios", comentarios, true, page, rowPage, new Document("idviaje", -1));

                    break;
                default:
                    viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;
            }

            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="prepareScheduleSolicitud(()">
    public String prepareScheduleSolicitud() {
        try {
            vehiculo = new Vehiculo();
            conductor = new Conductor();
            Document doc;
            Document docViajes = new Document("activo", "si").append("estatus.idestatus", "SOLICITADO");
            doc = new Document("activo", "si");
            List<Solicitud> list = solicitudRepository.findBy(docViajes, new Document("fecha", 1));
            // String tema = "schedule-orange";
            solicitudScheduleModel = new DefaultScheduleModel();

            if (!list.isEmpty()) {
                list.forEach((a) -> {
                    String tipovehiculo = "";
                    tipovehiculo = a.getTipovehiculo().stream().map((tv) -> tv.getIdtipovehiculo()).reduce(tipovehiculo, String::concat);
                    String tema = "";
                    if (a.getTiposolicitud().getIdtiposolicitud().equals("DOCENTE")) {
                        tema = "schedule-green";
                    } else {
                        tema = "schedule-orange";
                    }
                    solicitudScheduleModel.addEvent(
                            new DefaultScheduleEvent(a.getUsuario().get(0).getNombre() + " " + a.getUsuario().get(0).getCedula()
                                    + "Tipo vehiculo " + tipovehiculo
                                    + "Solicitud " + a.getTiposolicitud().getIdtiposolicitud()
                                    + " Destino " + a.getLugarllegada(),
                                    a.getFechahorapartida(), a.getFechahoraregreso(), tema)
                    );
                });
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="prepareScheduleConductor()">
    public String prepareScheduleConductor() {
        try {
            Document doc;
            Document docViajes = new Document("activo", "si");
            doc = new Document("activo", "si");
            List<Viaje> list = viajeRepository.findBy(docViajes, new Document("fecha", 1));

            conductorScheduleModel = new DefaultScheduleModel();

            if (!list.isEmpty()) {
                list.forEach((a) -> {

                    String tema = "schedule-blue";
                    switch (a.getRealizado()) {
//                        case "si":                            
//                            tema = "schedule-orange";
//                            break;
                        case "si":

                            tema = "schedule-green";
                            break;
                        case "no":

                            tema = "schedule-red";
                            break;
//                        case "talvez":
//
//                            tema = "schedule-red";
//                            break;
                    }

                    conductorScheduleModel.addEvent(
                            new DefaultScheduleEvent(a.getConductor().getNombre() + " " + a.getConductor().getCedula()
                                    + "# " + a.getIdviaje() + " Destino " + a.getLugardestino()
                                    + " " + a.getVehiculo().getMarca() + " " + a.getVehiculo().getModelo(),
                                    a.getFechahorainicioreserva(), a.getFechahorafinreserva(), tema)
                    );
                });
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="prepareScheduleVehiculo()">
    public String prepareScheduleVehiculo() {
        try {
            Document doc;
            Document docViajes = new Document("activo", "si");
            doc = new Document("activo", "si");
            List<Viaje> list = viajeRepository.findBy(docViajes, new Document("fecha", 1));

            vehiculoScheduleModel = new DefaultScheduleModel();

            if (!list.isEmpty()) {
                list.forEach((a) -> {

                    String tema = "schedule-blue";
                    switch (a.getRealizado()) {
//                        case "si":                            
//                            tema = "schedule-orange";
//                            break;
                        case "si":

                            tema = "schedule-green";
                            break;
                        case "no":

                            tema = "schedule-red";
                            break;
//                        case "talvez":
//
//                            tema = "schedule-red";
//                            break;
                    }

                    vehiculoScheduleModel.addEvent(
                            new DefaultScheduleEvent(a.getVehiculo().getMarca()
                                    + " " + a.getVehiculo().getModelo() + " Placa: "
                                    + a.getVehiculo().getPlaca() + "# " + a.getIdviaje() + " Destino " + a.getLugardestino()
                                    + " Conductor " + a.getConductor().getNombre(),
                                    a.getFechahorainicioreserva(), a.getFechahorafinreserva(), tema)
                    );
                });
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="completeVehiculo(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Vehiculo> completeVehiculo(String query) {
        List<Vehiculo> suggestions = new ArrayList<>();
        List<Vehiculo> temp = new ArrayList<>();

        try {
            Boolean found = false;
            query = query.trim();
            if (iseditable && noHayCambioFechaHoras()) {
                suggestions.add(vehiculoSelected);
            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = vehiculoRepository.findRegex(field, query, true, new Document(field, 1));

            if (!temp.isEmpty()) {
                List<Vehiculo> validos = new ArrayList<>();
                if (noHayCambioFechaHoras()) {
                    validos = temp.stream()
                            .filter(x -> isVehiculoActivoDisponible(x)).collect(Collectors.toList());

                } else {
                    validos = temp.stream()
                            .filter(x -> isVehiculoActivoDisponibleExcluyendoMismoViaje(x)).collect(Collectors.toList());
                    //si cambia el vehiculo

                }

                if (validos.isEmpty()) {

                    return suggestions;
                }
                if (vehiculoList == null || vehiculoList.isEmpty()) {

                    validos.forEach((v) -> {
                        suggestions.add(v);
                    }); //  validos.add(vehiculoSelected);

                    //   return validos;
                } else {
// REMOVERLOS SI YA ESTAN EN EL LISTADO

                    validos.forEach((v) -> {
                        Optional<Vehiculo> optional = vehiculoList.stream()
                                .filter(v2 -> v2.getIdvehiculo() == v.getIdvehiculo())
                                .findAny();
                        if (!optional.isPresent()) {
                            suggestions.add(v);
                        }
                    });

                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestions;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="completeConductor(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Conductor> completeConductor(String query) {
        suggestionsConductor = new ArrayList<>();
        List<Conductor> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
            if (iseditable && conductorSelected.getEscontrol().equals("no") && noHayCambioFechaHoras()) {
                suggestionsConductor.add(conductorSelected);
            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = conductorRepository.findRegex(field, query, true, new Document(field, 1));

            if (!temp.isEmpty()) {
                List<Conductor> validos = new ArrayList<>();
                if (noHayCambioFechaHoras()) {
                    validos = temp.stream()
                            .filter(x -> isConductorActivoDisponible(x)).collect(Collectors.toList());
                } else {
                    validos = temp.stream()
                            .filter(x -> isConductorActivoDisponibleExcluyendoMismoViaje(x)).collect(Collectors.toList());
                }

                if (validos.isEmpty()) {

                    return suggestionsConductor;
                }
                if (conductorList == null || conductorList.isEmpty()) {
                    validos.forEach((v) -> {
                        suggestionsConductor.add(v);
                    });

                } else {
                    validos.forEach((v) -> {
                        Optional<Conductor> optional = conductorList.stream()
                                .filter(v2 -> v2.getIdconductor() == v.getIdconductor())
                                .findAny();
                        if (!optional.isPresent()) {
                            if (iseditable && conductorSelected.getEscontrol().equals("no")) {
                                suggestionsConductor.add(conductorSelected);
                            }
                            suggestionsConductor.add(v);
                        }
                    });
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }
        return suggestionsConductor;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean noHayCambioFechaHoras()">
    private Boolean noHayCambioFechaHoras() {
        return fechaHoraInicioReservaanterior.equals(viaje.getFechahorainicioreserva()) && fechaHoraFinReservaAnterior.equals(viaje.getFechahorafinreserva());

    }
    // </editor-fold>

     // <editor-fold defaultstate="collapsed" desc="isVehiculoValid(Vehiculo vehiculo)">
    public Boolean isVehiculoValid(Vehiculo vehiculo) {
        return vehiculo.getActivo().equals("si") && vehiculo.getEnreparacion().equals("no");

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isVehiculoActivo(Vehiculo vehiculo)">
    public Boolean isVehiculoActivo(Vehiculo vehiculo) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("si") && vehiculo.getEnreparacion().equals("no")) {

                valid = true;

            }

        } catch (Exception e) {
//            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isVehiculoValid()", e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isVehiculoActivoDisponible(Vehiculo vehiculo)">
    public Boolean isVehiculoActivoDisponible(Vehiculo vehiculo) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("no") && vehiculo.getEnreparacion().equals("si")) {

            } else {
                if (viajeServices.vehiculoDisponible(vehiculo, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva())) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isVehiculoActivoDisponibleExcluyendoMismoViaje(Vehiculo vehiculo)">
    public Boolean isVehiculoActivoDisponibleExcluyendoMismoViaje(Vehiculo vehiculo) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("no") && vehiculo.getEnreparacion().equals("si")) {

            } else {
                if (viajeServices.vehiculoDisponibleExcluyendoMismoViaje(vehiculo, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva(), viaje.getIdviaje())) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isVehiculoValid()", e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isConductorActivoDisponible(Conductor conductor)">
    public Boolean isConductorActivoDisponible(Conductor conductor) {
        Boolean valid = false;
        try {
            if (conductor.getActivo().equals("si") && conductor.getEscontrol().equals("si")) {
                return true;
            }

            if (viajeServices.conductorDisponible(conductor, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva())) {
                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isConductorActivoDisponible", e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isConductorActivoDisponibleExcluyendoMismoViaje(Conductor conductor)">
    public Boolean isConductorActivoDisponibleExcluyendoMismoViaje(Conductor conductor) {
        Boolean valid = false;
        try {
            if (conductor.getActivo().equals("si") && conductor.getEscontrol().equals("si")) {
                return true;
            }

            if (viajeServices.conductorDisponibleExcluyendoMismoViaje(conductor, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva(), viaje.getIdviaje())) {
                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="view()">
    public String view() {
        try {
            viajeSelected = viaje;
            vehiculoSelected = viaje.getVehiculo();
            conductorSelected = viaje.getConductor();

            fechaHoraInicioReservaanterior = viaje.getFechahorainicioreserva();
            fechaHoraFinReservaAnterior = viaje.getFechahorafinreserva();
            iseditable = true;
            writable = true;

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }
        return "";
    }   // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
       
            if (!viajeServices.isValid(viaje)) {
                return "";
            }
 if (!viajeServices.isValidDates(viaje, true)) {
                    return "";
                }
              
            if (!viajeServices.vehiculoDisponible(viaje)) {
                JsfUtil.warningMessage(rf.getMessage("warning.vehiculoenviajefechas"));
                return null;
            }

            if (viaje.getConductor().getEscontrol().equals("no")) {
                if (!viajeServices.conductorDisponible(viaje)) {
                    JsfUtil.warningMessage(rf.getMessage("warning.conductoresenviajefechas"));
                    return null;
                }
            }

            Integer idviaje = autoincrementableServices.getContador("viaje");
            viaje.setIdviaje(idviaje);
            viaje.setRealizado("no");
            viaje.setActivo("si");

            //Lo datos del usuario
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            viaje.setUserInfo(viajeRepository.generateListUserinfo( jmoordb_user.getUsername(), "create"));
            if (viajeRepository.save(viaje)) {
                //guarda el contenido anterior
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(),  jmoordb_user.getUsername(),
                        "create", "viaje", viajeRepository.toDocument(viaje).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + viajeRepository.getException().toString());
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    
    
    
    // <editor-fold defaultstate="collapsed" desc="calendarChangeListener(SelectEvent event)">
    public void calendarChangeListener(SelectEvent event) {
        try {
            validFechas=false;
            viaje.setVehiculo(new Vehiculo());
            viaje.setConductor(new Conductor());
//verifica si hay buses disponibles
JsfUtil.warningMessage("actualizalo"+ viaje.getVehiculo());
PrimeFaces.current().ajax().update(":form:vehiculo");
PrimeFaces.current().ajax().update(":form:conductor");
//PrimeFaces.current().ajax().update(":form:growl");
JsfUtil.updateJSFComponent(":form:growl");
            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {

            } else {
                if (!viajeServices.isValidDates(viaje, false)) {
                    return;
                }else{
                        validFechas=true;
                }
              

             
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>
}
