package com.bc.actions.bookcountry;


import com.bc.dao.DaoResults;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.jasper.OrderDataSourceProvider;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.util.ActionRole;
import com.bc.util.OrderTitleComparator;
import com.bc.util.cache.CustomerCache;
import com.bc.util.Emailer;
import com.bc.util.XlsxExporter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Restrictions;


@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="emailinvoices", location="/WEB-INF/jsp/bookcountry/orders/emailinvoices.jsp"),
    @Result(name="customerorders", location="/WEB-INF/jsp/bookcountry/orders/customerorders.jsp"),
    @Result(name="customerinfo", location="/WEB-INF/jsp/bookcountry/orders/customerinfo.jsp")
})
public class OrderInvoiceEmailAction extends OrderInvoiceAction {
    
    private final Logger log = Logger.getLogger(OrderInvoiceEmailAction.class);

    private Long customerId;
    private Customer customer;
    private String orderIds;
    private String invoiceType;
    
    private String fromAddress;
    private String toAddresses;
    private String ccAddresses;
    private String bccAddresses;
    
    private String subject;
    private String bodyText;
    
    private Integer attachMode;
    
    private List<Customer> customers;
    
    private HashMap paramMap;
    
    @ActionRole({"BcOrderAdmin"})
    public String email(){
        customers = CustomerCache.getCustomers();
        return "emailinvoices";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String sendInvoiceEmail(){
        try {
            List<File> pdfFiles = new ArrayList<File>();
            StringTokenizer st = new StringTokenizer(orderIds, ",");
            while (st.hasMoreTokens()){
                Long orderId = Long.parseLong(st.nextToken());
                log.info("Creating order invoice pdf for order id "+orderId);
                OrderSessionLocal oSession = getOrderSession();
                
                InventoryItemSessionLocal iSession = getInventoryItemSession();
                
                CustomerOrder order = null;
                order = oSession.findById(orderId, "customer", "customerShipping", "customerOrderItems", "customerOrderItems.inventoryItem");
//                if (invoiceType.equals("barcodes")){
//                    order = oSession.findById(orderId, "customer", "customerShipping", "customerOrderItems", "customerOrderItems.inventoryItem");
//                } else {
//                    order = oSession.findById(orderId, "customer", "customerShipping", "customerOrderItems");
//                }
                
                if (order == null) {
                    log.warn("Order not found, id: "+orderId);
                    continue;
                }
                
                QueryInput qi = new QueryInput();
                qi.addAndCriterion(Restrictions.eq("customerOrder", order));
                DaoResults dr = oSession.findAllItems(qi, "inventoryItem");
                
                
                OrderDataSourceProvider datasource = new OrderDataSourceProvider();
//                ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(order.getCustomerOrderItems());
                ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(dr.getData());
                Collections.sort(items, new OrderTitleComparator());
                datasource.setup(order, items, true);
                
                
                log.info("Attach mode : " + this.attachMode);
                
                if ((this.attachMode & 1) == 1){
                    File xlsxFile = new File("/tmp/invoice-"+order.getInvoiceNumber()+".xlsx");
                    XlsxExporter.WriteInvoiceToFile(xlsxFile, order, items);
                    log.info("Completed exporting data to " + xlsxFile.getAbsolutePath());
                    pdfFiles.add(xlsxFile);
                }
                
                if ((this.attachMode & 2) == 2){
                    File pdfFile = new File("/tmp/invoice-"+order.getInvoiceNumber()+".pdf");
                    if (pdfFile.exists()) pdfFile.delete();

                    String reportName = "/WEB-INF/classes/invoice.jasper";
                    if (invoiceType.equals("barcodes")){
                        reportName = "/WEB-INF/classes/invoice-barcode.jasper";
                    } else if (invoiceType.equals("notshipped")){
                        reportName = "/WEB-INF/classes/invoice-salesorder.jasper";
                    }
                    JasperReport report = (JasperReport)JRLoader.loadObject(this.getClass().getResourceAsStream(reportName));
                    JasperPrint jp = JasperFillManager.fillReport(report, setupParamMap(order, false), datasource);
                    log.info("Exporting to pdf..." + pdfFile.getAbsolutePath());
                    JasperExportManager.exportReportToPdfStream(jp, new FileOutputStream(pdfFile));
                    if (pdfFile.exists())
                        log.info("Finished creating pdf");
                    pdfFiles.add(pdfFile);
                }
                
                //XlsxExporter.WriteInvoiceToFile(xlsxFile, order);
//                if (xlsFile.exists()) xlsFile.delete();
//                log.info("Exporting to xls..." + xlsFile.getAbsolutePath());
//                JExcelApiExporter exporterXLS = new JExcelApiExporter();
//                exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
//                exporterXLS.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, 65536);
//                exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_FILE, xlsFile);
//                exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
//                exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
//                exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
//                exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//                exporterXLS.exportReport();                
                
                log.info("Finished for order id "+orderId);
            }
            log.info("Created "+pdfFiles.size()+" invoices.");

            if (pdfFiles.size() > 0){
                String contentText = bodyText + "\n\n" +
//"<ul>\n" +"\n" +
                "Please do not reply to this message. Replies to this message are routed to an unmonitored mailbox. If you have questions please contact your Sales Representative at the following email: Ben@Bookcountryclearinghouse.com or Jerry@Bookcountryclearinghouse.com or Kelley@Bookcountryclearinghouse.com\n" +
                "Ben: 412-678-2400 Ext. 101\n" +
                "Jerry: 412-678-2400 Ext. 110\n" +
                "Kelley: 412-678-2400 Ext. 114\n" +
                "If you need the accounting office you may contact Teri via phone number 412-678-2400 Ext. 102 or email at Teri@Bookcountryclearinghouse.com\n" +
                "Thank you";
//"<li>Please do not reply to this message. Replies to this message are routed to an unmonitored mailbox. If you have questions please contact your Sales Representative at the following email: <a href=\"mailto:ben@bookcountryclearinghouse.com\">Ben@Bookcountryclearinghouse.com</a> or <a href=\"mailto:jerry@bookcountryclearinghouse.com\">Jerry@Bookcountryclearinghouse.com</a> or <a href=\"mailto:Kelley@Bookcountryclearinghouse.com\">Kelley@Bookcountryclearinghouse.com</a>.</li>\n" +
//"<li>Ben: 412-678-2400 Ext. 101</li>\n" +
//"<li>Jerry: 412-678-2400 Ext. 110</li>\n" +
//"<li>Kelley: 412-678-2400 Ext. 114</li>\n" +
//"<li>If you need the accounting office you may contact Teri via phone number 412-678-2400 Ext. 102 or email at <a href=\"mailto:Teri@Bookcountryclearinghouse.com\">Teri@Bookcountryclearinghouse.com</a></li>\n" +
//"<li>Thank you</li>\n" +
//"  </ul>\n" +
//"  </p>";
                boolean sent = Emailer.sendMail(toAddresses, fromAddress, ccAddresses, bccAddresses, subject, contentText, pdfFiles);
                log.info("Sent email successfully: "+sent);
                log.info("Finished sendInvoiceEmail");
            }
            
            setSuccess(true);
        } catch (Throwable t){
            log.error("Could not send the invoice email", t);
            setSuccess(false);
            setMessage("Could not send the email, there was a system error: "+t.getMessage());
        }
        return "status";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String getCustomerOrders(){
        //log.info("Getting customer orders...");
        CustomerSessionLocal custSession = getCustomerSession();
        customer = custSession.findById(customerId);
        OrderSessionLocal oSession = getOrderSession();
        QueryInput qi = new QueryInput(0, 100);
        qi.addAndCriterion(Restrictions.eq("customer", customer));
        qi.setSortCol("id");
        qi.setSortDir(QueryInput.SORT_DESC);
        queryResults = new QueryResults(oSession.findAll(qi));
        //log.info("Rendering orders: "+queryResults.getData().size());
        return "customerorders";
    }
    
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
    public String getCustomerInfo(){
        CustomerSessionLocal custSession = getCustomerSession();
        customer = custSession.findById(customerId);
        return "customerinfo";
    }
    
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(String ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    public String getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(String bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Integer getAttachMode(){
        return this.attachMode;
    }
    
    public void setAttachMode(Integer attachMode){
        this.attachMode = attachMode;
    }
        
}
