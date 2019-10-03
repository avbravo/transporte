/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller.sinarreglar;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.transporte.security.LoginController;
 
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import static com.avbravo.jmoordbutils.JsfUtil.nameOfClass;
import static com.avbravo.jmoordbutils.JsfUtil.nameOfMethod;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Viaje;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
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

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    Date fechaHoraInicioReservaanterior = new Date();
    Date fechaHoraFinReservaAnterior = new Date();

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Viaje viaje;

    Viaje viajeSelected;
    Solicitud solicitud;
    Vehiculo vehiculoSelected;
    Conductor conductorSelected;
    Boolean iseditable = false;

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<Viaje> viajeFiltered = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();
    List<Conductor> suggestionsConductor = new ArrayList<>();

    //Schedule
    private ScheduleModel vehiculoScheduleModel;
    private ScheduleModel conductorScheduleModel;
    private ScheduleModel solicitudScheduleModel;
    private ScheduleModel viajeScheduleModel;

//    private ScheduleModel lazyEventModel;
//
//    private ScheduleEvent event = new DefaultScheduleEvent();
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    ViajeRepository viajeRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    ErrorInfoServices errorServices;

    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    LookupServices lookupServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    SolicitudServices solicitudServices;

    @Inject
    ViajeServices viajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    @Inject
    LoginController loginController;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return viajeRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

  
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajeController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="preRenderView()">
   
    public String preRenderView(String action) {
        //acciones al llamar el formulario despues del init    
        return "";
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
            iseditable = false;
            vehiculoSelected = new Vehiculo();
            conductorSelected = new Conductor();
            String action = loginController.get("viaje");
            String id = loginController.get("idviaje");
            String pageSession = loginController.get("pageviaje");
            //Search

            if (loginController.get("searchviaje") == null || loginController.get("searchviaje").equals("")) {
                loginController.put("searchviaje", "_init");
            }
            writable = false;

            viajeList = new ArrayList<>();
            viajeFiltered = new ArrayList<>();
            viaje = new Viaje();
            viajeDataModel = new ViajeDataModel(viajeList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = viajeRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        viaje = new Viaje();
                        viaje.setFechahorainicioreserva(DateUtil.getFechaHoraActual());
                        viaje.setFechahorafinreserva(DateUtil.getFechaHoraActual());
                        viaje.setActivo("si");
                        viaje.setLugarpartida("UTP-AZUERO");
                        viajeSelected = viaje;

                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Viaje> optional = viajeRepository.find("idviaje", Integer.parseInt(id));
                            if (optional.isPresent()) {
                                viaje = optional.get();
                                viajeSelected = viaje;
                                vehiculoSelected = viaje.getVehiculo();
                                conductorSelected = viaje.getConductor();

                                fechaHoraInicioReservaanterior = viaje.getFechahorainicioreserva();
                                fechaHoraFinReservaAnterior = viaje.getFechahorafinreserva();
                                iseditable = true;
                                writable = true;

                            }
                        }
                        break;
                    case "golist":
                        move(page);
                        break;
                }
            } else {
                move(page);
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        PrimeFaces.current().resetInputs(":form:content");
        prepare("new", viaje);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Viaje item) {
        String url = "";
        try {
            loginController.put("pageviaje", page.toString());
            loginController.put("viaje", action);

            switch (action) {
                case "new":
                    viaje = new Viaje();
                    viajeSelected = new Viaje();

                    writable = false;
                    break;

                case "view":

                    viajeSelected = item;
                    viaje = viajeSelected;
                    loginController.put("idviaje", viaje.getIdviaje().toString());

                    url = "/pages/viaje/view.xhtml";
                    break;
                case "viewfecha":

                    viajeSelected = item;
                    viaje = viajeSelected;
                    loginController.put("idviaje", viaje.getIdviaje().toString());

                    url = "/pages/viaje/viewfecha.xhtml";
                    break;

                case "golist":
                    url = "/pages/viaje/list.xhtml";
                    break;

                case "gonew":

                    url = "/pages/viaje/new.xhtml";
                    break;

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return url;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showAll">
  
    public String showAll() {
        try {
            viajeList = new ArrayList<>();
            viajeFiltered = new ArrayList<>();
            viajeList = viajeRepository.findAll();
            viajeFiltered = viajeList;
            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">


    public String isNew() {
        try {
            writable = true;
            if (JsfUtil.isVacio(viaje.getIdviaje())) {
                writable = false;
                return "";
            }
            //viaje.setIdviaje(viaje.getIdviaje().toUpperCase());
            Optional<Viaje> optional = viajeRepository.findById(viaje);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                Integer id = viaje.getIdviaje();
                viaje = new Viaje();
                viaje.setIdviaje(id);
                viajeSelected = new Viaje();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
       
            if (!viajeServices.isValid(viaje)) {
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
            viaje.setUserInfo(viajeRepository.generateListUserinfo(loginController.getUsername(), "create"));
            if (viajeRepository.save(viaje)) {
                //guarda el contenido anterior
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(),
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
// <editor-fold defaultstate="collapsed" desc="edit">

    @Override
    public String edit() {
        try {
            if (!viajeServices.isValid(viaje)) {
                return "";
            }

            if (noHayCambioFechaHoras()) {
                //si cambia el vehiculo
                if (!viaje.getVehiculo().getIdvehiculo().equals(vehiculoSelected.getIdvehiculo())) {
                    if (!viajeServices.vehiculoDisponible(viaje)) {
                        JsfUtil.warningMessage(rf.getMessage("warning.vehiculoenviajefechas"));
                        return null;
                    }
                }
                // si cambio el conductor
                if (!viaje.getConductor().getIdconductor().equals(conductorSelected.getIdconductor())) {
                    if (viaje.getConductor().getEscontrol().equals("no")) {
                        if (!viajeServices.conductorDisponible(viaje)) {
                            JsfUtil.warningMessage(rf.getMessage("warning.conductoresenviajefechas"));
                            return null;
                        }
                    }
                }

            } else {
                //si cambia el vehiculo
                if (!viajeServices.vehiculoDisponibleExcluyendoMismoViaje(viaje)) {
                    JsfUtil.warningMessage(rf.getMessage("warning.vehiculoenviajefechas"));
                    return null;
                }

                // si cambio el conductor
                if (viaje.getConductor().getEscontrol().equals("no")) {
                    if (!viajeServices.conductorDisponibleExcluyendoMismoViaje(viaje)) {
                        JsfUtil.warningMessage(rf.getMessage("warning.conductoresenviajefechas"));
                        return null;
                    }
                }
            }

            viaje.getUserInfo().add(viajeRepository.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(),
                    "update", "viaje", viajeRepository.toDocument(viaje).toString()));

            viajeRepository.update(viaje);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage)">

    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            viaje = (Viaje) item;

            if (!viajeServices.isDeleted(viaje)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            viajeSelected = viaje;
            if (viajeRepository.delete("idviaje", viaje.getIdviaje())) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(), "delete", "viaje", viajeRepository.toDocument(viaje).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    viajeList.remove(viaje);
                    viajeFiltered = viajeList;
                    viajeDataModel = new ViajeDataModel(viajeList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageviaje", page.toString());

                } else {
                    viaje = new Viaje();
                    viajeSelected = new Viaje();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/viaje/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
  
    public String deleteAll() {
        if (viajeRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageviaje", page.toString());
            List<Viaje> list = new ArrayList<>();
            list.add(viaje);
            String ruta = "/resources/reportes/viaje/details.jasper";
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
            List<Viaje> list = new ArrayList<>();
            list = viajeRepository.findAll(new Document("idviaje", 1));

            String ruta = "/resources/reportes/viaje/all.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="printAll">

    public String printByFilter() {
        try {

            List<Viaje> list = new ArrayList<>();
            list = viajeRepository.findAll(new Document("idviaje", 1));

            String ruta = "/resources/reportes/viaje/all.jasper";
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
            viajeServices.isValidDate(viaje);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="itemUnselect">
    public void itemUnselect(UnselectEvent event) {
        try {

        } catch (Exception ex) {
            JsfUtil.errorMessage("itemUnselec() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public String handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
            viajeList.removeAll(viajeList);
            viajeList.add(viajeSelected);
            viajeFiltered = viajeList;
            viajeDataModel = new ViajeDataModel(viajeList);

            loginController.put("searchviaje", "idviaje");
            lookupServices.setIdviaje(viajeSelected.getIdviaje());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = viajeRepository.sizeOfPage(rowPage);
            move(page);
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
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="next">

    @Override
    public String next() {
        try {
            if (page < (viajeRepository.sizeOfPage(rowPage))) {
                page++;
            }
            move(page);
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
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="skip(Integer page)">


    public String skip(Integer page) {
        try {
            this.page = page;
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move(Integer page) {

        try {

            Document doc;
            Document sort = new Document("idviaje", -1);

            switch (loginController.get("searchviaje")) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage, sort);

                    break;

                case "activo":
                    if (lookupServices.getActivo() != null) {

                        viajeList = viajeRepository.findPagination(new Document("activo", lookupServices.getActivo()), page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "_betweendates":
                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si", "fechahorainicioreserva", lookupServices.getFechaDesde(), "fechahorafinreserva", lookupServices.getFechaHasta(), page, rowPage, new Document("idviaje", -1));

                    break;
                case "comentarios":
                    if (lookupServices.getComentarios() != null) {
                        viajeList = viajeRepository.findRegexInTextPagination("comentarios", lookupServices.getComentarios(), true, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "conductor":
                    if (lookupServices.getConductor().getIdconductor() != null) {
                        viajeList = viajeRepository.findPagination(new Document("conductor.idconductor", lookupServices.getConductor().getIdconductor()), page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;

                case "conconductor":
                    if (lookupServices.getConconductor() != null) {

                        Optional<Conductor> optional = conductorRepository.find(eq("escontrol", "si"));
                        Conductor c = new Conductor();
                        if (optional.isPresent()) {
                            c = optional.get();
                        } else {
                            JsfUtil.warningMessage(rf.getMessage("warning.nohayconductorcontrol"));
                            return;
                        }
                        if (lookupServices.getConconductor().equals("si")) {
                            Bson filter = Filters.ne("conductor.idconductor", c.getIdconductor());
                            viajeList = viajeRepository.filtersPagination(filter, page, rowPage, new Document("idviaje", -1));
                        } else {
                            viajeList = viajeRepository.findPagination(new Document("conductor.idconductor", c.getIdconductor()), page, rowPage, new Document("idviaje", -1));
                        }

                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "idviaje":
                    if (lookupServices.getIdviaje() != null) {
                        viajeList = viajeRepository.findPagination(new Document("idviaje", lookupServices.getIdviaje()), page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "lugardestino":
                    if (lookupServices.getLugardestino() != null) {
                        viajeList = viajeRepository.findRegexInTextPagination("lugardestino", lookupServices.getLugardestino(), true, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "realizado":
                    if (lookupServices.getRealizado() != null) {

                        viajeList = viajeRepository.findPagination(new Document("realizado", lookupServices.getRealizado()), page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;

                case "vehiculo":
                    if (lookupServices.getVehiculo().getPlaca() != null) {
                        viajeList = viajeRepository.findPagination(new Document("vehiculo.idvehiculo", lookupServices.getVehiculo().getIdvehiculo()), page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;
                case "viajeporconductor":

                    if (lookupServices.getConductor().getIdconductor() != null) {

                        Bson filter = Filters.eq("conductor.idconductor", lookupServices.getConductor().getIdconductor());
                        viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours(filter, "fechahorainicioreserva", lookupServices.getFechaDesde(), "fechahorafinreserva", lookupServices.getFechaHasta(), page, rowPage, new Document("idviaje", -1));
                    }

                    break;
                case "viajependiente":

                    Bson filter1 = Filters.eq("realizado", "no");
                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours(filter1, "fechahorainicioreserva", lookupServices.getFechaDesde(), "fechahorafinreserva", lookupServices.getFechaHasta(), page, rowPage, new Document("idviaje", -1));

                    break;
                case "viajeporvehiculo":

                    if (lookupServices.getVehiculo().getPlaca() != null) {

                        Bson filter = Filters.eq("vehiculo.idvehiculo", lookupServices.getVehiculo().getIdvehiculo());
                        viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours(filter, "fechahorainicioreserva", lookupServices.getFechaDesde(), "fechahorafinreserva", lookupServices.getFechaHasta(), page, rowPage, new Document("idviaje", -1));
                    }

                    break;
                default:

                    viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    break;
            }

            viajeFiltered = viajeList;

            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            reset();
            loginController.put("searchviaje", "_init");
            page = 1;
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">

    public String searchBy(String string) {
        try {

            loginController.put("searchviaje", string);

            writable = true;
            move(page);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

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
    // <editor-fold defaultstate="collapsed" desc="viajeVariosDias(Vehiculo v) ">
    /**
     * devuelve la lista de viaje entre varios dias considerar que el busca
     * entre la fecha de partida y la fecha de regreso por lo que muchos viaje
     * puede que tengan fecha de partida y no de regreso en viajes y en otros
     * casos no esten en la de partida y si en la de regreso
     *
     * @return
     */
    private List<Viaje> viajeVariosDias(Vehiculo v) {
        List<Viaje> viajeList = new ArrayList<>();
        try {
            viajeList = viajeRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", solicitud.getFechahorapartida());
            List<Viaje> viajeStart = viajeRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", solicitud.getFechahorapartida());
            List<Viaje> viajeEnd = viajeRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorafinreserva", solicitud.getFechahoraregreso());
            viajeList = new ArrayList<>();
            if (viajeStart.isEmpty() && viajeEnd.isEmpty()) {
                // NO HAY VIAJES EN ESAS FECHAS

            } else {
                if (!viajeStart.isEmpty() && !viajeEnd.isEmpty()) {
                    viajeList = viajeStart;
                    for (Viaje vjs : viajeEnd) {
                        Boolean foundv = false;
                        for (Viaje vje : viajeList) {
                            if (vjs.getIdviaje() == vje.getIdviaje()) {
                                foundv = true;
                                break;
                            }
                        }
                        if (!foundv) {
                            viajeList.add(vjs);
                        }
                    }
                } else {
                    if (viajeStart.isEmpty() && !viajeEnd.isEmpty()) {
                        viajeList = viajeEnd;
                    } else {
                        if (!viajeStart.isEmpty() && viajeEnd.isEmpty()) {
                            viajeList = viajeStart;
                        }
                    }
                }
                Collections.sort(viajeList,
                        (Viaje a, Viaje b) -> a.getIdviaje().compareTo(b.getIdviaje()));
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return viajeList;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="columnColor(String descripcion )"> 
    public String columnColor(String realizado, String activo) {
       return viajeServices.columnColor(realizado, activo);

    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String vehiculoPredeterminado(">
    /**
     * desde el panel al editar permite colocar el vehiculo predeterminado en el
     * autocomplete
     *
     * @return
     */
    public String vehiculoPredeterminado() {
        try {
            //    viaje.setVehiculo(viajeOld.getVehiculo());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String conductorPredeterminado(">
    public String conductorPredeterminado() {
        try {
            //         viaje.setConductor(viajeOld.getConductor());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean noHayCambioFechaHoras()">
    private Boolean noHayCambioFechaHoras() {
        return fechaHoraInicioReservaanterior.equals(viaje.getFechahorainicioreserva()) && fechaHoraFinReservaAnterior.equals(viaje.getFechahorafinreserva());

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
    // <editor-fold defaultstate="collapsed" desc="prepareScheduleSolicitud(()">
    public String prepareScheduleSolicitud() {
        try {
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

   
      // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return viajeServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return viajeServices.showHour(date);

    }// </editor-fold>
}
