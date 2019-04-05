/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.CarreraDataModel;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.repository.CarreraRepository;
 
 
import com.avbravo.commonejb.services.CarreraServices;
import com.avbravo.commonejb.services.FacultadServices;
import com.avbravo.jmoordb.interfaces.IControllerOld;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.transporte.security.LoginController;
 
import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporte.util.ResourcesFiles;

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
public class CarreraController implements Serializable, IControllerOld {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private CarreraDataModel carreraDataModel;
    private String _old = "";
    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Carrera carrera;
    Carrera carreraSelected;

    //List
    List<Carrera> carreraList = new ArrayList<>();
    List<Carrera> carreraFiltered = new ArrayList<>();

    //Repository
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;

    //Services
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    FacultadServices facultadServices;
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    LookupServices lookupServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
  
    @Inject
    CarreraServices carreraServices;
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

        return carreraRepository.listOfPage(rowPage);
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

    public CarreraServices getCarreraServices() {
        return carreraServices;
    }

    public void setCarreraServices(CarreraServices carreraServices) {
        this.carreraServices = carreraServices;
    }

    public List<Carrera> getCarreraList() {
        return carreraList;
    }

    public void setCarreraList(List<Carrera> carreraList) {
        this.carreraList = carreraList;
    }

    public List<Carrera> getCarreraFiltered() {
        return carreraFiltered;
    }

    public void setCarreraFiltered(List<Carrera> carreraFiltered) {
        this.carreraFiltered = carreraFiltered;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Carrera getCarreraSelected() {
        return carreraSelected;
    }

    public void setCarreraSelected(Carrera carreraSelected) {
        this.carreraSelected = carreraSelected;
    }

    public CarreraDataModel getCarreraDataModel() {
        return carreraDataModel;
    }

    public void setCarreraDataModel(CarreraDataModel carreraDataModel) {
        this.carreraDataModel = carreraDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public CarreraController() {
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
            String action = loginController.get("carrera");
            String id = loginController.get("idcarrera");
            String pageSession = loginController.get("pagecarrera");
            //Search

            if (loginController.get("searchcarrera") == null || loginController.get("searchcarrera").equals("")) {
                loginController.put("searchcarrera", "_init");
            }
            writable = false;

            carreraList = new ArrayList<>();
            carreraFiltered = new ArrayList<>();
            carrera = new Carrera();
            carreraSelected = new Carrera();

            carreraDataModel = new CarreraDataModel(carreraList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = carreraRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        carrera = new Carrera();
                        carreraSelected = carrera;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {

                            Optional<Carrera> optional = carreraRepository.find("idcarrera", Integer.parseInt(id));
                            if (optional.isPresent()) {
                                carrera = optional.get();
                                carreraSelected = optional.get();
                                _old = carrera.getDescripcion();
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
        prepare("new", carrera);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Carrera item) {
        String url = "";
        try {
            loginController.put("pagecarrera", page.toString());
            loginController.put("carrera", action);
            switch (action) {
                case "new":
                    carrera = new Carrera();
                    carreraSelected = new Carrera();

                    writable = false;
                    break;

                case "view":

                    carreraSelected = item;
                    carrera = carreraSelected;
                    loginController.put("idcarrera", carrera.getIdcarrera().toString());

                    url = "/pages/carrera/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/carrera/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/carrera/new.xhtml";
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
            carreraList = new ArrayList<>();
            carreraFiltered = new ArrayList<>();
            carreraList = carreraRepository.findAll();
            carreraFiltered = carreraList;
            carreraDataModel = new CarreraDataModel(carreraList);

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
            if (JsfUtil.isVacio(carrera.getDescripcion())) {
                writable = false;
                return "";
            }
            carrera.setDescripcion(carrera.getDescripcion().toUpperCase());
            List<Carrera> list = carreraRepository.findBy("descripcion", carrera.getDescripcion());
            if (!list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String idsecond = carrera.getDescripcion();
                carrera = new Carrera();
                carrera.setDescripcion(idsecond);
                carreraSelected = new Carrera();
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
            carrera.setDescripcion(carrera.getDescripcion().toUpperCase());
            List<Carrera> list = carreraRepository.findBy("descripcion", carrera.getDescripcion());
            if (!list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            }
            Integer identity = autoincrementableServices.getContador("carrera");
            carrera.setIdcarrera(identity);

            carrera.setUserInfo(carreraRepository.generateListUserinfo(loginController.getUsername(), "create"));

            if (carreraRepository.save(carrera)) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(),
                        "create", "carrera", carreraRepository.toDocument(carrera).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + carreraRepository.getException().toString());
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

            if (!carrera.getDescripcion().equals(_old)) {

                List<Carrera> list = carreraRepository.findBy("descripcion", carrera.getDescripcion());
                if (!list.isEmpty()) {
                    writable = false;

                    JsfUtil.warningMessage(rf.getAppMessage("warning.noeditableproduceduplicado"));
                    return "";
                }

            }

            carrera.getUserInfo().add(carreraRepository.generateUserinfo(loginController.getUsername(), "update"));

            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(),
                    "update", "carrera", carreraRepository.toDocument(carrera).toString()));

            carreraRepository.update(carrera);

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
            carrera = (Carrera) item;
            if (!carreraServices.isDeleted(carrera)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            carreraSelected = carrera;
            if (carreraRepository.delete("idcarrera", carrera.getIdcarrera())) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(), "delete", "carrera", carreraRepository.toDocument(carrera).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    carreraList.remove(carrera);
                    carreraFiltered = carreraList;
                    carreraDataModel = new CarreraDataModel(carreraList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagecarrera", page.toString());

                } else {
                    carrera = new Carrera();
                    carreraSelected = new Carrera();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        //  path = deleteonviewpage ? "/pages/carrera/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (carreraRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagecarrera", page.toString());
            List<Carrera> list = new ArrayList<>();
            list.add(carrera);
            String ruta = "/resources/reportes/carrera/details.jasper";
            HashMap parameters = new HashMap();
//            parameters.put("P_EMPRESA", loginController.getUsuario().getEmpresa().getDescripcion());
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
            List<Carrera> list = new ArrayList<>();
            list = carreraRepository.findAll(new Document("idcarrera", 1));

            String ruta = "/resources/reportes/carrera/all.jasper";
            HashMap parameters = new HashMap();
//            parameters.put("P_EMPRESA", loginController.getUsuario().getEmpresa().getDescripcion());
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
            carreraList.removeAll(carreraList);
            carreraList.add(carreraSelected);
            carreraFiltered = carreraList;
            carreraDataModel = new CarreraDataModel(carreraList);

            loginController.put("searchcarrera", "idcarrera");
            lookupServices.setIdcarrera(carreraSelected.getIdcarrera());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="last">

    @Override
    public String last() {
        try {
            page = carreraRepository.sizeOfPage(rowPage);
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
            if (page < (carreraRepository.sizeOfPage(rowPage))) {
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

    @Override
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
            switch (loginController.get("searchcarrera")) {
                case "_init":
                case "_autocomplete":
                    carreraList = carreraRepository.findPagination(page, rowPage);

                    break;

                case "idcarrera":
                    if (lookupServices.getIdcarrera() != null) {
                        doc = new Document("idcarrera", lookupServices.getIdcarrera());

                        carreraList = carreraRepository.findBy(doc);
                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }

                    break;
                case "descripcion":
                    carreraList = carreraRepository.findRegexInTextPagination("descripcion", lookupServices.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));
                    break;

                default:

                    carreraList = carreraRepository.findPagination(page, rowPage);
                    break;
            }

            carreraFiltered = carreraList;

            carreraDataModel = new CarreraDataModel(carreraList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="carrera()">

    @Override
    public String clear() {
        try {
            loginController.put("searchcarrera", "_init");
            page = 1;
            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {

            loginController.put("searchcarrera", string);

            writable = true;
            move(page);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    @Override
    public Integer sizeOfPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
