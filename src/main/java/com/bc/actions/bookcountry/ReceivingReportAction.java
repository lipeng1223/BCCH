package com.bc.actions.bookcountry;

import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.ejb.ReceivingSessionLocal;
import com.bc.jasper.ReceivingDataSourceProvider;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.Vendor;
import com.bc.struts.result.JasperResult;
import com.bc.util.ActionRole;
import com.bc.util.DateFormat;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class ReceivingReportAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ReceivingReportAction.class);

    private String filename;
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String report(){
        ReceivingSessionLocal rSession = getReceivingSession();
        Received receiving = rSession.findById(id, "receivedItems", "vendor");
        
        if (receiving == null){
            logger.error("Could not find receiving by id: "+id);
            return "404";
        }
        
        ReceivingDataSourceProvider datasource = new ReceivingDataSourceProvider();
        datasource.setup(receiving);
        
        setJasperDatasource(datasource);
        
        HashMap paramMap = new HashMap();
        paramMap.put("poNumber", "");
        if (receiving.getPoNumber() != null){
            paramMap.put("poNumber", receiving.getPoNumber());
        }
        paramMap.put("receiveDateString", "");
        if (receiving.getCreateTime() != null){
            paramMap.put("receiveDateString", DateFormat.format(receiving.getCreateTime()));
        }
        paramMap.put("poDateString", "");
        if (receiving.getPoDate() != null){
            paramMap.put("poDateString", DateFormat.format(receiving.getPoDate()));
        }
        paramMap.put("postDateString", "");
        if (receiving.getPostDate() != null){
            paramMap.put("postDateString", DateFormat.format(receiving.getPostDate()));
        }
        paramMap.put("vendorCode", "");
        if (receiving.getVendorCode() != null){
            paramMap.put("vendorCode", receiving.getVendorCode());
        }
        paramMap.put("comment", "");
        if (receiving.getComment() != null){
            paramMap.put("comment", receiving.getComment());
        }
        Vendor vendor = receiving.getVendor();
        paramMap.put("vendorName", "");
        paramMap.put("vendorAddress1", "");
        paramMap.put("vendorCity", "");
        paramMap.put("vendorState", "");
        paramMap.put("vendorZip", "");

        if (vendor != null) {
            if (vendor.getVendorName() != null){
                paramMap.put("vendorName", vendor.getVendorName());
            }
            if (vendor.getAddress1() != null){
                paramMap.put("vendorAddress1", vendor.getAddress1());
            }
            if (vendor.getCity() != null){
                paramMap.put("vendorCity", vendor.getCity());
            }
            if (vendor.getState() != null){
                paramMap.put("vendorState", vendor.getState());
            }
            if (vendor.getZip() != null){
                paramMap.put("vendorZip", vendor.getZip());
            }
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        paramMap.put("extendedTotal", nf.format(receiving.getTotalExtendedCost()));
        
        paramMap.put("itemCountTotal", ""+0);
        if (receiving.getReceivedItems() != null){
            int itemCount = 0;
            for (ReceivedItem ri : receiving.getReceivedItems()){
                itemCount += ri.getQuantity();
            }
            paramMap.put("itemCountTotal", ""+itemCount);
        }
        
        
        setJasperParamMap(paramMap);
        setJasperReportName("receiving.jasper");
        setJasperFilename(filename);
        setJasperExportType(JasperResult.PDF);

        return "jasperreport";
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
