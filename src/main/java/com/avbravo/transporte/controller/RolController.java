/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.AutoincrementableServices;
import com.avbravo.jmoordbutils.printer.Printer;



import com.avbravo.jmoordb.mongodb.history.ErrorInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.RolDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.services.RolServices;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
//@RequestScoped
public class RolController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;


    private Boolean writable = false;
    //DataModel
    private RolDataModel rolDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Rol rol = new Rol();
    Rol rolSelected;
    Rol rolSearch = new Rol();

    //List
    List<Rol> rolList = new ArrayList<>();

    //Repository
 
    @Inject
    RolRepository rolRepository;
      //Services
    @Inject
    ErrorInfoServices errorServices;
    @Inject
AutoincrementableServices autoincrementableServices;
    @Inject
    RolServices rolServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return rolRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public Rol getRolSearch() {
        return rolSearch;
    }

    public void setRolSearch(Rol rolSearch) {
        this.rolSearch = rolSearch;
    }

    public RolRepository getRolRepository() {
        return rolRepository;
    }

    public void setRolRepository(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
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

    public RolServices getRolServices() {
        return rolServices;
    }

    public void setRolServices(RolServices rolServices) {
        this.rolServices = rolServices;
    }

    public List<Rol> getRolList() {
        return rolList;
    }

    public void setRolList(List<Rol> rolList) {
        this.rolList = rolList;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Rol getRolSelected() {
        return rolSelected;
    }

    public void setRolSelected(Rol rolSelected) {
        this.rolSelected = rolSelected;
    }

    public RolDataModel getRolDataModel() {
        return rolDataModel;
    }

    public void setRolDataModel(RolDataModel rolDataModel) {
        this.rolDataModel = rolDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public RolController() {
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

            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario _userLogged = (Usuario) JmoordbContext.get("_userLogged");
        //    parameters.put("P_EMPRESA", _userLogged.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(rolRepository)
                    .withEntity(rol)
                    .withService(rolServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withSearchbysecondarykey(false)
                    .withPathReportDetail("/resources/reportes/rol/details.jasper")
                    .withPathReportAll("/resources/reportes/rol/all.jasper")
                    .withparameters(parameters)
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {
PrimeFaces.current().resetInputs(":form:content");
        prepareNew();
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

            aspectHandleAutocompleteOfListXhtml();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            rolDataModel = new RolDataModel(rolList);
            Document doc;

            switch ((String) JmoordbContext.get("searchrol")) {
                case "_init":
                case "_autocomplete":
                    rolList = rolRepository.findPagination(page, rowPage);

                    break;

                case "idrol":
                    if (JmoordbContext.get("_fieldsearchrol") != null) {
                         rolSearch.setIdrol(JmoordbContext.get("_fieldsearchrol").toString());
                        doc = new Document("idrol", JmoordbContext.get("_fieldsearchrol").toString());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                       
                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);

                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchrol") != null) {
                        rolSearch.setActivo(JmoordbContext.get("_fieldsearchrol").toString());
                        doc = new Document("activo", rolSearch.getActivo());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));

                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);

                    }
                    break;

                default:

                    rolList = rolRepository.findPagination(page, rowPage);
                    break;

            }

            rolDataModel = new RolDataModel(rolList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }
    }// </editor-fold>
    
   

}


