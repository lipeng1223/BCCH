package com.bc.actions.bellwether;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//import jxl.Sheet;
//import jxl.Workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.AmazonLookup;
import com.bc.actions.BaseAction;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.ejb.bellwether.BellReceivingSessionLocal;
import com.bc.orm.*;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import com.bc.util.Timing;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="upload", location="/WEB-INF/jsp/bellwether/receiving/item/upload.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bellwether/receiving/item/crud.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bellwether/receiving/item/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class ReceivingItemAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ReceivingItemAction.class);

    private static HashSet<String> coverTypes = new HashSet<String>();
    
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
    
    private BellReceived bellReceived;
    private BellReceivedItem bellReceivedItem;
    private BellInventory bellInventory;
    
    private String selectionIds;
    private Long skidTypeId;
    private Boolean skidLbs = false;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private static final int ISBN = 0;
    private static final int QUANTITY = 1;
    private static final int BIN = 2;
    private static final int COST = 3;
    private static final int COVER = 4;
    
    @ActionRole({"BellRecAdmin", "BellRecViewer"})
    public String detail(){
        BellReceivingSessionLocal rSession = getBellReceivingSession();
        bellReceivedItem = rSession.findItemById(id);
        return "detail";
    }
    
    @ActionRole({"BellRecAdmin"})
    public String create(){
        try {
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            bellReceived = rSession.findById(id, "vendor");
        } catch (Exception e){
            logger.error("Could not get bell received vendors", e);
        }
        return "crud";
    }
    
    @ActionRole({"BellRecAdmin"})
    public String createSubmit(){
        try {
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            bellReceived = rSession.findById(id);
            if (bellReceived == null){
                setSuccess(false);
                setMessage("This Receiving has been removed from the system.");
                return "status";
            }
            if (bellReceived.getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            String isbn = bellReceivedItem.getIsbn().trim();
            if (isbn != null && isbn.length() > 0) {
                bellReceivedItem.setIsbn(IsbnUtil.getIsbn10(isbn));
                
                if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(isbn)))
                    bellReceivedItem.setIsbn13(IsbnUtil.getIsbn13(isbn));
            }
            bellReceivedItem.setBellReceived(bellReceived);
            bellReceivedItem.setDate(bellReceived.getPoDate());
            bellReceivedItem.setAvailable(bellReceivedItem.getQuantity());
            bellReceivedItem.setType("Pieces");
            // handle skid stuff
            if (bellReceivedItem.getSkid()){
                bellReceivedItem.setType("Skid");
                if (skidLbs){
                    bellReceivedItem.setType("Lbs");
                }
                if (bellReceivedItem.getSkidPieceCost() != null && bellReceivedItem.getSkidPieceCount() != null){
                    bellReceivedItem.setCost(bellReceivedItem.getSkidPieceCost()*bellReceivedItem.getSkidPieceCount());
                }
                if (bellReceivedItem.getSkidPiecePrice() != null && bellReceivedItem.getSkidPieceCount() != null){
                    bellReceivedItem.setSellPrice(bellReceivedItem.getSkidPiecePrice()*bellReceivedItem.getSkidPieceCount());
                }
            }
            
            BellInventorySessionLocal iiSession = getBellInventorySession();
            BellInventory ii = iiSession.findByIsbn(bellReceivedItem.getIsbn());
            if (ii == null){
                // create a new inventory item
                ii = new BellInventory();
                ii.setIsbn(bellReceivedItem.getIsbn());
                ii.setIsbn13(bellReceivedItem.getIsbn13());
                ii.setBin(bellReceivedItem.getBin());
                if (bellReceivedItem.getSellPrice() != null) ii.setSellPrice(bellReceivedItem.getSellPrice().floatValue());
                ii.setSkid(bellReceivedItem.getSkid());
                ii.setListPrice(bellReceivedItem.getListPrice());
                ii.setCover(bellReceivedItem.getCoverType());
                ii.setOnhand(0);
                ii.setTitle(bellReceivedItem.getTitle());
                AmazonLookup.getInstance().lookupData(ii, true);
                iiSession.create(ii);
            } else {
                ii.setCover(bellReceivedItem.getCoverType());
                ii.setBin(bellReceivedItem.getBin());
                iiSession.update(ii);
            }
            bellReceivedItem.setTitle(ii.getTitle());
            bellReceivedItem.setBellInventory(ii);
            rSession.create(bellReceivedItem);
            rSession.recalculateReceived(bellReceived.getId());
            
            LifoSessionLocal lifoSession = getLifoSession();
            lifoSession.createBellReceivedItem(bellReceivedItem, ii.getId());
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not create the bell received item", e);
            setSuccess(false);
            setMessage("Could not create the bell received item, there was a system error.");
        }
        
        return "status";
    }
    
    @ActionRole({"BellRecAdmin"})
    public String editSubmit(){
        try {
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            BellReceivedItem dbri = rSession.findItemById(bellReceivedItem.getId(), "bellReceived");
            
            if (dbri.getBellReceived().getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            dbri.setPreQuantity(dbri.getQuantity());
            dbri.setQuantity(bellReceivedItem.getQuantity());
            dbri.setOrderedQuantity(bellReceivedItem.getOrderedQuantity());
            dbri.setCost(bellReceivedItem.getCost());
            dbri.setTitle(bellReceivedItem.getTitle());
            dbri.setBin(bellReceivedItem.getBin());
            dbri.setListPrice(bellReceivedItem.getListPrice());
            dbri.setSellPrice(bellReceivedItem.getSellPrice());
            dbri.setCoverType(bellReceivedItem.getCoverType());
            dbri.setBreakroom(bellReceivedItem.getBreakroom());
            
            BellInventorySessionLocal iiSession = getBellInventorySession();
            BellInventory ii = iiSession.findByIsbn(dbri.getIsbn());
            if (ii != null){
                ii.setCover(bellReceivedItem.getCoverType());
                ii.setBin(bellReceivedItem.getBin());
                iiSession.update(ii);
            }
            
            rSession.update(dbri);
            
            LifoSessionLocal lifoSession = getLifoSession();
            lifoSession.updateBellReceivedItem(dbri);

            rSession.recalculateReceived(dbri.getBellReceived().getId());

            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update bell received item by id: "+bellReceivedItem.getId(), e);
            setSuccess(false);
            setMessage("Could not update the bell item, there was a sytem error.");
        }
        return "status";
    }
    
    @ActionRole({"BellRecAdmin"})
    public String edit(){
        try {
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            bellReceivedItem = rSession.findItemById(id, "bellInventory", "bellReceived");
            bellInventory = bellReceivedItem.getBellInventory();
            bellReceived = rSession.findById(bellReceivedItem.getBellReceived().getId(), "vendor");
        } catch (Exception e){
            logger.error("Could not edit bell received item: "+id, e);
        }
        return "crud";
    }
    
    @ActionRole({"BellRecAdmin"})
    public String delete(){
        try {
            BellReceivingSessionLocal rSession = getBellReceivingSession();
            
            if(selectionIds != null) {
                String[] stringIds = selectionIds.split(",");
                Long recId = null;
                for(String theId : stringIds) {
                    Long idl = new Long(theId);
                    if (recId == null){
                        bellReceivedItem = rSession.findItemById(idl, "bellReceived");
                        recId = bellReceivedItem.getBellReceived().getId();
                        if (bellReceivedItem.getBellReceived().getPosted()){
                            setSuccess(false);
                            setMessage("This Receiving has been Posted, refresh the page.");
                            return "status";
                        }
                    } else {
                        bellReceivedItem = rSession.findItemById(idl);
                    }
                    if (bellReceivedItem == null){
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

    @ActionRole({"BellRecAdmin"})
    public String uploadPage(){
        return "upload";
    }
    
    @ActionRole({"BellRecAdmin"})
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
//        BellReceivingSessionLocal rSession = getBellReceivingSession();
//        bellReceived = rSession.findById(id);
//        if (bellReceived == null){
//            setSuccess(false);
//            setMessage("Could not find the receiving to add the items to, refresh the page.");
//            return "status";
//        }
//        if (bellReceived.getPosted()){
//            setSuccess(false);
//            setMessage("This Receiving has been Posted, refresh the page.");
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
//            try {
//                
//                // Check for hidden columns
//                for(int i = ISBN; i <= COVER; i++) {
//                    if(s.getCell(i, 0).isHidden()) {
//                        setSuccess(false);
//                        setMessage("There are hidden columns in the first 5 columns.  Not supported.");
//                        return "status";
//                    }
//                }
//
//                if (!s.getCell(ISBN,0).getContents().startsWith("ISBN") || 
//                    !s.getCell(QUANTITY,0).getContents().startsWith("Quantity") || 
//                    !s.getCell(BIN,0).getContents().startsWith("Bin") || 
//                    !s.getCell(COST,0).getContents().startsWith("Cost") || 
//                    !s.getCell(COVER,0).getContents().startsWith("Cover"))
//                {
//                    setSuccess(false);
//                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Quantity, Bin, Cost, Cover.");
//                    return "status";
//                }
//
//                
//            } catch (ArrayIndexOutOfBoundsException aie) {
//                setSuccess(false);
//                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Quantity, Bin, Cost, Cover.");
//                return "status";                                
//            }
//            
//            // Process each row.  Skip header row
//            BellInventorySessionLocal iiSession = getBellInventorySession();
//            List<BellReceivedItem> items = new ArrayList<BellReceivedItem>();
//            List<Long> ids = new ArrayList<Long>();
//            for(int row = 1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                BellReceivedItem item = new BellReceivedItem();
//                String isbn = s.getCell(ISBN, row).getContents();
//                if (isbn != null && isbn.length() > 0) {
//                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
//                    item.setIsbn13(IsbnUtil.getIsbn13(isbn));
//                } else {
//                    continue;
//                }
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
//                item.setBin(s.getCell(BIN, row).getContents());
//                
//                
//                String cost = s.getCell(COST, row).getContents();
//                if (cost != null && cost.length() > 0) {
//                    try {
//                        item.setCost(Float.parseFloat(cost));
//                    } catch (NumberFormatException nfe){
//                    }
//                }
//                
//                BellInventory ii = iiSession.findByIsbn(item.getIsbn());
//                if (ii == null){
//                    logger.info("Creating new bell inventory item for the isbn: "+item.getIsbn());
//                    ii = new BellInventory();
//                    ii.setIsbn(item.getIsbn());
//                    ii.setIsbn13(item.getIsbn13());
//                    ii.setBin(item.getBin());
//                    ii.setSellPrice(item.getSellPrice());
//                    AmazonLookup.getInstance().lookupData(ii, true);
//                    iiSession.create(ii);
//                }
//                // making sure we get the bin of the item
//                item.setBin(ii.getBin());
//                
//                item.setListPrice(ii.getListPrice());
//                item.setCost(ii.getCost());
//                item.setSellPrice(ii.getSellPrice());
//                item.setCoverType(ii.getCover());
//                
//                String cover = s.getCell(COVER, row).getContents();
//                if (coverTypes.contains(cover)) {
//                    item.setCoverType(cover);
//                }
//                item.setBellReceived(bellReceived);
//                
//                item.setTitle(ii.getTitle());
//                item.setBellInventory(ii);
//                
//                items.add(item);
//                ids.add(ii.getId());
//            }
//
//            Timing t = new Timing("Add Received Items");
//            t.start();
//            if (rSession.addReceivedItems(items)){
//                t.stop();
//                
//                Timing lt = new Timing("Lifo Creates");
//                lt.start();
//                LifoSessionLocal lifoSession = getLifoSession();
//                lifoSession.createBellReceivedItems(items, ids);
//                lt.stop();
//                
//                setSuccess(true);
//                setMessage("Uploaded the bell receiving items.");
//            } else {
//                setSuccess(false);
//                setMessage("There was a system error and we could not process the upload file");
//            }
//            
//            rSession.recalculateReceived(bellReceived.getId());
//            
//        } catch (Exception e){
//            logger.error("Could not upload bell receiving items", e);
//            setSuccess(false);
//            setMessage("Could not upload the bell receiving items, there was a system error.");
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
        
        BellReceivingSessionLocal rSession = getBellReceivingSession();
        bellReceived = rSession.findById(id);
        if (bellReceived == null){
            setSuccess(false);
            setMessage("Could not find the receiving to add the items to, refresh the page.");
            return "status";
        }
        if (bellReceived.getPosted()){
            setSuccess(false);
            setMessage("This Receiving has been Posted, refresh the page.");
            return "status";
        }
        
        try {
            XSSFWorkbook workbook = null;
            try {
                FileInputStream fis = new FileInputStream(upload);
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
                Row r;
                // Check for hidden columns
                for(int i = ISBN; i <= COVER; i++) {
                    if (s.isColumnHidden(i)) {
                        setSuccess(false);
                        setMessage("There are hidden columns in the first 5 columns.  Not supported.");
                        return "status";
                    }
                }
                
                r = s.getRow(0);

                if (!getCellValue(r.getCell(ISBN)).startsWith("ISBN") || 
                    !getCellValue(r.getCell(QUANTITY)).startsWith("Quantity") || 
                    !getCellValue(r.getCell(BIN)).startsWith("Bin") || 
                    !getCellValue(r.getCell(COST)).startsWith("Cost") || 
                    !getCellValue(r.getCell(COVER)).startsWith("Cover"))
                {
                    setSuccess(false);
                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Quantity, Bin, Cost, Cover.");
                    return "status";
                }

                
            } catch (ArrayIndexOutOfBoundsException aie) {
                setSuccess(false);
                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Quantity, Bin, Cost, Cover.");
                return "status";                                
            }
            
            // Process each row.  Skip header row
            BellInventorySessionLocal iiSession = getBellInventorySession();
            List<BellReceivedItem> items = new ArrayList<BellReceivedItem>();
            List<Long> ids = new ArrayList<Long>();
            for(int row = 1; row < numRows; row++) {
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getRowStyle() != null && r.getRowStyle().getHidden()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                BellReceivedItem item = new BellReceivedItem();
                String isbn = getCellValue(r.getCell(ISBN));
                if (isbn != null && isbn.length() > 0) {
                    item.setIsbn(IsbnUtil.getIsbn10(isbn));
                    item.setIsbn13(IsbnUtil.getIsbn13(isbn));
                } else {
                    continue;
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
                item.setBin(getCellValue(r.getCell(BIN)));
                
                
                String cost = getCellValue(r.getCell(COST));
                if (cost != null && cost.length() > 0) {
                    try {
                        item.setCost(Float.parseFloat(cost));
                    } catch (NumberFormatException nfe){
                    }
                }
                
                BellInventory ii = iiSession.findByIsbn(item.getIsbn());
                if (ii == null){
                    logger.info("Creating new bell inventory item for the isbn: "+item.getIsbn());
                    ii = new BellInventory();
                    ii.setIsbn(item.getIsbn());
                    ii.setIsbn13(item.getIsbn13());
                    ii.setBin(item.getBin());
                    ii.setSellPrice(item.getSellPrice());
                    AmazonLookup.getInstance().lookupData(ii, true);
                    iiSession.create(ii);
                }
                // making sure we get the bin of the item
                item.setBin(ii.getBin());
                
                item.setListPrice(ii.getListPrice());
                item.setCost(ii.getCost());
                item.setSellPrice(ii.getSellPrice());
                item.setCoverType(ii.getCover());
                
                String cover = getCellValue(r.getCell(COVER));
                if (coverTypes.contains(cover)) {
                    item.setCoverType(cover);
                }
                item.setBellReceived(bellReceived);
                
                item.setTitle(ii.getTitle());
                item.setBellInventory(ii);
                
                items.add(item);
                ids.add(ii.getId());
            }

            Timing t = new Timing("Add Received Items");
            t.start();
            if (rSession.addReceivedItems(items)){
                t.stop();
                
                Timing lt = new Timing("Lifo Creates");
                lt.start();
                LifoSessionLocal lifoSession = getLifoSession();
                lifoSession.createBellReceivedItems(items, ids);
                lt.stop();
                
                setSuccess(true);
                setMessage("Uploaded the bell receiving items.");
            } else {
                setSuccess(false);
                setMessage("There was a system error and we could not process the upload file");
            }
            
            rSession.recalculateReceived(bellReceived.getId());
            
        } catch (Exception e){
            logger.error("Could not upload bell receiving items", e);
            setSuccess(false);
            setMessage("Could not upload the bell receiving items, there was a system error.");
        }
        return "status";        
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

    public BellInventory getBellInventory() {
        return bellInventory;
    }

    public void setBellInventory(BellInventory bellInventory) {
        this.bellInventory = bellInventory;
    }

    public BellReceived getBellReceived() {
        return bellReceived;
    }

    public void setBellReceived(BellReceived bellReceived) {
        this.bellReceived = bellReceived;
    }

    public BellReceivedItem getBellReceivedItem() {
        return bellReceivedItem;
    }

    public void setBellReceivedItem(BellReceivedItem bellReceivedItem) {
        this.bellReceivedItem = bellReceivedItem;
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
