package com.avbravo.transporte.websocket;


import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.transporteejb.entity.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class PushSocket implements Serializable {

    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    private static final Logger LOG = Logger.getLogger(PushSocket.class.getName());
    List<Mensajes> mensajesList = new ArrayList<>();
    @Inject
    @Push(channel = "clock")
    private PushContext push;
    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Mensajes> getMensajesList() {
        System.out.println("======================================");
        System.out.println("mensajes");
        for (Mensajes m : mensajesList) {
            System.out.println("--->Time: " + m.getTime() + " : " + m.getData());
        }
        return mensajesList;
    }

    public void setMensajesList(List<Mensajes> mensajesList) {
        this.mensajesList = mensajesList;
    }

    public void clockAction() {
        try {
           
              Calendar now = Calendar.getInstance();

        String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
        LOG.log(Level.INFO, "Time: {0}", time);
        Mensajes m = new Mensajes();
        m.setTime(time);
        m.setData(time + "a");
        mensajesList.add(m);

        System.out.println("---> agregando");
        value = time;
        push.send(time);
        
        //Guardarlo en la base de datos
         JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
    Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
    jmoordbNotifications.setUsername(jmoordb_user.getUsername());
    jmoordbNotifications.setMessage(value);
    jmoordbNotifications.setViewed("no");
    jmoordbNotifications.setDate(DateUtil.fechaActual());
    jmoordbNotifications.setType("prueba");
    jmoordbNotificationsRepository.save(jmoordbNotifications);
        
        } catch (Exception e) {
            JsfUtil.errorDialog("socket()", e.getLocalizedMessage());
        }

              
        //push.send("updateNotifications");

    }

    
  
    public String actionWebSocket(){
        try {
                JsfUtil.warningMessage("Tiene una notificacion");
    JsfUtil.errorDialog("myAction", "invocado desde el javascript");
   
        } catch (Exception e) {
            JsfUtil.errorDialog("actionWebSocket()", e.getLocalizedMessage());
        }
    
    
return "";
    }
}
