package com.bc.actions.bookcountry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//import jxl.format.PageOrientation;
//import jxl.format.UnderlineStyle;
//import jxl.write.Label;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WritableSheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.usermodel.XSSFDataFormat;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.ejb.OrderSessionLocal;
import com.bc.excel.ExcelExtraDataWriter;
import com.bc.jasper.OrderDataSourceProvider;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.CustomerShipping;
import com.bc.struts.QueryResults;
import com.bc.struts.result.JasperResult;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Table;
import com.bc.util.ActionRole;
import com.bc.util.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
//import jxl.HeaderFooter;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.util.CellRangeAddress;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class OrderPicklistAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(OrderPicklistAction.class);

    private String filename;
    private String comment;

    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String execute(){
        OrderSessionLocal oSession = getOrderSession();
        CustomerOrder order = oSession.findById(id, "customer", "customerShipping", "customerOrderItems");
        
        if (order == null){
            logger.error("Could not find order by id: "+id);
            return "404";
        }
        
        OrderDataSourceProvider datasource = new OrderDataSourceProvider();
        ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(order.getCustomerOrderItems());
        Collections.sort(items);
        datasource.setup(order, items, true);
        
        setJasperDatasource(datasource);
        
        setJasperParamMap(setupParamMap(order));
        setJasperReportName("picklist.jasper");
        setJasperFilename(filename);
        setJasperExportType(JasperResult.PDF);

        return "jasperreport";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String excel(){
        try {
            OrderSessionLocal oSession = getOrderSession();
            CustomerOrder order = oSession.findById(id, "customer", "customerShipping");
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("customerOrder", order));
            queryResults = new QueryResults(oSession.findAllItems(queryInput, "customerOrder", "inventoryItem"));
            
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            
            // sort by bin
            Collections.sort((List<CustomerOrderItem>)queryResults.getResults().getData());
            queryResults.setTableConfig(setupTable(), queryInput.getFilterParams());
            setExtraDataWriter(new PicklistExtraDataWriter(order));
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not get excel", e);
        }
        return "queryresults";
    }
    
    private Table setupTable(){
        Table listTable = new Table();
        listTable.setExcelLandscape(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("invQuantityNoZero"));
        cd.add(new ColumnData("breakQuantityNoZero"));
        cd.add(new ColumnData("bellQuantityNoZero"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("inventoryItem.publisher"));
        cd.add(new ColumnData("inventoryItem.cover"));
        cd.add(new ColumnData("price").setType("float"));
        cd.add(new ColumnData("inventoryItem.onhand"));
        cd.add(new ColumnData("bin"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("quantity", "Ordered", 50).setExcelWidth(8));
        cm.add(new ColumnModel("invQuantityNoZero", "INV", 50).setExcelWidth(8));
        cm.add(new ColumnModel("breakQuantityNoZero", "BKRM", 50).setExcelWidth(8));
        cm.add(new ColumnModel("bellQuantityNoZero", "BELL", 50).setExcelWidth(8));
        cm.add(new ColumnModel("isbn", "ISBN", 80).setExcelWidth(15));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100).setExcelWidth(15));
        cm.add(new ColumnModel("cond", "Condition", 50).setExcelWidth(10));
        cm.add(new ColumnModel("title", "Title", 200).setExcelWidth(25).setExcelWordWrap(true));
        cm.add(new ColumnModel("inventoryItem.publisher", "Pub", 200).setExcelWidth(10));
        cm.add(new ColumnModel("inventoryItem.cover", "Bind", 200).setExcelWidth(10));
        cm.add(new ColumnModel("price", "Net", 200).setExcelWidth(10));
        cm.add(new ColumnModel("inventoryItem.onhand", "On Hand", 200).setExcelWidth(10));
        cm.add(new ColumnModel("bin", "Bin", 200).setExcelWidth(10));
        listTable.setColumnModels(cm);
                
        listTable.setDefaultSortCol("createTime");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryOrderPicklistExport");
        setExcelExportSheetName("Order Picklist");
        
        return listTable;
    }
    
    
    private HashMap setupParamMap(CustomerOrder order){
        HashMap paramMap = new HashMap();
        paramMap.put("fullBillingAddress", null);
        paramMap.put("fullShippingAddress", null);
        StringBuilder ba = new StringBuilder();
        StringBuilder sa = new StringBuilder();
        
        Customer customer = order.getCustomer();
        if (customer == null){
            customer = new Customer();
        }
        CustomerShipping shipping = order.getCustomerShipping();
        if ((shipping == null || shipping.getAddress1() == null || shipping.getAddress1().length() == 0) && customer != null) {
            shipping = new CustomerShipping();
            shipping.setAddress1(customer.getAddress1());
            shipping.setAddress2(customer.getAddress2());
            shipping.setAddress3(customer.getAddress3());
            shipping.setCity(customer.getCity());
            shipping.setState(customer.getState());
            shipping.setZip(customer.getZip());
            shipping.setCountry(customer.getCountry());
        } else if (shipping == null){
            shipping = new CustomerShipping();
            shipping.setAddress1("");
            shipping.setAddress2("");
            shipping.setAddress3("");
            shipping.setCity("");
            shipping.setState("");
            shipping.setZip("");
            shipping.setCountry("");
        }
        if (customer != null){
            shipping.setShippingName(customer.getCompanyName());
        } else {
            shipping.setShippingName("");
        }
        
        
        if (customer.getContactName() != null && customer.getContactName().length() > 0){
            ba.append(customer.getContactName());
            ba.append("\n");
        }
        if (customer.getCompanyName() != null && customer.getCompanyName().length() > 0) {
            ba.append(customer.getCompanyName());
            ba.append("\n");
        }
        if (customer.getAddress1() != null && customer.getAddress1().length() > 0){
            ba.append(customer.getAddress1());
            ba.append("\n");
        }
        if (customer.getAddress2() != null && customer.getAddress2().length() > 0){
            ba.append(customer.getAddress2());
            ba.append("\n");
        }
        if (customer.getAddress3() != null && customer.getAddress3().length() > 0){
            ba.append(customer.getAddress3());
            ba.append("\n");
        }
        if (customer.getCity() != null && customer.getCity().length() > 0){
            ba.append(customer.getCity());
            ba.append(", ");
        }
        if (customer.getState() != null && customer.getState().length() > 0){
            ba.append(customer.getState());
            ba.append(". ");
        }
        if (customer.getZip() != null && customer.getZip().length() > 0){
            ba.append(customer.getZip());
            ba.append(" ");
        }
        if (customer.getCountry() != null && customer.getCountry().length() > 0){
            ba.append(customer.getCountry());
        }
        ba.append("\n\n");
        if (customer.getWorkPhone() != null && customer.getWorkPhone().length() > 0){
            ba.append("Work Phone - ");
            ba.append(customer.getWorkPhone());
            ba.append("\n");
        }
        if (customer.getHomePhone() != null && customer.getHomePhone().length() > 0){
            ba.append("Home Phone - ");
            ba.append(customer.getHomePhone());
            ba.append("\n");
        }
        if (customer.getCellPhone() != null && customer.getCellPhone().length() > 0){
            ba.append("Cell Phone - ");
            ba.append(customer.getCellPhone());
            ba.append("\n");
        }
        if (customer.getFax() != null && customer.getFax().length() > 0){
            ba.append("Fax - ");
            ba.append(customer.getFax());
        }
        paramMap.put("fullBillingAddress", ba.toString());


        if (shipping.getShippingName() != null && shipping.getShippingName().length() > 0){
            sa.append(shipping.getShippingName());
            sa.append("\n");
        }
        if (shipping.getAddress1() != null && shipping.getAddress1().length() > 0){
            sa.append(shipping.getAddress1());
            sa.append("\n");
        }
        if (shipping.getAddress2() != null && shipping.getAddress2().length() > 0){
            sa.append(shipping.getAddress2());
            sa.append("\n");
        }
        if (shipping.getAddress3() != null && shipping.getAddress3().length() > 0){
            sa.append(shipping.getAddress3());
            sa.append("\n");
        }
        if (shipping.getCity() != null && shipping.getCity().length() > 0){
            sa.append(shipping.getCity());
            sa.append(", ");
        }
        if (shipping.getState() != null && shipping.getState().length() > 0){
            sa.append(shipping.getState());
            sa.append(". ");
        }
        if (shipping.getZip() != null && shipping.getZip().length() > 0){
            sa.append(shipping.getZip());
            sa.append(" ");
        }
        if (shipping.getCountry() != null && shipping.getCountry().length() > 0){
            sa.append(shipping.getCountry());
        }
        sa.append("\n");
        if (shipping.getPhone() != null && shipping.getPhone().length() > 0) {
            sa.append("Shipping Contact Number: ");
            sa.append(shipping.getPhone());
            sa.append("\n");
        }
        if (shipping.getFax() != null && shipping.getFax().length() > 0) {
            sa.append("Shipping Fax: ");
            sa.append(shipping.getFax());
            sa.append("\n");
        }
        if (shipping.getComment() != null && shipping.getComment().length() > 0) {
            sa.append(shipping.getComment());
        }
        paramMap.put("fullShippingAddress", sa.toString());

        paramMap.put("invoiceNumber", "");
        if (order.getInvoiceNumber() != null){
            paramMap.put("invoiceNumber", order.getInvoiceNumber().toString());
        }
        paramMap.put("picklistComment", null);
        if (customer.getPicklistComment() != null){
            paramMap.put("picklistComment", customer.getPicklistComment());
        }
        paramMap.put("pcomment", null);
        if (comment != null){
            paramMap.put("pcomment", comment);
        }
        paramMap.put("customerCode", "");
        if (order.getCustomerCode() != null){
            paramMap.put("customerCode", order.getCustomerCode());
        }
        paramMap.put("customerTerms", "");
        if (customer.getTerms() != null){
            paramMap.put("customerTerms", customer.getTerms());
        }
        paramMap.put("orderDateString", "");
        if (order.getOrderDate() != null){
            paramMap.put("orderDateString", DateFormat.format(order.getOrderDate()));
        }

        // add up the extendeds
        BigDecimal extendedTotal = new BigDecimal(0);
        paramMap.put("itemCountTotal", 0);
        if (order.getCustomerOrderItems() != null){
            paramMap.put("itemCountTotal", order.getCustomerOrderItems().size());
        }
        return paramMap;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    

    public class PicklistExtraDataWriter implements ExcelExtraDataWriter {
        
        private CustomerOrder order;
        private Workbook workbook;
        
        public PicklistExtraDataWriter(CustomerOrder order){
            this.order = order;
        }
        @Override
//        public int writeExtraPreData(int row, WritableSheet sheet) {
//            return 0;
//        }
        public int writeExtraPreData(int row, Sheet sheet) {
            return 0;
        }

        @Override
        public void writeExtraPostData(int row, Sheet sheet) {
            Workbook workbook = sheet.getWorkbook();
            try {
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldFont.setUnderline(Font.U_SINGLE);
                CellStyle bold = workbook.createCellStyle();
                CellStyle wrapped = workbook.createCellStyle();
                bold.setFont(boldFont);
                wrapped.setWrapText(true);
                
                // print settings
                PrintSetup ps = sheet.getPrintSetup();
                ps.setLandscape(true);
                ps.setFitWidth((short)1);
                ps.setFitHeight((short)0);
                
                sheet.setFitToPage(true);
                
                sheet.setAutobreaks(true);
                sheet.setPrintGridlines(true);
                sheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
                
                Header header = sheet.getHeader();
                if (order.getCustomer() != null){
                    header.setCenter(order.getCustomer().getCompanyName());
                } else{
                    header.setCenter(order.getCustomerCode());
                }
                
                header.setRight(DateFormat.format(Calendar.getInstance().getTime()));
                
                Footer footer = sheet.getFooter();
                footer.setCenter("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
                                
                row += 2;
                int colIndent = 7;
                
                Row r = sheet.createRow(row);
                Cell cell = r.createCell(0);
                cell.setCellValue("Invoice: ");
                cell.setCellStyle(bold);

                if (order.getCustomer() != null){
                    cell = r.createCell(colIndent);
                    cell.setCellValue("Contact Info: ");
                    cell.setCellStyle(bold);
                }
                
                row++;
                r = sheet.createRow(row);
                cell = r.createCell(0);
                cell.setCellValue(order.getInvoiceNumber());
                
                if (order.getCustomer() != null){
                    cell = r.createCell(colIndent);
                    r.setHeight((short)-1);
                    cell.setCellValue(order.getCustomer().getContactName());
                    
                    if (order.getCustomer().getWorkPhone() != null && order.getCustomer().getWorkPhone().length() > 0){
                        r = sheet.createRow(++row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Work Phone: " + order.getCustomer().getWorkPhone());
                        cell.setCellStyle(wrapped);
                    }
                    if (order.getCustomer().getCellPhone() != null && order.getCustomer().getCellPhone().length() > 0){
                        r = sheet.createRow(++row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Cell Phone: " + order.getCustomer().getCellPhone());
                        cell.setCellStyle(wrapped);
                    }
                    if (order.getCustomer().getEmail1() != null && order.getCustomer().getEmail1().length() > 0){
                        r = sheet.createRow(++row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Email 1: " + order.getCustomer().getEmail1());
                        cell.setCellStyle(wrapped);
                    }
                    if (order.getCustomer().getEmail2() != null && order.getCustomer().getEmail2().length() > 0){
                        r = sheet.createRow(++row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Email 2: " + order.getCustomer().getEmail2());
                        cell.setCellStyle(wrapped);
                    }
                }
                
                logger.info("row + 3 = " + row);
                
                row += 3;
                r = sheet.createRow(row);
                cell = r.createCell(0);
                cell.setCellValue("PO #: ");
                cell.setCellStyle(bold);
                
                r = sheet.createRow(row + 1);
                cell = r.createCell(0);
                cell.setCellValue(order.getPoNumber());
                logger.info("row = " + row);

                if (order.getCustomerShipping() != null){
                    //r = sheet.createRow(row + 1);
                    cell = r.createCell(colIndent);
                    cell.setCellValue("Shipping Address: ");
                    cell.setCellStyle(bold);
                    
                    int brow = row+2;
                    if (order.getCustomerShipping().getShippingName() != null && order.getCustomerShipping().getShippingName().length() > 0){
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getShippingName());
                        cell.setCellStyle(wrapped);
                        brow++;
                    }
                    if (order.getCustomerShipping().getShippingCompany() != null && order.getCustomerShipping().getShippingCompany().length() > 0){
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getShippingCompany());
                        cell.setCellStyle(wrapped);
                        brow++;
                    }
                    if (order.getCustomerShipping().getAddress1() != null && order.getCustomerShipping().getAddress1().length() > 0){
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getAddress1());
                        cell.setCellStyle(wrapped);
                        brow++;
                    }
                    if (order.getCustomerShipping().getAddress2() != null && order.getCustomerShipping().getAddress2().length() > 0){
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getAddress2());
                        cell.setCellStyle(wrapped);
                        brow++;
                    }
                    if (order.getCustomerShipping().getAddress3() != null && order.getCustomerShipping().getAddress3().length() > 0){
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getAddress3());
                        cell.setCellStyle(wrapped);
                        brow++;
                    }
                    StringBuilder sb = new StringBuilder();
                    if (order.getCustomerShipping().getCity() != null && order.getCustomerShipping().getCity().length() > 0){
                        sb.append(order.getCustomerShipping().getCity()+", " );
                    }
                    if (order.getCustomerShipping().getState() != null && order.getCustomerShipping().getState().length() > 0){
                        sb.append(order.getCustomerShipping().getState()+". " );
                    }
                    if (order.getCustomerShipping().getZip() != null && order.getCustomerShipping().getZip().length() > 0){
                        sb.append(order.getCustomerShipping().getZip()+"  " );
                    }
                    if (order.getCustomerShipping().getCountry() != null && order.getCustomerShipping().getCountry().length() > 0){
                        sb.append(order.getCustomerShipping().getCountry() );
                    }
                    
                    r = sheet.createRow(brow);
                    cell = r.createCell(colIndent);
                    r.setHeight((short)-1);
                    cell.setCellValue(sb.toString());
                    cell.setCellStyle(wrapped);
                    
                    if (order.getCustomerShipping().getPhone() != null && order.getCustomerShipping().getPhone().length() > 0){
                        brow++;
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getPhone());
                        cell.setCellStyle(wrapped);
                    }
                    if (order.getCustomerShipping().getFax() != null && order.getCustomerShipping().getFax().length() > 0){
                        brow++;
                        r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomerShipping().getFax());
                        cell.setCellStyle(wrapped);
                    }
                }
                row++;
                
                
                row += 3;
                r = sheet.getRow(row);
                if (r == null)
                    r = sheet.createRow(row);
                cell = r.createCell(0);
                cell.setCellStyle(bold);
                cell.setCellValue("Billing Address: ");

                row++;
                if (order.getCustomer() != null){
                    int brow = row;
                    if (order.getCustomer().getCompanyName() != null && order.getCustomer().getCompanyName().length() > 0){
                        r = sheet.getRow(brow);
                        if (r == null)
                            r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(0);
                        cell.setCellValue(order.getCustomer().getCompanyName());
                        brow++;
                    }
                    if (order.getCustomer().getAddress1() != null && order.getCustomer().getAddress1().length() > 0){
                        r = sheet.getRow(brow);
                        if (r == null)
                            r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(0);
                        cell.setCellValue(order.getCustomer().getAddress1());
                        brow++;
                    }
                    if (order.getCustomer().getAddress2() != null && order.getCustomer().getAddress2().length() > 0){
                        r = sheet.getRow(brow);
                        if (r == null)
                            r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(0);
                        cell.setCellValue(order.getCustomer().getAddress2());
                        brow++;
                    }
                    if (order.getCustomer().getAddress3() != null && order.getCustomer().getAddress3().length() > 0){
                        r = sheet.getRow(brow);
                        if (r == null)
                            r = sheet.createRow(brow);
                        r.setHeight((short)-1);
                        cell = r.createCell(0);
                        cell.setCellValue(order.getCustomer().getAddress3());
                        brow++;
                    }
                    StringBuilder sb = new StringBuilder();
                    if (order.getCustomer().getCity() != null && order.getCustomer().getCity().length() > 0){
                        sb.append(order.getCustomer().getCity()+", " );
                    }
                    if (order.getCustomer().getState() != null && order.getCustomer().getState().length() > 0){
                        sb.append(order.getCustomer().getState()+". " );
                    }
                    if (order.getCustomer().getZip() != null && order.getCustomer().getZip().length() > 0){
                        sb.append(order.getCustomer().getZip()+"  " );
                    }
                    if (order.getCustomer().getCountry() != null && order.getCustomer().getCountry().length() > 0){
                        sb.append(order.getCustomer().getCountry() );
                    }
                    r = sheet.createRow(brow);
                    r.setHeight((short)-1);
                    cell = r.createCell(0);
                    cell.setCellValue(sb.toString());
                }
                row += 3;
                logger.info("row + 3 = " + row);
                if (order.getCustomer() != null){
                    if (order.getCustomer().getPicklistComment() != null && order.getCustomer().getPicklistComment().length() > 0){
                        r = sheet.createRow(row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Customer Picklist Comment: ");
                        cell.setCellStyle(bold);
                        row++;
                        r = sheet.createRow(row);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomer().getPicklistComment());
                        cell.setCellStyle(wrapped);
                        row += 2;
                    }
                    if (order.getCustomer().getComment1() != null && order.getCustomer().getComment1().length() > 0){
                        r = sheet.createRow(row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Customer Comment 1: ");
                        cell.setCellStyle(bold);
                        row++;
                        r = sheet.createRow(row);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomer().getComment1());
                        cell.setCellStyle(wrapped);
                        row += 2;
                    }
                    if (order.getCustomer().getComment2() != null && order.getCustomer().getComment2().length() > 0){
                        r = sheet.createRow(row);
                        cell = r.createCell(colIndent);
                        cell.setCellValue("Customer Comment 2: ");
                        cell.setCellStyle(bold);

                        row++;
                        r = sheet.createRow(row);
                        r.setHeight((short)-1);
                        cell = r.createCell(colIndent);
                        cell.setCellValue(order.getCustomer().getComment2());
                        cell.setCellStyle(wrapped);
                    }
                }
            } catch (Exception e){
                logger.error("Could not write extra data", e);
            }
        }
//        public void writeExtraPostData(int row, WritableSheet sheet) {
//            try {
//                WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
//                boldFont.setUnderlineStyle(UnderlineStyle.SINGLE);
//                WritableCellFormat bold = new WritableCellFormat(boldFont);                
//                
//                // print settings
//                sheet.getSettings().setFitToPages(true);
//                sheet.getSettings().setPrintGridLines(true);
//                sheet.getSettings().setFitWidth(1);
//                sheet.setPageSetup(PageOrientation.LANDSCAPE);
//                sheet.getSettings().setPrintTitlesRow(0, 0);
//                HeaderFooter header = new HeaderFooter();
//                if (order.getCustomer() != null)
//                    header.getCentre().append(order.getCustomer().getCompanyName());
//                else if (order.getCustomerCode() != null)
//                    header.getCentre().append(order.getCustomerCode());
//                header.getRight().append(DateFormat.format(Calendar.getInstance().getTime()));
//                sheet.getSettings().setHeader(header);
//                HeaderFooter footer = new HeaderFooter();
//                footer.getCentre().append("Page ");
//                footer.getCentre().appendPageNumber();
//                footer.getCentre().append(" of ");
//                footer.getCentre().appendTotalPages();
//                sheet.getSettings().setFooter(footer);
//                
//                row += 2;
//                int colIndent = 7;
//                sheet.addCell(new Label( 0, row, "Invoice:", bold ));
//                if (order.getCustomer() != null){
//                    sheet.addCell(new Label( colIndent, row, "Contact Info:", bold ));
//                }
//                row++;
//                sheet.addCell(new Label( 0, row, order.getInvoiceNumber() ));
//                if (order.getCustomer() != null){
//                    sheet.addCell(new Label( colIndent, row,  order.getCustomer().getContactName()));
//                    if (order.getCustomer().getWorkPhone() != null && order.getCustomer().getWorkPhone().length() > 0)
//                        sheet.addCell(new Label( colIndent, ++row,  "Work Phone: "+order.getCustomer().getWorkPhone()));
//                    if (order.getCustomer().getCellPhone() != null && order.getCustomer().getCellPhone().length() > 0)
//                        sheet.addCell(new Label( colIndent, ++row,  "Cell Phone: "+order.getCustomer().getCellPhone()));
//                    if (order.getCustomer().getEmail1() != null && order.getCustomer().getEmail1().length() > 0)
//                        sheet.addCell(new Label( colIndent, ++row,  "Email 1: "+order.getCustomer().getEmail1()));
//                    if (order.getCustomer().getEmail2() != null && order.getCustomer().getEmail2().length() > 0)
//                        sheet.addCell(new Label( colIndent, ++row,  "Email 1: "+order.getCustomer().getEmail2()));
//                }
//                row += 3;
//                sheet.addCell(new Label( 0, row, "PO #:", bold ));
//                if (order.getCustomerShipping() != null){
//                    sheet.addCell(new Label( colIndent, row+1, "Shipping Address:", bold ));
//                    int brow = row+2;
//                    if (order.getCustomerShipping().getShippingName() != null && order.getCustomerShipping().getShippingName().length() > 0){
//                        sheet.addCell(new Label( colIndent, brow, order.getCustomerShipping().getShippingName() ));
//                        brow++;
//                    }
//                    if (order.getCustomerShipping().getShippingCompany() != null && order.getCustomerShipping().getShippingCompany().length() > 0){
//                        sheet.addCell(new Label( colIndent, brow, order.getCustomerShipping().getShippingCompany() ));
//                        brow++;
//                    }
//                    if (order.getCustomerShipping().getAddress1() != null && order.getCustomerShipping().getAddress1().length() > 0){
//                        sheet.addCell(new Label( colIndent, brow, order.getCustomerShipping().getAddress1() ));
//                        brow++;
//                    }
//                    if (order.getCustomerShipping().getAddress2() != null && order.getCustomerShipping().getAddress2().length() > 0){
//                        sheet.addCell(new Label( colIndent, brow, order.getCustomerShipping().getAddress2() ));
//                        brow++;
//                    }
//                    if (order.getCustomerShipping().getAddress3() != null && order.getCustomerShipping().getAddress3().length() > 0){
//                        sheet.addCell(new Label( colIndent, brow, order.getCustomerShipping().getAddress3() ));
//                        brow++;
//                    }
//                    StringBuilder sb = new StringBuilder();
//                    if (order.getCustomerShipping().getCity() != null && order.getCustomerShipping().getCity().length() > 0){
//                        sb.append(order.getCustomerShipping().getCity()+", " );
//                    }
//                    if (order.getCustomerShipping().getState() != null && order.getCustomerShipping().getState().length() > 0){
//                        sb.append(order.getCustomerShipping().getState()+". " );
//                    }
//                    if (order.getCustomerShipping().getZip() != null && order.getCustomerShipping().getZip().length() > 0){
//                        sb.append(order.getCustomerShipping().getZip()+"  " );
//                    }
//                    if (order.getCustomerShipping().getCountry() != null && order.getCustomerShipping().getCountry().length() > 0){
//                        sb.append(order.getCustomerShipping().getCountry() );
//                    }
//                    sheet.addCell(new Label( colIndent, brow, sb.toString() ));
//                    if (order.getCustomerShipping().getPhone() != null && order.getCustomerShipping().getPhone().length() > 0){
//                        brow++;
//                        sheet.addCell(new Label( colIndent, brow, "Shipping Contact Number: "+order.getCustomerShipping().getPhone() ));
//                    }
//                    if (order.getCustomerShipping().getFax() != null && order.getCustomerShipping().getFax().length() > 0){
//                        brow++;
//                        sheet.addCell(new Label( colIndent, brow, "Shipping Fax: "+order.getCustomerShipping().getFax() ));
//                    }
//                }
//                row++;
//                sheet.addCell(new Label( 0, row, order.getPoNumber() ));
//                row += 3;
//                sheet.addCell(new Label( 0, row, "Billing Address:", bold ));
//                row++;
//                if (order.getCustomer() != null){
//                    int brow = row;
//                    if (order.getCustomer().getCompanyName() != null && order.getCustomer().getCompanyName().length() > 0){
//                        sheet.addCell(new Label( 0, brow, order.getCustomer().getCompanyName() ));
//                        brow++;
//                    }
//                    if (order.getCustomer().getAddress1() != null && order.getCustomer().getAddress1().length() > 0){
//                        sheet.addCell(new Label( 0, brow, order.getCustomer().getAddress1() ));
//                        brow++;
//                    }
//                    if (order.getCustomer().getAddress2() != null && order.getCustomer().getAddress2().length() > 0){
//                        sheet.addCell(new Label( 0, brow, order.getCustomer().getAddress2() ));
//                        brow++;
//                    }
//                    if (order.getCustomer().getAddress3() != null && order.getCustomer().getAddress3().length() > 0){
//                        sheet.addCell(new Label( 0, brow, order.getCustomer().getAddress3() ));
//                        brow++;
//                    }
//                    StringBuilder sb = new StringBuilder();
//                    if (order.getCustomer().getCity() != null && order.getCustomer().getCity().length() > 0){
//                        sb.append(order.getCustomer().getCity()+", " );
//                    }
//                    if (order.getCustomer().getState() != null && order.getCustomer().getState().length() > 0){
//                        sb.append(order.getCustomer().getState()+". " );
//                    }
//                    if (order.getCustomer().getZip() != null && order.getCustomer().getZip().length() > 0){
//                        sb.append(order.getCustomer().getZip()+"  " );
//                    }
//                    if (order.getCustomer().getCountry() != null && order.getCustomer().getCountry().length() > 0){
//                        sb.append(order.getCustomer().getCountry() );
//                    }
//                    sheet.addCell(new Label( 0, brow, sb.toString() ));
//                }
//                row += 3;
//                if (order.getCustomer() != null){
//                    if (order.getCustomer().getPicklistComment() != null && order.getCustomer().getPicklistComment().length() > 0){
//                        sheet.addCell(new Label( colIndent, row, "Customer Picklist Comment:", bold ));
//                        row++;
//                        sheet.addCell(new Label( colIndent, row, order.getCustomer().getPicklistComment() ));
//                        row += 2;
//                    }
//                    if (order.getCustomer().getComment1() != null && order.getCustomer().getComment1().length() > 0){
//                        sheet.addCell(new Label( colIndent, row, "Customer Comment 1:", bold ));
//                        row++;
//                        sheet.addCell(new Label( colIndent, row, order.getCustomer().getComment1() ));
//                        row += 2;
//                    }
//                    if (order.getCustomer().getComment2() != null && order.getCustomer().getComment2().length() > 0){
//                        sheet.addCell(new Label( colIndent, row, "Customer Comment 2:", bold ));
//                        row++;
//                        sheet.addCell(new Label( colIndent, row, order.getCustomer().getComment2() ));
//                    }
//                }
//            } catch (Exception e){
//                logger.error("Could not write extra data", e);
//            }
//        }
        
    }
}
