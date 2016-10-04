package com.bc.actions.bellwether;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.bc.actions.BaseAction;
import com.bc.ejb.bellwether.BellOrderSessionLocal;
import com.bc.jasper.BellOrderDataSourceProvider;
import com.bc.orm.*;
import com.bc.struts.result.JasperResult;
import com.bc.util.*;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
public class OrderInvoiceAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(OrderInvoiceAction.class);

    private String filename;

    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String execute(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        BellOrder order = oSession.findById(id, "bellCustomer", "bellCustomerShipping", "bellOrderItems");
        
        if (order == null){
            logger.error("Could not find order by id: "+id);
            return "404";
        }
        
        BellOrderDataSourceProvider datasource = new BellOrderDataSourceProvider();
        ArrayList<BellOrderItem> items = new ArrayList<BellOrderItem>(order.getBellOrderItems());
        Collections.sort(items, new BellOrderTitleComparator());
        datasource.setup(order, items, true);
        
        setJasperDatasource(datasource);
        
        setJasperParamMap(setupParamMap(order, false));
        setJasperReportName("invoice.jasper");
        setJasperFilename(filename);
        setJasperExportType(JasperResult.PDF);

        return "jasperreport";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String barcodes(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        BellOrder order = oSession.findById(id, "bellCustomer", "bellCustomerShipping", "bellOrderItems");
        
        if (order == null){
            logger.error("Could not find order by id: "+id);
            return "404";
        }
        
        BellOrderDataSourceProvider datasource = new BellOrderDataSourceProvider();
        ArrayList<BellOrderItem> items = new ArrayList<BellOrderItem>(order.getBellOrderItems());
        Collections.sort(items, new BellOrderTitleComparator());
        datasource.setup(order, items, true);
        
        setJasperDatasource(datasource);
        
        setJasperParamMap(setupParamMap(order, false));
        setJasperReportName("invoice-barcode.jasper");
        setJasperFilename(filename);
        setJasperExportType(JasperResult.PDF);

        return "jasperreport";
    }
    
    @ActionRole({"BellOrderAdmin", "BellOrderViewer"})
    public String notShipped(){
        BellOrderSessionLocal oSession = getBellOrderSession();
        BellOrder order = oSession.findById(id, "bellCustomer", "bellCustomerShipping", "bellOrderItems");
        
        if (order == null){
            logger.error("Could not find order by id: "+id);
            return "404";
        }
        
        BellOrderDataSourceProvider datasource = new BellOrderDataSourceProvider();
        ArrayList<BellOrderItem> items = new ArrayList<BellOrderItem>(order.getBellOrderItems());
        Collections.sort(items, new BellOrderTitleComparator());
        datasource.setup(order, items, true);
        
        setJasperDatasource(datasource);
        
        setJasperParamMap(setupParamMap(order, true));
        setJasperReportName("invoice-salesorder.jasper");
        setJasperFilename(filename);
        setJasperExportType(JasperResult.PDF);

        return "jasperreport";
    }
    
    private HashMap setupParamMap(BellOrder order, boolean notShipped){
        HashMap paramMap = new HashMap();
        
        BellCustomer customer = order.getBellCustomer();
        if (customer == null){
            customer = new BellCustomer();
        }
        BellCustomerShipping shipping = order.getBellCustomerShipping();
        if ((shipping == null || shipping.getAddress1() == null || shipping.getAddress1().length() == 0) && customer != null) {
            shipping = new BellCustomerShipping();
            shipping.setAddress1(customer.getAddress1());
            shipping.setAddress2(customer.getAddress2());
            shipping.setAddress3(customer.getAddress3());
            shipping.setCity(customer.getCity());
            shipping.setState(customer.getState());
            shipping.setZip(customer.getZip());
            shipping.setCountry(customer.getCountry());
        } else if (shipping == null){
            shipping = new BellCustomerShipping();
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
        
        List<BellOrderItem> orderItems = new ArrayList<BellOrderItem>(order.getBellOrderItems()); 
        Collections.sort(orderItems, new BellOrderItemTitleComparator());

        paramMap.put("title", "INVOICE");
        paramMap.put("balanceLabel", "Balance Due");
        if (order.getCreditMemo() != null && order.getCreditMemo()){
            paramMap.put("title", "CREDIT MEMO");
            paramMap.put("balanceLabel", "Total Credit");
        }
        addToParamMap(paramMap, "invoiceNumber", order.getInvoiceNumber());

        paramMap.put("fullBillingAddress", null);
        paramMap.put("fullShippingAddress", null);
        
        StringBuilder ba = new StringBuilder();
        appendToAddress(ba, null, "\n", customer.getContactName());
        appendToAddress(ba, null, "\n", customer.getCompanyName());
        appendToAddress(ba, null, "\n", customer.getAddress1());
        appendToAddress(ba, null, "\n", customer.getAddress2());
        appendToAddress(ba, null, "\n", customer.getAddress3());
        appendToAddress(ba, null, ", ", customer.getCity());
        appendToAddress(ba, null, ". ", customer.getState());
        appendToAddress(ba, null, " ", customer.getZip());
        appendToAddress(ba, null, null, customer.getCountry());
        ba.append("\n\n");
        appendToAddress(ba, "Work Phone - ", "\n", customer.getWorkPhone());
        appendToAddress(ba, "Home Phone - ", "\n", customer.getHomePhone());
        appendToAddress(ba, "Cell Phone - ", "\n", customer.getCellPhone());
        appendToAddress(ba, "Fax - ", null, customer.getFax());
        paramMap.put("fullBillingAddress", ba.toString());

        StringBuilder sa = new StringBuilder();
        appendToAddress(sa, null, "\n", shipping.getShippingName());
        appendToAddress(sa, null, "\n", shipping.getAddress1());
        appendToAddress(sa, null, "\n", shipping.getAddress2());
        appendToAddress(sa, null, "\n", shipping.getAddress3());
        appendToAddress(sa, null, ", ", shipping.getCity());
        appendToAddress(sa, null, ". ", shipping.getState());
        appendToAddress(sa, null, " ", shipping.getZip());
        appendToAddress(sa, null, null, shipping.getCountry());
        paramMap.put("fullShippingAddress", sa.toString());

        addToParamMap(paramMap, "customerPO", order.getPoNumber());
        addToParamMap(paramMap, "billingCompanyName", customer.getCompanyName());
        addToParamMap(paramMap, "comment1", order.getComment());
        addToParamMap(paramMap, "shippingInstructions", shipping.getComment());
        addToParamMap(paramMap, "customerCode", order.getCustomerCode());
        addToParamMap(paramMap, "customerName", customer.getContactName());
        addToParamMap(paramMap, "salesman", order.getSalesman());
        addToParamMap(paramMap, "customerTerms", customer.getTerms());
        addToParamMap(paramMap, "customerPONumber", order.getPoNumber());
        addToParamMap(paramMap, "shipVia", order.getShipVia());
        addToParamMap(paramMap, "terms", customer.getTerms());
        addDateToParamMap(paramMap, "shipDateString", order.getShipDate());
        if (order.getShipDate() == null){
            addDateToParamMap(paramMap, "cornerDateString", order.getOrderDate());
            addDateToParamMap(paramMap, "invoiceDate", order.getOrderDate());
        } else {
            addDateToParamMap(paramMap, "cornerDateString", order.getShipDate());
            addDateToParamMap(paramMap, "invoiceDate", order.getShipDate());
        }
        addDateToParamMap(paramMap, "orderDateString", order.getOrderDate());

        paramMap.put("itemCountTotal", "0");
        if (order.getBellOrderItems() != null){
            paramMap.put("itemCountTotal", ""+order.getBellOrderItems().size());
        }
        
        
        /*
        */
        if (notShipped){
            // add up the extendeds
            BigDecimal extendedTotal = new BigDecimal(0);
            Integer quantityTotal = 0;
            paramMap.put("itemCountTotal", "0");
            if (order.getBellOrderItems() != null){
                paramMap.put("itemCountTotal", ""+order.getBellOrderItems().size());
                ArrayList items = new ArrayList(order.getBellOrderItems());
                for (int i = 0; i < items.size(); i++){
                    BellOrderItem boi = (BellOrderItem)items.get(i);
                    extendedTotal = extendedTotal.add(boi.getTotalPriceNonShippedWithCredit());
                    quantityTotal += boi.getQuantity();
                }
            }
            BigDecimal total = new BigDecimal(extendedTotal.doubleValue());
            paramMap.put("tax", "$0.00");
            if (customer.getTax() != null && customer.getTax()){
                BigDecimal totalWithTax = total.multiply(Constants.TAX);
                BigDecimal tax = totalWithTax.subtract(total);
                paramMap.put("tax", formatMoney(MoneyRound.round(tax)));
                total = totalWithTax;
            }
            paramMap.put("shipping", "$0.00");
            if (order.getShippingCharges() != null){
                paramMap.put("shipping", formatMoney(order.getShippingCharges()));
                total = total.add(new BigDecimal(order.getShippingCharges()));
            }
            paramMap.put("palleteCharge", "$0.00");
            //if (order.getPalleteCharge() != null){
            //    paramMap.put("palleteCharge", formatMoney(order.getPalleteCharge()));
            //    total = total.add(new BigDecimal(order.getPalleteCharge()));
            //}
            paramMap.put("deposit", "$0.00");
            paramMap.put("balanceDue", formatMoney(total));
            if (order.getDepositAmmount() != null){
                paramMap.put("deposit", formatMoney(order.getDepositAmmount()));
                BigDecimal bal = total.subtract(new BigDecimal(order.getDepositAmmount()));
                paramMap.put("balanceDue", formatMoney(MoneyRound.round(bal)));
            }

            paramMap.put("extendedTotal", formatMoney(MoneyRound.round(extendedTotal)));
            paramMap.put("total", formatMoney(MoneyRound.round(total)));
            
        } else {
            paramMap.put("extendedTotal", formatMoney(order.getTotalPricePreTax())); // TODO have to handle credit
            paramMap.put("tax", formatMoney(order.getTotalTax()));
            paramMap.put("shipping", formatMoney(order.getShippingCharges()));
            //paramMap.put("palleteCharge", formatMoney(order.getPalleteCharge()));
            paramMap.put("deposit", formatMoney(order.getDepositAmmount()));
            paramMap.put("balanceDue", formatMoney(order.getBalanceDue()));
            paramMap.put("total", formatMoney(order.getTotalPrice()));
        }
        
        paramMap.put("shippedQuantityTotal", "0");
        if (order.getTotalQuantity() != null){
            paramMap.put("shippedQuantityTotal", order.getTotalQuantity().toString());
        }

        return paramMap;
    }
    
    private void appendToAddress(StringBuilder add, String pre, String post, String ob){
        if (ob != null && ob.length() > 0){
            if (pre != null) add.append(pre);
            add.append(ob);
            if (post != null) add.append(post);
        }        
    }
    
    private void addToParamMap(HashMap paramMap, String key, Object ob){
        if (ob != null){
            paramMap.put(key, ob);
        } else {
            paramMap.put(key, "");
        }
    }

    private void addDateToParamMap(HashMap paramMap, String key, Object ob){
        if (ob != null){
            paramMap.put(key, DateFormat.format((Date)ob));
        } else {
            paramMap.put(key, "");
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
