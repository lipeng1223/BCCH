package com.bc.struts.result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.bc.excel.ExcelReportExporter;
import com.bc.actions.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Custom Result that returns an Excel spreadsheet.
 *
 */
public class ExcelResult extends StrutsResultSupport {

    private static Logger log = Logger.getLogger(ExcelResult.class);

    /** Name of the Action property which is an instance of ExcelExportInfo. */
    private String excelDataName = "excelReportExporter";

    /** Contains the info used to generate the Excel sheet */
    private ExcelReportExporter exporter;


    public void doExecute(String finalLocation, ActionInvocation invocation) throws IOException {

        // Get the HttpServletResponse
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletResponse response =
            (HttpServletResponse) actionContext.get(HTTP_RESPONSE);

        Object action = invocation.getAction();
        String fileName = "InventoryExport";
        String sheetName = "Export";
        HashMap<String, List> sheetMap = null;
        if (action instanceof BaseAction){
            BaseAction ba = (BaseAction)action;
            fileName = ba.getExcelExportFileName();
            sheetName = ba.getExcelExportSheetName();
            sheetMap = ba.getExcelReportExporter().getReport().getResultsMap();
        }

        // Get the excel info
        exporter = (ExcelReportExporter) invocation.getStack().findValue(conditionalParse(excelDataName, invocation));

        exporter.configureResponse(response, fileName);
        
        if (sheetMap != null) {
            List<String> sheetNames = new ArrayList<String>();
            sheetNames.addAll(sheetMap.keySet());
            exporter.writeOutputStream(response.getOutputStream(), sheetNames);
        }
        else {
            exporter.writeOutputStream(response.getOutputStream(), sheetName);
        }

    }

    /**
     * Get the excelDataName.
     *
     * @return The value of excelDataName.
     */
    public String getExcelDataName() {
        return excelDataName;
    }

    /**
     * Set the excelDataName.
     *
     * @param excelDataName
     */
    public void setExcelDataName(String excelDataName) {
        this.excelDataName = excelDataName;
    }

}