package com.bc.actions.bellwether;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Criterion;

import com.bc.actions.BaseAction;
import com.bc.amazon.AmazonData;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import com.bc.ejb.bellwether.BellReceivingSessionLocal;
import com.bc.orm.BellInventory;
import com.bc.orm.BellOrder;
import com.bc.orm.BellReceived;
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
import java.util.StringTokenizer;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bellwether/inventory/list.jsp"),
    @Result(name="skus", location="/WEB-INF/jsp/bellwether/inventory/skus.jsp"),
    @Result(name="searchlist", location="/WEB-INF/jsp/bellwether/inventory/search.jsp"),
    @Result(name="multisearchlist", location="/WEB-INF/jsp/bellwether/inventory/multisearch.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bellwether/inventory/searchwindow.jsp"),    
    @Result(name="view", location="/WEB-INF/jsp/bellwether/inventory/view.jsp"),
    @Result(name="viewredirect", location="/secure/bellwether/inventory!view.bc", type="redirect"),
    @Result(name="detail", location="/WEB-INF/jsp/bellwether/inventory/detail.jsp"),
    @Result(name="amazonjson", location="/WEB-INF/jsp/bookcountry/inventoryitem/amazonjson.jsp"),
    @Result(name="amazondetail", location="/WEB-INF/jsp/bookcountry/inventoryitem/amazondetail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bellwether/inventory/crud.jsp"),
    @Result(name="isbninfo", location="/WEB-INF/jsp/bellwether/inventory/isbninfo.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="print", location="/WEB-INF/jsp/bellwether/inventory/print.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class InventoryAction extends BaseAction {

    private static Logger logger = Logger.getLogger(InventoryAction.class);
    
    private BellInventory inventory;
    private AmazonData amazonData;
    private Table listTable;
    
    private String isbn;
    private String sku;

    private List<BellOrder> latestOrders;
    private List<BellOrder> latestAmzOrders;
    private List<BellReceived> latestReceived;
    private Integer totalReceived;
    private Integer totalOrdered;
    private Integer totalAmzOrdered;
    
    private Table recListTable;
    private Table orderListTable;
    private Table amzOrderListTable;

    private boolean isQuickSearch = false;
    private String quickSearch;
    private String quickSearchLocation;

    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String quickSearch(){
        setupListTable();
        isQuickSearch = true;
        return "list";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String quickSearchListData(){
        setupListTable();
        try {
            Disjunction dis = Restrictions.disjunction();
            if (IsbnUtil.isValid(quickSearch)){
                if (IsbnUtil.isValid10(quickSearch)) {
                    dis.add(Restrictions.eq("isbn", quickSearch));
                    dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(quickSearch)));
                } else {
                    dis.add(Restrictions.eq("isbn", quickSearch));
                    dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(quickSearch)));
                }
            } else {
                dis.add(Restrictions.ilike("isbn", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("title", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("author", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("sku", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("bin", quickSearch, MatchMode.ANYWHERE));
            }
            queryInput.addAndCriterion(dis);
            BellInventorySessionLocal inventorySession = getBellInventorySession();
            queryResults = new QueryResults(inventorySession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }

    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String getInfo(){
        try {
            BellInventorySessionLocal inventorySession = getBellInventorySession();
            inventory = inventorySession.findByIsbn(isbn);
        } catch (Exception e){
            logger.error("could not get inventory item for isbn: "+isbn);
        }
        return "isbninfo";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String getAmazonInfo(){
        amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
        return "amazonjson";
    }

    private void setupSearchNames() {
        for(Filter f : listTable.getFilters()) {
            ColumnModel cm = listTable.getColumnModel(f.getName());
            if(cm != null && !cm.getHeader().equals("ISBN10") && !cm.getHeader().equals("ISBN13")) {
                searchNames.put(f.getName(), cm.getHeader());
            }
        }
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String print(){
        try {
            BellInventorySessionLocal inventorySession  = getBellInventorySession();
            inventory = inventorySession.findById(id);
            isbn = inventory.getIsbn();
            getAmazonInfo();
            
            // get the latest orders / receivings
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            queryInput = new QueryInput(0, 25);
            queryInput.addAndCriterion(Restrictions.eq("bellInventory", inventory));
            queryInput.setSortCol("date");
            queryInput.setSortDir(QueryInput.SORT_DESC);
            queryResults = new QueryResults(rSession.findAllItems(queryInput, "bellReceived"));
            latestReceived = queryResults.getData();
            totalReceived = queryResults.getTotalRecords();
            
            
            BellOrderSessionLocal oSession = getBellOrderSession();
            queryInput = new QueryInput(0, 25);
            queryInput.addAndCriterion(Restrictions.eq("bellInventory", inventory));
            queryInput.setSortCol("bellOrder.orderDate");
            queryInput.setSortDir(QueryInput.SORT_DESC);
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "bellOrder"));
            latestOrders = queryResults.getData();
            totalOrdered = queryResults.getTotalRecords();
            
            queryInput = new QueryInput(0, 25);
            queryInput.addAndCriterion(Restrictions.eq("category", "Amazon"));
            queryInput.addAndCriterion(Restrictions.like("sku", inventory.getIsbn(), MatchMode.END));
            queryInput.setSortCol("orderDate");
            queryInput.setSortDir(QueryInput.SORT_DESC);
            queryResults = new QueryResults(oSession.findAll(queryInput));
            latestAmzOrders = queryResults.getData();
            totalAmzOrdered = queryResults.getTotalRecords();
            
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "print";
    }
    
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
     
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String multiSearch(){
        setupListTable();
        return "multisearchlist";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String listsku(){
        setupSkuListTable();
        return "skus";
    }
    
    @ActionRole({"BellInvAdmin"})
    public String delete(){
        try {
            BellInventorySessionLocal inventorySession = getBellInventorySession();
            inventorySession.delete(id);
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not delete this inventory item, refresh the page and try again.");
        }
        return "status";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String listData(){
        setupListTable();
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            queryResults = new QueryResults(biSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String listSkuData(){
        setupSkuListTable();
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            queryResults = new QueryResults(biSession.findAllSkus(queryInput, "bellInventory"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String searchData(){
        setupListTable(); 
        setupSearchNames();
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            queryResults = new QueryResults(biSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String multiSearchData(){
        setupListTable();
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            if (search != null){
                if (search.getMultiIsbn() != null && search.getMultiIsbn().length() > 0){
                    //logger.info("search multiIsbn: "+search.getMultiIsbn());
                    Disjunction dis = Restrictions.disjunction();
                    StringTokenizer st = new StringTokenizer(search.getMultiIsbn(), ";"); 
                    while (st.hasMoreTokens()){
                        String token = st.nextToken();
                        dis.add(Restrictions.eq("isbn", token));
                        if (IsbnUtil.isValid(token)){
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(token)));
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(token)));
                        }
                    }
                    queryInput.addAndCriterion(dis);
                }
            }
            queryResults = new QueryResults(biSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list multi search data", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String detail(){
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id, "bellSkus");
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "detail";
    }

    @ActionRole({"BellInvAdmin"})
    public String edit(){
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id);
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "crud";
    }

    @ActionRole({"BellInvAdmin"})
    public String editSubmit(){
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            BellInventory dbi = biSession.findById(inventory.getId());
            
            dbi.setIsbn(inventory.getIsbn());
            if (dbi.getIsbn().length() == 10){
                dbi.setIsbn(dbi.getIsbn());
                dbi.setIsbn13(IsbnUtil.getIsbn13(dbi.getIsbn()));
            } else if (dbi.getIsbn().length() == 13 && dbi.getIsbn().startsWith("978")) {
                dbi.setIsbn(IsbnUtil.getIsbn10(dbi.getIsbn()));
                dbi.setIsbn13(dbi.getIsbn());
            }
            dbi.setTitle(inventory.getTitle());
            dbi.setAuthor(inventory.getAuthor());
            dbi.setPublisher(inventory.getPublisher());
            dbi.setListPrice(inventory.getListPrice());
            dbi.setSellPrice(inventory.getSellPrice());
            if (dbi.getOnhand() != inventory.getOnhand()){
                dbi.setOnhand(inventory.getOnhand());
                dbi.setAvailable(dbi.getOnhand() - dbi.getCommitted());
                if (dbi.getAvailable() < 0) dbi.setAvailable(0);
            }
            dbi.setBin(inventory.getBin());
            dbi.setCover(inventory.getCover());
            dbi.setCategory(inventory.getCategory());
            dbi.setBellcomment(inventory.getBellcomment());

            biSession.update(dbi);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not find inventory item for id: "+inventory.getId(), e);
            setSuccess(false);
            setMessage("Could not find this inventory to update, refresh the page and try again.");
        }
        return "status";
    }
    
    @ActionRole({"BellInvAdmin", "BellInvViewer"})
    public String amazonDetail(){
        // go after the amazon information based on isbn
        amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
        amazonData.setCategories(AmazonItemLookupSoap.getInstance().lookupCategories(isbn));
        //logger.info(amazonData.debugString());
        return "amazondetail";
    }
    
    @ActionRole({"BellInvViewer"})
    public String view(){
        try {
            setupRecListTable();
            setupOrderListTable();
            setupAmazonOrderListTable();
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id, "bellSkus");
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "view";
    }
    
    
    @ActionRole({"BellInvViewer"})
    public String listOrders(){
        setupOrderListTable();
        
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id);
            BellOrderSessionLocal oSession = getBellOrderSession();
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("bellInventory", inventory));
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "bellOrder"));
            queryResults.setTableConfig(orderListTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listOrders", e);
        }
        
        return "queryresults";
    }

    @ActionRole({"BellInvViewer"})
    public String listAmazonOrders(){
        setupAmazonOrderListTable();
        
        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id);
            BellOrderSessionLocal oSession = getBellOrderSession();
            queryInput.addAndCriterion(Restrictions.eq("category", "Amazon"));
            queryInput.addAndCriterion(Restrictions.like("sku", inventory.getIsbn(), MatchMode.END));
            queryResults = new QueryResults(oSession.findAll(queryInput));
            queryResults.setTableConfig(amzOrderListTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not listAmazonOrders", e);
        }
        
        return "queryresults";
    }
    
    @ActionRole({"BellInvViewer"})
    public String listReceivings(){
        setupRecListTable();

        try {
            BellInventorySessionLocal biSession = getBellInventorySession();
            inventory = biSession.findById(id);
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("bellInventory", inventory));
            queryResults = new QueryResults(rSession.findAllItems(queryInput, "bellReceived"));
            queryResults.setTableConfig(recListTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listReceivings", e);
        }
        
        return "queryresults";
    }
    
    private void setupRecListTable(){
        recListTable = new Table();
        recListTable.setExportable(true);
        recListTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("bellReceived.id", "bellReceived_id").setType("int"));
        cd.add(new ColumnData("bellReceived.posted", "bellReceived_posted"));
        cd.add(new ColumnData("bellReceived.date", "bellReceived_date").setType("date"));
        cd.add(new ColumnData("bellReceived.poNumber", "bellReceived_poNumber"));
        cd.add(new ColumnData("bellReceived.vendorCode", "bellReceived_vendorCode"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("orderedQuantity").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("listPrice").setType("float"));
        cd.add(new ColumnData("sellPrice").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("type"));
        cd.add(new ColumnData("bookType"));
        cd.add(new ColumnData("coverType"));
        cd.add(new ColumnData("breakroom"));
        cd.add(new ColumnData("lbs").setType("int"));
        cd.add(new ColumnData("lbsPrice").setType("float"));
        cd.add(new ColumnData("lbsCost").setType("float"));
        cd.add(new ColumnData("pieces").setType("int"));
        cd.add(new ColumnData("skid"));
        cd.add(new ColumnData("skidPrice").setType("float"));
        cd.add(new ColumnData("skidCost").setType("float"));
        cd.add(new ColumnData("skidPieceCount").setType("int"));
        cd.add(new ColumnData("skidPiecePrice").setType("float"));
        cd.add(new ColumnData("skidPieceCost").setType("float"));
        recListTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        
        cm.add(new ColumnModel("bellReceived_poNumber", "PO", 100));
        cm.add(new ColumnModel("bellReceived_date", "Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("bellReceived_posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bellReceived_vendorCode", "Vendor Code", 100));
        
        cm.add(new ColumnModel("bin", "Bin", 90));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("orderedQuantity", "Ordered Quantity", 50));
        cm.add(new ColumnModel("available", "Available", 50));
        cm.add(new ColumnModel("listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("sellPrice", "Sell Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("type", "Type", 70));
        cm.add(new ColumnModel("breakRoom", "Break Room", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bookType", "Book Type", 70));
        cm.add(new ColumnModel("coverType", "Cover Type", 70));
        cm.add(new ColumnModel("lbs", "Lbs", 70));
        cm.add(new ColumnModel("lbsPrice", "Lbs Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("lbsCost", "Lbs Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("pieces", "Pieces", 70));
        cm.add(new ColumnModel("skid", "Skid", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("skidPieceCount", "Skid Piece Count", 70));
        cm.add(new ColumnModel("skidPiecePrice", "Skid Piece Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("skidPieceCost", "Skid Piece Cost", 70).setRenderer("moneyRenderer"));
        recListTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "orderedQuantity"));
        filters.add(new Filter("integer", "available"));
        filters.add(new Filter("float", "listPirce"));
        filters.add(new Filter("float", "sellPrice"));
        filters.add(new Filter("float", "cost"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("string", "type"));
        filters.add(new Filter("boolean", "breakRoom"));
        filters.add(new Filter("string", "bookType"));
        filters.add(new Filter("string", "coverType"));
        filters.add(new Filter("integer", "lbs"));
        filters.add(new Filter("float", "lbsPrice"));
        filters.add(new Filter("float", "lbsCost"));
        filters.add(new Filter("integer", "pieces"));
        filters.add(new Filter("boolean", "skid"));
        filters.add(new Filter("integer", "skidPieceCount"));
        filters.add(new Filter("float", "skidPiecePrice"));
        filters.add(new Filter("float", "skidPieceCost"));
        recListTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        buttons.add(new ToolbarButton("View", "receivingViewButtonClick", "view_icon", "View The Selected Receivings Detail Page").setSingleRowAction(true));
        
        t.setButtons(buttons);
        recListTable.setToolbar(t);
        
        recListTable.setDefaultSortCol("createTime");
        recListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherReceivingItemsExport");
        setExcelExportSheetName("Receiving Items");
    }
    
    private void setupOrderListTable(){
        orderListTable = new Table();
        orderListTable.setExportable(true);
        orderListTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id"));
        
        cd.add(new ColumnData("bellOrder.id", "bellOrder_id").setType("int"));
        cd.add(new ColumnData("bellOrder.poNumber", "bellOrder_poNumber"));
        cd.add(new ColumnData("bellOrder.invoiceNumber", "bellOrder_invoiceNumber"));
        cd.add(new ColumnData("bellOrder.shipDate", "bellOrder_shipDate").setType("date"));
        cd.add(new ColumnData("bellOrder.orderDate", "bellOrder_orderDate").setType("date"));
        cd.add(new ColumnData("bellOrder.customerCode", "bellOrder_customerCode"));
        cd.add(new ColumnData("bellOrder.salesman", "bellOrder_salesman"));
        cd.add(new ColumnData("bellOrder.posted", "bellOrder_posted"));
        
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("sku"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("filled").setType("int"));
        cd.add(new ColumnData("price").setType("float"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("extended").setType("float"));
        cd.add(new ColumnData("vendorpo"));
        orderListTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        
        cm.add(new ColumnModel("bellOrder_posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bellOrder_poNumber", "PO", 150));
        cm.add(new ColumnModel("bellOrder_invoiceNumber", "Invoice", 75));
        cm.add(new ColumnModel("bellOrder_shipDate", "Ship Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("bellOrder_orderDate", "Order Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("bellOrder_customerCode", "Customer Code", 100));
        cm.add(new ColumnModel("bellOrder_salesman", "Salesman", 100));
        
        cm.add(new ColumnModel("isbn", "ISBN", 120));
        cm.add(new ColumnModel("sku", "SKU", 120));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("filled", "Shipped", 80));
        cm.add(new ColumnModel("price", "Price", 90).setRenderer("moneyRendererRedBoldZero"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("extended", "Extended Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("vendorpo", "Vendor PO", 100));
        orderListTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "sku"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "filled"));
        filters.add(new Filter("float", "price"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("float", "extended"));
        filters.add(new Filter("string", "vendorpo"));
        orderListTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        buttons.add(new ToolbarButton("View", "orderViewButtonClick", "view_icon", "View The Selected Orders Detail Page").setSingleRowAction(true));
        
        t.setButtons(buttons);
        orderListTable.setToolbar(t);
        
        orderListTable.setDefaultSortCol("createTime");
        orderListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherOrderItemsExport");
        setExcelExportSheetName("Order Items");
    }
    
    private void setupAmazonOrderListTable(){
        amzOrderListTable = new Table();
        amzOrderListTable.setExportable(true);
        amzOrderListTable.setPageable(true);
        
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
        cd.add(new ColumnData("shipAddress1"));
        cd.add(new ColumnData("shipAddress2"));
        cd.add(new ColumnData("shipCity"));
        cd.add(new ColumnData("shipState"));
        cd.add(new ColumnData("shipZip"));
        cd.add(new ColumnData("shipCountry"));
        cd.add(new ColumnData("specialComments"));
        cd.add(new ColumnData("upc"));
        cd.add(new ColumnData("shipMethod"));
        amzOrderListTable.setColumnDatas(cd);
        
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
        cm.add(new ColumnModel("shipMethod", "Ship Method", 100));
        cm.add(new ColumnModel("shipAddress1", "Ship Address 1", 100));
        cm.add(new ColumnModel("shipAddress2", "Ship Address 2", 100));
        cm.add(new ColumnModel("shipCity", "Ship City", 100));
        cm.add(new ColumnModel("shipState", "Ship State", 100));
        cm.add(new ColumnModel("shipZip", "Ship Zip", 100));
        cm.add(new ColumnModel("shipCountry", "Ship Country", 100));
        cm.add(new ColumnModel("specialComments", "Special Comments", 100));
        amzOrderListTable.setColumnModels(cm);
        
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
        filters.add(new Filter("string", "shipAddress1"));
        filters.add(new Filter("string", "shipAddress2"));
        filters.add(new Filter("string", "shipCity"));
        filters.add(new Filter("string", "shipState"));
        filters.add(new Filter("string", "shipZip"));
        filters.add(new Filter("string", "shipCountry"));
        filters.add(new Filter("string", "specialComments"));
        filters.add(new Filter("string", "upc"));
        filters.add(new Filter("string", "shipMethod"));
        amzOrderListTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        t.setButtons(buttons);
        amzOrderListTable.setToolbar(t);
        
        amzOrderListTable.setDefaultSortCol("id");
        amzOrderListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherOrderExport");
        setExcelExportSheetName("Orders");
    }
    
    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn10"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("listPrice").setType("float"));
        //cd.add(new ColumnData("sellPrice").setType("float"));
        cd.add(new ColumnData("lowUsed").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("receivedPrice").setType("float"));
        cd.add(new ColumnData("receivedDiscount").setType("float"));
        cd.add(new ColumnData("weight").setType("float"));
        cd.add(new ColumnData("costPerPound").setType("float"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("bellBook"));
        //cd.add(new ColumnData("bellcondition"));
        cd.add(new ColumnData("bellcomment"));
        cd.add(new ColumnData("cover"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("publisher"));
        cd.add(new ColumnData("author"));
        cd.add(new ColumnData("category"));
        cd.add(new ColumnData("onhand").setType("int"));
        cd.add(new ColumnData("committed").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("listed").setType("int"));
        cd.add(new ColumnData("salesrank").setType("int"));
        cd.add(new ColumnData("lastListDate").setType("date"));
        //cd.add(new ColumnData("sellPricePercentList").setType("float"));
        //cd.add(new ColumnData("costPercentList").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("isbn10", "ISBN10", 100).setSortable(false));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("bellBook", "Bellwether Book", 70).setRenderer("booleanRenderer"));
        //cm.add(new ColumnModel("bellcondition", "Condition", 70));
        cm.add(new ColumnModel("listPrice", "List Pirce", 70).setRenderer("moneyRenderer"));
        //cm.add(new ColumnModel("sellPrice", "Selling Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("lowUsed", "Lowest Used Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("onhand", "On Hand", 70));
        //cm.add(new ColumnModel("committed", "Committed", 70));
        //cm.add(new ColumnModel("available", "Available", 70));
        cm.add(new ColumnModel("listed", "Listed", 70));
        cm.add(new ColumnModel("salesrank", "Sales Rank", 70));
        cm.add(new ColumnModel("lastListDate", "Last Listed", 70).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("cover", "Cover", 70));
        cm.add(new ColumnModel("title", "Title", 150));
        cm.add(new ColumnModel("publisher", "Publisher", 70));
        cm.add(new ColumnModel("author", "Author", 70));
        if (isUserInRole("BellInvAdmin")) {
            cm.add(new ColumnModel("cost", "Cost", 70).setRenderer("moneyRenderer"));
            //cm.add(new ColumnModel("costPerPound", "Cost Per Pound", 70));
        }
        cm.add(new ColumnModel("receivedPrice", "Received Pirce", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("receivedDiscount", "Received Discount", 70));
        cm.add(new ColumnModel("weight", "Weight", 70));
        cm.add(new ColumnModel("category", "Category", 70));
        cm.add(new ColumnModel("bellcomment", "Comment", 150));
        
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "publisher"));
        filters.add(new Filter("string", "author"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("boolean", "bellBook"));
        //filters.add(new Filter("string", "bellcondition"));
        filters.add(new Filter("string", "cover"));
        filters.add(new Filter("string", "bellcomment"));
        filters.add(new Filter("string", "category"));
        filters.add(new Filter("date", "lastListDate"));
        filters.add(new Filter("integer", "onhand"));
        filters.add(new Filter("integer", "listed"));
        //filters.add(new Filter("integer", "available"));
        //filters.add(new Filter("integer", "committed"));
        filters.add(new Filter("integer", "salesrank"));
        if (isUserInRole("BellInvAdmin")) {
            filters.add(new Filter("float", "cost"));
        }
        filters.add(new Filter("float", "listPrice"));
        filters.add(new Filter("float", "sellPrice"));
        filters.add(new Filter("float", "lowUsed"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcInventoryAdmin()){
            //buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Inventory Item"));
            //buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Inventory Item").setSingleRowAction(true));
            //buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Inventory Item").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Inventory Items Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Print", "printButtonClick", "print_icon", "View The Selected Inventory Items Print Page").setSingleRowAction(true));
        //buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Inventory Items History Of Changes").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("title");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherInventoryExport");
        setExcelExportSheetName("Inventory Items");
        
    }

    private void setupSkuListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("bellInventory.id", "bellInventory_id").setType("int"));
        cd.add(new ColumnData("sku"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("bellInventory.isbn10", "bellInventory_isbn10"));
        cd.add(new ColumnData("bellInventory.isbn13", "bellInventory_isbn13"));
        cd.add(new ColumnData("bellInventory.listPrice", "bellInventory_listPrice").setType("float"));
        cd.add(new ColumnData("bellInventory.sellPrice", "bellInventory_sellPrice").setType("float"));
        cd.add(new ColumnData("bellInventory.lowUsed", "bellInventory_lowUsed").setType("float"));
        cd.add(new ColumnData("bellInventory.cost", "bellInventory_cost").setType("float"));
        cd.add(new ColumnData("bellInventory.receivedPrice", "bellInventory_receivedPrice").setType("float"));
        cd.add(new ColumnData("bellInventory.receivedDiscount", "bellInventory_receivedDiscount").setType("float"));
        cd.add(new ColumnData("bellInventory.weight", "bellInventory.weight").setType("float"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("location"));
        cd.add(new ColumnData("bellBook"));
        cd.add(new ColumnData("bellcondition"));
        cd.add(new ColumnData("bellcomment"));
        cd.add(new ColumnData("cover"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("publisher"));
        cd.add(new ColumnData("author"));
        cd.add(new ColumnData("bellInventory.category", "bellInventory_category"));
        cd.add(new ColumnData("onhand").setType("int"));
        //cd.add(new ColumnData("committed").setType("int"));
        //cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("lowest"));
        cd.add(new ColumnData("listed").setType("int"));
        cd.add(new ColumnData("salesrank").setType("int"));
        cd.add(new ColumnData("lastListDate").setType("date"));
        //cd.add(new ColumnData("sellPricePercentList").setType("float"));
        //cd.add(new ColumnData("costPercentList").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("sku", "SKU", 150));
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("bellInventory_isbn10", "ISBN10", 100).setSortable(false));
        cm.add(new ColumnModel("bellInventory_isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("bin", "Bin", 70));
        cm.add(new ColumnModel("location", "Location", 125));
        cm.add(new ColumnModel("bellBook", "Bellwether Book", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bellcondition", "Condition", 100).setRenderer("bellConditionRenderer"));
        cm.add(new ColumnModel("bellInventory_listPrice", "List Pirce", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("bellInventory_sellPrice", "Selling Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("bellInventory_lowUsed", "Lowest Used Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("lowest", "Lowest?", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("onhand", "On Hand", 70));
        //cm.add(new ColumnModel("committed", "Committed", 70));
        //cm.add(new ColumnModel("available", "Available", 70));
        cm.add(new ColumnModel("listed", "Listed", 70));
        cm.add(new ColumnModel("salesrank", "Sales Rank", 70));
        cm.add(new ColumnModel("lastListDate", "Last Listed", 70).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("cover", "Cover", 70));
        cm.add(new ColumnModel("title", "Title", 150));
        cm.add(new ColumnModel("publisher", "Publisher", 70));
        cm.add(new ColumnModel("author", "Author", 70));
        if (isUserInRole("BellInvAdmin")) {
            cm.add(new ColumnModel("bellInventory_cost", "Cost", 70).setRenderer("moneyRenderer"));
            //cm.add(new ColumnModel("costPerPound", "Cost Per Pound", 70));
        }
        cm.add(new ColumnModel("bellInventory_receivedPrice", "Received Pirce", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("bellInventory_receivedDiscount", "Received Discount", 70));
        cm.add(new ColumnModel("bellInventory_weight", "Weight", 70));
        cm.add(new ColumnModel("bellInventory_category", "Category", 70));
        cm.add(new ColumnModel("bellcomment", "Comment", 150));
        
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("string", "sku"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "bellInventory_isbn13"));
        filters.add(new Filter("string", "publisher"));
        filters.add(new Filter("string", "author"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("string", "location"));
        filters.add(new Filter("boolean", "bellBook"));
        filters.add(new Filter("string", "bellcondition"));
        filters.add(new Filter("string", "cover"));
        filters.add(new Filter("string", "bellcomment"));
        filters.add(new Filter("string", "bellInventory_category"));
        filters.add(new Filter("date", "lastListDate"));
        filters.add(new Filter("integer", "onhand"));
        //filters.add(new Filter("integer", "available"));
        //filters.add(new Filter("integer", "committed"));
        filters.add(new Filter("integer", "salesrank"));
        filters.add(new Filter("boolean", "lowest"));
        filters.add(new Filter("integer", "listed"));
        if (isUserInRole("BellInvAdmin")) {
            filters.add(new Filter("float", "bellInventory_cost"));
        }
        filters.add(new Filter("float", "listPrice"));
        filters.add(new Filter("float", "sellPrice"));
        filters.add(new Filter("float", "bellInventory_lowUsed"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcInventoryAdmin()){
            //buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Inventory Item"));
            //buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Inventory Item").setSingleRowAction(true));
            //buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Inventory Item").setSingleRowAction(true));
        }
        //buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Inventory Items Detail Page").setSingleRowAction(true));
        //buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Inventory Items History Of Changes").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("title");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BellwetherInventoryExport");
        setExcelExportSheetName("Inventory Items");
        
    }
    
    public BellInventory getInventory() {
        return inventory;
    }

    public void setInventory(BellInventory inventory) {
        this.inventory = inventory;
    }

    public AmazonData getAmazonData() {
        return amazonData;
    }

    public void setAmazonData(AmazonData amazonData) {
        this.amazonData = amazonData;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Table getRecListTable() {
        return recListTable;
    }

    public void setRecListTable(Table recListTable) {
        this.recListTable = recListTable;
    }

    public List<BellOrder> getLatestOrders() {
        return latestOrders;
    }

    public void setLatestOrders(List<BellOrder> latestOrders) {
        this.latestOrders = latestOrders;
    }

    public List<BellReceived> getLatestReceived() {
        return latestReceived;
    }

    public void setLatestReceived(List<BellReceived> latestReceived) {
        this.latestReceived = latestReceived;
    }

    public Integer getTotalOrdered() {
        return totalOrdered;
    }

    public void setTotalOrdered(Integer totalOrdered) {
        this.totalOrdered = totalOrdered;
    }

    public Integer getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(Integer totalReceived) {
        this.totalReceived = totalReceived;
    }

    public Table getAmzOrderListTable() {
        return amzOrderListTable;
    }

    public void setAmzOrderListTable(Table amzOrderListTable) {
        this.amzOrderListTable = amzOrderListTable;
    }

    public List<BellOrder> getLatestAmzOrders() {
        return latestAmzOrders;
    }

    public void setLatestAmzOrders(List<BellOrder> latestAmzOrders) {
        this.latestAmzOrders = latestAmzOrders;
    }

    public Integer getTotalAmzOrdered() {
        return totalAmzOrdered;
    }

    public void setTotalAmzOrdered(Integer totalAmzOrdered) {
        this.totalAmzOrdered = totalAmzOrdered;
    }

    
}
