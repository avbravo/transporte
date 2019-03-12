/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbsecurity.SecurityInterface;

import javax.inject.Inject;
import com.avbravo.jmoordbutils.email.ManagerEmail;
import com.avbravo.jmoordb.services.AccessInfoServices;
import com.avbravo.transporte.roles.ValidadorRoles;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.producer.AccessInfoTransporteejbRepository;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import java.util.logging.Logger;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@SessionScoped
public class LoginController implements Serializable, SecurityInterface {

// <editor-fold defaultstate="collapsed" desc="fields">
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());
    private HashMap<String, String> parameters = new HashMap<>();

    private String passwordold;
    private String passwordnew;
    private String passwordnewrepeat;

    Rol rol = new Rol();

    //Acceso
    @Inject
    AccessInfoServices accessInfoServices;
    @Inject
    AccessInfoTransporteejbRepository accessInfoTransporteejbRepository;
    @Inject
    ResourcesFiles rf;
      @Inject
ErrorInfoTransporteejbServices errorServices;
    @Inject
    ValidadorRoles validadorRoles;
    Boolean loggedIn = false;
    private String username;
    private String password;
    private String foto;
    private String id;
    private String key;
    String usernameSelected;
    Boolean recoverSession = false;
    Boolean userwasLoged = false;
    Boolean tokenwassend = false;
    String usernameRecover = "";
    String myemail = "@gmail.com";
    String mytoken = "";
    @Inject
    UsuarioRepository usuarioRepository;
    Usuario usuario = new Usuario();
    @Inject
    RolRepository rolRepository;

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getPasswordold() {
        return passwordold;
    }

    public void setPasswordold(String passwordold) {
        this.passwordold = passwordold;
    }

    public String getPasswordnew() {
        return passwordnew;
    }

    public void setPasswordnew(String passwordnew) {
        this.passwordnew = passwordnew;
    }

    public String getPasswordnewrepeat() {
        return passwordnewrepeat;
    }

    public void setPasswordnewrepeat(String passwordnewrepeat) {
        this.passwordnewrepeat = passwordnewrepeat;
    }

    public String getMyemail() {
        return myemail;
    }

    public void setMyemail(String myemail) {
        this.myemail = myemail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getTokenwassend() {
        return tokenwassend;
    }

    public void setTokenwassend(Boolean tokenwassend) {
        this.tokenwassend = tokenwassend;
    }

    public String getMytoken() {
        return mytoken;
    }

    public void setMytoken(String mytoken) {
        this.mytoken = mytoken;
    }

    public String getUsernameSelected() {
        return usernameSelected;
    }

    public void setUsernameSelected(String usernameSelected) {
        this.usernameSelected = usernameSelected;
    }

    public Boolean getUserwasLoged() {
        return userwasLoged;
    }

    public void setUserwasLoged(Boolean userwasLoged) {
        this.userwasLoged = userwasLoged;
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        loggedIn = false;
        recoverSession = false;
        userwasLoged = false;
        tokenwassend = false;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="destroy">
    @PreDestroy
    public void destroy() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public LoginController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="irLogin">
    public String irLogin() {
//        return "/faces/login";
        return "/login";
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="doLogin">
    public String doLogin() {
        try {

            tokenwassend = false;
            userwasLoged = false;
            loggedIn = true;
            usuario = new Usuario();
            if (username == null || password == null) {
                JsfUtil.warningMessage(rf.getAppMessage("login.usernamenotvalid"));
                return null;
            }
            usernameRecover = usernameRecoveryOfSession();
            recoverSession = !usernameRecover.equals("");
            if (recoverSession) {
                invalidateCurrentSession();
                //  RequestContext.getCurrentInstance().execute("PF('sessionDialog').show();");
                JsfUtil.warningMessage(rf.getAppMessage("session.procederacerrar"));
                return "";
            }
            if (recoverSession && usernameRecover.equals(username)) {
            } else {
                if (isUserLogged(username)) {
                    userwasLoged = true;
                    JsfUtil.warningMessage(rf.getAppMessage("login.alreadylogged"));
                    if (destroyByUsername(username)) {

                    }
                    return "";
                }

            }
            if (!isUserValid()) {
                accessInfoTransporteejbRepository.save(accessInfoServices.generateAccessInfo(username, "login", rf.getAppMessage("login.usernameorpasswordnotvalid")));
                JsfUtil.warningMessage(rf.getAppMessage("login.usernameorpasswordnotvalid"));
                return "";

            }
            saveUserInSession(username, 2100);
            accessInfoTransporteejbRepository.save(accessInfoServices.generateAccessInfo(username, "login", rf.getAppMessage("login.welcome")));
            loggedIn = true;
            foto = "img/me.jpg";
            JsfUtil.successMessage(rf.getAppMessage("login.welcome") + " " + usuario.getNombre());
            if (rol.getIdrol().equals("DOCENTE")) {
                return "/faces/pages/solicituddocente/list.xhtml?faces-redirect=true";
            } else {
                if (rol.getIdrol().equals("ADMINISTRATIVO")) {
                 return "/faces/pages/solicitudadministrativo/list.xhtml?faces-redirect=true";
                } else {
                    return "/faces/index.xhtml?faces-redirect=true";
                }
            }

            //              return "/dashboard.xhtml?faces-redirect=true";
        } catch (Exception e) {
errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isValid">
    /**
     * verifica si es valido el usuario
     *
     * @return
     */
    private Boolean isUserValid() {
        Boolean isvalid = false;
        try {
            if (username.isEmpty() || username.equals("") || username == null) {
                JsfUtil.successMessage(rf.getAppMessage("warning.usernameisempty"));
                return false;
            }
            if (password.isEmpty() || password.equals("") || password == null) {
                JsfUtil.successMessage(rf.getAppMessage("warning.passwordisempty"));
                return false;
            }
            usuario.setUsername(username);
            Optional<Usuario> optional = usuarioRepository.findById(usuario);
            if (!optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("login.usernamenotvalid"));
                return false;
            } else {
                Usuario u2 = optional.get();
//               usuario = optional.get();
                usuario = u2;
                if (!JsfUtil.desencriptar(usuario.getPassword()).equals(password)) {
                    JsfUtil.successMessage(rf.getAppMessage("login.passwordnotvalid"));
                    return false;
                }
                if (usuario.getActivo().equals("no")) {
                    JsfUtil.successMessage(rf.getAppMessage("login.usuarioinactivo"));
                    return false;
                }
                //Valida los roles del usuario si coincide con el seleccionado
                Boolean foundrol = false;
                for (Rol r : usuario.getRol()) {
                    if (rol.getIdrol().equals(r.getIdrol())) {
                        foundrol = true;
                    }
                }
                if (!foundrol) {
                    JsfUtil.successMessage(rf.getAppMessage("login.notienerolenelsistema") + " " + rol.getIdrol());
                    return false;
                }
//                if (!validadorRoles.validarRoles(usuario.getRol().getIdrol())) {
//                    JsfUtil.successMessage(rf.getAppMessage("login.notienerolenelsistema") + " " + usuario.getRol().getIdrol());
                if (!validadorRoles.validarRoles(rol.getIdrol())) {
                    JsfUtil.successMessage(rf.getAppMessage("login.notienerolenelsistema") + " " + rol.getIdrol());
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return isvalid;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="sendToken()"> 

    public String sendToken() {
        try {

//            if(!myemail.equals("emailusuario")){
//                //no es el email del usuario
//            }
            ManagerEmail managerEmail = new ManagerEmail();
            String token = tokenOfUsername(username);
            if (!token.equals("")) {

                String texto = rf.getAppMessage("token.forinitsession") + " " + token + rf.getAppMessage("token.forinvalidate ");
                if (managerEmail.send(myemail, rf.getAppMessage("token.tokenofsecurity"), texto, "adminemail@gmail.com", "adminpasswordemail")) {
                    JsfUtil.successMessage(rf.getAppMessage("token.wassendtoemail"));
                    tokenwassend = true;
                } else {
                    JsfUtil.warningMessage(rf.getAppMessage("token.errortosendemail"));
                }
            } else {
                JsfUtil.warningMessage(rf.getAppMessage("token.asiganedtouser"));
            }

        } catch (Exception e) {
errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="destroyByUser()"> 

    public String destroyByUser() {
        try {
            if (isUserValid()) {
                userwasLoged = !destroyByUsername(username);
                if (!userwasLoged) {
                    JsfUtil.successMessage(rf.getAppMessage("session.destroyedloginagain"));
                } else {
                    JsfUtil.successMessage(rf.getAppMessage("session.notdestroyed"));
                }
            } else {
                JsfUtil.warningMessage(rf.getAppMessage("warning.usernotvalid"));
            }
        } catch (Exception e) {
           errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="destroyWithToken()">

    public String destroyByToken() {
        try {
            if (isUserValid()) {
                userwasLoged = !destroyByToken(username, mytoken);

            } else {
                JsfUtil.warningMessage("Los datos del usuario no son validos");
            }
        } catch (Exception e) {
            JsfUtil.warningMessage(rf.getAppMessage("warning.usernotvalid"));
        }
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="invalidateCurrentSession"> 

    public String invalidateCurrentSession() {
        try {
            if (invalidateMySession()) {
                JsfUtil.successMessage(rf.getAppMessage("sesion.invalidate"));
            } else {
                JsfUtil.warningMessage(rf.getAppMessage("sesion.errortoinvalidate"));
            }

        } catch (Exception e) {
            JsfUtil.successMessage("invalidateCurrentSession() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="doLogout">

    public String doLogout() {
        return logout("/transporte/faces/login.xhtml?faces-redirect=true");
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="changePassword">
    public String changePassword() {
        try {
            if (passwordold.isEmpty() || passwordold.equals("") || passwordold == null) {
                //password anterior no debe estar vacio
                JsfUtil.warningMessage(rf.getMessage("warning.passwordvacio"));
                return "";
            }
            if (passwordnew.isEmpty() || passwordnew.equals("") || passwordold == null) {
                //password nuevo no debe estar vacio
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnuevovacio"));
                return "";
            }
            if (passwordnewrepeat.isEmpty() || passwordnewrepeat.equals("") || passwordnewrepeat == null) {
                //el password repetido no coincide
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnuevorepetidovacio"));
                return "";
            }
            if (!passwordnew.equals(passwordnewrepeat)) {
                //password nuevo no coincide
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return "";
            }

            if (!passwordold.equals(JsfUtil.desencriptar(usuario.getPassword()))) {
                //password anterior no valido
                JsfUtil.warningMessage(rf.getMessage("warning.passwordanteriornoescorrecto"));
                return "";
            }
            if (passwordold.equals(passwordnew)) {
                //esta colocando el password anterior como nuevo
                JsfUtil.warningMessage(rf.getMessage("warning.passwordanteriorigualalnuevo"));
                return "";
            }
            usuario.setPassword(JsfUtil.encriptar(passwordnew));
            usuarioRepository.update(usuario);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="put(String key, String value)">
    public void put(String key, String value) {
        try {
            parameters.put(key, value);
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="get(String key)">
    public String get(String key) {
        String value = "";
        try {
            value = parameters.get(key);
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(),JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return value;
    }   // </editor-fold>

}
