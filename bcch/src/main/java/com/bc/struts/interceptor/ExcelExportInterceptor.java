package com.bc.struts.interceptor;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.bc.actions.BaseAction;
import com.bc.excel.ExcelReport;
import com.bc.excel.ExcelReportExporter;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnModel;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * If this is an excel export it will bypass the default return and instead go through the ExcelResult
 * 
 */
public class ExcelExportInterceptor extends AbstractInterceptor {

    private static Logger log = Logger.getLogger(ExcelExportInterceptor.class);
    
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    // perform operation necessary before Result execution
                    // if this is a base action and an export intercept the return
                    if (invocation.getAction() instanceof BaseAction){
                        BaseAction ba = (BaseAction)invocation.getAction();
                        if (ba.getExportToExcel() || ba.getExportBulkToExcel() || ba.getExportCountToExcel() || ba.getExportWithItemsToExcel()){
                            ba.setLimit(null);
                            if (ba.getExportBulkToExcel()){
                                log.info("Export bulk update excel for: "+invocation.getProxy().getActionName()+"."+invocation.getProxy().getMethod()+"()");
                            } else if (ba.getExportCountToExcel()) {
                                log.info("Export count update excel for: "+invocation.getProxy().getActionName()+"."+invocation.getProxy().getMethod()+"()");
                            } else if (ba.getExportWithItemsToExcel()) {
                                log.info("Export to excel with items for: "+invocation.getProxy().getActionName()+"."+invocation.getProxy().getMethod()+"()");
                            } else {
                                log.info("Export to excel for: "+invocation.getProxy().getActionName()+"."+invocation.getProxy().getMethod()+"()");
                            }
                            QueryResults qr = ba.getQueryResults();
                            ExcelReport report = new ExcelReport(qr.getResults(), qr.getTableConfig().getColumnDatas(), new ArrayList<ColumnModel>(qr.getTableConfig().getColumnModels()), ba.getExportColumnsList(), ba.getExportColumnNamesList());
                            report.setStartRow(ba.getStartRow());
                            ExcelReportExporter ere = new ExcelReportExporter(report);
                            if (ba.getExtraDataWriter() != null){
                                ere.setExtraDataWriter(ba.getExtraDataWriter());
                            }
                            ba.setExcelReportExporter(ere);
                            //invocation.setResultCode("excelreport"); // just pushes back the actual result
                            invocation.setResultCode("excelfilereport"); // pushes back status json and creates a tmp file on disk
                        }
                    }
                }
            });
            return invocation.invoke();
        } catch (Throwable t){
            log.error("Intercept exception", t);
        }
        return "error";
    }
}
