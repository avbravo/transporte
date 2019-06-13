package com.avbravo.transporte.websocket;

import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.transporteejb.entity.Usuario;
import java.io.Serializable;
import java.util.Calendar;
import static java.util.Locale.filter;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.bson.Document;

@Named
@ApplicationScoped
public class PushSocket implements Serializable {

    private static final Logger LOG = Logger.getLogger(PushSocket.class.getName());
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    
    @Inject
    @Push(channel = "notification")
    private PushContext push;
    private String value = "";
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void clockAction() {
        try {
            
            Calendar now = Calendar.getInstance();
            
            String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
            
            value = time;
           push.send(time);

            //Guardarlo en la base de datos
            JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            jmoordbNotifications.setIdjmoordbnotifications(autoincrementableServices.getContador("jmoordbnNotifications"));
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
    
//    public String actionWebSocket() {
//        try {
//            JsfUtil.warningMessage("you have a notification");
//       //     JsfUtil.errorDialog("myAction", "invocado desde el javascript");
//        Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
//        Document doc = new Document("username",jmoordb_user.getUsername()).append("viewed","no");
//        value= jmoordbNotificationsRepository.count(doc).toString();
//        
//            
//        } catch (Exception e) {
//            JsfUtil.errorDialog("actionWebSocket()", e.getLocalizedMessage());
//           value="0";
//        }
//        
//        return "";
//    }
}
