/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.commonejb.datamodel.CarreraDataModel;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.services.CarreraServices;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.repository.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.services.LookupTransporteejbServices;


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
public class CarreraController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private CarreraDataModel carreraDataModel;
    

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
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
     //Atributos para busquedas
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;
    
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
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
            loginController.put("searchcarrera", "_init");
            writable = false;

            carreraList = new ArrayList<>();
            carreraFiltered = new ArrayList<>();
            carrera = new Carrera();
            carreraDataModel = new CarreraDataModel(carreraList);
          
            
            if (id != null) {
                Optional<Carrera> optional = carreraRepository.find("idcarrera", Integer.parseInt(id));
                 if (optional.isPresent()) {
                    carrera = optional.get();
                    carreraSelected = carrera;
                    writable = true;
                      
                }
            } 
           if (action != null && action.equals("gonew")) {
                carrera = new Carrera();
                carreraSelected = carrera;
                writable =false;

            }
            if (pageSession != null) 
            {
                page = Integer.parseInt(pageSession);
            }
            Integer c = carreraRepository.sizeOfPage(rowPage);
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
        prepare("new",carrera);
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
            JsfUtil.errorMessage("prepare() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("showAll()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">

    @Override
    public String isNew() {
        try {
            writable = true;
            if (JsfUtil.isVacio(carrera.getIdcarrera())) {
                writable = false;
                return "";
            }
            Optional<Carrera> optional = carreraRepository.findById(carrera);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                Integer id = carrera.getIdcarrera();
                carrera = new Carrera();
                carrera.setIdcarrera(id);
                carreraSelected = new Carrera();
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
            Optional<Carrera> optional = carreraRepository.findById(carrera);
            if (optional.isPresent()) {
               JsfUtil.warningMessage(  rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            carrera.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (carreraRepository.save(carrera)) {
                  //guarda el contenido anterior
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(),
                    "create", "carrera", carreraRepository.toDocument(carrera).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + carreraRepository.getException().toString());
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

            carrera.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

          
            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(),
                    "update", "carrera", carreraRepository.toDocument(carrera).toString()));

            carreraRepository.update(carrera);
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
            carrera = (Carrera) item;
            
            carreraSelected = carrera;
            if (carreraRepository.delete("idcarrera", carrera.getIdcarrera())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(carrera.getIdcarrera().toString(), loginController.getUsername(), "delete", "carrera", carreraRepository.toDocument(carrera).toString()));
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
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
       // path = deleteonviewpage ? "/pages/carrera/list.xhtml" : "";
       path="";
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
             List<Carrera> list = new ArrayList<>();
            list = carreraRepository.findAll(new Document("idcarrera",1));
           
            String ruta = "/resources/reportes/carrera/all.jasper";
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
            carreraList.removeAll(carreraList);
            carreraList.add(carreraSelected);
            carreraFiltered = carreraList;
            carreraDataModel = new CarreraDataModel(carreraList);
             loginController.put("searchcarrera", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = carreraRepository.sizeOfPage(rowPage);
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
            if (page < (carreraRepository.sizeOfPage(rowPage))) {
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
                switch (loginController.get("searchcarrera")) {
                    case "_init":
                         carreraList = carreraRepository.findPagination(page, rowPage);

                        break;
                    case "_autocomplete":
                        //no se realiza ninguna accion 
                        break;
              
                    case "idcarrera":
                        doc = new Document("idcarrera", carrera.getIdcarrera());
                        carreraList = carreraRepository.findFilterPagination(doc, page, rowPage, new Document("idcarrera", -1));
                        break;
                  
                    default:

                     carreraList = carreraRepository.findPagination(page, rowPage);
                        break;
                }
            

            carreraFiltered = carreraList;

            carreraDataModel = new CarreraDataModel(carreraList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

   // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchcarrera", "_init");
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

            loginController.put("searchcarrera", string);      
      
            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>


}
