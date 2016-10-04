package com.bc.actions.bookcountry;

import java.io.File;
import java.util.ArrayList;
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

import com.bc.actions.BaseAction;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.orm.InventoryItem;
import com.bc.util.ActionRole;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class InventoryBulkUpdateAction extends BaseAction {

    private static Logger logger = Logger.getLogger(InventoryBulkUpdateAction.class);
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    /*
    Column A: ISBN
    Column B: Condition (hurt, overstock, unjacketed)
    Column C: Sell Price
    Column D: Bell Book (true / false)
    Column E: Restricted (true / false)
    Column F: Higher Education (true / false)
    */

    private static final int ISBN = 0;
    private static final int COND = 1;
    private static final int SELL_PRICE = 2;
    private static final int BELL = 3;
    private static final int RESTRICTED = 4;
    private static final int HE = 5;
    
    @ActionRole({"BcInvAdmin"})
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
//                setMessage("The uploaded file contained no items to update.");
//                return "status";
//            }
//            
//            // Check for header row and hidden columns
//            try {
//                
//                // Check for hidden columns
//                for(int i = ISBN; i <= HE; i++) {
//                    if(s.getCell(i, 0).isHidden()) {
//                        setSuccess(false);
//                        setMessage("There are hidden columns in the first 6 columns.  Not supported.");
//                        return "status";
//                    }
//                }
//
//                if (!s.getCell(ISBN,0).getContents().startsWith("ISBN") || 
//                    !s.getCell(COND,0).getContents().startsWith("Condition") || 
//                    !s.getCell(SELL_PRICE,0).getContents().startsWith("Selling Price") ||
//                    !s.getCell(BELL,0).getContents().startsWith("Bell Book") || 
//                    !s.getCell(RESTRICTED,0).getContents().startsWith("Restricted") ||
//                    !s.getCell(HE,0).getContents().startsWith("Higher Education"))
//                {
//                    setSuccess(false);
//                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Selling Price, Bell Book, Restricted, Higher Education.");
//                    return "status";
//                }
//
//                
//            } catch (ArrayIndexOutOfBoundsException aie) {
//                setSuccess(false);
//                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Selling Price, Bell Book, Restricted, Higher Education.");
//                return "status";                                
//            }
//            
//            // Process each row.  Skip header row
//            InventoryItemSessionLocal iiSession = getInventoryItemSession();
//            List<Long> iiIds = new ArrayList<Long>();
//            for(int row = 1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                String isbn = s.getCell(ISBN, row).getContents();
//                String cond = s.getCell(COND, row).getContents();
//                if (cond != null) cond = cond.toLowerCase();
//                
//                InventoryItem ii = iiSession.findByIsbnCond(isbn, cond);
//                if (ii == null){
//                    logger.info("Could not find inventory item for the isbn: "+isbn+" cond: "+cond);
//                    continue;
//                }
//                iiIds.add(ii.getId());
//                
//                String sprice = s.getCell(SELL_PRICE, row).getContents();
//                if (sprice != null && sprice.length() > 0) {
//                    try {
//                        ii.setSellingPrice(Float.parseFloat(sprice));
//                    } catch (NumberFormatException nfe){}
//                }
//                
//                String bell = s.getCell(BELL, row).getContents();
//                ii.setBellbook(false);
//                if (bell != null && bell.equals("true")){
//                    ii.setBellbook(true);
//                }
//                
//                String res = s.getCell(RESTRICTED, row).getContents();
//                ii.setRestricted(false);
//                if (res != null && res.equals("true")) {
//                    ii.setRestricted(true);
//                }
//
//                String he = s.getCell(HE, row).getContents();
//                ii.setHe(false);
//                if (he != null && he.equals("true")) {
//                    ii.setHe(true);
//                }
//                
//                iiSession.update(ii);
//            }
//            
//            setSuccess(true);
//            setMessage("Uploaded the bulk inventory item updates.");
//        } catch (Exception e){
//            logger.error("Could not bulk update the inventory items", e);
//            setSuccess(false);
//            setMessage("There was a system error and we could not update the inventory items.");
//        }
//
//        return "status";
//    }
//    
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
                setMessage("The uploaded file contained no items to update.");
                return "status";
            }
            
            // Check for header row and hidden columns
            try {
                
                // Check for hidden columns
                for(int i = ISBN; i <= HE; i++) {
                    if (s.isColumnHidden(i)) {
                        setSuccess(false);
                        setMessage("There are hidden columns in the first 6 columns.  Not supported.");
                        return "status";
                    }
                }
                
                Row r = s.getRow(0);

                if (!getCellValue(r.getCell(ISBN)).startsWith("ISBN") || 
                    !getCellValue(r.getCell(COND)).startsWith("Condition") || 
                    !getCellValue(r.getCell(SELL_PRICE)).startsWith("Selling Price") ||
                    !getCellValue(r.getCell(BELL)).startsWith("Bell Book") || 
                    !getCellValue(r.getCell(RESTRICTED)).startsWith("Restricted") ||
                    !getCellValue(r.getCell(HE)).startsWith("Higher Education"))
                {
                    setSuccess(false);
                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Selling Price, Bell Book, Restricted, Higher Education.");
                    return "status";
                }

                
            } catch (ArrayIndexOutOfBoundsException aie) {
                setSuccess(false);
                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, Condition, Selling Price, Bell Book, Restricted, Higher Education.");
                return "status";                                
            }
            
            // Process each row.  Skip header row
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            List<Long> iiIds = new ArrayList<Long>();
            for(int row = 1; row < numRows; row++) {
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getZeroHeight()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                String isbn = getCellValue(r.getCell(ISBN));
                String cond = getCellValue(r.getCell(COND));
                if (cond != null) cond = cond.toLowerCase();
                
                InventoryItem ii = iiSession.findByIsbnCond(isbn, cond);
                if (ii == null){
                    logger.info("Could not find inventory item for the isbn: "+isbn+" cond: "+cond);
                    continue;
                }
                iiIds.add(ii.getId());
                
                String sprice = getCellValue(r.getCell(SELL_PRICE));
                if (sprice != null && sprice.length() > 0) {
                    try {
                        ii.setSellingPrice(Float.parseFloat(sprice));
                    } catch (NumberFormatException nfe){}
                }
                
                String bell = getCellValue(r.getCell(BELL));
                ii.setBellbook(false);
                if (bell != null && bell.equals("true")){
                    ii.setBellbook(true);
                }
                
                String res = getCellValue(r.getCell(RESTRICTED));
                ii.setRestricted(false);
                if (res != null && res.equals("true")) {
                    ii.setRestricted(true);
                }

                String he = getCellValue(r.getCell(HE));
                ii.setHe(false);
                if (he != null && he.equals("true")) {
                    ii.setHe(true);
                }
                
                iiSession.update(ii);
            }
            
            setSuccess(true);
            setMessage("Uploaded the bulk inventory item updates.");
        } catch (Exception e){
            logger.error("Could not bulk update the inventory items", e);
            setSuccess(false);
            setMessage("There was a system error and we could not update the inventory items.");
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
