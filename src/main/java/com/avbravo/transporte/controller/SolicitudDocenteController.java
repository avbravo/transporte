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
    //DataModel
    private SolicitudDataModel solicitudDataModel;
    private SugerenciaDataModel sugerenciaDataModel;

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
    //
    private String[] diasSelected;
    private List<String> diasList;

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

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    @Override
    public Boolean beforeSave() {
        try {

            solicitud.setIdsolicitud(autoincrementableServices.getContador("solicitud"));
            solicitud.setFecha(DateUtil.getFechaActual());
            usuarioList = new ArrayList<>();
            usuarioList.add(solicita);
            usuarioList.add(responsable);
            solicitud.setUsuario(usuarioList);

            List<String> rangoAgenda = new ArrayList<>();
            Integer c = 0;
            Boolean rs = false;
            for (String d : diasSelected) {
                if (d.equals("Dia/ Dias Consecutivo")) {
                    rs = true;
                }
                c++;
                rangoAgenda.add(d);
            }
            if (c > 1 && rs) {
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

            Integer solicitudesGuardadas = 0;
            solicitud.setSolicitudpadre(0);
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean afterSave(Boolean saved)">
    @Override
    public Boolean afterSave(Boolean saved) {
        try {
            if (saved) {

                Integer solicitudesGuardadas = 1;
                solicitud.setSolicitudpadre(solicitud.getIdsolicitud());
                for (Integer index = 0; index < solicitud.getNumerodevehiculos(); index++) {
                    if (index == 0) {

                    } else {

                        Integer idsolicitud = autoincrementableServices.getContador("solicitud");
                        solicitud.setIdsolicitud(idsolicitud);
                        Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
                        if (optional.isPresent()) {
                            JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                            return null;
                        }
                        //Lo datos del usuario
                        Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
                        solicitud.setUserInfo(solicitudRepository.generateListUserinfo(jmoordb_user.getUsername(), "create"));
                        if (solicitudRepository.save(solicitud)) {
                            //guarda el contenido anterior
                            JmoordbConfiguration jmc = new JmoordbConfiguration();
                            Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
                            RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
                            repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                                    "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
//enviarEmails();

                            //si cambia el email o celular del responsable actualizar ese usuario
                            if (!responsableOld.getEmail().equals(responsable.getEmail()) || !responsableOld.getCelular().equals(responsable.getCelular())) {
                                usuarioRepository.update(responsable);
                                //actuliza el que esta en el login
                                if (responsable.getUsername().equals(jmoordb_user.getUsername())) {
                                    //  loginController.setUsuario(responsable);
                                }
                            }
                            solicitudesGuardadas++;

                        } else {
                            JsfUtil.successMessage("save() " + solicitudRepository.getException().toString());
                        }
                        //Asigna la solicitud padre para las demas solicitudes

                    }//else
                }//for

                facultadList = new ArrayList<>();
                carreraList = new ArrayList<>();

                //Ver las fechas
                /*
                Descomponener la fecha de inicio
                 */
                Integer diaInicio = DateUtil.diaDeUnaFecha(solicitud.getFechahorapartida());
                Integer numeroMesInicio = DateUtil.mesDeUnaFechaStartEneroWith0(solicitud.getFechahorapartida());
                String nombreMesInicio = DateUtil.nombreMes(numeroMesInicio);
                Integer anioInicial = DateUtil.anioDeUnaFecha(solicitud.getFechahorapartida());
                //descomponer la fecha de regreso
                Integer diaFin = DateUtil.diaDeUnaFecha(solicitud.getFechahoraregreso());
                Integer numeroMesFin = DateUtil.mesDeUnaFechaStartEneroWith0(solicitud.getFechahoraregreso());
                String nombreMesFin = DateUtil.nombreMes(numeroMesFin);
                Integer anioFin = DateUtil.anioDeUnaFecha(solicitud.getFechahoraregreso());

                //Determinar cuantos meses hay
                Integer meses = 0;
                if (numeroMesInicio > numeroMesFin) {
                    meses = (numeroMesFin + 12) - numeroMesInicio;
                } else {
                    meses = numeroMesFin - numeroMesInicio;
                }
                //mismo mes
                if (meses == 0) {
                    List<FechaDiaUtils> fechaDiaUtilsInicialList = DateUtil.nameOfDayOfDateOfMonth(anioInicial, nombreMesInicio);
                    List<FechaDiaUtils> fechaDiaUtilsSaveList = new ArrayList<>();

                    System.out.println(" FECHAS DE SOLICITUD ---> " + solicitud.getFechahorapartida() + " hasta " + solicitud.getFechahoraregreso());
                    LocalDate ld = DateUtil.convertirJavaDateToLocalDate(solicitud.getFechahorapartida());
                    LocalDate ld2 = DateUtil.convertirJavaDateToLocalDate(solicitud.getFechahoraregreso());
                    System.out.println("******* PARTIDA" + ld + " Regreso " + ld2);
                    for (FechaDiaUtils fdu : fechaDiaUtilsInicialList) {
                        System.out.println("  name:" + fdu.getName() + " letter:" + fdu.getLetter() + "date: " + fdu.getDate());
                        LocalDate l = fdu.getDate();

                        if (l.isEqual(ld) || l.isEqual(ld2) || (l.isAfter(ld) && l.isBefore(ld2))) {
                            fechaDiaUtilsSaveList.add(fdu);
                            System.out.println("{-------" + l + "------------> ESTA EN EL INTERVALO......}");
                        } else {
                            //System.out.println(l+"------------> NO ESTA EN EL INTERVALO......"); 
                        }

                    }
                    //recorre todos los vehiculos 
                    for (int i = 0; i < solicitud.getNumerodevehiculos(); i++) {

                        if (fechaDiaUtilsSaveList.isEmpty()) {
                            //no hay fechas para guardar
                            System.out.println("no hay fechas para guardar");
                        } else {
                            //ESTOS SON LOS QUE SE GUARDARIAN
                            System.out.println("----<FECHAS PARA GUARDAR>-----");
                            for (FechaDiaUtils f : fechaDiaUtilsSaveList) {
                                System.out.println("name:" + f.getName() + " letter:" + f.getLetter() + "date: " + f.getDate());
                                //RANGO
                                Boolean valido = false;
                                for (String rango : solicitud.getRangoagenda()) {
                                    if (rango.equals("Dia/ Dias Consecutivo")) {
                                        // guardarlo porque es todo el intervalo
                                        valido = true;
                                    } else {
                                        //verificar que dia es
                                        if (f.getName().equals(rango.toUpperCase())) {
                                            // Guardo porque coincide el dia
                                            valido = true;
                                        }
                                    }
                                }
                                if (valido) {
                                    //Se guarda ya que coincide las fechas
                                    Integer idsolicitud = autoincrementableServices.getContador("solicitud");
                                    solicitud.setIdsolicitud(idsolicitud);
                                    Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
                                    if (optional.isPresent()) {
                                        JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                                        return null;
                                    }
                                    //Lo datos del usuario
                                    Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
                                    solicitud.setUserInfo(solicitudRepository.generateListUserinfo(jmoordb_user.getUsername(), "create"));
                                    if (solicitudRepository.save(solicitud)) {
                                        //guarda el contenido anterior
                                        JmoordbConfiguration jmc = new JmoordbConfiguration();
                                        Repository repositoryRevisionHistory = jmc.getRepositoryRevisionHistory();
                                        RevisionHistoryServices revisionHistoryServices = jmc.getRevisionHistoryServices();
                                        repositoryRevisionHistory.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), jmoordb_user.getUsername(),
                                                "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
//enviarEmails();

                                        //si cambia el email o celular del responsable actualizar ese usuario
                                        if (!responsableOld.getEmail().equals(responsable.getEmail()) || !responsableOld.getCelular().equals(responsable.getCelular())) {
                                            usuarioRepository.update(responsable);
                                            //actuliza el que esta en el login
                                            if (responsable.getUsername().equals(jmoordb_user.getUsername())) {
                                                //  loginController.setUsuario(responsable);
                                            }
                                        }
                                        solicitudesGuardadas++;

                                    } else {
                                        JsfUtil.successMessage("save() " + solicitudRepository.getException().toString());
                                    }
                                    //Asigna la solicitud padre para las demas solicitudes

                                }

                            }

                        }//isEmpty
                    } //getNumerodevehiculos

                } else {
                    if (meses > 0) {

                    }
                }

                JsfUtil.successMessage(rf.getMessage("info.savesolicitudes") + " : " + solicitudesGuardadas.toString() + " " + rf.getMessage("info.solicitudesde") + solicitud.getNumerodevehiculos());
                reset();
            } else {
                //No guardo el primero

            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
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

}
