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
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.pojos.JmoordbEmailMaster;
import com.avbravo.jmoordb.profiles.datamodel.JmoordbEmailMasterDataModel;
import com.avbravo.jmoordb.profiles.repository.JmoordbEmailMasterRepository;
import com.avbravo.jmoordb.services.JmoordbEmailMasterServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
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
public class JmoordbEmailMasterController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private JmoordbEmailMasterDataModel jmoordbEmailMasterDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    private String passwordnewrepeat;

    //Entity
    JmoordbEmailMaster jmoordbEmailMaster = new JmoordbEmailMaster();
    JmoordbEmailMaster jmoordbEmailMasterSelected;
    JmoordbEmailMaster jmoordbEmailMasterSearch = new JmoordbEmailMaster();

    //List
    List<JmoordbEmailMaster> jmoordbEmailMasterList = new ArrayList<>();
    List<JmoordbEmailMaster> jmoordbEmailMasterListSelected = new ArrayList<>();

    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    JmoordbEmailMasterRepository jmoordbEmailMasterRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    JmoordbEmailMasterServices jmoordbEmailMasterServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return jmoordbEmailMasterRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public JmoordbEmailMasterController() {
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
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(jmoordbEmailMasterRepository)
                    .withEntity(jmoordbEmailMaster)
                    .withService(jmoordbEmailMasterServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(true)
                    .withPathReportDetail("/resources/reportes/jmoordbEmailMaster/details.jasper")
                    .withPathReportAll("/resources/reportes/jmoordbEmailMaster/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true) 
                   .withAction("golist")
                    .build();

            start();
            outlook();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            jmoordbEmailMasterDataModel = new JmoordbEmailMasterDataModel(jmoordbEmailMasterList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    break;

                case "email":
                    if (getValueSearch() != null) {
                        jmoordbEmailMasterSearch.setEmail(getValueSearch().toString());
                        doc = new Document("email", jmoordbEmailMasterSearch.getEmail());
                        jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(doc, page, rowPage, new Document("email", -1));
                    } else {
                        jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    }

                    break;

                default:
                    jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    break;
            }

            jmoordbEmailMasterDataModel = new JmoordbEmailMasterDataModel(jmoordbEmailMasterList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeSave()">
    public Boolean beforeSave() {
        try {
            //password nuevo no coincide
            if (!JsfUtil.isValidEmail(jmoordbEmailMaster.getEmail())) {
                JsfUtil.warningMessage(rf.getMessage("warning.emailnovalido"));
                return false;
            }
            if (!validPassword()) {
                return false;
            }
            if(jmoordbEmailMaster.getMail_smtp_starttls_enable().equals("si")){
                jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
            }
            jmoordbEmailMaster.setPassword(JsfUtil.encriptar(jmoordbEmailMaster.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return false;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeEdit()">
    public Boolean beforeEdit() {
        try {

            if (!JsfUtil.isValidEmail(jmoordbEmailMaster.getEmail())) {
                JsfUtil.warningMessage(rf.getMessage("warning.emailnovalido"));
                return false;
            }
jmoordbEmailMaster.setPassword(JsfUtil.encriptar(jmoordbEmailMaster.getPassword()));
            if (!validPassword()) {
                return false;
            }
            
        
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String outlook()">
    public String outlook() {
        try {

            jmoordbEmailMaster.setMail_smtp_host("smtp.office365.com");
            jmoordbEmailMaster.setMail_smtp_auth("true");
            jmoordbEmailMaster.setMail_smtp_port("587");
            jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String gmail()">
    public String gmail() {
        try {

            jmoordbEmailMaster.setMail_smtp_host("smtp.gmail.com");
            jmoordbEmailMaster.setMail_smtp_auth("true");
            jmoordbEmailMaster.setMail_smtp_port("587");
            jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean validPassword()">
    private Boolean validPassword() {
        try {

            if (jmoordbEmailMaster.getPassword() == null || jmoordbEmailMaster.getPassword().equals("")) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordvacio"));
                return false;
            }
            if (passwordnewrepeat == null || passwordnewrepeat.equals("")) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordrepetidovacio"));
                return false;
            }
            if (!jmoordbEmailMaster.getPassword().equals(passwordnewrepeat)) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return false;
            }
            if (jmoordbEmailMaster.getPassword().length() < 6) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordmenorseis"));
                return false;
            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return false;
    }    // </editor-fold>

     // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

//        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("EMAIL MASTER", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(6);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{160, 60, 60,60,60,40});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Email", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("smtp_auth", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("smtp_host", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("smtp_port", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("smtp_starttls_enable", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Activo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (JmoordbEmailMaster jem : jmoordbEmailMasterList) {

                table.addCell(ReportUtils.PdfCell(jem.getEmail(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(jem.getMail_smtp_auth(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(jem.getMail_smtp_host(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(jem.getMail_smtp_port(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(jem.getMail_smtp_starttls_enable(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(jem.getActivo(), FontFactory.getFont("arial", 10, Font.NORMAL)));

            }
            document.add(table);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="String print">
    @Override
    public String print() {

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("EMAIL MASTER", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));



            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Email: " + jmoordbEmailMaster.getEmail(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("smtp_auth: " + jmoordbEmailMaster.getMail_smtp_auth(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("smtp_host: " + jmoordbEmailMaster.getMail_smtp_host(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("smtp_port: " + jmoordbEmailMaster.getMail_smtp_port(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("smtp_starttls_enable: " + jmoordbEmailMaster.getMail_smtp_starttls_enable(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Activo: " + jmoordbEmailMaster.getActivo(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}
