/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.UnidadDataModel;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporteejb.producer.LookupTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.services.UnidadServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
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
public class UnidadController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private UnidadDataModel unidadDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Unidad unidad;
    Unidad unidadSelected;

    //List
    List<Unidad> unidadList = new ArrayList<>();
    List<Unidad> unidadFiltered = new ArrayList<>();

    //Repository
    @Inject
    UnidadRepository unidadRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
    @Inject
    ReferentialIntegrityTransporteejbServices referentialIntegrityTransporteejbServices;
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    UnidadServices unidadServices;
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

        return unidadRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public LookupTransporteejbServices getLookupTransporteejbServices() {
        return lookupTransporteejbServices;
    }

    public void setLookupTransporteejbServices(LookupTransporteejbServices lookupTransporteejbServices) {
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

    public UnidadServices getUnidadServices() {
        return unidadServices;
    }

    public void setUnidadServices(UnidadServices unidadServices) {
        this.unidadServices = unidadServices;
    }

    public List<Unidad> getUnidadList() {
        return unidadList;
    }

    public void setUnidadList(List<Unidad> unidadList) {
        this.unidadList = unidadList;
    }

    public List<Unidad> getUnidadFiltered() {
        return unidadFiltered;
    }

    public void setUnidadFiltered(List<Unidad> unidadFiltered) {
        this.unidadFiltered = unidadFiltered;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public Unidad getUnidadSelected() {
        return unidadSelected;
    }

    public void setUnidadSelected(Unidad unidadSelected) {
        this.unidadSelected = unidadSelected;
    }

    public UnidadDataModel getUnidadDataModel() {
        return unidadDataModel;
    }

    public void setUnidadDataModel(UnidadDataModel unidadDataModel) {
        this.unidadDataModel = unidadDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public UnidadController() {
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
            String action = loginController.get("unidad");
            String id = loginController.get("idunidad");
            String pageSession = loginController.get("pageunidad");
            //Search

            if (loginController.get("searchunidad") == null || loginController.get("searchunidad").equals("")) {
                loginController.put("searchunidad", "_init");
            }
            writable = false;

            unidadList = new ArrayList<>();
            unidadFiltered = new ArrayList<>();
            unidad = new Unidad();
            unidadDataModel = new UnidadDataModel(unidadList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = unidadRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        unidad = new Unidad();
                        unidadSelected = unidad;
                        writable = false;

                        break;
                    case "view":
                        if (id != null) {
                            Optional<Unidad> optional = unidadRepository.find("idunidad", id);
                            if (optional.isPresent()) {
                                unidad = optional.get();
                                unidadSelected = unidad;
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
            JsfUtil.errorMessage("init() " + e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
        prepare("new", unidad);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Unidad item) {
        String url = "";
        try {
            loginController.put("pageunidad", page.toString());
            loginController.put("unidad", action);

            switch (action) {
                case "new":
                    unidad = new Unidad();
                    unidadSelected = new Unidad();

                    writable = false;
                    break;

                case "view":

                    unidadSelected = item;
                    unidad = unidadSelected;
                    loginController.put("idunidad", unidad.getIdunidad());

                    url = "/pages/unidad/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/unidad/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/unidad/new.xhtml";
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
            unidadList = new ArrayList<>();
            unidadFiltered = new ArrayList<>();
            unidadList = unidadRepository.findAll();
            unidadFiltered = unidadList;
            unidadDataModel = new UnidadDataModel(unidadList);

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
            if (JsfUtil.isVacio(unidad.getIdunidad())) {
                writable = false;
                return "";
            }
            unidad.setIdunidad(unidad.getIdunidad().toUpperCase());
            Optional<Unidad> optional = unidadRepository.findById(unidad);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = unidad.getIdunidad();
                unidad = new Unidad();
                unidad.setIdunidad(id);
                unidadSelected = new Unidad();
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("isNew()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
            unidad.setIdunidad(unidad.getIdunidad().toUpperCase());
            Optional<Unidad> optional = unidadRepository.findById(unidad);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            unidad.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (unidadRepository.save(unidad)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(unidad.getIdunidad(), loginController.getUsername(),
                        "create", "unidad", unidadRepository.toDocument(unidad).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + unidadRepository.getException().toString());
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

            unidad.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(unidad.getIdunidad(), loginController.getUsername(),
                    "update", "unidad", unidadRepository.toDocument(unidad).toString()));

            unidadRepository.update(unidad);
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
            unidad = (Unidad) item;
            if (!unidadServices.isDeleted(unidad)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            unidadSelected = unidad;
            if (unidadRepository.delete("idunidad", unidad.getIdunidad())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(unidad.getIdunidad(), loginController.getUsername(), "delete", "unidad", unidadRepository.toDocument(unidad).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    unidadList.remove(unidad);
                    unidadFiltered = unidadList;
                    unidadDataModel = new UnidadDataModel(unidadList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageunidad", page.toString());

                } else {
                    unidad = new Unidad();
                    unidadSelected = new Unidad();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/unidad/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (unidadRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageunidad", page.toString());
            List<Unidad> list = new ArrayList<>();
            list.add(unidad);
            String ruta = "/resources/reportes/unidad/details.jasper";
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
            List<Unidad> list = new ArrayList<>();
            list = unidadRepository.findAll(new Document("idunidad", 1));

            String ruta = "/resources/reportes/unidad/all.jasper";
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
            unidadList.removeAll(unidadList);
            unidadList.add(unidadSelected);
            unidadFiltered = unidadList;
            unidadDataModel = new UnidadDataModel(unidadList);
            loginController.put("searchunidad", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = unidadRepository.sizeOfPage(rowPage);
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
            if (page < (unidadRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchunidad")) {
                case "_init":
                    unidadList = unidadRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;
//              
//                    case "idunidad":
//                        doc = new Document("idunidad", lookupTransporteejbServices.getIdunidad());
//                        unidadList = unidadRepository.findPagination(doc, page, rowPage, new Document("idunidad", -1));
//                        break;

                case "idunidad":
                    unidadList = unidadRepository.findRegexInTextPagination("idunidad", lookupTransporteejbServices.getIdunidad(), true, page, rowPage, new Document("descripcion", -1));
                    break;

                default:

                    unidadList = unidadRepository.findPagination(page, rowPage);
                    break;
            }

            unidadFiltered = unidadList;

            unidadDataModel = new UnidadDataModel(unidadList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchunidad", "_init");
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

            loginController.put("searchunidad", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
