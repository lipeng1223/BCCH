/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.util;

import com.bc.dao.DaoResults;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;

/**
 *
 * @author Alex
 */
public class XlsxExporter {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XlsxExporter.class);
    public static void WriteInvoiceToFile(File output, CustomerOrder order, ArrayList<CustomerOrderItem> items){
        try {
            if (output.exists()){
                log.info(output.getName() + " exists. Deleting");
                output.delete();
                log.info("Deleted " + output.getName());
            }
            
            log.info("Creating xlsx file...");
            
            FileOutputStream fos = new FileOutputStream(output);
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("Order");
            CellStyle style = workBook.createCellStyle();
            style.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFillPattern(CellStyle.ALIGN_FILL);
            Font font = workBook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            
            String[] columnHeaders = {"Invoice", "Salesman", "Customer Name", "Customer Code", "PO", "Ship Date", "Post Date", "ISBN", "ISBN13", "Title", "List Price", "Price", "Quantity", "Shipped", "Discount", "Extended Price"};
            
            log.info("Creating header row & columns");
            
            Row row = sheet.createRow(0);
            
            for (int i = 0; i < columnHeaders.length; i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(columnHeaders[i]);
                cell.setCellStyle(style);
                sheet.setColumnWidth(i, 4500);
            }
            
            sheet.setColumnWidth(9, 13500);
            
            log.info("Writing " + items.size() + " records");
            
            XSSFDataFormat decimalFormat = workBook.createDataFormat();
            CellStyle dstyle = workBook.createCellStyle();
            dstyle.setDataFormat(decimalFormat.getFormat("0.00"));
            
            int i = 1;
            for (CustomerOrderItem orderItem : items){
                Row drow = sheet.createRow(i++);
                Hibernate.initialize(order.getCustomerOrderItems());
                
                String strValue;
                Float floatValue;
                Integer intVal;
                
                Cell cInvoice = drow.createCell(0);
                strValue = order.getInvoiceNumber();
                if (strValue == null)
                    strValue = "";
                cInvoice.setCellValue(order.getInvoiceNumber());

                Cell cSalesman = drow.createCell(1);
                strValue = order.getSalesman();
                if (strValue == null)
                    strValue = "";
                cSalesman.setCellValue(strValue);

                Cell cCustomerName = drow.createCell(2);
                strValue = order.getCustomer().getCompanyName();
                if (strValue == null)
                    strValue = "";
                cCustomerName.setCellValue(strValue);
                
                Cell cCustomerCode = drow.createCell(3);
                strValue = order.getCustomerCode();
                if (strValue == null)
                    strValue = "";
                cCustomerCode.setCellValue(strValue);

                Cell cPo = drow.createCell(4);
                strValue = order.getPoNumber();
                if (strValue == null)
                    strValue = "";
                cPo.setCellValue(strValue);

                Cell cShipDate = drow.createCell(5);
                Date d = order.getShipDate();
                if (d == null)
                    cShipDate.setCellValue("");
                else
                    cShipDate.setCellValue("" + d.getMonth() + "/" + d.getDay() + "/" + (1900 + d.getYear()));

                Cell cPostDate = drow.createCell(6);
                d = order.getPostDate();
                if (d == null)
                    cPostDate.setCellValue("");
                else
                    cPostDate.setCellValue("" + d.getMonth() + "/" + d.getDay() + "/" + (1900 + d.getYear()));
                Hibernate.initialize(orderItem.getInventoryItem());
                InventoryItem item = orderItem.getInventoryItem(); //orderItem.getInventoryItem();
                if (item != null)
                {
                    Cell cIsbn = drow.createCell(7);
                    strValue = item.getIsbn();
                    if (strValue == null)
                        strValue = "";
                    cIsbn.setCellValue(strValue);

                    Cell cIsbn13 = drow.createCell(8);
                    strValue = item.getIsbn13();
                    if (strValue == null)
                        strValue = "";
                    cIsbn13.setCellValue(strValue);

                    Cell cTitle = drow.createCell(9);
                    strValue = item.getTitle();
                    if (strValue == null)
                        strValue = "";
                    cTitle.setCellValue(strValue);

                    Cell cListPrice = drow.createCell(10);
                    floatValue = item.getListPrice();
                    cListPrice.setCellStyle(dstyle);
                    if (floatValue == null)
                        floatValue = 0.0f;
                    cListPrice.setCellValue(floatValue);

                    Cell cPrice = drow.createCell(11);
                    floatValue = item.getSellingPrice();
                    cPrice.setCellStyle(dstyle);
                    if (floatValue == null)
                        floatValue = 0.0f;
                    cPrice.setCellValue(floatValue);
                }
                Cell cQuantity = drow.createCell(12);
                intVal = orderItem.getQuantity();
                log.info("Quantity : " + intVal);
                if (intVal == null)
                    intVal = 0;
                cQuantity.setCellValue(intVal);

                Cell cShipped = drow.createCell(13);
                intVal = orderItem.getFilled();
                log.info("Shipped QTY : " + intVal);
                if (intVal == null)
                    intVal = 0;
                cShipped.setCellValue(intVal);

                Cell cDiscount = drow.createCell(14);
                cDiscount.setCellStyle(dstyle);
                floatValue = orderItem.getDiscount();
                if (floatValue == null)
                    floatValue = 0.0f;
                cDiscount.setCellValue(floatValue);

                Cell cExtendedPrice = drow.createCell(15);
                cExtendedPrice.setCellStyle(dstyle);
                BigDecimal dValue = orderItem.getTotalPrice();
                if (dValue == null)
                    dValue = BigDecimal.ZERO;
                cExtendedPrice.setCellValue(dValue.doubleValue());

            }
            
            workBook.write(fos);
            log.info("Finished writing data, closing...");
            
            fos.close();
            log.info("Completed exporting data to " + output.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(XlsxExporter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
