package com.bc.actions.bellwether;

import com.bc.actions.BaseAction;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.bellwether.BellCustomerSessionLocal;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import com.bc.orm.*;
import com.bc.struts.QueryResults;
import com.bc.table.*;
import com.bc.util.ActionRole;
import com.bc.util.Timing;
import com.bc.util.cache.BellCustomerCache;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="searchlist", location="/WEB-INF/jsp/bellwether/orders/search.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bellwether/orders/searchwindow.jsp"),    
    @Result(name="crud", location="/WEB-INF/jsp/bellwether/orders/crud.jsp"),
    @Result(name="list", location="/WEB-INF/jsp/bellwether/orders/list.jsp"),
    @Result(name="view", location="/WEB-INF/jsp/bellwether/orders/view.jsp"),
    @Result(name="viewamazon", location="/WEB-INF/jsp/bellwether/orders/viewamazon.jsp"),
    @Result(name="viewdetail", location="/WEB-INF/jsp/bellwether/orders/viewdetail.jsp"),
    @Result(name="viewdetail-internal", location="/WEB-INF/jsp/bellwether/orders/viewdetail-internal.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bellwether/orders/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class OrderAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(OrderAction.class);

    private final Float TAX = 1.07F;
    
    private BellOrder order;
    protected Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    private List<BellCustomer> customers;
    
    private Long customerId;
    private Long customerShippingId;
    private String orderDateString;
    private String postDateString;
    private String shipDateString;
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    
    @ActionRole({"BcOrderAdmin"})
    public String create(){
        customers = BellCustomerCache.getCustomers();
        return "crud";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String createSubmit(){
        if (orderDateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                order.setOrderDate(sdf.parse(orderDateString));
                order.setPurchaseDate(order.getOrderDate());
            } catch (Exception e){
                order.setOrderDate(Calendar.getInstance().getTime());
                logger.error("invalid order date: "+orderDateString);
            }
        }
        try {
            order.setCategory("Internal");
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            //BellCustomerShippingSessionLocal customerShippingSession = getCustomerShippingSession();
            if (customerId != null && customerId > -1L){
                BellCustomer customer = customerSession.findById(customerId);
                order.setBellCustomer(customer);
            }
            /* TODO
            if (customerShippingId != null && customerShippingId > -1L){
                order.setCustomerShipping(customerShippingSession.findById(customerShippingId));
            }
            */
            
            if (order.getShippingCharges() == null) order.setShippingCharges(0F);
            if (order.getDepositAmmount() == null) order.setDepositAmmount(0F);
            
            BellOrderSessionLocal oSession = getBellOrderSession();
            oSession.create(order);
            id = order.getId();
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not create the bell order, there was a system error.");
            logger.error("Could not create order", e);
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String edit(){
        customers = BellCustomerCache.getCustomers();
        BellOrderSessionLocal oSession = getBellOrderSession();
        order = oSession.findById(id, "bellCustomer");
        return "crud";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String editSubmit(){
        if (orderDateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                order.setOrderDate(sdf.parse(orderDateString));
                order.setPurchaseDate(order.getOrderDate());
            } catch (Exception e){
                logger.error("invalid order date: "+orderDateString);
            }
        }
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            BellOrder dbco = oSession.findById(order.getId());
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            BellCustomer customer = null;
            BellCustomerShipping customerShipping = null;
            if (customerId != null && customerId > -1L){
                customer = customerSession.findById(customerId);
            }
            if (customerShippingId != null && customerShippingId > -1L){
                customerShipping = customerSession.findShippingById(customerShippingId);
            }
            dbco.setBellCustomer(customer);
            if (customer != null){
                dbco.setCustomerCode(customer.getCode());
            }
            dbco.setBellCustomerShipping(customerShipping);
            
            dbco.setComment(order.getComment());
            dbco.setComment2(order.getComment2());
            // TODO credit
            //dbco.setCreditMemo(creditMemo)
            dbco.setCustomerVisit(order.getCustomerVisit());
            dbco.setDepositAmmount(order.getDepositAmmount());
            dbco.setOrderDate(order.getOrderDate());
            dbco.setPurchaseDate(order.getPurchaseDate());
            dbco.setPoNumber(order.getPoNumber());
            dbco.setSalesman(order.getSalesman());
            dbco.setShippingCharges(order.getShippingCharges());
            dbco.setShipVia(order.getShipVia());
            
            if (dbco.getShippingCharges() == null) dbco.setShippingCharges(0F);
            if (dbco.getDepositAmmount() == null) dbco.setDepositAmmount(0F);
            
            oSession.update(dbco);
            oSession.recalculateAllOrderTotals(dbco.getId());
            
            id = dbco.getId();
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get update the bell order", e);
            setSuccess(false);
            setMessage("Could not update the order, there was a system error.");
        }
        return "status";
    }
    
    
    @ActionRole({"BellOrderAdmin"})
    public String delete(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id);
            if (order == null){
                setSuccess(false);
                setMessage("Could not delete the order, the order no longer exists in the system.");
            }
            logger.info("deleting bell order id: "+id);
            List<Long> inventoryItemIds = oSession.getAllInventoryItemIds(id);
            oSession.delete(id);
            logger.info("deleted bellorder id: "+id);
            BellInventorySessionLocal iiSession = getBellInventorySession();
            logger.info("Recalculating all of the bell inventory items committed...");
            for (Long iiId : inventoryItemIds){
                iiSession.recalculateCommitted(iiId);
            }
            logger.info("Finished bell order delete.");
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get delete the bell order", e);
            setSuccess(false);
            setMessage("Could not delete the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String listData(){
        setupListTable();
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            if (quickSearch != null){
                Disjunction dis = Restrictions.disjunction();
                dis.add(Restrictions.ilike("poNumber", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("invoiceNumber", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("comment", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("salesman", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("customerCode", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("sku", quickSearch, MatchMode.ANYWHERE));
                queryInput.addAndCriterion(dis);
            }
            queryResults = new QueryResults(oSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not listData", e);
        }
        return "queryresults";
    }

    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String view(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id, "bellCustomer");
            if (order.getCategory().equals("Amazon")){
                return "viewamazon";
            }
            setupItemListTable(order);
        } catch (Exception e){
            logger.error("Could not view order", e);
        }
        return "view";
    }

    private void setupSearchNames() {
        if (listTable.getFilters() != null){
            for(Filter f : listTable.getFilters()) {
                ColumnModel cm = listTable.getColumnModel(f.getName());
                if (cm != null)
                    searchNames.put(f.getName(), cm.getHeader());
            }
        }
        if (listTable.getAdditionalSearch() != null){
            // additional order items search
            for(Filter f : listTable.getAdditionalSearch()) {
                searchNames.put(f.getName(), f.getDisplay());
            }
        }
    }

    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String searchData(){
        //logger.info("start");
        setupListTable(); 
        setupSearchNames();
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            HashMap<String, String> aliases = new HashMap<String, String>();
            aliases.put("bellOrderItems", "bellOrderItems");
            queryResults = new QueryResults(oSession.findAll(queryInput, aliases, "bellCustomer"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        //logger.info("end");
        return "queryresults";
    }

    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String quickItems(){
        setupItemListTable(null, false);
        return "quickitems";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String listItemData(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id);
            setupItemListTable(order); 
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("bellOrder", order));
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "bellInventory", "bellOrder"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listItemData", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String detail(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        order = oSession.findById(id, "bellCustomer", "bellCustomerShipping");
        return "detail";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String viewDetail(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        order = oSession.findById(id, "bellCustomer", "bellCustomerShipping");
        if (order.getCategory().equals("Internal")) return "viewdetail-internal";
        return "viewdetail";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String shipDate(){
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date shipDate = sdf.parse(shipDateString);
            
        BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id);
            
            order.setShipDate(shipDate);
            oSession.update(order);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not set ship date", e);
            setSuccess(false);
            setMessage("Could not set the ship date of the order, there was a system error.");
        }
        return "status";
    }

    @ActionRole({"BellOrderAdmin"})
    public String shipMax(){
        try {
            
            BellOrderSessionLocal oSession = getBellOrderSession();
            oSession.shipMax(id);
            oSession.recalculateAllOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not ship max", e);
            setSuccess(false);
            setMessage("Could not ship current max, there was a system error.");
        }
        return "status";
    }

    @ActionRole({"BellOrderAdmin"})
    public String post(){
        try {
            logger.info("post order id: "+id);
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date postDate = sdf.parse(postDateString);
            
            Timing t = new Timing("POST");
            t.start();
            
            Timing t1 = new Timing("lifo");
            t1.start();
            // we try this a couple of times in case of stale object exceptions
            boolean done = false;
            int count = 0;
            List<Long> iiIds = null;
            Exception currException = null;
            while (!done && count < 5){
                count++;
                try {
                    LifoSessionLocal lifo = getLifoSession();
                    iiIds = lifo.postBellOrder(id, ServletActionContext.getRequest().getUserPrincipal().getName(), postDate);
                    done = true;
                } catch (Exception e){
                    if (e.getMessage().startsWith("Cost is not set")){
                        setSuccess(false);
                        setMessage(e.getMessage());
                        return "status";
                    }
                    currException = e;
                    logger.error("Could not post order "+id+", retrying: "+e.getMessage());
                }
            }
            t1.stop();
            if (!done) throw currException;

            Timing t2 = new Timing("recalc");
            t2.start();
            done = false;
            count = 0;
            while (!done && count < 5){
                count++;
                try {
                    BellInventorySessionLocal biSession = getBellInventorySession();
                    for (Long iiId : iiIds){
                        biSession.recalculateCommitted(iiId);
                    }
                    done = true;
                } catch (Exception e){
                    logger.error("Could not recalc committed for inventory item, retrying: "+e.getMessage());
                }
            }
                
            t2.stop();
            t.stop();
            
            getBellOrderSession().recalculateOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not post order", e);
            setSuccess(false);
            setMessage("Could not post the order, there was a system error.");
        }
        return "status";
    }
    
    protected void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("orderId"));
        cd.add(new ColumnData("category"));
        cd.add(new ColumnData("orderDate").setType("date"));
        cd.add(new ColumnData("paymentsStatus"));
        cd.add(new ColumnData("orderItemId"));
        cd.add(new ColumnData("paymentsDate").setType("date"));
        cd.add(new ColumnData("paymentsTransactionId"));
        cd.add(new ColumnData("itemName"));
        cd.add(new ColumnData("listingId"));
        cd.add(new ColumnData("sku"));
        cd.add(new ColumnData("price").setType("float"));
        cd.add(new ColumnData("shippingFee").setType("float"));
        cd.add(new ColumnData("quantityPurchased").setType("int"));
        cd.add(new ColumnData("totalPriceForDisplay").setType("float"));
        cd.add(new ColumnData("purchaseDate").setType("date"));
        cd.add(new ColumnData("batchId"));
        cd.add(new ColumnData("buyerEmail"));
        cd.add(new ColumnData("buyerName"));
        cd.add(new ColumnData("recipientName"));
        cd.add(new ColumnData("location"));
        cd.add(new ColumnData("shipAddress1"));
        cd.add(new ColumnData("shipAddress2"));
        cd.add(new ColumnData("shipCity"));
        cd.add(new ColumnData("shipState"));
        cd.add(new ColumnData("shipZip"));
        cd.add(new ColumnData("shipCountry"));
        cd.add(new ColumnData("specialComments"));
        cd.add(new ColumnData("upc"));
        cd.add(new ColumnData("shipMethod"));
        /*
        cd.add(new ColumnData("orderHandlingState").setType("int"));
        cd.add(new ColumnData("orderHandler"));
        cd.add(new ColumnData("partialRefund"));
        cd.add(new ColumnData("invoiceNumber"));
        cd.add(new ColumnData("salesman"));
        cd.add(new ColumnData("comment"));
        cd.add(new ColumnData("comment2"));
        cd.add(new ColumnData("poNumber"));
        cd.add(new ColumnData("customerCode"));
        cd.add(new ColumnData("shipVia"));
        cd.add(new ColumnData("shippingCharges").setType("float"));
        cd.add(new ColumnData("depositAmmount").setType("float"));
        cd.add(new ColumnData("customerVisit").setType("boolean"));
        cd.add(new ColumnData("creditMemo").setType("boolean"));
        cd.add(new ColumnData("posted").setType("boolean"));
        cd.add(new ColumnData("refundCategory"));
        cd.add(new ColumnData("shipDate").setType("date"));
        cd.add(new ColumnData("orderDate").setType("date"));
        cd.add(new ColumnData("postDate").setType("date"));
        cd.add(new ColumnData("extended").setType("float"));
        cd.add(new ColumnData("backordered").setType("int"));
        cd.add(new ColumnData("cost").setType("float"));
        */
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 80));
        cm.add(new ColumnModel("orderId", "Order ID", 125));
        cm.add(new ColumnModel("category", "Category", 80));
        cm.add(new ColumnModel("paymentsStatus", "Payment Status", 100));
        cm.add(new ColumnModel("orderItemId", "Order Item Id", 100));
        cm.add(new ColumnModel("purchaseDate", "Purchase Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("paymentsDate", "Payment Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("paymentsTransactionId", "Payment Trans Id", 100));
        cm.add(new ColumnModel("sku", "SKU", 175));
        cm.add(new ColumnModel("listingId", "Listing ID", 100));
        cm.add(new ColumnModel("itemName", "Title", 200));
        cm.add(new ColumnModel("quantityPurchased", "Quantity", 75));
        cm.add(new ColumnModel("price", "Price", 75).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("shippingFee", "Shipping", 75).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("totalPriceForDisplay", "Total", 75).setRenderer("moneyRenderer").setSortable(false));
        cm.add(new ColumnModel("batchId", "Batch ID", 100));
        cm.add(new ColumnModel("buyerEmail", "Buyer Email", 100));
        cm.add(new ColumnModel("buyerName", "Buyer Name", 100));
        cm.add(new ColumnModel("recipientName", "Recipient Name", 100));
        cm.add(new ColumnModel("location", "Location", 100));
        cm.add(new ColumnModel("shipMethod", "Ship Method", 100));
        cm.add(new ColumnModel("shipAddress1", "Ship Address 1", 100));
        cm.add(new ColumnModel("shipAddress2", "Ship Address 2", 100));
        cm.add(new ColumnModel("shipCity", "Ship City", 100));
        cm.add(new ColumnModel("shipState", "Ship State", 100));
        cm.add(new ColumnModel("shipZip", "Ship Zip", 100));
        cm.add(new ColumnModel("shipCountry", "Ship Country", 100));
        cm.add(new ColumnModel("specialComments", "Special Comments", 100));
        /*
        cm.add(new ColumnModel("upc", "UPC", 100));
        cm.add(new ColumnModel("orderHandlingState", "Order Handling State", 100));
        cm.add(new ColumnModel("partialRefund", "Partial Refund", 100));
        cm.add(new ColumnModel("invoiceNumber", "invoiceNumber", 100));
        cm.add(new ColumnModel("salesman", "Salesman", 100));
        cm.add(new ColumnModel("comment", "Comment", 100));
        cm.add(new ColumnModel("comment2", "Comment 2", 100));
        cm.add(new ColumnModel("poNumber", "PO Number", 100));
        cm.add(new ColumnModel("customerCode", "customerCode", 100));
        cm.add(new ColumnModel("shipVia", "Ship Via", 100));
        cm.add(new ColumnModel("shippingCharges", "Shipping Charges", 100));
        cm.add(new ColumnModel("depositAmmount", "Deposit", 100));
        cm.add(new ColumnModel("customerVisit", "Customer Visit", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("creditMemo", "Credit Memo", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("refundCategory", "Refund Category", 100));
        cm.add(new ColumnModel("shipDate", "Ship Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("orderDate", "Order Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("extended", "Extended", 100));
        cm.add(new ColumnModel("backordered", "Backordered", 100));
        cm.add(new ColumnModel("cost", "Cost", 100));
        */
        listTable.setColumnModels(cm);
        
        
        List<Filter> moreSearch = new ArrayList<Filter>();
        moreSearch.add(new Filter("string", "bellOrderItems.isbn", "Order Item: ISBN"));
        moreSearch.add(new Filter("string", "bellOrderItems.title", "Order Item: Title"));
        moreSearch.add(new Filter("string", "bellOrderItems.bin", "Order Item: Bin"));
        moreSearch.add(new Filter("int", "bellOrderItems.quantity", "Order Item: Quantity"));
        moreSearch.add(new Filter("int", "bellOrderItems.filled", "Order Item: Shipped"));
        moreSearch.add(new Filter("string", "bellOrderItems.vendorpo", "Order Item: Vendor PO"));
        listTable.setAdditionalSearch(moreSearch);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("string", "orderId"));
        filters.add(new Filter("string", "category"));
        filters.add(new Filter("date", "orderDate"));
        filters.add(new Filter("string", "paymentsStatus"));
        filters.add(new Filter("string", "orderItemId"));
        filters.add(new Filter("date", "paymentsDate"));
        filters.add(new Filter("string", "paymentsTransactionId"));
        filters.add(new Filter("string", "itemName"));
        filters.add(new Filter("string", "sku"));
        filters.add(new Filter("float", "price"));
        filters.add(new Filter("float", "shippingFee"));
        filters.add(new Filter("integer", "quantityPurchased"));
        filters.add(new Filter("date", "purchaseDate"));
        filters.add(new Filter("string", "batchId"));
        filters.add(new Filter("string", "buyerEmail"));
        filters.add(new Filter("string", "buyerName"));
        filters.add(new Filter("string", "recipientName"));
        filters.add(new Filter("string", "location"));
        filters.add(new Filter("string", "shipAddress1"));
        filters.add(new Filter("string", "shipAddress2"));
        filters.add(new Filter("string", "shipCity"));
        filters.add(new Filter("string", "shipState"));
        filters.add(new Filter("string", "shipZip"));
        filters.add(new Filter("string", "shipCountry"));
        filters.add(new Filter("string", "specialComments"));
        filters.add(new Filter("string", "upc"));
        filters.add(new Filter("string", "shipMethod"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBellOrderAdmin()) {
            buttons.add(new ToolbarButton("Create", "createOrderButtonClick", "create_icon", "Create A New Order"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Order").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Order").setSingleRowAction(true));
            //buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Orders History Of Changes").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Orders Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Items", "quickViewButtonClick", "view_icon", "View The Selected Orders Items In A Popup").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherOrderExport");
        setExcelExportSheetName("Orders");
        
    }

    protected void setupItemListTable(BellOrder order){
        setupItemListTable(order, true);
    }
    protected void setupItemListTable(BellOrder order, Boolean showButtons){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        listTable.setPageSize(250);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("bellOrder.invoiceNumber", "bellOrder_invoiceNumber"));
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("bellInventory.id", "bellInventory_id"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("filled").setType("int"));
        //cd.add(new ColumnData("allFilled"));
        //cd.add(new ColumnData("currentAllowed").setType("int"));
        cd.add(new ColumnData("price").setType("float"));
        //cd.add(new ColumnData("cost"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("extended").setType("float"));
        //cd.add(new ColumnData("totalExtended"));
        cd.add(new ColumnData("vendorpo"));
        cd.add(new ColumnData("bellInventory.publisher", "bellInventory_publisher"));
        cd.add(new ColumnData("bellInventory.cover", "bellInventory_cover"));
        cd.add(new ColumnData("bellInventory.listPrice", "bellInventory_listPrice").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("bellOrder_invoiceNumber", "Invoice Number", 50).setHidden(!getExportToExcel()));
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("filled", "Shipped", 80));
        //cm.add(new ColumnModel("allFilled", "All Filled", 80).setRenderer("booleanRenderer").setSortable(false));
        //cm.add(new ColumnModel("currentAllowed", "Allowed", 80).setSortable(false));
        cm.add(new ColumnModel("bellInventory_listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("price", "Price", 90).setRenderer("moneyRendererRedBoldZero"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("percentRenderer"));
        cm.add(new ColumnModel("extended", "Extended Price", 100).setRenderer("moneyRenderer"));
        //cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer").setHidden(true));
        //cm.add(new ColumnModel("totalExtended", "Total Cost", 90).setRenderer("moneyRenderer").setHidden(true));
        cm.add(new ColumnModel("vendorpo", "Vendor PO", 100));
        cm.add(new ColumnModel("bellInventory_cover", "Cover", 90));
        cm.add(new ColumnModel("bellInventory_publisher", "Publisher", 120));
        listTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "filled"));
        filters.add(new Filter("integer", "currentAllowed"));
        filters.add(new Filter("float", "price"));
        //filters.add(new Filter("float", "cost"));
        //filters.add(new Filter("float", "totalExtended"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("float", "totalPrice"));
        filters.add(new Filter("float", "bellInventory_listPrice"));
        filters.add(new Filter("string", "vendorpo"));
        filters.add(new Filter("string", "bellInventory_publisher"));
        filters.add(new Filter("string", "bellInventory_cover"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (showButtons){
            if (order != null && !order.getPosted()){
                if (getIsBellOrderAdmin()){
                    listTable.setMultiselect(true);
                    buttons.add(new ToolbarButton("Create", "createItemButtonClick", "create_icon", "Create A New Order Item"));
                    buttons.add(new ToolbarButton("Edit", "editItemButtonClick", "edit_icon", "Edit The Selected Order Item").setSingleRowAction(true));
                    buttons.add(new ToolbarButton("Delete", "deleteItemButtonClick", "delete_icon", "Delete The Selected Order Item").setRowAction(true));
                    //buttons.add(new ToolbarButton("History", "itemHistoryButtonClick", "calendar_icon", "View The Selected Order Items History Of Changes").setSingleRowAction(true));
                    //buttons.add(new ToolbarButton("Set Shipped", "editItemShippedButtonClick", "edit_icon", "Set The Shipped Quantity For This Item").setSingleRowAction(true));
                }
            }
            buttons.add(new ToolbarButton("View Inv", "viewInvItemButtonClick", "view_icon", "View Inventory Item").setSingleRowAction(true));
            buttons.add(new ToolbarButton("View Inv New Window", "viewInvItemNewWindowButtonClick", "view_icon", "View Inventory Item New Window").setSingleRowAction(true));
            if (order != null && !order.getPosted()){
                if (getIsBellOrderAdmin()){
                    buttons.add(new ToolbarButton().setRight(true));
                    buttons.add(new ToolbarButton("Import", "importItemButtonClick", "down_arrow_icon", "Import Order Items From Excel"));
                    //buttons.add(new ToolbarButton("Fix All Zero Prices", "fixZeroButtonClick", "money_icon", "Fix All Zero Prices"));
                    buttons.add(new ToolbarButton("Ship Max", "shipMaxButtonClick", "lorry_icon", "Ship Current Max For Items"));
                    buttons.add(new ToolbarButton().setSeparator(true));
                }
            }
        }
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherOrderExport");
        setExcelExportSheetName("Order");
        
    }
    
    private void setupWithItemListTable(){
        listTable = new Table();
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        
        cd.add(new ColumnData("customerOrder.invoiceNumber", "customerOrder_invoiceNumber"));
        cd.add(new ColumnData("customerOrder.salesman", "customerOrder_salesman"));
        cd.add(new ColumnData("customerOrder.customer.companyName", "customerOrder_customer_companyName"));
        cd.add(new ColumnData("customerOrder.customerCode", "customerOrder_customerCode"));
        cd.add(new ColumnData("customerOrder.poNumber", "customerOrder_poNumber"));
        cd.add(new ColumnData("customerOrder.status", "customerOrder_status"));
        cd.add(new ColumnData("customerOrder.orderDate", "customerOrder_orderDate").setType("date"));
        cd.add(new ColumnData("customerOrder.shipDate", "customerOrder_shipDate").setType("date"));
        cd.add(new ColumnData("customerOrder.postDate", "customerOrder_postDate").setType("date"));
        cd.add(new ColumnData("customerOrder.totalNonShippedQuantity", "customerOrder_totalNonShippedQuantity"));
        cd.add(new ColumnData("customerOrder.totalQuantity", "customerOrder_totalQuantity").setType("int"));
        cd.add(new ColumnData("customerOrder.totalPrice", "customerOrder_totalPrice").setType("float"));
        cd.add(new ColumnData("customerOrder.totalExtended", "customerOrder_totalExtended").setType("float"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("inventoryItem.author", "inventoryItem_author"));
        cd.add(new ColumnData("inventoryItem.publisher", "inventoryItem_publisher"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("vendorpo"));
        cd.add(new ColumnData("inventoryItem.cover", "inventoryItem_cover"));
        cd.add(new ColumnData("inventoryItem.bccategory", "inventoryItem_bccategory"));
        cd.add(new ColumnData("inventoryItem.onhand", "inventoryItem_onhand").setType("int"));
        cd.add(new ColumnData("inventoryItem.committed", "inventoryItem_committed").setType("int"));
        cd.add(new ColumnData("inventoryItem.available", "inventoryItem_available").setType("int"));
        cd.add(new ColumnData("inventoryItem.listPrice", "inventoryItem_listPrice").setType("float"));
        cd.add(new ColumnData("price").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("filled").setType("int"));
        cd.add(new ColumnData("discount").setType("int"));
        cd.add(new ColumnData("totalExtended").setType("float"));
        cd.add(new ColumnData("totalPrice").setType("float"));
        cd.add(new ColumnData("inventoryItem.skidPiecePrice", "inventoryItem_skidPiecePrice").setType("float"));
        cd.add(new ColumnData("inventoryItem.skidPieceCost", "inventoryItem_skidPieceCost").setType("float"));
        cd.add(new ColumnData("inventoryItem.skidPieceCount", "inventoryItem_skidPieceCount").setType("int"));
        
        /*
        cd.add(new ColumnData("customerOrder.createTime"));
        cd.add(new ColumnData("customerOrder.transno"));
        cd.add(new ColumnData("customerOrder.totalItems"));
        cd.add(new ColumnData("customerOrder.discount"));
        cd.add(new ColumnData("customerOrder.palleteCharge"));
        cd.add(new ColumnData("customerOrder.shippingCharges"));
        cd.add(new ColumnData("customerOrder.postedBy"));
        cd.add(new ColumnData("customerOrder.postedByDate"));
        cd.add(new ColumnData("customerOrder.terms"));
        cd.add(new ColumnData("customerOrder.creditMemo"));
        cd.add(new ColumnData("customerOrder.posted"));
        cd.add(new ColumnData("customerOrder.customerVisit"));
        cd.add(new ColumnData("customerOrder.depositAmmount"));
        cd.add(new ColumnData("customerOrder.shipVia"));
        cd.add(new ColumnData("customerOrder.picker1"));
        cd.add(new ColumnData("customerOrder.picker2"));
        cd.add(new ColumnData("customerOrder.qualityControl"));
        */
        
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        
        cm.add(new ColumnModel("customerOrder_invoiceNumber", "Invoice", 75));
        cm.add(new ColumnModel("customerOrder_salesman", "Salesman", 100));
        cm.add(new ColumnModel("customerOrder_customer_companyName", "Customer Name", 100));
        cm.add(new ColumnModel("customerOrder_customerCode", "Customer Code", 100));
        cm.add(new ColumnModel("customerOrder_poNumber", "PO", 150));
        cm.add(new ColumnModel("customerOrder_status", "Status", 100));
        cm.add(new ColumnModel("customerOrder_orderDate", "Order Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_shipDate", "Ship Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("customerOrder_totalNonShippedQuantity", "Shipped", 100));
        cm.add(new ColumnModel("customerOrder_totalPrice", "Total Extended Price", 100));
        cm.add(new ColumnModel("customerOrder_totalExtended", "Total Extended Cost", 100));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("cond", "Condition", 50).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("inventoryItem_author", "Author", 100));
        cm.add(new ColumnModel("inventoryItem_publisher", "Publisher", 100));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("vendorpo", "Vendor PO", 100));
        cm.add(new ColumnModel("inventoryItem_cover", "Cover", 90));
        cm.add(new ColumnModel("inventoryItem_bccategory", "Category", 90));
        cm.add(new ColumnModel("inventoryItem_onhand", "On Hand", 90));
        cm.add(new ColumnModel("inventoryItem_committed", "Committed", 90));
        cm.add(new ColumnModel("inventoryItem_available", "Available", 90));
        cm.add(new ColumnModel("inventoryItem_listPrice", "List Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("price", "Price", 90).setRenderer("moneyRendererRedBoldZero"));
        cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("filled", "Shipped", 80));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("totalExtended", "Extended Cost", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("totalPrice", "Extended Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("inventoryItem_skidPieceCount", "Piece Count", 90));
        cm.add(new ColumnModel("inventoryItem_skidPiecePrice", "Piece Price", 90));
        cm.add(new ColumnModel("inventoryItem_skidPieceCost", "Piece Cost", 90));
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("customerOrdercreateTime");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryOrdersWithItemsExport");
        setExcelExportSheetName("Orders");
        
    }
    
    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }
    
    public BellOrder getOrder() {
        return order;
    }

    public void setOrder(BellOrder order) {
        this.order = order;
    }

    public List<BellCustomer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<BellCustomer> customers) {
        this.customers = customers;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerShippingId() {
        return customerShippingId;
    }

    public void setCustomerShippingId(Long customerShippingId) {
        this.customerShippingId = customerShippingId;
    }

    public String getOrderDateString() {
        return orderDateString;
    }

    public void setOrderDateString(String orderDateString) {
        this.orderDateString = orderDateString;
    }

    public String getPostDateString() {
        return postDateString;
    }

    public void setPostDateString(String postDateString) {
        this.postDateString = postDateString;
    }

    public String getShipDateString() {
        return shipDateString;
    }

    public void setShipDateString(String shipDateString) {
        this.shipDateString = shipDateString;
    }

    public String getQuickSearch() {
        return quickSearch;
    }

    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }

    public Boolean getIsQuickSearch() {
        return isQuickSearch;
    }

    public void setIsQuickSearch(Boolean isQuickSearch) {
        this.isQuickSearch = isQuickSearch;
    }

    public String getQuickSearchLocation() {
        return quickSearchLocation;
    }

    public void setQuickSearchLocation(String quickSearchLocation) {
        this.quickSearchLocation = quickSearchLocation;
    }

}
