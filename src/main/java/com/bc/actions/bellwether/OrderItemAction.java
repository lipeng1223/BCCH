package com.bc.actions.bellwether;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.hibernate.criterion.Restrictions;

import com.bc.dao.DaoResults;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import com.bc.orm.*;
import com.bc.struts.QueryInput;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="crud", location="/WEB-INF/jsp/bellwether/orders/item/crud.jsp"),
    @Result(name="uploadstatus", location="/WEB-INF/jsp/bellwether/orders/item/uploadstatus.jsp"),
    @Result(name="shipped", location="/WEB-INF/jsp/bellwether/orders/item/shipped.jsp"),
    @Result(name="uploadPage", location="/WEB-INF/jsp/bellwether/orders/item/upload.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bellwether/orders/item/detail.jsp"),
    @Result(name="json", location="/WEB-INF/jsp/jsonresults.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class OrderItemAction extends InventoryAction {
    
    private final Logger logger = Logger.getLogger(OrderItemAction.class);

    private BellOrder order;
    private BellOrderItem orderItem;
    private BellInventory bellInventory;
    
    private String selectionIds;
    
    private Integer shipped;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private String isbnCol;
    private String qtyCol;
    private String priceCol;
    private Integer startRow;
    private Boolean hasImportErrors = false;
    private String importErrors = "";
    
    private Boolean loadNext = false;
    
    private int ISBN = 0;
    private int DISPLAY_ISBN = 2; 
    private int QUANTITY = 3;
    private int PRICE = 4;
    private int DISCOUNT = 5;
    private int TITLE = 6;
    private int CREDIT = 7;
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String detail(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        orderItem = oSession.findItemById(id, "BellInventory");
        return "detail";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String create(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        order = oSession.findById(id);
        //logger.info("order creditMemoType: "+order.getCreditMemoType());
        return "crud";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String createSubmit(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id);
            if (order == null){
                setSuccess(false);
                setMessage("This Order has been removed from the system.");
                return "status";
            }
            BellInventorySessionLocal iiSession = getBellInventorySession();
            BellInventory ii = iiSession.findByIsbn(orderItem.getIsbn().trim());
            if (ii == null){
                setSuccess(false);
                setMessage("There was no Item in Bell Inventory that matched the ISBN: "+orderItem.getIsbn());
                return "status";
            }
            BellOrderItem exists = oSession.findItemByIsbn(order, orderItem.getIsbn().trim());
            if (exists != null){
                exists.setQuantity(exists.getQuantity()+orderItem.getQuantity());
                exists.setPrice(orderItem.getPrice());
                oSession.update(exists);
                orderItem = exists;
            } else {
                if (orderItem.getDiscount() == null) orderItem.setDiscount(0F);
                orderItem.setExtended(0F);
                orderItem.setTotalPrice(BigDecimal.ZERO);
                orderItem.setBellOrder(order);
                orderItem.setBellInventory(ii);
                orderItem.setIsbn(ii.getIsbn());
                orderItem.setIsbn13(ii.getIsbn13());
                orderItem.setBin(ii.getBin());
                orderItem.setFilled(0);
                orderItem.setCredit(order.getCreditMemo());
                
                if (order.getCreditMemo()) orderItem.setCredit(true);
                
                /*
                if (order.getCreditMemo() && (orderItem.getCreditDamage() || orderItem.getCreditShortage())){
                    // negative price
                    if (orderItem.getPrice() > 0F)
                        orderItem.setPrice(-orderItem.getPrice());
                }
                */
                
                oSession.create(orderItem);
            }
            
            oSession.recalculateOrderItemTotals(orderItem.getId());
            iiSession.recalculateCommitted(ii.getId());
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not create the order item", e);
            setSuccess(false);
            setMessage("Could not create the order item, there was a system error.");
        }
        
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String shipped(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        orderItem = oSession.findItemById(id, "bellInventory");
        bellInventory = orderItem.getBellInventory();
        return "shipped";
    }
    @ActionRole({"BellOrderAdmin"})
    public String shippedSubmit(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            BellOrderItem dbItem = oSession.findItemById(id, "bellOrder");
            dbItem.setFilled(orderItem.getFilled());
            oSession.update(dbItem);
            oSession.recalculateOrderItemTotals(id);
            if (loadNext){
                // load the next one and send back json
                orderItem = oSession.findNextItem(getSort(), getDir(), getStart(), dbItem.getBellOrder(), "bellInventory");
                if (orderItem != null){
                    orderItem.setSuccess(true);
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                    setJsonResults(gson.toJson(orderItem));
                    return "json";
                } else {
                    setSuccess(false);
                    setMessage("There are no more items on this BellOrder.");
                    return "status";
                }
            }
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not set shipped bell order item", e);
            setSuccess(false);
            setMessage("Could not set shipped on the bell order item, there was a system error.");
        }
        
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String editSubmit(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            BellInventorySessionLocal iiSession = getBellInventorySession();
            BellOrderItem dboi = oSession.findItemById(orderItem.getId(), "bellInventory");
            
            dboi.setDisplayIsbn(orderItem.getDisplayIsbn());
            dboi.setPrice(orderItem.getPrice());
            dboi.setDiscount(orderItem.getDiscount());
            dboi.setQuantity(orderItem.getQuantity());
            dboi.setTitle(orderItem.getTitle());
            if (dboi.getDiscount() == null) dboi.setDiscount(0F);
            
            /*
            if (orderItem.getCreditDamage() || orderItem.getCreditShortage()){
                // negative price
                if (orderItem.getPrice() > 0F)
                    dboi.setPrice(-orderItem.getPrice());
            } else if (orderItem.getCreditRecNoBill() && orderItem.getPrice() < 0F) {
                // go back to positive
                dboi.setPrice(-orderItem.getPrice());
            }
            */
            
            oSession.update(dboi);
            
            oSession.recalculateOrderItemTotals(orderItem.getId());
            if (dboi.getBellInventory() != null){
                iiSession.recalculateCommitted(dboi.getBellInventory().getId());
            }
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update bell order item by id: "+orderItem.getId(), e);
            setSuccess(false);
            setMessage("Could not update the bell order item, there was a sytem error.");
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String edit(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            orderItem = oSession.findItemById(id, "bellInventory", "bellOrder");
            bellInventory = orderItem.getBellInventory();
        } catch (Exception e){
            logger.error("Could not edit: "+id, e);
        }
        return "crud";
    }

    @ActionRole({"BellOrderAdmin"})
    public String delete(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            
            if(selectionIds != null) {
                String[] stringIds = selectionIds.split(",");
                for(String theId : stringIds) {
                    Long idl = new Long(theId);
            
                    orderItem = oSession.findItemById(idl, "bellInventory", "bellOrder");
                    if (orderItem == null){
                        continue;
                    }
                    Long orderId = orderItem.getBellOrder().getId();
                    oSession.deleteItem(idl);
                    
                    oSession.recalculateOrderTotals(orderId);
                    BellInventorySessionLocal iiSession = getBellInventorySession();
                    iiSession.recalculateCommitted(orderItem.getBellInventory().getId());
                }
            }
            
            setSuccess(true);
            setMessage("Deleted items");
        } catch (Exception e){
            logger.error("Could not delete bell order items", e);
            setSuccess(false);
            setMessage("Could not delete the bell items, there was a sytem error.");
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String fixZero(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            order = oSession.findById(id);
            if (order == null){
                setSuccess(false);
                setMessage("Could not find the order to fix.");
                return "status";
            }
            
            QueryInput qi = new QueryInput();
            qi.addAndCriterion(Restrictions.eq("bellOrder", order));
            qi.addAndCriterion(Restrictions.eq("price", 0F));
            DaoResults dr = oSession.findAllItems(qi, "bellInventory");
            for (BellOrderItem coi : (List<BellOrderItem>)dr.getData()){
                if (coi.getBellInventory() != null && coi.getBellInventory().getSellPrice() > 0){
                    coi.setPrice(coi.getBellInventory().getSellPrice());
                    oSession.update(coi);
                }
            }
            // make sure we update the order totals
            oSession.recalculateAllOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not fix zero prices", e);
            setSuccess(false);
            setMessage("There was a system error and we could not fix the zero selling prices.");
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String shipMax(){
        try {
            BellOrderSessionLocal oSession = getBellOrderSession();
            BellInventorySessionLocal iiSession = getBellInventorySession();
            order = oSession.findById(id);
            if (order == null){
                setSuccess(false);
                setMessage("Could not find the order to ship max.");
                return "status";
            }
            
            QueryInput qi = new QueryInput();
            qi.addAndCriterion(Restrictions.eq("bellOrder", order));
            DaoResults dr = oSession.findAllItems(qi, "bellInventory");
            HashMap<Long, Integer> sofar = new HashMap<Long, Integer>();
            for (BellOrderItem coi : (List<BellOrderItem>)dr.getData()){
                if (coi.getBellInventory() == null){
                    coi.setFilled(coi.getQuantity());
                } else if (coi.getQuantity() < 0){
                    coi.setFilled(coi.getQuantity());
                } else {
                    int committed = coi.getBellInventory().getCommitted() - coi.getQuantity();
                    if (sofar.containsKey(coi.getBellInventory().getId())){
                        committed -= sofar.get(coi.getBellInventory().getId());
                        sofar.put(coi.getBellInventory().getId(), committed);
                    } else {
                        sofar.put(coi.getBellInventory().getId(), coi.getQuantity());
                    }
                    if (committed > coi.getBellInventory().getOnhand()){
                        coi.setFilled(0);
                    } else {
                        if (committed < 0){
                            committed = 0;
                        }
                        int max = coi.getBellInventory().getOnhand() - committed;
                        if (max > coi.getQuantity()){
                            max = coi.getQuantity();
                        }
                        int quantity = max;
                        if (max < 0){
                            quantity = coi.getQuantity()+max;
                            if (quantity < 0){
                                quantity = 0;
                            }
                        }
                        coi.setFilled(quantity);
                    }
                }
                oSession.update(coi);
                
                if (coi.getBellInventory() != null){
                    iiSession.recalculateCommitted(coi.getBellInventory().getId());
                }
            }
            // make sure we update the order totals
            oSession.recalculateAllOrderTotals(id);
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not ship max", e);
            setSuccess(false);
            setMessage("There was a system error and we could not ship the max for the order.");
        }
        return "status";
    }
    
    @ActionRole({"BellOrderAdmin"})
    public String uploadPage(){
        return "uploadPage";
    }
    
    @ActionRole({"BellOrderAdmin"})
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
//        BellOrderSessionLocal oSession = getBellOrderSession();
//        order = oSession.findById(id);
//        if (order == null){
//            setSuccess(false);
//            setMessage("Could not find the order to add the items to, refresh the page.");
//            return "status";
//        }
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
//            /*
//            try {
//                
//                // Check for hidden columns
//                for(int i = ISBN; i <= CREDIT; i++) {
//                    if(s.getCell(i, 0).isHidden()) {
//                        setSuccess(false);
//                        setMessage("There are hidden columns in the first 8 columns.  Not supported.");
//                        return "status";
//                    }
//                }
//
//                if (!s.getCell(ISBN,0).getContents().startsWith("ISBN") || 
//                    !s.getCell(COND,0).getContents().startsWith("Condition") || 
//                    !s.getCell(DISPLAY_ISBN,0).getContents().startsWith("Display ISBN") ||
//                    !s.getCell(QUANTITY,0).getContents().startsWith("Quantity") || 
//                    !s.getCell(PRICE,0).getContents().startsWith("Price") || 
//                    !s.getCell(DISCOUNT,0).getContents().startsWith("Discount") || 
//                    !s.getCell(TITLE,0).getContents().startsWith("Title") || 
//                    !s.getCell(CREDIT,0).getContents().startsWith("Credit"))
//                {
//                    setSuccess(false);
//                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting: 'ISBN', 'Condition', 'Display ISBN', 'Quantity', 'Price', 'Discount', 'Title', 'Credit'.");
//                    return "status";
//                }
//
//                
//            } catch (ArrayIndexOutOfBoundsException aie) {
//                setSuccess(false);
//                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting: 'ISBN', 'Condition', 'Display ISBN', 'Quantity', 'Price', 'Discount', 'Title', 'Credit'.");
//                return "status";                                
//            }
//            */
//            
//            ISBN = getColumnInt(isbnCol.toLowerCase());
//            QUANTITY = getColumnInt(qtyCol.toLowerCase());
//            PRICE = getColumnInt(priceCol.toLowerCase());
//            
//            logger.info("ISBN col: "+ISBN);
//            logger.info("QUANTITY col: "+QUANTITY);
//            logger.info("PRICE col: "+PRICE);
//            
//            // Process each row.  Skip header row
//            BellInventorySessionLocal iiSession = getBellInventorySession();
//            List<BellOrderItem> items = new ArrayList<BellOrderItem>();
//            List<Long> iiIds = new ArrayList<Long>();
//            for(int row = startRow-1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                BellOrderItem item = new BellOrderItem();
//                String isbn = s.getCell(ISBN, row).getContents();
//                if (isbn != null && isbn.length() > 0) {
//                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
//                    item.setIsbn13(IsbnUtil.getIsbn13(isbn));
//                } else {
//                    continue;
//                }
//                
//                BellInventory ii = iiSession.findByIsbn(item.getIsbn());
//                if (ii == null){
//                    hasImportErrors = true;
//                    StringBuilder sb = new StringBuilder("<tr><td style=\"padding-top:5px;padding-left:25px;\">");
//                    sb.append(row+1);
//                    sb.append("</td><td style=\"padding-top:5px;padding-left:25px;\">");
//                    sb.append(item.getIsbn());
//                    sb.append("</td></tr>");
//                    importErrors += sb.toString();
//                    logger.error("IMPORT: Could not find bell inventory item for the isbn: "+item.getIsbn());
//                    continue;
//                }
//                iiIds.add(ii.getId());
//                
//                /*
//                String di = s.getCell(DISPLAY_ISBN, row).getContents();
//                if (di != null && di.length() > 0) {
//                    item.setDisplayIsbn(di);
//                }
//                */
//                String quantity = s.getCell(QUANTITY, row).getContents();
//                if (quantity != null && quantity.length() > 0) {
//                    try {
//                        item.setQuantity(Integer.parseInt(quantity));
//                    } catch (NumberFormatException nfe){
//                        // could not set quantity
//                    }
//                }
//                try {
//                    String price = s.getCell(PRICE, row).getContents();
//                    if (price != null && price.length() > 0) {
//                        if (price.startsWith("$")){
//                            price = price.substring(1);
//                        }                        
//                        try {
//                            item.setPrice(Float.parseFloat(price));
//                        } catch (NumberFormatException nfe){
//                            item.setPrice(ii.getSellPrice());
//                        }
//                    } else {
//                        item.setPrice(ii.getSellPrice());
//                    }
//                } catch (Exception e){
//                    item.setPrice(ii.getSellPrice());
//                } 
//                if (item.getPrice() == null){
//                    item.setPrice(ii.getSellPrice());
//                }
//                /*
//                String dis = s.getCell(DISCOUNT, row).getContents();
//                if (dis != null && dis.length() > 0) {
//                    try {
//                        item.setDiscount(Float.parseFloat(dis));
//                    } catch (NumberFormatException nfe){
//                    }
//                }
//                item.setTitle(ii.getTitle());
//                String title = s.getCell(TITLE, row).getContents();
//                if (title != null && title.length() > 0) {
//                    if (title.length() > 255) title = title.substring(0, 256);
//                    item.setTitle(title);
//                }
//                String credit = s.getCell(CREDIT, row).getContents();
//                if (credit != null && credit.length() > 0) {
//                    credit = credit.toLowerCase();
//                } else {
//                    credit = "";
//                }
//                */
//                
//                item.setCost(ii.getCost());
//                item.setBin(ii.getBin());
//                item.setTitle(ii.getTitle());
//                /*
//                item.setCredit(order.getCreditMemo());
//                item.setCreditDamage(false);
//                item.setCreditShortage(false);
//                item.setCreditRecNoBill(false);
//                */
//                item.setBellOrder(order);
//                item.setBellInventory(ii);
//                
//                /*
//                if (item.getCredit()){
//                    item.setFilled(item.getQuantity());
//                    if (credit.equals("damage")){
//                        item.setCreditDamage(true);
//                    } else if (credit.equals("shortage")){
//                        item.setCreditShortage(true);
//                    } else if (credit.equals("recnobill")){
//                        item.setCreditRecNoBill(true);
//                    }
//                }
//                */
//                
//                items.add(item);
//            }
//            
//            if (oSession.addOrderItems(items)){
//                oSession.recalculateAllOrderTotals(id);
//                
//                for (Long iiId : iiIds){
//                    iiSession.recalculateCommitted(iiId);
//                }
//                
//                setSuccess(true);
//                setMessage("Uploaded the order items.");
//            } else {
//                setSuccess(false);
//                setMessage("There was a system error and we could not process the upload file");
//            }
//        } catch (Exception e){
//            logger.error("Could not upload order items", e);
//            setSuccess(false);
//            setMessage("Could not upload the order items, there was a system error.");
//        }
//        return "uploadstatus";
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
        
        BellOrderSessionLocal oSession = getBellOrderSession();
        order = oSession.findById(id);
        if (order == null){
            setSuccess(false);
            setMessage("Could not find the order to add the items to, refresh the page.");
            return "status";
        }
        
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
                        
            ISBN = getColumnInt(isbnCol.toLowerCase());
            QUANTITY = getColumnInt(qtyCol.toLowerCase());
            PRICE = getColumnInt(priceCol.toLowerCase());
            
            logger.info("ISBN col: "+ISBN);
            logger.info("QUANTITY col: "+QUANTITY);
            logger.info("PRICE col: "+PRICE);
            
            // Process each row.  Skip header row
            BellInventorySessionLocal iiSession = getBellInventorySession();
            List<BellOrderItem> items = new ArrayList<BellOrderItem>();
            List<Long> iiIds = new ArrayList<Long>();
            for(int row = startRow-1; row < numRows; row++) {
                
                // Check for hidden row (and skip it)
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                if(r.getZeroHeight()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                BellOrderItem item = new BellOrderItem();
                String isbn = getCellValue(r.getCell(ISBN));
                if (isbn != null && isbn.length() > 0) {
                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
                    item.setIsbn13(IsbnUtil.getIsbn13(isbn));
                } else {
                    continue;
                }
                
                BellInventory ii = iiSession.findByIsbn(item.getIsbn());
                if (ii == null){
                    hasImportErrors = true;
                    StringBuilder sb = new StringBuilder("<tr><td style=\"padding-top:5px;padding-left:25px;\">");
                    sb.append(row+1);
                    sb.append("</td><td style=\"padding-top:5px;padding-left:25px;\">");
                    sb.append(item.getIsbn());
                    sb.append("</td></tr>");
                    importErrors += sb.toString();
                    logger.error("IMPORT: Could not find bell inventory item for the isbn: "+item.getIsbn());
                    continue;
                }
                iiIds.add(ii.getId());
                
                /*
                String di = s.getCell(DISPLAY_ISBN, row).getContents();
                if (di != null && di.length() > 0) {
                    item.setDisplayIsbn(di);
                }
                */
                String quantity = getCellValue(r.getCell(QUANTITY));
                if (quantity != null && quantity.length() > 0) {
                    try {
                        item.setQuantity(Integer.parseInt(quantity));
                    } catch (NumberFormatException nfe){
                        // could not set quantity
                    }
                }
                try {
                    String price = getCellValue(r.getCell(PRICE));
                    if (price != null && price.length() > 0) {
                        if (price.startsWith("$")){
                            price = price.substring(1);
                        }                        
                        try {
                            item.setPrice(Float.parseFloat(price));
                        } catch (NumberFormatException nfe){
                            item.setPrice(ii.getSellPrice());
                        }
                    } else {
                        item.setPrice(ii.getSellPrice());
                    }
                } catch (Exception e){
                    item.setPrice(ii.getSellPrice());
                } 
                if (item.getPrice() == null){
                    item.setPrice(ii.getSellPrice());
                }
                
                item.setCost(ii.getCost());
                item.setBin(ii.getBin());
                item.setTitle(ii.getTitle());
               
                item.setBellOrder(order);
                item.setBellInventory(ii);
                
                                
                items.add(item);
            }
            
            if (oSession.addOrderItems(items)){
                oSession.recalculateAllOrderTotals(id);
                
                for (Long iiId : iiIds){
                    iiSession.recalculateCommitted(iiId);
                }
                
                setSuccess(true);
                setMessage("Uploaded the order items.");
            } else {
                setSuccess(false);
                setMessage("There was a system error and we could not process the upload file");
            }
        } catch (Exception e){
            logger.error("Could not upload order items", e);
            setSuccess(false);
            setMessage("Could not upload the order items, there was a system error.");
        }
        return "uploadstatus";
    }
    
    private int getColumnInt(String col){
        if (col.equals("a")) return 0;
        else if (col.equals("b")) return 1;
        else if (col.equals("c")) return 2;
        else if (col.equals("d")) return 3;
        else if (col.equals("e")) return 4;
        else if (col.equals("f")) return 5;
        else if (col.equals("g")) return 6;
        else if (col.equals("h")) return 7;
        else if (col.equals("i")) return 8;
        else if (col.equals("j")) return 9;
        else if (col.equals("k")) return 10;
        else if (col.equals("l")) return 11;
        else if (col.equals("m")) return 12;
        else if (col.equals("n")) return 13;
        else if (col.equals("o")) return 14;
        else if (col.equals("p")) return 15;
        else if (col.equals("q")) return 16;
        else if (col.equals("r")) return 17;
        else if (col.equals("s")) return 18;
        else if (col.equals("t")) return 19;
        else if (col.equals("u")) return 20;
        else if (col.equals("v")) return 21;
        else if (col.equals("w")) return 22;
        else if (col.equals("x")) return 23;
        else if (col.equals("y")) return 24;
        else if (col.equals("z")) return 25;
        else if (col.equals("aa")) return 26;
        else if (col.equals("ab")) return 27;
        else if (col.equals("ac")) return 28;
        else if (col.equals("ad")) return 29;
        else if (col.equals("ae")) return 30;
        else if (col.equals("af")) return 31;
        else if (col.equals("ag")) return 32;
        else if (col.equals("ah")) return 33;
        else if (col.equals("ai")) return 34;
        else if (col.equals("aj")) return 35;
        else if (col.equals("ak")) return 36;
        else if (col.equals("al")) return 37;
        else if (col.equals("am")) return 38;
        else if (col.equals("an")) return 39;
        else if (col.equals("ao")) return 40;
        else if (col.equals("ap")) return 41;
        else if (col.equals("aq")) return 42;
        else if (col.equals("ar")) return 43;
        else if (col.equals("as")) return 44;
        else if (col.equals("at")) return 45;
        else if (col.equals("au")) return 46;
        else if (col.equals("av")) return 47;
        else if (col.equals("aw")) return 48;
        else if (col.equals("ax")) return 49;
        else if (col.equals("ay")) return 50;
        else if (col.equals("az")) return 51;
        else {
            logger.error("Could not determine column into for col: "+col);
        }
        return 0;
    }
    
    public BellOrder getOrder() {
        return order;
    }

    public void setOrder(BellOrder order) {
        this.order = order;
    }

    public BellOrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(BellOrderItem orderItem) {
        this.orderItem = orderItem;
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

    public BellInventory getBellInventory() {
        return bellInventory;
    }

    public void setBellInventory(BellInventory bellInventory) {
        this.bellInventory = bellInventory;
    }

    public Integer getShipped() {
        return shipped;
    }

    public void setShipped(Integer shipped) {
        this.shipped = shipped;
    }

    public String getSelectionIds() {
        return selectionIds;
    }

    public void setSelectionIds(String selectionIds) {
        this.selectionIds = selectionIds;
    }

    public Boolean getLoadNext() {
        return loadNext;
    }

    public void setLoadNext(Boolean loadNext) {
        this.loadNext = loadNext;
    }

    public String getIsbnCol() {
        return isbnCol;
    }

    public void setIsbnCol(String isbnCol) {
        this.isbnCol = isbnCol;
    }

    public String getQtyCol() {
        return qtyCol;
    }

    public void setQtyCol(String qtyCol) {
        this.qtyCol = qtyCol;
    }

    public String getPriceCol() {
        return priceCol;
    }

    public void setPriceCol(String priceCol) {
        this.priceCol = priceCol;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Boolean getHasImportErrors() {
        return hasImportErrors;
    }
    
    public void setHasImportErrors(Boolean hasImportErrors) {
        this.hasImportErrors = hasImportErrors;
    }

    public String getImportErrors() {
        StringBuilder sb = new StringBuilder("<div style=\"margin-bottom:10px;margin-top:10px;width:100%;\"><table style=\"margin-left:auto;margin-right:auto;\"><tr style=\"border-bottom:1px solid #999;\"><td align=\"center\" style=\"padding-left:25px;font-weight:bold;\">Row</td><td align=\"center\" style=\"padding-left:25px;font-weight:bold;\">ISBN</td><td align=\"center\" style=\"padding-left:25px;font-weight:bold;\">Condition</td></tr>");
        sb.append(importErrors);
        sb.append("</table></div>");
        return sb.toString();
    }

    public void setImportErrors(String importErrors) {
        this.importErrors = importErrors;
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


}
