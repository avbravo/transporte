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
import com.avbravo.jmoordb.pojos.JmoordbEmailMaster;
import com.avbravo.jmoordb.profiles.repository.JmoordbEmailMasterRepository;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.email.ManagerEmail;
import com.avbravo.jmoordbutils.pojos.Tiempo;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.entity.EstatusViaje;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.EstatusRepository;
import com.avbravo.transporteejb.repository.EstatusViajeRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.ConductorServices;
import com.avbravo.transporteejb.services.EstatusViajeServices;
import com.avbravo.transporteejb.services.NotificacionServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.avbravo.transporteejb.services.VistoBuenoSubdirectorAdministrativoServices;
import com.avbravo.transporteejb.services.VistoBuenoServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
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
public class CambiarVehiculoViajeController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Boolean validFechas = false;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private String mensajeWarning = "";
    private String mensajeWarningTitle = "";

    private Date fechaRestaurarSolicitudPartida = new Date();
    private Date fechaRestaurarSolicitudRegreso = new Date();
    private Date fechaPartidaNueva = new Date();
    private Date fechaRegresoNueva = new Date();

    Integer index = 0;
    Integer indexcc = 0;
    Integer indexbcc = 0;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    Date fechaDesde = new Date();
    Date fechaHasta = new Date();
    Tiempo tiempoIdaPartida = new Tiempo();
    Tiempo tiempoIdaRegreso = new Tiempo();
    Tiempo tiempoRegresoPartida = new Tiempo();
    Tiempo tiempoRegresoRegreso = new Tiempo();

    Date fechaInicialParaSolicitud = new Date();
    Date fechaFinalParaSolicitud = new Date();

    String lugarDestino = "";
    String comentarios = "";
    String activo = "";
    String realizado = "";
    Date fechaPartida = new Date();
    Date fechaHoraInicioReservaanterior = new Date();
    Date fechaHoraFinReservaAnterior = new Date();

    //Entity
    ManagerEmail managerEmail = new ManagerEmail();
    Viaje viaje = new Viaje();

    Viaje viajeIda = new Viaje();

    Conductor conductorSelected;
    Boolean iseditable = false;

    Conductor conductor = new Conductor();
    Vehiculo vehiculo = new Vehiculo();
    Vehiculo vehiculoNuevo = new Vehiculo();

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<Conductor> suggestionsConductor = new ArrayList<>();
    List<Vehiculo> suggestions = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Usuario> usuarioList = new ArrayList<>();

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
    UsuarioRepository usuarioRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ViajeRepository viajeRepository;
    @Inject
    JmoordbEmailMasterRepository jmoordbEmailMasterRepository;
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
    @Inject
    VistoBuenoServices vistoBuenoServices;
    @Inject
    VistoBuenoSubdirectorAdministrativoServices vistoBuenoSubdirectorAdministrativoServices;
    //List of Relations
    //Repository of Relations
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="notifications()">
    //Notification
    @Inject
    NotificacionServices notificacionServices;
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    @Inject
    @Push(channel = "notification")
    private PushContext push;
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
    public CambiarVehiculoViajeController() {
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

            if (action == null || action.equals("gonew") || action.equals("new")) {
                //  inicializar();
                EstatusViaje estatusViaje = new EstatusViaje();
                estatusViaje.setIdestatusviaje("IDA/REGRESO");
                Optional<EstatusViaje> optional = estatusViajeRepository.findById(estatusViaje);
                if (!optional.isPresent()) {

                }
                estatusViaje = optional.get();
                fechaInicialParaSolicitud = DateUtil.primerDiaDelMesActualConHoraMinutosSegundos(0, 1, 0);
                fechaFinalParaSolicitud = DateUtil.ultimoDiaDelMesActualConHoraMinutoSegundo(23, 59, 0);

            }
            if (action.equals("view")) {
                view();
            }
            if (action.equals("golist")) {

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String iniciarHoras()">
    public String iniciarHoras() {
        try {
            fechaInicialParaSolicitud = DateUtil.primerDiaDelMesActualConHoraMinutosSegundos(0, 1, 0);
            fechaFinalParaSolicitud = DateUtil.ultimoDiaDelMesActualConHoraMinutoSegundo(23, 59, 0);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return "";
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }

    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="handleSelectedVehiculo(SelectEvent event) ">

    public void handleSelectedVehiculo(SelectEvent event) {
        try {

            setSearchAndValue("vehiculo", vehiculo);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
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
                    //  viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;

                case "idviaje":
//                    if (getValueSearch() != null) {
//                        viajeSearch.setIdviaje(Integer.parseInt(getValueSearch().toString()));
//                        doc = new Document("idviaje", viajeSearch.getIdviaje());
//                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
//                    } else {
//                        viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
//                    }

                    break;
                case "activo":
//
//                    String activo = getValueSearch().toString();
//                    doc = new Document("activo", activo);
//                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;

                default:
                    //       viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;
            }

            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }

    }// </editor-fold>

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

            if (viajeIda.getFechahorainicioreserva() == null || viajeIda.getFechahorafinreserva() == null) {
                JsfUtil.warningMessage(rf.getMessage("warning.seleccionefechas"));
                JsfUtil.updateJSFComponent(":form:growl");
                return suggestions;
            }
            Boolean found = false;
            query = query.trim();
//            if (iseditable && noHayCambioFechaHoras()) {
//                suggestions.add(vehiculoSelected);
//            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = vehiculoRepository.findRegex(field, query, true, new Document(field, 1));

            if (!temp.isEmpty()) {
                List<Vehiculo> validos = new ArrayList<>();
                if (noHayCambioFechaHoras()) {
                    validos = temp.stream()
                            .filter(x -> viajeServices.isVehiculoActivoDisponibleSinSolicitud(x, viajeIda)).collect(Collectors.toList());

                } else {
                    validos = temp.stream()
                            .filter(x -> viajeServices.isVehiculoActivoDisponibleExcluyendoMismoViajeSinSolicitud(x, viajeIda)).collect(Collectors.toList());
                    //si cambia el vehiculo

                }

                if (validos.isEmpty()) {

                    return suggestions;
                }
                if (vehiculoList == null || vehiculoList.isEmpty()) {

                    validos.forEach((v) -> {
                        if (v.getIdvehiculo().equals(viajeIda.getVehiculo().getIdvehiculo())) {

                        } else {
                            suggestions.add(v);
                        }

                    }); //  validos.add(vehiculoSelected);

                    //   return validos;
                } else {
// REMOVERLOS SI YA ESTAN EN EL LISTADO

                    validos.forEach((v) -> {
                        Optional<Vehiculo> optional = vehiculoList.stream()
                                .filter(v2 -> v2.getIdvehiculo() == v.getIdvehiculo())
                                .findAny();
                        if (!optional.isPresent()) {
                            if (v.getIdvehiculo().equals(viajeIda.getVehiculo().getIdvehiculo())) {

                            } else {
                                suggestions.add(v);
                            }

                        }
                    });
//                  //Agrega solo los que tienen el tipo de datos  

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
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
    public List<Viaje> completeViajeRangoFechas(String query) {
        List<Viaje> suggestions = new ArrayList<>();
        List<Viaje> list = new ArrayList<>();

        try {
            if (fechaInicialParaSolicitud == null || fechaFinalParaSolicitud == null) {
                return suggestions;
            }

            list = viajeRepository.filterBetweenDate("activo", "si", "fechahorainicioreserva", fechaInicialParaSolicitud, "fechahorafinreserva", fechaFinalParaSolicitud, new Document("fechahorainicioreserva", 1));
            if (list == null || list.isEmpty()) {
                return list;
            }
            for (Viaje v : list) {
                if (v.getRealizado().equals("no")) {
                    suggestions.add(v);
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }

        return suggestions;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean noHayCambioFechaHoras()">
    private Boolean noHayCambioFechaHoras() {
        return fechaHoraInicioReservaanterior.equals(viajeIda.getFechahorainicioreserva()) && fechaHoraFinReservaAnterior.equals(viajeIda.getFechahorafinreserva());

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
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isVehiculoValid()", e.getLocalizedMessage(), e);
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
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage(), e);
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
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isSolicitudActivoDisponibleExcluyendoMismoViaje()", e.getLocalizedMessage(), e);
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

            if (viajeServices.conductorDisponible(conductor, viajeIda.getFechahorainicioreserva(), viajeIda.getFechahorafinreserva())) {
                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isConductorActivoDisponible", e.getLocalizedMessage(), e);
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

            if (viajeServices.conductorDisponibleExcluyendoMismoViaje(conductor, viajeIda.getFechahorainicioreserva(), viajeIda.getFechahorafinreserva(), viajeIda.getIdviaje())) {
                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="view()">
    public String view() {
        try {

         
            conductorSelected = viaje.getConductor();

            fechaHoraInicioReservaanterior = viaje.getFechahorainicioreserva();
            fechaHoraFinReservaAnterior = viaje.getFechahorafinreserva();
            iseditable = true;
            writable = true;

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }
        return "";
    }   // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String primerDia()">
    public String primerDia() {
        try {
            viaje.setFechahorainicioreserva(DateUtil.primerDiaDelMesActualConHoraMinutosSegundos(0, 1, 0));

            updateChangeDate();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return "";
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="String ultimoDia()">
    public String ultimoDia() {
        try {
            viaje.setFechahorafinreserva(DateUtil.ultimoDiaDelMesActualConHoraMinutoSegundo(23, 59, 0));
            updateChangeDate();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return "";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="String updateChangeDate()">
    public String updateChangeDate() {
        try {
            validFechas = false;

            if (viaje.getFechahorainicioreserva() == null || viaje.getFechahorafinreserva() == null) {

            } else {
                if (!viajeServices.isValidDates(viaje, false, rf.getMrb(), rf.getArb())) {
                    //return;
                } else {
                    validFechas = true;
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return "";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="calendarChangeListener(SelectEvent event)">
    public void calendarChangeListener(SelectEvent event) {
        try {

            updateChangeDate();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }

    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="calendarChangeListener(SelectEvent event)">

    public void calendarFechaSolicitudesChangeListener(SelectEvent event) {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeAllSolicitudParaCopiar(String query)">
    public List<Solicitud> completeSolicitudByEstatus(String query) {
        return solicitudServices.completeByEstatus(query, "SOLICITADO");

    }
    // </editor-fold>

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

        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    } // </editor-fold>

// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="handleSelectCopiarDesde(SelectEvent event)">
    public void handleSelectEstatusViaje(SelectEvent event) {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isSolicitudValida()">
    public Boolean isCambioValido() {
        try {
            if (vehiculoNuevo == null || vehiculoNuevo.getPlaca()== null|| vehiculoNuevo.getPlaca().endsWith("")) {
                return false;
            }
            if (!vehiculoNuevo.getIdvehiculo().equals(viajeIda.getVehiculo().getIdvehiculo())) {
                return true;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="save">
    @Override
    public String save() {
        try {

            if (viajeIda == null || viajeIda.getIdviaje() == null) {
                JsfUtil.warningMessage(rf.getMessage("warning.seleccionaviajeida"));
                return "";
            }

            if (vehiculoNuevo == null || vehiculoNuevo.getPlaca() == null || vehiculoNuevo.getPlaca().equals("")) {
                JsfUtil.warningMessage(rf.getMessage("warning.seleccioneunvehiculo"));
                return null;
            }
            viajeIda.setVehiculo(vehiculoNuevo);

            //Cambia el estatus del viaje ahora tiene asignado
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            viajeIda.setUserInfo(viajeRepository.generateListUserinfo(jmoordb_user.getUsername(), "update"));
            if (viajeRepository.update(viajeIda)) {
                //guarda el contenido anterior
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viajeIda.getIdviaje().toString(), jmoordb_user.getUsername(),
                        "uddate cambio de vehiculo", "viaje", viajeRepository.toDocument(viajeIda).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
            } else {
                JsfUtil.successMessage("save() " + viajeRepository.getException().toString());
                return "";
            }
            /**
             * actualiza total de km y total consumo del vehiculo
             */
         
            viajeIda = new Viaje();
            vehiculoNuevo = new Vehiculo();
            reset();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean validar(Viaje viaje)">
    private Boolean validar(Viaje viaje) {
        try {
            if (!viajeServices.isValid(viaje, rf.getMrb(), rf.getArb(), false)) {
                return false;
            }

            if (DateUtil.fechaMayor(viaje.getFechahorainicioreserva(), DateUtil.getFechaActual())) {
                if (!viajeServices.isValidDates(viaje, true, rf.getMrb(), rf.getArb())) {

                    return false;
                }
            } else {
                //Indica que es un viaje anterior que no se habia registrado 
                if (!viajeServices.isValidDates(viaje, true, rf.getMrb(), rf.getArb(), false)) {
                    return false;
                }
            }

            if (viaje.getKmestimados() < 0) {
                JsfUtil.warningMessage(rf.getMessage("warning.kmmenorcero"));
                return false;
            }
            if (viaje.getCostocombustible() < 0) {
                JsfUtil.warningMessage(rf.getMessage("warning.costocombustiblemenorcero"));
                return false;
            }

            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="metodo()">
    public Boolean showStaticMessage(String text) {
        try {
            if (text == null || text.equals("")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleSelectCopiarDesde(SelectEvent event)">
    public void handleSelectedViaje(SelectEvent event) {
        try {
            // System.out.println("ViajeIda conductor: " + viajeIda.getConductor());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }

}
