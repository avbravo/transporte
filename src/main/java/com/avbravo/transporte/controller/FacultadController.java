/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.commonejb.datamodel.FacultadDataModel;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.FacultadRepository;
import com.avbravo.commonejb.services.FacultadServices;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.producer.IntegerirdadreferencialTransporteejbServices;
import com.avbravo.transporteejb.producer.LookupTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;

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
public class FacultadController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private FacultadDataModel facultadDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Facultad facultad;
    Facultad facultadSelected;

    //List
    List<Facultad> facultadList = new ArrayList<>();
    List<Facultad> facultadFiltered = new ArrayList<>();

    //Repository
    @Inject
    FacultadRepository facultadRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
    @Inject
    IntegerirdadreferencialTransporteejbServices integerirdadreferencialTransporteejbServices;
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    FacultadServices facultadServices;
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

        return facultadRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
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

    public FacultadServices getFacultadServices() {
        return facultadServices;
    }

    public void setFacultadServices(FacultadServices facultadServices) {
        this.facultadServices = facultadServices;
    }

    public List<Facultad> getFacultadList() {
        return facultadList;
    }

    public void setFacultadList(List<Facultad> facultadList) {
        this.facultadList = facultadList;
    }

    public List<Facultad> getFacultadFiltered() {
        return facultadFiltered;
    }

    public void setFacultadFiltered(List<Facultad> facultadFiltered) {
        this.facultadFiltered = facultadFiltered;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public Facultad getFacultadSelected() {
        return facultadSelected;
    }

    public void setFacultadSelected(Facultad facultadSelected) {
        this.facultadSelected = facultadSelected;
    }

    public FacultadDataModel getFacultadDataModel() {
        return facultadDataModel;
    }

    public void setFacultadDataModel(FacultadDataModel facultadDataModel) {
        this.facultadDataModel = facultadDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public FacultadController() {
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
            String action = loginController.get("facultad");
            String id = loginController.get("idfacultad");
            String pageSession = loginController.get("pagefacultad");
            //Search
            loginController.put("searchfacultad", "_init");
            writable = false;

            facultadList = new ArrayList<>();
            facultadFiltered = new ArrayList<>();
            facultad = new Facultad();
            facultadDataModel = new FacultadDataModel(facultadList);

            if (id != null) {

                Optional<Facultad> optional = facultadRepository.find("idfacultad", Integer.parseInt(id));
                if (optional.isPresent()) {
                    facultad = optional.get();
                    facultadSelected = facultad;
                    writable = true;

                }
            }
            if (action != null && action.equals("gonew")) {
                facultad = new Facultad();
                facultadSelected = facultad;
                writable = false;

            }
            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = facultadRepository.sizeOfPage(rowPage);
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
        prepare("new", facultad);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Facultad item) {
        String url = "";
        try {
            loginController.put("pagefacultad", page.toString());
            loginController.put("facultad", action);

            switch (action) {
                case "new":
                    facultad = new Facultad();
                    facultadSelected = new Facultad();

                    writable = false;
                    break;

                case "view":

                    facultadSelected = item;
                    facultad = facultadSelected;
                    loginController.put("idfacultad", facultad.getIdfacultad().toString());

                    url = "/pages/facultad/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/facultad/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/facultad/new.xhtml";
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
            facultadList = new ArrayList<>();
            facultadFiltered = new ArrayList<>();
            facultadList = facultadRepository.findAll();
            facultadFiltered = facultadList;
            facultadDataModel = new FacultadDataModel(facultadList);

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
            if (JsfUtil.isVacio(facultad.getIdfacultad())) {
                writable = false;
                return "";
            }

            Optional<Facultad> optional = facultadRepository.findById(facultad);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                Integer id = facultad.getIdfacultad();
                facultad = new Facultad();
                facultad.setIdfacultad(id);
                facultadSelected = new Facultad();
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
            Optional<Facultad> optional = facultadRepository.findById(facultad);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            facultad.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (facultadRepository.save(facultad)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(facultad.getIdfacultad().toString(), loginController.getUsername(),
                        "create", "facultad", facultadRepository.toDocument(facultad).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + facultadRepository.getException().toString());
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

            facultad.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(facultad.getIdfacultad().toString(), loginController.getUsername(),
                    "update", "facultad", facultadRepository.toDocument(facultad).toString()));

            facultadRepository.update(facultad);
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
            facultad = (Facultad) item;

            facultadSelected = facultad;
            if (facultadRepository.delete("idfacultad", facultad.getIdfacultad())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(facultad.getIdfacultad().toString(), loginController.getUsername(), "delete", "facultad", facultadRepository.toDocument(facultad).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    facultadList.remove(facultad);
                    facultadFiltered = facultadList;
                    facultadDataModel = new FacultadDataModel(facultadList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagefacultad", page.toString());

                } else {
                    facultad = new Facultad();
                    facultadSelected = new Facultad();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/facultad/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (facultadRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagefacultad", page.toString());
            List<Facultad> list = new ArrayList<>();
            list.add(facultad);
            String ruta = "/resources/reportes/facultad/details.jasper";
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
            List<Facultad> list = new ArrayList<>();
            list = facultadRepository.findAll(new Document("idfacultad", 1));

            String ruta = "/resources/reportes/facultad/all.jasper";
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
            facultadList.removeAll(facultadList);
            facultadList.add(facultadSelected);
            facultadFiltered = facultadList;
            facultadDataModel = new FacultadDataModel(facultadList);
            loginController.put("searchfacultad", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = facultadRepository.sizeOfPage(rowPage);
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
            if (page < (facultadRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchfacultad")) {
                case "_init":
                    facultadList = facultadRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;

                case "idfacultad":
                    doc = new Document("idfacultad", facultad.getIdfacultad());
                    facultadList = facultadRepository.findFilterPagination(doc, page, rowPage, new Document("idfacultad", -1));
                    break;

                default:

                    facultadList = facultadRepository.findPagination(page, rowPage);
                    break;
            }

            facultadFiltered = facultadList;

            facultadDataModel = new FacultadDataModel(facultadList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchfacultad", "_init");
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

            loginController.put("searchfacultad", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
