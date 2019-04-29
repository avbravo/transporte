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
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.repository.Repository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.dates.FechaDiaUtils;
import com.avbravo.jmoordbutils.printer.Printer;

import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.datamodel.SugerenciaDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Sugerencia;
import com.avbravo.transporteejb.entity.Tiposolicitud;
import com.avbravo.transporteejb.entity.Tipovehiculo;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.SugerenciaRepository;
import com.avbravo.transporteejb.repository.TipovehiculoRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.TipogiraServices;
import com.avbravo.transporteejb.services.TiposolicitudServices;
import com.avbravo.transporteejb.services.TipovehiculoServices;
import com.avbravo.transporteejb.services.UsuarioServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
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
public class SolicitudDocenteController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Boolean leyoSugerencias = false;
    Boolean diasconsecutivos = false;
    //DataModel
    private SolicitudDataModel solicitudDataModel;
    private SugerenciaDataModel sugerenciaDataModel;
    private Date varFechaHoraPartida;
    private Date varFechaHoraRegreso;
    private Integer varAnio;

    private Integer totalAprobado = 0;
    private Integer totalSolicitado = 0;
    private Integer totalRechazadoCancelado = 0;
    private Integer totalViajes = 0;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    Integer page = 1;
    Integer rowPage = 25;
    private String stmpPort = "25";
    List<Integer> pages = new ArrayList<>();
    List<Sugerencia> sugerenciaList = new ArrayList<>();

    //Entity
    Solicitud solicitud = new Solicitud();
    Solicitud solicitudSelected;
    Solicitud solicitudSearch = new Solicitud();
    Usuario solicita = new Usuario();
    Usuario responsable = new Usuario();
    Usuario responsableOld = new Usuario();
    Solicitud solicitudCopiar = new Solicitud();

    //List
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudFiltered = new ArrayList<>();
    List<Unidad> unidadList = new ArrayList<>();
    List<Facultad> facultadList = new ArrayList<>();
    List<Carrera> carreraList = new ArrayList<>();
    List<Usuario> usuarioList = new ArrayList<>();
    List<Tiposolicitud> tiposolicitudList = new ArrayList<>();
    List<Tipovehiculo> tipovehiculoList = new ArrayList<>();
    List<Tipovehiculo> suggestionsTipovehiculo = new ArrayList<>();
    //
    private String[] diasSelected;
    private List<String> diasList;
    List<Facultad> suggestionsFacultad = new ArrayList<>();
    List<Carrera> suggestionsCarrera = new ArrayList<>();
    List<Unidad> suggestionsUnidad = new ArrayList<>();
    List<Tiposolicitud> suggestionsTiposolicitud = new ArrayList<>();

    //Repository
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    FacultadRepository facultadRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    SugerenciaRepository sugerenciaRepository;
    @Inject
    TipovehiculoRepository tipovehiculoRepository;
    @Inject
    UsuarioRepository usuarioRepository;
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
    UsuarioServices usuarioServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

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
    public SolicitudDocenteController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            eventModel = new DefaultScheduleModel();
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
                    .withPathReportDetail("/resources/reportes/solicitud/details.jasper")
                    .withPathReportAll("/resources/reportes/solicitud/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(false)
                    .build();

            start();
            sugerenciaList = sugerenciaRepository.findBy("activo", "si");
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);
            cargarSchedule();
            String action = JmoordbContext.get("solicitud").toString();
            if (action.equals("gonew")) {
                inicializar();

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
            this.page = page;
            solicitudDataModel = new SolicitudDataModel(solicitudList);
            Document doc;
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            switch ((String) JmoordbContext.get("searchsolicitud")) {
                case "_init":
                case "_autocomplete":

                    doc = new Document("usuario.username", jmoordb_user.getUsername());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

                case "idsolicitud":
                    if (JmoordbContext.get("_fieldsearchsolicitud") != null) {
                        solicitudSearch.setIdsolicitud((Integer) JmoordbContext.get("_fieldsearchsolicitud"));
                        doc = new Document("idsolicitud", solicitudSearch.getIdsolicitud()).append("usuario.username", jmoordb_user.getUsername());
                        solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));
                    } else {
                        solicitudList = solicitudRepository.findPagination(page, rowPage);
                    }

                    break;

                default:
                    doc = new Document("usuario.username", jmoordb_user.getUsername());
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

            List<String> rangoAgenda = new ArrayList<>();
            Integer c = 0;

            for (String d : diasSelected) {
                if (d.equals("Dia/ Dias Consecutivo")) {
                    diasconsecutivos = true;
                }
                c++;
                rangoAgenda.add(d);
            }
            if (c > 1 && diasconsecutivos) {
                JsfUtil.warningDialog("Advertencia", "Cuando selecciona Dia/ Dias Consecutivo no puede seleccionar otro valor");
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

            String textsearch = "ADMINISTRATIVO";
            Rol rol = (Rol) JmoordbContext.get("jmoordb_rol");
            if (rol.getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            if (!solicitudServices.isValid(solicitud)) {
                return false;
            }

            if (!leyoSugerencias) {
                JsfUtil.warningDialog("Advertencia", "Por favor lea las sugerencias");
                return false;
            }

            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean insert()">
    public Boolean insert() {
        try {
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Integer idsolicitud = autoincrementableServices.getContador("solicitud");
            solicitud.setIdsolicitud(idsolicitud);
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
            //Lo datos del usuario

            solicitud.setUserInfo(solicitudRepository.generateListUserinfo(jmoordb_user.getUsername(), "create"));
            if (solicitudRepository.save(solicitud)) {
                //guarda el contenido anterior
                JmoordbConfiguration jmc = new JmoordbConfiguration();
                Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
                RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
                repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                        "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            } else {
                JsfUtil.successMessage("save() " + solicitudRepository.getException().toString());
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
    // <editor-fold defaultstate="collapsed" desc="Integer procesar(List<FechaDiaUtils> fechasValidasList, Integer horapartida, Integer minutopartida, Integer horaregreso, Integer minutoregreso) ">

    private Integer procesar(List<FechaDiaUtils> fechasValidasList, Integer horapartida, Integer minutopartida, Integer horaregreso, Integer minutoregreso) {
        Integer solicitudesGuardadas = 0;
        try {
            for (FechaDiaUtils f : fechasValidasList) {
                //si es un dia valido
                if (isValidDayName(f.getName())) {
                    /**
                     * crear la nueva fechahora de partida crear la nueva
                     * fechahora de regreso
                     */
                    Date newDatePartida = DateUtil.setHourToDate(DateUtil.convertirLocalDateToJavaDate(f.getDate()), horapartida, minutopartida);
                    Date newDateRegreso = DateUtil.setHourToDate(DateUtil.convertirLocalDateToJavaDate(f.getDate()), horaregreso, minutoregreso);

                    solicitud.setFechahorapartida(newDatePartida);
                    solicitud.setFechahoraregreso(newDateRegreso);
                    if (insert()) {
                        solicitudesGuardadas++;
                    }
                    solicitud.setSolicitudpadre(solicitud.getIdsolicitud());

                }

            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return solicitudesGuardadas;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String save()">
    @Override
    public String save() {
        try {
            if (!localValid()) {
                return "";
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
            solicitud.setSolicitudpadre(0);
            varFechaHoraPartida = solicitud.getFechahorapartida();
            varFechaHoraRegreso = solicitud.getFechahoraregreso();

            /**
             * Si es dias consecutivos es un solo intervalo para la reservacion
             * se creara una solicitud para cada vehiculos solicitado
             */
            if (diasconsecutivos) {
                for (Integer index = 0; index < solicitud.getNumerodevehiculos(); index++) {
                    if (insert()) {
                        solicitudesGuardadas++;
                    }
                    //Asigna la solicitud padre para las demas solicitudes
                    solicitud.setSolicitudpadre(solicitud.getIdsolicitud());
                }//for
            } else {

                /*
                No son dias consecutivo
                Se deben descomponer las fechas
                verificar si son dias validos
                Descomponener la fecha de inicio
                 */
                Date fechahorapartidad = solicitud.getFechahorapartida();
                Date fechahoraregreso = solicitud.getFechahoraregreso();

                Integer diaPartida = DateUtil.diaDeUnaFecha(solicitud.getFechahorapartida());
                Integer mesPartida = DateUtil.mesDeUnaFechaStartEneroWith0(solicitud.getFechahorapartida());
                String nombreMesPartida = DateUtil.nombreMes(mesPartida);
                Integer anioPartida = DateUtil.anioDeUnaFecha(solicitud.getFechahorapartida());
                Integer horapartida = DateUtil.horaDeUnaFecha(solicitud.getFechahorapartida());
                Integer minutopartida = DateUtil.minutosDeUnaFecha(solicitud.getFechahorapartida());
                //descomponer la fecha de regreso
                Integer diaRegreso = DateUtil.diaDeUnaFecha(solicitud.getFechahoraregreso());
                Integer mesRegreso = DateUtil.mesDeUnaFechaStartEneroWith0(solicitud.getFechahoraregreso());
                String nombreMesRegreso = DateUtil.nombreMes(mesRegreso);
                Integer anioRegreso = DateUtil.anioDeUnaFecha(solicitud.getFechahoraregreso());
                Integer horaregreso = DateUtil.horaDeUnaFecha(solicitud.getFechahoraregreso());
                Integer minutoregreso = DateUtil.minutosDeUnaFecha(solicitud.getFechahoraregreso());

                varAnio = anioPartida;
                //Determinar cuantos meses hay
                Integer meses = 0;
                if (mesPartida > mesRegreso) {
                    meses = (mesRegreso + 12) - mesPartida;
                } else {
                    meses = mesRegreso - mesPartida;
                }
                //mismo mes
                if (meses == 0) {
// Encontrar las fechas en el rango
                    List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
                    fechasValidasList = validarRangoFechas(anioPartida, nombreMesPartida);
                    //recorre todos los vehiculos 
                    for (int i = 0; i < solicitud.getNumerodevehiculos(); i++) {
                        if (fechasValidasList.isEmpty()) {
                            //no hay fechas para guardar
                            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos"));
                            return "";
                        } else {
                            //ESTOS SON LOS QUE SE GUARDARIAN
                            solicitudesGuardadas = procesar(fechasValidasList, horapartida, minutopartida, horaregreso, minutoregreso);

                        }//isEmpty
                    } //getNumerodevehiculos

                } else {
                    // mas de un mes recorrer todos los meses en ese intervalo
                    if (meses > 0 && meses <= 24) {
                        System.out.println("==> meses " + meses);
                        for (int i = 0; i <= meses; i++) {
                            //Verificar si es el mismo año
                            if (anioPartida.equals(anioRegreso)) {
                                Integer m = mesPartida + i;
                                String nameOfMohth = DateUtil.nombreMes(m);
                                List<FechaDiaUtils> list = validarRangoFechas(anioPartida, nameOfMohth);
                                List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
                                if (list == null || list.isEmpty()) {
                                    JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos") + " Mes;" + nameOfMohth);
                                    //return "";
                                } else {
                                    list.forEach((f) -> {
                                        fechasValidasList.add(f);
                                    });
                                }
                                if (fechasValidasList == null || fechasValidasList.isEmpty()) {

                                } else {
                                    solicitudesGuardadas = procesar(fechasValidasList, horapartida, minutopartida, horaregreso, minutoregreso);
                                }

                            } else {
                                //cambio el año   
                                Integer m = mesPartida + i;
                                if (m == 12) {
                                    varAnio = varAnio + 1;
                                }
                                if (m >= 12) {
                                    m = m - 12;

                                }
                                System.out.println("===========================================================");
                                System.out.println("===> m: " + m + " ==mesPartida " + mesPartida + "==>i " + i);
                                String nameOfMohth = DateUtil.nombreMes(m);
                                System.out.println("===>nameOfMonth " + nameOfMohth + "varAnio "+varAnio);
                                List<FechaDiaUtils> list = validarRangoFechas(varAnio, nameOfMohth);
                                List<FechaDiaUtils> fechasValidasList = new ArrayList<>();
                                if (list == null || list.isEmpty()) {
                                    System.out.println("===> no tiene dias validos en ese rango");
                                    JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nohaydiasvalidosenesosrangos") + " Mes: " + nameOfMohth);
                                    //return "";
                                } else {
                                    list.forEach((f) -> {
                                        fechasValidasList.add(f);
                                    });
                                }
                                if (fechasValidasList == null || fechasValidasList.isEmpty()) {

                                } else {
                                    solicitudesGuardadas = procesar(fechasValidasList, horapartida, minutopartida, horaregreso, minutoregreso);
                                }

                            }

                        }

                    }else{
                         JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.verifiqueestasolicitandomas24meses") );
                         return "";
                    }
                }

            }//no son dias consecutivos

//            JsfUtil.successMessage(rf.getMessage("info.savesolicitudes") + " : " + solicitudesGuardadas.toString() + " " + rf.getMessage("info.solicitudesde") + solicitud.getNumerodevehiculos());
            JsfUtil.successMessage(rf.getMessage("info.savesolicitudes") );
            //Enviar los emails
            facultadList = new ArrayList<>();
            carreraList = new ArrayList<>();
            reset();

            return "";
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="columnColor(String descripcion )">
    public String columnColor(String estatus) {
        String color = "";
        try {
            switch (estatus) {
                case "RECHAZADO":
                    color = "red";
                    break;
                case "APROBADO":
                    color = "green";
                    break;
                default:
                    color = "black";
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return color;
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeSolicitudParaCopiar(String query)">
    public List<Solicitud> completeSolicitudParaCopiar(String query) {
        List<Solicitud> suggestions = new ArrayList<>();
        try {
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            List<Solicitud> list = new ArrayList<>();
            list = solicitudRepository.complete(query);
            if (!list.isEmpty()) {
                for (Solicitud s : list) {
                    if (s.getTiposolicitud().getIdtiposolicitud().equals("DOCENTE")
                            && (s.getUsuario().get(0).getUsername().equals(jmoordb_user.getUsername())
                            || s.getUsuario().get(1).getUsername().equals(jmoordb_user.getUsername()))) {
                        suggestions.add(s);
                    }
                }
            }
            if (!suggestions.isEmpty()) {
                Collections.sort(suggestions,
                        (Solicitud a, Solicitud b) -> a.getIdsolicitud().compareTo(b.getIdsolicitud()));
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return suggestions;
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

    // <editor-fold defaultstate="collapsed" desc="enviarEmails()">
    public String enviarEmails() {
        try {
            Boolean enviados = false;

            final String username = "avbravo@gmail.com";
            final String password = "javnet180denver$";

            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.host", "smtpout.secureserver.net");
            props.put("mail.smtp.port", stmpPort);
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            Integer c = 0;
            List<Usuario> list = usuarioRepository.findBy(new Document("activo", "si"));
            if (!list.isEmpty()) {
                for (Usuario u : list) {
                    if (u.getEmail().contains("@") == true && JsfUtil.emailValidate(u.getEmail())) {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress("avbravo@gmail.com"));

                        c++;

                        message.setRecipients(Message.RecipientType.TO,
                                InternetAddress.parse(u.getEmail()));

                        message.setSubject("Solicitud de Viaje Docente");
                        String texto = "";
                        texto = " <h1> Solicitud #:" + solicitud.getIdsolicitud() + "  </h1>";
                        texto = " <h1> Solicitadi por: " + solicitud.getUsuario().get(0).getNombre() + "  </h1>";
                        texto += " <b>";
                        texto += "<br> Fecha de partidad " + solicitud.getFechahorapartida() + " lugar de salida: " + solicitud.getLugarpartida()
                                + "   <FONT COLOR=\"red\">Pendiente de aprobaciòn </FONT>  ";
                        texto += "</b>";
                        message.setContent(texto, "text/html");

                        Transport.send(message);
                    }
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }    // </editor-fold>

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

                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fecharegresomenorquefechapartida"));
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
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Date idsecond = solicitud.getFecha();
            Integer id = solicitud.getIdsolicitud();

//            List<Solicitud> list = solicitudRepository.findBy(new Document("usuario.username", jmoordb_user.getUsername()).append("fecha", solicitud.getFecha()));
//            if (!list.isEmpty()) {
//                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.yasolicitoviajeenestafecha"));
//            }
//            if (DateUtil.fechaMenor(solicitud.getFecha(), DateUtil.getFechaActual())) {
//                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fechasolicitudmenorqueactual"));
//                writable = false;
//
//            }
            solicitud = new Solicitud();
            solicitudSelected = new Solicitud();
            solicitud.setIdsolicitud(id);
            solicitud.setFecha(idsecond);
            solicitud.setMision("---");
            solicitud.setLugarpartida("UTP-AZUERO");
            solicitud.setNumerodevehiculos(1);
            solicitud.setPasajeros(25);
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

            String textsearch = "ADMINISTRATIVO";
            Rol rol = (Rol) JmoordbContext.get("jmoordb_rol");
            if (rol.getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            solicitudSelected = solicitud;
            leyoSugerencias = false;

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

    // <editor-fold defaultstate="collapsed" desc="cargarSchedule()">
    public void cargarSchedule() {
        try {
            totalAprobado = 0;
            totalSolicitado = 0;
            totalRechazadoCancelado = 0;
            totalViajes = 0;
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Document doc = new Document("usuario.username", jmoordb_user.getUsername());
            List<Solicitud> list = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

            eventModel = new DefaultScheduleModel();
            if (!list.isEmpty()) {
                list.forEach((a) -> {
                    String car = "{ ";
                    car = a.getTipovehiculo().stream().map((t) -> t.getIdtipovehiculo() + " ").reduce(car, String::concat);
                    car += " }";
                    String tema = "schedule-blue";
                    switch (a.getEstatus().getIdestatus()) {
                        case "SOLICITADO":
                            totalSolicitado++;
                            tema = "schedule-orange";
//                            tema="schedule-blue";
                            break;
                        case "APROBADO":
                            totalAprobado++;
                            String viajest = "{";
                            viajest = a.getViaje().stream().map((t) -> t.getIdviaje() + " ").reduce(viajest, String::concat);
                            viajest = "}";
                            car += viajest;
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
//                    eventModel.addEvent(
//                            new DefaultScheduleEvent("# " + a.getIdsolicitud() + " Mision:" + a.getMision() + " Responsable: " + a.getUsuario().get(1).getNombre() + " " + a.getEstatus().getIdestatus(), a.getFechahorapartida(), a.getFechahoraregreso())
//                    );
                    eventModel.addEvent(
                            new DefaultScheduleEvent("# " + a.getIdsolicitud() + " Mision: " + a.getMision() + " Responsable: " + a.getUsuario().get(1).getNombre() + " " + a.getEstatus().getIdestatus()
                                    + car,
                                    a.getFechahorapartida(), a.getFechahoraregreso(), tema)
                    //                    eventModel.addEvent(
                    //                            new DefaultScheduleEvent("# " + a.getIdsolicitud() + " Mision: " + a.getMision() + " Responsable: " + a.getUsuario().get(1).getNombre() + " " + a.getEstatus().getIdestatus()
                    //                                    + car,
                    //                                    a.getFechahorapartida(), a.getFechahoraregreso(), tema)
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
            Integer i = title.indexOf("M");

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

}
