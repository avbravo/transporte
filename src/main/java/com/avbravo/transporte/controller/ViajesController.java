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
import com.avbravo.transporteejb.datamodel.ViajesDataModel;
import com.avbravo.transporteejb.entity.Viajes;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajesRepository;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.ViajesServices;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collections;
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
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
public class ViajesController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private ViajesDataModel viajesDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Viajes viajes;
    Viajes viajesSelected;
    Solicitud solicitud;

    //List
    List<Viajes> viajesList = new ArrayList<>();
    List<Viajes> viajesFiltered = new ArrayList<>();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Conductor> conductorList = new ArrayList<>();

    //Repository
    @Inject
    ViajesRepository viajesRepository;
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
    ViajesServices viajesServices;
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

        return viajesRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
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

    public ViajesServices getViajesServices() {
        return viajesServices;
    }

    public void setViajesServices(ViajesServices viajesServices) {
        this.viajesServices = viajesServices;
    }

    public List<Viajes> getViajesList() {
        return viajesList;
    }

    public void setViajesList(List<Viajes> viajesList) {
        this.viajesList = viajesList;
    }

    public List<Viajes> getViajesFiltered() {
        return viajesFiltered;
    }

    public void setViajesFiltered(List<Viajes> viajesFiltered) {
        this.viajesFiltered = viajesFiltered;
    }

    public Viajes getViajes() {
        return viajes;
    }

    public void setViajes(Viajes viajes) {
        this.viajes = viajes;
    }

    public Viajes getViajesSelected() {
        return viajesSelected;
    }

    public void setViajesSelected(Viajes viajesSelected) {
        this.viajesSelected = viajesSelected;
    }

    public ViajesDataModel getViajesDataModel() {
        return viajesDataModel;
    }

    public void setViajesDataModel(ViajesDataModel viajesDataModel) {
        this.viajesDataModel = viajesDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajesController() {
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
            String action = loginController.get("viajes");
            String id = loginController.get("idviajes");
            String pageSession = loginController.get("pageviajes");
            //Search

            if (loginController.get("searchviajes") == null || loginController.get("searchviajes").equals("")) {
                loginController.put("searchviajes", "_init");
            }
            writable = false;

            viajesList = new ArrayList<>();
            viajesFiltered = new ArrayList<>();
            viajes = new Viajes();
            viajesDataModel = new ViajesDataModel(viajesList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = viajesRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        viajes = new Viajes();
                        viajes.setFechahorainicioreserva(DateUtil.getFechaHoraActual());
                        viajes.setFechahorafinreserva(DateUtil.getFechaHoraActual());
                        viajes.setActivo("si");
                        viajes.setLugarpartida("UTP-AZUERO");
                        viajesSelected = viajes;

                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Viajes> optional = viajesRepository.find("idviajes", id);
                            if (optional.isPresent()) {
                                viajes = optional.get();
                                viajesSelected = viajes;
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
        prepare("new", viajes);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Viajes item) {
        String url = "";
        try {
            loginController.put("pageviajes", page.toString());
            loginController.put("viajes", action);

            switch (action) {
                case "new":
                    viajes = new Viajes();
                    viajesSelected = new Viajes();

                    writable = false;
                    break;

                case "view":

                    viajesSelected = item;
                    viajes = viajesSelected;
                    loginController.put("idviajes", viajes.getIdviaje().toString());

                    url = "/pages/viajes/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/viajes/list.xhtml";
                    break;

                case "gonew":

                    url = "/pages/viajes/new.xhtml";
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
            viajesList = new ArrayList<>();
            viajesFiltered = new ArrayList<>();
            viajesList = viajesRepository.findAll();
            viajesFiltered = viajesList;
            viajesDataModel = new ViajesDataModel(viajesList);

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
            if (JsfUtil.isVacio(viajes.getIdviaje())) {
                writable = false;
                return "";
            }
            //viajes.setIdviajes(viajes.getIdviaje().toUpperCase());
            Optional<Viajes> optional = viajesRepository.findById(viajes);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                Integer id = viajes.getIdviaje();
                viajes = new Viajes();
                viajes.setIdviaje(id);
                viajesSelected = new Viajes();
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
            if (!viajesServices.isValid(viajes)) {
                return "";
            }

           if(viajesServices.vehiculoTieneViajeFecha(viajes)){
                            JsfUtil.warningMessage(rf.getMessage("warning.vehiculoenviajefechas"));
                return null;
            }

             if(viajesServices.conductorTieneViajeFecha(viajes)){
                JsfUtil.warningMessage(rf.getMessage("warning.conductoresenviajefechas"));
                return null;
            }

            Integer idviaje = autoincrementableTransporteejbServices.getContador("viajes");
            viajes.setIdviaje(idviaje);
            viajes.setRealizado("no");
            viajes.setActivo("si");

            //Lo datos del usuario
            viajes.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (viajesRepository.save(viajes)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajes.getIdviaje().toString(), loginController.getUsername(),
                        "create", "viajes", viajesRepository.toDocument(viajes).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + viajesRepository.getException().toString());
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

            viajes.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajes.getIdviaje().toString(), loginController.getUsername(),
                    "update", "viajes", viajesRepository.toDocument(viajes).toString()));

            viajesRepository.update(viajes);
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
            viajes = (Viajes) item;

            if (!viajesServices.isDeleted(viajes)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            viajesSelected = viajes;
            if (viajesRepository.delete("idviajes", viajes.getIdviaje())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(viajes.getIdviaje().toString(), loginController.getUsername(), "delete", "viajes", viajesRepository.toDocument(viajes).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    viajesList.remove(viajes);
                    viajesFiltered = viajesList;
                    viajesDataModel = new ViajesDataModel(viajesList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageviajes", page.toString());

                } else {
                    viajes = new Viajes();
                    viajesSelected = new Viajes();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/viajes/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (viajesRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageviajes", page.toString());
            List<Viajes> list = new ArrayList<>();
            list.add(viajes);
            String ruta = "/resources/reportes/viajes/details.jasper";
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
            List<Viajes> list = new ArrayList<>();
            list = viajesRepository.findAll(new Document("idviajes", 1));

            String ruta = "/resources/reportes/viajes/all.jasper";
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
            viajesList.removeAll(viajesList);
            viajesList.add(viajesSelected);
            viajesFiltered = viajesList;
            viajesDataModel = new ViajesDataModel(viajesList);

            loginController.put("searchviajes", "idviajes");
            lookupServices.setIdviaje(viajesSelected.getIdviaje());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = viajesRepository.sizeOfPage(rowPage);
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
            if (page < (viajesRepository.sizeOfPage(rowPage))) {
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

            switch (loginController.get("searchviajes")) {
                case "_init":
                case "_autocomplete":
                    viajesList = viajesRepository.findPagination(page, rowPage, sort);

                    break;

                case "idviajes":
                    if (lookupServices.getIdviaje() != null) {
                        viajesList = viajesRepository.findRegexInTextPagination("idviajes", lookupServices.getIdviaje().toString(), true, page, rowPage, new Document("idviajes", -1));
                    } else {
                        viajesList = viajesRepository.findPagination(page, rowPage, sort);
                    }

                    break;

                default:

                    viajesList = viajesRepository.findPagination(page, rowPage, sort);
                    break;
            }

            viajesFiltered = viajesList;

            viajesDataModel = new ViajesDataModel(viajesList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchviajes", "_init");
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

            loginController.put("searchviajes", string);

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
                if (vehiculoList == null || vehiculoList.isEmpty()) {
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
                        if (solicitudServices.esMismoDiaSolicitud(solicitud)) {
                            //SI LA SOLICITUD(salida y regreso es el mismo dia)
                            //BUSCAR LOS REGISTROS DE VIAJES DEL VEHICULO ESE DIA
                            viajesList = viajesRepository.filterDayWithoutHour("vehiculo.idvehiculo", v.getIdvehiculo(), "fechahorainicioreserva", viajesSelected.getFechahorainicioreserva());
                            if (viajesList.isEmpty()) {
                                // INDICA QUE ESE VEHICULO ESTA DISPONIBLE NO TIENE NINGUN VIAJE
                                disponibles.add(v);
                            } else {
                                // RECORRER LA LISTA Y VER SI EN LOS VIAJES QUE TIENE ESE DIA ESTA DISPONIBLE
                                if (!viajesServices.tieneDisponibilidadViaje(viajesList, solicitud)) {
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
                                if (!viajesServices.tieneDisponibilidadViaje(viajesList, solicitud)) {
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
    // <editor-fold defaultstate="collapsed" desc="completeConductorFiltrado(String query)">
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

            if (conductorList == null || conductorList.isEmpty()) {
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
    // <editor-fold defaultstate="collapsed" desc="viajesVariosDias(Vehiculo v) ">
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

}
