/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.reportes;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.interfaces.IError;
import com.avbravo.jmoordb.mongodb.history.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
 
import com.avbravo.transporte.security.LoginController;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.entity.Viaje;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.datamodel.ConductorDataModel;
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
import org.bson.Document;
import org.bson.conversions.Bson;
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
public class ConductorReporteController implements Serializable, IError {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;
    private Double totalGlobalkm;
    private Double totalGlobalConsumo;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private ConductorDataModel conductorDataModel;

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
    List<Conductor> conductorFiltered = new ArrayList<>();
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

    public ScheduleModel getVehiculoScheduleModel() {
        return vehiculoScheduleModel;
    }

    public void setVehiculoScheduleModel(ScheduleModel vehiculoScheduleModel) {
        this.vehiculoScheduleModel = vehiculoScheduleModel;
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

    public Double getTotalGlobalkm() {
        return totalGlobalkm;
    }

    public void setTotalGlobalkm(Double totalGlobalkm) {
        this.totalGlobalkm = totalGlobalkm;
    }

    public Double getTotalGlobalConsumo() {
        return totalGlobalConsumo;
    }

    public void setTotalGlobalConsumo(Double totalGlobalConsumo) {
        this.totalGlobalConsumo = totalGlobalConsumo;
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

    public List<Conductor> getConductorFiltered() {
        return conductorFiltered;
    }

    public void setConductorFiltered(List<Conductor> conductorFiltered) {
        this.conductorFiltered = conductorFiltered;
    }

    public ConductorDataModel getConductorDataModel() {
        return conductorDataModel;
    }

    public void setConductorDataModel(ConductorDataModel conductorDataModel) {
        this.conductorDataModel = conductorDataModel;
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

    public ScheduleModel getConductorScheduleModel() {
        return conductorScheduleModel;
    }

    public void setConductorScheduleModel(ScheduleModel conductorScheduleModel) {
        this.conductorScheduleModel = conductorScheduleModel;
    }

    public ScheduleModel getSolicitudScheduleModel() {
        return solicitudScheduleModel;
    }

    public void setSolicitudScheduleModel(ScheduleModel solicitudScheduleModel) {
        this.solicitudScheduleModel = solicitudScheduleModel;
    }

    public ScheduleModel getViajeScheduleModel() {
        return viajeScheduleModel;
    }

    public void setViajeScheduleModel(ScheduleModel viajeScheduleModel) {
        this.viajeScheduleModel = viajeScheduleModel;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ConductorReporteController() {
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
            totalGlobalConsumo = 0.0;
            totalGlobalkm = 0.0;

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

            if (loginController.get("searchconductorreporte") == null || loginController.get("searchviaje").equals("")) {
                loginController.put("searchconductorreporte", "_init");
            }
            writable = false;
            conductorList = new ArrayList<>();
            viajeList = new ArrayList<>();
            conductorFiltered = new ArrayList<>();
            viaje = new Viaje();
            conductorDataModel = new ConductorDataModel(conductorList);

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

// <editor-fold defaultstate="collapsed" desc="print">
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
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
            viajeList.removeAll(viajeList);
            viajeList.add(viajeSelected);
            conductorFiltered = conductorList;
            conductorDataModel = new ConductorDataModel(conductorList);

            loginController.put("searchconductorreporte", "idviaje");
            lookupServices.setIdviaje(viajeSelected.getIdviaje());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    public String last() {
        try {
            page = viajeRepository.sizeOfPage(rowPage);
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="clear()">

    public String clear() {
        try {

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="first">

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

    public void move(Integer page) {

        try {
            totalGlobalConsumo = 0.0;
            totalGlobalkm = 0.0;
            Document doc;
            Document sort = new Document("idviaje", -1);

            switch (loginController.get("searchconductorreporte")) {
                case "_init":
                    conductorList = new ArrayList<>();
                    break;

                case "kmrecorridosporconductor":
                    conductorList = new ArrayList<>();

                    List<Conductor> list = conductorRepository.findBy(new Document("activo", "si"), new Document("idconductor", 1));
                    if (list == null || list.isEmpty()) {
                        JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohayconductoresactivos"));

                    } else {
                        conductorList = list;
                        Bson filter_ = Filters.eq("realizado", "si");
                        List<Viaje> listV = viajeRepository.findBy(filter_, new Document("idviaje", -1));
                        if (listV == null || listV.isEmpty()) {
                            JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohayviajesrealizados"));
                            Integer index = 0;
                            for (Conductor v : conductorList) {
                                conductorList.get(index).setTotalkm(0.0);
                                conductorList.get(index).setTotalconsumo(0.0);
                                conductorList.get(index).setTotalviajes(0);
                                index++;
                            }
                        } else {
                            //Recorrer los viajes de cada vehiculo en esas fechas
                            //totaliza los km y el costo de combustible en esas fechas
                            Integer index = 0;
                            for (Conductor c : conductorList) {
                                viajeList = new ArrayList<>();
                                Bson filter = Filters.and(Filters.eq("conductor.idconductor", c.getIdconductor()), eq("realizado", "si"));
                                viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours(filter, "fechahorainicioreserva", lookupServices.getFechaDesde(), "fechahorafinreserva", lookupServices.getFechaHasta(), page, rowPage, new Document("idviaje", -1));
                                conductorList.get(index).setTotalkm(0.0);
                                conductorList.get(index).setTotalconsumo(0.0);
                                conductorList.get(index).setTotalviajes(0);
                                for (Viaje vi : viajeList) {
                                    conductorList.get(index).setTotalkm(conductorList.get(index).getTotalkm() + vi.getKmestimados());
                                    conductorList.get(index).setTotalconsumo(conductorList.get(index).getTotalconsumo() + vi.getCostocombustible());
                                    conductorList.get(index).setTotalviajes(conductorList.get(index).getTotalviajes() + 1);

                                }
                                index++;
                            }
                        }
                    }

                    break;

                default:

                    break;
            }
            conductorList.stream().map((c) -> {
                totalGlobalConsumo += c.getTotalconsumo();
                return c;
            }).forEachOrdered((c) -> {
                totalGlobalkm += c.getTotalkm();
            });
            conductorFiltered = conductorList;

            conductorDataModel = new ConductorDataModel(conductorList);

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    public String searchBy(String string) {
        try {

            loginController.put("searchconductorreporte", string);

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
            if (activo.equals("no")) {
                color = "red";
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

    // <editor-fold defaultstate="collapsed" desc="metodo()">
    public Boolean tieneRows() {
        return conductorDataModel.getRowCount() > 0;
    }
    // </editor-fold>

}
