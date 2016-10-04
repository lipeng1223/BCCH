 package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.amazon.AmazonData;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.ejb.ReceivingSessionLocal;
import com.bc.orm.CustomerOrder;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
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
import com.bc.util.Search;
import java.util.Set;
import org.hibernate.Hibernate;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/inventoryitem/list.jsp"),
    @Result(name="print", location="/WEB-INF/jsp/bookcountry/inventoryitem/print.jsp"),
    @Result(name="searchlist", location="/WEB-INF/jsp/bookcountry/inventoryitem/search.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bookcountry/inventoryitem/searchwindow.jsp"),    
    @Result(name="multisearchlist", location="/WEB-INF/jsp/bookcountry/inventoryitem/multisearch.jsp"),
    @Result(name="view", location="/WEB-INF/jsp/bookcountry/inventoryitem/view.jsp"),
    @Result(name="view-orders", location="/WEB-INF/jsp/bookcountry/inventoryitem/view-orders.jsp"),
    @Result(name="view-recs", location="/WEB-INF/jsp/bookcountry/inventoryitem/view-recs.jsp"),
    @Result(name="viewredirect", location="/secure/bookcountry/inventoryitem!view.bc", type="redirect"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/inventoryitem/detail.jsp"),
    @Result(name="amazonjson", location="/WEB-INF/jsp/bookcountry/inventoryitem/amazonjson.jsp"),
    @Result(name="amazondetail", location="/WEB-INF/jsp/bookcountry/inventoryitem/amazondetail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/inventoryitem/crud.jsp"),
    @Result(name="isbninfo", location="/WEB-INF/jsp/bookcountry/inventoryitem/isbninfo.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class InventoryItemAction extends BaseAction {

    private static Logger logger = Logger.getLogger(InventoryItemAction.class);
    
    private InventoryItem inventoryItem;
    private AmazonData amazonData;
    private Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    private Boolean includeRestricted = false;
    private Boolean includeBell = false;
    
    private Boolean showNewWindowButton = true;

    private List<CustomerOrder> latestOrders;
    private List<Received> latestReceived;
    private Integer totalReceived;
    private Integer totalOrdered;

    private Table recListTable;
    private Table orderListTable;
    
    private String condition;
    private String isbn;
    private String title;
    
    private Integer preReceivingQuantity;
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String getInfo(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findByIsbnCond(isbn, condition);
        } catch (Exception e){
            logger.error("could not get inventory item for isbn: "+isbn+" cond: "+condition);
        }
        return "isbninfo";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String getInfoFromTitle(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findByTitleCond(title, condition);
        } catch (Exception e){
            logger.error("could not get inventory item for isbn: "+isbn+" cond: "+condition);
        }
        return "isbninfo";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String getAmazonInfo(){
        amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
        return "amazonjson";
    }
        
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String multiSearch(){
        setupListTable();
        listTable.setMarketable(true);
        return "multisearchlist";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String quickSearch(){
        setupListTable();
        isQuickSearch = true;
        return "list";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String createSubmit(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItemSession.create(inventoryItem);
            id = inventoryItem.getId();
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not create the inventory item", e);
            setSuccess(false);
            setMessage("Could not create the inventory item there was a system error");
        }
        return "status";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String edit(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findById(id);
        } catch (Exception e){
            logger.error("Could not find inventory item for id: "+id);
        }
        return "crud";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String editSubmit(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            InventoryItem check = inventoryItemSession.findByIsbnCond(inventoryItem.getIsbn(), inventoryItem.getCond());
            if (!check.getId().equals(inventoryItem.getId())){
                setSuccess(false);
                setMessage("There is another inventory item in the system with isbn: "+inventoryItem.getIsbn()+" and condition: "+
                    inventoryItem.getCond()+".  Please edit that Inventory Item.");
                return "status";
            }
            InventoryItem dbi = inventoryItemSession.findById(inventoryItem.getId());
            
            dbi.setCond(inventoryItem.getCond());
            dbi.setIsbn(inventoryItem.getIsbn());
            if (dbi.getIsbn().length() == 10){
                dbi.setIsbn10(dbi.getIsbn());
                dbi.setIsbn13(IsbnUtil.getIsbn13(dbi.getIsbn()));
            } else if (dbi.getIsbn().length() == 13 && dbi.getIsbn().startsWith("978")) {
                dbi.setIsbn10(IsbnUtil.getIsbn10(dbi.getIsbn()));
                dbi.setIsbn13(dbi.getIsbn());
                dbi.setIsbn(dbi.getIsbn10());
            }
            dbi.setTitle(inventoryItem.getTitle());
            dbi.setAuthor(inventoryItem.getAuthor());
            dbi.setCompanyRec(inventoryItem.getCompanyRec());
            dbi.setListPrice(inventoryItem.getListPrice());
            dbi.setSellingPrice(inventoryItem.getSellingPrice());
            if (dbi.getOnhand() != inventoryItem.getOnhand()){
                dbi.setOnhand(inventoryItem.getOnhand());
                dbi.setAvailable(dbi.getOnhand() - dbi.getCommitted());
                if (dbi.getAvailable() < 0) dbi.setAvailable(0);
            }
            dbi.setBin(inventoryItem.getBin());
            dbi.setCover(inventoryItem.getCover());
            dbi.setBellbook(inventoryItem.getBellbook());
            dbi.setRestricted(inventoryItem.getRestricted());
            dbi.setHe(inventoryItem.getHe());
            dbi.setBiblio(inventoryItem.getBiblio());
            dbi.setBccategory(inventoryItem.getBccategory());
            dbi.setSkid(inventoryItem.getSkid());
            dbi.setNumberOfPages(inventoryItem.getNumberOfPages());
            dbi.setLength(inventoryItem.getLength());
            dbi.setWidth(inventoryItem.getWidth());
            dbi.setHeight(inventoryItem.getHeight());
            dbi.setWeight(inventoryItem.getWeight());

            inventoryItemSession.update(dbi);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not find inventory item for id: "+inventoryItem.getId(), e);
            setSuccess(false);
            setMessage("Could not find this inventory to update, refresh the page and try again.");
        }
        return "status";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String delete(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            if (!inventoryItemSession.canBeDeleted(id)){
                setSuccess(false);
                setMessage("This Inventory Item has at least one Order Item or Received Item associated with it.  It cannot be deleted.");
                return "status";
            }
            inventoryItemSession.delete(id);
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not delete this inventory item, refresh the page and try again.");
        }
        return "status";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String listData(){
        if (exportBulkToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupBulkListTable();
        } else if (exportCountToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupCountListTable();
        } else {
            setupListTable();
        }
        try {
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            queryResults = new QueryResults(iiSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String quickSearchListData(){
        if (exportBulkToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupBulkListTable();
        } else if (exportCountToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupCountListTable();
        } else {
            setupListTable();
        }
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
                dis.add(Restrictions.ilike("companyRec", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("imprintRec", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("bin", quickSearch, MatchMode.ANYWHERE));
            }
            queryInput.addAndCriterion(dis);
            
            // do not show bell book or restricted on quick search ??
            //queryInput.addAndCriterion(Restrictions.eq("bellbook", false));
            //queryInput.addAndCriterion(Restrictions.eq("restricted", false));
            
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            queryResults = new QueryResults(iiSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String searchData(){
        setupListTable(); 
        try {
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            
            if (!includeBell) queryInput.addAndCriterion(Restrictions.eq("bellbook", false));
            if (!includeRestricted) queryInput.addAndCriterion(Restrictions.eq("restricted", false));
            
            queryResults = new QueryResults(iiSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String multiSearchData(){
        if (exportBulkToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupBulkListTable();
        } else if (exportCountToExcel){
            queryInput.setStart(0);
            queryInput.setLimit(null);
            setupCountListTable();
        } else {
            setupListTable();
        }
        try {
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
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
                    if (search.getIncludeBell() == null || !search.getIncludeBell()){
                        queryInput.addAndCriterion(Restrictions.eq("bellbook", false));
                    }
                    if (search.getIncludeRestricted() == null || !search.getIncludeRestricted()){
                        queryInput.addAndCriterion(Restrictions.eq("restricted", false));
                    }
                    if (search.getIncludeHigherEducation() == null || !search.getIncludeHigherEducation()){
                        queryInput.addAndCriterion(Restrictions.eq("he", false));
                    }
                    
                    //queryInput.addAndCriterion(Restrictions.ge("available", 0));
                }
            }
            queryResults = new QueryResults(iiSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list multi search data", e);
        }
        return "queryresults";
    }

    @ActionRole({"BcInvViewer"})
    public String view(){
        try {
            setupRecListTable();
            setupOrderListTable();
            
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            if (id != null){
                inventoryItem = inventoryItemSession.findById(id, "receivedItems", "receivedItems.received");
            } else if (isbn != null && condition != null){
                inventoryItem = inventoryItemSession.findByIsbnCond(isbn, condition, "receivedItems", "receivedItems.received");
            } else {
                logger.error("NO ID, ISBN, or COND to load inventory item");
            }
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        Set<ReceivedItem> ritems = inventoryItem.getReceivedItems();
        this.preReceivingQuantity = 0;
        for (ReceivedItem ri : ritems){
            if (!ri.getReceived().getPosted()){
                if (!ri.getPoNumber().startsWith("PRE"))
                    continue;
                this.preReceivingQuantity += ri.getQuantity();
            }
        }
        return "view";
    }
    
    @ActionRole({"BcInvViewer"})
    public String listOrdersWin(){
        view();
        return "view-orders";
    }
    
    @ActionRole({"BcInvViewer"})
    public String listOrders(){
        setupOrderListTable();
        
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findById(id);
            OrderSessionLocal oSession = getOrderSession();
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("inventoryItem", inventoryItem));
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "customerOrder"));
            queryResults.setTableConfig(orderListTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listOrders", e);
        }
        
        return "queryresults";
    }
    
    @ActionRole({"BcInvViewer"})
    public String listReceivingsWin(){
        view();
        return "view-recs";
    }
    
    @ActionRole({"BcInvViewer"})
    public String listReceivings(){
        setupRecListTable();

        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findById(id);
            ReceivingSessionLocal rSession = getReceivingSession();
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("inventoryItem", inventoryItem));
            queryResults = new QueryResults(rSession.findAllItems(queryInput, "received"));
            queryResults.setTableConfig(recListTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listReceivings", e);
        }
        
        return "queryresults";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String detail(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findById(id, "receivedItems", "receivedItems.received");
            String isbn =inventoryItem.getIsbn();
            amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
            amazonData.setCategories(AmazonItemLookupSoap.getInstance().lookupCategories(isbn));
            if (amazonData.getSalesRank() != null){
            //    inventoryItem.setSalesRank(Integer.parseInt(amazonData.getSalesRank()));
            //    inventoryItem.setLastAmazonUpdate(amazonData.getCheckTime());
                inventoryItem.setSmallImage(amazonData.getSmallImageUrl());
                inventoryItem.setMediumImage(amazonData.getLargeImageUrl());
            }
//            if (amazonData == null)
//            {
//                logger.info("Amazon Data is null for ISBN : " + isbn);
//            }
//            else{
//                logger.info("Title : " + amazonData.getTitle());
//                logger.info("Large Image : " + amazonData.getLargeImageUrl());
//                logger.info("Small Image : " + amazonData.getSmallImageUrl());
//            }
//            inventoryItemSession.update(inventoryItem);
            Set<ReceivedItem> ritems = inventoryItem.getReceivedItems();
            this.preReceivingQuantity = 0;
            for (ReceivedItem ri : ritems){
                if (!ri.getReceived().getPosted()){
                    if (!ri.getPoNumber().startsWith("PRE"))
                        continue;
                    this.preReceivingQuantity += ri.getQuantity();
                }
            }
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "detail";
    }

    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String print(){
        try {
            InventoryItemSessionLocal inventoryItemSession = getInventoryItemSession();
            inventoryItem = inventoryItemSession.findById(id);
            isbn = inventoryItem.getIsbn();
            getAmazonInfo();
            
            // get the latest orders / receivings
            ReceivingSessionLocal rSession = getReceivingSession();
            queryInput = new QueryInput(0, 25);
            queryInput.addAndCriterion(Restrictions.eq("inventoryItem", inventoryItem));
            queryInput.setSortCol("createTime");
            queryInput.setSortDir(QueryInput.SORT_DESC);
            queryResults = new QueryResults(rSession.findAllItems(queryInput, "received"));
            latestReceived = queryResults.getData();
            totalReceived = queryResults.getTotalRecords();
            
            
            OrderSessionLocal oSession = getOrderSession();
            queryInput = new QueryInput(0, 25);
            queryInput.addAndCriterion(Restrictions.eq("inventoryItem", inventoryItem));
            queryInput.setSortCol("customerOrder.orderDate");
            queryInput.setSortDir(QueryInput.SORT_DESC);
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "customerOrder"));
            latestOrders = queryResults.getData();
            totalOrdered = queryResults.getTotalRecords();
        } catch (Exception e){
            logger.error("Could not find inventory item by id "+id, e);
        }
        return "print";
    }
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String amazonDetail(){
        // go after the amazon information based on isbn
        if (isbn.length() == 10 || isbn.length() == 13){
            amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
            amazonData.setCategories(AmazonItemLookupSoap.getInstance().lookupCategories(isbn));
        } else {
            // never going to be in amazon
            amazonData = new AmazonData(null);
        }
        //logger.info(amazonData.debugString());
        return "amazondetail";
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
        cd.add(new ColumnData("companyRec"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("author"));        
        cd.add(new ColumnData("listPrice").setType("float"));
        cd.add(new ColumnData("sellingPrice").setType("float"));
        cd.add(new ColumnData("sellPricePercentList").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("costPercentList").setType("float"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("cover"));
        //cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("onhand").setType("int"));
        cd.add(new ColumnData("committed").setType("int"));
        //cd.add(new ColumnData("onorder").setType("int"));
        cd.add(new ColumnData("salesRank").setType("int"));
        cd.add(new ColumnData("bellbook").setType("boolean"));
        cd.add(new ColumnData("restricted").setType("boolean"));
        cd.add(new ColumnData("backStock").setType("boolean"));
        cd.add(new ColumnData("he").setType("boolean"));
        cd.add(new ColumnData("lastpo"));
        cd.add(new ColumnData("lastpoDate").setType("date"));
        cd.add(new ColumnData("receivedDate").setType("date"));
        cd.add(new ColumnData("receivedQuantity").setType("int"));
        cd.add(new ColumnData("comment"));
        cd.add(new ColumnData("bccategory"));
        cd.add(new ColumnData("category1"));
        cd.add(new ColumnData("category2"));
        cd.add(new ColumnData("category3"));
        cd.add(new ColumnData("category4"));
        cd.add(new ColumnData("nightlyAmazonTotalNew").setType("int"));
        cd.add(new ColumnData("nightlyAmazonLowestNewPrice").setType("float"));
        cd.add(new ColumnData("nightlyAmazonTotalUsed").setType("int"));
        cd.add(new ColumnData("nightlyAmazonLowestUsedPrice").setType("float"));
        cd.add(new ColumnData("amazonLink"));
        cd.add(new ColumnData("publishDate").setType("date"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("isbn10", "ISBN10", 100));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("companyRec", "Publisher", 125));
        cm.add(new ColumnModel("title", "Title", 180));
        cm.add(new ColumnModel("author", "Author", 200));
        cm.add(new ColumnModel("listPrice", "List Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("sellingPrice", "Selling Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("sellPricePercentList", "Selling Percent List", 70).setRenderer("percentNoModRenderer"));
        if (isUserInRole("BcInvAdmin")) {
            cm.add(new ColumnModel("cost", "Cost", 70).setRenderer("moneyRenderer").setSortable(false));
            cm.add(new ColumnModel("costPercentList", "Cost Percent List", 70).setRenderer("percentNoModRenderer").setSortable(false));
        }
        cm.add(new ColumnModel("bin", "Bin", 60));
        cm.add(new ColumnModel("cond", "Condition", 60).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("cover", "Cover", 60));
        //cm.add(new ColumnModel("quantity", "Quantity", 60));
        cm.add(new ColumnModel("available", "Available", 60));
        cm.add(new ColumnModel("onhand", "On Hand", 60));
        cm.add(new ColumnModel("committed", "Committed", 60));
        //cm.add(new ColumnModel("onorder", "On Order", 60));
        cm.add(new ColumnModel("salesRank", "Nightly Sales Rank", 80));
        cm.add(new ColumnModel("bellbook", "Bell Book", 60).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("restricted", "Restricted", 60).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("he", "HE", 50).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("backStock", "Back Stock", 50).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("lastpo", "Last PO", 100));
        cm.add(new ColumnModel("lastpoDate", "Last PO Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("receivedDate", "Last Received Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("publishDate", "Publish Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("receivedQuantity", "Last Received Qty", 60));
        cm.add(new ColumnModel("bccategory", "Bc Category", 100));
        cm.add(new ColumnModel("category1", "Category 1", 100));
        cm.add(new ColumnModel("category2", "Category 2", 100));
        cm.add(new ColumnModel("category3", "Category 3", 100));
        cm.add(new ColumnModel("category4", "Category 4", 100));
        cm.add(new ColumnModel("comment", "Comment", 200));
        cm.add(new ColumnModel("nightlyAmazonTotalNew", "Amazon New", 100));
        cm.add(new ColumnModel("nightlyAmazonLowestNewPrice", "Amazon New Lowest", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("nightlyAmazonTotalUsed", "Amazon Used", 100));
        cm.add(new ColumnModel("nightlyAmazonLowestUsedPrice", "Amazon Used Lowest", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("amazonLink", "Amazon Link", 100));//.setHidden(true));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn10"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "publisher"));
        filters.add(new Filter("string", "companyRec"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "author"));        
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("string", "cond"));
        filters.add(new Filter("string", "cover"));
        filters.add(new Filter("string", "comment"));
        filters.add(new Filter("string", "bccategory"));
        filters.add(new Filter("string", "category1"));
        filters.add(new Filter("string", "category2"));
        filters.add(new Filter("string", "category3"));
        filters.add(new Filter("string", "category4"));
        filters.add(new Filter("string", "lastpo"));
        filters.add(new Filter("date", "lastpoDate"));
        filters.add(new Filter("date", "receivedDate"));
        filters.add(new Filter("boolean", "bellbook"));
        filters.add(new Filter("boolean", "restricted"));
        filters.add(new Filter("boolean", "he"));
        filters.add(new Filter("boolean", "backStock"));
        //filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "onhand"));
        filters.add(new Filter("integer", "available"));
        filters.add(new Filter("integer", "committed"));
        //filters.add(new Filter("integer", "onorder"));
        filters.add(new Filter("integer", "salesRank"));
        filters.add(new Filter("float", "listPrice"));
        filters.add(new Filter("float", "sellingPrice"));
        filters.add(new Filter("float", "sellPricePercentList"));
        filters.add(new Filter("integer", "nightlyAmazonTotalNew"));
        filters.add(new Filter("integer", "nightlyAmazonTotalUsed"));
        filters.add(new Filter("float", "nightlyAmazonLowestNewPrice"));
        filters.add(new Filter("float", "nightlyAmazonLowestUsedPrice"));
        filters.add(new Filter("integer", "receivedQuantity"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcInventoryAdmin()){
            //buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Inventory Item"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Inventory Item").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Inventory Item").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Inventory Items Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("View In New Window", "viewNewWinButtonClick", "view_icon", "View The Selected Inventory Item Detail Page In A New Window").setSingleRowAction(true));
        buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Inventory Items History Of Changes").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Print", "printButtonClick", "print_icon", "View The Selected Inventory Items Print Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Bulk", "bulkExportButtonClick", "excel_icon", "Export This Set Of Inventory For Bulk Update"));
        buttons.add(new ToolbarButton("Count", "countExportButtonClick", "excel_icon", "Export This Set Of Inventory For Count Update"));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("title");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryInventoryExport");
        setExcelExportSheetName("Inventory Items");
        
    }
    
    private void setupSearchNames() {
        for(Filter f : listTable.getFilters()) {
            ColumnModel cm = listTable.getColumnModel(f.getName());
            if(cm != null && !cm.getHeader().equals("ISBN10") && !cm.getHeader().equals("ISBN13")) {
                searchNames.put(f.getName(), cm.getHeader());
            }
        }
    }
    
    private void setupBulkListTable(){
        /*
        Column A: ISBN
        Column B: Condition (hurt, overstock, unjacketed)
        Column C: Sell Price
        Column D: Bell Book (true / false)
        Column E: Restricted (true / false)
        Column F: Higher Education (true / false)
        */
        
        listTable = new Table();
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("sellingPrice"));
        cd.add(new ColumnData("bellbook"));
        cd.add(new ColumnData("restricted"));
        cd.add(new ColumnData("he"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("cond", "Condition", 60));
        cm.add(new ColumnModel("sellingPrice", "Selling Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("bellbook", "Bell Book", 60).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("restricted", "Restricted", 60).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("he", "Higher Education", 60).setRenderer("booleanRenderer"));
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("isbn");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryBulkUpdateInventoryExport");
        setExcelExportSheetName("Inventory Items");
    }
    
    private void setupCountListTable(){
        /*
        Column A: ISBN
        Column B: Condition (hurt, overstock, unjacketed)
        Column C: Title
        Column D: Cover
        Column E: Bin
        Column F: Current Count
        Column G: Revised Count
        Column H: Revised Bin
        */
        
        listTable = new Table();
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("cover"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("onhand"));
        cd.add(new ColumnData("blank1"));
        cd.add(new ColumnData("blank2"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("cond", "Condition", 60));
        cm.add(new ColumnModel("title", "Title", 60));
        cm.add(new ColumnModel("cover", "Cover", 60));
        cm.add(new ColumnModel("bin", "Bin", 60));
        cm.add(new ColumnModel("onhand", "Current Count", 60));
        cm.add(new ColumnModel("blank1", "Revised Count", 60));
        cm.add(new ColumnModel("blank2", "Revised Bin", 60));
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("isbn");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryCountUpdateInventoryExport");
        setExcelExportSheetName("Inventory Items");
    }
    
    
    private void setupRecListTable(){
        recListTable = new Table();
        recListTable.setExportable(true);
        recListTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("received.id", "received_id").setType("int"));
        cd.add(new ColumnData("received.posted", "received_posted").setType("boolean"));
        cd.add(new ColumnData("received.holding", "received_holding").setType("boolean"));
        cd.add(new ColumnData("received.date", "received_date").setType("date"));
        cd.add(new ColumnData("received.poNumber", "received_poNumber"));
        cd.add(new ColumnData("received.vendorCode", "received_vendorCode"));
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
        cd.add(new ColumnData("percentageList").setType("float"));
        recListTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        
        cm.add(new ColumnModel("received_poNumber", "PO", 100));
        cm.add(new ColumnModel("received_date", "Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("received_posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("received_holding", "Holding", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("received_vendorCode", "Vendor Code", 100));
        
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
        cm.add(new ColumnModel("percentageList", "Percent List", 70));
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
        filters.add(new Filter("float", "percentageList"));
        recListTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        buttons.add(new ToolbarButton("View", "receivingViewButtonClick", "view_icon", "View The Selected Receivings Detail Page").setSingleRowAction(true));
        if (showNewWindowButton)
            buttons.add(new ToolbarButton("Open", "receivingOpenButtonClick", "newwin_icon", "Open Receiving List In A New Window"));
        
        t.setButtons(buttons);
        recListTable.setToolbar(t);
        
        recListTable.setDefaultSortCol("createTime");
        recListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryReceivingItemsExport");
        setExcelExportSheetName("ReceivingItems");
    }
    
    private void setupOrderListTable(){
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
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        buttons.add(new ToolbarButton("View", "orderViewButtonClick", "view_icon", "View The Selected Orders Detail Page").setSingleRowAction(true));
        if (showNewWindowButton)
            buttons.add(new ToolbarButton("Open", "orderOpenButtonClick", "newwin_icon", "Open Orders List In A New Window"));
        
        t.setButtons(buttons);
        orderListTable.setToolbar(t);
        
        orderListTable.setDefaultSortCol("createTime");
        orderListTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryOrderItemsExport");
        setExcelExportSheetName("Order Items");
    }
    
    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
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
    
    public String getTitle(){
        return title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }

    public AmazonData getAmazonData() {
        return amazonData;
    }

    public void setAmazonData(AmazonData amazonData) {
        this.amazonData = amazonData;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Table getRecListTable() {
        return recListTable;
    }

    public void setRecListTable(Table recListTable) {
        this.recListTable = recListTable;
    }

    public Table getOrderListTable() {
        return orderListTable;
    }

    public void setOrderListTable(Table orderListTable) {
        this.orderListTable = orderListTable;
    }

    public List<CustomerOrder> getLatestOrders() {
        return latestOrders;
    }

    public void setLatestOrders(List<CustomerOrder> latestOrders) {
        this.latestOrders = latestOrders;
    }

    public List<Received> getLatestReceived() {
        return latestReceived;
    }

    public void setLatestReceived(List<Received> latestReceived) {
        this.latestReceived = latestReceived;
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
    
    public Boolean getIncludeBell() {
        return includeBell;
    }

    public void setIncludeBell(Boolean includeBell) {
        this.includeBell = includeBell;
    }

    public Boolean getIncludeRestricted() {
        return includeRestricted;
    }

    public void setIncludeRestricted(Boolean includeRestricted) {
        this.includeRestricted = includeRestricted;
    }

    public Boolean getShowNewWindowButton() {
        return showNewWindowButton;
    }

    public void setShowNewWindowButton(Boolean showNewWindowButton) {
        this.showNewWindowButton = showNewWindowButton;
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

    public void setPrereceivingQuantity(Integer preReceivingQuantity){
        this.preReceivingQuantity = preReceivingQuantity;
    }
    
    public Integer getPrereceivingQuantity(){
        return this.preReceivingQuantity;
    }
    
}
