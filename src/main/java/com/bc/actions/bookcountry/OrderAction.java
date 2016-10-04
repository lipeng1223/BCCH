package com.bc.actions.bookcountry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import com.bc.actions.BaseAction;
import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.CustomerShippingSessionLocal;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.CustomerShipping;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import com.bc.util.Timing;
import com.bc.util.cache.CustomerCache;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringEscapeUtils;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/orders/list.jsp"),
    @Result(name="searchlist", location="/WEB-INF/jsp/bookcountry/orders/search.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bookcountry/orders/searchwindow.jsp"),    
    @Result(name="multisearchlist", location="/WEB-INF/jsp/bookcountry/orders/multisearch.jsp"),
    @Result(name="bestsellerlist", location="/WEB-INF/jsp/bookcountry/orders/bestseller.jsp"),
    @Result(name="view", location="/WEB-INF/jsp/bookcountry/orders/view.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/orders/detail.jsp"),
    @Result(name="quickitems", location="/WEB-INF/jsp/bookcountry/orders/quickitems.jsp"),
    @Result(name="viewdetail", location="/WEB-INF/jsp/bookcountry/orders/viewdetail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/orders/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class OrderAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(OrderAction.class);

    private final Float TAX = 1.07F;
    
    private CustomerOrder order;
    protected Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    private List<Customer> customers;
    
    private Long customerId;
    private Long customerShippingId;
    private String orderDateString;
    private String postDateString;
    private String shipDateString;
    
    
    private Integer customerIndex = 0;
    
    private Table orderListTable;
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String create(){
        customers = CustomerCache.getCustomers();
        return "crud";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String createSubmit(){
        if (orderDateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                order.setOrderDate(sdf.parse(orderDateString));
            } catch (Exception e){
                order.setOrderDate(Calendar.getInstance().getTime());
                logger.error("invalid order date: "+orderDateString);
            }
        }
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            CustomerShippingSessionLocal customerShippingSession = getCustomerShippingSession();
            if (customerId != null && customerId > -1L){
                Customer customer = customerSession.findById(customerId);
                if (customer.getTax()) order.setTax(TAX);
                order.setCustomer(customer);
                order.setCustomerCode(customer.getCode());
                order.setTerms(customer.getTerms());
                customer.setLastSalesDate(order.getOrderDate());
                customerSession.update(customer);
            }
            if (customerShippingId != null && customerShippingId > -1L){
                order.setCustomerShipping(customerShippingSession.findById(customerShippingId));
            }
            
            if (order.getShippingCharges() == null) order.setShippingCharges(0F);
            if (order.getPalleteCharge() == null) order.setPalleteCharge(0F);
            if (order.getTax() == null) order.setTax(1F);
            if (order.getDepositAmmount() == null) order.setDepositAmmount(0F);
            
            OrderSessionLocal oSession = getOrderSession();
            oSession.create(order);
            id = order.getId();
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not create the order, there was a system error.");
            logger.error("Could not create order", e);
        }
        return "status";
    }

    @ActionRole({"BcOrderAdmin"})
    public String post(){
        try {
            logger.info("post order id: "+id);
            OrderSessionLocal oSession = getOrderSession();
            CustomerOrder order = oSession.findById(id, "customer");
            if (order.getCustomer().getEmailInvoice()){
                logger.info("This customer wants to get invoice by email.");
                this.setResult("Email");
            }
            
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
                    iiIds = lifo.postOrder(id, ServletActionContext.getRequest().getUserPrincipal().getName(), postDate);
                    done = true;
                } catch (Exception e){
                    if (e.getMessage().startsWith("Cost is not set")){
                        setSuccess(false);
                        setMessage(e.getMessage());
                        return "status";
                    } else if (e.getMessage().startsWith("Price is not set")){
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
                    InventoryItemSessionLocal iiSession = getInventoryItemSession();
                    for (Long iiId : iiIds){
                        iiSession.recalculateCommitted(iiId);
                    }
                    done = true;
                } catch (Exception e){
                    logger.error("Could not recalc committed for inventory item, retrying: "+e.getMessage());
                }
            }
                
            t2.stop();
            t.stop();
            
            getOrderSession().recalculateOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not post order", e);
            setSuccess(false);
            setMessage("Could not post the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String shipDate(){
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date shipDate = sdf.parse(shipDateString);
            
            OrderSessionLocal oSession = getOrderSession();
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

    @ActionRole({"BcOrderAdmin"})
    public String shipMax(){
        try {
            
            OrderSessionLocal oSession = getOrderSession();
            oSession.shipMax(id);
            logger.info("recalculating order totals...");
            oSession.recalculateAllOrderTotals(id);
            logger.info("finished recalculating order totals");
            setSuccess(true);
            logger.info("success ship Max");
        } catch (Exception e){
            logger.error("Could not ship max", e);
            setSuccess(false);
            setMessage("Could not ship current max, there was a system error.");
        }
        logger.info("returning from shipMax...");
        return "status";
    }
    

    @ActionRole({"BcOrderAdmin"})
    public String fixZeroPrice(){
        try {
            
            OrderSessionLocal oSession = getOrderSession();
            oSession.fixZeroPrice(id);
            oSession.recalculateAllOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not fix zero prices", e);
            setSuccess(false);
            setMessage("Could not fix zero prices, there was a system error.");
        }
        return "status";
    }
    
    
    @ActionRole({"BcOrderAdmin"})
    public String edit(){
        customers = CustomerCache.getCustomers();
        logger.info("customer set!");
        OrderSessionLocal oSession = getOrderSession();
        order = oSession.findById(id, "customer", "customer.customerShippings");
//        if (order.getComment2().trim().length() == 0)
//            order.setComment2(order.getCustomer().getPicklistComment());
        return "crud";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String editSubmit(){
        if (orderDateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                order.setOrderDate(sdf.parse(orderDateString));
            } catch (Exception e){
                logger.error("invalid order date: "+orderDateString);
            }
        }
        try {
            OrderSessionLocal oSession = getOrderSession();
            CustomerOrder dbco = oSession.findById(order.getId());
            CustomerSessionLocal customerSession = getCustomerSession();
            CustomerShippingSessionLocal customerShippingSession = getCustomerShippingSession();
            Customer customer = null;
            CustomerShipping customerShipping = null;
            if (customerId != null && customerId > -1L){
                customer = customerSession.findById(customerId);
            }
            if (customerShippingId != null && customerShippingId > -1L){
                customerShipping = customerShippingSession.findById(customerShippingId);
            }
            dbco.setTax(1F);
            if (customer != null){
                dbco.setCustomer(customer);
                dbco.setCustomerCode(customer.getCode());
                dbco.setDiscount(customer.getDiscount());
                if (customer.getTax()) dbco.setTax(TAX);
                dbco.setTerms(customer.getTerms());
                
                customer.setLastSalesDate(order.getOrderDate());
//                if (order.getComment2().trim().length() > 0 && customer.getPicklistComment() != order.getComment2())
//                    customer.setPicklistComment(order.getComment2());
                customerSession.update(customer);
            }
            dbco.setCustomerShipping(customerShipping);
            
            dbco.setComment(order.getComment());
            dbco.setComment2(order.getComment2());
            // TODO credit
            //dbco.setCreditMemo(creditMemo)
            dbco.setCustomerVisit(order.getCustomerVisit());
            dbco.setCreditMemoType(order.getCreditMemoType());
            dbco.setDepositAmmount(order.getDepositAmmount());
            //dbco.setDiscount(order.getDiscount());
            dbco.setOrderDate(order.getOrderDate());
            dbco.setPalleteCharge(order.getPalleteCharge());
            dbco.setPicker1(order.getPicker1());
            dbco.setPicker2(order.getPicker2());
            dbco.setPoNumber(order.getPoNumber());
            dbco.setQualityControl(order.getQualityControl());
            dbco.setSalesman(order.getSalesman());
            dbco.setShippingCharges(order.getShippingCharges());
            dbco.setShipVia(order.getShipVia());
            dbco.setStatus(order.getStatus());
            
            if (dbco.getShippingCharges() == null) dbco.setShippingCharges(0F);
            if (dbco.getPalleteCharge() == null) dbco.setPalleteCharge(0F);
            if (dbco.getTax() == null) dbco.setTax(1F);
            if (dbco.getDepositAmmount() == null) dbco.setDepositAmmount(0F);
            
            oSession.update(dbco);
            oSession.recalculateAllOrderTotals(dbco.getId());
            
            id = dbco.getId();
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get update the order", e);
            setSuccess(false);
            setMessage("Could not update the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String delete(){
        try {
            OrderSessionLocal oSession = getOrderSession();
            order = oSession.findById(id);
            if (order == null){
                setSuccess(false);
                setMessage("Could not delete the order, the order no longer exists in the system.");
            }
            logger.info("deleting order id: "+id);
            List<Long> inventoryItemIds = oSession.getAllInventoryItemIds(id);
            oSession.delete(id);
            logger.info("deleted order id: "+id);
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            logger.info("Recalculating all of the inventory items committed...");
            for (Long iiId : inventoryItemIds){
                iiSession.recalculateCommitted(iiId);
            }
            logger.info("Finished order delete.");
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get delete the order", e);
            setSuccess(false);
            setMessage("Could not delete the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String unpost(){
        try {
            OrderSessionLocal oSession = getOrderSession();
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            //BaseDao<CustomerOrder> coDao = new BaseDao<CustomerOrder>(CustomerOrder.class);
            logger.info("unposting order id: "+id);
            order = oSession.findById(id, "customerOrderItems");
            //order = coDao.findById(id, "customerOrderItems");
            if (order == null){
                setSuccess(false);
                setMessage("Could not unpost the order, the order no longer exists in the system.");
            }
            order.setPosted(false);
            oSession.update(order);
            logger.info("unposted order id: "+id);
            
            LifoSessionLocal lifo = getLifoSession();
            lifo.unpostOrder(id);
            
            for (CustomerOrderItem coi : order.getCustomerOrderItems()){
                InventoryItem ii = coi.getInventoryItem(); 
                if (ii == null){
                    continue;
                }
                if (order.getDebitMemo() != null && order.getDebitMemo() && order.getDebitMemoType().equals("recNoInv")){
                    iiSession.recalculateCommitted(ii.getId());
                } else if (order.getCreditMemo()){
                    if (order.getCreditMemoType().equals("shortage") || order.getCreditMemoType().equals("recNoBill")){
                        iiSession.recalculateCommitted(ii.getId());
                    }
                } else{
                    iiSession.recalculateCommitted(ii.getId());
                }
            }
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get unpost the order", e);
            setSuccess(false);
            setMessage("Could not unpost the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String listData(){
        if (exportWithItemsToExcel){
            try {
                OrderSessionLocal oSession = getOrderSession();
                if (queryInput == null || queryInput.getFilterParams() == null)
                    queryInput = new QueryInput();
                queryInput.setStart(0);
                int limit = 250;
                // grabbing orders 100 at a time
                queryInput.setLimit(limit);
                setupListTable();
                setupSearchNames();
                if (search != null){
                    List<Criterion> crits = search.getRestrictions(listTable);
                    for (Criterion crit : crits){
                        //logger.info("adding crit "+crit.toString());
                        queryInput.addAndCriterion(crit);
                    }
                }
                if (quickSearch != null){
                    Disjunction dis = Restrictions.disjunction();
                    dis.add(Restrictions.ilike("poNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("invoiceNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("comment", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("salesman", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("customerCode", quickSearch, MatchMode.ANYWHERE));
                    queryInput.addAndCriterion(dis);
                }
                logger.info("Start getting order oids...");
                HashMap<String, String> aliases = new HashMap<String, String>();
                aliases.put("customerOrderItems", "customerOrderItems");
                List<Long> oids = new ArrayList<Long>();
                long total = 0;
                int loop = 0;
                boolean keepLooping = true;
                while (keepLooping){
                    queryInput.setStart(loop * limit);
                    loop++;
                    DaoResults orderResults = oSession.findAll(queryInput, aliases, "customer");
                    //boolean breakout = false;
                    for (CustomerOrder co : (List<CustomerOrder>)orderResults.getData()){
                        oids.add(co.getId());
                        //total += co.getTotalItems();
                        //if (total >= ExcelConstants.EXCEL_MAX_ROW) {
                        //    breakout = true;
                        //    break;
                        //}
                    }
                    //if (breakout) break;
                    //logger.info("loop start: "+queryInput.getStart()+" data size: "+orderResults.getDataSize()+" total items: "+total);
                    keepLooping = orderResults.getDataSize() == limit;
                }
                /*
                if (total >= ExcelConstants.EXCEL_MAX_ROW) {
                    // break out and return
                    logger.error("Could not export orders with items, went over excel row limit");
                    queryResults = new QueryResults(new DaoResults());
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                    exportLimitExceeded = true;
                    return "queryresults";
                }
                */
                
                setupWithItemListTable();
                logger.info("Finished getting order oids.  oids size: "+oids.size()+" total items: "+total);
                
                queryInput = new QueryInput();
                queryInput.setStart(0);
                limit = 500;
                queryInput.setLimit(limit);
                CustomerSessionLocal cSession = getCustomerSession();
                if (customerId != null){
                    Customer cust = cSession.findById(customerId);
                    queryInput.addAndCriterion(Restrictions.eq("customerOrder.customer", cust));
                }
                if (oids.size() > 0)
                    queryInput.addAndCriterion(Restrictions.in("customerOrder.id", oids));
                else {
                    logger.error("No oids loaded, not going to try and export all order items!");
                    queryResults = new QueryResults(new DaoResults());
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                    exportLimitExceeded = true;
                    return "queryresults";
                }
                // add any additional order item specific criteria
                if (search != null){
                    List<Criterion> crits = search.getRestrictions(listTable, "customerOrderItems.");
                    for (Criterion crit : crits){
                        queryInput.addAndCriterion(crit);
                    }
                }
                //long start = System.currentTimeMillis();
                queryInput.setExportToExcel(true);
                aliases = new HashMap<String, String>();
                aliases.put("customerOrder", "customerOrder");
                
                DaoResults rolledUp = new DaoResults(new ArrayList());
                loop = 0;
                keepLooping = true;
                while (keepLooping){
                    queryInput.setStart(loop * limit);
                    loop++;
                    DaoResults orderResults = oSession.findAllItems(queryInput, aliases, "customerOrder", "customerOrder.customer", "inventoryItem");
                    rolledUp.getData().addAll(orderResults.getData());
                    keepLooping = orderResults.getDataSize() == limit;
                }
                queryResults = new QueryResults(rolledUp);
                
                logger.info("total: "+queryResults.getTotalRecords());
                queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                //long end = System.currentTimeMillis();
                //logger.info("timing: "+((end-start) / 1000.0));
            } catch (Throwable t){
                logger.error("Could not listItemData", t);
            }            
        } else {
            setupListTable();
            try {
                OrderSessionLocal oSession = getOrderSession();
                HashMap<String, String> aliases = new HashMap<String, String>();
                if (quickSearch != null){
                    //aliases.put("customerOrderItems", "customerOrderItems");
                    Disjunction dis = Restrictions.disjunction();
                    dis.add(Restrictions.ilike("poNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("invoiceNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("comment", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("salesman", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("customerCode", quickSearch, MatchMode.ANYWHERE));
                    //dis.add(Restrictions.ilike("customerOrderItems.isbn", quickSearch, MatchMode.ANYWHERE));
                    //dis.add(Restrictions.ilike("customerOrderItems.title", quickSearch, MatchMode.ANYWHERE));
                    //dis.add(Restrictions.ilike("customerOrderItems.bin", quickSearch, MatchMode.ANYWHERE));
                    queryInput.addAndCriterion(dis);
                }
                if (aliases.size() > 0)
                    queryResults = new QueryResults(oSession.findAll(queryInput, aliases, "customer"));
                else
                    queryResults = new QueryResults(oSession.findAll(queryInput, "customer"));
                queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            } catch (Exception e){
                logger.error("Could not listData", e);
            }
        }
        return "queryresults";
    }

    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String view(){
        try {
            OrderSessionLocal oSession = getOrderSession();
            order = oSession.findById(id, "customer");
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
                searchNames.put(f.getName().replace("_", "."), cm.getHeader());
            }
        }
        if (listTable.getAdditionalSearch() != null){
            // additional order items search
            for(Filter f : listTable.getAdditionalSearch()) {
                searchNames.put(f.getName().replace("_", "."), f.getDisplay());
            }
        }
    }

    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String searchData(){
        //logger.info("start");
        setupListTable(); 
        setupSearchNames();
        try {
            OrderSessionLocal oSession = getOrderSession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            HashMap<String, String> aliases = new HashMap<String, String>();
            aliases.put("customerOrderItems", "customerOrderItems");
            aliases.put("customer", "customer");
            queryResults = new QueryResults(oSession.findAll(queryInput, aliases, "customer"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        //logger.info("end");
        return "queryresults";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String multiSearch(){
        setupListTable();
        return "multisearchlist";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String bestSeller(){
        setupSaleDataListTable();
        return "bestsellerlist";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String multiSearchData(){
        setupListTable(); 
        setupSearchNames();
        try {
            OrderSessionLocal oSession = getOrderSession();
            if (search != null){
                if (search.getMultiIsbn() != null && search.getMultiIsbn().length() > 0){
                    //logger.info("search multiIsbn: "+search.getMultiIsbn());
//                    Disjunction dis = Restrictions.disjunction();
                    int cnt = 0;
                    StringTokenizer st = new StringTokenizer(search.getMultiIsbn(), ";"); 
                    String isbn = "(";
                    while (st.hasMoreTokens()){
                        String token = st.nextToken();
//                        dis.add(Restrictions.eq("inventoryItem.isbn", token));
                        if (IsbnUtil.isValid(token)){
//                            dis.add(Restrictions.eq("inventoryItem.isbn", IsbnUtil.getIsbn10(token)));
                            isbn += "'" + IsbnUtil.getIsbn10(token) + "'";
                            if (st.hasMoreTokens()){
                                isbn += ",";
                            }
                            cnt++;
//                            dis.add(Restrictions.eq("inventoryItem.isbn13", IsbnUtil.getIsbn13(token)));
                            
                        }
                    }
                    isbn += ")";
                    List<Long> oids = oSession.getAllOrderIds(isbn, cnt, search.getIncludeBell(), search.getIncludeRestricted(), search.getIncludeHigherEducation());
//                    queryInput.addAndCriterion(dis);
//                    if (search.getIncludeBell() == null || !search.getIncludeBell()){
//                        queryInput.addAndCriterion(Restrictions.eq("inventoryItem.bellbook", false));
//                    }
//                    if (search.getIncludeRestricted() == null || !search.getIncludeRestricted()){
//                        queryInput.addAndCriterion(Restrictions.eq("inventoryItem.restricted", false));
//                    }
//                    if (search.getIncludeHigherEducation() == null || !search.getIncludeHigherEducation()){
//                        queryInput.addAndCriterion(Restrictions.eq("inventoryItem.he", false));
//                    }
//                    
                    //queryInput.addAndCriterion(Restrictions.ge("available", 0));
                    
//                    List<CustomerOrderItem> items = oSession.findAllItems(queryInput, "customerOrder", "inventoryItem").getData();
//                    Long[] ids = new Long[items.size()];
//                    for (int i = 0; i < items.size(); i++){
//                        CustomerOrderItem coi = items.get(i);
//                        ids[i] = coi.getCustomerOrder().getId();
//                    }
                    queryInput = new QueryInput();
                    if (oids.size() > 0){
                        queryInput.addAndCriterion(Restrictions.in("id", oids));
                    }
                    else{
                        queryInput.addAndCriterion(Restrictions.eq("id", Long.MIN_VALUE));
                    }
                    queryResults = new QueryResults(oSession.findAll(queryInput, "customerOrderItems", "inventoryItem", "customer"));
                } else{
                    queryResults = new QueryResults(oSession.findAll(queryInput, "customerOrderItems", "inventoryItem", "customer"));
                    
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                }
            }
            
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list multi search data", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String saleSearchData(){
        setupSaleDataListTable();
        
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            List<InventoryItem> items = inventoryItemSession.findByIsbn(search.getIsbn());
            OrderSessionLocal oSession = getOrderSession();
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.in("inventoryItem", items));
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            if (search.getDateFrom() != null && search.getDateFrom().length() > 0){
                Date df = sdf.parse(search.getDateFrom());
                queryInput.addAndCriterion(Restrictions.gt("customerOrder.orderDate", df));
                logger.info("Date From :" + search.getDateFrom());
                logger.info("Date >= " + sdft.format(df));
            }
            if (search.getDateTo() != null && search.getDateTo().length() > 0){
                Date dt = sdf.parse(search.getDateTo());
                queryInput.addAndCriterion(Restrictions.lt("customerOrder.orderDate", dt));
                logger.info("Date To:" + search.getDateTo());
                logger.info("Date <= " + sdft.format(dt));
            }
            if (search.getMinQty() != null && search.getMinQty().length() > 0){
                int mq = Integer.parseInt(search.getMinQty());
                queryInput.addAndCriterion(Restrictions.ge("quantity", mq));
                logger.info("Quantity >= " + mq);
            }
            if (search.getMaxQty() != null && search.getMaxQty().length() > 0){
                int mq = Integer.parseInt(search.getMaxQty());
                queryInput.addAndCriterion(Restrictions.le("quantity", mq));
                logger.info("Quantity <= " + mq);
            }
            
            HashMap aliases;
            aliases = new HashMap<String, String>();
            aliases.put("customerOrder", "customerOrder");
                
            queryResults = new QueryResults(oSession.findAllItems(queryInput, aliases, "customerOrder"));
            queryResults.setTableConfig(orderListTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listOrders", e);
        }
        
        return "queryresults";
    }

    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String quickItems(){
        setupItemListTable(null, false);
        return "quickitems";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String listItemData(){
        try {
            OrderSessionLocal oSession = getOrderSession();
            order = oSession.findById(id);
            setupItemListTable(order); 
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("customerOrder", order));
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "inventoryItem", "customerOrder"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listItemData", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String detail(){
        OrderSessionLocal oSession = getOrderSession();
        order = oSession.findById(id, "customer", "customerShipping");
        return "detail";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String viewDetail(){
        OrderSessionLocal oSession = getOrderSession();
        order = oSession.findById(id, "customer", "customerShipping");
        return "viewdetail";
    }
    
    protected void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("status"));
        cd.add(new ColumnData("poNumber"));
        cd.add(new ColumnData("invoiceNumber"));
        cd.add(new ColumnData("shipDate").setType("date"));
        cd.add(new ColumnData("orderDate").setType("date"));
        cd.add(new ColumnData("customerCode"));
        cd.add(new ColumnData("transno"));
        cd.add(new ColumnData("totalItems").setType("int"));
        cd.add(new ColumnData("totalPrice").setType("float"));
        cd.add(new ColumnData("totalTax").setType("float"));
        cd.add(new ColumnData("totalPricePreTax").setType("float"));
        cd.add(new ColumnData("balanceDue").setType("float"));
        //cd.add(new ColumnData("totalExtended"));
        cd.add(new ColumnData("totalQuantity").setType("int"));
        cd.add(new ColumnData("totalNonShippedQuantity").setType("int"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("palleteCharge").setType("float"));
        cd.add(new ColumnData("shippingCharges").setType("float"));
        cd.add(new ColumnData("postDate").setType("date"));
        cd.add(new ColumnData("postedBy"));
        cd.add(new ColumnData("postedByDate").setType("date"));
        cd.add(new ColumnData("terms"));
        cd.add(new ColumnData("salesman"));
        cd.add(new ColumnData("customer.companyName", "customer_companyName"));
        cd.add(new ColumnData("creditMemo").setType("boolean"));
        cd.add(new ColumnData("posted").setType("boolean"));
        cd.add(new ColumnData("shipped").setType("boolean"));
        cd.add(new ColumnData("customerVisit").setType("boolean"));
        cd.add(new ColumnData("depositAmmount").setType("float"));
        cd.add(new ColumnData("shipVia"));
        cd.add(new ColumnData("picker1"));
        cd.add(new ColumnData("picker2"));
        cd.add(new ColumnData("qualityControl"));
        cd.add(new ColumnData("comment"));
        cd.add(new ColumnData("comment2"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("status", "Status", 100));
        cm.add(new ColumnModel("posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("poNumber", "PO", 150));
        cm.add(new ColumnModel("invoiceNumber", "Invoice", 75));
        cm.add(new ColumnModel("shipDate", "Ship Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("orderDate", "Order Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerCode", "Customer Code", 100));
        cm.add(new ColumnModel("transno", "Trans No", 100));
        cm.add(new ColumnModel("totalItems", "Total Items", 100));
        cm.add(new ColumnModel("totalPricePreTax", "Total Extended Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("totalPrice", "Total Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("balanceDue", "Balance Due", 100).setRenderer("moneyRenderer"));
        //cm.add(new ColumnModel("totalExtended", "Total Cost", 100).setRenderer("moneyRenderer").setHidden(true));
        cm.add(new ColumnModel("totalQuantity", "Shipped Quantity", 100));
        cm.add(new ColumnModel("totalNonShippedQuantity", "Ordered Qty", 100));
        cm.add(new ColumnModel("discount", "Discount", 100));
        cm.add(new ColumnModel("totalTax", "Total Tax", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("palleteCharge", "Pallete Charge", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("shippingCharges", "Shipping Charges", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("depositAmmount", "Deposit Ammount", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("postedBy", "Posted By", 100));
        cm.add(new ColumnModel("postedByDate", "Post By Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("terms", "Terms", 100));
        cm.add(new ColumnModel("salesman", "Salesman", 100));
        cm.add(new ColumnModel("customer_companyName", "Customer Name", 100));
        cm.add(new ColumnModel("creditMemo", "Credit", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("shipped", "Shipped", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("customerVisit", "Customer Visit", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("shipVia", "Ship Via", 100));
        cm.add(new ColumnModel("picker1", "Picker 1", 100));
        cm.add(new ColumnModel("picker2", "Picker 2", 100));
        cm.add(new ColumnModel("qualityControl", "Quality Control", 100));
        cm.add(new ColumnModel("comment", "Comment", 300));
        cm.add(new ColumnModel("comment2", "Comment 2", 300));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "status"));
        filters.add(new Filter("string", "poNumber"));
        filters.add(new Filter("string", "invoiceNumber"));
        filters.add(new Filter("date", "shipDate"));
        filters.add(new Filter("date", "orderDate"));
        filters.add(new Filter("string", "customerCode"));
        filters.add(new Filter("string", "transno"));
        filters.add(new Filter("integer", "totalItems"));
        filters.add(new Filter("float", "totalPrice"));
        //filters.add(new Filter("float", "totalExtended"));
        filters.add(new Filter("integer", "totalQuantity"));
        filters.add(new Filter("integer", "totalNonShippedQuantity"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("float", "palleteCharge"));
        filters.add(new Filter("float", "shippingCharges"));
        filters.add(new Filter("date", "postDate"));
        filters.add(new Filter("boolean", "posted"));
        filters.add(new Filter("string", "postedBy"));
        filters.add(new Filter("date", "postedByDate"));
        filters.add(new Filter("string", "terms"));
        filters.add(new Filter("string", "salesman"));
        filters.add(new Filter("string", "customer_companyName"));
        filters.add(new Filter("boolean", "creditMemo"));
        filters.add(new Filter("boolean", "shipped"));
        filters.add(new Filter("boolean", "customerVisit"));
        filters.add(new Filter("float", "depositAmmount"));
        filters.add(new Filter("string", "shipVia"));
        filters.add(new Filter("string", "picker1"));
        filters.add(new Filter("string", "picker2"));
        filters.add(new Filter("string", "qualityControl"));
        filters.add(new Filter("string", "comment"));
        filters.add(new Filter("string", "comment2"));
        listTable.setFilters(filters);
        
        List<Filter> moreSearch = new ArrayList<Filter>();
        moreSearch.add(new Filter("string", "customerOrderItems.isbn", "Order Item: ISBN"));
        moreSearch.add(new Filter("string", "customerOrderItems.title", "Order Item: Title"));
        moreSearch.add(new Filter("string", "customerOrderItems.bin", "Order Item: Bin"));
        moreSearch.add(new Filter("int", "customerOrderItems.quantity", "Order Item: Quantity"));
        moreSearch.add(new Filter("int", "customerOrderItems.filled", "Order Item: Shipped"));
        moreSearch.add(new Filter("string", "customerOrderItems.vendorpo", "Order Item: Vendor PO"));
        listTable.setAdditionalSearch(moreSearch);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcOrderAdmin()) {
            buttons.add(new ToolbarButton("Create", "createOrderButtonClick", "create_icon", "Create A New Order"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Order").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Order").setSingleRowAction(true));
            buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Orders History Of Changes").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Unpost", "unpostButtonClick", "table_delete_icon", "Unpost this order").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Orders Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("View In New Window", "viewNewWinButtonClick", "view_icon", "View The Selected Orders Detail Page In A New Window").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Items", "quickViewButtonClick", "view_icon", "View The Selected Orders Items In A Popup").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Items", "exportWithItemsButtonClick", "excel_icon", "Export these Orders with their order items"));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("createTime");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryOrderExport");
        setExcelExportSheetName("Orders");
        
    }

    protected void setupItemListTable(CustomerOrder order){
        setupItemListTable(order, true);
    }
    protected void setupItemListTable(CustomerOrder order, Boolean showButtons){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        listTable.setPageSize(250);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("customerOrder.invoiceNumber", "customerOrder_invoiceNumber"));
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("inventoryItem.id", "inventoryItem_id"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("inventoryItem.bin", "inventoryItem_bin"));
        if (order != null && order.getCreditMemo()){
            cd.add(new ColumnData("creditType"));
        }
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("filled").setType("int"));
        //cd.add(new ColumnData("allFilled"));
        cd.add(new ColumnData("currentAllowed").setType("int"));
        cd.add(new ColumnData("price").setType("float"));
        //cd.add(new ColumnData("cost"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("totalPrice").setType("float"));
        //cd.add(new ColumnData("totalExtended"));
        cd.add(new ColumnData("vendorpo"));
        cd.add(new ColumnData("inventoryItem.publisher", "inventoryItem_publisher"));
        cd.add(new ColumnData("inventoryItem.cover", "inventoryItem_cover"));
        cd.add(new ColumnData("inventoryItem.listPrice", "inventoryItem_listPrice").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("customerOrder_invoiceNumber", "Invoice Number", 50).setHidden(!getExportToExcel()));
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("cond", "Condition", 50).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("inventoryItem_bin", "Current Bin", 100));
        if (order != null && order.getCreditMemo()){
            cm.add(new ColumnModel("creditType", "Credit Type", 80).setRenderer("creditTypeRenderer"));
        }
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("filled", "Shipped", 80));
        //cm.add(new ColumnModel("allFilled", "All Filled", 80).setRenderer("booleanRenderer").setSortable(false));
        cm.add(new ColumnModel("currentAllowed", "Allowed", 80).setSortable(false));
        cm.add(new ColumnModel("inventoryItem_listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("price", "Price", 90).setRenderer("moneyRendererRedBoldZero"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("percentNoModRenderer"));
        cm.add(new ColumnModel("totalPrice", "Extended Price", 100).setRenderer("moneyRenderer"));
        //cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer").setHidden(true));
        //cm.add(new ColumnModel("totalExtended", "Total Cost", 90).setRenderer("moneyRenderer").setHidden(true));
        cm.add(new ColumnModel("vendorpo", "Vendor PO", 100));
        cm.add(new ColumnModel("inventoryItem_cover", "Cover", 90));
        cm.add(new ColumnModel("inventoryItem_publisher", "Publisher", 120));
        listTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "cond"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("string", "inventoryItem_bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "filled"));
        filters.add(new Filter("integer", "currentAllowed"));
        filters.add(new Filter("float", "price"));
        //filters.add(new Filter("float", "cost"));
        //filters.add(new Filter("float", "totalExtended"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("float", "totalPrice"));
        filters.add(new Filter("float", "inventoryItem_listPrice"));
        filters.add(new Filter("string", "vendorpo"));
        filters.add(new Filter("string", "inventoryItem_publisher"));
        filters.add(new Filter("string", "inventoryItem_cover"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (showButtons){
            if (order != null && !order.getPosted()){
                if (getIsBcOrderAdmin()){
                    listTable.setMultiselect(true);
                    buttons.add(new ToolbarButton("Create", "createItemButtonClick", "create_icon", "Create A New Order Item"));
                    buttons.add(new ToolbarButton("Edit", "editItemButtonClick", "edit_icon", "Edit The Selected Order Item").setSingleRowAction(true));
                    buttons.add(new ToolbarButton("Delete", "deleteItemButtonClick", "delete_icon", "Delete The Selected Order Item").setRowAction(true));
                    buttons.add(new ToolbarButton("History", "itemHistoryButtonClick", "calendar_icon", "View The Selected Order Items History Of Changes").setSingleRowAction(true));
                    buttons.add(new ToolbarButton("Set Shipped", "editItemShippedButtonClick", "edit_icon", "Set The Shipped Quantity For This Item").setSingleRowAction(true));
                }
            }
            buttons.add(new ToolbarButton("View Inv", "viewInvItemButtonClick", "view_icon", "View Inventory Item").setSingleRowAction(true));
            buttons.add(new ToolbarButton("View Inv New Window", "viewInvItemNewWindowButtonClick", "view_icon", "View Inventory Item New Window").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Print Inv New Window", "printInvItemNewWindowButtonClick", "print_icon", "Print Inventory Item New Window").setSingleRowAction(true));
            if (order != null && !order.getPosted()){
                if (getIsBcOrderAdmin()){
                    buttons.add(new ToolbarButton().setRight(true));
                    buttons.add(new ToolbarButton("Import", "importItemButtonClick", "down_arrow_icon", "Import Order Items From Excel"));
                    buttons.add(new ToolbarButton("Fix All Zero Prices", "fixZeroButtonClick", "money_icon", "Fix All Zero Prices"));
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
        setExcelExportFileName("BookcountryOrderExport");
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
        cd.add(new ColumnData("extended").setType("float"));
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
        cm.add(new ColumnModel("extended", "Extended Cost", 100).setRenderer("moneyRenderer"));
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
    
    public CustomerOrder getOrder() {
        return order;
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
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
    
    public Table getOrderListTable() {
        return orderListTable;
    }

    public void setOrderListTable(Table orderListTable) {
        this.orderListTable = orderListTable;
    }
     
    
    
    protected void setupSaleDataListTable(){
        orderListTable = new Table();
        orderListTable.setExportable(true);
        orderListTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id"));
        
        cd.add(new ColumnData("customerOrder.id", "customerOrder_id").setType("int"));
        cd.add(new ColumnData("customerOrder.poNumber", "customerOrder_poNumber"));
        cd.add(new ColumnData("customerOrder.invoiceNumber", "customerOrder_invoiceNumber"));
        cd.add(new ColumnData("customerOrder.shipDate", "customerOrder_shipDate").setType("date"));
        cd.add(new ColumnData("customerOrder.orderDate", "customerOrder_orderDate").setType("date"));
        cd.add(new ColumnData("customerOrder.customerCode", "customerOrder_customerCode"));
        cd.add(new ColumnData("customerOrder.status", "customerOrder_status"));
        cd.add(new ColumnData("customerOrder.salesman", "customerOrder_salesman"));
        cd.add(new ColumnData("customerOrder.posted", "customerOrder_posted"));
        cd.add(new ColumnData("customerOrder.postDate", "customerOrder_postDate"));
        
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("displayIsbn"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("filled").setType("int"));
        cd.add(new ColumnData("price").setType("float"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("totalPrice").setType("float"));
        cd.add(new ColumnData("vendorpo"));
        orderListTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        
        cm.add(new ColumnModel("customerOrder_status", "Status", 100));
        cm.add(new ColumnModel("customerOrder_posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("customerOrder_poNumber", "PO", 150));
        cm.add(new ColumnModel("customerOrder_invoiceNumber", "Invoice", 75));
        cm.add(new ColumnModel("customerOrder_shipDate", "Ship Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_orderDate", "Order Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("customerOrder_customerCode", "Customer Code", 100));
        cm.add(new ColumnModel("customerOrder_salesman", "Salesman", 100));
        
        cm.add(new ColumnModel("displayIsbn", "Display ISBN", 80));
        cm.add(new ColumnModel("cond", "Condition", 50).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("filled", "Shipped", 80));
        cm.add(new ColumnModel("price", "Price", 90).setRenderer("moneyRendererRedBoldZero"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("totalPrice", "Extended Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("vendorpo", "Vendor PO", 100));
        orderListTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "displayIsbn"));
        filters.add(new Filter("string", "cond"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "filled"));
        filters.add(new Filter("float", "price"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("float", "totalPrice"));
        filters.add(new Filter("string", "vendorpo"));
        orderListTable.setFilters(filters);
        
        orderListTable.setDefaultSortCol("createTime");
        orderListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        Toolbar t = new Toolbar();
        orderListTable.setToolbar(t);
        
        // this is for the exports
        setExcelExportFileName("BookcountryOrderItemsExport");
        setExcelExportSheetName("Order Items");
        
    }
}
