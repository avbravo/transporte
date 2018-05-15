/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
package com.avbravo.transporte.roles;

import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.transporte.util.ResourcesFiles;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 *
 * @authoravbravo
 */
@Named
@RequestScoped
public class ValidadorRoles implements Serializable{
private static final long serialVersionUID = 1L;

 @Inject
 ApplicationMenu applicationMenu;
 @Inject
 ResourcesFiles rf;
 @Inject
 RolAdministrador rolAdministrador;
 @Inject
 RolDocente rolDocente;
 
 @Inject
 RolSecretaria rolSecretaria;
 public Boolean validarRoles(String rolvalidacion) {

        rolvalidacion = rolvalidacion.toLowerCase();
        Boolean ok = Boolean.TRUE;
        try {
            switch (rolvalidacion) {
            case "administrador": 
                rolAdministrador.enabled();
                 break;
            case "profesor": 
                rolDocente.enabled();
                 break;
           
                 
                 
           
            case "secretaria": 
                rolSecretaria.enabled();
                 break;
            default:
                applicationMenu.enabledAll(false);
                ok = Boolean.FALSE;
                JsfUtil.warningDialog(rf.getAppMessage("warning.title"),
                        rf.getAppMessage("info.sinrolasignado"));
             }
         } catch (Exception e) {
             JsfUtil.errorMessage("validarRoles() " + e.getLocalizedMessage());
         }
         return ok;
     }




}
