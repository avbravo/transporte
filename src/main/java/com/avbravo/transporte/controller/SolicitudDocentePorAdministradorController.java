/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.avbravoutils.validator.EmailValidator;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporteejb.producer.LookupTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.TiposolicitudServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
public class SolicitudDocentePorAdministradorController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;
    
    //    private String stmpPort="80";
    private String stmpPort = "25";
    List<Facultad> suggestionsFacultad = new ArrayList<>();
    List<Carrera> suggestionsCarrera = new ArrayList<>();
    List<Unidad> suggestionsUnidad = new ArrayList<>();
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

    //List
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudFiltered = new ArrayList<>();
    List<Unidad> unidadList = new ArrayList<>();
    List<Facultad> facultadList = new ArrayList<>();
    List<Carrera> carreraList = new ArrayList<>();

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
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;
    @Inject 
    UsuarioRepository  usuarioRepository;

    //Services
    //Atributos para busquedas
    @Inject
    AutoincrementableTransporteejbServices autoincrementableTransporteejbServices;

    @Inject
    ReferentialIntegrityTransporteejbServices referentialIntegrityTransporteejbServices;
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    EstatusServices estatusServices;

    @Inject
    TiposolicitudServices tiposolicitudServices;
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

    public LookupTransporteejbServices getlookupTransporteejbServices() {
        return lookupTransporteejbServices;
    }

    public void setlookupTransporteejbServices(LookupTransporteejbServices lookupTransporteejbServices) {
        this.lookupTransporteejbServices = lookupTransporteejbServices;
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
    public SolicitudDocentePorAdministradorController() {
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
            String action = loginController.get("solicitud");
            String id = loginController.get("idsolicitud");
            String pageSession = loginController.get("pagesolicitud");
            //Search
            loginController.put("searchsolicitud", "_init");
            writable = false;

            solicitudList = new ArrayList<>();
            solicitudFiltered = new ArrayList<>();
            solicitud = new Solicitud();
            solicitudDataModel = new SolicitudDataModel(solicitudList);

            if (id != null) {
                Optional<Solicitud> optional = solicitudRepository.find("idsolicitud", Integer.parseInt(id));
                if (optional.isPresent()) {
                    solicitud = optional.get();
                    unidadList = solicitud.getUnidad();

                    facultadList = solicitud.getFacultad();
                    carreraList = solicitud.getCarrera();

                    solicitudSelected = solicitud;
                    _old = solicitud.getFecha();
                    writable = true;

                }
            }
            if (action != null && action.equals("gonew")) {
                solicitud = new Solicitud();
                solicitudSelected = solicitud;
                writable = false;

            }
            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = solicitudRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("init() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("prepare() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("showAll()" + e.getLocalizedMessage());
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
            solicitud = new Solicitud();
            solicitudSelected = new Solicitud();
            solicitud.setIdsolicitud(id);
            solicitud.setFecha(idsecond);
            solicitud.setResponsable(loginController.getUsuario().getNombre());
            solicitud.setEmail(loginController.getUsuario().getEmail());
            solicitud.setTelefono(loginController.getUsuario().getCelular());
            solicitud.setPeriodoacademico(JsfUtil.getAnioActual().toString());
            solicitud.setFechahorapartida(solicitud.getFecha());
            solicitud.setFechahoraregreso(solicitud.getFecha());
            unidadList = new ArrayList<>();
            unidadList.add(loginController.getUsuario().getUnidad());

            solicitud.setEstatus(estatusServices.findById("SOLICITADO"));

            String textsearch = "ADMINISTRATIVO";
            if (loginController.getRol().getIdrol().toUpperCase().equals("DOCENTE")) {
                textsearch = "DOCENTE";
            }
            solicitud.setTiposolicitud(tiposolicitudServices.findById(textsearch));
            solicitudSelected = solicitud;

        } catch (Exception e) {
            JsfUtil.errorMessage("isNew()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
            Integer idsolicitud = autoincrementableTransporteejbServices.getContador("solicitud");
            solicitud.setIdsolicitud(idsolicitud);
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
            solicitud.setActivo("si");
            solicitud.setUnidad(unidadList);
            solicitud.setFacultad(facultadList);
            solicitud.setCarrera(carreraList);
            //Lo datos del usuario
            solicitud.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (solicitudRepository.save(solicitud)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
                        "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
//enviarEmails();
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + solicitudRepository.getException().toString());
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("save()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="edit">

    @Override
    public String edit() {
        try {
            solicitud.setUnidad(unidadList);
            solicitud.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));
            solicitud.setUnidad(unidadList);
            solicitud.setFacultad(facultadList);
            solicitud.setCarrera(carreraList);
            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
                    "update", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

            solicitudRepository.update(solicitud);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            JsfUtil.errorMessage("edit()" + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
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
        } catch (Exception ex) {
            JsfUtil.errorMessage("imprimir() " + ex.getLocalizedMessage());
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
        } catch (Exception ex) {
            JsfUtil.errorMessage("imprimir() " + ex.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {
//            JsfUtil.testMessage("======================handle Selected");
//            System.out.println("Factultad");
//            facultadList.forEach(f -> System.out.println(f.getDescripcion()));
//            System.out.println("Carrera");
//            carreraList.forEach(c -> System.out.println(c.getDescripcion()));

            solicitudList.removeAll(solicitudList);
            solicitudList.add(solicitudSelected);
            solicitudFiltered = solicitudList;
            solicitudDataModel = new SolicitudDataModel(solicitudList);
            loginController.put("searchsolicitud", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = solicitudRepository.sizeOfPage(rowPage);
            move();
        } catch (Exception e) {
            JsfUtil.errorMessage("last() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("first() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("next() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("back() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("skip() " + e.getLocalizedMessage());
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
                    if(loginController.getRol().getIdrol().equals("DOCENTE")){
                        
                    }
                    solicitudList = solicitudRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;

                case "idsolicitud":
                    doc = new Document("idsolicitud", solicitud.getIdsolicitud());
                    solicitudList = solicitudRepository.findPagination(doc, page, rowPage, new Document("idsolicitud", -1));
                    break;

                default:

                    solicitudList = solicitudRepository.findPagination(page, rowPage);
                    break;
            }

            solicitudFiltered = solicitudList;

            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("clear()" + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("completeFiltradoUnidad() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("completeFiltradoFacultad() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("completeFiltradoCarrera() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("removeByNotFoundFacultad() " + e.getLocalizedMessage());
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

        } catch (Exception ex) {
            JsfUtil.errorMessage("itemUnselec() " + ex.getLocalizedMessage());
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
            JsfUtil.errorMessage("foundFacultad() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("foundCarrera() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("foundUnidad() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("addFacultad()" + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("addCarrera()" + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("addUnidad()" + e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

    
    
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
List<Usuario> list = usuarioRepository.findBy(new Document("activo","si"));
            if(!list.isEmpty()){
                for(Usuario u:list){
                  if (u.getEmail().contains("@") == true && JsfUtil.emailValidate(u.getEmail())) {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress("avbravo@gmail.com"));

                        c++;

                        message.setRecipients(Message.RecipientType.TO,
                                InternetAddress.parse(u.getEmail()));

                        message.setSubject("Solicitud de Viaje Docente");
                        String texto = "";
                        texto = " <h1> Solicitud #:" + solicitud.getIdsolicitud()+ "  </h1>";
                        texto = " <h1> Solicitadi por: " + solicitud.getResponsable()+ "  </h1>";
                        texto += " <b>";
                        texto += "<br> Fecha de partidad " + solicitud.getFechahorapartida() + " lugar de salida: " + solicitud.getLugarpartida() +
                                "   <FONT COLOR=\"red\">Pendiente de aprobaciòn </FONT>  ";
                        texto += "</b>";
                        message.setContent(texto, "text/html");

                        Transport.send(message);
                  }
                }
            }
         
                      
                
         
        } catch (Exception e) {
            JsfUtil.errorMessage("enviarEmails() " + e.getLocalizedMessage());
        }
        return "";
    }

            
}