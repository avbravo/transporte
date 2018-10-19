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
import com.avbravo.transporteejb.datamodel.ConductorDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporteejb.producer.LookupTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.services.ConductorServices;

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
public class ConductorController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    private String cedulanueva;
    //DataModel
    private ConductorDataModel conductorDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Conductor conductor;
    Conductor conductorSelected;

    //List
    List<Conductor> conductorList = new ArrayList<>();
    List<Conductor> conductorFiltered = new ArrayList<>();

    //Repository
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

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
    ConductorServices conductorServices;
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

        return conductorRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public String getCedulanueva() {
        return cedulanueva;
    }

    public void setCedulanueva(String cedulanueva) {
        this.cedulanueva = cedulanueva;
    }

    public LookupTransporteejbServices getLookupTransporteejbServices() {
        return lookupTransporteejbServices;
    }

    public void setLookupTransporteejbServices(LookupTransporteejbServices lookupTransporteejbServices) {
        this.lookupTransporteejbServices = lookupTransporteejbServices;
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

    public ConductorServices getConductorServices() {
        return conductorServices;
    }

    public void setConductorServices(ConductorServices conductorServices) {
        this.conductorServices = conductorServices;
    }

    public List<Conductor> getConductorList() {
        return conductorList;
    }

    public void setConductorList(List<Conductor> conductorList) {
        this.conductorList = conductorList;
    }

    public List<Conductor> getConductorFiltered() {
        return conductorFiltered;
    }

    public void setConductorFiltered(List<Conductor> conductorFiltered) {
        this.conductorFiltered = conductorFiltered;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Conductor getConductorSelected() {
        return conductorSelected;
    }

    public void setConductorSelected(Conductor conductorSelected) {
        this.conductorSelected = conductorSelected;
    }

    public ConductorDataModel getConductorDataModel() {
        return conductorDataModel;
    }

    public void setConductorDataModel(ConductorDataModel conductorDataModel) {
        this.conductorDataModel = conductorDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ConductorController() {
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
            String action = loginController.get("conductor");
            String id = loginController.get("idconductor");
            String pageSession = loginController.get("pageconductor");
            //Search

            if (loginController.get("searchconductor") == null || loginController.get("searchconductor").equals("")) {
                loginController.put("searchconductor", "_init");
            }
            writable = false;

            conductorList = new ArrayList<>();
            conductorFiltered = new ArrayList<>();
            conductor = new Conductor();
            conductorDataModel = new ConductorDataModel(conductorList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = conductorRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        conductor = new Conductor();
                        conductorSelected = conductor;
                        writable = false;

                        break;
                    case "view":
                        if (id != null) {
                            Optional<Conductor> optional = conductorRepository.find("idconductor", Integer.parseInt(id));
                            if (optional.isPresent()) {
                                conductor = optional.get();
                                conductorSelected = conductor;
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
        prepare("new", conductor);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Conductor item) {
        String url = "";
        try {
            loginController.put("pageconductor", page.toString());
            loginController.put("conductor", action);

            switch (action) {
                case "new":
                    conductor = new Conductor();
                    conductorSelected = new Conductor();

                    writable = false;
                    break;

                case "view":

                    conductorSelected = item;
                    conductor = conductorSelected;
                    loginController.put("idconductor", conductor.getIdconductor().toString());

                    url = "/pages/conductor/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/conductor/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/conductor/new.xhtml";
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
            conductorList = new ArrayList<>();
            conductorFiltered = new ArrayList<>();
            conductorList = conductorRepository.findAll();
            conductorFiltered = conductorList;
            conductorDataModel = new ConductorDataModel(conductorList);

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
            if (JsfUtil.isVacio(conductor.getCedula())) {
                writable = false;
                return "";
            }
            conductor.setCedula(conductor.getCedula().toUpperCase());

            List<Conductor> list = conductorRepository.findBy(new Document("cedula", conductor.getCedula()));
            if (!list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = conductor.getCedula();
                conductor = new Conductor();
                conductor.setCedula(id);
                conductorSelected = new Conductor();
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
            conductor.setCedula(conductor.getCedula().toUpperCase());
            List<Conductor> list = conductorRepository.findBy(new Document("cedula", conductor.getCedula()));
            if (!list.isEmpty()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
            Integer id = autoincrementableTransporteejbServices.getContador("conductor");
            conductor.setIdconductor(id);
            //Lo datos del usuario
            conductor.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (conductorRepository.save(conductor)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), loginController.getUsername(),
                        "create", "conductor", conductorRepository.toDocument(conductor).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + conductorRepository.getException().toString());
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

            conductor.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), loginController.getUsername(),
                    "update", "conductor", conductorRepository.toDocument(conductor).toString()));

            conductorRepository.update(conductor);
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
            conductor = (Conductor) item;

            if (!conductorServices.isDeleted(conductor)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            conductorSelected = conductor;
            if (conductorRepository.delete("idconductor", conductor.getIdconductor())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), loginController.getUsername(), "delete", "conductor", conductorRepository.toDocument(conductor).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    conductorList.remove(conductor);
                    conductorFiltered = conductorList;
                    conductorDataModel = new ConductorDataModel(conductorList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageconductor", page.toString());

                } else {
                    conductor = new Conductor();
                    conductorSelected = new Conductor();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/conductor/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (conductorRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageconductor", page.toString());
            List<Conductor> list = new ArrayList<>();
            list.add(conductor);
            String ruta = "/resources/reportes/conductor/details.jasper";
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
            List<Conductor> list = new ArrayList<>();
            list = conductorRepository.findAll(new Document("idconductor", 1));

            String ruta = "/resources/reportes/conductor/all.jasper";
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
         
       
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
            conductorList.removeAll(conductorList);
            conductorList.add(conductorSelected);
            conductorFiltered = conductorList;
            conductorDataModel = new ConductorDataModel(conductorList);
             
             
             
            
            
            loginController.put("searchconductor", "idconductor");
            lookupTransporteejbServices.setIdconductor(conductorSelected.getIdconductor());
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleAutocompleteOfListXhtml() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = conductorRepository.sizeOfPage(rowPage);
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
            if (page < (conductorRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchconductor")) {
                case "_init":
                    conductorList = conductorRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;

                case "idconductor":

                    conductorList = conductorRepository.findBy(new Document("idconductor", lookupTransporteejbServices.getIdconductor()), new Document("idconductor", -1));
                    break;
                case "nombre":

                    conductorList = conductorRepository.findRegexInTextPagination("nombre", lookupTransporteejbServices.getNombre(), true, page, rowPage, new Document("nombre", -1));
                    break;

                default:

                    conductorList = conductorRepository.findPagination(page, rowPage);
                    break;
            }

            conductorFiltered = conductorList;

            conductorDataModel = new ConductorDataModel(conductorList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchconductor", "_init");
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

            loginController.put("searchconductor", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearCedula()">
    public String clearCedula() {
        try {
            conductor = new Conductor();
            conductor.setCedula("");
            writable = false;
        } catch (Exception e) {
            JsfUtil.errorMessage("clearCedula() " + e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="findByCedula()">
    public String findByCedula() {
        try {
            if (JsfUtil.isVacio(conductor.getCedula())) {
                writable = false;
                return "";
            }
            conductor.setCedula(conductor.getCedula().toUpperCase());
            writable = true;
            List<Conductor> list = conductorRepository.findBy(new Document("cedula", conductor.getCedula()));
            if (list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idnotexist"));
                return "";
            } else {
                writable = true;
                conductor = list.get(0);

                conductorSelected = conductor;
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("findByCedula()" + e.getLocalizedMessage());
        }

        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="editCedula">
    public String editCedula() {
        try {

            if (conductor.getCedula().equals(cedulanueva)) {
                JsfUtil.warningMessage(rf.getMessage("warning.cedulasiguales"));
                return "";
            }

            Integer c = conductorRepository.count(new Document("cedula", cedulanueva));
            if (c >= 1) {
                JsfUtil.warningMessage(rf.getMessage("warning.conductorceduladuplicada"));
                return "";
            }

            conductor.setCedula(cedulanueva);

            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), loginController.getUsername(),
                    "update", "conductor", conductorRepository.toDocument(conductor).toString()));

            conductorRepository.update(conductor);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            JsfUtil.errorMessage("edit()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
