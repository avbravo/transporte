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
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.entity.EstatusViaje;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.EstatusRepository;
import com.avbravo.transporteejb.repository.EstatusViajeRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.ConductorServices;
import com.avbravo.transporteejb.services.EstatusViajeServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.avbravo.transporteejb.services.VistoBuenoServices;

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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
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
    private Boolean validFechas = false;
    private ScheduleEvent event = new DefaultScheduleEvent();
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
    Solicitud solicitud = new Solicitud();
    Solicitud solicitudCopiar = new Solicitud();

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<Conductor> suggestionsConductor = new ArrayList<>();
    List<Vehiculo> suggestions = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();
    List<Solicitud> solicitudList = new ArrayList<>();

// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">
    //Repository
    @Inject
    EstatusRepository estatusRepository;
    @Inject
    EstatusViajeRepository estatusViajeRepository;
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
    VistoBuenoServices vistoBuenoServices;
    @Inject
    ConductorServices conductorServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    EstatusViajeServices estatusViajeServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    SolicitudServices solicitudServices;
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

            if (action == null || action.equals("gonew") || action.equals("new")  ) {
                //  inicializar();
                  EstatusViaje estatusViaje = new EstatusViaje();
                estatusViaje.setIdestatusviaje("IDA/REGRESO");
                Optional<EstatusViaje> optional = estatusViajeRepository.findById(estatusViaje);
                if (!optional.isPresent()) {
                  
                }
                estatusViaje = optional.get();
               viaje.setEstatusViaje(estatusViaje);
            }
            if (action.equals("view")) {
                view();
            }
            if(action.equals("golist")){
                
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
//            Optional<Vehiculo> v = vehiculoRepository.findFirst(new Document("activo","si"));
//            Vehiculo b = v.get();
//            viaje.setVehiculo(new Vehiculo());
            if (solicitud.getPasajeros() > viaje.getVehiculo().getPasajeros()) {
                JsfUtil.warningMessage(rf.getMessage("warning.capacidadvehiculomenorsolicitados"));
            }
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
                    if (a.getEstatus().getIdestatus().equals("CANCELADO") || a.getEstatus().getIdestatus().equals("RECHAZADO")) {
                        //No mostrarlas
                    } else {
                        solicitudScheduleModel.addEvent(
                                new DefaultScheduleEvent(
                                        "# " + a.getIdsolicitud() + " : ("
                                        + a.getUsuario().get(0).getNombre() + " " + a.getUsuario().get(0).getCedula()
                                        + "Tipo vehiculo " + tipovehiculo
                                        + "Solicitud " + a.getTiposolicitud().getIdtiposolicitud()
                                        + " Destino " + a.getLugarllegada()
                                        + "Estatus " + a.getEstatus().getIdestatus(),
                                        a.getFechahorapartida(), a.getFechahoraregreso(), tema)
                        );
                    }

                });
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="prepareScheduleViajes(()">
    public String prepareScheduleViajes() {
        try {
            vehiculo = new Vehiculo();
            conductor = new Conductor();
            Document doc;
            Document docViajes = new Document("activo", "si");
            doc = new Document("activo", "si");
            List<Viaje> list = viajeRepository.findBy(docViajes, new Document("fecha", 1));
            // String tema = "schedule-orange";
            viajeScheduleModel = new DefaultScheduleModel();

            if (!list.isEmpty()) {
                list.forEach((a) -> {

                    String tema = "";

                    tema = "schedule-green";

                    viajeScheduleModel.addEvent(
                            new DefaultScheduleEvent(
                                    a.getVehiculo().getMarca()
                                    + "Placa " + a.getVehiculo().getPlaca()
                                    + "Conductor " + a.getConductor().getNombre()
                                    + " Destino " + a.getLugardestino(),
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
//        List<Vehiculo> suggestions = new ArrayList<>();
        suggestions = new ArrayList<>();
        List<Vehiculo> temp = new ArrayList<>();

        try {
            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {
                return suggestions;
            }
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
                            .filter(x -> viajeServices.isVehiculoActivoDisponible(x, solicitud, viaje)).collect(Collectors.toList());

                } else {
                    validos = temp.stream()
                            .filter(x -> viajeServices.isVehiculoActivoDisponibleExcluyendoMismoViaje(x, solicitud, viaje)).collect(Collectors.toList());
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
//                  //Agrega solo los que tienen el tipo de datos  

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestions;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="List<Solicitud> completeSolicitudRangoFechas(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Solicitud> completeSolicitudRangoFechas(String query) {
        List<Solicitud> suggestions = new ArrayList<>();
        List<Solicitud> temp = new ArrayList<>();

        try {
            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {
                return suggestions;
            }
            Boolean found = false;
            query = query.trim();
            if (iseditable && noHayCambioFechaHoras()) {
                suggestions.add(solicitud);
            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = solicitudRepository.findRegex(field, query, true, new Document(field, 1));

            if (!temp.isEmpty()) {
                List<Solicitud> validos = new ArrayList<>();
                if (noHayCambioFechaHoras()) {
                    validos = temp.stream()
                            .filter(x -> isSolicitudActivoDisponible(x)).collect(Collectors.toList());

                } else {
                    validos = temp.stream()
                            .filter(x -> isSolicitudActivoDisponibleExcluyendoMismoViaje(x)).collect(Collectors.toList());
                    //si cambia la solicitud

                }

                if (validos.isEmpty()) {

                    return suggestions;
                }
                if (solicitudList == null || solicitudList.isEmpty()) {

                    validos.forEach((v) -> {
                        suggestions.add(v);
                    }); //  validos.add(vehiculoSelected);

                    //   return validos;
                } else {
// REMOVERLOS SI YA ESTAN EN EL LISTADO

                    validos.forEach((v) -> {
                        Optional<Solicitud> optional = solicitudList.stream()
                                .filter(v2 -> v2.getIdsolicitud() == v.getIdsolicitud())
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
            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {
                return suggestionsConductor;
            }
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
    // <editor-fold defaultstate="collapsed" desc="isSolicitudActivoDisponible(Solicitud solicitud)">
    /**
     *
     * @param solicitud
     * @return
     */
    public Boolean isSolicitudActivoDisponible(Solicitud solicitud) {
        Boolean valid = false;
        try {

            if (solicitud.getActivo().equals("no") && (solicitud.getEstatus().getIdestatus().equals("CANCELADO") || solicitud.getEstatus().getIdestatus().equals("RECHAZADO")
                    || solicitud.getEstatus().getIdestatus().equals("APROBADO"))) {

            } else {
                if (solicitudServices.solicitudDisponibleParaViajes(solicitud, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva())) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean isSolicitudActivoDisponibleExcluyendoMismoViaje(Solicitud solicitud)">
    public Boolean isSolicitudActivoDisponibleExcluyendoMismoViaje(Solicitud solicitud) {
        Boolean valid = false;
        try {

            if (solicitud.getActivo().equals("no") && (solicitud.getEstatus().getIdestatus().equals("CANCELADO") || solicitud.getEstatus().getIdestatus().equals("RECHAZADO")
                    || solicitud.getEstatus().getIdestatus().equals("APROBADO"))) {

            } else {
                if (solicitudServices.solicitudDisponibleExcluyendoMismoViaje(solicitud, viaje.getFechahorainicioreserva(), viaje.getFechahorafinreserva())) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isSolicitudActivoDisponibleExcluyendoMismoViaje()", e.getLocalizedMessage());
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
            viaje.setActivo("si");
            viaje.setAsientosdisponibles(viaje.getVehiculo().getPasajeros() - solicitud.getPasajeros());
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
            if (solicitud.getPasajeros() > viaje.getVehiculo().getPasajeros()) {
                JsfUtil.warningMessage(rf.getMessage("warning.capacidadvehiculomenorsolicitados"));
                return "";
            }
            List<Viaje> viajeList = new ArrayList<>();
            switch (viaje.getEstatusViaje().getIdestatusviaje()) {
                case "IDA/REGRESO":
                    viajeList.add(viaje);
                    viajeList.add(viaje);

                    break;
                case "IDA":
                    viajeList.add(viaje);
                    break;
                case "REGRESO":
                    if (solicitud.getViaje() == null || solicitud.getViaje().size() == 0) {
                        JsfUtil.warningMessage(rf.getMessage("warning.solicitudnotieneviajeida"));
                        return "";
                    }
                    viajeList = solicitud.getViaje();
                    viajeList.add(viaje);
                    break;
                case "NO ASIGNADO":
                    if (viajeList.size() == 0) {
                        JsfUtil.warningMessage(rf.getMessage("warning.seleccioneunestatusviaje"));
                        return "";
                    }
//
                    break;
                default:
                    JsfUtil.warningMessage(rf.getMessage("warning.indiquetipoviaje"));
                    return "";
            }

            if (!viaje.getVehiculo().getTipovehiculo().getIdtipovehiculo().equals(solicitud.getTipovehiculo().get(0).getIdtipovehiculo())) {
                JsfUtil.warningMessage(rf.getMessage("warning.tipovehiculonocoincideconeltipodelasolicitud"));
                return "";
            }
            Integer idviaje = autoincrementableServices.getContador("viaje");
            viaje.setIdviaje(idviaje);
            viaje.setRealizado("no");
            viaje.setActivo("si");

            //Lo datos del usuario
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            viaje.setUserInfo(viajeRepository.generateListUserinfo(jmoordb_user.getUsername(), "create"));
            if (viajeRepository.save(viaje)) {
                //guarda el contenido anterior
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), jmoordb_user.getUsername(),
                        "create", "viaje", viajeRepository.toDocument(viaje).toString()));
                /**
                 * //Actualizar los viajes  y el estatus  de la solicitud
                 *
                 */

                solicitud.setViaje(viajeList);
                solicitud.setEstatusViaje(viaje.getEstatusViaje());

                Estatus estatus = new Estatus();
                estatus.setIdestatus("APROBADO");
                Optional<Estatus> optional = estatusRepository.findById(estatus);
                if (!optional.isPresent()) {
                    JsfUtil.warningMessage(rf.getMessage("warning.noexisteestatuscancelado"));
                    return "";
                }
                estatus = optional.get();
                solicitud.setEstatus(estatus);
                
                if(solicitudRepository.update(solicitud)){
                     revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                        "update", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
                }else{
                    JsfUtil.warningMessage(rf.getMessage("warning.solicitudnoactualizada"));
                    return ""; 
                }

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

            validFechas = false;

            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {

            } else {
                if (!viajeServices.isValidDates(viaje, false)) {
                    return;
                } else {
                    validFechas = true;
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeAllSolicitudParaCopiar(String query)">
    public List<Solicitud> completeSolicitudByEstatus(String query) {
        return solicitudServices.completeByEstatus(query, "SOLICITADO");

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleSelectCopiarDesde(SelectEvent event)">
    public void handleSelectCopiarDesde(SelectEvent event) {
        try {

            // solicitud = solicitudServices.copiarDesde(solicitudCopiar, solicitud);
            viaje.setMision(solicitud.getMision());
            viaje.setComentarios("Responsable " + solicitud.getUsuario().get(1).getNombre() + " Destino " + solicitud.getLugarllegada());
            viaje.setFechahorainicioreserva(solicitud.getFechahorapartida());
            viaje.setFechahorafinreserva(solicitud.getFechahoraregreso());
            viaje.setLugarpartida(solicitud.getLugarpartida());
            viaje.setLugardestino(solicitud.getLugarllegada());
            completeVehiculo("");
            completeConductor("");
            JsfUtil.updateJSFComponent(":form:content");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onEventSelect(SelectEvent selectEvent)">
    public void onEventSelect(SelectEvent selectEvent) {
        try {

            event = (ScheduleEvent) selectEvent.getObject();

            String title = event.getTitle();
            Integer i = title.indexOf(":");

            Integer idsolicitud = 0;
            if (i != -1) {
                idsolicitud = Integer.parseInt(title.substring(1, i).trim());
            }
            solicitud.setIdsolicitud(idsolicitud);
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                solicitud = optional.get();
                viaje.setMision(solicitud.getMision());
                viaje.setComentarios("Responsable " + solicitud.getUsuario().get(1).getNombre() + " Destino " + solicitud.getLugarllegada());
                viaje.setFechahorainicioreserva(solicitud.getFechahorapartida());
                viaje.setFechahorafinreserva(solicitud.getFechahoraregreso());
                viaje.setLugarpartida(solicitud.getLugarpartida());
                viaje.setLugardestino(solicitud.getLugarllegada());

                JsfUtil.updateJSFComponent(":form:panel");
                JsfUtil.updateJSFComponent(":form:content");
            }
            JsfUtil.updateJSFComponent("solicitudDetallesPanel");
        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String columnNameVistoBueno()">
    public String columnNameVistoBueno() {
        return vistoBuenoServices.columnNameVistoBueno(solicitud.getVistoBueno());
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="handleSelectCopiarDesde(SelectEvent event)">

    public void handleSelectEstatusViaje(SelectEvent event) {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

}
