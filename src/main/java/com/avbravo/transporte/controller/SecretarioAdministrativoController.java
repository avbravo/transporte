/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.commonejb.services.SemestreServices;
import com.avbravo.jmoordb.configuration.JmoordbConfiguration;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.repository.AutoincrementablebRepository;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.repository.Repository;
import com.avbravo.jmoordb.pojos.JmoordbEmailMaster;
import com.avbravo.jmoordb.profiles.repository.JmoordbEmailMasterRepository;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.dates.FechaDiaUtils;
import com.avbravo.jmoordbutils.printer.Printer;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.dates.DecomposedDate;
import com.avbravo.jmoordbutils.email.ManagerEmail;
import com.avbravo.transporte.beans.DisponiblesBeans;
import com.avbravo.transporte.beans.TipoVehiculoCantidadBeans;
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.datamodel.SugerenciaDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.entity.EstatusViaje;
import com.avbravo.transporteejb.entity.Lugares;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Sugerencia;
import com.avbravo.transporteejb.entity.Tiposolicitud;
import com.avbravo.transporteejb.entity.Tipovehiculo;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.VistoBueno;
import com.avbravo.transporteejb.entity.VistoBuenoSecretarioAdministrativo;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.EstatusRepository;
import com.avbravo.transporteejb.repository.EstatusViajeRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.SugerenciaRepository;
import com.avbravo.transporteejb.repository.TipovehiculoRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.NotificacionServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.TipogiraServices;
import com.avbravo.transporteejb.services.TiposolicitudServices;
import com.avbravo.transporteejb.services.TipovehiculoServices;
import com.avbravo.transporteejb.services.UsuarioServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.avbravo.transporteejb.services.VistoBuenoSecretarioAdministrativoServices;
import com.avbravo.transporteejb.services.VistoBuenoServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
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
import javax.faces.application.FacesMessage;
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
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter

@Setter

public class SecretarioAdministrativoController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;
    private TimelineModel timelineModel;
    private TimelineModel timelineConductorModel;
    String messages = "";
    Boolean coordinadorvalido = false;
    Boolean escoordinador = false;
    Integer index = 0;
    Integer pasajerosDisponibles = 0;
    ManagerEmail managerEmail = new ManagerEmail();
    private Boolean writable = false;
    private Boolean leyoSugerencias = false;
    Boolean diasconsecutivos = false;
    private Date fechaDesde = new Date();
    private Date fechaHasta = new Date();

    private String vistoBuenoSearch = "no";
    private String vistoBuenoSecretarioAdministrativoSearch = "no";
    private String viajeSecretarioAdministrativoSearch = "no";
    //DataModel
    private SolicitudDataModel solicitudDataModel;
    private SugerenciaDataModel sugerenciaDataModel;
    private Date varFechaHoraPartida;
    private Date varFechaHoraRegreso;
    private Integer varAnio;

    private Integer totalAprobado = 0;
    private Integer totalSolicitado = 0;
    private Integer totalRechazadoCancelado = 0;
    private Integer totalPendienteVistoBueno = 0;
    private Integer totalAprobadoVistoBueno = 0;
    private Integer totalNoAprobadoVistoBueno = 0;
    private Integer totalViajesNoRealizado = 0;
    private Integer totalViajesRealizados = 0;
    private Integer totalViajesCancelados = 0;

    private Integer totalViajes = 0;

    /**
     * se usan para obtener los lugares y asignarselo a los atributos lugres de
     * partida y llegada de la solicitud
     */
    Lugares lugaresPartida = new Lugares();
    Lugares lugaresLlegada = new Lugares();

    private ScheduleModel eventModel;
    private ScheduleModel eventModelViajes;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private ScheduleEvent eventViajes = new DefaultScheduleEvent();
    Integer page = 1;
    Integer rowPage = 25;
    private String stmpPort = "25";
    List<Integer> pages = new ArrayList<>();
    List<Sugerencia> sugerenciaList = new ArrayList<>();
    List<DisponiblesBeans> disponiblesBeansList = new ArrayList<>();

    DisponiblesBeans disponiblesBeansSelected = new DisponiblesBeans();

    //Entity
    Solicitud solicitud = new Solicitud();
    Viaje viaje = new Viaje();
    Conductor conductor = new Conductor();
    Vehiculo vehiculo = new Vehiculo();
    Solicitud solicitudSelected;

    Solicitud solicitudOld = new Solicitud();

    Usuario solicita = new Usuario();
    Usuario solicitaOld = new Usuario();
    Usuario responsable = new Usuario();
    Usuario responsableOld = new Usuario();

    Solicitud solicitudCopiar = new Solicitud();
    //
    Solicitud solicitudSearch = new Solicitud();
    Estatus estatusSearch = new Estatus();
    Tiposolicitud tiposolicitudSearch = new Tiposolicitud();

    //List
    List<Solicitud> solicitudGuardadasList = new ArrayList<>();
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudFiltered = new ArrayList<>();
    List<Unidad> unidadList = new ArrayList<>();
    List<Facultad> facultadList = new ArrayList<>();
    List<Carrera> carreraList = new ArrayList<>();
    List<Usuario> usuarioList = new ArrayList<>();
    List<Tiposolicitud> tiposolicitudList = new ArrayList<>();
    List<Tipovehiculo> tipovehiculoList = new ArrayList<>();
    List<Tipovehiculo> suggestionsTipovehiculo = new ArrayList<>();
    List<TipoVehiculoCantidadBeans> tipoVehiculoCantidadBeansList = new ArrayList<>();

    List<String> rangoAgenda = new ArrayList<>();
    //
    private String[] diasSelected;
    private List<String> diasList;
    List<Facultad> suggestionsFacultad = new ArrayList<>();
    List<Carrera> suggestionsCarrera = new ArrayList<>();
    List<Unidad> suggestionsUnidad = new ArrayList<>();
    List<Tiposolicitud> suggestionsTiposolicitud = new ArrayList<>();

    //Repository
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    EstatusRepository estatusRepository;
    @Inject
    EstatusViajeRepository estatusViajeRepository;
    @Inject
    FacultadRepository facultadRepository;
    @Inject
    JmoordbEmailMasterRepository jmoordbEmailMasterRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    SugerenciaRepository sugerenciaRepository;
    @Inject
    TipovehiculoRepository tipovehiculoRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    ViajeRepository viajeRepository;

    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    AutoincrementablebRepository autoincrementablebRepository;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    EstatusServices estatusServices;
    @Inject
    VistoBuenoServices vistoBuenoServices;
    @Inject
    VistoBuenoSecretarioAdministrativoServices vistoBuenoSecretarioAdministrativoServices;
    @Inject
    SemestreServices semestreServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    TipogiraServices tipogiraServices;
    @Inject
    TiposolicitudServices tiposolicitudServices;
    @Inject
    TipovehiculoServices tipovehiculoServices;
    @Inject
    UnidadRepository unidadRepository;
    @Inject
    ViajeServices viajeServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    NotificacionServices notificacionServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //Notification
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    @Inject
    @Push(channel = "notification")
    private PushContext push;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return solicitudRepository.listOfPage(rowPage);
    }

//    public SugerenciaDataModel getSugerenciaDataModel() {
//          sugerenciaList = sugerenciaRepository.findBy("activo", "si");
//            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);
//
//        return sugerenciaDataModel;
//    }
//    
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SecretarioAdministrativoController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            eventModel = new DefaultScheduleModel();
            eventModelViajes = new DefaultScheduleModel();
            timelineModel = new TimelineModel();
            timelineConductorModel = new TimelineModel();

            // eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", DateUtil.fechaHoraActual(), DateUtil.fechaHoraActual()));
            diasList = new ArrayList<String>();
            diasList.add("Dia/ Dias Consecutivo");
            diasList.add("Lunes");
            diasList.add("Martes");
            diasList.add("Miercoles");
            diasList.add("Jueves");
            diasList.add("Viernes");
            diasList.add("Sabado");
            diasList.add("Domingo");

            //autoincrementablebRepository.setDatabase("transporte");
            /*
            configurar el ambiente del contsolicitudler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(solicitudRepository)
                    .withEntity(solicitud)
                    .withService(solicitudServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/secretarioadministrativo/details.jasper")
                    .withPathReportAll("/resources/reportes/secretarioadministrativo/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(false) 
                  .withAction("golist")
                    .build();

            start();
            sugerenciaList = sugerenciaRepository.findBy("activo", "si");
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);
            loadSchedule();
            loadScheduleViajes();
            loadTimeLine();
            loadTimeLineConductor();

            String action = "gonew";
            if (JmoordbContext.get("secretarioadministrativo") != null) {
                action = JmoordbContext.get("secretarioadministrativo").toString();
            }

            if (action == null || action.equals("gonew") || action.equals("new") || action.equals("golist")) {
                inicializar();

            }
            if (action.equals("view")) {
                view();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean view()">
    public Boolean view() {
        try {
            tipoVehiculoCantidadBeansList = new ArrayList<>();
            solicitudOld = solicitud;
            solicita = solicitud.getUsuario().get(0);
            solicitaOld = solicitud.getUsuario().get(0);
            responsable = solicitud.getUsuario().get(1);
            responsableOld = solicitud.getUsuario().get(1);
            facultadList = solicitud.getFacultad();
            carreraList = solicitud.getCarrera();
            // diasList= solicitud.getRangoagenda();
            diasSelected = new String[solicitud.getRangoagenda().size()];
            Integer contador = 0;
            for (String s : solicitud.getRangoagenda()) {
                diasSelected[contador++] = s;

            }
            tipovehiculoList = solicitud.getTipovehiculo();
            disponiblesBeansList = new ArrayList<>();
            // hayBusDisponiblesConFechas();

            /**
             * Carga los tipos de vehiculos disponibles con las cantidades
             * seleccionadas,
             */
            List<Tipovehiculo> list = tipovehiculoRepository.findBy("activo", "si");
            if (list == null || list.isEmpty()) {

            } else {
                Integer maximo = 0;
                Integer numerovehiculo = 0;
                Integer numeropasajero = 0;
                for (Tipovehiculo tv : list) {
                    maximo = vehiculoServices.cantidadVehiculosPorTipo(tv);
                    if (tv.getIdtipovehiculo().equals(solicitud.getTipovehiculo().get(0).getIdtipovehiculo())) {
                        numerovehiculo = 1;
                        numeropasajero = solicitud.getPasajeros();
                        TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans = new TipoVehiculoCantidadBeans(tv, numerovehiculo, maximo, numeropasajero);
                        tipoVehiculoCantidadBeansList.add(tipoVehiculoCantidadBeans);
                    } else {
                        numerovehiculo = 0;
                        numeropasajero = 0;
                    }

                }

            }

            changeDaysViewAvailable();
            if (disponiblesBeansList == null || disponiblesBeansList.isEmpty()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohaybusesdisponiblesenesasfechas"));

                return false;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return true;

    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelectPorSolicitado(SelectEvent event) ">

    public void handleSelectPorSolicitado(SelectEvent event) {
        try {

         setSearchAndValue("porsolicitado", solicita);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelectPorResponsable(SelectEvent event)">

    public void handleSelectPorResponsable(SelectEvent event) {
        try {
            setSearchAndValue("porresponsable", responsable);

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
            solicitudDataModel = new SolicitudDataModel(solicitudList);

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            String descripcion = jmoordb_user.getUnidad().getIdunidad();
            Document doc = new Document("activo", "si");

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":

                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

                case "idsolicitud":
                    if (getValueSearch() != null) {
                        solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));
                    } else {
                        solicitudList = solicitudRepository.findPagination(doc, page, rowPage);
                    }

                    break;

                case "estatus":
                    Estatus estatus = new Estatus();
                    estatus = (Estatus) getValueSearch() ;
                    doc.append("estatus.idestatus", estatus.getIdestatus());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

                case "_betweendates":

                    solicitudList = solicitudRepository.filterBetweenDatePaginationWithoutHours("activo", "si",
                            "fechahorapartida", fechaDesde,
                            "fechahoraregreso", fechaHasta,
                            page, rowPage, new Document("idsolicitud", -1));
                    break;
                case "vistobuenocoordinador":

                    String vistoBueno = (String) getValueSearch() ;
                    doc = new Document("activo", "si");
                    doc.append("vistoBueno.aprobado", vistoBueno);
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;
                case "vistobuenosecretarioadministrativo":

                    String vistoBuenoSecretarioAdministrativo = (String) getValueSearch() ;
                    doc = new Document("activo", "si");
                    doc.append("vistoBuenoSecretarioAdministrativo.aprobado", vistoBuenoSecretarioAdministrativo);
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;
                case "porsolicitado":

                    Usuario solicita = (Usuario)getValueSearch() ;
                    doc = new Document("activo", "si");
                    doc.append("usuario.0.username", solicita.getUsername());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;
                case "porresponsable":

                    Usuario responsable = (Usuario) getValueSearch() ;
                    doc = new Document("activo", "si");
                    doc.append("usuario.1.username", responsable.getUsername());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;
                default:

                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;
            }

            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean localValid()">
    public Boolean localValid() {
        try {
            diasconsecutivos = false;
            solicitud.setFecha(DateUtil.getFechaActual());
            usuarioList = new ArrayList<>();
            usuarioList.add(solicita);
            usuarioList.add(responsable);
            solicitud.setUsuario(usuarioList);

            rangoAgenda = new ArrayList<>();

            if (!isValidDiasConsecutivos()) {
                return false;
            }

            solicitud.setRangoagenda(rangoAgenda);

            solicitud.setFacultad(facultadList);
            solicitud.setCarrera(carreraList);

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            unidadList = new ArrayList<>();
            unidadList.add(jmoordb_user.getUnidad());
            solicitud.setUnidad(unidadList);

            solicitud.setEstatus(estatusServices.findById("SOLICITADO"));
            solicitud.setFechaestatus(DateUtil.fechaActual());
            solicitud.setActivo("si");

            String textsearch = "DOCENTE";
            Rol rol = (Rol) JmoordbContext.get("jmoordb_rol");
            if (rol.getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            if (!solicitudServices.isValid(solicitud)) {
                return false;
            }

            Integer periodo = Integer.parseInt(solicitud.getPeriodoacademico());
            if (DateUtil.anioActual() > periodo) {

                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.anioactualmayorperiodo"));
                return false;
            }
            Integer diferencia = periodo - DateUtil.anioActual();
            if (diferencia > 1) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.periodoacademico"));

                return false;
            }
            if (!leyoSugerencias) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.leersugerencias"));

                return false;
            }

            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean insert()">
    public Boolean insert(Tipovehiculo tipovehiculo) {
        try {
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Integer idsolicitud = autoincrementableServices.getContador("solicitud");
            solicitud.setIdsolicitud(idsolicitud);
            //Se establece en 1 el numero de vehiculos solicitados a ser guardados
            solicitud.setNumerodevehiculos(1);
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
            //Lo datos del usuario
            List<Tipovehiculo> tipovehiculoList = new ArrayList<>();

//            tipovehiculoList.add(tipovehiculoServices.findById("BUS"));
            tipovehiculoList.add(tipovehiculo);
            solicitud.setTipovehiculo(tipovehiculoList);
            solicitud.setUserInfo(solicitudRepository.generateListUserinfo(jmoordb_user.getUsername(), "create"));
            if (solicitudRepository.save(solicitud)) {
                Solicitud sol = new Solicitud();
                sol = (Solicitud) JsfUtil.copyBeans(sol, solicitud);
                solicitudGuardadasList.add(sol);

                //guarda el contenido anterior
                JmoordbConfiguration jmc = new JmoordbConfiguration();
                Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
                RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
                repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                        "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            } else {
                JsfUtil.successMessage("insert() " + solicitudRepository.getException().toString());
                return false;
            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isValidDayName(String name)">
    private Boolean isValidDayName(String name) {
        Boolean valid = false;
        try {
            for (String rango : solicitud.getRangoagenda()) {
                //verificar que dia es
                if (name.equals(rango.toUpperCase())) {
                    valid = true;
                    break;
                }
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="List<FechaDiaUtils> validarRangoFechas(Integer anioPartida, String nombreMesPartida)">
    /**
     * valida el rango de las fechas validas
     *
     * @param anioPartida
     * @param nombreMesPartida
     * @return
     */
    private List<FechaDiaUtils> validarRangoFechas(Integer anioPartida, String nombreMesPartida) {
        List<FechaDiaUtils> fechaDiaUtilsSaveList = new ArrayList<>();
        try {
            List<FechaDiaUtils> fechaDiaUtilsInicialList = DateUtil.nameOfDayOfDateOfMonth(anioPartida, nombreMesPartida);

//convertir la fecha de solicitud a LocalDate
            LocalDate start = DateUtil.convertirJavaDateToLocalDate(varFechaHoraPartida);
            LocalDate end = DateUtil.convertirJavaDateToLocalDate(varFechaHoraRegreso);

            //Buscar si esta en el intervalo de dias entre las fechas
            fechaDiaUtilsInicialList.forEach((fdu) -> {
                LocalDate l = fdu.getDate();

                if (l.isEqual(start) || l.isEqual(end) || (l.isAfter(start) && l.isBefore(end))) {
                    fechaDiaUtilsSaveList.add(fdu);

                }
            });

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return fechaDiaUtilsSaveList;
    }  // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String save()">
    @Override
    public String save() {
        try {
            solicitudGuardadasList = new ArrayList<>();

            if (!localValid()) {
                return "";
            }

            //verifica si hay buses disponibles
            if (disponiblesBeansList == null || disponiblesBeansList.isEmpty()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohaybusesdisponiblesenesasfechas"));
                return "";
            }
            if (!isValidDisponibles()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.listavehiculosnoesvalida"));
                return "";
            }

            //Asignar el estatusViaje
            EstatusViaje estatusViaje = new EstatusViaje();
            estatusViaje.setIdestatusviaje("NO ASIGNADO");
            Optional<EstatusViaje> optional = estatusViajeRepository.findById(estatusViaje);
            if (optional.isPresent()) {
                estatusViaje = optional.get();
            } else {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.noexisteestatusviajenoasigando"));
                return "";
            }
            solicitud.setEstatusViaje(estatusViaje);
            /**
             * Habilitarlo si no deseamos guardar los que estan en rojo
             */

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            //si cambia el email o celular del responsable actualizar ese usuario
            if (!responsableOld.getEmail().equals(responsable.getEmail()) || !responsableOld.getCelular().equals(responsable.getCelular())) {
                usuarioRepository.update(responsable);
                //actuliza el que esta en el login
                if (responsable.getUsername().equals(jmoordb_user.getUsername())) {
                    //  loginController.setUsuario(responsable);
                }
            }

            Integer solicitudesGuardadas = 0;
            Integer idSolicitudPadre = 0;
            solicitud.setSolicitudpadre(0);
            varFechaHoraPartida = solicitud.getFechahorapartida();
            varFechaHoraRegreso = solicitud.getFechahoraregreso();

            //Obtiene la lista de usuarios para notificar
            usuarioList = usuarioServices.usuariosParaNotificar(facultadList);
            //Verifica si es el mismo coordinador quien hace la solicitud, si es asi colocar aprobado directamente
            Boolean vistoBuenoAprobado = usuarioServices.esElCoordinadorQuienSolicita(usuarioList, jmoordb_user);

            //Si es el mismo usuario el coordinador removerlo para no enviarle notificaciones
            if (vistoBuenoAprobado) {
                usuarioList = usuarioServices.removerCoordinadorLista(usuarioList, jmoordb_user);
            }

            //Guarda la solicitud
            for (DisponiblesBeans db : disponiblesBeansList) {
                for (int i = 0; i < db.getBusesRecomendados(); i++) {

                    //Aqui revisar la cantidad de pasajeros
                    solicitud.setPasajeros(db.getPasajerosPorViaje().get(0));

                    solicitud.setFechahorapartida(db.getFechahorainicio());
                    solicitud.setFechahoraregreso(db.getFechahorafin());
                    solicitud.setNumerodevehiculos(1);
                    if (vistoBuenoAprobado) {
                        solicitud.setVistoBueno(vistoBuenoServices.inicializarAprobado(jmoordb_user));
                    } else {
                        solicitud.setVistoBueno(vistoBuenoServices.inicializarPendiente(jmoordb_user));
                    }

                    if (insert(db.getVehiculo().get(0).getTipovehiculo())) {
                        solicitudesGuardadas++;

                        if (solicitudesGuardadas.equals(1)) {
                            idSolicitudPadre = solicitud.getIdsolicitud();
                        } else {
                            solicitud.setSolicitudpadre(idSolicitudPadre);
                        }

                    }
                }
            }

            if (usuarioList == null || usuarioList.isEmpty()) {
            } else {
                //Verifica si es un coordinador y le envia la notificacion

                usuarioList.forEach((u) -> {
                    notificacionServices.saveNotification("Nueva solicitud de: " + responsable.getNombre(), u.getUsername(), "solicituddocente");

                });

                //Envia la notificacion.....
                push.send("Nueva solicitud Docente ");

            }
            JsfUtil.infoDialog("Mensaje", rf.getMessage("info.savesolicitudes"));
            /**
             * Enviar un email al administrativo y al mismo administrador
             */
            sendEmail(" creada(s) ");

            facultadList = new ArrayList<>();
            carreraList = new ArrayList<>();
            reset();
            inicializar();
            return "";
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String columnColor(Estatus estatus">
    public String columnColor(Estatus estatus) {
        String color = "black";
        try {
            color = estatusServices.columnColor(estatus);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return color;
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String columnColor(Estatus estatus">

    public String columnColorVistoBueno(VistoBueno vistoBueno) {
        String color = "black";
        try {
            color = vistoBuenoServices.columnColor(vistoBueno);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return color;
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String columnNameVistoBueno(VistoBueno vistoBueno) ">

    public String columnNameVistoBueno(VistoBueno vistoBueno) {
        return vistoBuenoServices.columnNameVistoBueno(vistoBueno);
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String columnNameVistoBueno()">

    public String columnNameVistoBueno() {
        return vistoBuenoServices.columnNameVistoBueno(solicitud.getVistoBueno());
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isVistoBuenoCoordinadorYSecretarioAprobadoOPendiente(Estatus estatus, Solicitud solicitud)">
    public Boolean isVistoBuenoCoordinadorYSecretarioAprobadoOPendiente(Estatus estatus, Solicitud solicitud) {
        Boolean valid = false;
        try {
            Boolean solicitado = estatusServices.isSolicitado(estatus);
            if (solicitado) {
                //verifica que tenga visto bueno del coordinador
                if (solicitud.getTiposolicitud().getIdtiposolicitud().equals("DOCENTE")) {
                    //visto bueno del coordinador
                    if (solicitud.getVistoBueno().getAprobado().equals("si")) {
                        //visto bueno  o pendiente del secretario administrativo
                        if (solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("si") || solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("pe")) {
                            valid = true;
                        }

                    }
                } else {
                    if (solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("si") || solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("pe")) {
                        valid = true;
                    }

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isSolicitadoConVistoBuenoNoAprobadoOPendienteCoordinador(Estatus estatus, Solicitud solicitud)">
    public Boolean noVistoBuenoCoordinadorYSecretarioAprobadoOPendiente(Estatus estatus, Solicitud solicitud) {
        Boolean valid = false;
        try {
            Boolean solicitado = estatusServices.isSolicitado(estatus);
            if (solicitado) {
                //verifica que tenga visto bueno del coordinador
                if (solicitud.getTiposolicitud().getIdtiposolicitud().equals("DOCENTE")) {
                    //visto bueno del coordinador
                    if (solicitud.getVistoBueno().getAprobado().equals("si")) {
                        //visto bueno  o pendiente del secretario administrativo
                        if (solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("no") || solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("pe")) {
                            valid = true;
                        }

                    }
                } else {
                    if (solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("no") || solicitud.getVistoBuenoSecretarioAdministrativo().getAprobado().equals("pe")) {
                        valid = true;
                    }

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isVistoBuenoCoordinador( Solicitud solicitud)">
    public Boolean isVistoBuenoCoordinador(Solicitud solicitud) {
        Boolean valid = false;
        try {

            //verifica que tenga visto bueno del coordinador
            if (solicitud.getVistoBueno().getAprobado().equals("si")) {
                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="columnColorDisponibles(DisponiblesBeans disponiblesBeans) ">
    public String columnColorDisponibles(DisponiblesBeans disponiblesBeans) {
        String color = "black";
        try {
            if ((disponiblesBeans.getNumeroVehiculosSolicitados() > disponiblesBeans.getNumeroBuses()) || disponiblesBeans.getNumeroPasajerosSolicitados() > disponiblesBeans.getNumeroPasajeros()) {
                color = "red";
            }
//            if (disponiblesBeans.getNumeroBuses() < solicitud.getNumerodevehiculos() || disponiblesBeans.getNumeroPasajeros() < solicitud.getPasajeros()) {
//                color = "red";
//            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return disponible;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeSolicitudParaCopiar(String query)">
    public List<Solicitud> completeSolicitudParaCopiar(String query) {
        return solicitudServices.completeSolicitudParaCopiar(query, "DOCENTE");

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeFiltradoUnidad">
    public List<Unidad> completeFiltradoUnidad(String query) {
        suggestionsUnidad = new ArrayList<>();
        List<Unidad> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
            if (query.length() < 1) {
                return suggestionsUnidad;
            }

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = unidadRepository.findRegexInText(field, query, true, new Document(field, 1));
            if (unidadList.isEmpty()) {
                if (!temp.isEmpty()) {
                    suggestionsUnidad = temp;
                }
            } else {
                if (!temp.isEmpty()) {
                    temp.forEach((u) -> {
                        addUnidad(u);
                    });

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestionsUnidad;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="completeFiltradoFacultad(String query)">

    public List<Facultad> completeFiltradoFacultad(String query) {

        suggestionsFacultad = new ArrayList<>();
        List<Facultad> temp = new ArrayList<>();
        try {

            Boolean found = false;
            query = query.trim();
//            if (query.length() < 1) {
//                return suggestionsFacultad;
//            }

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = facultadRepository.findRegexInText(field, query, true, new Document(field, 1));
            if (facultadList == null || facultadList.isEmpty()) {

                if (!temp.isEmpty()) {
                    suggestionsFacultad = temp;
                }
            } else {

                if (!temp.isEmpty()) {
                    temp.stream().forEach(f -> addFacultad(f));
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestionsFacultad;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="completeFiltradoCarrera(String query)">
    public List<Carrera> completeFiltradoCarrera(String query) {
        suggestionsCarrera = new ArrayList<>();
        List<Carrera> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
//            if (query.length() < 1) {
//                return suggestionsCarrera;
//            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = carreraRepository.findRegexInText(field, query, true, new Document(field, 1));
            temp = removeByNotFoundFacultad(temp);
            if (carreraList == null || carreraList.isEmpty()) {
                if (!temp.isEmpty()) {
                    suggestionsCarrera = temp;
                }
            } else {
                if (!temp.isEmpty()) {
                    temp.stream().forEach(c -> addCarrera(c));

                }
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestionsCarrera;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="removeByNotFoundFacultad(List<Carrera> carreraList)">
    private List<Carrera> removeByNotFoundFacultad(List<Carrera> carreraList) {
        List<Carrera> list = new ArrayList<>();
        try {
            //1.recorre las facultades
            //2.filtra las carreras de esa facultad
            //3.crea una lista
            //4. luego va agregando esa lista a la otra por cada facultad
            if (facultadList == null || facultadList.isEmpty()) {
                return list;
            }
            facultadList.forEach((f) -> {
                List<Carrera> temp = carreraList.stream()
                        .parallel()
                        .filter(p -> p.getFacultad().getIdfacultad().equals(f.getIdfacultad()))
                        .collect(Collectors.toCollection(ArrayList::new));

                temp.forEach((c) -> {
                    list.add(c);
                });
            });

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return list;
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="itemUnselect">
    public void itemUnselect(UnselectEvent event) {
        try {
            if (carreraList != null && !carreraList.isEmpty()) {
                carreraList = removeByNotFoundFacultad(carreraList);
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="foundFacultad(Integer idfacultad)">
    private Boolean foundFacultad(Integer idfacultad) {
        Boolean _found = true;
        try {
            Facultad facultad = facultadList.stream() // Convert to steam
                    .filter(x -> x.getIdfacultad() == idfacultad) // we want "jack" only
                    .findAny() // If 'findAny' then return found
                    .orElse(null);
            if (facultad == null) {

                _found = false;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return _found;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="foundCarrera(Integer idcarrera)">

    private Boolean foundCarrera(Integer idcarrera) {
        Boolean _found = true;
        try {
            Carrera carrera = carreraList.stream()
                    .filter(x -> x.getIdcarrera() == idcarrera)
                    .findAny()
                    .orElse(null);
            if (carrera == null) {
                _found = false;
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return _found;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="foundUnidad(Integer idunidad)">
    private Boolean foundUnidad(String idunidad) {
        Boolean _found = true;
        try {
            Unidad unidad = unidadList.stream()
                    .filter(x -> x.getIdunidad() == idunidad)
                    .findAny() // If 'findAny' then return found
                    .orElse(null);
            if (unidad == null) {
                _found = false;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return _found;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addFacultad(Facultad facultad)">
    private Boolean addFacultad(Facultad facultad) {
        try {
            if (!foundFacultad(facultad.getIdfacultad())) {
                suggestionsFacultad.add(facultad);
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="addCarrera(Carrera carrera)">
    private Boolean addCarrera(Carrera carrera) {
        try {
            if (!foundCarrera(carrera.getIdcarrera())) {
                suggestionsCarrera.add(carrera);
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addUnidad(Unidad unidad)">
    private Boolean addUnidad(Unidad unidad) {
        try {
            if (!foundUnidad(unidad.getIdunidad())) {
                suggestionsUnidad.add(unidad);
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="verificarEditable(Solicitud item)">
    /**
     * verifica si es editable
     *
     * @param item
     * @return
     */
    public Boolean verificarEditable(Solicitud item) {
        Boolean editable = false;
        try {
            if (item.getEstatus().getIdestatus().equals("SOLICITADO")) {
                editable = true;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return editable;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onDateSelect(SelectEvent event)">
    public String onDateSelect(SelectEvent event) {
        try {
            if (DateUtil.fechaMenor(solicitud.getFechahoraregreso(), solicitud.getFechahorapartida())) {

//                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fecharegresomenorquefechapartida"));
                JsfUtil.warningMessage(rf.getMessage("warning.fecharegresomenorquefechapartida"));
                return "";
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="completeFiltradoTipovehiculo(String query)">
    public List<Tipovehiculo> completeFiltradoTipovehiculo(String query) {

        suggestionsTipovehiculo = new ArrayList<>();
        List<Tipovehiculo> temp = new ArrayList<>();
        try {

            Boolean found = false;
            query = query.trim();

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = tipovehiculoRepository.findRegexInText(field, query, true, new Document(field, 1));
            if (tipovehiculoList == null || tipovehiculoList.isEmpty()) {

                if (!temp.isEmpty()) {
                    suggestionsTipovehiculo = temp;
                }
            } else {

                if (!temp.isEmpty()) {
                    temp.stream().forEach(f -> addTipovehiculo(f));
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestionsTipovehiculo;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addTipovehiculo(Tipovehiculo tipovehiculo)">
    private Boolean addTipovehiculo(Tipovehiculo tipovehiculo) {
        try {
            if (!foundTipovehiculo(tipovehiculo.getIdtipovehiculo())) {
                suggestionsTipovehiculo.add(tipovehiculo);
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="foundTipovehiculo(String idtipovehiculo)">
    private Boolean foundTipovehiculo(String idtipovehiculo) {
        Boolean _found = true;
        try {
            Tipovehiculo tipovehiculo = tipovehiculoList.stream() // Convert to steam
                    .filter(x -> x.getIdtipovehiculo() == idtipovehiculo) // we want "jack" only
                    .findAny() // If 'findAny' then return found
                    .orElse(null);
            if (tipovehiculo == null) {

                _found = false;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return _found;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean inicializar()">
    private String inicializar() {
        try {
            tipoVehiculoCantidadBeansList = new ArrayList<>();
            Integer id = 0;
            Date idsecond = new Date();

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            if (solicitud == null || solicitud.getFecha() == null) {
                idsecond = DateUtil.getFechaHoraActual();
            } else {
                idsecond = solicitud.getFecha();
                id = solicitud.getIdsolicitud();
            }

            solicitud = new Solicitud();
            solicitudSelected = new Solicitud();
            solicitud.setIdsolicitud(id);
            solicitud.setFecha(idsecond);
            solicitud.setMision("---");
            solicitud.setLugarpartida("UTP-AZUERO");
            solicitud.setNumerodevehiculos(1);
            solicitud.setPasajeros(0);
            solicitud.setFechaestatus(DateUtil.getFechaHoraActual());
            solicita = jmoordb_user;
            responsable = solicita;
            responsableOld = responsable;

            usuarioList = new ArrayList<>();
            usuarioList.add(solicita);
            usuarioList.add(responsable);
            solicitud.setUsuario(usuarioList);

            solicitud.setPeriodoacademico(DateUtil.getAnioActual().toString());
            solicitud.setFechahorapartida(solicitud.getFecha());
            solicitud.setFechahoraregreso(solicitud.getFecha());
            unidadList = new ArrayList<>();
            unidadList.add(jmoordb_user.getUnidad());
            solicitud.setUnidad(unidadList);

            Integer mes = DateUtil.mesDeUnaFecha(solicitud.getFecha());

            String idsemestre = "V";
            if (mes <= 3) {
                //verano
                idsemestre = "V";

            } else {
                if (mes <= 7) {
                    //primer
                    idsemestre = "I";
                } else {
                    //segundo
                    idsemestre = "II";
                }
            }
            solicitud.setSemestre(semestreServices.findById(idsemestre));
            List<Tipovehiculo> tipovehiculoList = new ArrayList<>();

            tipovehiculoList.add(tipovehiculoServices.findById("BUS"));
            solicitud.setTipovehiculo(tipovehiculoList);

            solicitud.setEstatus(estatusServices.findById("SOLICITADO"));

            String textsearch = "DOCENTE";
            Rol rol = (Rol) JmoordbContext.get("jmoordb_rol");
            if (rol.getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            //
//            diasSelected = new String[0];           
//            diasSelected[0] = "Dia/ Dias Consecutivo";

            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            solicitudSelected = solicitud;
            leyoSugerencias = false;

            //inicializa tipo de vehiculos
            List<Tipovehiculo> list = tipovehiculoRepository.findBy("activo", "si");
            if (list == null || list.isEmpty()) {

            } else {
                Integer maximo = 0;
                for (Tipovehiculo tv : list) {
                    maximo = vehiculoServices.cantidadVehiculosPorTipo(tv);
                    TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans = new TipoVehiculoCantidadBeans(tv, 0, maximo, 0);
                    tipoVehiculoCantidadBeansList.add(tipoVehiculoCantidadBeans);
                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleSelectCopiarDesde(SelectEvent event)">
    public void handleSelectCopiarDesde(SelectEvent event) {
        try {

            solicitud = solicitudServices.copiarDesde(solicitudCopiar, solicitud);

            facultadList = solicitud.getFacultad();
            carreraList = solicitud.getCarrera();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String loadTimeLine()">
    public String loadTimeLine() {
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
        return "";
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
        return "";
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="loadSchedule(Document...doc)">
    public void loadSchedule(Document... doc) {
        try {
            Document docSearch = new Document();
            if (doc.length != 0) {
                docSearch = doc[0];

            } else {
                docSearch = new Document("activo", "si");
            }
            totalAprobado = 0;
            totalSolicitado = 0;
            totalRechazadoCancelado = 0;
            totalViajes = 0;

            totalPendienteVistoBueno = 0;
            totalAprobadoVistoBueno = 0;
            totalNoAprobadoVistoBueno = 0;

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            String descripcion = jmoordb_user.getUnidad().getIdunidad();
            List<Solicitud> list = new ArrayList<>();

//                list = solicitudRepository.findBy("activo","si", new Document("idsolicitud", -1));
            list = solicitudRepository.findBy(docSearch, new Document("idsolicitud", -1));

            eventModel = new DefaultScheduleModel();
            if (!list.isEmpty()) {
                list.forEach((a) -> {
                    String nameOfCarrera = "";
                    String nameOfViajes = "";
                    String viajest = "";
                    nameOfCarrera = a.getCarrera().stream().map((c) -> c.getDescripcion() + "").reduce(nameOfCarrera, String::concat);
                    String tipoVehiculo = "{ ";
                    tipoVehiculo = a.getTipovehiculo().stream().map((t) -> t.getIdtipovehiculo() + " ").reduce(tipoVehiculo, String::concat);
                    tipoVehiculo += " }";
                    String tema = "schedule-blue";
                    switch (a.getEstatus().getIdestatus()) {
                        case "SOLICITADO":
                            totalSolicitado++;
                            tema = "schedule-orange";
                            break;
                        case "APROBADO":
                            totalAprobado++;
                            viajest = "{";
                            viajest = a.getViaje().stream().map((t) -> t.getIdviaje() + " ").reduce(viajest, String::concat);
                            viajest = "}";

                            tema = "schedule-green";
                            break;
                        case "RECHAZADO":
                            totalRechazadoCancelado++;
                            tema = "schedule-red";
                            break;
                        case "CANCELADO":
                            totalRechazadoCancelado++;
                            tema = "schedule-red";
                            break;
                    }

                    switch (a.getVistoBueno().getAprobado().trim()) {
                        case "no":
                            totalNoAprobadoVistoBueno++;
                            break;
                        case "si":
                            totalAprobadoVistoBueno++;
                            break;
                        case "pe":
                            totalPendienteVistoBueno++;
                            break;
                    }

                    String texto = nameOfCarrera + " " + viajest;

                    eventModel
                            .addEvent(
                                    new DefaultScheduleEvent("# " + a.getIdsolicitud() + " : (" + a.getEstatus().getIdestatus().substring(0, 1) + ")  " + "  {" + a.getVistoBueno().getAprobado().substring(0, 1).toUpperCase() + "}  " + a.getObjetivo() + " "
                                            + texto,
                                            a.getFechahorapartida(), a.getFechahoraregreso(), tema)
                            );
                });
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="loadScheduleViajes(Document...doc) ">

    public void loadScheduleViajes(Document... doc) {
        try {
            Document docSearch = new Document();
            if (doc.length != 0) {
                docSearch = doc[0];

            } else {
                docSearch = new Document("activo", "si");
            }

            totalViajes = 0;
            totalViajesCancelados = 0;
            totalViajesNoRealizado = 0;
            totalViajesRealizados = 0;

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            String descripcion = jmoordb_user.getUnidad().getIdunidad();
            List<Viaje> list = new ArrayList<>();

            list = viajeRepository.findBy(docSearch, new Document("idviaje", -1));

            eventModelViajes = new DefaultScheduleModel();
            if (!list.isEmpty()) {
                list.forEach((a) -> {
                    String nameOfConductor = "";
                    String nameOfVehiculo = a.getVehiculo().getPlaca() + " " + a.getVehiculo().getMarca() + " " + a.getVehiculo().getModelo();
                    String viajest = "";
                    nameOfConductor = a.getConductor().getNombre();
                    String tipoVehiculo = "{ ";
                    tipoVehiculo = a.getVehiculo().getTipovehiculo().getIdtipovehiculo();
                    tipoVehiculo += " }";
                    String tema = "schedule-blue";
                    switch (a.getRealizado()) {
                        case "si":
                            totalViajesRealizados++;
                            tema = "schedule-orange";
                            break;
                        case "no":
                            totalViajesNoRealizado++;

                            tema = "schedule-green";
                            break;
                        case "ca":
                            totalViajesCancelados++;
                            tema = "schedule-red";
                            break;

                    }

                    totalViajes += totalViajesCancelados + totalViajesNoRealizado + totalViajesRealizados;
                    String texto = nameOfVehiculo + " " + nameOfConductor;
                    eventModelViajes
                            .addEvent(
                                    new DefaultScheduleEvent("# " + a.getIdviaje() + " : (" + a.getRealizado().substring(0, 1) + ")  " + " "
                                            + texto,
                                            a.getFechahorainicioreserva(), a.getFechahorainicioreserva(), tema)
                            );
                });
            }

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

                solicita = solicitud.getUsuario().get(0);
                responsable = solicitud.getUsuario().get(1);
                facultadList = solicitud.getFacultad();
                unidadList = solicitud.getUnidad();
                carreraList = solicitud.getCarrera();
                solicitudSelected = solicitud;

            }

        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    } // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="onEventSelect(SelectEvent selectEvent)">

    public void onEventSelectViaje(SelectEvent selectEvent) {
        try {

            eventViajes = (ScheduleEvent) selectEvent.getObject();

            String title = eventViajes.getTitle();
            Integer i = title.indexOf(":");

            Integer idviaje = 0;
            if (i != -1) {
                idviaje = Integer.parseInt(title.substring(1, i).trim());
            }
            viaje.setIdviaje(idviaje);
            Optional<Viaje> optional = viajeRepository.findById(viaje);
            if (optional.isPresent()) {
                viaje = optional.get();

            }

        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return solicitudServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return solicitudServices.showHour(date);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Future<String> calculateAsync(">
    public Future<String> sendEmailAsync(String emailreceptor, String titulo, String mensaje, String emailemisor, String passwordemisor) throws InterruptedException {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {

                managerEmail.sendOutlook(emailreceptor, titulo, mensaje, emailemisor, passwordemisor);

                completableFuture.complete("enviado");

                return null;
            }
        });

        return completableFuture;
    }// </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc="Future<String> calculateAsync(">
//

    public Future<String> sendEmailCccBccAsync(String[] to, String[] cc, String[] bcc, String titulo, String mensaje, String emailemisor, String passwordemisor) throws InterruptedException {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                managerEmail.sendOutlook(to, cc, bcc, titulo, mensaje, emailemisor, passwordemisor, false);
                completableFuture.complete("enviado");

                return null;
            }
        });

        return completableFuture;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calendarChangeListener(SelectEvent event)">
    public void calendarChangeListener(SelectEvent event) {
        try {
//verifica si hay buses disponibles

            if (solicitud.getFechahorapartida() == null || solicitud.getFechahoraregreso() == null) {

            } else {
                if (!solicitudServices.isValidDates(solicitud, true)) {
                    return;
                }
                changeDaysViewAvailable();
                if (diasSelected == null || diasSelected.length == 0) {
                    // JsfUtil.warningDialog("texto", "Aun no ha seleccionado el rango");
                } else {
                    if (disponiblesBeansList == null || disponiblesBeansList.isEmpty()) {
                        JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohaybusesdisponiblesenesasfechas"));

                        return;
                    }
                }

                if (!solicitudServices.solicitudDisponible(solicitud, solicitud.getFechahorapartida(), solicitud.getFechahoraregreso())) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.yatienesolicitudenesasfechas"));

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="List<Vehiculo> busesActivo()">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Vehiculo> vehiculosActivo(Tipovehiculo tipovehiculo) {
        Boolean haydisponibles = false;

        List<Vehiculo> vehiculoList = new ArrayList<>();
        List<Vehiculo> suggestions = new ArrayList<>();
        try {
            //Verifica si se selecciono los tipos de vehiculos

            pasajerosDisponibles = 0;
            Document doc = new Document("activo", "si").append("tipovehiculo.idtipovehiculo", tipovehiculo.getIdtipovehiculo()).append("enreparacion", "no");
            vehiculoList = vehiculoRepository.findBy(doc);
            if (vehiculoList == null || vehiculoList.isEmpty()) {
                return vehiculoList;
            }
            vehiculoList.sort(Comparator.comparingDouble(Vehiculo::getPasajeros)
                    .reversed());

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return vehiculoList;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isVehiculoActivoDisponible(Vehiculo vehiculo)">
    public Boolean isVehiculoActivoDisponible(Vehiculo vehiculo) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("no") && vehiculo.getEnreparacion().equals("si")) {

            } else {
                if (viajeServices.vehiculoDisponible(vehiculo, solicitud.getFechahorapartida(), solicitud.getFechahoraregreso())) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean isVehiculoActivoDisponible(Vehiculo vehiculo,Date fechahorainicio, Date fechahorafin) ">
    public Boolean isVehiculoActivoDisponible(Vehiculo vehiculo, Date fechahorainicio, Date fechahorafin) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("no") && vehiculo.getEnreparacion().equals("si")) {

            } else {
                if (viajeServices.vehiculoDisponible(vehiculo, fechahorainicio, fechahorafin)) {
                    valid = true;
                }
            }

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String edit()">
    @Override
    public String edit() {
        try {
            leyoSugerencias = true;
            solicitudGuardadasList = new ArrayList<>();
            solicitudGuardadasList.add(solicitud);
            if (solicitud.getEstatus().getIdestatus().equals("APROBAOD") || solicitud.getEstatus().getIdestatus().equals("CANCELADO")) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nosepuedeeditarsolicitudaprobadaocancelada"));
                return "";
            }
            if (!localValid()) {
                return "";
            }

            if (disponiblesBeansList == null || disponiblesBeansList.isEmpty()) {

            } else {
                if (!isValidDisponibles()) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.listavehiculosnoesvalida"));
                    return "";
                }
                /**
                 * Verifica que el numero de pasajeros no exceda la capacidad
                 * maxima del bus con mayor capacidad , ya que cuando se edita
                 * no se permite agregar mas buses.
                 */

                for (DisponiblesBeans db : disponiblesBeansList) {
                    if (db.getVehiculo() == null || db.getVehiculo().isEmpty()) {

                    } else {
                        if (solicitud.getPasajeros() > db.getVehiculo().get(0).getPasajeros()) {

                            JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.pasajerosexcedencapicidadbusdisponible"));
                            return "";
                        }
                    }

                }

            }

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            //si cambia el email o celular del responsable actualizar ese usuario
            if (!responsableOld.getEmail().equals(responsable.getEmail()) || !responsableOld.getCelular().equals(responsable.getCelular())) {
                usuarioRepository.update(responsable);
                //actuliza el que esta en el login
                if (responsable.getUsername().equals(jmoordb_user.getUsername())) {
                    //  loginController.setUsuario(responsable);
                }
            }
            Integer solicitudesGuardadas = 0;

            varFechaHoraPartida = solicitud.getFechahorapartida();
            varFechaHoraRegreso = solicitud.getFechahoraregreso();

            /**
             * Si es dias consecutivos es un solo intervalo para la reservacion
             * se creara una solicitud para cada vehiculos solicitado
             */
            solicitudRepository.update(solicitud);

            JsfUtil.infoDialog("Mensaje", rf.getMessage("info.editsolicitudes"));
            //guarda el contenido anterior
            JmoordbConfiguration jmc = new JmoordbConfiguration();
            Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
            RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
            repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                    "update", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            usuarioList = usuarioServices.usuariosParaNotificar(facultadList);
            //  Guardar las notificaciones

            //Verifica si es el mismo coordinador quien hace la solicitud, si es asi colocar aprobado directamente
            Boolean vistoBuenoAprobado = usuarioServices.esElCoordinadorQuienSolicita(usuarioList, jmoordb_user);

            //Si es el mismo usuario el coordinador removerlo para no enviarle notificaciones
            if (vistoBuenoAprobado) {

                usuarioList = usuarioServices.removerCoordinadorLista(usuarioList, jmoordb_user);
            }

            if (usuarioList == null || usuarioList.isEmpty()) {
            } else {

                usuarioList.forEach((u) -> {
                    notificacionServices.saveNotification("Nueva solicitud de: " + responsable.getNombre(), u.getUsername(), "solicituddocente");

                });
                push.send("Edicicion de solicitud docente ");
            }

            /**
             * Enviar un email al administrativo y al mismo administrador
             */
            sendEmail(" editada(s) ");

            return "";
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean beforePrepareView()">
    @Override
    public Boolean beforePrepareView() {
        try {
            Solicitud item = (Solicitud) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("item");
            switch (item.getEstatus().getIdestatus()) {
                case "SOLICITADO":
                case "APROBADO":
                    return true;
                default:
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.soloseeditanlossolicitados"));
                    return false;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hString handleAutocompleteEstatus(SelectEvent event)">
    
    public String handleAutocompleteEstatus(SelectEvent event) {
        try {
          setSearchAndValue("estatus",estatusSearch);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String handleAutocompleteEstatusForSchedule(SelectEvent event)">
    /**
     * Actualiza el schedule en base al filtro del autocomplete
     *
     * @param event
     * @return
     */
    public String handleAutocompleteEstatusForSchedule(SelectEvent event) {
        try {
            Document doc = new Document("activo", "si").append("estatus.idestatus", estatusSearch.getIdestatus());
            loadSchedule(doc);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String handleAutocompleteTipoSolicitudForSchedule(SelectEvent event)">

    /**
     * Actualiza el schedule en base al filtro del autocomplete
     *
     * @param event
     * @return
     */
    public String handleAutocompleteTipoSolicitudForSchedule(SelectEvent event) {
        try {
            Document doc = new Document("activo", "si").append("tiposolicitud.idtiposolicitud", tiposolicitudSearch.getIdtiposolicitud());
            loadSchedule(doc);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String handleAutocompleteSolicitaForSchedule(SelectEvent event)">

    /**
     * Actualiza el schedule en base al filtro del autocomplete
     *
     * @param event
     * @return
     */
    public String handleAutocompleteSolicitaForSchedule(SelectEvent event) {
        try {
            Document doc = new Document("activo", "si").append("usuario.0.username", solicita.getUsername());
            loadSchedule(doc);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String handleAutocompleteSolicitaForSchedule(SelectEvent event)">

    /**
     * Actualiza el schedule en base al filtro del autocomplete
     *
     * @param event
     * @return
     */
    public String handleAutocompleteResponsableForSchedule(SelectEvent event) {
        try {
            Document doc = new Document("activo", "si").append("usuario.1.username", responsable.getUsername());
            loadSchedule(doc);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

  

    // <editor-fold defaultstate="collapsed" desc="enviarMensajesDirectos()">
    public String enviarMensajesDirectos() {
        try {
            if (messages.equals("")) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohaymensajeparaenviar"));
                return "";

            }
            //  Guardar las notificaciones

            usuarioList = usuarioServices.usuariosParaNotificar();
            if (usuarioList == null || usuarioList.isEmpty()) {
            } else {
                usuarioList.forEach((u) -> {
                    notificacionServices.saveNotification(messages, u.getUsername(), "solicituddocente");

                });
                push.send("Mensaje de docente ");
            }
            JsfUtil.infoDialog("Informacion", "Se envio la notifacion a los administradores");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String goList()">
    /**
     * se invoca desde los menus
     *
     * @return
     */
    public String goList(String ruta) {
        ruta = ruta.trim();
        JmoordbContext.put("coordinador", "golist");
        return "/pages/" + ruta + "/list.xhtml";

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String changeDaysViewAvailable()">
    /**
     * cuando el usario selecciona el dia verifica los vehiculos disponibles y
     * carga la lista de disponiblesBeansList y vehiculoDisponiblesList
     *
     * @return
     */
    public String changeDaysViewAvailable() {
        try {
            Integer iddisponible = 0;
            disponiblesBeansList = new ArrayList<>();
            rangoAgenda = new ArrayList<>();
            if (!isValidDiasConsecutivos()) {
                return "";
            }
            solicitud.setRangoagenda(rangoAgenda);
            List<Vehiculo> vehiculoFreeList = new ArrayList<>();
            varFechaHoraPartida = solicitud.getFechahorapartida();
            varFechaHoraRegreso = solicitud.getFechahoraregreso();
            List<Vehiculo> vehiculoList = new ArrayList<>();
            if (tipoVehiculoCantidadBeansList == null || tipoVehiculoCantidadBeansList.isEmpty()) {
                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.seleccionetipodevehiculos"));
                return "";
            }
            if (calcularTotalVehiculo() == 0) {
                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.indiquelacantidaddevehiculosportipo"));
                return "";
            }
            //Genero para cada fecha y cada tipo
            for (TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans : tipoVehiculoCantidadBeansList) {
                if (tipoVehiculoCantidadBeans.getCantidad() > 0) {
                    vehiculoList = vehiculosActivo(tipoVehiculoCantidadBeans.getTipovehiculo());
                    if (vehiculoList == null || vehiculoList.isEmpty()) {
                        JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohayvehiculosactivosconesascondiciones"));
                        return "";
                    }
                    vehiculoFreeList = new ArrayList<>();
                    Integer numeroVehiculosSolicitados = solicitud.getNumerodevehiculos();
                    if (diasconsecutivos) {
                        //dias consecutivos
                        Integer numeroBuses = 0;
                        Integer numeroPasajeros = 0;
                        for (Vehiculo v : vehiculoList) {

                            if (isVehiculoActivoDisponible(v, solicitud.getFechahorapartida(), solicitud.getFechahoraregreso())) {
                                //agrega a la lista los vehiculos disponibles
                                vehiculoFreeList.add(v);
                                pasajerosDisponibles = 0;
                                numeroBuses++;
                                numeroPasajeros += v.getPasajeros();
                            }
                        }
//ordena la lista de vehiculos
                        vehiculoFreeList.sort(Comparator.comparingDouble(Vehiculo::getPasajeros)
                                .reversed());
//Almacena los vehiculos disponibles
                        DisponiblesBeans disponiblesBeans = new DisponiblesBeans();
                        disponiblesBeans.setIddisponible(iddisponible++);
                        disponiblesBeans.setFechahorainicio(solicitud.getFechahorapartida());
                        disponiblesBeans.setFechahorafin(solicitud.getFechahoraregreso());
                        disponiblesBeans.setNumeroBuses(numeroBuses);
                        disponiblesBeans.setNumeroPasajeros(numeroPasajeros);
                        disponiblesBeans.setVehiculo(vehiculoFreeList);
                        disponiblesBeans.setBusesRecomendados(vehiculosRecomendados(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setPasajerosPendientes(pasajerosRecomendados(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setPasajerosPorViaje(generarPasajerosPorViajes(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setNumeroVehiculosSolicitados(tipoVehiculoCantidadBeans.getCantidad());
                        disponiblesBeans.setNumeroPasajerosSolicitados(tipoVehiculoCantidadBeans.getPasajeros());
                        disponiblesBeansList.add(disponiblesBeans);

                    } else {


                        /*
                No son dias consecutivo
                Se deben descomponer las fechas
                verificar si son dias validos
                Descomponener la fecha de inicio
                         */
                        DecomposedDate fechaPartidaDescompuesta = DateUtil.descomponerFecha(solicitud.getFechahorapartida());

                        //descomponer la fecha de regreso
                        DecomposedDate fechaRegresoDescompuesta = DateUtil.descomponerFecha(solicitud.getFechahoraregreso());

                        varAnio = fechaPartidaDescompuesta.getYear();
                        //Determinar cuantos meses hay
                        Integer meses = DateUtil.numberOfMonthBetweenDecomposedDate(fechaPartidaDescompuesta, fechaRegresoDescompuesta);
                        //mismo mes
                        if (meses == 0) {
                            foundVehicleSameMonth(fechaPartidaDescompuesta, fechaRegresoDescompuesta, vehiculoList, tipoVehiculoCantidadBeans);

                        } else {
                            // mas de un mes recorrer todos los meses en ese intervalo
                            if (meses > 0 && meses <= 12) {

                                foundVehicleOtherMonth(fechaPartidaDescompuesta, fechaRegresoDescompuesta, vehiculoList, meses, tipoVehiculoCantidadBeans);

                            } else {
                                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.verifiqueestasolicitandomas12meses"));
                                return "";
                            }
                        }

                    }// no son consecutivos
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean isValidDiasConsecutivos()">
    /**
     * Valida los dias consecutivos
     *
     * @return
     */
    private Boolean isValidDiasConsecutivos() {
        Boolean valid = false;
        try {
            Integer c = 0;
            diasconsecutivos = false;
            for (String d : diasSelected) {
                if (d.equals("Dia/ Dias Consecutivo")) {
                    diasconsecutivos = true;
                }
                rangoAgenda.add(d);
                c++;

            }
            if (c > 1 && diasconsecutivos) {

                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.diasconsecutivosinvalidos"));

                return false;
            }
            valid = true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Boolean beforePrepareGoNew()">
    @Override
    public Boolean beforePrepareNew() {
        try {
            disponiblesBeansList = new ArrayList<>();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return true;
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="String sendEmail(String msg)">
    private String sendEmail(String msg) {
        try {
            /**
             * Enviar un email al administrador y al mismo administrador
             */
            Solicitud s0 = solicitudGuardadasList.get(0);

            String varFacultadName = "";
            String varCarreraName = "";
            String varRango = "";
            varFacultadName = s0.getFacultad().stream().map((f) -> "" + f.getDescripcion()).reduce(varFacultadName, String::concat);
            for (Carrera c : s0.getCarrera()) {
                varCarreraName = "" + c.getDescripcion();
            }
            for (String r : s0.getRangoagenda()) {
                varRango = "" + r;
            }
            String header = "\n Detalle de la solicitud:"
                    + "\nObjetivo : " + s0.getObjetivo()
                    + "\nObservaciones: " + s0.getObservaciones()
                    + "\nLugares: " + s0.getLugares()
                    + "\nLugar de partida: " + s0.getLugarpartida()
                    + "\nLugar de llegada: " + s0.getLugarllegada()
                    + "\nFacultad: " + varFacultadName
                    + "\nCarrera: " + varCarreraName
                    + "\nRango: " + varRango
                    + "\nEstatus: " + s0.getEstatus().getIdestatus() + "";

            String texto = "\n___________________________SOLICITUDES___________________________________";
            texto += "\n" + String.format("%10s %25s %30s %30s %20s", "#", "Partida", "Regreso", "Pasajeros", "Vehiculo");

            for (Solicitud s : solicitudGuardadasList) {

                texto += "\n" + String.format("%10d %20s %25s %10d %20s",
                        s.getIdsolicitud(),
                        DateUtil.dateFormatToString(s.getFechahorapartida(), "dd/MM/yyyy hh:mm a"),
                        DateUtil.dateFormatToString(s.getFechahoraregreso(), "dd/MM/yyyy hh:mm a"),
                        s.getPasajeros(),
                        s.getTipovehiculo().get(0).getIdtipovehiculo());
                texto += "\n________________________________________________________________________";

            }

            String mensajeAdmin = "Hay solicitudes " + msg + "  de :" + solicita.getNombre()
                    + "\nemail:" + solicita.getEmail()
                    + "\n" + header
                    + "\n" + texto
                    + "\n Por favor ingrese al sistema de transporte para verificarlas.";
            String mensaje = "Su solicitud ha";
            if (solicitudGuardadasList.size() > 1) {
                mensaje = "Sus solicitudes se han ";
            }
            mensaje += "  registrado en el sistema de Transporte"
                    + "\n este pendiente de la aprobaciòn o rechazo de la misma"
                    + "\n se le enviara un correo informandole al respecto"
                    + "\n o puede ingresar al sistema y consultar su estatus."
                    + "\n"
                    + "\n " + header
                    + texto
                    + "\n Muchas gracias.";

            List<JmoordbEmailMaster> jmoordbEmailMasterList = jmoordbEmailMasterRepository.findBy(new Document("activo", "si"));
            if (jmoordbEmailMasterList == null || jmoordbEmailMasterList.isEmpty()) {

            } else {
                JmoordbEmailMaster jmoordbEmailMaster = jmoordbEmailMasterList.get(0);
                //enviar al administrativo

                Future<String> completableFuture = sendEmailAsync(responsable.getEmail(), "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
                //    Future<String> completableFuture = managerEmail.sendAsync(responsable.getEmail(), "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));

//String msg =completableFuture.get();
                //BUSCA LOS USUARIOS QUE SON ADMINISTRADORES O SECRETARIA
                if (usuarioList == null || usuarioList.isEmpty()) {

                } else {

//                    usuarioList.forEach((u) -> {
//                        managerEmail.sendOutlook(u.getEmail(), "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
//                    });
//Divide para las copias y bcc,cc
                    Integer size = usuarioList.size();
                    String[] to; // list of recipient email addresses
                    String[] cc;
                    String[] bcc;

                    if (size <= 5) {
                        to = new String[usuarioList.size()];
                        cc = new String[0];
                        bcc = new String[0];
                    } else {
                        if (size > 5 && size <= 10) {
                            to = new String[4];
                            cc = new String[4];
                            bcc = new String[0];
                        } else {
                            to = new String[4];
                            cc = new String[4];
                            bcc = new String[size - 8];
                        }
                    }
                    index = 0;
                    usuarioList.forEach((u) -> {

                        if (index <= 5) {
                            to[index] = u.getEmail();
                        } else {
                            if (index > 5 && index <= 10) {
                                cc[index] = u.getEmail();
                            } else {

                                bcc[index] = u.getEmail();

                            }
                        }
                        index++;
                    });

                    Future<String> completableFutureCC = sendEmailCccBccAsync(to, cc, bcc, "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
//                  Future<String> completableFutureCC = managerEmail.sendAsync(to, cc, bcc, "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
                }

            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="String sendEmail(String msg)">
    private String sendEmailVistoBueno(Solicitud solicitud, String aprobado) {
        try {
            /**
             * Enviar un email al administrador y al mismo administrador
             */
            Solicitud s0 = solicitud;

            String varFacultadName = "";
            String varCarreraName = "";
            String varRango = "";
            varFacultadName = s0.getFacultad().stream().map((f) -> "" + f.getDescripcion()).reduce(varFacultadName, String::concat);
            for (Carrera c : s0.getCarrera()) {
                varCarreraName = "" + c.getDescripcion();
            }
            for (String r : s0.getRangoagenda()) {
                varRango = "" + r;
            }
            String header = "\n Detalle de la solicitud:"
                    + "\nObjetivo : " + s0.getObjetivo()
                    + "\nObservaciones: " + s0.getObservaciones()
                    + "\nLugares: " + s0.getLugares()
                    + "\nLugar de partida: " + s0.getLugarpartida()
                    + "\nLugar de llegada: " + s0.getLugarllegada()
                    + "\nFacultad: " + varFacultadName
                    + "\nCarrera: " + varCarreraName
                    + "\nRango: " + varRango
                    + "\nEstatus: " + s0.getEstatus().getIdestatus() + "";

            String texto = "\n___________________________SOLICITUDES___________________________________";
            texto += "\n" + String.format("%10s %25s %30s %30s %20s", "#", "Partida", "Regreso", "Pasajeros", "Vehiculo");

            texto += "\n" + String.format("%10d %20s %25s %10d %20s",
                    solicitud.getIdsolicitud(),
                    DateUtil.dateFormatToString(solicitud.getFechahorapartida(), "dd/MM/yyyy hh:mm a"),
                    DateUtil.dateFormatToString(solicitud.getFechahoraregreso(), "dd/MM/yyyy hh:mm a"),
                    solicitud.getPasajeros(),
                    solicitud.getTipovehiculo().get(0).getIdtipovehiculo());
            texto += "\n________________________________________________________________________";

            String text = "El coordinador ";
            if (aprobado.toLowerCase().equals("si")) {
                text += " Aprobo  el visto bueno para la solicitud " + solicitud.getIdsolicitud();
                ;
            } else {
                text += " Rechazo el visto bueno para la solicitud " + solicitud.getIdsolicitud();
            }
            String mensajeAdmin = text + "   de :" + solicitud.getUsuario().get(0).getNombre()
                    + "\nemail:" + solicitud.getUsuario().get(0).getEmail()
                    + "\n" + header
                    + "\n" + texto
                    + "\n Por favor ingrese al sistema de transporte para verificarlas.";

            List<JmoordbEmailMaster> jmoordbEmailMasterList = jmoordbEmailMasterRepository.findBy(new Document("activo", "si"));
            if (jmoordbEmailMasterList == null || jmoordbEmailMasterList.isEmpty()) {

            } else {
                JmoordbEmailMaster jmoordbEmailMaster = jmoordbEmailMasterList.get(0);
                //enviar al administrativo

                Future<String> completableFuture = sendEmailAsync(responsable.getEmail(), "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));

                //BUSCA LOS USUARIOS QUE SON ADMINISTRADORES O SECRETARIA
                if (usuarioList == null || usuarioList.isEmpty()) {

                } else {

//Divide para las copias y bcc,cc
                    Integer size = usuarioList.size();
                    String[] to; // list of recipient email addresses
                    String[] cc;
                    String[] bcc;

                    if (size <= 5) {
                        to = new String[usuarioList.size()];
                        cc = new String[0];
                        bcc = new String[0];
                    } else {
                        if (size > 5 && size <= 10) {
                            to = new String[4];
                            cc = new String[4];
                            bcc = new String[0];
                        } else {
                            to = new String[4];
                            cc = new String[4];
                            bcc = new String[size - 8];
                        }
                    }
                    index = 0;
                    usuarioList.forEach((u) -> {

                        if (index <= 5) {
                            to[index] = u.getEmail();
                        } else {
                            if (index > 5 && index <= 10) {
                                cc[index] = u.getEmail();
                            } else {

                                bcc[index] = u.getEmail();

                            }
                        }
                        index++;
                    });

                    Future<String> completableFutureCC = sendEmailCccBccAsync(to, cc, bcc, "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
//                  Future<String> completableFutureCC = managerEmail.sendAsync(to, cc, bcc, "{Sistema de Transporte}", mensajeAdmin, jmoordbEmailMaster.getEmail(), JsfUtil.desencriptar(jmoordbEmailMaster.getPassword()));
                }

            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean foundVehicleSameMonth(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta, List<Vehiculo> vehiculoList,TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans)">
    private Boolean foundVehicleSameMonth(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta, List<Vehiculo> vehiculoList, TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans) {
        try {

// Encontrar las fechas en el rango
            Integer numeroBuses = 0;
            Integer numeroPasajeros = 0;

            List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
            fechasValidasList = validarRangoFechas(fechaPartidaDescompuesta.getYear(), fechaPartidaDescompuesta.getNameOfMonth());
            //recorre todos los vehiculos 

            Integer pasajerosPendientes = solicitud.getPasajeros();
            Integer pasajeros = 0;
            if (fechasValidasList.isEmpty()) {
                //no hay fechas para guardar
                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos"));
                return false;
            } else {
                //Recorre las fechas validas
                insertIntoDisponiblesList(fechaPartidaDescompuesta, fechaRegresoDescompuesta, fechasValidasList, vehiculoList, tipoVehiculoCantidadBeans);

            }//isEmpty

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean foundVehicleOtherMonth(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta, List<Vehiculo> vehiculoList,TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans)">
    private Boolean foundVehicleOtherMonth(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta, List<Vehiculo> vehiculoList, Integer meses, TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans) {
        try {
            Integer numeroBuses = 0;
            Integer numeroPasajeros = 0;
            for (int i = 0; i <= meses; i++) {
                //Verificar si es el mismo año
                if (fechaPartidaDescompuesta.getYear().equals(fechaRegresoDescompuesta.getYear())) {
                    Integer m = fechaPartidaDescompuesta.getMonth() + i;
                    String nameOfMohth = DateUtil.nombreMes(m);
                    List<FechaDiaUtils> list = validarRangoFechas(fechaPartidaDescompuesta.getYear(), nameOfMohth);
                    List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
                    if (list == null || list.isEmpty()) {
                        JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos") + " Mes;" + nameOfMohth);

                    } else {
                        list.forEach((f) -> {
                            fechasValidasList.add(f);
                        });
                    }
                    if (fechasValidasList == null || fechasValidasList.isEmpty()) {
                        JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohayfechasvalidas"));
                    } else {
                        insertIntoDisponiblesList(fechaPartidaDescompuesta, fechaRegresoDescompuesta, fechasValidasList, vehiculoList, tipoVehiculoCantidadBeans);

                    }

                } else {
                    //cambio el año   
                    Integer m = fechaPartidaDescompuesta.getMonth() + i;
                    if (m == 12) {
                        varAnio = varAnio + 1;
                    }
                    if (m >= 12) {
                        m = m - 12;

                    }

                    String nameOfMohth = DateUtil.nombreMes(m);

                    List<FechaDiaUtils> list = validarRangoFechas(varAnio, nameOfMohth);
                    List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
                    if (list == null || list.isEmpty()) {
                        JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos") + " Mes: " + nameOfMohth);
                        //return "";
                    } else {
                        list.forEach((f) -> {
                            fechasValidasList.add(f);
                        });
                    }
                    if (fechasValidasList == null || fechasValidasList.isEmpty()) {
                        JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohayfechasvalidas"));
                    } else {
                        insertIntoDisponiblesList(fechaPartidaDescompuesta, fechaRegresoDescompuesta, fechasValidasList, vehiculoList, tipoVehiculoCantidadBeans);

                    }

                }

            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean insertIntoDisponiblesList(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta,List<FechaDiaUtils> fechasValidasList, List<Vehiculo> vehiculoList ,TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans)">
    /**
     * inserta elementos en disponiblesBeansList y vehiculosList
     *
     * @param fechaPartidaDescompuesta
     * @param fechaRegresoDescompuesta
     * @param fechasValidasList
     * @param vehiculoList
     * @return
     */
    private Boolean insertIntoDisponiblesList(DecomposedDate fechaPartidaDescompuesta, DecomposedDate fechaRegresoDescompuesta, List<FechaDiaUtils> fechasValidasList, List<Vehiculo> vehiculoList, TipoVehiculoCantidadBeans tipoVehiculoCantidadBeans) {
        try {
            Integer contadorDispobibes = 0;
            Integer numeroBuses = 0;
            Integer numeroPasajeros = 0;
            if (fechasValidasList == null || fechasValidasList.isEmpty()) {
                JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohayfechasvalidas"));
            } else {
                for (FechaDiaUtils f : fechasValidasList) {
                    List<Vehiculo> vehiculoFreeList = new ArrayList<>();
                    numeroBuses = 0;
                    numeroPasajeros = 0;
                    //si es un dia valido
                    if (isValidDayName(f.getName())) {
                        Date newDatePartida = DateUtil.setHourToDate(DateUtil.convertirLocalDateToJavaDate(f.getDate()), fechaPartidaDescompuesta.getHour(), fechaPartidaDescompuesta.getMinute());
                        Date newDateRegreso = DateUtil.setHourToDate(DateUtil.convertirLocalDateToJavaDate(f.getDate()), fechaRegresoDescompuesta.getHour(), fechaRegresoDescompuesta.getMinute());
                        for (Vehiculo v : vehiculoList) {

                            if (isVehiculoActivoDisponible(v, newDatePartida, newDateRegreso)) {
                                //agrega a la lista los vehiculos disponibles
                                vehiculoFreeList.add(v);
                                pasajerosDisponibles = 0;
                                numeroBuses++;
                                numeroPasajeros += v.getPasajeros();
                            }

                        }
                        vehiculoFreeList.sort(Comparator.comparingDouble(Vehiculo::getPasajeros)
                                .reversed());
                        DisponiblesBeans disponiblesBeans = new DisponiblesBeans();
                        disponiblesBeans.setIddisponible(++contadorDispobibes);
                        disponiblesBeans.setFechahorainicio(newDatePartida);
                        disponiblesBeans.setFechahorafin(newDateRegreso);
                        disponiblesBeans.setNumeroBuses(numeroBuses);
                        disponiblesBeans.setNumeroPasajeros(numeroPasajeros);
                        disponiblesBeans.setVehiculo(vehiculoFreeList);

                        disponiblesBeans.setBusesRecomendados(vehiculosRecomendados(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setPasajerosPendientes(pasajerosRecomendados(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setPasajerosPorViaje(generarPasajerosPorViajes(vehiculoFreeList, tipoVehiculoCantidadBeans.getPasajeros()));
                        disponiblesBeans.setNumeroVehiculosSolicitados(tipoVehiculoCantidadBeans.getCantidad());
                        disponiblesBeans.setNumeroPasajerosSolicitados(tipoVehiculoCantidadBeans.getPasajeros());
                        disponiblesBeansList.add(disponiblesBeans);
                    }
                }

            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="vehiculosRecomendados(List<Vehiculo> vehiculoDisponiblesList)">
    /**
     * Devuelve la cantidad de vehiculos recomendados en base a los disponibles
     *
     * @param vehiculoDisponiblesList
     * @return
     */
    private Integer vehiculosRecomendados(List<Vehiculo> vehiculoDisponiblesList, Integer pasajeros) {
        Integer totalVehiculos = 0;
        try {
            Integer mayorCapacidad = vehiculoDisponiblesList.get(0).getPasajeros();
            Integer pasajerosPendientes = pasajeros;
            for (Vehiculo v : vehiculoDisponiblesList) {

                if (pasajerosPendientes > 0) {
                    totalVehiculos++;
                    pasajerosPendientes -= v.getPasajeros();
                    if (pasajerosPendientes < 0) {
                        pasajerosPendientes = 0;
                    }
                }
            }
            if (pasajerosPendientes > 0) {
                if (pasajerosPendientes <= mayorCapacidad) {
                    totalVehiculos++;
                } else {
                    Integer residuo = pasajerosPendientes % mayorCapacidad;
                    Integer divisor = pasajerosPendientes / mayorCapacidad;
                    if (residuo > 0) {
                        divisor++;
                    }
                    totalVehiculos += divisor;
                }

            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return totalVehiculos;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Integer pasajerosRecomendados(List<Vehiculo> vehiculoDisponiblesList, Integer pasajeros)">
    /**
     * Devuelve la cantidad de pasajeros que quedan pendientes
     *
     * @param vehiculoDisponiblesList
     * @return
     */
    private Integer pasajerosRecomendados(List<Vehiculo> vehiculoDisponiblesList, Integer pasajeros) {
        Integer pasajerosPendientes = pasajeros;
        try {
            Integer mayorCapacidad = vehiculoDisponiblesList.get(0).getPasajeros();
            for (Vehiculo v : vehiculoDisponiblesList) {

                if (pasajerosPendientes > 0) {
                    pasajerosPendientes -= v.getPasajeros();
                    if (pasajerosPendientes < 0) {
                        pasajerosPendientes = 0;
                    }

                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return pasajerosPendientes;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="List<Integer> listPasajerosRecomendados(List<Vehiculo> vehiculoDisponiblesList, Integer pasajeros)">
    /**
     * Devuelve la lista de pasajeros recomendados para cada viaje
     *
     * @param vehiculoDisponiblesList
     * @return
     */
    private List<Integer> generarPasajerosPorViajes(List<Vehiculo> vehiculoDisponiblesList, Integer pasajeros) {

        List<Integer> pasajerosRecomendadosList = new ArrayList<>();
        try {
            Integer mayorCapacidad = vehiculoDisponiblesList.get(0).getPasajeros();
            Integer pasajerosPendientes = pasajeros;

            if (pasajeros <= mayorCapacidad) {
                //Si es igual o menor que la capacidad del bus con mayor capacidad
                pasajerosRecomendadosList.add(pasajeros);
            } else {
                for (Vehiculo v : vehiculoDisponiblesList) {
                    if (pasajerosPendientes > 0) {
                        if (pasajerosPendientes >= v.getPasajeros()) {
                            pasajerosPendientes -= v.getPasajeros();
                            pasajerosRecomendadosList.add(v.getPasajeros());
                        } else {

                            pasajerosRecomendadosList.add(pasajerosPendientes);
                            pasajerosPendientes = 0;
                        }

                    }

                }
                // revisa los pendientes

                if (pasajerosPendientes <= mayorCapacidad) {
                    pasajerosRecomendadosList.add(pasajerosPendientes);
                } else {
                    Integer residuo = pasajerosPendientes % mayorCapacidad;
                    Integer divisor = pasajerosPendientes / mayorCapacidad;

                    if (residuo > 0) {
                        divisor++;
                    }

                    for (Integer i = 1; i <= divisor; i++) {
                        if (i < divisor) {
                            pasajerosRecomendadosList.add(mayorCapacidad);
                        } else {
                            pasajerosRecomendadosList.add(residuo);
                        }
                    }

                }
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return pasajerosRecomendadosList;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String  cancel()">
    public String cancel() {
        try {
            Solicitud item = (Solicitud) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("item");
            if (item.getEstatus().getIdestatus().equals("SOLICITADO")) {
                return "";
            } else {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.soloseeditanlossolicitados"));
                return "";
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="String delete())">
    /*
    En lugar de eliminarla se pasa el estado a CANCELADO.
     */
    @Override
    public String delete() {
        try {
            if (!beforeDelete()) {
                return "";
            }
            Document doc = new Document("idestatus", "CANCELADO");
            Estatus estatus = new Estatus();
            estatus.setIdestatus("CANCELADO");
            Optional<Estatus> optional = estatusRepository.findById(estatus);
            if (!optional.isPresent()) {
                JsfUtil.warningMessage(rf.getMessage("warning.noexisteestatuscancelado"));
                return "";
            }
            estatus = optional.get();
            solicitud.setEstatus(estatus);
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            solicitud = solicitudRepository.addUserInfoForEditMethod(solicitud, jmoordb_user.getUsername(), "update");
            /**
             * Verifica si el viaje se realizo si es asi no se puede eliminar si
             * tiene viaje se van a limpiar esos viajes
             */
            Boolean isRealizado = false;
            if (solicitud.getViaje() == null || solicitud.getViaje().isEmpty()) {

            } else {
                for (Viaje v : solicitud.getViaje()) {
                    if (v.getRealizado().equals("si")) {
                        isRealizado = true;
                    }
                }

            }

            if (isRealizado) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nosepuedecancelartieneviajesrealizados"));
            }
            List<Viaje> viajeList = new ArrayList<>();
            solicitud.setViaje(viajeList);
//Remuevo los viajes que tenga asignados
            if (solicitudRepository.update(solicitud)) {
                JsfUtil.infoDialog("Mensaje", rf.getMessage("info.cancelacionsolicitudes"));
                //guarda el contenido anterior
                JmoordbConfiguration jmc = new JmoordbConfiguration();
                Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
                RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
                repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                        "cancel", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            } else {

                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.cancelacionsolicitudesfallida"));
            }

            usuarioList = usuarioServices.usuariosParaNotificar(facultadList);
//            //  Guardar las notificaciones
//Verifica si es el mismo coordinador quien hace la solicitud, si es asi colocar aprobado directamente
            Boolean vistoBuenoAprobado = usuarioServices.esElCoordinadorQuienSolicita(usuarioList, jmoordb_user);

            //Si es el mismo usuario el coordinador removerlo para no enviarle notificaciones
            if (vistoBuenoAprobado) {
//                    usuarioList.remove(jmoordb_user);
                usuarioList = usuarioServices.removerCoordinadorLista(usuarioList, jmoordb_user);
            }
            if (usuarioList == null || usuarioList.isEmpty()) {
            } else {
                usuarioList.forEach((u) -> {
                    notificacionServices.saveNotification("Nueva solicitud de: " + responsable.getNombre(), u.getUsername(), "solicituddocente");

                });
                push.send("Se cancelo una solicitud ");
            }
            solicitudGuardadasList = new ArrayList<>();
            solicitudGuardadasList.add(solicitud);
            /**
             * Enviar un email al administrativo y al mismo administrador
             */
            sendEmail(" cancelada(s) ");

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onCellEdit(CellEditEvent event)">
    /**
     * analiza los cambios en la celda del datatable
     *
     * @param event
     */
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        int alteredRow = event.getRowIndex();
        //Obtiene el encabezado de la columna que se edita la propiedad headerText
        UIColumn col = (UIColumn) event.getColumn();

        Integer v = (Integer) newValue;
        switch (col.getHeaderText().toUpperCase()) {
            case "VEHICULOS":
                if (v < 0 || v > tipoVehiculoCantidadBeansList.get(alteredRow).getMaximo()) {
                    tipoVehiculoCantidadBeansList.get(alteredRow).setCantidad(0);
                }
                break;
            case "PASAJEROS":
                if (tipoVehiculoCantidadBeansList.get(alteredRow).getPasajeros() < 0) {
                    tipoVehiculoCantidadBeansList.get(alteredRow).setPasajeros(0);
                }
                break;
        }

        changeDaysViewAvailable();
        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }   // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Integer calcularTotalVehiculo() ">
    public Integer calcularTotalVehiculo() {
        Integer total = 0;

        try {
            for (TipoVehiculoCantidadBeans tvc : tipoVehiculoCantidadBeansList) {
                total += tvc.getCantidad();

            }
            solicitud.setNumerodevehiculos(total);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return total;
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="Integer calcularTotalPasajeros()">
    public Integer calcularTotalPasajeros() {

        Integer totalpasajeros = 0;
        try {
            for (TipoVehiculoCantidadBeans tvc : tipoVehiculoCantidadBeansList) {

                totalpasajeros += tvc.getPasajeros();
            }
            solicitud.setPasajeros(totalpasajeros);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return totalpasajeros;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Boolean isValidDisponibles()">
    /**
     * Verifica que la lista de disponibles sea valida en base cantidad de
     * pasajeros buses o recomandados
     *
     * @return
     */
    private Boolean isValidDisponibles() {
        Boolean valid = true;
        try {
            for (DisponiblesBeans db : disponiblesBeansList) {
                if (db.getBusesRecomendados() == 0 || db.getNumeroPasajerosSolicitados() == 0 || db.getNumeroVehiculosSolicitados() == 0) {
                    valid = false;
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="String aceptarVistoBueno(Solicitud solicitud, String aprobado) ">
    public String aceptarVistoBuenoSecretarioAdministrativo(Solicitud solicitud, String aprobado) {
        try {
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            solicitud.setVistoBuenoSecretarioAdministrativo(vistoBuenoSecretarioAdministrativoServices.aprobar(jmoordb_user, aprobado));

            solicitudRepository.update(solicitud);
            JsfUtil.infoDialog("Mensaje", rf.getMessage("info.editado"));
            //guarda el contenido anterior
            JmoordbConfiguration jmc = new JmoordbConfiguration();
            Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
            RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
            repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                    "update vistobueno secretario administrativo", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            //Obtiene la lista de usuarios para notificar
            usuarioList = usuarioServices.usuariosParaNotificar(facultadList);
            //Verifica si es el mismo coordinador quien hace la solicitud, si es asi colocar aprobado directamente
            Boolean vistoBuenoAprobado = usuarioServices.esElCoordinadorQuienSolicita(usuarioList, jmoordb_user);

            //Si es el mismo usuario el coordinador removerlo para no enviarle notificaciones
            if (vistoBuenoAprobado) {
                usuarioList = usuarioServices.removerCoordinadorLista(usuarioList, jmoordb_user);
            } else {
                //Agrega el docente para que se le envie la notificacion
                usuarioList.add(solicitud.getUsuario().get(0));
            }
            if (usuarioList == null || usuarioList.isEmpty()) {
            } else {
                //Verifica si es un coordinador y le envia la notificacion

//incluir quien la solicito que no sea el administrador
                usuarioList.forEach((u) -> {
                    String messages = "";
                    if (aprobado.toLowerCase().equals("si")) {
                        messages = "Se aprobo el visto bueno de la solicitud No, " + solicitud.getIdsolicitud() + " por " + jmoordb_user.getNombre();
                    } else {
                        messages = "Se rechazo el visto bueno de la solicitud No, " + solicitud.getIdsolicitud() + " por " + jmoordb_user.getNombre();
                    }

                    notificacionServices.saveNotification(messages, u.getUsername(), "vistobuenosolicitud");

                });

                //Envia la notificacion.....
                push.send("Nueva solicitud Docente ");

                sendEmailVistoBueno(solicitud, aprobado);
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="String onVistoBuenoChange()">
    public String onVistoBuenoChange() {
        try {
             setSearchAndValue("vistobuenocoordinador", vistoBuenoSearch);
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String onVistoBuenoChangeSecretarioAdministrativo()">

    public String onVistoBuenoChangeSecretarioAdministrativo() {
        try {
            setSearchAndValue("vistobuenosecretarioadministrativo", vistoBuenoSecretarioAdministrativoSearch);
           
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String onVistoBuenoChangeSecretarioAdministrativo()">

    public String onRealizadoCalenadarioViajes() {
        try {
            Document doc = new Document("activo", "si").append("realizado", viajeSecretarioAdministrativoSearch);
            loadScheduleViajes(doc);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String columnNameVistoBueno(VistoBueno vistoBueno) ">
    public String columnNameVistoBuenoSecretarioAdministrativo(VistoBuenoSecretarioAdministrativo vistoBuenoSecretarioAdministrativo) {
        return vistoBuenoSecretarioAdministrativoServices.columnNameVistoBuenoSecretarioAdministrativo(vistoBuenoSecretarioAdministrativo);
    }
// </editor-fold>

}
