package com.avbravo.transporte.websocket;


import com.avbravo.jmoordbutils.JsfUtil;
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
        
        } catch (Exception e) {
            JsfUtil.errorDialog("socket()", e.getLocalizedMessage());
        }

              
        //push.send("updateNotifications");

    }

}
