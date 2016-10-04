package com.bc.actions.bookcountry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//import jxl.Sheet;
//import jxl.Workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.AmazonLookup;
import com.bc.actions.BaseAction;
import com.bc.dao.DaoResults;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.ReceivingSessionLocal;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.*;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import com.bc.util.Timing;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import org.hibernate.criterion.Restrictions;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="upload", location="/WEB-INF/jsp/bookcountry/receiving/item/upload.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/receiving/item/crud.jsp"),
    @Result(name="fastrec", location="/WEB-INF/jsp/bookcountry/receiving/item/fastrec.jsp"),
    @Result(name="fastrechistory", location="/WEB-INF/jsp/bookcountry/receiving/item/fastrechistory.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/receiving/item/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class ReceivingItemAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ReceivingItemAction.class);

    private static HashSet<String> coverTypes = new HashSet<String>();
    
    private Table listTable;
    
    static {
        coverTypes.add("PAP");
        coverTypes.add("AUDIO");
        coverTypes.add("HC");
        coverTypes.add("AUDIO");
        coverTypes.add("NON");
        coverTypes.add("SPIRAL");
        coverTypes.add("BOARD");
        coverTypes.add("LEATHER");
    }
    
    private Received received;
    private ReceivedItem receivedItem;
    private InventoryItem inventoryItem;
    
    private String selectionIds;
    private Long skidTypeId;
    private Boolean skidLbs = false;
    private Boolean fastReceiving = false;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private static final int ISBN = 0;
    private static final int COND = 1;
    private static final int QUANTITY = 2;
    private static final int BIN = 3;
    private static final int COST = 4;
    private static final int COVER = 5;
    private static final int SELLING_PRICE = 6;
    private static final int TITLE = 7;
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String detail(){
        ReceivingSessionLocal rSession = getReceivingSession();
        receivedItem = rSession.findItemById(id);
        return "detail";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String create(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            received = rSession.findById(id, "vendor", "vendor.vendorSkidTypes");
        } catch (Exception e){
            logger.error("Could not get received vendors", e);
        }
        return "crud";
    }

    @ActionRole({"BcRecAdmin"})
    public String fastrec(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            received = rSession.findById(id);
        } catch (Exception e){
            logger.error("Could not get received", e);
        }
        return "fastrec";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String fastrecHistory(){
        setupFastRecHistory();
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            received = rSession.findById(id);
        } catch (Exception e){
            logger.error("Could not get received", e);
        }
        return "fastrechistory";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String fastrecHistoryData(){
        setupFastRecHistory();
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            received = rSession.findById(id);
            queryInput.addAndCriterion(Restrictions.eq("received", received));
            queryResults = new QueryResults(rSession.findAllFastRecItems(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not get fastrecHistoryData", e);
        }
        return "queryresults";
    }
    
    private void setupFastRecHistory(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        listTable.setPageSize(250);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("lastUpdateBy"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("lastUpdateBy", "User", 100));
        listTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "lastUpdateBy"));

        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryFastRecHistoryExport");
        setExcelExportSheetName("Fast Rec History");
        
    }    
    
    @ActionRole({"BcRecAdmin"})
    public String fastrecSubmit(){
        //Timing t = new Timing("receivedItemCreate");
        //t.start();
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            
            String isbn = receivedItem.getIsbn().trim();
            if (isbn != null && isbn.length() > 0) {
                receivedItem.setIsbn(IsbnUtil.getIsbn10(isbn));
                if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(isbn)))
                    receivedItem.setIsbn13(IsbnUtil.getIsbn13(isbn));
            }
            
            boolean failedUpdates = false;
            for (int i = 0; i < 5; i++){
                try {
                    rSession.createFromFastRec(id, receivedItem);
                    failedUpdates = false;
                    break;
                } catch (Exception e){
                    if ("noexist".equals(e.getMessage())){
                        setSuccess(false);
                        setMessage("This Receiving no longer exists, it was deleted.");
                        return "status";
                    } else if ("posted".equals(e.getMessage())){
                        setSuccess(false);
                        setMessage("This Receiving has been posted, refresh the page.");
                        return "status";
                    } else if ("noamazondata".equals(e.getMessage())){
                        setSuccess(false);
                        setMessage("noamazondata");
                        return "status";
                    }
                    failedUpdates = true;
                    logger.error("Failed update on rec item "+i, e);
                }
            }
            if (failedUpdates){
                setSuccess(false);
                setMessage("systemerror");
                return "status";
            }
        } catch (Exception e){
            setSuccess(false);
            setMessage("systemerror");
            return "status";
        }
        
        setSuccess(true);
        //t.stop();
        return "status";
    }
    
    
    @ActionRole({"BcRecAdmin"})
    public String createSubmit(){
        //Timing t = new Timing("receivedItemCreate");
        //t.start();
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            received = rSession.findById(id);
            if (received == null){
                setSuccess(false);
                setMessage("This Receiving has been removed from the system.");
                return "status";
            }
            if (received.getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            String isbn = receivedItem.getIsbn().trim();
            if (isbn != null && isbn.length() > 0) {
                receivedItem.setIsbn(IsbnUtil.getIsbn10(isbn));
                if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(isbn)))
                    receivedItem.setIsbn13(IsbnUtil.getIsbn13(isbn));
            }
            
            // see if this received item is already on the receiving, just update in that case
            boolean failedUpdates = false;
            for (int i = 0; i < 5; i++){
                try {
                    QueryInput qi = new QueryInput();
                    qi.addAndCriterion(Restrictions.eq("isbn", receivedItem.getIsbn()));
                    qi.addAndCriterion(Restrictions.eq("cond", receivedItem.getCond()));
                    qi.addAndCriterion(Restrictions.eq("received", received));
                    DaoResults results = rSession.findAllItems(qi);
                    if (results.getData() != null && results.getData().size() > 0){
                        ReceivedItem toUpdate = (ReceivedItem)results.getData().get(0);
                        toUpdate.setPreQuantity(toUpdate.getQuantity());
                        toUpdate.setQuantity(toUpdate.getQuantity()+receivedItem.getQuantity());
                        rSession.updateWithLifo(toUpdate, received.getId());
                        failedUpdates = false;
                        setSuccess(true);
                        //t.stop();
                        return "status";
                    }
                } catch (Exception e){
                    failedUpdates = true;
                    logger.error("Failed update on rec item, waiting");
                    try {Thread.sleep(100);} catch (Exception ex){}
                }
            }
            if (failedUpdates){
                setSuccess(false);
                setMessage("System error, could not update, try again.");
                return "status";
            }
            
            receivedItem.setReceived(received);
            receivedItem.setDate(received.getPoDate());
            receivedItem.setAvailable(receivedItem.getQuantity());
            receivedItem.setPoNumber(received.getPoNumber());
            receivedItem.setType("Pieces");
            // handle skid stuff
            if (receivedItem.getSkid()){
                receivedItem.setType("Skid");
                if (skidLbs){
                    receivedItem.setType("Lbs");
                }
                if (receivedItem.getSkidPieceCost() != null && receivedItem.getSkidPieceCount() != null){
                    receivedItem.setCost(receivedItem.getSkidPieceCost()*receivedItem.getSkidPieceCount());
                }
                if (receivedItem.getSkidPiecePrice() != null && receivedItem.getSkidPieceCount() != null){
                    receivedItem.setSellPrice(receivedItem.getSkidPiecePrice()*receivedItem.getSkidPieceCount());
                }
            }
            
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            InventoryItem ii = iiSession.findByIsbnCond(receivedItem.getIsbn(), receivedItem.getCond());
            if (ii == null){
                // create a new inventory item
                ii = new InventoryItem();
                ii.setIsbn(receivedItem.getIsbn());
                if (IsbnUtil.isValid10(ii.getIsbn())){
                    ii.setIsbn10(ii.getIsbn());
                }
                ii.setIsbn13(receivedItem.getIsbn13());
                ii.setCond(receivedItem.getCond());
                ii.setBin(receivedItem.getBin());
                if (receivedItem.getSellPrice() != null) ii.setSellingPrice(receivedItem.getSellPrice().floatValue());
                ii.setSkid(receivedItem.getSkid());
                ii.setBellbook(receivedItem.getBellbook());
                ii.setHe(receivedItem.getHigherEducation());
                ii.setRestricted(receivedItem.getRestricted());
                ii.setSellPricePercentList(receivedItem.getPercentageList());
                ii.setListPrice(receivedItem.getListPrice());
                ii.setCover(receivedItem.getCoverType());
                ii.setOnhand(0);
                ii.setTitle(receivedItem.getTitle());
                AmazonLookup.getInstance().lookupData(ii, true);
                if (!fastReceiving){
                    List<String> cats = AmazonLookup.getInstance().lookupCategories(ii.getIsbn());
                    if (cats.size() > 0) ii.setCategory1(cats.get(0));
                    if (cats.size() > 1) ii.setCategory2(cats.get(1));
                    if (cats.size() > 2) ii.setCategory3(cats.get(2));
                    if (cats.size() > 3) ii.setCategory4(cats.get(3));
                } else if (fastReceiving && !ii.getAmazonDataLoaded()){
                    setSuccess(false);
                    setMessage("noamazondata");
                    return "status";
                }
                
                iiSession.create(ii);
            } else  if (!receivedItem.getReceived().getHolding()) {
                ii.setBellbook(receivedItem.getBellbook());
                ii.setHe(receivedItem.getHigherEducation());
                ii.setRestricted(receivedItem.getRestricted());
                ii.setCover(receivedItem.getCoverType());
                ii.setBin(receivedItem.getBin());
                iiSession.update(ii);
            }
            receivedItem.setTitle(ii.getTitle());
            receivedItem.setInventoryItem(ii);
            rSession.create(receivedItem);
            rSession.recalculateReceived(received.getId());
            
            if (!receivedItem.getReceived().getHolding()) {
            	LifoSessionLocal lifoSession = getLifoSession();
            	lifoSession.createReceivedItem(receivedItem, ii.getId());
            }
            
            setSuccess(true);
            //t.stop();
        } catch (Exception e){
            logger.error("Could not create the received item", e);
            setSuccess(false);
            if (fastReceiving){
                setMessage("systemerror");
            } else {
                setMessage("Could not create the received item, there was a system error.");
            }
        }
        
        return "status";
    }

   
    @ActionRole({"BcRecAdmin"})
    public String editSubmit(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            ReceivedItem dbri = rSession.findItemById(receivedItem.getId(), "received");
            
            if (dbri.getReceived().getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            dbri.setPreQuantity(dbri.getQuantity());
            dbri.setQuantity(receivedItem.getQuantity());
            dbri.setAvailable(dbri.getQuantity());
            dbri.setOrderedQuantity(receivedItem.getOrderedQuantity());
            dbri.setPercentageList(receivedItem.getPercentageList());
            dbri.setCostPerLb(receivedItem.getCostPerLb());
            dbri.setCost(receivedItem.getCost());
            boolean titleEquals = true;
            if (receivedItem.getTitle() != null) titleEquals = receivedItem.getTitle().equals(dbri.getTitle());
            dbri.setTitle(receivedItem.getTitle());
            dbri.setBin(receivedItem.getBin());
            dbri.setListPrice(receivedItem.getListPrice());
            dbri.setSellPrice(receivedItem.getSellPrice());
            dbri.setCoverType(receivedItem.getCoverType());
            dbri.setBellbook(receivedItem.getBellbook());
            dbri.setBreakroom(receivedItem.getBreakroom());
            dbri.setHigherEducation(receivedItem.getHigherEducation());
            
            if (!dbri.getReceived().getHolding()) {
	            InventoryItemSessionLocal iiSession = getInventoryItemSession();
	            InventoryItem ii = iiSession.findByIsbnCond(dbri.getIsbn(), dbri.getCond());
	            if (ii != null){
	                ii.setBellbook(receivedItem.getBellbook());
	                ii.setHe(receivedItem.getHigherEducation());
	                ii.setRestricted(receivedItem.getRestricted());
	                ii.setCover(receivedItem.getCoverType());
	                ii.setBin(receivedItem.getBin());
	                if (!titleEquals) ii.setTitle(receivedItem.getTitle());
	                if (ii.getListPrice() == null && dbri.getListPrice() != null) ii.setListPrice(dbri.getListPrice());
	                iiSession.update(ii);
	            }
            }
            
            rSession.update(dbri);
            
            if (!dbri.getReceived().getHolding()) {
	            LifoSessionLocal lifoSession = getLifoSession();
	            lifoSession.updateReceivedItem(dbri);
            }

            rSession.recalculateReceived(dbri.getReceived().getId());

            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update received item by id: "+receivedItem.getId(), e);
            setSuccess(false);
            setMessage("Could not update the item, there was a sytem error.");
        }
        return "status";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String edit(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            receivedItem = rSession.findItemById(id, "inventoryItem", "received");
            inventoryItem = receivedItem.getInventoryItem();
            received = rSession.findById(receivedItem.getReceived().getId(), "vendor", "vendor.vendorSkidTypes");
        } catch (Exception e){
            logger.error("Could not edit: "+id, e);
        }
        return "crud";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String delete(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            
            if(selectionIds != null) {
                String[] stringIds = selectionIds.split(",");
                Long recId = null;
                for(String theId : stringIds) {
                    Long idl = new Long(theId);
                    if (recId == null){
                        receivedItem = rSession.findItemById(idl, "received");
                        recId = receivedItem.getReceived().getId();
                        if (receivedItem.getReceived().getPosted()){
                            setSuccess(false);
                            setMessage("This Receiving has been Posted, refresh the page.");
                            return "status";
                        }
                    } else {
                        receivedItem = rSession.findItemById(idl);
                    }
                    if (receivedItem == null){
                        continue;
                    }
                    
                    rSession.deleteItem(idl);
                }
                rSession.recalculateReceived(recId);
                
            } 
            
            setSuccess(true);
            setMessage("Deleted items");
        } catch (Exception e){
            logger.error("Could not delete received items", e);
            setSuccess(false);
            setMessage("Could not delete the items, there was a sytem error.");
        }
        return "status";
    }

    @ActionRole({"BcRecAdmin"})
    public String uploadPage(){
        return "upload";
    }
    
    @ActionRole({"BcRecAdmin"})
//    public String upload(){
//        if (upload == null){
//            setSuccess(false);
//            setMessage("You must provide a file to upload.");
//            return "status";
//        }
//        
//        // Check File Extension
//        int place = uploadFileName.lastIndexOf( '.' );
//        if ( place >= 0 ) {           
//            String ext = uploadFileName.substring( place + 1 );
//            if(!ext.toLowerCase().equals("xlsx")) {
//                setSuccess(false);
//                setMessage("Only xlsx files are supported at this time.");
//                return "status";
//            }
//        }
//        
//        ReceivingSessionLocal rSession = getReceivingSession();
//        received = rSession.findById(id);
//        if (received == null){
//            setSuccess(false);
//            setMessage("Could not find the receiving to add the items to, refresh the page.");
//            return "status";
//        }
//        if (received.getPosted()){
//            setSuccess(false);
//            setMessage("This Receiving has been Posted, refresh the page.");
//            return "status";
//        }
//        
//        logger.info("Importing receiveing items from: "+uploadFileName+" into received: "+received.getPoNumber()+" id: "+received.getId()+" by user: "+getUserName());
//        
//        try {
//            Workbook workbook = null;
//            try {
//                workbook = Workbook.getWorkbook(upload);
//            } catch(Exception e) {
//                logger.error("Unsupported file type.", e);
//                setSuccess(false);
//                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
//                return "status";
//            }
//            
//            Sheet s = workbook.getSheet(0);
//            
//            int numRows = s.getRows();
//            if(numRows <= 1) {
//                setSuccess(false);
//                setMessage("The uploaded file contained no items to upload.");
//                return "status";
//            }
//            
//            // Check for header row and hidden columns
//            try {
//                
//                // Check for hidden columns
//                for(int i = ISBN; i <= SELLING_PRICE; i++) {
//                    if(s.getCell(i, 0).isHidden()) {
//                        setSuccess(false);
//                        setMessage("There are hidden columns in the first 8 columns.  Not supported.");
//                        return "status";
//                    }
//                }
//
//                if (!s.getCell(ISBN,0).getContents().startsWith("ISBN") || 
//                    !s.getCell(COND,0).getContents().startsWith("Condition") || 
//                    !s.getCell(QUANTITY,0).getContents().startsWith("Quantity") || 
//                    !s.getCell(BIN,0).getContents().startsWith("Bin") || 
//                    !s.getCell(COST,0).getContents().startsWith("Cost") || 
//                    !s.getCell(COVER,0).getContents().startsWith("Cover") || 
//                    !s.getCell(SELLING_PRICE,0).getContents().startsWith("Selling Price"))
//                {
//                    setSuccess(false);
//                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Quantity, Bin, Cost, Cover, Selling Price, Title.");
//                    return "status";
//                }
//
//                
//            } catch (ArrayIndexOutOfBoundsException aie) {
//                setSuccess(false);
//                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Quantity, Bin, Cost, Cover, Selling Price, Title.");
//                return "status";                                
//            }
//            
//            // Process each row.  Skip header row
//            InventoryItemSessionLocal iiSession = getInventoryItemSession();
//            List<ReceivedItem> items = new ArrayList<ReceivedItem>();
//            HashMap<String, ReceivedItem> itemMap = new HashMap<String, ReceivedItem>();
//            List<Long> ids = new ArrayList<Long>();
//            logger.info("starting to import the received items");
//            for(int row = 1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                ReceivedItem item = new ReceivedItem();
//                String isbn = s.getCell(ISBN, row).getContents();
//                if (isbn != null && isbn.length() > 0) {
//                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
//                    if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(isbn)))
//                        item.setIsbn13(IsbnUtil.getIsbn13(isbn));
//                    if (received.getHolding()){
//                        item.setIsbn(item.getIsbn().toUpperCase());
//                    }
//                } else {
//                    continue;
//                }          
//                String cond = s.getCell(COND, row).getContents();
//                if (cond != null) cond = cond.toLowerCase();
//                if ("hurt".equals(cond) || "overstock".equals(cond) || "unjacketed".equals(cond)){
//                    item.setCond(cond);
//                }                
//                if (item.getCond() == null){
//                    item.setCond("hurt");
//                }
//                
//                
//                String quantity = s.getCell(QUANTITY, row).getContents();
//                if (quantity != null && quantity.length() > 0) {
//                    try {
//                        item.setQuantity(Integer.parseInt(quantity));
//                        item.setAvailable(item.getQuantity());
//                    } catch (NumberFormatException nfe){
//                        // could not set quantity
//                    }
//                }
//                String bin = s.getCell(BIN, row).getContents();
//                if (bin != null && bin.length() > 0) {
//                    item.setBin(bin);
//                }
//                
//                String title = s.getCell(TITLE, row).getContents();
//                if (title != null && title.length() > 0) {
//                    item.setTitle(title);
//                }
//                
//                String cost = s.getCell(COST, row).getContents();
//                if (cost != null && cost.length() > 0) {
//                    try {
//                        item.setCost(Float.parseFloat(cost));
//                    } catch (NumberFormatException nfe){
//                    }
//                }
//                String sellingPrice = s.getCell(SELLING_PRICE, row).getContents();
//                if (sellingPrice != null && sellingPrice.length() > 0) {
//                    try {
//                        item.setSellPrice(Float.parseFloat(sellingPrice));
//                    } catch (NumberFormatException nfe){
//                    }
//                }
//                
//                String key = item.getIsbn13()+"-"+item.getCond();
//                if (itemMap.containsKey(key)){
//                	ReceivedItem mapItem = itemMap.get(key);
//                	mapItem.setQuantity(mapItem.getQuantity()+item.getQuantity());
//                	mapItem.setAvailable(mapItem.getQuantity());
//                	continue;
//                }
//                
//                String cover = s.getCell(COVER, row).getContents();
//                
//                InventoryItem ii = iiSession.findByIsbnCond(item.getIsbn(), item.getCond());
//                if (ii == null){
//                    logger.info("Creating new inventory item for the isbn: "+item.getIsbn()+" cond: "+item.getCond());
//                    ii = new InventoryItem();
//                    ii.setCond(item.getCond());
//                    ii.setIsbn(item.getIsbn());
//                    if (IsbnUtil.isValid10(ii.getIsbn())){
//                        ii.setIsbn10(ii.getIsbn());
//                    }
//                    ii.setCover(cover);
//                    ii.setIsbn13(item.getIsbn13());
//                    ii.setBin(item.getBin());
//                    ii.setSellingPrice(item.getSellPrice());
//                    ii.setReceivedPrice(item.getCost());
//                    try {
//                        AmazonLookup.getInstance().lookupData(ii, true);
//                        List<String> cats = AmazonLookup.getInstance().lookupCategories(ii.getIsbn());
//                        if (cats.size() > 0) ii.setCategory1(cats.get(0));
//                        if (cats.size() > 1) ii.setCategory2(cats.get(1));
//                        if (cats.size() > 2) ii.setCategory3(cats.get(2));
//                        if (cats.size() > 3) ii.setCategory4(cats.get(3));
//                    } catch (Throwable t){
//                        // making sure we don't fail to create the inventory item and continue on
//                    } finally {
//                        // waiting 1 second - throttling
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception e){}
//                    }
//                    if (ii.getTitle() == null || ii.getTitle().length() == 0){
//                    	ii.setTitle(item.getTitle());
//                    }
//                    iiSession.create(ii);
//                }
//                if (item.getTitle() != null && item.getTitle().length() > 0 && ii.getTitle() == null || ii.getTitle().length() == 0){
//                	ii.setTitle(item.getTitle());
//                	iiSession.update(ii);
//                }
//                // making sure we get the bin of the item
//                if (item.getBin() == null || item.getBin().length() == 0)
//                    item.setBin(ii.getBin());
//
//                item.setListPrice(ii.getListPrice());
//                if (!item.isUpdated("cost"))
//                	item.setCost(ii.getCost());
//                if (!item.isUpdated("sellPrice"))
//                    item.setSellPrice(ii.getSellingPrice());
//                item.setCoverType(ii.getCover());
//                item.setType("Pieces");
//
//                item.setReceived(received);
//                item.setPoNumber(received.getPoNumber());
//
//                if (item.getTitle() == null || item.getTitle().length() == 0)
//                	item.setTitle(ii.getTitle());
//                item.setInventoryItem(ii);
//
//            	items.add(item);
//            	itemMap.put(key, item);
//            	
//                ids.add(ii.getId());
//            }
//            logger.info("finished the excel processing");
//
//            if (items.size() > 0){
//                try {
//                    logger.info("update existing items");
//                    List<ReceivedItem> newItems = rSession.updateWithLifo(items, received.getId());
//                    logger.info("creating any new items");
//                    setSuccess(true);
//                    setMessage("Uploaded the receiving items.");
//                    if (newItems.size() > 0){
//                        Timing t = new Timing("Add Received Items");
//                        t.start();
//                        if (rSession.addReceivedItems(newItems)){
//                            t.stop();
//
//                            Timing lt = new Timing("Lifo Creates");
//                            lt.start();
//                            LifoSessionLocal lifoSession = getLifoSession();
//                            lifoSession.createReceivedItems(newItems, ids);
//                            lt.stop();
//
//                            setSuccess(true);
//                            setMessage("Uploaded the receiving items.");
//                        } else {
//                            setSuccess(false);
//                            setMessage("There was a system error and we could not process the upload file");
//                        }
//                    }
//                } catch (Throwable t){
//                    logger.error("Error processing lifo or adding new rec items", t);
//                }
//                logger.info("recalculating the received");
//                rSession.recalculateReceived(received.getId());
//            }
//            
//        } catch (Exception e){
//            logger.error("Could not upload receiving items", e);
//            setSuccess(false);
//            setMessage("Could not upload the receiving items, there was a system error.");
//        }
//        return "status";        
//    }
    
    public String upload(){
        if (upload == null){
            setSuccess(false);
            setMessage("You must provide a file to upload.");
            return "status";
        }
        
        // Check File Extension
        int place = uploadFileName.lastIndexOf( '.' );
        if ( place >= 0 ) {           
            String ext = uploadFileName.substring( place + 1 );
            if(!ext.toLowerCase().equals("xlsx")) {
                setSuccess(false);
                setMessage("Only xlsx files are supported at this time.");
                return "status";
            }
        }
        
        ReceivingSessionLocal rSession = getReceivingSession();
        received = rSession.findById(id);
        if (received == null){
            setSuccess(false);
            setMessage("Could not find the receiving to add the items to, refresh the page.");
            return "status";
        }
        if (received.getPosted()){
            setSuccess(false);
            setMessage("This Receiving has been Posted, refresh the page.");
            return "status";
        }
        
        logger.info("Importing receiveing items from: "+uploadFileName+" into received: "+received.getPoNumber()+" id: "+received.getId()+" by user: "+getUserName());
        
        try {
            XSSFWorkbook workbook = null;
            FileInputStream fis = new FileInputStream(upload);
            try {
                workbook = new XSSFWorkbook(fis);
            } catch(Exception e) {
                logger.error("Unsupported file type.", e);
                setSuccess(false);
                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
                return "status";
            }
            
            XSSFSheet s = workbook.getSheetAt(0);
            
            int numRows = s.getPhysicalNumberOfRows() + 1;
            if(numRows <= 1) {
                setSuccess(false);
                setMessage("The uploaded file contained no items to upload.");
                return "status";
            }
            
            // Check for header row and hidden columns
            try {
                
                // Check for hidden columns
                for(int i = ISBN; i <= SELLING_PRICE; i++) {
                    if(s.isColumnHidden(i)) {
                        setSuccess(false);
                        setMessage("There are hidden columns in the first 8 columns.  Not supported.");
                        return "status";
                    }
                }

                Row r = s.getRow(0);
                if (!getCellValue(r.getCell(ISBN)).startsWith("ISBN") || 
                    !getCellValue(r.getCell(COND)).startsWith("Condition") || 
                    !getCellValue(r.getCell(QUANTITY)).startsWith("Quantity") || 
                    !getCellValue(r.getCell(BIN)).startsWith("Bin") || 
                    !getCellValue(r.getCell(COST)).startsWith("Cost") || 
                    !getCellValue(r.getCell(COVER)).startsWith("Cover") || 
                    !getCellValue(r.getCell(SELLING_PRICE)).startsWith("Selling Price"))
                {
                    setSuccess(false);
                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Quantity, Bin, Cost, Cover, Selling Price, Title.");
                    return "status";
                }

                
            } catch (ArrayIndexOutOfBoundsException aie) {
                setSuccess(false);
                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Quantity, Bin, Cost, Cover, Selling Price, Title.");
                return "status";                                
            }
            
            // Process each row.  Skip header row
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            List<ReceivedItem> items = new ArrayList<ReceivedItem>();
            HashMap<String, ReceivedItem> itemMap = new HashMap<String, ReceivedItem>();
            List<Long> ids = new ArrayList<Long>();
            logger.info("starting to import the received " + (numRows - 1) + " items");
            for(int row = 1; row < numRows; row++) {
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getZeroHeight()) {
                    logger.info("Skipping hidden row: " + row);
                    continue;
                }
                
                ReceivedItem item = new ReceivedItem();
                String isbn = getCellValue(r.getCell(ISBN));
                //logger.info(isbn);
                
                if (isbn != null && isbn.length() > 0) {
                    isbn = fixIsbn(isbn);
                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
                    if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(isbn)))
                        item.setIsbn13(IsbnUtil.getIsbn13(isbn));
                    if (received.getHolding()){
                        item.setIsbn(item.getIsbn().toUpperCase());
                    }
                } else {
                    logger.info("skipping item due to bad isbn");
                    continue;
                }          
                String cond = r.getCell(COND).getStringCellValue();
                if (cond != null) cond = cond.toLowerCase();
                if ("hurt".equals(cond) || "overstock".equals(cond) || "unjacketed".equals(cond)){
                    item.setCond(cond);
                }                
                if (item.getCond() == null){
                    item.setCond("hurt");
                }
                
                
                String quantity = getCellValue(r.getCell(QUANTITY));
                if (quantity != null && quantity.length() > 0) {
                    try {
                        item.setQuantity(Integer.parseInt(quantity));
                        item.setAvailable(item.getQuantity());
                    } catch (NumberFormatException nfe){
                        // could not set quantity
                    }
                }
                String bin = getCellValue(r.getCell(BIN));
                if (bin != null && bin.length() > 0) {
                    item.setBin(bin);
                }
                
                String title = getCellValue(r.getCell(TITLE));
                if (title != null && title.length() > 0) {
                    item.setTitle(title);
                }
                
                String cost = getCellValue(r.getCell(COST));
                if (cost != null && cost.length() > 0) {
                    try {
                        item.setCost(Float.parseFloat(cost));
                    } catch (NumberFormatException nfe){
                    }
                }
                String sellingPrice = getCellValue(r.getCell(SELLING_PRICE));
                if (sellingPrice != null && sellingPrice.length() > 0) {
                    try {
                        item.setSellPrice(Float.parseFloat(sellingPrice));
                    } catch (NumberFormatException nfe){
                    }
                }
                
                String key = item.getIsbn13()+"-"+item.getCond();
                if (itemMap.containsKey(key)){
                	ReceivedItem mapItem = itemMap.get(key);
                	mapItem.setQuantity(mapItem.getQuantity()+item.getQuantity());
                	mapItem.setAvailable(mapItem.getQuantity());
                        logger.info("skipping item due to containing key");
                	continue;
                }
                
                String cover = getCellValue(r.getCell(COVER));
                
                InventoryItem ii = iiSession.findByIsbnCond(item.getIsbn(), item.getCond());
                if (ii == null){
                    logger.info("Creating new inventory item for the isbn: "+item.getIsbn()+" cond: "+item.getCond());
                    ii = new InventoryItem();
                    ii.setCond(item.getCond());
                    ii.setIsbn(item.getIsbn());
                    if (IsbnUtil.isValid10(ii.getIsbn())){
                        ii.setIsbn10(ii.getIsbn());
                    }
                    ii.setCover(cover);
                    ii.setIsbn13(item.getIsbn13());
                    ii.setBin(item.getBin());
                    ii.setSellingPrice(item.getSellPrice());
                    ii.setReceivedPrice(item.getCost());
                    try {
                        AmazonLookup.getInstance().lookupData(ii, true);
                        List<String> cats = AmazonLookup.getInstance().lookupCategories(ii.getIsbn());
                        if (cats.size() > 0) ii.setCategory1(cats.get(0));
                        if (cats.size() > 1) ii.setCategory2(cats.get(1));
                        if (cats.size() > 2) ii.setCategory3(cats.get(2));
                        if (cats.size() > 3) ii.setCategory4(cats.get(3));
                    } catch (Throwable t){
                        // making sure we don't fail to create the inventory item and continue on
                    } finally {
                        // waiting 1 second - throttling
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e){}
                    }
                    if (ii.getTitle() == null || ii.getTitle().length() == 0){
                    	ii.setTitle(item.getTitle());
                    }
                    iiSession.create(ii);
                }
                if (item.getTitle() != null && item.getTitle().length() > 0 && ii.getTitle() == null || ii.getTitle().length() == 0){
                	ii.setTitle(item.getTitle());
                	iiSession.update(ii);
                }
                // making sure we get the bin of the item
                if (item.getBin() == null || item.getBin().length() == 0)
                    item.setBin(ii.getBin());

                item.setListPrice(ii.getListPrice());
                if (!item.isUpdated("cost"))
                	item.setCost(ii.getCost());
                if (!item.isUpdated("sellPrice"))
                    item.setSellPrice(ii.getSellingPrice());
                item.setCoverType(ii.getCover());
                item.setType("Pieces");

                item.setReceived(received);
                item.setPoNumber(received.getPoNumber());

                if (item.getTitle() == null || item.getTitle().length() == 0)
                    item.setTitle(ii.getTitle());
                item.setInventoryItem(ii);

            	items.add(item);
            	itemMap.put(key, item);
            	
                ids.add(ii.getId());
            }
            logger.info("finished the excel processing");

            if (items.size() > 0){
                try {
                    logger.info("update existing items");
                    List<ReceivedItem> newItems = rSession.updateWithLifo(items, received.getId());
                    logger.info("creating any new items");
                    setSuccess(true);
                    setMessage("Uploaded the receiving items.");
                    if (newItems.size() > 0){
                        Timing t = new Timing("Add Received Items");
                        t.start();
                        if (rSession.addReceivedItems(newItems)){
                            t.stop();

                            Timing lt = new Timing("Lifo Creates");
                            lt.start();
                            LifoSessionLocal lifoSession = getLifoSession();
                            lifoSession.createReceivedItems(newItems, ids);
                            lt.stop();

                            setSuccess(true);
                            setMessage("Uploaded the receiving items.");
                        } else {
                            setSuccess(false);
                            setMessage("There was a system error and we could not process the upload file");
                        }
                    }
                } catch (Throwable t){
                    logger.error("Error processing lifo or adding new rec items", t);
                }
                logger.info("recalculating the received");
                rSession.recalculateReceived(received.getId());
            }
            
        } catch (Exception e){
            logger.error("Could not upload receiving items", e);
            setSuccess(false);
            setMessage("Could not upload the receiving items, there was a system error.");
        }
        return "status";        
    }
    
    

    public Received getReceived() {
        return received;
    }

    public void setReceived(Received received) {
        this.received = received;
    }

    public ReceivedItem getReceivedItem() {
        return receivedItem;
    }

    public void setReceivedItem(ReceivedItem receivedItem) {
        this.receivedItem = receivedItem;
    }

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    public String getUploadContentType() {
        return uploadContentType;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getSelectionIds() {
        return selectionIds;
    }

    public void setSelectionIds(String selectionIds) {
        this.selectionIds = selectionIds;
    }

    public Long getSkidTypeId() {
        return skidTypeId;
    }

    public void setSkidTypeId(Long skidTypeId) {
        this.skidTypeId = skidTypeId;
    }

    public Boolean getSkidLbs() {
        return skidLbs;
    }

    public void setSkidLbs(Boolean skidLbs) {
        this.skidLbs = skidLbs;
    }

    public Boolean getFastReceiving() {
        return fastReceiving;
    }

    public void setFastReceiving(Boolean fastReceiving) {
        this.fastReceiving = fastReceiving;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    private String getCellValue(Cell cell){
        if (cell!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell))
            {
                return sdf.format(cell.getDateCellValue());
            }
            
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_NUMERIC:
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case Cell.CELL_TYPE_BLANK:
                    return "";
                case Cell.CELL_TYPE_ERROR:
                    return "";

                // CELL_TYPE_FORMULA will never occur
                case Cell.CELL_TYPE_FORMULA: 
                    return "";
            }
        }
        return "";
    }
    
    private String fixIsbn(String isbn){
        if (isbn.length() < 10){
            while (isbn.length() < 10){
                isbn = "0" + isbn;
            }
        }
        return isbn;
    }
    
}
