package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.ejb.AuditSessionLocal;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.criterion.Restrictions;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/inventoryaudit/list.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class InventoryAuditAction extends BaseAction {

    private static Logger logger = Logger.getLogger(InventoryAuditAction.class);
    
    private Table listTable;
    private String startDate;
    private String endDate;
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String list(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        if (startDate == null){
            Calendar prev = Calendar.getInstance();
            prev.add(Calendar.DAY_OF_MONTH, -1);
            startDate = sdf.format(prev.getTime());
        }
        if (endDate == null){
            endDate = sdf.format(Calendar.getInstance().getTime());
        }
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String listData(){
        setupListTable();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date sdate = sdf.parse(startDate);
            Date edate = sdf.parse(endDate);
            
            AuditSessionLocal aSession = getAuditSession();
            queryInput.addAndCriterion(Restrictions.eq("tableName", "inventory_item"));
            queryInput.addAndCriterion(Restrictions.ge("auditTime", sdate));
            queryInput.addAndCriterion(Restrictions.le("auditTime", edate));
            queryInput.addOrCriterion(Restrictions.eq("columnName1", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName2", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName3", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName4", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName5", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName6", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName7", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName8", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName9", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName10", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName11", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName12", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName13", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName14", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName15", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName16", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName17", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName18", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName19", "bin"));
            queryInput.addOrCriterion(Restrictions.eq("columnName20", "bin"));
            queryResults = new QueryResults(aSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }
    
    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("tableId").setType("int"));
        cd.add(new ColumnData("username"));
        cd.add(new ColumnData("isbnFromAuditMessage"));
        cd.add(new ColumnData("condFromAuditMessage"));
        cd.add(new ColumnData("auditTime"));
        cd.add(new ColumnData("previousBin"));
        cd.add(new ColumnData("currentBin"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("tableId", "TABLE ID", 50, true));
        cm.add(new ColumnModel("username", "User", 150));
        cm.add(new ColumnModel("isbnFromAuditMessage", "ISBN", 100));
        cm.add(new ColumnModel("condFromAuditMessage", "Condition", 100));
        cm.add(new ColumnModel("auditTime", "Audit Time", 150));
        cm.add(new ColumnModel("previousBin", "Previous Bin", 150));
        cm.add(new ColumnModel("currentBin", "Current Bin", 150));

        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("long", "tableId"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        //buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Inventory Items Detail Page").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("auditTime");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryInventoryBinAuditExport");
        setExcelExportSheetName("Bin Updates");
        
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    
}
