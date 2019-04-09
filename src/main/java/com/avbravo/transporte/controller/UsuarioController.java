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
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.UsuarioDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.RolServices;
import com.avbravo.transporteejb.services.UnidadServices;
import com.avbravo.transporteejb.services.UsuarioServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class UsuarioController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private String passwordnewrepeat;
    //DataModel
    private UsuarioDataModel usuarioDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Usuario usuario = new Usuario();
    Usuario usuarioSelected;
    Usuario usuarioSearch = new Usuario();

    //List
    List<Usuario> usuarioList = new ArrayList<>();
    //Para multiples roles
    List<Rol> rolList = new ArrayList();

    //Repository
    @Inject
    RolRepository rolRepository;
    @Inject
    UsuarioRepository usuarioRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    RolServices rolServices;
    @Inject
    UnidadServices unidadServices;
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return usuarioRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public UsuarioController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            /*
            configurar el ambiente del contusuarioler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(usuarioRepository)
                    .withEntity(usuario)
                    .withService(usuarioServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withSearchbysecondarykey(false)
                    .withSearchLowerCase(true)
                    .withPathReportDetail("/resources/reportes/usuario/details.jasper")
                    .withPathReportAll("/resources/reportes/usuario/all.jasper")
                    .withparameters(parameters)
                    .build();

            start();
            //En este caso desencriptamos el password
            String action = (String) JmoordbContext.get("usuario");
            if (action.equals("view")) {
                usuario.setPassword(JsfUtil.desencriptar(usuario.getPassword()));
                   rolList = usuario.getRol();
                usuarioSelected = usuario;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            usuarioDataModel = new UsuarioDataModel(usuarioList);
            Document doc;

            switch ((String) JmoordbContext.get("searchusuario")) {
                case "_init":
                case "_autocomplete":
                    usuarioList = usuarioRepository.findPagination(page, rowPage);
                    break;

                case "username":
                    if (JmoordbContext.get("_fieldsearchusuario") != null) {
                        usuarioSearch.setUsername(JmoordbContext.get("_fieldsearchusuario").toString());
                        doc = new Document("username", JmoordbContext.get("_fieldsearchusuario").toString());
                        usuarioList = usuarioRepository.findPagination(doc, page, rowPage, new Document("idusuario", -1));
                    } else {
                        usuarioList = usuarioRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchusuario") != null) {
                        usuarioSearch.setActivo(JmoordbContext.get("_fieldsearchusuario").toString());
                        doc = new Document("activo", usuarioSearch.getActivo());
                        usuarioList = usuarioRepository.findPagination(doc, page, rowPage, new Document("idusuario", -1));
                    } else {
                        usuarioList = usuarioRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    usuarioList = usuarioRepository.findPagination(page, rowPage);
                    break;
            }

            usuarioDataModel = new UsuarioDataModel(usuarioList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

  
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeSave()">
    public Boolean beforeSave() {
        try {
            //password nuevo no coincide
            if (!usuario.getPassword().equals(passwordnewrepeat)) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return false;
            }
            usuario.setRol(rolList);
            usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeEdit()">
    public Boolean beforeEdit() {
        try {
            usuario.setRol(rolList);
            usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="beforeDelete()">
    public Boolean beforeDelete() {
        try {
            if (usuarioServices.isDeleted(usuario)) {
                return true;
            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;

        // </editor-fold>
    }

    // <editor-fold defaultstate="collapsed" desc="completeFiltrado(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Rol> completeFiltrado(String query) {
        List<Rol> suggestions = new ArrayList<>();
        List<Rol> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = rolRepository.findRegex(field, query, true, new Document(field, 1));

            if (rolList.isEmpty()) {
                if (!temp.isEmpty()) {
                    suggestions = temp;
                }
            } else {
                if (!temp.isEmpty()) {

                    for (Rol r : temp) {
                        found = false;
                        for (Rol r2 : rolList) {
                            if (r.getIdrol().equals(r2.getIdrol())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            suggestions.add(r);
                        }

                    }
                }

            }
            //suggestions=  rolRepository.findRegex(field,query,true,new Document(field,1));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return suggestions;
    }

    // </editor-fold>
}
