/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.SemestreDataModel;
import com.avbravo.commonejb.entity.Semestre;
import com.avbravo.commonejb.repository.SemestreRepository;
import com.avbravo.commonejb.services.SemestreServices;
import com.avbravo.jmoordb.interfaces.IControllerOld;
import com.avbravo.jmoordb.mongodb.history.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
 

import com.avbravo.transporte.util.ResourcesFiles;

import com.avbravo.transporte.util.LookupServices;
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
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
public class SemestreController implements Serializable, IControllerOld {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;
    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    private Boolean writable = false;

    //Entity
    Semestre semestre;
    Semestre semestreSelected;

    //DataModel
    private SemestreDataModel semestreDataModel;

    //List
    List<Semestre> semestreList = new ArrayList<>();
    List<Semestre> semestreFiltered = new ArrayList<>();

    //Repository
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    SemestreRepository semestreRepository;

    @Inject
    RevisionHistoryRepository revisionHistoryRepository;

    //Services
    //Atributos para busquedas
   
    @Inject
    LookupServices lookupServices;
       @Inject
ErrorInfoServices errorServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
   
    @Inject
    SemestreServices semestreServices;
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

        return semestreRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
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

    public SemestreServices getSemestreServices() {
        return semestreServices;
    }

    public void setSemestreServices(SemestreServices semestreServices) {
        this.semestreServices = semestreServices;
    }

    public List<Semestre> getRolList() {
        return semestreList;
    }

    public void setRolList(List<Semestre> semestreList) {
        this.semestreList = semestreList;
    }

    public List<Semestre> getRolFiltered() {
        return semestreFiltered;
    }

    public void setRolFiltered(List<Semestre> semestreFiltered) {
        this.semestreFiltered = semestreFiltered;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public Semestre getSemestreSelected() {
        return semestreSelected;
    }

    public void setSemestreSelected(Semestre semestreSelected) {
        this.semestreSelected = semestreSelected;
    }

    public SemestreDataModel getSemestreDataModel() {
        return semestreDataModel;
    }

    public void setSemestreDataModel(SemestreDataModel semestreDataModel) {
        this.semestreDataModel = semestreDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SemestreController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="preRenderView()">
    @Override
    public String preRenderView(String action) {
        //acciones al llamar el formulario despuès del init    
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">

    @PostConstruct
    public void init() {
        try {
            String action = loginController.get("semestre");
            String id = loginController.get("idsemestre");
            String pageSession = loginController.get("pagerol");
            //Search

            if (loginController.get("searchrol") == null || loginController.get("searchrol").equals("")) {
                loginController.put("searchrol", "_init");
            }
            writable = false;

            semestreList = new ArrayList<>();
            semestreFiltered = new ArrayList<>();
            semestre = new Semestre();
            semestreDataModel = new SemestreDataModel(semestreList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = semestreRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        semestre = new Semestre();
                        semestreSelected = semestre;
                        writable = false;

                        break;
                    case "view":
                        if (id != null) {
                            Optional<Semestre> optional = semestreRepository.find("idsemestre", id);
                            if (optional.isPresent()) {
                                semestre = optional.get();
                                semestreSelected = semestre;
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
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        PrimeFaces.current().resetInputs(":form:content");
        prepare("new", semestre);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Semestre item) {
        String url = "";
        try {
            loginController.put("pagesemestre", page.toString());
            loginController.put("semestre", action);
            switch (action) {
                case "new":
                    semestre = new Semestre();
                    semestreSelected = new Semestre();

                    writable = false;
                    break;

                case "view":

                    semestreSelected = item;
                    semestre = semestreSelected;
                    loginController.put("idsemestre", semestre.getIdsemestre());

                    url = "/pages/semestre/view.xhtml";
                    break;
                case "golist":

                    url = "/pages/semestre/list.xhtml";
                    break;

                case "gonew":
                    semestre = new Semestre();
                    semestreSelected = new Semestre();
                    url = "/pages/semestre/new.xhtml";
                    break;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }

        return url;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showAll">
    @Override
    public String showAll() {
        try {
            semestreList = new ArrayList<>();
            semestreFiltered = new ArrayList<>();
            semestreList = semestreRepository.findAll();
            semestreFiltered = semestreList;
            semestreDataModel = new SemestreDataModel(semestreList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">

    @Override
    public String isNew() {
        try {
            writable = true;
            if (JsfUtil.isVacio(semestre.getIdsemestre())) {
                writable = false;
                return "";
            }
            semestre.setIdsemestre(semestre.getIdsemestre().toUpperCase());
            Optional<Semestre> optional = semestreRepository.findById(semestre);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = semestre.getIdsemestre();
                semestre = new Semestre();
                semestre.setIdsemestre(id);
                semestreSelected = new Semestre();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
            semestre.setIdsemestre(semestre.getIdsemestre().toUpperCase());
            Optional<Semestre> optional = semestreRepository.findById(semestre);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            semestre.setUserInfo(semestreRepository.generateListUserinfo(loginController.getUsername(), "create"));
            if (semestreRepository.save(semestre)) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(semestre.getIdsemestre(), loginController.getUsername(),
                        "create", "semestre", semestreRepository.toDocument(semestre).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + semestreRepository.getException().toString());
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="edit">

    @Override
    public String edit() {
        try {

            semestre.getUserInfo().add(semestreRepository.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido anterior
            //guarda el contenido actualizado
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(semestre.getIdsemestre(), loginController.getUsername(),
                    "update", "semestre", semestreRepository.toDocument(semestre).toString()));

            semestreRepository.update(semestre);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage)">

    @Override
    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            semestre = (Semestre) item;
            if (!semestreServices.isDeleted(semestre)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            semestreSelected = semestre;
            if (semestreRepository.delete("idsemestre", semestre.getIdsemestre())) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(semestre.getIdsemestre(), loginController.getUsername(), "delete", "semestre", semestreRepository.toDocument(semestre).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    semestreList.remove(semestre);
                    semestreFiltered = semestreList;
                    semestreDataModel = new SemestreDataModel(semestreList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagerol", page.toString());

                } else {
                    semestre = new Semestre();
                    semestreSelected = new Semestre();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        //path = deleteonviewpage ? "/pages/semestre/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (semestreRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagerol", page.toString());
            List<Semestre> list = new ArrayList<>();
            list.add(semestre);
            String ruta = "/resources/reportes/semestre/details.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="printAll">

    @Override
    public String printAll() {
        try {
            List<Semestre> list = new ArrayList<>();
            list = semestreRepository.findAll(new Document("idsemestre", 1));

            String ruta = "/resources/reportes/semestre/all.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {
           
        
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
           semestreList.removeAll(semestreList);
            semestreList.add(semestreSelected);
            semestreFiltered = semestreList;
            semestreDataModel = new SemestreDataModel(semestreList);
            
            loginController.put("searchsemestre", "idsemestre");
            lookupServices.setIdsemestre(semestreSelected.getIdsemestre());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = semestreRepository.sizeOfPage(rowPage);
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
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
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="next">

    @Override
    public String next() {
        try {
            if (page < (semestreRepository.sizeOfPage(rowPage))) {
                page++;
            }
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
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
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="skip(Integer page)">

    @Override
    public String skip(Integer page) {
        try {
            this.page = page;
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {
            loginController.put("searchrol", string);
            loginController.put("betweendaterol", "false");
            writable = true;
            move(page);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="clear">

    @Override
    public String clear() {
        try {
            loginController.put("searchrol", "_init");
            page = 1;
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move(Integer page) {
        try {

            Document doc;
            switch (loginController.get("searchrol")) {
                case "_init":
                       case "_autocomplete":
                    semestreList = semestreRepository.findPagination(page, rowPage);

                    break;

                case "idsemestre":
                    if (lookupServices.getIdsemestre() != null) {
                         doc = new Document("idsemestre", lookupServices.getIdsemestre());
                    semestreList = semestreRepository.findPagination(doc, page, rowPage, new Document("idsemestre", -1));
                    } else {
                        semestreList = semestreRepository.findPagination(page, rowPage);
                    }
                   
                    break;

                default:

                    semestreList = semestreRepository.findPagination(page, rowPage);
                    break;
            }

            semestreFiltered = semestreList;

            semestreDataModel = new SemestreDataModel(semestreList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    @Override
    public Integer sizeOfPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
