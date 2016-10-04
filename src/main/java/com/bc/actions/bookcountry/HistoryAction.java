package com.bc.actions.bookcountry;

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

import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.orm.Customer;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="saleshistory", location="/WEB-INF/jsp/bookcountry/orders/saleshistory.jsp"),
    @Result(name="titlehistory", location="/WEB-INF/jsp/bookcountry/orders/titlehistory.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class HistoryAction extends OrderAction {
    
    private final Logger logger = Logger.getLogger(HistoryAction.class);
    
    private Customer customer;
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String sales(){
        setupListTable();
        
        setExcelExportFileName("BookcountryCustomerSalesExport");
        setExcelExportSheetName("Sales");
        
        if (id != null){
            CustomerSessionLocal cSession = getCustomerSession();
            customer = cSession.findById(id, "customerShippings");
        }
        return "saleshistory";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String salesData(){
        setupListTable(); 
        try {
            if (id != null){
                OrderSessionLocal oSession = getOrderSession();
                CustomerSessionLocal cSession = getCustomerSession();
                customer = cSession.findById(id);
                if (customer != null){
                    queryInput.addAndCriterion(Restrictions.eq("customer", customer));
                    queryResults = new QueryResults(oSession.findAll(queryInput, "customer"));
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
        setExcelExportFileName("BookcountryCustomerTitleExport");
        setExcelExportSheetName("Titles");
        
        listTable.getColumnDatas().add(new ColumnData("customerOrder.invoiceNumber", "customerOrder_invoiceNumber"));
        listTable.getColumnDatas().add(new ColumnData("customerOrder.status", "customerOrder_status"));
        listTable.getColumnDatas().add(new ColumnData("customerOrder.posted", "customerOrder_posted"));
        listTable.getColumnDatas().add(new ColumnData("inventoryItem.id", "inventoryItem_id"));
        listTable.getColumnDatas().add(new ColumnData("inventoryItem.available", "inventoryItem_available"));
        Collection<ColumnModel> cms = listTable.getColumnModels();
        List cmList = new ArrayList(cms);
        cmList.add(2, new ColumnModel("customerOrder_invoiceNumber", "Order Invoice", 100));
        cmList.add(3, new ColumnModel("customerOrder_status", "Order Status", 100));
        cmList.add(4, new ColumnModel("customerOrder_posted", "Order Posted", 100).setRenderer("booleanRenderer"));
        cmList.add(14, new ColumnModel("inventoryItem_available", "Current Available", 100));
        listTable.setColumnModels(cmList);
        
        if (id != null){
            CustomerSessionLocal cSession = getCustomerSession();
            customer = cSession.findById(id, "customerShippings");
        }
        return "titlehistory";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String titleData(){
        setupItemListTable(null);
        
        listTable.getColumnDatas().add(new ColumnData("customerOrder.invoiceNumber", "customerOrder_invoiceNumber"));
        listTable.getColumnDatas().add(new ColumnData("customerOrder.status", "customerOrder_status"));
        listTable.getColumnDatas().add(new ColumnData("customerOrder.posted", "customerOrder_posted"));
        listTable.getColumnDatas().add(new ColumnData("inventoryItem.id", "inventoryItem_id"));
        listTable.getColumnDatas().add(new ColumnData("inventoryItem.available", "inventoryItem_available"));
        Collection<ColumnModel> cms = listTable.getColumnModels();
        List cmList = new ArrayList(cms);
        cmList.add(2, new ColumnModel("customerOrder_invoiceNumber", "Order Invoice", 100));
        cmList.add(3, new ColumnModel("customerOrder_status", "Order Status", 100));
        cmList.add(4, new ColumnModel("customerOrder_posted", "Order Posted", 100).setRenderer("booleanRenderer"));
        cmList.add(14, new ColumnModel("inventoryItem_available", "Current Available", 100));
        listTable.setColumnModels(cmList);
        
        try {
            if (id != null){
                OrderSessionLocal oSession = getOrderSession();
                CustomerSessionLocal cSession = getCustomerSession();
                customer = cSession.findById(id);
                if (customer != null){
                    HashMap<String, String> aliases = new HashMap<String, String>();
                    aliases.put("customerOrder", "customerOrder");
                    aliases.put("customerOrder.customer", "customerOrder.customer");
                    queryInput.addAndCriterion(Restrictions.eq("customerOrder.customer", customer));
                    queryResults = new QueryResults(oSession.findAllItems(queryInput, aliases, "customerOrder", "inventoryItem"));
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                }
            }
        } catch (Exception e){
            logger.error("Could not list titleData", e);
        }
        return "queryresults";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


}
