/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.reportes;

import java.time.LocalDateTime;
import java.util.Date;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author avbravo
 */
@Named(value = "helloBean")
@RequestScoped
public class HelloBean {
private String id;
private Date date;
    /**
     * Creates a new instance of HelloBean
     */
    public HelloBean() {
    }
    public String getMessage() {
      return "Hi there!";
  }

  public LocalDateTime getTime() {
      return LocalDateTime.now();
  }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
  
  
}
