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
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.RevisionHistoryRepository;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.LookupServices;

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
public class SolicitudController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private SolicitudDataModel solicitudDataModel;
    

    Integer page = 1;
    Integer rowPage = 25;
 
    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Solicitud solicitud;
    Solicitud solicitudSelected;

    //List
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudFiltered = new ArrayList<>();

    //Repository
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;

    //Services
     //Atributos para busquedas
    @Inject
    LookupServices lookupServices;
    
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    SolicitudServices solicitudServices;
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
    public SolicitudController() {
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
                Optional<Solicitud> optional = solicitudRepository.find("idsolicitud", id);
                 if (optional.isPresent()) {
                    solicitud = optional.get();
                    solicitudSelected = solicitud;
                    writable = true;
                      
                }
            } 
           if (action != null && action.equals("gonew")) {
                solicitud = new Solicitud();
                solicitudSelected = solicitud;
                writable =false;

            }
            if (pageSession != null) 
            {
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
        prepare("new");
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    @Override
    public String prepare(String action, Object... item) {
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
                    if (item.length != 0) {
                        solicitudSelected = (Solicitud) item[0];
                        solicitud = solicitudSelected;
                        loginController.put("idsolicitud", solicitud.getIdsolicitud().toString());
                    }

                    url = "/pages/solicitud/view.xhtml";
                    break;
                    
                case "golist":
                    url = "/pages/solicitud/list.xhtml";
                    break;
                    
                case "gonew":
                    url = "/pages/solicitud/new.xhtml";
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
            if (JsfUtil.isVacio(solicitud.getIdsolicitud())) {
                writable = false;
                return "";
            }
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                Integer  id = solicitud.getIdsolicitud();
                solicitud = new Solicitud();
                solicitud.setIdsolicitud(id);
                solicitudSelected = new Solicitud();
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
            Optional<Solicitud> optional = solicitudRepository.findById(solicitud);
            if (optional.isPresent()) {
               JsfUtil.warningMessage(  rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            solicitud.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (solicitudRepository.save(solicitud)) {
                  //guarda el contenido anterior
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
                    "create", "solicitud", solicitudRepository.toDocument(solicitud).toString()));

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

            solicitud.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

          
            //guarda el contenido actualizado
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(),
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
            
            solicitudSelected = solicitud;
            if (solicitudRepository.delete("idsolicitud", solicitud.getIdsolicitud())) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(solicitud.getIdsolicitud().toString(), loginController.getUsername(), "delete", "solicitud", solicitudRepository.toDocument(solicitud).toString()));
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
       path="";
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
            list = solicitudRepository.findAll(new Document("idsolicitud",1));
           
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
                         solicitudList = solicitudRepository.findPagination(page, rowPage);

                        break;
                    case "_autocomplete":
                        //no se realiza ninguna accion 
                        break;
              
                    case "idsolicitud":
                        doc = new Document("idsolicitud", solicitud.getIdsolicitud());
                        solicitudList = solicitudRepository.findFilterPagination(doc, page, rowPage, new Document("idsolicitud", -1));
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


}
