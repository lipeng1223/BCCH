package com.bc.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import jxl.Workbook;
//import jxl.format.Alignment;
//import jxl.format.Border;
//import jxl.format.BorderLineStyle;
//import jxl.format.Colour;
//import jxl.format.VerticalAlignment;
//import jxl.write.Blank;
//import jxl.write.DateFormat;
//import jxl.write.DateTime;
//import jxl.write.Label;
//import jxl.write.Number;
//import jxl.write.NumberFormats;
//import jxl.write.WritableCell;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//import jxl.write.biff.RowsExceededException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bc.orm.BaseEntity;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormat;

public class ExcelWriter {

    private static Logger log = Logger.getLogger(ExcelWriter.class);
    
//    private static WritableCellFormat nowrapCellFormat;
//    private static WritableCellFormat integerCellFormat;
//    private static WritableCellFormat floatCellFormat;
//    private static WritableCellFormat dateCellFormat;
//    private static SimpleDateFormat dateParseFormat;
//    private static WritableCellFormat headerFormat;
    private static CellStyle nowrapCellFormat;
    private static CellStyle integerCellFormat;
    private static CellStyle floatCellFormat;
    private static CellStyle textCellFormat;
    private static CellStyle dateCellFormat;
    private static SimpleDateFormat dateParseFormat;
    private static CellStyle headerFormat;
    private Boolean formattersSetup = false;
    private static ExcelWriter instance;
    
    private XSSFWorkbook workbook;

    private ExcelWriter(){}
    
//    public static ExcelWriter getInstance(){
//        if (instance == null) instance = new ExcelWriter();
//        return instance;
//    }
    
    public static ExcelWriter getInstance(){
        if (instance == null) {
            instance = new ExcelWriter();
            instance.workbook = new XSSFWorkbook();
        }
        return instance;
    }
//    private void setupFormatters(){
//        try {
//            if (formattersSetup) return;
//            formattersSetup = true;
//            nowrapCellFormat = new WritableCellFormat();
//            nowrapCellFormat.setWrap(true);
//            
//            integerCellFormat = new WritableCellFormat(NumberFormats.INTEGER);
//            floatCellFormat = new WritableCellFormat(NumberFormats.FLOAT);
//            dateParseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//            DateFormat customDateFormat = new DateFormat ("MM/dd/yyyy");
//            dateCellFormat = new WritableCellFormat (customDateFormat);
//            
//            WritableFont font = new WritableFont(WritableFont.ARIAL);
//            font.setBoldStyle(WritableFont.BOLD);
//            font.setColour(Colour.WHITE);
//            headerFormat = new WritableCellFormat(font);
//            headerFormat.setBackground(Colour.GREY_40_PERCENT);
//            headerFormat.setAlignment(Alignment.CENTRE);
//            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
//            headerFormat.setBorder(Border.LEFT, BorderLineStyle.THIN, Colour.WHITE);
//            headerFormat.setWrap(false);
//            
//        } catch (Exception e){
//            log.error("Could not setup the cell formats", e);
//        }
//    }
    
    private void setupFormatters(){
        try {
            if (formattersSetup) return;
            formattersSetup = true;
            
            nowrapCellFormat = workbook.createCellStyle();
            nowrapCellFormat.setWrapText(true);
            
            integerCellFormat = workbook.createCellStyle();
            integerCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
            
            floatCellFormat = workbook.createCellStyle();
            floatCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
            
            textCellFormat = workbook.createCellStyle();
            DataFormat fmt = workbook.createDataFormat();
            textCellFormat.setDataFormat(fmt.getFormat("@"));
            
            dateParseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            dateCellFormat = workbook.createCellStyle();
            XSSFDataFormat dateFormat = workbook.createDataFormat();
            dateCellFormat.setDataFormat(dateFormat.getFormat("mm/dd/yyyy"));
            
            headerFormat = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(HSSFColor.WHITE.index);
            headerFormat.setFont(font);
            headerFormat.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
            headerFormat.setFillPattern(CellStyle.FINE_DOTS);
            
            headerFormat.setAlignment(CellStyle.ALIGN_CENTER);
            headerFormat.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            headerFormat.setBorderLeft(CellStyle.BORDER_THIN);
            headerFormat.setLeftBorderColor(HSSFColor.WHITE.index);
            headerFormat.setWrapText(false);
            
        } catch (Exception e){
            log.error("Could not setup the cell formats", e);
        }
    }
    
    /*
     * Each ExcelWriterData is a sheet 
     */
//    public void write(File file, List<ExcelWriterData> writerData){
//        setupFormatters();
//        WritableWorkbook wb = null;
//        try {
//            wb = createWorkbook(new FileOutputStream(file));
//            int sheetNum = 0;
//            for (ExcelWriterData wd : writerData){
//                WritableSheet sheet = wb.createSheet(wd.getSheetName(), sheetNum);
//                writeColumnHeaders(sheet, wd.getHeaders(), 0, 0);
//                int currRow = 0;
//                for (Object ob : wd.getData()){
//                    int currCol = 0;
//                    for (String prop : wd.getPropertyNames()){
//                        Object propOb = PropertyUtils.getProperty(ob, prop);
//                        String propValue = propOb.toString();
//                        WritableCell cell = getTypedDataCell(currCol, currRow, getColClass(propOb), propValue);
//                        sheet.addCell(cell);
//                        currCol++;
//                    }
//                    currRow++;
//                }
//                if (wd.getExtraData() != null){
//                    for (List<String> row : wd.getExtraData()){
//                        currRow++;
//                        int currCol = 0;
//                        for (String cell : row){
//                            sheet.addCell(new Label(currCol, currRow, cell));
//                            currCol++;
//                        }
//                    }
//                }
//                sheetNum++;
//            }
//            wb.setProtected(false);
//            wb.write();
//        } catch (Exception e){
//            log.error("Could not write the excel file", e);
//        } finally {
//            try {
//                wb.close();
//            } catch (Exception ex){}
//        }
//    }
    
//        public void write(File file, List<ExcelWriterData> writerData){
//        setupFormatters();
//        try {
//            FileOutputStream os = new FileOutputStream(file);
//            int sheetNum = 0;
//            for (ExcelWriterData wd : writerData){
//                XSSFSheet sheet = workbook.createSheet(wd.getSheetName());
//                writeColumnHeaders(sheet, wd.getHeaders(), 0, 0);
//                int currRow = 0;
//                Row r;
//                for (Object ob : wd.getData()){
//                    r = sheet.createRow(currRow);
//                    int currCol = 0;
//                    for (String prop : wd.getPropertyNames()){
//                        Object propOb = PropertyUtils.getProperty(ob, prop);
//                        String propValue = propOb.toString();
//                        CreateTypedDataCell(currCol, r, getColClass(propOb), propValue);
//                        currCol++;
//                    }
//                    currRow++;
//                }
//                if (wd.getExtraData() != null){
//                    for (List<String> row : wd.getExtraData()){
//                        currRow++;
//                        r = sheet.createRow(currRow);
//                        int currCol = 0;
//                        for (String cell : row){
//                            Cell c = r.createCell(currCol);
//                            c.setCellValue(cell);
//                            currCol++;
//                        }
//                    }
//                }
//                sheetNum++;
//            }
//            workbook.write(os);
//            os.close();
//        } catch (Exception e){
//            log.error("Could not write the excel file", e);
//        } finally {
//            try {
//                workbook.close();
//            } catch (Exception ex){}
//        }
//    }
    
    public void write(File file, List<ExcelWriterData> writerData){
        setupFormatters();
        try {
            FileOutputStream os = new FileOutputStream(file);
            int sheetNum = 0;
            for (ExcelWriterData wd : writerData){
                XSSFSheet sheet = workbook.createSheet(wd.getSheetName());
                writeColumnHeaders(sheet, wd.getHeaders(), 0, 0);
                int currRow = 0;
                Row r;
                for (Object ob : wd.getData()){
                    r = sheet.createRow(currRow);
                    int currCol = 0;
                    for (String prop : wd.getPropertyNames()){
                        Object propOb = PropertyUtils.getProperty(ob, prop);
                        String propValue = propOb.toString();
                        CreateTypedDataCell(currCol, r, getColClass(propOb), propValue);
                        currCol++;
                    }
                    currRow++;
                }
                if (wd.getExtraData() != null){
                    for (List<String> row : wd.getExtraData()){
                        currRow++;
                        r = sheet.createRow(currRow);
                        int currCol = 0;
                        for (String cell : row){
                            Cell c = r.createCell(currCol);
                            c.setCellValue(cell);
                            currCol++;
                        }
                    }
                }
                sheetNum++;
            }
            workbook.write(os);
            os.close();
        } catch (Exception e){
            log.error("Could not write the excel file", e);
        } finally {
            try {
                workbook.close();
            } catch (Exception ex){}
        }
    }
    
    public Class getColClass(Object propOb){
        if (propOb instanceof String) return String.class;
        else if (propOb instanceof Integer) return Integer.class;
        else if (propOb instanceof Double) return Double.class;
        else if (propOb instanceof Float) return Float.class;
        else if (propOb instanceof Date) return Date.class;
        return String.class;
    }
    
//    public WritableWorkbook createWorkbook(OutputStream os) {
//        if (os == null) {
//            throw new IllegalArgumentException("OutputStream os argument cannot be null");
//        }
//
//        WritableWorkbook wbExportFile = null;
//
//        try {
//
//            wbExportFile = Workbook.createWorkbook(os);
//
//        } catch (Exception e) {
//            log.error("createWorkbook", e);
//            throw new RuntimeException("createWorkbook", e);
//        } finally {
//        }
//        return wbExportFile;
//    }
    
    public void writeColumnHeaders(XSSFSheet sheet,
                List<String> headers,
                int startCol,
                int startRow)
    {
        if (sheet == null) {
            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
        }
        if (headers == null) {
            throw new IllegalArgumentException("List headers argument cannot be null");
        }
        try {
            int colCount = startCol;
            Row r = sheet.createRow(startRow);
            for (String header : headers) {
                Cell cell = r.createCell(colCount);
                cell.setCellValue(header);
                cell.setCellStyle(headerFormat);
                colCount++;
            }
            // freeze the header row
            sheet.createFreezePane(0, 1, 0, 1);
        } catch (Exception e) {
            log.error("writeColumnHeaders", e);
            throw new RuntimeException("writeColumnHeaders", e);
        }
    }

//    private static WritableCell CreateTypedDataCell(int column,
//                                                 Row row,
//                                                 Class attrClass,
//                                                 String propertyValue)
//    {
//        if ( propertyValue == null || StringUtils.isEmpty(propertyValue)) {
//            return new Blank( column, row );
//        } else if ( attrClass == null ) {
//            // attrClass could be null for "artificial" non-persistent attributes, treat as string
//            return new Label( column, row, propertyValue );
//        } else if ( java.lang.Integer.class.isAssignableFrom(attrClass)) {
//            return new Number( column, row, Double.parseDouble(propertyValue), integerCellFormat );
//        } else if ( java.lang.Number.class.isAssignableFrom(attrClass)) {
//            return new Number( column, row, Double.parseDouble(propertyValue), floatCellFormat );
//        } else if ( Date.class.isAssignableFrom(attrClass)) {
//            try {
//                return new DateTime( column, row, dateParseFormat.parse(propertyValue), dateCellFormat );
//            } catch (ParseException e) {
//                log.error("CreateTypedDataCell", e);
//                throw new RuntimeException("CreateTypedDataCell", e);
//            }
//        }
//        else {
//            // Either this is a type we want formatted as a label/string, or an unknown type,
//            // so we punt and format as a label/string.
//            return new Label( column, row, propertyValue );
//        }
//    }
    
    private static Cell CreateTypedDataCell(int column,
                                                 Row row,
                                                 Class attrClass,
                                                 String propertyValue)
    {
        Cell cell = row.createCell(column);
        cell.setCellStyle(textCellFormat);
        
        if ( propertyValue == null || StringUtils.isEmpty(propertyValue)) {
            return cell;
        } else if ( attrClass == null ) {
            return cell;
        } else if ( java.lang.Integer.class.isAssignableFrom(attrClass)) {
            cell.setCellValue(Double.parseDouble(propertyValue));
            cell.setCellStyle(integerCellFormat);
            return cell;
        } else if ( java.lang.Number.class.isAssignableFrom(attrClass)) {
            cell.setCellValue(Double.parseDouble(propertyValue));
            cell.setCellStyle(floatCellFormat);
            return cell;
        } else if ( Date.class.isAssignableFrom(attrClass)) {
            try {
                cell.setCellValue(dateParseFormat.parse(propertyValue));
                cell.setCellStyle(dateCellFormat);
                return cell;
            } catch (ParseException e) {
                log.error("getTypedDataCell", e);
                throw new RuntimeException("getTypedDataCell", e);
            }
        }
        else {
            // Either this is a type we want formatted as a label/string, or an unknown type,
            // so we punt and format as a label/string.
            cell.setCellValue(propertyValue);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell;
        }
    }
    
}
