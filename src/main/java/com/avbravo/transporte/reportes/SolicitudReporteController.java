/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.reportes;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.commonejb.services.SemestreServices;
import com.avbravo.jmoordb.interfaces.IError;
import com.avbravo.jmoordb.mongodb.history.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
 
import com.avbravo.transporte.security.LoginController;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.entity.Tiposolicitud;
import com.avbravo.transporteejb.entity.Tipovehiculo;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.TiposolicitudRepository;
import com.avbravo.transporteejb.repository.TipovehiculoRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.TiposolicitudServices;
import com.avbravo.transporteejb.services.TipovehiculoServices;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
public class SolicitudReporteController implements Serializable, IError {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

    //    private String stmpPort="80";
    private String stmpPort = "25";

    private Date _old;
    private Boolean writable = false;
    //DataModel
    private SolicitudDataModel solicitudDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();

    //Entity
    Solicitud solicitud;
    Solicitud solicitudSelected;
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
    FacultadRepository facultadRepository;
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    UnidadRepository unidadRepository;

    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    @Inject
    UsuarioRepository usuarioRepository;

    //Services
    //Atributos para busquedas
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    LookupServices lookupServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    TiposolicitudRepository tiposolicitudRepository;
    @Inject
    TipovehiculoRepository tipovehiculoRepository;
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

    public Solicitud getSolicitudCopiar() {
        return solicitudCopiar;
    }

    public void setSolicitudCopiar(Solicitud solicitudCopiar) {
        this.solicitudCopiar = solicitudCopiar;
    }

    public List<Tipovehiculo> getTipovehiculoList() {
        return tipovehiculoList;
    }

    public void setTipovehiculoList(List<Tipovehiculo> tipovehiculoList) {
        this.tipovehiculoList = tipovehiculoList;
    }

    public List<Tiposolicitud> getTiposolicitudList() {
        return tiposolicitudList;
    }

    public void setTiposolicitudList(List<Tiposolicitud> tiposolicitudList) {
        this.tiposolicitudList = tiposolicitudList;
    }

    public List<Usuario> getUsuarioList() {
        return usuarioList;
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
    public SolicitudReporteController() {
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
            String action = loginController.get("solicitud");
            String id = loginController.get("idsolicitud");
            String pageSession = loginController.get("pagesolicitud");
            //Search

            if (loginController.get("searchsolicitudreporte") == null || loginController.get("searchsolicitudreporte").equals("")) {
                loginController.put("searchsolicitudreporte", "_init");
            }
            writable = false;

            solicitudList = new ArrayList<>();
            solicitudFiltered = new ArrayList<>();
            solicitud = new Solicitud();
            solicitudDataModel = new SolicitudDataModel(solicitudList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = solicitudRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
//            Bson filter = Filters.or(eq("viaje.0.idviaje", null), eq("viaje.1.idviaje", null));
//            solicitudList = solicitudRepository.findBy(filter, new Document("idsolicitud", -1));
//            solicitudFiltered = solicitudList;
//            solicitudDataModel = new SolicitudDataModel(solicitudList);
            move(page);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="print">
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

            loginController.put("searchsolicitudreporte", "idsolicitud");
            lookupServices.setIdsolicitud(solicitudSelected.getIdsolicitud());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelectResponsable(SelectEvent event)">

    public void handleSelectResponsable(SelectEvent event) {
        try {

            responsableOld = responsable;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    public String last() {
        try {
            page = solicitudRepository.sizeOfPage(rowPage);
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
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
            if (page < (solicitudRepository.sizeOfPage(rowPage))) {
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

            Document doc;
            switch (loginController.get("searchsolicitudreporte")) {
                case "_init":
//                    Bson filter = Filters.or(eq("viaje.0.idviaje", null), eq("viaje.1.idviaje", null));
//                    Bson filter0 = Filters.or(eq("viaje.0.idviaje", 0), eq("viaje.1.idviaje", 0));
            //        Bson filter = Filters.eq("viaje.length", 0);
              
                   Bson filter= Filters.eq("estatus.idestatus","SOLICITADO");
                
                    solicitudList = solicitudRepository.findBy(filter, new Document("idsolicitud", -1));
                    solicitudFiltered = solicitudList;
                    solicitudDataModel = new SolicitudDataModel(solicitudList);
//                    doc = new Document("usuario.username", loginController.getUsuario().getUsername());
//                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));

                    break;

            }

            solicitudFiltered = solicitudList;

            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            errorServices.errorDialog(nameOfClass(), nameOfMethod(), nameOfMethod(),e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    public String clear() {
        try {
            loginController.put("searchsolicitudreporte", "_init");
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

            loginController.put("searchsolicitudreporte", string);

            writable = true;
            move(page);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    
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

}
