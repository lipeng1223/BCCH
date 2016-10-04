package com.bc.actions.bellwether;

import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.excel.ExcelReport;
import com.bc.excel.ExcelReportExporter;
import com.bc.jasper.BellwetherPackingSlipData;
import com.bc.jasper.BellwetherPackingSlipDataSourceProvider;
import com.bc.jasper.BellwetherShippingData;
import com.bc.jasper.BellwetherShippingDataSourceProvider;
import com.bc.orm.BellInventory;
import com.bc.orm.BellOrder;
import com.bc.orm.InventoryItem;
import com.bc.struts.result.JasperResult;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.util.ActionRole;
import com.bc.util.Emailer;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="bellhomepage", location="/WEB-INF/jsp/bellwether/home.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")    
})
public class FillzAction extends FillzBaseAction {
    
    private static final Logger logger = Logger.getLogger(FillzAction.class);
    
    private Float weekdayUpgrade = 30F;
    private Float weekendUpgrade = 40F;
    private Integer excelSheet = 0;
    private String slipsType;
 
    @ActionRole({"BellInvViewer"})
	public String priority(){

        
        try {
            List<BellOrder> all = readUpload(0, true);
            
            if (all == null){
                return "status";
            }
            
            ArrayList<BellOrder> priority = new ArrayList<BellOrder>();
            ArrayList<BellOrder> global = new ArrayList<BellOrder>();
            ArrayList<BellOrder> globalPriority = new ArrayList<BellOrder>();
            ArrayList<BellOrder> standard = new ArrayList<BellOrder>();
            ArrayList<BellOrder> inv = new ArrayList<BellOrder>();
            ArrayList<BellOrder> consolidated = new ArrayList<BellOrder>();
            HashSet<String> addressCheck = new HashSet<String>();
            
            for(BellOrder bo : all) {
                boolean weekend = false;
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(bo.getPurchaseDate().getTime()));
                cal.setTimeZone(TimeZone.getTimeZone("PST"));
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                {
                    weekend = true;
                }
                
                // if shipaddress1 is dupe then go to consolidated
                boolean dupe = false;
                if (bo.getShipAddress1() != null && bo.getShipAddress1().length() > 0 && addressCheck.contains(bo.getShipAddress1())){
                    boolean found = false;
                    for (BellOrder check : globalPriority){
                        if (check.getShipAddress1().equals(bo.getShipAddress1())){
                            globalPriority.remove(check);
                            consolidated.add(check);
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        for (BellOrder check : priority){
                            if (check.getShipAddress1().equals(bo.getShipAddress1())){
                                priority.remove(check);
                                consolidated.add(check);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found){
                        for (BellOrder check : standard){
                            if (check.getShipAddress1().equals(bo.getShipAddress1())){
                                standard.remove(check);
                                consolidated.add(check);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found){
                        for (BellOrder check : global){
                            if (check.getShipAddress1().equals(bo.getShipAddress1())){
                                global.remove(check);
                                consolidated.add(check);
                                found = true;
                                break;
                            }
                        }
                    }
                    consolidated.add(bo);
                    dupe = true;
                } else if (bo.getShipAddress1() != null && bo.getShipAddress1().length() > 0){
                    addressCheck.add(bo.getShipAddress1());
                }
                
                if (!dupe){
                    if (bo.getRecipientName() != null && bo.getRecipientName().toLowerCase().equals("ship via alibris")){
                        consolidated.add(bo);
                    } else if (!bo.getShipCountry().toUpperCase().equals("US") && !bo.getShipCountry().toUpperCase().equals("USA") && !bo.getShipCountry().toUpperCase().equals("U.S.A") && !bo.getShipCountry().toUpperCase().equals("U.S.A") && bo.getPrice() > 80){
                        globalPriority.add(bo);
                    } else if (bo.getSpecialComments().startsWith("AmazonUK/")){
                        globalPriority.add(bo);
                    } else if (bo.getShipMethod().equals("International Expedited 6 - 10 days")){
                        globalPriority.add(bo);
                    } else if (bo.getShipState().toUpperCase().equals("AE") || bo.getShipState().toUpperCase().equals("AP") || bo.getShipState().toUpperCase().equals("APO") || bo.getShipState().toUpperCase().equals("FPO")) {
                        globalPriority.add(bo);
                    } else if (!bo.getShipCountry().toUpperCase().equals("US") && !bo.getShipCountry().toUpperCase().equals("USA") && !bo.getShipCountry().toUpperCase().equals("U.S.A")&& !bo.getShipCountry().toUpperCase().equals("U.S.A.")){
                        global.add(bo);
                    } else if (bo.getSpecialComments().toLowerCase().startsWith("half") && shipForHalf(bo)){
                        consolidated.add(bo);
                    } else if (bo.getSku().contains("~")) {
                            inv.add(bo);
                    } else if (bo.getShipState().toUpperCase().equals("AK") || bo.getShipState().toUpperCase().equals("HI") ||
                               bo.getShipState().toUpperCase().equals("ALASKA") || bo.getShipState().toUpperCase().equals("HAWAII")) 
                    {
                        priority.add(bo);
                    } else if (bo.getShipMethod().toLowerCase().equals("expedited") || bo.getShipMethod().equals("USPS Express") ||
                            bo.getShipMethod().equals("USPS Priority or UPS Ground") ||
                            bo.getShipMethod().equals("International Expedited 5 - 10 days")) 
                    {
                        priority.add(bo);
                    } else if (bo.getPrice() >= weekendUpgrade && weekend) {
                        priority.add(bo);
                    } else if (bo.getPrice() >= weekdayUpgrade && !weekend){
                        priority.add(bo);
                    } else {
                        standard.add(bo);
                    }
                }
            }

            HashSet<BellOrder> toremove = new HashSet<BellOrder>();
            for (BellOrder bo : global){
                if (bo.getSpecialComments() != null && (bo.getSpecialComments().startsWith("Biblio") || bo.getSpecialComments().startsWith("AmazonFR"))){
                    globalPriority.add(bo);
                    toremove.add(bo);
                } else if (bo.getShipCountry() != null && 
                        (bo.getShipCountry().toUpperCase().equals("PR") || 
                        bo.getShipCountry().toUpperCase().equals("PRI") ||
                        bo.getShipCountry().toUpperCase().equals("PUERTO RICO"))) 
                {
                    priority.add(bo);
                    toremove.add(bo);
                } else if (bo.getShipMethod().toLowerCase().equals("expedited") || 
                        bo.getShipMethod().equals("International Expedited 6 - 10 days") ||
                        bo.getShipMethod().equals("Air") ||
                        bo.getShipMethod().equals("Airmail") ||
                        bo.getShipMethod().equals("IPAROW") )
                {
                    globalPriority.add(bo);
                    toremove.add(bo);
                    
                }
            }
            for (BellOrder bo : toremove) global.remove(bo);
            
            toremove.clear();
            for (BellOrder bo : globalPriority){
                if (bo.getShipCountry() != null && 
                        (bo.getShipCountry().toUpperCase().equals("PR") || 
                        bo.getShipCountry().toUpperCase().equals("PRI") ||
                        bo.getShipCountry().toUpperCase().equals("PUERTO RICO"))) 
                {
                    priority.add(bo);
                    toremove.add(bo);
                }
            }
            for (BellOrder bo : toremove) globalPriority.remove(bo);
            
            // move all ship-method to Expedited for Priority
            for (BellOrder bo : priority){
                bo.setShipMethod("Expedited");
            }
            
            toremove.clear();
            for (BellOrder bo : consolidated){
                if (bo.getRecipientName() != null && (bo.getRecipientName().toLowerCase().equals("ship via alibris") || bo.getRecipientName().toLowerCase().startsWith("bww-alibris"))){
                    inv.add(bo);
                    toremove.add(bo);
                }
            }
            for (BellOrder bo : toremove) consolidated.remove(bo);
            toremove.clear();
            
            
            // sort by sku
            BellOrder[] sort = new BellOrder[priority.size()];
            priority.toArray(sort);
            priority.clear();
            Arrays.sort(sort, new BOLocationComparator());
            priority.addAll(Arrays.asList(sort));
            sort = new BellOrder[global.size()];
            global.toArray(sort);
            global.clear();
            Arrays.sort(sort, new BOComparator());
            global.addAll(Arrays.asList(sort));
            sort = new BellOrder[globalPriority.size()];
            globalPriority.toArray(sort);
            globalPriority.clear();
            Arrays.sort(sort, new BOComparator());
            globalPriority.addAll(Arrays.asList(sort));
            sort = new BellOrder[standard.size()];
            standard.toArray(sort);
            standard.clear();
            Arrays.sort(sort, new BOComparator());
            standard.addAll(Arrays.asList(sort));
            sort = new BellOrder[inv.size()];
            inv.toArray(sort);
            inv.clear();
            Arrays.sort(sort, new BOComparator());
            inv.addAll(Arrays.asList(sort));
            sort = new BellOrder[consolidated.size()];
            consolidated.toArray(sort);
            consolidated.clear();
            Arrays.sort(sort, new BOComparator());
            consolidated.addAll(Arrays.asList(sort));
            
            // setup the column data and column models and the results

            LinkedHashMap<String, List> sheets = new LinkedHashMap<String, List>();
            sheets.put("Priority", priority);
            sheets.put("Global Priority", globalPriority);
            sheets.put("Global", global);
            sheets.put("Standard", standard);
            sheets.put("Inv", inv);
            sheets.put("Consolidated", consolidated);
            
            /*
            logger.info("priority size: "+priority.size());
            logger.info("globalPriority size: "+globalPriority.size());
            logger.info("global size: "+global.size());
            logger.info("standard size: "+standard.size());
            logger.info("inv size: "+inv.size());
            logger.info("consolidated size: "+consolidated.size());
            */
            
            List<ColumnData> columnDatas = new ArrayList<ColumnData>();
            columnDatas.add(new ColumnData("paymentsStatus"));
            columnDatas.add(new ColumnData("orderId"));
            columnDatas.add(new ColumnData("orderItemId"));
            columnDatas.add(new ColumnData("paymentsDateFormattedWithTzPst"));
            columnDatas.add(new ColumnData("paymentsTransactionId").setType("int"));
            columnDatas.add(new ColumnData("productId"));
            columnDatas.add(new ColumnData("itemName"));
            columnDatas.add(new ColumnData("listingId"));
            columnDatas.add(new ColumnData("sku"));
            columnDatas.add(new ColumnData("price").setType("float"));
            columnDatas.add(new ColumnData("shippingFee").setType("float"));
            columnDatas.add(new ColumnData("quantityPurchased").setType("int"));
            columnDatas.add(new ColumnData("totalPrice").setType("float"));
            columnDatas.add(new ColumnData("purchaseDateFormattedWithTzPst"));
            columnDatas.add(new ColumnData("batchId").setType("int"));
            columnDatas.add(new ColumnData("buyerEmail"));
            columnDatas.add(new ColumnData("buyerName"));
            columnDatas.add(new ColumnData("recipientName"));
            columnDatas.add(new ColumnData("shipAddress1"));
            columnDatas.add(new ColumnData("shipAddress2"));
            columnDatas.add(new ColumnData("shipCity"));
            columnDatas.add(new ColumnData("shipState"));
            columnDatas.add(new ColumnData("shipZip"));
            columnDatas.add(new ColumnData("shipCountry"));
            columnDatas.add(new ColumnData("specialComments"));
            columnDatas.add(new ColumnData("upc"));
            columnDatas.add(new ColumnData("shipMethod"));
            columnDatas.add(new ColumnData("fillzStatus"));
            columnDatas.add(new ColumnData("location"));
            columnDatas.add(new ColumnData("tracking"));
            columnDatas.add(new ColumnData("buyerNote"));
            columnDatas.add(new ColumnData("sellerNote"));
            columnDatas.add(new ColumnData("paymentMethod"));
            columnDatas.add(new ColumnData("itemCondition"));
            columnDatas.add(new ColumnData("itemSource"));
            columnDatas.add(new ColumnData("fillzCost").setType("float"));
            columnDatas.add(new ColumnData("isBook"));
            columnDatas.add(new ColumnData("sellerId"));
            
            List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
            columnModels.add(new ColumnModel("paymentsStatus", "payments-status", 100));
            columnModels.add(new ColumnModel("orderId", "order-id", 100));
            columnModels.add(new ColumnModel("orderItemId", "order-item-id", 100));
            columnModels.add(new ColumnModel("paymentsDateFormattedWithTzPst", "payments-date", 100));
            columnModels.add(new ColumnModel("paymentsTransactionId", "payments-transaction-id", 100));
            columnModels.add(new ColumnModel("productId", "product-id", 100));
            columnModels.add(new ColumnModel("itemName", "item-name", 100));
            columnModels.add(new ColumnModel("listingId", "listing-id", 100));
            columnModels.add(new ColumnModel("sku", "sku", 100));
            columnModels.add(new ColumnModel("price", "price", 100));
            columnModels.add(new ColumnModel("shippingFee", "shipping-fee", 100));
            columnModels.add(new ColumnModel("quantityPurchased", "quantity-purchased", 100));
            columnModels.add(new ColumnModel("totalPrice", "total-price", 100));
            columnModels.add(new ColumnModel("purchaseDateFormattedWithTzPst", "purchase-date", 100));
            columnModels.add(new ColumnModel("batchId", "batch-id", 100));
            columnModels.add(new ColumnModel("buyerEmail", "buyer-email", 100));
            columnModels.add(new ColumnModel("buyerName", "buyer-name", 100));
            columnModels.add(new ColumnModel("recipientName", "recipient-name", 100));
            columnModels.add(new ColumnModel("shipAddress1", "ship-address-1", 100));
            columnModels.add(new ColumnModel("shipAddress2", "ship-address-2", 100));
            columnModels.add(new ColumnModel("shipCity", "ship-city", 100));
            columnModels.add(new ColumnModel("shipState", "ship-state", 100));
            columnModels.add(new ColumnModel("shipZip", "ship-zip", 100));
            columnModels.add(new ColumnModel("shipCountry", "ship-country", 100));
            columnModels.add(new ColumnModel("specialComments", "special-comments", 100));
            columnModels.add(new ColumnModel("upc", "upc", 100));
            columnModels.add(new ColumnModel("shipMethod", "ship-method", 100));
            columnModels.add(new ColumnModel("fillzStatus", "status", 100));
            columnModels.add(new ColumnModel("location", "location", 100));
            columnModels.add(new ColumnModel("tracking", "tracking", 100));
            columnModels.add(new ColumnModel("buyerNote", "buyer-note", 100));
            columnModels.add(new ColumnModel("sellerNote", "seller-note", 100));
            columnModels.add(new ColumnModel("paymentMethod", "payment-method", 100));
            columnModels.add(new ColumnModel("itemCondition", "item-condition", 100));
            columnModels.add(new ColumnModel("itemSource", "item-source", 100));
            columnModels.add(new ColumnModel("fillzCost", "cost", 100));
            columnModels.add(new ColumnModel("isBook", "is-book", 100));
            columnModels.add(new ColumnModel("sellerId", "seller-id", 100));
            
            ExcelReport report = new ExcelReport(sheets, columnDatas, columnModels);
            report.setHeaderRowFormatPlain();
            ExcelReportExporter ere = new ExcelReportExporter(report);
            setExcelExportFileName("FillzPriority");
            setExcelReportExporter(ere);
            
        } catch (Throwable t){
            logger.error("Failed doing Fillz Priority Upload", t);
            return "error";
        }
        
		return "excelreport";
	}
    
    
    @ActionRole({"BellInvViewer"})
    public String slips(){

        
        try {
            List<BellOrder> all = readUpload(excelSheet, false);
            
            if (all == null){
                return "status";
            }
            
            boolean packing = "packing".equals(slipsType);
            boolean oneitem = "packingoneitem".equals(slipsType);
            
            List<BellwetherPackingSlipData> allPackingSlipData = new ArrayList<BellwetherPackingSlipData>();
            List<BellwetherShippingData> allShippingData = new ArrayList<BellwetherShippingData>();
            for (BellOrder bo : all){
                if (packing || oneitem){
                    BellwetherPackingSlipData bpsd = new BellwetherPackingSlipData();
                    bpsd.setSkuAndItemName(bo.getSku().toUpperCase()+bo.getItemName().toUpperCase());
    
                    String val = null;
                    if (bo.getOrderId() != null){
                        bpsd.setOrderNumber(bo.getOrderId());
                    }
                    // no idea what to put in the comment
                    //bpsd.setComment(bo.getSpecialComments());
                    bpsd.setTitle(bo.getItemName());
                    // showing the seller site in the listing id
                    bpsd.setListingId(bo.getSpecialComments());
                    bpsd.setSku(bo.getSku());
                    bpsd.setQuantity(bo.getQuantityPurchased().toString());
                    bpsd.setLocation(bo.getLocation());
                    
                    String buyer = "";
                    val = bo.getBuyerName();
                    if (val != null){
                        buyer += val;
                    }
                    val = bo.getBuyerEmail();
                    if (val != null && val.length() > 0){
                        buyer += ", ";
                        buyer += val;
                    }
                    bpsd.setBuyer(buyer);
                    bpsd.setShip(bo.getShipMethod());
                    StringBuilder recipient = new StringBuilder();
                    val = bo.getRecipientName();
                    if (val != null){
                        recipient.append(val);
                    }
                    val = bo.getShipAddress1();
                    if (val != null){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    val = bo.getShipAddress2();
                    if (val != null){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    val = bo.getShipCity();
                    if (val != null){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    val = bo.getShipState();
                    if (val != null){
                        recipient.append(val);
                        recipient.append(", ");
                    }
                    val = bo.getShipCountry();
                    if (val != null){
                        recipient.append(" ");
                        recipient.append(val);
                    }
                    val = bo.getShipZip();
                    if (val != null){
                        recipient.append(" ");
                        recipient.append(val);
                    }
                    bpsd.setRecipient(recipient.toString());
                    allPackingSlipData.add(bpsd);
                } else {
                    BellwetherShippingData bpsd = new BellwetherShippingData();
                    bpsd.setSkuAndItemName(bo.getSku().toUpperCase()+bo.getItemName().toUpperCase());
                    
                    if (bo.getOrderId() != null){
                        bpsd.setOrderid(bo.getOrderId());
                    }
                    String val = null;
                    StringBuilder recipient = new StringBuilder();
                    val = bo.getRecipientName();
                    if (val != null && val.length() > 0){
                        recipient.append(val);
                    }
                    val = bo.getShipAddress1();
                    if (val != null && val.length() > 0){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    val = bo.getShipAddress2();
                    if (val != null && val.length() > 0){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    val = bo.getShipCity();
                    if (val != null && val.length() > 0){
                        recipient.append("\n");
                        recipient.append(val);
                        recipient.append(", ");
                    }
                    val = bo.getShipState();
                    if (val != null && val.length() > 0){
                        recipient.append(val);
                        recipient.append(". ");
                    }
                    val = bo.getShipZip();
                    if (val != null && val.length() > 0){
                        recipient.append(" ");
                        recipient.append(val);
                    }
                    val = bo.getShipCountry();
                    if (val != null && val.length() > 0){
                        recipient.append("\n");
                        recipient.append(val);
                    }
                    bpsd.setRecipient(recipient.toString());
                    allShippingData.add(bpsd);
                }
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
            Calendar now = Calendar.getInstance();
            if (oneitem){
                BellwetherPackingSlipDataSourceProvider dataSource =
                    new BellwetherPackingSlipDataSourceProvider();
                dataSource.setup(allPackingSlipData);
                
                setJasperDatasource(dataSource);
                setJasperReportName("bwpackinglist-oneitem.jasper");
                setJasperFilename("FillzPackingSlips-"+sdf.format(now.getTime())+".pdf");
            } else if (packing){
                BellwetherPackingSlipDataSourceProvider dataSource =
                    new BellwetherPackingSlipDataSourceProvider();
                dataSource.setup(allPackingSlipData);
                
                setJasperDatasource(dataSource);
                setJasperReportName("bwpackinglist.jasper");
                setJasperFilename("FillzPackingSlips-"+sdf.format(now.getTime())+".pdf");
            } else {
                // shipping
                BellwetherShippingDataSourceProvider dataSource =
                    new BellwetherShippingDataSourceProvider();
                dataSource.setup(allShippingData);
                
                setJasperDatasource(dataSource);
                setJasperReportName("bwshippinglabels.jasper");
                setJasperFilename("FillzShippingLabels-"+sdf.format(now.getTime())+".pdf");
            }
            setJasperExportType(JasperResult.PDF);
            
        } catch (Throwable t){
            logger.error("Failed doing something", t);
            return "error";
        }
        
        return "jasperreport";
    }

    
    @ActionRole({"BellInvViewer"})
    public String importOrders(){
        try {
            logger.info("importOrders from "+getUploadFileName());
            
            List<BellOrder> all = readUpload(0, true);
            if (all == null){
                return "status";
            }
            
            BellOrderSessionLocal boSession = getBellOrderSession();
            BellInventorySessionLocal biSession = getBellInventorySession();
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            
            List<String> missing = new ArrayList<String>();
            
            List<BellOrder> creates = new ArrayList<BellOrder>();
            List<BellOrder> updates = new ArrayList<BellOrder>();
            HashSet<String> processedOrderIds = new HashSet<String>();
            
            for (BellOrder fillzBo : all){
                if (processedOrderIds.contains(fillzBo.getOrderId()+"-"+fillzBo.getSku())){
                    logger.info("Dupe found on sheet for orderId: "+fillzBo.getOrderId()+" sku: "+fillzBo.getSku());
                    continue;
                }
                processedOrderIds.add(fillzBo.getOrderId()+"-"+fillzBo.getSku());
                BellOrder bo = boSession.findByOrderIdAndSku(fillzBo.getOrderId(), fillzBo.getSku());
                boolean newOrder = false;
                if (bo == null){
                    bo = fillzBo;
                    bo.setUserId(1);
                    bo.setOrderHandlingState(0);
                    newOrder = true;
                }
                bo.setOrderItemId(fillzBo.getOrderItemId());
                bo.setPaymentsDate(fillzBo.getPaymentsDate());
                bo.setPaymentMethod(fillzBo.getPaymentMethod());
                bo.setPaymentsStatus(fillzBo.getPaymentsStatus());
                bo.setPaymentsTransactionId(fillzBo.getPaymentsTransactionId());
                bo.setItemName(fillzBo.getItemName());
                if (bo.getItemName().length() > 100){
                    bo.setItemName(bo.getItemName().substring(0, 99));
                }
                bo.setListingId(fillzBo.getListingId());
                bo.setPrice(fillzBo.getPrice());
                bo.setShippingFee(fillzBo.getShippingFee());
                bo.setQuantityPurchased(fillzBo.getQuantityPurchased());
                bo.setTotalPrice(fillzBo.getTotalPrice());
                bo.setShippingFee(fillzBo.getShippingFee());
                bo.setPurchaseDate(fillzBo.getPurchaseDate());
                bo.setBatchId(fillzBo.getBatchId());
                bo.setBuyerEmail(fillzBo.getBuyerEmail());
                bo.setRecipientName(fillzBo.getRecipientName());
                bo.setLocation(fillzBo.getLocation());
                bo.setShipAddress1(fillzBo.getShipAddress1());
                bo.setShipAddress2(fillzBo.getShipAddress2());
                bo.setShipCity(fillzBo.getShipCity());
                bo.setShipState(fillzBo.getShipState());
                bo.setShipZip(fillzBo.getShipZip());
                bo.setShipCountry(fillzBo.getShipCountry());
                bo.setSpecialComments(fillzBo.getSpecialComments());
                bo.setUpc(fillzBo.getUpc());
                bo.setShipMethod(fillzBo.getShipMethod());
                if (bo.getShipMethod() == null){
                    bo.setShipMethod("standard");
                }
                if (bo.getShipMethod().equalsIgnoreCase("International Expedited 5 - 10 days")){
                    bo.setShipMethod("expedited");
                } else  if (bo.getShipMethod().equalsIgnoreCase("International Standard 21 - 36 days")){
                    bo.setShipMethod("standard");
                } else if (bo.getShipMethod().equalsIgnoreCase("USPS Priority or UPS Ground")){
                    bo.setShipMethod("expedited");
                }
                if (bo.getShipMethod().length() > 30){
                    bo.setShipMethod(bo.getShipMethod().substring(0, 29));
                }
                bo.setCategory("Amazon");
                
                if (newOrder){
                    BellInventory bi = biSession.findByIsbn(bo.getIsbn());
                    if (bi == null){
                        // try isbn 13
                        bi = biSession.findByIsbn(bo.getIsbn13());
                    }
                    if (bi != null && bi.getBellBook() != null && !bi.getBellBook()){
                        // go after the sell price on bookcountry to set cost
                        InventoryItem ii = iiSession.findByIsbnCond(bo.getIsbn(), "hurt");
                        if (ii != null){
                            bo.setCost(ii.getSellingPrice());
                        } else {
                            ii = iiSession.findByIsbnCond(bo.getIsbn(), "overstock");
                            if (ii != null){
                                bo.setCost(ii.getSellingPrice());
                            } else {
                                ii = iiSession.findByIsbnCond(bo.getIsbn(), "unjacketed");
                                if (ii != null){
                                    bo.setCost(ii.getSellingPrice());
                                }
                            }                            
                        }
                    } else if (bi != null){
                        bo.setCost(bi.getCost());
                    } else if (bi == null){
                        // missing
                        missing.add(bo.getIsbn()+" - "+bo.getSku()+" - "+bo.getItemName());
                    }
                    
                    if (bi != null){
                        if (!bo.getSku().contains("~")){
                            bi.setOnhand(bi.getOnhand()-bo.getQuantityPurchased());
                            biSession.update(bi);
                        }
                    }
                    creates.add(bo);
                    boSession.create(bo);
                } else {
                    updates.add(bo);
                    boSession.update(bo);
                }
                
            }
            
            logger.info("Created "+creates.size()+" bell orders");
            logger.info("Updated "+updates.size()+" bell orders");
            
            if (missing != null && missing.size() > 0){
                // send an email about the missing isbns in the system
                StringBuilder sb = new StringBuilder();
                sb.append("Missing ISBN's in Bellwether inventory:\n\n");
                for (String s : missing){
                    sb.append(s);
                    sb.append("\n");
                }
                Emailer.sendMail(new String[]{"kelley@bookcountryclearinghouse.com", "megela@gmail.com"}, "FillZ import missing ISBN's", sb.toString());
                //Emailer.sendMail(new String[]{"kelley@bookcountryclearinghouse.com", "mick@bellwetherbooks.net", "megela@gmail.com"}, "FillZ import missing ISBN's", sb.toString());
            }
            
            creates = null;
            updates = null;
            all = null;
            processedOrderIds = null;
            
            setMessage("Imported FillZ orders.");
            setSuccess(true);
        } catch (Throwable t){
            logger.error("Failed importing FillZ orders", t);
            setMessage("System failed importing FillZ orders: "+t.getMessage());
            setSuccess(false);
        }
    
        return "status";
    }
    
    
    private class BOComparator implements Comparator<BellOrder> {
        public int compare(BellOrder bo1, BellOrder bo2) {
            return bo1.getSku().toUpperCase().compareTo(bo2.getSku().toUpperCase());
        }
    }

    private class BOLocationComparator implements Comparator<BellOrder> {
        public int compare(BellOrder bo1, BellOrder bo2) {
            return bo1.getLocation().toUpperCase().compareTo(bo2.getLocation().toUpperCase());
        }
    }

    private boolean shipForAbebooks(BellOrder bo){
        HashSet<Float> fees = new HashSet<Float>();
        fees.add(3.99F);
        fees.add(6.99F);
        fees.add(8F);
        fees.add(9F);
        fees.add(12.49F);
        fees.add(17.50F);
        return !fees.contains(bo.getShippingFee());
    }
    
    private boolean shipForAlibris(BellOrder bo){
        return bo.getShippingFee() == 0F && (bo.getShipMethod().toUpperCase().equals("SEE MANIFEST") || bo.getShipMethod().toUpperCase().equals("CHECK MANIFEST"));
    }
    
    private boolean shipForAmazon(BellOrder bo){
        HashSet<Float> fees = new HashSet<Float>();
        fees.add(3.99F);
        fees.add(6.99F);
        fees.add(12.49F);
        return !fees.contains(bo.getShippingFee());
    }
    
    private boolean shipForHalf(BellOrder bo){
        return bo.getShippingFee() == 0F;
    }
    
    public Float getWeekdayUpgrade() {
        return weekdayUpgrade;
    }

    public void setWeekdayUpgrade(Float weekdayUpgrade) {
        this.weekdayUpgrade = weekdayUpgrade;
    }

    public Float getWeekendUpgrade() {
        return weekendUpgrade;
    }

    public void setWeekendUpgrade(Float weekendUpgrade) {
        this.weekendUpgrade = weekendUpgrade;
    }


    public Integer getExcelSheet() {
        return excelSheet;
    }


    public void setExcelSheet(Integer excelSheet) {
        this.excelSheet = excelSheet;
    }


    public String getSlipsType() {
        return slipsType;
    }


    public void setSlipsType(String slipsType) {
        this.slipsType = slipsType;
    }

	
}
