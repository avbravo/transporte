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
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Viaje;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.ViajeServices;

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
import org.bson.Document;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
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
    Date fechaHoraInicioReservaanterior= new Date();
    Date fechaHoraFinReservaAnterior= new Date();

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Viaje viaje;

    Viaje viajeSelected;
    Solicitud solicitud;
    Vehiculo vehiculoSelected;
    Conductor conductorSelected;
    Boolean _editable = false;

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<Viaje> viajeFiltered = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();
    List<Conductor> suggestionsConductor = new ArrayList<>();

    //Repository
    @Inject
    ViajeRepository viajeRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    //Services
    @Inject
    ErrorInfoTransporteejbServices errorServices;

    @Inject
    AutoincrementableTransporteejbServices autoincrementableTransporteejbServices;
    @Inject
    LookupServices lookupServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    ViajeServices viajeServices;
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

        return viajeRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public List<Vehiculo> getVehiculoList() {
        return vehiculoList;
    }

    public Date getFechaHoraInicioReservaanterior() {
        return fechaHoraInicioReservaanterior;
    }

    public void setFechaHoraInicioReservaanterior(Date fechaHoraInicioReservaanterior) {
        this.fechaHoraInicioReservaanterior = fechaHoraInicioReservaanterior;
    }

    public Date getFechaHoraFinReservaAnterior() {
        return fechaHoraFinReservaAnterior;
    }

    public void setFechaHoraFinReservaAnterior(Date fechaHoraFinReservaAnterior) {
        this.fechaHoraFinReservaAnterior = fechaHoraFinReservaAnterior;
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

    public ViajeServices getViajeServices() {
        return viajeServices;
    }

    public void setViajeServices(ViajeServices viajeServices) {
        this.viajeServices = viajeServices;
    }

    public List<Viaje> getViajeList() {
        return viajeList;
    }

    public void setViajeList(List<Viaje> viajeList) {
        this.viajeList = viajeList;
    }

    public List<Viaje> getViajeFiltered() {
        return viajeFiltered;
    }

    public void setViajeFiltered(List<Viaje> viajeFiltered) {
        this.viajeFiltered = viajeFiltered;
    }

    public ViajeDataModel getViajeDataModel() {
        return viajeDataModel;
    }

    public void setViajeDataModel(ViajeDataModel viajeDataModel) {
        this.viajeDataModel = viajeDataModel;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public Viaje getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(Viaje viajeSelected) {
        this.viajeSelected = viajeSelected;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
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

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajeController() {
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
            _editable = false;
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
                    case "viewfecha":
                        if (id != null) {
                            Optional<Viaje> optional = viajeRepository.find("idviaje", Integer.parseInt(id));
                            if (optional.isPresent()) {
                                viaje = optional.get();
                                viajeSelected = viaje;
                                vehiculoSelected = viaje.getVehiculo();
                                conductorSelected = viaje.getConductor();
                                
                                 fechaHoraInicioReservaanterior= viaje.getFechahorainicioreserva();
fechaHoraFinReservaAnterior= viaje.getFechahorafinreserva();
                                _editable = true;
                                writable = true;

                            }
                        }
                        break;
                    case "golist":
                        move();
                        break;
                }
            } else {
                move();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
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
    @Override
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

    @Override
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

            Integer idviaje = autoincrementableTransporteejbServices.getContador("viaje");
            viaje.setIdviaje(idviaje);
            viaje.setRealizado("no");
            viaje.setActivo("si");

            //Lo datos del usuario
            viaje.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (viajeRepository.save(viaje)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(),
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

            viaje.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(),
                    "update", "viaje", viajeRepository.toDocument(viaje).toString()));

            viajeRepository.update(viaje);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="editDates">


    public String editExcluyendoMismoViaje() {
        try {
            if (!viajeServices.isValid(viaje)) {
                return "";
            }
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
         

            viaje.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(),
                    "update", "viaje", viajeRepository.toDocument(viaje).toString()));

            viajeRepository.update(viaje);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
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
            viaje = (Viaje) item;

            if (!viajeServices.isDeleted(viaje)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            viajeSelected = viaje;
            if (viajeRepository.delete("idviaje", viaje.getIdviaje())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viaje.getIdviaje().toString(), loginController.getUsername(), "delete", "viaje", viajeRepository.toDocument(viaje).toString()));
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
    @Override
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
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
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
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = viajeRepository.sizeOfPage(rowPage);
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
            if (page < (viajeRepository.sizeOfPage(rowPage))) {
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
            Document sort = new Document("idviaje", -1);

            switch (loginController.get("searchviaje")) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage, sort);

                    break;

                case "idviaje":
                    if (lookupServices.getIdviaje() != null) {
                        viajeList = viajeRepository.findRegexInTextPagination("idviaje", lookupServices.getIdviaje().toString(), true, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    }

                    break;

                default:

                    viajeList = viajeRepository.findPagination(page, rowPage, sort);
                    break;
            }

            viajeFiltered = viajeList;

            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchviaje", "_init");
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

            loginController.put("searchviaje", string);

            writable = true;
            move();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeVehiculoFiltrado(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Vehiculo> completeVehiculo(String query) {
        List<Vehiculo> suggestions = new ArrayList<>();
//        List<Vehiculo> disponibles = new ArrayList<>();
        List<Vehiculo> temp = new ArrayList<>();

        try {
            Boolean found = false;
            query = query.trim();

            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = vehiculoRepository.findRegex(field, query, true, new Document(field, 1));

            if (temp.isEmpty()) {
                //Solo para el caso que necesite editar y debe mostrar el original en la lista
                // debido a que el filtro lo excluira porque ya esta registrado
                if (_editable) {
                    suggestions.add(vehiculoSelected);
                }

                return suggestions;
            } else {
                List<Vehiculo> validos = temp.stream()
                        .filter(x -> isVehiculoActivoDisponible(x)).collect(Collectors.toList());
                if (validos.isEmpty()) {
                    if (_editable) {
                        suggestions.add(vehiculoSelected);
                    }
                    return suggestions;
                }
                if (vehiculoList == null || vehiculoList.isEmpty()) {
                    if (_editable) {
                        validos.add(vehiculoSelected);
                    }
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
                    if (_editable) {
                        suggestions.add(vehiculoSelected);
                    }

                }
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestions;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="completeConductorFiltrado(String query)">
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
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = conductorRepository.findRegex(field, query, true, new Document(field, 1));
            if (temp.isEmpty()) {
                if (_editable && conductorSelected.getEscontrol().equals("no")) {
                    suggestionsConductor.add(conductorSelected);
                }
                return suggestionsConductor;
            } else {

                List<Conductor> validos = temp.stream()
                        .filter(x -> isConductorActivoDisponible(x)).collect(Collectors.toList());
                if (validos.isEmpty()) {
                    if (_editable && conductorSelected.getEscontrol().equals("no")) {
                        suggestionsConductor.add(conductorSelected);
                    }
                    return suggestionsConductor;
                }
                if (conductorList == null || conductorList.isEmpty()) {
                    if (_editable && conductorSelected.getEscontrol().equals("no")) {
                        validos.add(conductorSelected);
                    }
                    return validos;
                } else {
                    validos.forEach((v) -> {
                        Optional<Conductor> optional = conductorList.stream()
                                .filter(v2 -> v2.getIdconductor() == v.getIdconductor())
                                .findAny();
                        if (!optional.isPresent()) {
                            if (_editable && conductorSelected.getEscontrol().equals("no")) {
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
        String color = "";
        try {
            switch (realizado.toLowerCase()) {
                case "si":
                    color = "green";
                    break;
                case "no":
                    color = "black";
                    break;
                default:
                    color = "blue";
            }
            if(activo.equals("no")){
                color ="red";
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return color;
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
}
