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
import com.avbravo.transporteejb.datamodel.UsuarioDataModel;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.AutoincrementableRepository;
import com.avbravo.transporteejb.repository.RevisionHistoryRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.LookupServices;
import com.avbravo.transporteejb.services.RolServices;
import com.avbravo.transporteejb.services.UsuarioServices;

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
public class UsuarioController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;
    
    private String passwordnewrepeat;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private UsuarioDataModel usuarioDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Usuario usuario;
    Usuario usuarioSelected;

    //List
    List<Usuario> usuarioList = new ArrayList<>();
    List<Usuario> usuarioFiltered = new ArrayList<>();

    //Repository
    @Inject
    AutoincrementableRepository autoincrementableRepository;
    @Inject
    UsuarioRepository usuarioRepository;

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
    UsuarioServices usuarioServices;
    @Inject
    RolServices rolServices;
    
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

        return usuarioRepository.listOfPage(rowPage);
    }


    public LookupServices getLookupServices() {
        return lookupServices;
    }

    public String getPasswordnewrepeat() {
        return passwordnewrepeat;
    }

    public void setPasswordnewrepeat(String passwordnewrepeat) {
        this.passwordnewrepeat = passwordnewrepeat;
    }

    
    
    public void setLookupServices(LookupServices lookupServices) {
        this.lookupServices = lookupServices;
    }
    
    

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public RolServices getRolServices() {
        return rolServices;
    }

    public void setRolServices(RolServices rolServices) {
        this.rolServices = rolServices;
    }

    public Integer getRowPage() {
        return rowPage;
    }

    public void setRowPage(Integer rowPage) {
        this.rowPage = rowPage;
    }

    public UsuarioServices getUsuarioServices() {
        return usuarioServices;
    }

    public void setUsuarioServices(UsuarioServices usuarioServices) {
        this.usuarioServices = usuarioServices;
    }

    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    public List<Usuario> getUsuarioFiltered() {
        return usuarioFiltered;
    }

    public void setUsuarioFiltered(List<Usuario> usuarioFiltered) {
        this.usuarioFiltered = usuarioFiltered;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuarioSelected() {
        return usuarioSelected;
    }

    public void setUsuarioSelected(Usuario usuarioSelected) {
        this.usuarioSelected = usuarioSelected;
    }

    public UsuarioDataModel getUsuarioDataModel() {
        return usuarioDataModel;
    }

    public void setUsuarioDataModel(UsuarioDataModel usuarioDataModel) {
        this.usuarioDataModel = usuarioDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public UsuarioController() {
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
            String action = loginController.get("usuario");
            String id = loginController.get("username");
            String pageSession = loginController.get("pageusuario");
                //Search
            loginController.put("searchusuario", "_init");
            writable = false;

            usuarioList = new ArrayList<>();
            usuarioFiltered = new ArrayList<>();
            usuario = new Usuario();
            usuarioDataModel = new UsuarioDataModel(usuarioList);

            if (id != null) {
                Optional<Usuario> optional = usuarioRepository.find("username", id);
                if (optional.isPresent()) {
                    usuario = optional.get();
                     
                    usuario.setPassword(JsfUtil.desencriptar(usuario.getPassword()));
                   
                    usuarioSelected = usuario;
                    writable = true;

                }
            }
            if (action != null && action.equals("gonew")) {
                usuario = new Usuario();
                usuarioSelected = usuario;
                writable = false;

            }
            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = usuarioRepository.sizeOfPage(rowPage);
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
            loginController.put("pageusuario", page.toString());
            loginController.put("usuario", action);
            switch (action) {
                case "new":
                    usuario = new Usuario();
                    usuarioSelected = new Usuario();

                    writable = false;
                    break;

                case "view":
                    if (item.length != 0) {
                        usuarioSelected = (Usuario) item[0];
                        usuario = usuarioSelected;
                        loginController.put("username", usuario.getUsername());
                    }

                    url = "/pages/usuario/view.xhtml";
                    break;
                case "golist":

                    url = "/pages/usuario/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/usuario/new.xhtml";
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
            usuarioList = new ArrayList<>();
            usuarioFiltered = new ArrayList<>();
            usuarioList = usuarioRepository.findAll();
            usuarioFiltered = usuarioList;
            usuarioDataModel = new UsuarioDataModel(usuarioList);

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
           if(JsfUtil.isVacio(usuario.getUsername())){
                writable=false;
                return "";
            }
            Optional<Usuario> optional = usuarioRepository.findById(usuario);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = usuario.getUsername();
                usuario = new Usuario();
                usuario.setUsername(id);
                usuarioSelected = new Usuario();
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
            Optional<Usuario> optional = usuarioRepository.findById(usuario);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
              if (!usuario.getPassword().equals(passwordnewrepeat)) {
                //password nuevo no coincide
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return "";
            }
usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            usuario.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (usuarioRepository.save(usuario)) {
                 revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(usuario.getUsername(), loginController.getUsername(),
                    "create", "usuario", usuarioRepository.toDocument(usuario).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + usuarioRepository.getException().toString());
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
            usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            usuario.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido anterior
           

            //guarda el contenido actualizado
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(usuario.getUsername(), loginController.getUsername(),
                    "update", "usuario", usuarioRepository.toDocument(usuario).toString()));

            usuarioRepository.update(usuario);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            JsfUtil.errorMessage("edit()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage) ">

    @Override
    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            usuario = (Usuario) item;

            usuarioSelected = usuario;
            if (usuarioRepository.delete("username", usuario.getUsername())) {
                revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(usuario.getUsername(), loginController.getUsername(), "delete", "usuario", usuarioRepository.toDocument(usuario).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    usuarioList.remove(usuario);
                    usuarioFiltered = usuarioList;
                    usuarioDataModel = new UsuarioDataModel(usuarioList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageusuario", page.toString());

                } else {
                    usuario = new Usuario();
                    usuarioSelected = new Usuario();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
//        path = deleteonviewpage ? "/pages/usuario/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="deleteAll">

    @Override
    public String deleteAll() {
        if (usuarioRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageusuario", page.toString());
            List<Usuario> list = new ArrayList<>();
            list.add(usuario);
            String ruta = "/resources/reportes/usuario/details.jasper";
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
            List<Usuario> list = new ArrayList<>();
            list = usuarioRepository.findAll(new Document("username", 1));

            String ruta = "/resources/reportes/usuario/all.jasper";
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
            usuarioList.removeAll(usuarioList);
            usuarioList.add(usuarioSelected);
            usuarioFiltered = usuarioList;
            usuarioDataModel = new UsuarioDataModel(usuarioList);
             loginController.put("searchusuario", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="last">

    @Override
    public String last() {
        try {
            page = usuarioRepository.sizeOfPage(rowPage);
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
            if (page < (usuarioRepository.sizeOfPage(rowPage))) {
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
                switch (loginController.get("searchusuario")) {
                    case "_init":
                         usuarioList = usuarioRepository.findPagination(page, rowPage);

                        break;
                    case "_autocomplete":
                        //no se realiza ninguna accion 
                        break;
              
                    case "username":
                        doc = new Document("username", usuario.getUsername());
                        usuarioList = usuarioRepository.findFilterPagination(doc, page, rowPage, new Document("username", -1));
                        break;
                  
                    default:

                     usuarioList = usuarioRepository.findPagination(page, rowPage);
                        break;
                }
            

            usuarioFiltered = usuarioList;

            usuarioDataModel = new UsuarioDataModel(usuarioList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchusuario", "_init");
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

            loginController.put("searchusuario", string);      
      
            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
