/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
package com.avbravo.transporte.reportes;


import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author avbravo
 */
public class ProgramacionFlotaDataModel extends ListDataModel<ProgramacionFlotaPojo> implements SelectableDataModel<ProgramacionFlotaPojo>{

    public ProgramacionFlotaDataModel() {
    }
    public ProgramacionFlotaDataModel(List<ProgramacionFlotaPojo>data) {
        super(data);
    }

    @Override
    public ProgramacionFlotaPojo  getRowData(String rowKey) {
        List<ProgramacionFlotaPojo> programacionFlotaList = (List<ProgramacionFlotaPojo>) getWrappedData();
        for (ProgramacionFlotaPojo programacionFlota : programacionFlotaList) {
             if (programacionFlota.getIdprogramacionFlota().equals(rowKey)) {
                 return programacionFlota;
             }
        }
        return null;
     }
     @Override
     public Object getRowKey(ProgramacionFlotaPojo programacionFlota) {
         return programacionFlota.getIdprogramacionFlota();
     }


}
