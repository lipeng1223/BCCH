package com.bc.actions.bellwether;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Restrictions;

import com.bc.ejb.bellwether.BellCustomerSessionLocal;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import com.bc.orm.BellCustomer;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="saleshistory", location="/WEB-INF/jsp/bellwether/orders/saleshistory.jsp"),
    @Result(name="titlehistory", location="/WEB-INF/jsp/bellwether/orders/titlehistory.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class HistoryAction extends OrderAction {
    
    private final Logger logger = Logger.getLogger(HistoryAction.class);
    
    private BellCustomer bellCustomer;
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String sales(){
        setupListTable();
        
        setExcelExportFileName("BellwetherCustomerSalesExport");
        setExcelExportSheetName("Sales");
        
        if (id != null){
            BellCustomerSessionLocal cSession = getBellCustomerSession();
            bellCustomer = cSession.findById(id, "bellCustomerShippings");
        }
        return "saleshistory";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String salesData(){
        setupListTable(); 
        try {
            if (id != null){
                BellOrderSessionLocal oSession = getBellOrderSession();
                BellCustomerSessionLocal cSession = getBellCustomerSession();
                bellCustomer = cSession.findById(id);
                if (bellCustomer != null){
                    queryInput.addAndCriterion(Restrictions.eq("bellCustomer", bellCustomer));
                    queryResults = new QueryResults(oSession.findAll(queryInput, "bellCustomer"));
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                }
            }
        } catch (Exception e){
            logger.error("Could not list salesData", e);
        }
        return "queryresults";
    }

    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String title(){
        setupItemListTable(null);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        t.setButtons(buttons);
        listTable.setToolbar(t);
        setExcelExportFileName("BellwetherCustomerTitleExport");
        setExcelExportSheetName("Titles");
        
        listTable.getColumnDatas().add(new ColumnData("bellOrder.invoiceNumber", "bellOrder_invoiceNumber"));
        //listTable.getColumnDatas().add(new ColumnData("bellOrder.status", "bellOrder_status"));
        listTable.getColumnDatas().add(new ColumnData("bellOrder.posted", "bellOrder_posted"));
        listTable.getColumnDatas().add(new ColumnData("bellInventory.id", "bellInventory_id"));
        Collection<ColumnModel> cms = listTable.getColumnModels();
        List cmList = new ArrayList(cms);
        cmList.add(2, new ColumnModel("bellOrder_invoiceNumber", "Order Invoice", 100));
        //cmList.add(3, new ColumnModel("bellOrder_status", "Order Status", 100));
        cmList.add(4, new ColumnModel("bellOrder_posted", "Order Posted", 100).setRenderer("booleanRenderer"));
        listTable.setColumnModels(cmList);
        
        if (id != null){
            BellCustomerSessionLocal cSession = getBellCustomerSession();
            bellCustomer = cSession.findById(id, "bellCustomerShippings");
        }
        return "titlehistory";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String titleData(){
        setupItemListTable(null);
        
        listTable.getColumnDatas().add(new ColumnData("bellOrder.invoiceNumber", "bellOrder_invoiceNumber"));
        //listTable.getColumnDatas().add(new ColumnData("bellOrder.status", "bellOrder_status"));
        listTable.getColumnDatas().add(new ColumnData("bellOrder.posted", "bellOrder_posted"));
        listTable.getColumnDatas().add(new ColumnData("bellInventory.id", "bellInventory_id"));
        Collection<ColumnModel> cms = listTable.getColumnModels();
        List cmList = new ArrayList(cms);
        cmList.add(2, new ColumnModel("bellOrder_invoiceNumber", "Order Invoice", 100));
        //cmList.add(3, new ColumnModel("bellOrder_status", "Order Status", 100));
        cmList.add(4, new ColumnModel("bellOrder_posted", "Order Posted", 100).setRenderer("booleanRenderer"));
        listTable.setColumnModels(cmList);
        
        try {
            if (id != null){
                BellOrderSessionLocal oSession = getBellOrderSession();
                BellCustomerSessionLocal cSession = getBellCustomerSession();
                bellCustomer = cSession.findById(id);
                if (bellCustomer != null){
                    HashMap<String, String> aliases = new HashMap<String, String>();
                    aliases.put("bellOrder", "bellOrder");
                    aliases.put("bellOrder.bellCustomer", "bellOrder.bellCustomer");
                    queryInput.addAndCriterion(Restrictions.eq("bellOrder.bellCustomer", bellCustomer));
                    queryResults = new QueryResults(oSession.findAllItems(queryInput, aliases, "bellOrder", "bellInventory"));
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                }
            }
        } catch (Exception e){
            logger.error("Could not list titleData", e);
        }
        return "queryresults";
    }

    public BellCustomer getBellCustomer() {
        return bellCustomer;
    }

    public void setBellCustomer(BellCustomer bellCustomer) {
        this.bellCustomer = bellCustomer;
    }


}
