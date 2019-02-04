/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.DateUtil;
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.commonejb.services.SemestreServices;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Tipovehiculo;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.entity.Viajes;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;

import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajesRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.TiposolicitudServices;
import com.avbravo.transporteejb.services.TipovehiculoServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.bson.Document;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
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
public class CalendarioSolicitudController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

    //    private String stmpPort="80";
    private Boolean esAprobado = false;
    private Boolean esAprobadoParaEditar = false;
    private String stmpPort = "25";
    private String menuelement = "";

    private Date _old;
    private Boolean writable = false;
    private Boolean esDocente = true;
    //DataModel
    private SolicitudDataModel solicitudDataModel;
    Estatus estatus = new Estatus();
    Estatus estatusSelected = new Estatus();
    Tipovehiculo tipovehiculo = new Tipovehiculo();
    Vehiculo vehiculo = new Vehiculo();
    Conductor conductor = new Conductor();
    Vehiculo vehiculoSelected = new Vehiculo();
    Conductor conductorSelected = new Conductor();
    Viajes viajes = new Viajes();
    Viajes viajesSelected = new Viajes();

    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private Boolean mostrarSchedule = false;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();

    //Entity
    Solicitud solicitud;
    Solicitud solicitudSelected;
    Usuario solicita = new Usuario();
    Usuario responsable = new Usuario();
    Usuario responsableOld = new Usuario();

    //List  
    List<Carrera> carreraList = new ArrayList<>();
    List<Facultad> facultadList = new ArrayList<>();
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudFiltered = new ArrayList<>();
    List<Unidad> unidadList = new ArrayList<>();
    List<Viajes> viajesList = new ArrayList<>();

    List<Usuario> usuarioList = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();
    List<Facultad> suggestionsFacultad = new ArrayList<>();
    List<Carrera> suggestionsCarrera = new ArrayList<>();
    List<Unidad> suggestionsUnidad = new ArrayList<>();

    //Repository
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    FacultadRepository facultadRepository;

    @Inject
    UnidadRepository unidadRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;
    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    ViajesRepository viajesRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ConductorRepository conductorRepository;

    //Services
    //Atributos para busquedas
    @Inject
    AutoincrementableTransporteejbServices autoincrementableTransporteejbServices;

    @Inject
    LookupServices lookupServices;
    @Inject
    ErrorInfoTransporteejbServices errorServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    EstatusServices estatusServices;
    @Inject
    SemestreServices semestreServices;

    @Inject
    TiposolicitudServices tiposolicitudServices;
    @Inject
    TipovehiculoServices tipovehiculoServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;
    @Inject
    LoginController loginController;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return solicitudRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public Vehiculo getVehiculoSelected() {
        return vehiculoSelected;
    }

    public void setVehiculoSelected(Vehiculo vehiculoSelected) {
        this.vehiculoSelected = vehiculoSelected;
    }

    public Conductor getConductorSelected() {
        return conductorSelected;
    }

    public void setConductorSelected(Conductor conductorSelected) {
        this.conductorSelected = conductorSelected;
    }

    
    
    
    public Viajes getViajesSelected() {
        return viajesSelected;
    }

    public Boolean getEsAprobadoParaEditar() {
        return esAprobadoParaEditar;
    }

    public void setEsAprobadoParaEditar(Boolean esAprobadoParaEditar) {
        this.esAprobadoParaEditar = esAprobadoParaEditar;
    }

    public void setViajesSelected(Viajes viajesSelected) {
        this.viajesSelected = viajesSelected;
    }

    public Boolean getEsAprobado() {
        return esAprobado;
    }

    public void setEsAprobado(Boolean esAprobado) {
        this.esAprobado = esAprobado;
    }

    public List<Vehiculo> getVehiculoList() {
        return vehiculoList;
    }

    public void setVehiculoList(List<Vehiculo> vehiculoList) {
        this.vehiculoList = vehiculoList;
    }

    public List<Conductor> getConductorList() {
        return conductorList;
    }

    public void setConductorList(List<Conductor> conductorList) {
        this.conductorList = conductorList;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Viajes getViajes() {
        return viajes;
    }

    public void setViajes(Viajes viajes) {
        this.viajes = viajes;
    }

    public Estatus getEstatusSelected() {
        return estatusSelected;
    }

    public void setEstatusSelected(Estatus estatusSelected) {
        this.estatusSelected = estatusSelected;
    }

    public Boolean getEsDocente() {
        return esDocente;
    }

    public void setEsDocente(Boolean esDocente) {
        this.esDocente = esDocente;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Tipovehiculo getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(Tipovehiculo tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    public Usuario getSolicita() {
        return solicita;
    }

    public void setSolicita(Usuario solicita) {
        this.solicita = solicita;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public List<Facultad> getFacultadList() {
        return facultadList;
    }

    public void setFacultadList(List<Facultad> facultadList) {
        this.facultadList = facultadList;
    }

    public List<Carrera> getCarreraList() {
        return carreraList;
    }

    public void setCarreraList(List<Carrera> carreraList) {
        this.carreraList = carreraList;
    }

    public Date getOld() {
        return _old;
    }

    public void setOld(Date _old) {
        this._old = _old;
    }

    public List<Unidad> getUnidadList() {
        return unidadList;
    }

    public TiposolicitudServices getTiposolicitudServices() {
        return tiposolicitudServices;
    }

    public void setTiposolicitudServices(TiposolicitudServices tiposolicitudServices) {
        this.tiposolicitudServices = tiposolicitudServices;
    }

    public void setUnidadList(List<Unidad> unidadList) {
        this.unidadList = unidadList;
    }

    public LookupServices getLookupServices() {
        return lookupServices;
    }

    public void setLookupServices(LookupServices lookupServices) {
        this.lookupServices = lookupServices;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRowPage() {
        return rowPage;
    }

    public void setRowPage(Integer rowPage) {
        this.rowPage = rowPage;
    }

    public SolicitudServices getSolicitudServices() {
        return solicitudServices;
    }

    public void setSolicitudServices(SolicitudServices solicitudServices) {
        this.solicitudServices = solicitudServices;
    }

    public List<Solicitud> getSolicitudList() {
        return solicitudList;
    }

    public void setSolicitudList(List<Solicitud> solicitudList) {
        this.solicitudList = solicitudList;
    }

    public List<Solicitud> getSolicitudFiltered() {
        return solicitudFiltered;
    }

    public void setSolicitudFiltered(List<Solicitud> solicitudFiltered) {
        this.solicitudFiltered = solicitudFiltered;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public Solicitud getSolicitudSelected() {
        return solicitudSelected;
    }

    public void setSolicitudSelected(Solicitud solicitudSelected) {
        this.solicitudSelected = solicitudSelected;
    }

    public SolicitudDataModel getSolicitudDataModel() {
        return solicitudDataModel;
    }

    public void setSolicitudDataModel(SolicitudDataModel solicitudDataModel) {
        this.solicitudDataModel = solicitudDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public CalendarioSolicitudController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="preRenderView()">
    @Override
    public String preRenderView(String action) {
        //acciones al llamar el formulario despues del init    
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">

    @PostConstruct
    public void init() {
        try {
            esAprobadoParaEditar = false;
            String action = loginController.get("solicitud");
            String id = loginController.get("idsolicitud");
            String pageSession = loginController.get("pagesolicitud");
            //Search
            if (loginController.get("searchsolicitud") == null || loginController.get("searchsolicitud").equals("")) {
                loginController.put("searchsolicitud", "_init");
            }

            if (loginController.get("calendariotipovehiculo") == null || loginController.get("calendariotipovehiculo").equals("")) {
                loginController.put("calendariotipovehiculo", "BUS");

            }
            if (loginController.get("calendarioestatus") == null || loginController.get("calendarioestatus").equals("")) {
                loginController.put("calendarioestatus", "SOLICITADO");

            }

            writable = false;

            solicitudList = new ArrayList<>();
            solicitudFiltered = new ArrayList<>();
            solicitud = new Solicitud();
            solicitudDataModel = new SolicitudDataModel(solicitudList);

            tipovehiculo = tipovehiculoServices.findById(loginController.get("calendariotipovehiculo"));
            estatus = estatusServices.findById(loginController.get("calendarioestatus"));
            esDocente = true;
            cargarSchedule();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
        prepare("new", solicitud);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Solicitud item) {
        String url = "";
        try {
            loginController.put("pagesolicitud", page.toString());
            loginController.put("solicitud", action);

            switch (action) {
                case "new":
                    solicitud = new Solicitud();
                    solicitudSelected = new Solicitud();

                    writable = false;
                    break;

                case "view":

                    solicitudSelected = item;
                    solicitud = solicitudSelected;
                    unidadList = solicitud.getUnidad();
                    loginController.put("idsolicitud", solicitud.getIdsolicitud().toString());

                    url = "/pages/solicituddocente/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/solicituddocente/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/solicituddocente/new.xhtml";
                    break;

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return url;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showAll">
    @Override
    public String showAll() {
        try {
            solicitudList = new ArrayList<>();
            solicitudFiltered = new ArrayList<>();
            solicitudList = solicitudRepository.findAll();
            solicitudFiltered = solicitudList;
            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">

    @Override
    public String isNew() {
        try {
            writable = true;

            Date idsecond = solicitud.getFecha();
            Integer id = solicitud.getIdsolicitud();

            List<Solicitud> list = solicitudRepository.findBy(new Document("usuario.username", loginController.getUsuario().getUsername()).append("fecha", solicitud.getFecha()));
            if (!list.isEmpty()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.yasolicitoviajeenestafecha"));
            }
            solicitud = new Solicitud();
            solicitudSelected = new Solicitud();
            solicitud.setIdsolicitud(id);
            solicitud.setFecha(idsecond);
            solicitud.setMision("---");
            solicitud.setFechaestatus(DateUtil.getFechaHoraActual());
            solicita = loginController.getUsuario();
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
            unidadList.add(loginController.getUsuario().getUnidad());

            viajesSelected = new Viajes();
            Integer mes = DateUtil.getMesDeUnaFecha(solicitud.getFecha());

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
            if (loginController.getRol().getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            solicitudSelected = solicitud;

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {

            solicitud.setActivo("si");
            solicitud.setUnidad(unidadList);
            solicitud.setFacultad(facultadList);
            solicitud.setCarrera(carreraList);
            usuarioList = new ArrayList<>();
            usuarioList.add(solicita);
            usuarioList.add(responsable);
            solicitud.setUsuario(usuarioList);

            if (DateUtil.fechaMenor(solicitud.getFechahoraregreso(), solicitud.getFechahorapartida())) {

                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fecharegresomenorquefechapartida"));
                return "";
            }
            //Verificar si tiene un viaje en esas fechas

            Optional<Solicitud> optionalRango = solicitudServices.coincidenciaResponsableEnRango(solicitud);
            if (optionalRango.isPresent()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.solicitudnumero") + " " + optionalRango.get().getIdsolicitud().toString() + "  " + rf.getMessage("warning.solicitudfechahoraenrango"));
                return "";
            }

            if (DateUtil.getHoraDeUnaFecha(solicitud.getFechahorapartida()) == 0
                    && DateUtil.getMinutosDeUnaFecha(solicitud.getFechahorapartida()) == 0) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.horapartidaescero"));
                return "";
            }
            if (DateUtil.getHoraDeUnaFecha(solicitud.getFechahoraregreso()) == 0
                    && DateUtil.getMinutosDeUnaFecha(solicitud.getFechahoraregreso()) == 0) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.horallegadaescero"));
            }

            if (solicitud.getPasajeros() < 0) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.numerodepasajerosmenorcero"));
                return "";
            }

            Integer idsolicitud = autoincrementableTransporteejbServices.getContador("solicitud");
            solicitud.setIdsolicitud(idsolicitud);
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            solicitud.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (solicitudRepository.save(solicitud)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
                        "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
//enviarEmails();

                //si cambia el email o celular del responsable actualizar ese usuario
                if (!responsableOld.getEmail().equals(responsable.getEmail()) || !responsableOld.getCelular().equals(responsable.getCelular())) {
                    usuarioRepository.update(responsable);
                    //actuliza el que esta en el login
                    if (responsable.getUsername().equals(loginController.getUsuario().getUsername())) {
                        loginController.setUsuario(responsable);
                    }
                }
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + solicitudRepository.getException().toString());
            }

        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="edit">
    @Override
    public String edit() {
        try {
            //Si era apronado para editar

            if (esAprobado) {
                if (DateUtil.fechaMayor(viajes.getFechahorainicioreserva(), solicitud.getFechahorapartida())) {

                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fechainicioreservamayorfechapartida"));
                    return "";
                }
                if (DateUtil.fechaMenor(viajes.getFechahorafinreserva(), solicitud.getFechahoraregreso())) {

                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fechafinreservamenorfecharegreso"));
                    return "";
                }

                if (solicitud.getNumerodevehiculos() != vehiculoList.size()) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.numerodevehiculosnoigualvehiculos"));
                    return "";
                }
                if (conductorList.size() != vehiculoList.size()) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.conductoresnoigualvehiculos"));
                    return "";
                }
                if (!esAprobadoParaEditar) {
                    viajes = viajesSelected;
                    Integer idviaje = autoincrementableTransporteejbServices.getContador("viajes");
                    viajes.setActivo("si");
                    viajes.setIdviaje(idviaje);
                    viajes.setConductor(conductorSelected);
                    viajes.setVehiculo(vehiculoSelected);
                    // viajes.setVehsolicitudList);
                   

                    viajes.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));

                    if (viajesRepository.save(viajes)) {
                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajes.getIdviaje().toString(), loginController.getUsername(),
                                "create", "viajes", viajesRepository.toDocument(viajes).toString()));
                        JsfUtil.successMessage(rf.getAppMessage("info.save"));

                    } else {
                        JsfUtil.successMessage("save() " + viajesRepository.getException().toString());
                        return "";
                    }
                } else {
                    viajes = viajesSelected;
                    viajes.setConductor(conductorSelected);
                    viajes.setVehiculo(vehiculoSelected);
                    viajesSelected.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "update"));
                    if (viajesRepository.update(viajesSelected)) {
                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajesSelected.getIdviaje().toString(), loginController.getUsername(),
                                "update", "viajes", viajesRepository.toDocument(viajesSelected).toString()));

                    }
                }

            } else {
                //Si era apronado para editar y se cambio el estatus se coloca activo = no
                if (esAprobadoParaEditar) {
                    viajesSelected.setActivo("no");
                    viajesSelected.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "update"));
                    if (viajesRepository.update(viajesSelected)) {
                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajesSelected.getIdviaje().toString(), loginController.getUsername(),
                                "update", "viajes", viajesRepository.toDocument(viajesSelected).toString()));

                    }
                }
            }

            solicitud.setEstatus(estatusSelected);

            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
                    "update", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            solicitudRepository.update(solicitud);

            JsfUtil.infoDialog(rf.getAppMessage("info.view"), rf.getAppMessage("info.update"));
        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="saveViaje()">

    public String saveViaje() {
        try {
            //Si era apronado para editar

//            if (esAprobado) {
//                if (DateUtil.fechaMayor(viajes.getFechahorainicioreserva(), solicitud.getFechahorapartida())) {
//
//                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fechainicioreservamayorfechapartida"));
//                    return "";
//                }
//                if (DateUtil.fechaMenor(viajes.getFechahorafinreserva(), solicitud.getFechahoraregreso())) {
//
//                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.fechafinreservamenorfecharegreso"));
//                    return "";
//                }
//
//                if (solicitud.getNumerodevehiculos() != vehiculoList.size()) {
//                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.numerodevehiculosnoigualvehiculos"));
//                    return "";
//                }
                if (conductorList.size() != vehiculoList.size()) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.conductoresnoigualvehiculos"));
                    return "";
                }
//                if (!esAprobadoParaEditar) {
                    viajes = viajesSelected;
                    Integer idviaje = autoincrementableTransporteejbServices.getContador("viajes");
                    viajes.setActivo("si");
                    viajes.setIdviaje(idviaje);
                    viajes.setConductor(conductorSelected);
                    viajes.setVehiculo(vehiculoSelected);
                    // viajes.setVehsolicitudList);


                    viajes.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));

                    if (viajesRepository.save(viajes)) {
                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajes.getIdviaje().toString(), loginController.getUsername(),
                                "create", "viajes", viajesRepository.toDocument(viajes).toString()));
                        JsfUtil.successMessage(rf.getAppMessage("info.save"));

                    } else {
                        JsfUtil.errorDialog("save() " , viajesRepository.getException().toString());
                        return "";
                    }
//                } else {
//                    viajes = viajesSelected;
//                    viajes.setConductor(conductorList);
//                    viajes.setVehiculo(vehiculoList);
//                    viajesSelected.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "update"));
//                    if (viajesRepository.update(viajesSelected)) {
//                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajesSelected.getIdviaje().toString(), loginController.getUsername(),
//                                "update", "viajes", viajesRepository.toDocument(viajesSelected).toString()));
//
//                    }
//                }

//            } else {
//                //Si era apronado para editar y se cambio el estatus se coloca activo = no
//                if (esAprobadoParaEditar) {
//                    viajesSelected.setActivo("no");
//                    viajesSelected.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "update"));
//                    if (viajesRepository.update(viajesSelected)) {
//                        revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajesSelected.getIdviaje().toString(), loginController.getUsername(),
//                                "update", "viajes", viajesRepository.toDocument(viajesSelected).toString()));
//
//                    }
//                }
//            }

           // solicitud.setEstatus(estatusSelected);

//            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
//                    "update", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

         //   solicitudRepository.update(solicitud);

       //     JsfUtil.infoDialog(rf.getAppMessage("info.view"), rf.getAppMessage("info.update"));
        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage)">

    @Override
    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            solicitud = (Solicitud) item;
            if (!solicitudServices.isDeleted(solicitud)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            solicitudSelected = solicitud;
            if (solicitudRepository.delete("idsolicitud", solicitud.getIdsolicitud())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(), "delete", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    solicitudList.remove(solicitud);
                    solicitudFiltered = solicitudList;
                    solicitudDataModel = new SolicitudDataModel(solicitudList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagesolicitud", page.toString());

                } else {
                    solicitud = new Solicitud();
                    solicitudSelected = new Solicitud();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/solicitud/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (solicitudRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagesolicitud", page.toString());
            List<Solicitud> list = new ArrayList<>();
            list.add(solicitud);
            String ruta = "/resources/reportes/solicitud/details.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="printAll">

    @Override
    public String printAll() {
        try {
            List<Solicitud> list = new ArrayList<>();
            list = solicitudRepository.findAll(new Document("idsolicitud", 1));

            String ruta = "/resources/reportes/solicitud/all.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {

            solicitudList.removeAll(solicitudList);
            solicitudList.add(solicitudSelected);
            solicitudFiltered = solicitudList;
            solicitudDataModel = new SolicitudDataModel(solicitudList);

            loginController.put("searchsolicitud", "idsolicitud");
            lookupServices.setIdsolicitud(solicitudSelected.getIdsolicitud());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelectEstatus(SelectEvent event)">

    public void handleSelectEstatus(SelectEvent event) {
        try {
            esAprobado = false;
            if (estatusSelected.getIdestatus().equals("APROBADO")) {
                esAprobado = true;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelectEstatus(SelectEvent event)">

    public void handleSelectEstatusTipovehiculo(SelectEvent event) {
        try {
            loginController.put("calendariotipovehiculo", tipovehiculo.getIdtipovehiculo());
            loginController.put("calendarioestatus", estatus.getIdestatus());
            cargarSchedule();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="cargarSchedule()">

    public void cargarSchedule() {
        try {

            Document doc = new Document("tipovehiculo.idtipovehiculo", tipovehiculo.getIdtipovehiculo()).append("estatus.idestatus", estatus.getIdestatus()).append("activo", "si");
            solicitudList = solicitudRepository.findBy(doc, new Document("fecha", 1));
            eventModel = new DefaultScheduleModel();
            if (!solicitudList.isEmpty()) {
                solicitudList.forEach((a) -> {

                    eventModel.addEvent(
                            new DefaultScheduleEvent("# " + a.getIdsolicitud() + " Mision:" + a.getMision() + " Responsable: " + a.getUsuario().get(1).getNombre() + " " + a.getEstatus().getIdestatus(), a.getFechahorapartida(), a.getFechahoraregreso())
                    );
                });
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    public String recorrerEventModel(ScheduleModel eventModel) {
        try {

            String title = event.getTitle();
            System.out.println("----> " + eventModel.toString());
        } catch (Exception e) {
        }
        return "";
    }
// <editor-fold defaultstate="collapsed" desc="handleSelectResponsable(SelectEvent event)">

    public void handleSelectResponsable(SelectEvent event) {
        try {

            responsableOld = responsable;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = solicitudRepository.sizeOfPage(rowPage);
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="first">

    @Override
    public String first() {
        try {
            page = 1;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="next">

    @Override
    public String next() {
        try {
            if (page < (solicitudRepository.sizeOfPage(rowPage))) {
                page++;
            }
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="back">

    @Override
    public String back() {
        try {
            if (page > 1) {
                page--;
            }
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="skip(Integer page)">

    @Override
    public String skip(Integer page) {
        try {
            this.page = page;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move() {

        try {

            Document doc;
            switch (loginController.get("searchsolicitud")) {
                case "_init":
                case "_autocomplete":
                    doc = new Document("usuario.username", loginController.getUsuario().getUsername());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

                case "idsolicitud":

                    doc = new Document("idsolicitud", solicitud.getIdsolicitud()).append("usuario.username", loginController.getUsuario().getUsername());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

                default:
                    doc = new Document("usuario.username", loginController.getUsuario().getUsername());
//                    solicitudList = solicitudRepository.findPagination(page, rowPage);
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

//                    solicitudList = solicitudRepository.findPagination(page, rowPage);
                    break;
            }

            solicitudFiltered = solicitudList;

            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchsolicitud", "_init");
            page = 1;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {

            loginController.put("searchsolicitud", string);

            writable = true;
            move();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

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
            if (query.length() < 1) {
                return suggestionsFacultad;
            }

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
            if (query.length() < 1) {
                return suggestionsCarrera;
            }
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

    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onEventSelect(SelectEvent selectEvent)">
    public void onEventSelect(SelectEvent selectEvent) {
        try {
            // esnuevo = false;
            esAprobadoParaEditar = false;
            viajesSelected = new Viajes();
            esDocente = false;
            event = (ScheduleEvent) selectEvent.getObject();
            esAprobado = false;
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
                estatusSelected = solicitud.getEstatus();
                esDocente = solicitud.getTiposolicitud().getIdtiposolicitud().equals("DOCENTE");
                if (solicitud.getEstatus().getIdestatus().equals("APROBADO")) {
                    esAprobado = true;
                    esAprobadoParaEditar = true;
                    List<Viajes> list = new ArrayList<>();
                    list = viajesRepository.findBy(new Document("solicitud.idsolicitud", solicitud.getIdsolicitud()));
                    if (list.isEmpty()) {
                        JsfUtil.warningMessage(rf.getMessage("warning.notexitsviajeconesasolicitud"));
                    } else {
                        viajesSelected = list.get(0);
                        vehiculoSelected= viajesSelected.getVehiculo();
                        conductorSelected = viajesSelected.getConductor();
                    }
                } else {
                    viajesSelected.setActivo("si");
                    viajesSelected.setFechahorainicioreserva(solicitud.getFechahorapartida());
                    viajesSelected.setFechahorafinreserva(solicitud.getFechahoraregreso());
                }
            }

        } catch (Exception e) {

            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onDateSelectCalendar(SelectEvent selectEvent)">

    /*
    cuando selecciona una fecha
     */
    public void onDateSelectCalendar(SelectEvent selectEvent) {
        try {
//            esnuevo = true;
            event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
            JsfUtil.warningDialog("this ", "onDateSelectCalendar " + (Date) selectEvent.getObject());
            System.out.println("onDateSelectCalendar()");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onEventMove(ScheduleEntryMoveEvent event) ">
    public void onEventMove(ScheduleEntryMoveEvent event) {

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

//        addMessage(message);
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onEventResize(ScheduleEntryResizeEvent event)">
    public void onEventResize(ScheduleEntryResizeEvent event) {
        System.out.println("--->onEventResize() ");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

//        addMessage(message);
    } // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeFiltrado(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Vehiculo> completeVehiculoFiltrado(String query) {
        List<Vehiculo> suggestions = new ArrayList<>();
        List<Vehiculo> disponibles = new ArrayList<>();
        List<Vehiculo> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = vehiculoRepository.findRegex(field, query, true, new Document(field, 1));

            if (temp.isEmpty()) {
                return suggestions;
            } else {
                List<Vehiculo> validos = temp.stream()
                        .filter(x -> isVehiculoActivo(x)).collect(Collectors.toList());
                if (validos.isEmpty()) {
                    return suggestions;
                }
                if (vehiculoList.isEmpty()) {
                    return validos;
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
                    /*
                    Verificar si tiene viajes
                     */
                    for (Vehiculo v : suggestions) {

                        List<Viajes> viajesList;
                        if (esMismoDiaSolicitud()) {
                            //SI LA SOLICITUD(salida y regreso es el mismo dia)
                            //BUSCAR LOS REGISTROS DE VIAJES DEL VEHICULO ESE DIA
                            viajesList = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", viajesSelected.getFechahorainicioreserva());
                            if (viajesList.isEmpty()) {
                                // INDICA QUE ESE VEHICULO ESTA DISPONIBLE NO TIENE NINGUN VIAJE
                                disponibles.add(v);
                            } else {
                                // RECORRER LA LISTA Y VER SI EN LOS VIAJES QUE TIENE ESE DIA ESTA DISPONIBLE
                                if (!tieneDisponibilidadViaje(viajesList)) {
                                    disponibles.add(v);
                                }

                            }
                        } else {
                            // ABARCA VARIOS DIAS 
                            // OBTENER LOS VIAJES ENTRE ESOS DIAS

                            viajesList = viajesVariosDias(v);
                            if (viajesList.isEmpty()) {
                                //SI ESTA VACIO INDICA QUE ESTA DISPONIBLE NO TIENE VIAJES EN ESA FECHA
                                disponibles.add(v);
                            } else {
                                // RECORRER LA LISTA Y VER SI EN LOS VIAJES QUE TIENE ESE DIA ESTA DISPONIBLE
                                if (!tieneDisponibilidadViaje(viajesList)) {
                                    disponibles.add(v);
                                }
                            }
//                     
                        }
                    }
                    // VERIRIFICAR SI TIENE VIAJES
                    // List<Viajes> viajesList = viajesRepository.fi
                    //si el dia y mes
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return disponibles;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="completeFiltrado(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Conductor> completeConductorFiltrado(String query) {
        List<Conductor> suggestions = new ArrayList<>();
        List<Conductor> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
//            if (query.length() < 1) {
//                return suggestions;
//            }
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = conductorRepository.findRegex(field, query, true, new Document(field, 1));

            if (conductorList.isEmpty()) {
                if (!temp.isEmpty()) {
                    suggestions = temp;
                }
            } else {
                if (!temp.isEmpty()) {

                    for (Conductor c : temp) {
                        found = false;
                        for (Conductor c2 : conductorList) {
                            if (c.getIdconductor() == c2.getIdconductor()) {
                                found = true;
                            }
                        }
                        if (!found) {
                            suggestions.add(c);
                        }

                    }
                }

            }
            //suggestions=  rolRepository.findRegex(field,query,true,new Document(field,1));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }
        return suggestions;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isVehiculoValido(Vehiculo vehiculo)">
    public Boolean isVehiculoValid(Vehiculo vehiculo) {
        Boolean valid = false;
        try {

            if (vehiculo.getActivo().equals("si") && vehiculo.getEnreparacion().equals("no")) {

                for (Tipovehiculo t : solicitud.getTipovehiculo()) {
                    if (vehiculo.getTipovehiculo().getIdtipovehiculo().equals(t.getIdtipovehiculo())) {
                        valid = true;
                        break;
                    }
                }
            }

        } catch (Exception e) {
//            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), "isVehiculoValid()", e.getLocalizedMessage());
        }
        return valid;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="isVehiculoValido(Vehiculo vehiculo)">
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
    // <editor-fold defaultstate="collapsed" desc="isSolicitudValid(Vehiculo vehiculo)">
    public Boolean isSolicitudValid(Solicitud solicitud) {
        Boolean valid = false;
        try {

            if (solicitud.getActivo().equals("si") && solicitud.getEstatus().getIdestatus().equals("SOLICITADO")) {

                valid = true;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return valid;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean esMismoDiaSolicitud()">
    /**
     * si el dia de partida es el mismo que el de regreso
     *
     * @return
     */
    private Boolean esMismoDiaSolicitud() {
        try {

            Integer dia = DateUtil.getDiaDeUnaFecha(solicitud.getFechahorapartida());
            Integer mes = DateUtil.getDiaDeUnaFecha(solicitud.getFechahorapartida());
            Integer anio = DateUtil.getDiaDeUnaFecha(solicitud.getFechahorapartida());
            Integer diaf = DateUtil.getDiaDeUnaFecha(solicitud.getFechahoraregreso());
            Integer mesf = DateUtil.getDiaDeUnaFecha(solicitud.getFechahoraregreso());
            Integer aniof = DateUtil.getDiaDeUnaFecha(solicitud.getFechahoraregreso());
// ES EN LA MISMA FECHA

            if (anio == aniof && mes == mesf && dia == diaf) {
                return true;
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="tieneDisponibilidadViaje(List<Viajes> viajesList)">
    /**
     * recorre el list de viajes y verifica si esta ocupado
     *
     * @param viajesList
     * @return
     */
    public Boolean tieneDisponibilidadViaje(List<Viajes> viajesList) {
        Boolean disponible = true;
        try {

            for (Viajes vj : viajesList) {
                if (esOcupadoEseDiaHora(solicitud, vj)) {
                    disponible = false;
                    break;
                }
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return disponible;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="esOcupadoEseDiaHora()">
    /**
     * verifica si esa solicitud esta ocupada
     *
     * @param solicitud
     * @param viajes
     * @return
     */
    public Boolean esOcupadoEseDiaHora(Solicitud solicitud, Viajes viajes) {
        try {
            if (DateUtil.dateBetween(solicitud.getFechahorapartida(), viajes.getFechahorainicioreserva(), viajes.getFechahorainicioreserva())
                    || DateUtil.dateBetween(solicitud.getFechahoraregreso(), viajes.getFechahorainicioreserva(), viajes.getFechahorainicioreserva())) {
                return true;
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="viajesVariosDias()">
    /**
     * devuelve la lista de viajes entre varios dias considerar que el busca
     * entre la fecha de partida y la fecha de regreso por lo que muchos viajes
     * puede que tengan fecha de partida y no de regreso en viajess y en otros
     * casos no esten en la de partida y si en la de regreso
     *
     * @return
     */
    private List<Viajes> viajesVariosDias(Vehiculo v) {
        List<Viajes> viajesList = new ArrayList<>();
        try {
            viajesList = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", solicitud.getFechahorapartida());
            List<Viajes> viajesStart = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", solicitud.getFechahorapartida());
            List<Viajes> viajesEnd = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorafinreserva", solicitud.getFechahoraregreso());
            viajesList = new ArrayList<>();
            if (viajesStart.isEmpty() && viajesEnd.isEmpty()) {
                // NO HAY VIAJES EN ESAS FECHAS

            } else {
                if (!viajesStart.isEmpty() && !viajesEnd.isEmpty()) {
                    viajesList = viajesStart;
                    for (Viajes vjs : viajesEnd) {
                        Boolean foundv = false;
                        for (Viajes vje : viajesList) {
                            if (vjs.getIdviaje() == vje.getIdviaje()) {
                                foundv = true;
                                break;
                            }
                        }
                        if (!foundv) {
                            viajesList.add(vjs);
                        }
                    }
                } else {
                    if (viajesStart.isEmpty() && !viajesEnd.isEmpty()) {
                        viajesList = viajesEnd;
                    } else {
                        if (!viajesStart.isEmpty() && viajesEnd.isEmpty()) {
                            viajesList = viajesStart;
                        }
                    }
                }
                Collections.sort(viajesList,
                        (Viajes a, Viajes b) -> a.getIdviaje().compareTo(b.getIdviaje()));
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return viajesList;
    }
    // </editor-fold>

    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Solicitud> completeSolicitudFiltrado(String query) {
        List<Solicitud> suggestions = new ArrayList<>();
        List<Solicitud> disponibles = new ArrayList<>();
        List<Solicitud> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            // temp = solicitudRepository.findRegex(field, query, true, new Document(field, 1));
            Document doc = new Document();
            doc.append("activo", "si").append("estatus.idestatus", "SOLICITADO");
            temp = solicitudRepository.findBy(doc, new Document("idsolicitud", -1));

            if (temp.isEmpty()) {
                return suggestions;
            } else {
                List<Solicitud> validos = temp.stream()
                        .filter(x -> isSolicitudValid(x)).collect(Collectors.toList());
                if (validos.isEmpty()) {
                    return suggestions;
                }

                if (solicitudList.isEmpty()) {
                    return validos;
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
                    disponibles = suggestions;

//                    for (Solicitud v : suggestions) {
//
//                        List<Viajes> viajesList;
//                        if (esMismoDiaSolicitud()) {
//                            //SI LA SOLICITUD(salida y regreso es el mismo dia)
//                            //BUSCAR LOS REGISTROS DE VIAJES DEL VEHICULO ESE DIA
//                            viajesList = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdsolicitud(), "fechahorainicioreserva", solicitud.getFechahorapartida());
//                            if (viajesList.isEmpty()) {
//                                // INDICA QUE ESE VEHICULO ESTA DISPONIBLE NO TIENE NINGUN VIAJE
//                                disponibles.add(v);
//                            } else {
//                                // RECORRER LA LISTA Y VER SI EN LOS VIAJES QUE TIENE ESE DIA ESTA DISPONIBLE
//                                if (!tieneDisponibilidadViaje(viajesList)) {
//                                    disponibles.add(v);
//                                }
//
//                            }
//                        } else {
//                            // ABARCA VARIOS DIAS 
//                            // OBTENER LOS VIAJES ENTRE ESOS DIAS
//
//                            viajesList = viajesVariosDias(v);
//                            if (viajesList.isEmpty()) {
//                                //SI ESTA VACIO INDICA QUE ESTA DISPONIBLE NO TIENE VIAJES EN ESA FECHA
//                                disponibles.add(v);
//                            } else {
//                                // RECORRER LA LISTA Y VER SI EN LOS VIAJES QUE TIENE ESE DIA ESTA DISPONIBLE
//                                if (!tieneDisponibilidadViaje(viajesList)) {
//                                    disponibles.add(v);
//                                }
//                            }
////                     
//                        }
//                    }
                    // VERIRIFICAR SI TIENE VIAJES
                    // List<Viajes> viajesList = viajesRepository.fi
                    //si el dia y mes
                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return disponibles;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="metodo()">
    public String inicializarViaje(){
        try {
            viajesSelected = new Viajes();
            viajesSelected.setFechahorainicioreserva(DateUtil.getFechaHoraActual());
            viajesSelected.setFechahorafinreserva(DateUtil.getFechaHoraActual());
            viajesSelected.setLugarpartida("UTP Azuero");
            conductorList = new ArrayList<>();
            vehiculoList = new ArrayList<>();
        } catch (Exception e) {
             errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>
}
