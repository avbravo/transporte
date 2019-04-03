/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.security;

import com.avbravo.jmoordb.configuration.JmoordbConfiguration;
import com.avbravo.jmoordb.configuration.JmoordbConnection;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.mongodb.history.AccessInfoRepository;
import com.avbravo.jmoordb.mongodb.history.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.ConfiguracionRepository;
import com.avbravo.jmoordb.mongodb.history.ConfiguracionServices;
import com.avbravo.jmoordb.mongodb.history.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.RevisionHistoryRepository;
import com.avbravo.jmoordb.pojos.Configuracion;
import com.avbravo.jmoordb.services.AccessInfoServices;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbsecurity.SecurityInterface;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.email.ManagerEmail;
import com.avbravo.transporte.roles.ValidadorRoles;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.UsuarioServices;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named
@SessionScoped
public class LoginController implements Serializable, SecurityInterface {
// <editor-fold defaultstate="collapsed" desc="fields">

    @Inject
    private SecurityContext securityContext;
    @Inject
    private ExternalContext externalContext;
    @Inject
    private FacesContext facesContext;

    //Atributos para la interface IController
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ConfiguracionRepository configuracionRepository;

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());
    private HashMap<String, String> parameters = new HashMap<>();

    private String passwordold;
    private String passwordnew;
    private String passwordnewrepeat;

    Configuracion configuracion = new Configuracion();
    //Acceso
    @Inject
    AccessInfoServices accessInfoServices;
    @Inject
    AccessInfoRepository accessInfoRepository;
    @Inject
    ResourcesFiles rf;
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
    /*
    
     */

    @Inject
    UsuarioRepository usuarioRepository;
    Usuario usuario = new Usuario();
    @Inject
    RolRepository rolRepository;
    Rol rol = new Rol();

    //Services
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    ConfiguracionServices configuracionServices;
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

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
        configuracion = new Configuracion();

        JmoordbConnection jmc = new JmoordbConnection.Builder()
                .withSecurity(false)
                .withDatabase("store")
                .withHost("")
                .withPort(0)
                .withUsername("")
                .withPassword("")
                .build();
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

//   String version=    getClass().getPackage().getImplementationVersion();
//            System.out.println("---> numero "+version);
            tokenwassend = false;
            userwasLoged = false;
            loggedIn = true;
            usuario = new Usuario();
            if (username == null || password == null) {
                JsfUtil.warningMessage(rf.getAppMessage("login.usernamenotvalid"));
                return null;
            }

            if (!isValidSession(username)) {
                return "";
            }

            /**
             * Cargando la configuracion
             */
            configuracion = configuracionServices.generarConfiguracionInicial(username);

            //----------------------------------------------
//Agregar al context
            JmoordbConfiguration jmc = new JmoordbConfiguration.Builder()
                    .withSpanish(true)
                    .withRepositoryRevisionHistory(revisionHistoryRepository)
                    .withRevisionHistoryServices(revisionHistoryServices)
                    .withRevisionSave(true)
                    .withUsername(username)
                    .build();

            JmoordbContext.put("_userLogged", usuario);
            JmoordbContext.put("_login_rol", rol);
//---Injectarlo en el Session
            switch (continueAuthentication()) {
                case SEND_CONTINUE:
                    facesContext.responseComplete();
                    break;
                case SEND_FAILURE:
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed", null));
                    break;
                case SUCCESS:
                    foto = "img/me.jpg";
                    loggedIn = true;
                    usuario = (Usuario) JmoordbContext.get("_userLogged");
                    // JmoordbContext.put("_userLogged", usuario);
                    saveUserInSession(username, 2100);
                    accessInfoRepository.save(accessInfoServices.generateAccessInfo(username, "login", rf.getAppMessage("login.welcome")));
                    loggedIn = true;
                    JsfUtil.successMessage(rf.getAppMessage("login.welcome") + " " + usuario.getNombre());
//                    return "/faces/pages/index.xhtml?faces-redirect=true";
                    switch(rol.getIdrol()){
                        case "DOCENTE":
                             return "/faces/pages/solicituddocente/list.xhtml?faces-redirect=true";
                        case "ADMINISTRATIVO":
                            return "/faces/pages/solicituddocente/list.xhtml?faces-redirect=true";
                        case "ADMINISTRADOR" :
                             return "/faces/pages/index.xhtml?faces-redirect=true";
                    }
                  

                case NOT_DONE:
            }

            //-----------------------------
            //              return "/dashboard.xhtml?faces-redirect=true";
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    private AuthenticationStatus continueAuthentication() {
        return securityContext.authenticate(
                (HttpServletRequest) externalContext.getRequest(),
                (HttpServletResponse) externalContext.getResponse(),
                AuthenticationParameters.withParams()
                        .credential(new UsernamePasswordCredential(username, password))
        );
    }

    // <editor-fold defaultstate="collapsed" desc="put(String key, String value)">
    public void put(String key, String value) {
        try {
            parameters.put(key, value);
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="get(String key)">
    public String get(String key) {
        String value = "";
        try {
            value = parameters.get(key);
        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return value;
    }   // </editor-fold>

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
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="destroyByUser()"> 

    public String destroyByUser() {
        try {

            userwasLoged = !destroyByUsername(username);
            if (!userwasLoged) {
                JsfUtil.successMessage(rf.getAppMessage("session.destroyedloginagain"));
            } else {
                JsfUtil.successMessage(rf.getAppMessage("session.notdestroyed"));
            }

        } catch (Exception e) {
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="destroyWithToken()">

    public String destroyByToken() {
        try {

            userwasLoged = !destroyByToken(username, mytoken);

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
            errorServices.errorMessage(JsfUtil.nameOfClass(), JsfUtil.nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }
    // </editor-fold>
}
