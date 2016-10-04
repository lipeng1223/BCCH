package com.bc.socketserver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.bc.actions.AmazonLookup;
import com.bc.ejb.CustomerSession;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.InventoryItemSession;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.ManifestSession;
import com.bc.ejb.ManifestSessionLocal;
import com.bc.ejb.OrderSession;
import com.bc.ejb.OrderSessionLocal;
import com.bc.ejb.ReceivingSession;
import com.bc.ejb.ReceivingSessionLocal;
import com.bc.ejb.UserSession;
import com.bc.ejb.UserSessionLocal;
import com.bc.ejb.VendorSession;
import com.bc.ejb.VendorSessionLocal;
import com.bc.ejb.bellwether.BellInventorySession;
import com.bc.ejb.bellwether.BellInventorySessionLocal;
import com.bc.orm.BellInventory;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.orm.Manifest;
import com.bc.orm.ManifestItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.User;
import com.bc.orm.UserRole;
import com.bc.orm.Vendor;
import com.bc.struts.QueryInput;
import com.bc.util.DateFormat;
import com.bc.util.IsbnUtil;
import com.bc.util.PriceFormat;
import com.bc.util.ThreadContext;

public class SocketServerHandler  extends SimpleChannelUpstreamHandler {

    private Logger logger = Logger.getLogger(SocketServerHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        String msg = (String)e.getMessage();
        
        logger.info(msg);
        
        String resp = null;
        try {
            if (msg != null && msg.length() > 0) {
                resp = processMessage(msg);
            }
        } catch (Exception ex){
            logger.error("Could not process msg: "+msg, ex);
        }
        if (resp == null){
            resp = "Error:System Error";
        }
        
        ChannelFuture future = e.getChannel().write(resp);
        
        future.addListener(ChannelFutureListener.CLOSE); // close the channel after we write back
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.warn("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }   
    
    /*
     * All messages should be command:pin:input
     */
    private String processMessage(String msg) throws Exception {

        StringTokenizer st = new StringTokenizer(msg, ":");
        Date now = Calendar.getInstance().getTime();
        String resp = "Error:System Error";
        if (st.countTokens() >= 2){
            String command = st.nextToken();
            String pin = st.nextToken();
            String input = "";
            if (st.hasMoreTokens()) {
                input = st.nextToken();
            }
            logger.info("Command: "+command+" pin: "+pin+" input: "+input);
            
            UserSessionLocal uSession = getUserSession();
            User user = null;
            
            try {
                user = uSession.findByPin(new Integer(pin));
                if (user == null){
                    return "Error:Invalid Pin";
                } 
            }
            catch (Exception e){
                return "Error:Invalid Pin";
            }
            // setup the threadcontext for the audit
            ThreadContext.setContext(user.getId(), user.getUsername(), command);
            
            if (command.equals("GetOrderItems")){
                //synchronized (lockObject) {
                resp = getOrderItems(input);
                //}
            } else if (command.equals("GetRecItems")){
                resp = getRecItems(input);
            } else if (command.equals("DeleteFromOrder")){
                resp = deleteFromOrder(input);
            } else if (command.equals("DeleteFromRec")){
                resp = deleteFromRec(input);
            } else if (command.equals("AddToOrder")){
                //synchronized (lockObject) {
                resp = addToOrder(input, now);
                //}
            } else if (command.equals("CreateOrder")){
                resp = createOrder(input, now);
            } else if (command.equals("CreateReceiving")){
                resp = createReceiving(input, now);
            } else if (command.equals("GetCustomers")){
                //synchronized (lockObject) {
                resp = getCustomers();
                //}
            } else if (command.equals("GetVendors")){
                //synchronized (lockObject) {
                resp = getVendors();
                //}
            } else if (command.equals("GetPendingOrders")){
                //synchronized (lockObject) {
                resp = getPendingOrders();
                //}
            } else if (command.equals("UpdateRecItem")){
                resp = updateRecItem(input);
            } else if (command.equals("GetRecItem")){
                resp = getRecItem(input);
            } else if (command.equals("GetPendingRecs")){
                //synchronized (lockObject) {
                resp = getPendingRecs();
                //}
            } else if (command.equals("GetIsbnInfo")){
                resp = getIsbnInfo(input);
            } else if (command.equals("GetIsbnRecInfo")){
                resp = getIsbnRecInfo(input);
            } else if (command.equals("UpdateBin")){
                resp = updateBin(input);
            } else if (command.equals("AddToRec")){
                //synchronized (lockObject) {
                resp = addToRec(input, now);
                //}
            } else if (command.equals("GetManifests")){
                resp = getManifests();
            } else if (command.equals("GetManifestItems")){
                resp = getManifestItems(input);
            } else if (command.equals("AddManifestItem")){
                resp = addManifestItem(input);
            } else if (command.equals("DeleteManifestItem")){
                resp = deleteManifestItem(input);
            } else if (command.equals("GetManifestIsbnInfo")){
                resp = getManifestIsbnInfo(input);
            } else if (command.equals("GetRecItems")){
                resp = getRecItems(input);
            } else if (command.equals("GetBellIsbnInfo")){
                resp = getBellIsbnInfo(input);
            } else if (command.equals("Login")){
                resp = userPin(pin);
            } else {
                logger.error("DID NOT RECOGNIZE COMMAND: "+command);
            }
        } else {
            logger.error("BAD COMMAND: "+msg);
        }
        
        //logger.info(resp);
        return resp;
    }
    
    
    
    /* command processor implementations */
    
    private InventoryItem findInventoryItem(String isbn, String cond){
        InventoryItemSessionLocal iiSession = getInventoryItemSession();
        InventoryItem ii = null;
        try {
            ii = iiSession.findByIsbnCond(isbn, cond);
            if (ii == null){
                // try isbn 10
                ii = iiSession.findByIsbnCond(IsbnUtil.getIsbn10(isbn), cond);
                if (ii == null){
                    // try isbn 13
                    ii = iiSession.findByIsbnCond(IsbnUtil.getIsbn13(isbn), cond);
                }
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
        return ii;
    }
    
    private String getOrderItems(String input) throws Exception {
        Long oid = new Long(input);
        OrderSessionLocal oSession = getOrderSession();
        CustomerOrder co = oSession.findById(oid, "customerOrderItems");
        if (co == null){
            return "Error:The order has been deleted!";
        } 
        StringBuilder sb = new StringBuilder();
        ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(co.getCustomerOrderItems());
        Object[] itemArray = items.toArray();
        Arrays.sort(itemArray);

        boolean colon = false;
        for (int i = 0; i < itemArray.length; i++){
            CustomerOrderItem coi = (CustomerOrderItem)itemArray[i];
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(coi.getId());
            sb.append(":");
            sb.append(coi.getQuantity());
            sb.append(" - ");
            sb.append(coi.getBin());
            sb.append(" - ");
            sb.append(coi.getIsbn());
            sb.append(" - ");
            if (coi.getTitle() != null){
                sb.append(coi.getTitle().replace(":", "&colon;"));
            } else {
                sb.append("unknown");
            }
        }
        if (items == null || items.size() == 0){
            sb.append("Info:No items on this order.");
        } else {
            sb.append(":TotalItems:");
            sb.append(co.getTotalNonShippedQuantity());
            sb.append(":TotalPrice:");
            sb.append(PriceFormat.format(co.getTotalPriceNonShipped().doubleValue()));
        }
        sb.append(":InvoiceNumber:");
        sb.append(co.getInvoiceNumber());
        return sb.toString();
    }
    
    private String getRecItems(String input) throws Exception {
        Long id = new Long(input);
        ReceivingSessionLocal recSession = getReceivingSession();
        Received rec = recSession.findById(id, "receivedItems", "vendor");
        if (rec == null){
            return "Error:The receiving has been deleted!";
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<ReceivedItem> items = new ArrayList<ReceivedItem>(rec.getReceivedItems());
        Object[] itemArray = items.toArray();
        Arrays.sort(itemArray);

        boolean colon = false;
        for (int i = 0; i < itemArray.length; i++){
            ReceivedItem ri = (ReceivedItem)itemArray[i];
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(ri.getId());
            sb.append(":");
            sb.append(ri.getQuantity());
            sb.append(" - ");
            sb.append(ri.getBin());
            sb.append(" - ");
            sb.append(ri.getIsbn());
            sb.append(" - ");
            if (ri.getTitle() != null){
                sb.append(ri.getTitle().replace(":", "&colon;"));
            } else {
                sb.append("unknown");
            }
        }
        if (items == null || items.size() == 0){
            sb.append("Info:No items on this receiving.");
        }
        sb.append(":RecDate:");
        sb.append(DateFormat.format(rec.getCreateTime()));
        sb.append(":Vendor:");
        if (rec.getVendor() != null){
            sb.append(rec.getVendor().getVendorName());
        } else {
            sb.append(" ");
        }
        items = null;
        return sb.toString();
    }
    
    private String deleteFromOrder(String input) throws Exception {
        Long id = new Long(input);
        OrderSessionLocal oSession = getOrderSession();
        CustomerOrderItem coi = oSession.findItemById(id, "customerOrder", "inventoryItem");
        if (coi == null){
            return "Error: Could not delete item from order";
        }
        CustomerOrder co = coi.getCustomerOrder();
        InventoryItem ii = coi.getInventoryItem();
        
        oSession.deleteItem(id);
        oSession.recalculateOrderTotals(co.getId());
        co = oSession.findById(co.getId());
        
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        iSession.recalculateCommitted(ii.getId());
        
        StringBuilder sb = new StringBuilder();
        sb.append("Deleted Item from order");
        sb.append(":TotalItems:");
        sb.append(co.getTotalNonShippedQuantity());
        sb.append(":TotalPrice:");
        if (co.getTotalPriceNonShipped() == null){
            sb.append(PriceFormat.format(0D));
        } else {
            sb.append(PriceFormat.format(co.getTotalPriceNonShipped().doubleValue()));
        }
        return sb.toString();
    }
    
    private String deleteFromRec(String input) throws Exception {
        Long id = new Long(input);
        ReceivingSessionLocal rSession = getReceivingSession();
        ReceivedItem ri = rSession.findItemById(id, "received");
        if (ri == null){
            return "Error: Could not delete item from receiving";
        }
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        InventoryItem ii = iSession.findByIsbnCond(ri.getIsbn(), ri.getCond());
        if (ri != null && ii != null){
            Received rec = ri.getReceived();
            rSession.deleteItem(id);
            
            StringBuilder sb = new StringBuilder();
            rec = rSession.findById(rec.getId(), "receivedItems", "vendor");
            boolean colon = false;
            for (ReceivedItem recitem : rec.getReceivedItems()){
                if (colon){
                    sb.append(":");
                } else {
                    colon = true;
                }
                sb.append(recitem.getId());
                sb.append(":");
                sb.append(recitem.getQuantity());
                sb.append(" - ");
                sb.append(recitem.getBin());
                sb.append(" - ");
                sb.append(recitem.getIsbn());
                sb.append(" - ");
                if (recitem.getTitle() != null){
                    sb.append(recitem.getTitle().replace(":", "&colon;"));
                } else {
                    sb.append("unknown");
                }
            }
            if (rec.getReceivedItems() == null || rec.getReceivedItems().size() == 0){
                sb.append("Info:No items on this receiving.");
            }
            sb.append(":RecDate:");
            sb.append(DateFormat.format(rec.getCreateTime()));
            if (rec.getVendor() != null){
                sb.append(":Vendor:");
                sb.append(rec.getVendor().getVendorName());
            } else {
                sb.append(":Vendor:");
                sb.append(" ");
            }
            return sb.toString();
        }
        return "Error: Could not delete item from receiving";
    }
    
    private String addToOrder(String input, Date now) throws Exception {
        // input is the order id, isbn, and quantity to add
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        int count = inputSt.countTokens();
        if (count < 2){
            return "Error:Could not add to order";
        } 
        Long id = new Long(inputSt.nextToken());
        Integer qty = 0;
        Integer invqty = 0;
        Integer breakqty = 0;
        Integer bellqty = 0;
        Float price = 0F;
        try {
            try {
                invqty = new Integer(inputSt.nextToken());
            } catch (Exception ex){invqty = 0;}
            try {
                breakqty = new Integer(inputSt.nextToken());
            } catch (Exception ex){breakqty = 0;}
            try {
                bellqty = new Integer(inputSt.nextToken());
            } catch (Exception ex){bellqty = 0;}
        } catch (Exception e){
            // bad qty
            return "Error: Could not add, BAD QUANTITY!";
        }
        qty = invqty + breakqty + bellqty;
        try {
            price = new Float(inputSt.nextToken());
        } catch (Exception e){
            // bad price
            return "Error: Could not add, BAD PRICE!";
        }
        String cond = inputSt.nextToken().toLowerCase();
        String isbn = inputSt.nextToken();
        String title = null;
        if (inputSt.hasMoreTokens()){
            title = inputSt.nextToken();
        }
        while (inputSt.hasMoreTokens()){
            // for some reason there are semi colons in the isbn
            title += ";"+inputSt.nextToken();
        }
        OrderSessionLocal oSession = getOrderSession();
        CustomerOrder co = new CustomerOrder();
        if (id > -1){
            co = oSession.findById(id, "customer");
        }
        int customerDiscount = 0;
        if (co != null) customerDiscount = co.getDiscount();
        InventoryItem ii = findInventoryItem(isbn, cond);
        // if it is not found we need to look it up on amazon
        if (ii == null){
            //System.out.println("Adding new inventory item: "+isbn);
            // new inventory item
            ii = new InventoryItem();
            isbn = IsbnUtil.getIsbn10(isbn);
            ii.setIsbn(isbn);
            ii.setIsbn13(IsbnUtil.getIsbn13(isbn));
            newInventoryItem(null, ii, cond);
        }
        CustomerOrderItem coi = null;
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("customerOrder", co));
        qi.addAndCriterion(Restrictions.eq("inventoryItem", ii));
        List<CustomerOrderItem> matches = oSession.findAllItems(qi).getData();
        if (matches != null && matches.size() > 0){
            coi = matches.get(0);
            coi.setQuantity(qty);
            coi.setInvQuantity(invqty);
            coi.setBreakQuantity(breakqty);
            coi.setBellQuantity(bellqty);
            coi.setPrice(price);
            coi.setLastUpdate(now);
            if (title != null && title.trim().length() > 0){
                coi.setTitle(title);
            }
            oSession.update(coi);
            oSession.recalculateOrderItemTotals(coi.getId());
        }
        if (coi == null){
            coi = new CustomerOrderItem();
            coi.setIsbn(isbn);
            coi.setIsbn13(ii.getIsbn13());
            coi.setCond(cond);
            coi.setCredit(false);
            coi.setExtended(0F);
            coi.setTitle(ii.getTitle());
            coi.setQuantity(qty);
            coi.setInvQuantity(invqty);
            coi.setBreakQuantity(breakqty);
            coi.setBellQuantity(bellqty);
            coi.setBin(ii.getBin());
            coi.setCost(ii.getCost());
            coi.setFilled(0);
            coi.setDiscount(new Float(customerDiscount));
            if (co.getCustomer() != null && co.getCustomer().getDiscount() != null && co.getCustomer().getDiscount() > 0F){
                coi.setDiscount(new Float(co.getCustomer().getDiscount()));
            } else {
                coi.setDiscount(0F);
            }
            coi.setCustomerOrder(co);
            coi.setInventoryItem(ii);
            coi.setPrice(price);
            if (title != null && title.trim().length() > 0){
                coi.setTitle(title);
            }
            coi.setType("Pieces");
            oSession.create(coi);
            oSession.recalculateOrderItemTotals(coi.getId());
        }
        
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        iSession.recalculateCommitted(ii.getId());

        return "Added "+qty.toString()+" to order.";
    }
    
    public void newInventoryItem(Received received, InventoryItem inventory, String condition){
        inventory.setSkid(false);
        inventory.setOnhand(0);
        inventory.setBellbook(false);
        inventory.setCond(condition);
        AmazonLookup.getInstance().lookupData(inventory, true);
        if (received != null && received.getPublisher() != null && received.getPublisher().length() > 0){
            inventory.setCompanyRec(received.getPublisher());
            inventory.setPublisher(received.getPublisher());
        }
        List<String> cats = AmazonLookup.getInstance().lookupCategories(inventory.getIsbn());
        if (cats.size() > 0) inventory.setCategory1(cats.get(0));
        if (cats.size() > 1) inventory.setCategory2(cats.get(1));
        if (cats.size() > 2) inventory.setCategory3(cats.get(2));
        if (cats.size() > 3) inventory.setCategory4(cats.get(3));
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        iSession.create(inventory);

        // TODO
        // amazon category and subject load
        //AmazonLookup.getInstance().getCategories(dao, inventory);
        //AmazonLookup.getInstance().getSubjects(dao, inventory);
    }
    
    private String createOrder(String input, Date now) throws Exception {
        // the input is the customer oid
        Customer c = null;
        OrderSessionLocal oSession = getOrderSession();
        CustomerSessionLocal cSession = getCustomerSession();
        try {
            c = cSession.findById(new Long(input));
        } catch (Exception e){}
        if (c == null){
            // return error
            return "Error:No Customer.";
        }
        // create an order
        // get the next invoice number
        CustomerOrder co = new CustomerOrder();
        co.setSalesman(c.getSalesRep());
        co.setShipVia(co.getShipVia());
        co.setCustomer(c);
        // TODO we may need to do shipping over here
        //co.setCustomerShipping(blah);
        co.setCreditMemo(false);
        co.setPosted(false);
        if (c != null){
            co.setCustomerCode(c.getCode());
        }
        co.setCustomerVisit(false);
        co.setOrderDate(now);
        oSession.create(co);
        return "Created:"+co.getId().toString();
    }
    
    private String createReceiving(String input, Date now) throws Exception {
        // the input is the vendor oid
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        String vid = input;
        String ponum = "Scanner";
        if (inputSt.countTokens() == 2){
            vid = inputSt.nextToken();
            ponum = inputSt.nextToken();
        }
        VendorSessionLocal vSession = getVendorSession();
        Vendor v = null;
        try {
            v = vSession.findById(new Long(vid));
        } catch (Exception e){}
        if (v == null){
            // return error
            return "Error:No Vendor.";
        }
        // create a receiving
        Received rec = new Received();
        rec.setVendor(v);
        rec.setVendorCode(v.getCode());
        rec.setPoNumber(ponum);
        rec.setSkid(false);
        rec.setDate(now);
        ReceivingSessionLocal rSession = getReceivingSession();
        rSession.create(rec);
        return "Created:"+rec.getId().toString();
    }
    
    private String getCustomers() throws Exception {
        CustomerSessionLocal cSession = getCustomerSession();
        QueryInput qi = new QueryInput();
        qi.setSortCol("code");
        qi.setSortDir(QueryInput.SORT_DESC);
        List<Customer> customers = cSession.findAll(qi).getData();
        StringBuilder sb = new StringBuilder();
        boolean colon = false;
        for (Customer c : customers){
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(c.getId());
            sb.append(":");
            if (c.getCode() != null){
                sb.append(c.getCode().replace(":", "&colon;"));
            } else {
                sb.append(c.getCompanyName().replace(":", "&colon;"));
            }
        }
        if (customers == null || customers.size() == 0){
            sb.append("Error:Could not find any customers!");
        }
        return sb.toString();            
    }
    
    private String getVendors() throws Exception {
        VendorSessionLocal vSession = getVendorSession();
        QueryInput qi = new QueryInput();
        qi.setSortCol("code");
        qi.setSortDir(QueryInput.SORT_DESC);
        List<Vendor> vendors = vSession.findAll(qi).getData();
        StringBuilder sb = new StringBuilder();
        boolean colon = false;
        for (Vendor v : vendors){
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(v.getId());
            sb.append(":");
            if (v.getCode() != null){
                sb.append(v.getCode().replace(":", "&colon;"));
            } else {
                sb.append(v.getVendorName().replace(":", "&colon;"));
            }
        }
        if (vendors == null || vendors.size() == 0){
            sb.append("Error:Could not find any customers!");
        }
        return sb.toString();            
    }
    
    private String getPendingOrders() throws Exception {
        OrderSessionLocal oSession = getOrderSession();
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("posted", false));
        qi.setSortCol("createTime");
        qi.setSortDir(QueryInput.SORT_DESC);
        List<CustomerOrder> orders = oSession.findAll(qi).getData();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (CustomerOrder co : orders){
            if (count > 0){
                sb.append(":");
            }
            sb.append(co.getId());
            sb.append(":");
            sb.append(DateFormat.format(co.getCreateTime()));
            sb.append(" - ");
            sb.append(co.getInvoiceNumber());
            sb.append(" - ");
            sb.append(co.getCustomerCode());
            count++;
        }
        if (orders == null || orders.size() == 0){
            sb.append("Error:Could not find any pending orders!");
        }
        return sb.toString();
    }
    
    private String updateRecItem(String input) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        String recItemId = inputSt.nextToken();
        String qtyStr = inputSt.nextToken();
        String bin = "";
        if (inputSt.hasMoreTokens()){
            bin = inputSt.nextToken();
        }
        Integer qty = 0;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e){
            return "Error:Quantity must be a number!";
        }
        ReceivingSessionLocal rSession = getReceivingSession();
        ReceivedItem ri = rSession.findItemById(new Long(recItemId));
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        InventoryItem ii = iSession.findByIsbnCond(ri.getIsbn(), ri.getCond());
        if (ri == null || ii == null){
            return "Error:Could not find inventory item or rec item!";
        }
        ii.setOnhand(ii.getOnhand()-ri.getQuantity());
        ii.setOnhand(ii.getOnhand()+qty);
        if (ii.getBin() != bin){
            ii.setBin(bin);
        }
        ri.setQuantity(qty);
        ri.setBin(bin);
        
        rSession.update(ri);
        
        iSession.update(ii);
        iSession.recalculateCommitted(ii.getId());
        
        return "Updated received item";            
    }
    
    private String getRecItem(String input) throws Exception {
        ReceivingSessionLocal rSession = getReceivingSession();
        ReceivedItem ri = rSession.findItemById(new Long(input));
        if (ri == null){
            return "Error:Could not find received item!";
        }
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        InventoryItem ii = iSession.findByIsbnCond(ri.getIsbn(), ri.getCond());
        StringBuilder sb = new StringBuilder();
        addData(sb, "rid", ri.getId());
        addData(sb, "isbn", ri.getIsbn());
        addData(sb, "isbn13", ri.getIsbn13());
        if (ii != null){
            addData(sb, "onhand", ii.getOnhand(), false, "0");
        } else {
            addData(sb, "onhand", "0");
        }
        addData(sb, "qty", ri.getQuantity());
        addData(sb, "cover", ri.getCoverType());
        if (ii != null && ii.getBellbook()){
            addData(sb, "bell", "true");
        } else {
            addData(sb, "bell", "false");
        }
        addData(sb, "bin", ri.getBin());
        addData(sb, "cond", ri.getCond());
        addData(sb, "title", ri.getTitle(), true, "unknown");
        
        return sb.toString();            
    }
    
    private String getPendingRecs() throws Exception {
        ReceivingSessionLocal rSession = getReceivingSession();
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("posted", false));
        qi.setSortCol("createTime");
        qi.setSortDir(QueryInput.SORT_DESC);
        List<Received> pending = rSession.findAll(qi).getData();
        StringBuilder sb = new StringBuilder();
        boolean colon = false;
        for (Received rec : pending){
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(rec.getId());
            sb.append(":");
            sb.append(DateFormat.format(rec.getCreateTime()).replace(":", "&colon;"));
            if (rec.getPoNumber() != null){
                sb.append(" - ");
                sb.append(rec.getPoNumber().replace(":", "&colon;"));
            }
            if (rec.getComment() != null && rec.getComment().length() > 0){
                sb.append(" - ");
                sb.append(rec.getComment().replace(":", "&colon;"));
            }
        }
        if (pending == null || pending.size() == 0){
            return "Error:Could not find any pending!";
        }
        return sb.toString();            
    }
    
    private String getIsbnRecInfo(String input) throws Exception {
        return getIsbnInfo(input, true);
    }
    private String getIsbnInfo(String input) throws Exception {
        return getIsbnInfo(input, false);
    }
    private String getIsbnInfo(String input, boolean rec) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        String cond = inputSt.nextToken();
        String isbn = inputSt.nextToken();
        StringBuilder sb = new StringBuilder();
        Boolean checkRec = rec;
        Boolean checkOrder = false;
        Long id = -1L;
        if (inputSt.hasMoreTokens()){
            id = new Long(inputSt.nextToken());
            checkOrder = true;
        }
        InventoryItemSessionLocal iiSession = getInventoryItemSession();
        InventoryItem ii = findInventoryItem(isbn, cond);
        if (ii == null){
            ii = new InventoryItem();
            addData(sb, "Status", "NotFound");
            isbn = IsbnUtil.getIsbn10(isbn);
            ii.setIsbn(isbn);
            AmazonLookup.getInstance().lookupData(ii, true);
            addData(sb, "isbn", ii.getIsbn());
            if (ii.getIsbn().length() == 10){
                addData(sb, "10digit", ii.getIsbn());
            } else if (ii.getIsbn().length() == 13){
                addData(sb, "10digit", IsbnUtil.getIsbn10(ii.getIsbn()));
            }
            if (ii.getIsbn13() == null || ii.getIsbn13().length() != 13){
                addData(sb, "13digit", IsbnUtil.getIsbn13(ii.getIsbn()));
            } else {
                addData(sb, "13digit", ii.getIsbn13());
            }
            addData(sb, "author", ii.getAuthor());
            addData(sb, "publisher", ii.getPublisher());
            addData(sb, "salesRank", ii.getSalesRank());
            addData(sb, "Title", ii.getTitle(), true, "unknown");
            addData(sb, "listPrice", PriceFormat.format(ii.getListPrice()));
            addData(sb, "listPriceNoFormat", PriceFormat.noSignFormat(ii.getListPrice()));
        } else {
            AmazonLookup.getInstance().lookupData(ii, false);
            addData(sb, "Status", "Found");
            addData(sb, "Bin", ii.getBin());
            addData(sb, "Title", ii.getTitle(), true, "unknown");
            if (ii.getBellbook()){
                addData(sb, "bell", "true");
            } else {
                addData(sb, "bell", "false");
            }                     
            addData(sb, "OnHand", ii.getOnhand());
            addData(sb, "isbn", ii.getIsbn());
            addData(sb, "cover", ii.getCover());
            addData(sb, "listPrice", PriceFormat.format(ii.getListPrice()));
            addData(sb, "listPriceNoFormat", PriceFormat.noSignFormat(ii.getListPrice()));
            addData(sb, "sellPrice", PriceFormat.format(ii.getSellingPrice()));
            addData(sb, "Price", PriceFormat.noSignFormat(ii.getSellingPrice()));
            addData(sb, "available", ii.getAvailable());
            addData(sb, "committed", ii.getCommitted());
            try {
                addData(sb, "lastRec", DateFormat.format(ii.getReceivedDate()));
            } catch (Exception e){
                addData(sb, "lastRec", "");
            }
            addData(sb, "lastRecQty", ii.getReceivedQuantity());
            if (ii.getIsbn().length() == 10){
                addData(sb, "10digit", ii.getIsbn());
            } else if (ii.getIsbn().length() == 13){
                addData(sb, "10digit", IsbnUtil.getIsbn10(ii.getIsbn()));
            }
            if (ii.getIsbn13() == null || ii.getIsbn13().length() != 13){
                addData(sb, "13digit", IsbnUtil.getIsbn13(ii.getIsbn()));
            } else {
                addData(sb, "13digit", ii.getIsbn13());
            }
            addData(sb, "author", ii.getAuthor());
            addData(sb, "publisher", ii.getPublisher());
            addData(sb, "salesRank", ii.getSalesRank());
        }
        if (checkRec && id > -1){
            ReceivingSessionLocal rSession = getReceivingSession();
            Received r = rSession.findById(id);
            QueryInput qi = new QueryInput();
            qi.addAndCriterion(Restrictions.eq("received", r));
            qi.addAndCriterion(Restrictions.eq("isbn", ii.getIsbn()));
            qi.addAndCriterion(Restrictions.eq("cond", ii.getCond()));
            List<ReceivedItem> matches = rSession.findAllItems(qi).getData();
            if (matches != null && matches.size() > 0){
                ReceivedItem ri = matches.get(0);
                addData(sb, "quantity", ri.getQuantity());
            }
        } else if (checkOrder && id > -1){
            // see if this is part of this order
            OrderSessionLocal oSession = getOrderSession();
            CustomerOrder co = oSession.findById(id);
            QueryInput qi = new QueryInput();
            qi.addAndCriterion(Restrictions.eq("customerOrder", co));
            qi.addAndCriterion(Restrictions.eq("isbn", ii.getIsbn()));
            qi.addAndCriterion(Restrictions.eq("cond", ii.getCond()));
            List<CustomerOrderItem> matches = oSession.findAllItems(qi).getData();
            if (matches != null && matches.size() > 0){
                CustomerOrderItem coi = matches.get(0);
                addData(sb, "invQty", coi.getInvQuantity());
                addData(sb, "bellQty", coi.getBellQuantity());
                addData(sb, "breakQty", coi.getBreakQuantity());
            }
        }
        return sb.toString();
    }
    
    private String updateBin(String input) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        String cond = inputSt.nextToken();
        String isbn = inputSt.nextToken();
        String bin = inputSt.nextToken();
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        InventoryItem ii = findInventoryItem(isbn, cond);
        StringBuilder sb = new StringBuilder();
        if (ii != null && bin != null && bin.length() > 0){
            ii.setBin(bin);
            iSession.update(ii);
            sb.append("Success.  Bin updated to: "+bin);
        } else {
            sb.append("ERROR: Did not update the bin.");
        }
        return sb.toString();            
    }
    
    private String addToRec(String input, Date now) throws Exception {
        // first token is the receiving id, second is qty and the rest is the isbn to add
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        if (inputSt.countTokens() < 3){
            return "Error, Could not add to receiving";
        }
        Long id = new Long(inputSt.nextToken());
        Integer qty = 0;
        try {
            qty = new Integer(inputSt.nextToken());
        } catch (Exception e){
            // bad qty
            return "ERROR - Could not add, BAD QUANTITY!";
        }
        String bin = inputSt.nextToken();
        String cond = inputSt.nextToken().toLowerCase();
        String isbn = inputSt.nextToken();
        String listPrice = inputSt.nextToken();
        String cover = inputSt.nextToken();
        boolean override = false;
        String titleOverride = null;
        if (inputSt.hasMoreTokens()){
            titleOverride = inputSt.nextToken();
            while (inputSt.hasMoreTokens()){
                // for some reason there are semi colons in the title
                titleOverride += ";"+inputSt.nextToken();
            }
            if (titleOverride.length() > 0){
                override = true;
            }
        }
        // Look at the recId and see if it is > -1, if not need to create new rec
        ReceivingSessionLocal rSession = getReceivingSession();
        Received rec = new Received();
        rec.setPoDate(now);
        if (id > -1){
            rec = rSession.findById(id);
        } else {
            rSession.create(rec);
        }
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        InventoryItem ii = findInventoryItem(isbn, cond);
        // if it is not found we need to look it up on amazon
        boolean newInv = false;
        if (ii == null){
            // new inventory item
            ii = new InventoryItem();
            isbn = IsbnUtil.getIsbn10(isbn);
            ii.setIsbn(isbn);
            ii.setIsbn13(IsbnUtil.getIsbn13(isbn));
            ii.setBin(bin);
            ii.setCover(cover);
            newInventoryItem(rec, ii, cond);
            if (override){
                ii.setTitle(titleOverride);
            }
            newInv = true;
        }
        boolean update = false;
        ReceivedItem ri = new ReceivedItem();
        int prevqty = 0;
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("received", rec));
        qi.addAndCriterion(Restrictions.eq("inventoryItem", ii));
        List<ReceivedItem> matches = rSession.findAllItems(qi).getData();
        if (matches != null && matches.size() > 0){
            ri = matches.get(0);
            update = true;
            prevqty = ri.getQuantity();
        } else {
            ri.setCreateTime(Calendar.getInstance().getTime());
        }
        ri.setIsbn(isbn);
        ri.setIsbn13(ii.getIsbn13());
        ri.setCond(cond);
        if (cover != null){
            ri.setCoverType(cover);
        }
        if (override){
            ri.setTitle(titleOverride);
        } else {
            ri.setTitle(ii.getTitle());
        }
        ri.setDate(rec.getPoDate());
        ri.setQuantity(qty);
        ri.setOrderedQuantity(qty);
        ri.setAvailable(qty);
        ri.setSellPrice(ii.getSellingPrice());
        if (cover != null){
            ii.setCover(cover);
        }
        ri.setCoverType(ii.getCover());
        ri.setBookType(ii.getBiblio());
        if (!bin.equals(ii.getBin())){
            // set the inv item to this bin
            ii.setBin(bin);
        }
        // update inventory item
        if (override){
            ii.setTitle(titleOverride);
        }
        try {
            if (ii.getListPrice() == null || !ii.getListPrice().equals(new Float(listPrice))) {
                ii.setListPrice(new Float(listPrice));
            }
        } catch (Exception e){}

        ri.setListPrice(ii.getListPrice());
        ri.setBin(ii.getBin());
        ri.setCost(ii.getCost());
        ri.setInventoryItem(ii);
        ri.setType("Pieces");
        ri.setReceived(rec);
        ri.setPoNumber(rec.getPoNumber());
        if (update){
            rSession.update(ri);
        } else {
            rSession.create(ri);
        }

        // LIFO BEGIN

        // update the inventory onhand and available
        int givenToBackorder = 0;
        if (ii.getBackorder() != null && ii.getBackorder() > 0){
            // we have a backorder on this item
            givenToBackorder = ii.getBackorder() - ri.getQuantity()-prevqty;
            if (givenToBackorder < 0){
                givenToBackorder = ii.getBackorder();
            }
            ii.setBackorder(ii.getBackorder()-givenToBackorder);
            //ri.setBackordered(ri.getBackordered()-givenToBackorder);
        }
        if (ii.getOnhand() == null){
            ii.setOnhand(ri.getQuantity()-prevqty-givenToBackorder);
        } else {
            ii.setOnhand(ii.getOnhand()-prevqty+ri.getQuantity()-givenToBackorder);
        }
        if (ii.getOnhand() < 0){
            ii.setOnhand(0);
        }
        
        iSession.update(ii);
        iSession.recalculateCommitted(ii.getId());

        // END LIFO


        StringBuilder wd = new StringBuilder();
        if (newInv){
            wd.append("NewInventory:");
        }
        wd.append("Added ");
        wd.append(qty.toString());
        wd.append(" to receiving.");
        return wd.toString();
    }
    
    private String getManifests() throws Exception {
        ManifestSessionLocal mSession = getManifestSession();
        QueryInput qi = new QueryInput();
        qi.setSortCol("date");
        qi.setSortDir(QueryInput.SORT_DESC);
        List<Manifest> manifests = mSession.findAll(qi).getData();
        StringBuilder sb = new StringBuilder();
        boolean colon = false;
        for (Manifest m : manifests){
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(m.getId());
            sb.append(":");
            if (m.getDate() != null){
                sb.append(DateFormat.format(m.getDate()));
            } else {
                sb.append(" ");
            }
            sb.append(" - ");
            if (m.getName() != null){
                sb.append(m.getName().replace(":", "&colon;"));
            } else {
                sb.append(" ");
            }
        }
        if (manifests == null || manifests.size() == 0){
            return "Error:Could not find any manifests!";
        }
        return sb.toString();            
    }
    
    private String getManifestItems(String input) throws Exception {
        ManifestSessionLocal mSession = getManifestSession();
        Manifest m = mSession.findById(new Long(input), "manifestItems");
        if (m == null){
            return "Error:Could not find manifest!";
        }
        
        ArrayList<ManifestItem> items = new ArrayList<ManifestItem>(m.getManifestItems());
        Object[] itemArray = items.toArray();
        Arrays.sort(itemArray);
        
        StringBuilder sb = new StringBuilder();
        boolean colon = false;
        for (int i = 0; i < itemArray.length; i++){
            ManifestItem mi = (ManifestItem)itemArray[i];
            if (colon){
                sb.append(":");
            } else {
                colon = true;
            }
            sb.append(mi.getId());
            sb.append(":");
            sb.append(mi.getQuantity());
            sb.append(" - ");
            sb.append(mi.getIsbn());
            sb.append(" - ");
            if (mi.getTitle() != null){
                sb.append(mi.getTitle().replace(":", "&colon;"));
            } else {
                sb.append("unknown");
            }
        }
        if (m.getManifestItems() == null || m.getManifestItems().size() == 0){
            sb.append("Info:No items on this manifest.");
        }
        sb.append(":TotalItems:");
        if (m.getManifestItems() != null){
            sb.append(m.getManifestItems().size());
        } else {
            sb.append("0");
        }
        sb.append(":Date:");
        sb.append(DateFormat.format(m.getDate()));
        sb.append(":ManifestName:");
        if (m.getName() != null){
            sb.append(m.getName());
        } else {
            sb.append(" ");
        }
        return sb.toString();
    }
    
    private String addManifestItem(String input) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        Long id = new Long(inputSt.nextToken());
        String isbn = inputSt.nextToken();
        Integer qty = 0;
        try {
            qty = Integer.parseInt(inputSt.nextToken());
        } catch (NumberFormatException ne){
            return "Error:Bad Quantity";
        }
        String title = "";
        if (inputSt.hasMoreTokens()){
            title = inputSt.nextToken().trim();
        }
        if (isbn.length() == 13){
            isbn = IsbnUtil.getIsbn10(isbn);
        }
        if (isbn == null || isbn.length() == 0){
            return "Error:No ISBN to add";
        }
        if (title.length() == 0){
            InventoryItem ii = new InventoryItem();
            isbn = IsbnUtil.getIsbn10(isbn);
            ii.setIsbn(isbn);
            AmazonLookup.getInstance().lookupData(ii, true);
            title = ii.getTitle();
            if (title == null) title = "";
        }
        ManifestSessionLocal mSession = getManifestSession();
        Manifest m = mSession.findById(id);
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("manifest", m));
        qi.addAndCriterion(Restrictions.eq("isbn", isbn));
        List<ManifestItem> matches = mSession.findAllManifestItems(qi).getData();
        if (matches != null && matches.size() > 0){
            ManifestItem mi = matches.get(0);
            mi.setTitle(title);
            mi.setQuantity(qty);
            mSession.update(mi);
        } else {
            ManifestItem mi = new ManifestItem();
            mi.setManifest(m);
            mi.setIsbn(isbn);
            mi.setIsbn13(IsbnUtil.getIsbn13(isbn));
            mi.setQuantity(qty);
            mi.setTitle(title);
            mSession.create(mi);
        }
        mSession.updateCounts(m.getId());
        return "Success:Added Manifest Item";
    }
    
    private String deleteManifestItem(String input) throws Exception {
        ManifestSessionLocal mSession = getManifestSession();
        ManifestItem mi = mSession.findManifestItemById(new Long(input), "manifest");
        if (mi == null){
            return "Error:Could not find manifest item!";
        }
        Long mid = mi.getManifest().getId();
        mSession.deleteManifestItem(new Long(input));
        mSession.updateCounts(mid);
        return "TotalItems:"+mSession.getItemCount(mid);
    }
    
    private String getManifestIsbnInfo(String input) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        Long id = new Long(inputSt.nextToken());
        if (!inputSt.hasMoreTokens()) {
            return "Error: No ISBN provided.";
        }
        String isbn = inputSt.nextToken();
        if (isbn.length() == 13){
            isbn = IsbnUtil.getIsbn10(isbn);
        }
        ManifestSessionLocal mSession = getManifestSession();
        Manifest m = mSession.findById(id);
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("manifest", m));
        qi.addAndCriterion(Restrictions.eq("isbn", isbn));
        List<ManifestItem> matches = mSession.findAllManifestItems(qi).getData();
        ManifestItem foundMi = null;
        if (matches != null && matches.size() > 0){
            foundMi = matches.get(0);
        }
        StringBuilder sb = new StringBuilder();
        if (foundMi != null){
            addData(sb, "isbn", foundMi.getIsbn());
            addData(sb, "10digit", IsbnUtil.getIsbn10(isbn));
            addData(sb, "13digit", IsbnUtil.getIsbn13(isbn));
            addData(sb, "qty", foundMi.getQuantity());
            addData(sb, "Title", foundMi.getTitle(), true, "");
        } else {
            InventoryItem ii = new InventoryItem();
            isbn = IsbnUtil.getIsbn10(isbn);
            ii.setIsbn(isbn);
            AmazonLookup.getInstance().lookupData(ii, true);
            addData(sb, "isbn", isbn);
            addData(sb, "10digit", IsbnUtil.getIsbn10(isbn));
            addData(sb, "13digit", IsbnUtil.getIsbn13(isbn));
            addData(sb, "Title", ii.getTitle(), true, "");
        }
        return sb.toString();
    }
    
    private String getBellIsbnInfo(String input) throws Exception {
        StringTokenizer inputSt = new StringTokenizer(input, ";");
        String isbn = inputSt.nextToken();
        if (isbn.length() == 13 && isbn.startsWith("978")){
            isbn = IsbnUtil.getIsbn10(isbn);
        }
        InventoryItem ii = new InventoryItem();
        ii.setIsbn(isbn);
        DecimalFormat df = new DecimalFormat("###,###,###,###");
        AmazonLookup.getInstance().lookupData(ii, true);
        StringBuilder sb = new StringBuilder();
        addData(sb, "Status", "Success");
        addData(sb, "isbn", ii.getIsbn());
        if (ii.getIsbn().length() == 10){
            addData(sb, "10digit", ii.getIsbn());
        } else {
            addData(sb, "10digit", IsbnUtil.getIsbn10(ii.getIsbn()));
        }
        if (ii.getIsbn13() == null || ii.getIsbn13().length() != 13){
            addData(sb, "13digit", IsbnUtil.getIsbn13(ii.getIsbn()));
        } else {
            addData(sb, "13digit", ii.getIsbn13());
        }
        addData(sb, "Title", ii.getTitle(), true, "unknown");
        addData(sb, "author", ii.getAuthor());
        addData(sb, "publisher", ii.getPublisher());
        addData(sb, "totalNew", ii.getAmazonTotalNew(), false, "0");
        addData(sb, "totalUsed", ii.getAmazonTotalUsed(), false, "0");
        addData(sb, "totalCollectable", ii.getAmazonTotalCollectible(), false, "0");
        addData(sb, "lowestUsed", ii.getAmazonLowestUsedPrice(), false, " ");
        addData(sb, "lowestNew", ii.getAmazonLowestNewPrice(), false, " ");
        addData(sb, "lowestCollectable", ii.getAmazonLowestCollectiblePrice(), false, " ");
        if (ii.getSalesRank() != null){
            addData(sb, "salesRank", df.format(new Long(ii.getSalesRank())));
        } else {
            addData(sb, "salesRank", "unknown");
        }
        if (ii.getSellingPrice() != null){
            addData(sb, "ourPrice", PriceFormat.format(ii.getSellingPrice()));
        } else {
            addData(sb, "ourPrice", " ");
        }
        if (ii.getListPrice() != null){
            addData(sb, "listPrice", PriceFormat.format(ii.getListPrice()));
            addData(sb, "listPriceNoFormat", PriceFormat.noSignFormat(ii.getListPrice()));
        } else {
            addData(sb, "listPrice", " ");
            addData(sb, "listPriceNoFormat", " ");
        }

        BellInventorySessionLocal bSession = getBellInventorySession();
        BellInventory bi = bSession.findByIsbn(isbn);
        String listed = "NO";
        int listedCount = 0;
        if (bi != null && bi.getListed() > 0){
            listed = "YES";
            listedCount = bi.getListed();
        }
        addData(sb, "listed", listed);
        addData(sb, "listedCount", ""+listedCount);
        return sb.toString();            
    }
    
    private String userPin(String pin) throws Exception {
        UserSessionLocal uSession = getUserSession();
        User user = uSession.findByPin(new Integer(pin));
        if (user == null){
            return "Error:Invalid Pin";
        } 
        StringBuilder roles = new StringBuilder();
        boolean colon = false;
        for (UserRole ur : user.getRoles()){
            if (ur.getRole().equals("SystemAdmin")) {
                if (colon) roles.append(":");
                else colon = true;
                roles.append("Manager");
            }
            if (ur.getRole().equals("BcInvViewer")){
                if (colon) roles.append(":");
                else colon = true;
                roles.append("Info");
            }
            if (ur.getRole().equals("BcRecViewer")){
                if (colon) roles.append(":");
                else colon = true;
                roles.append("Receiving");
            }
            if (ur.getRole().equals("BcOrderViewer")){
                if (colon) roles.append(":");
                else colon = true;
                roles.append("Orders");
            }
            if (ur.getRole().equals("BcManifestViewer")){
                if (colon) roles.append(":");
                else colon = true;
                roles.append("Manifest");
            }
        }
        return "User:"+user.getUsername()+":Role:"+roles.toString();
    }
    
    private void addData(StringBuilder sb, String key, Object value){
        addData(sb, key, value, false, null);
    }
    private void addData(StringBuilder sb, String key, Object value, Boolean replace, String defaultValue){
        if (sb.length() > 0) sb.append(":");
        sb.append(key);
        sb.append(":");
        if (value == null || (value instanceof String && ((String)value).length() == 0) ) {
            if (defaultValue != null){
                sb.append(defaultValue);
            } else {
                sb.append(" ");
            }
        } else {
            if (replace){
                sb.append(((String)value).replace(":", "&colon;"));
            } else {
                sb.append(value);
            }
        }
    }
    
    /* session lookups */
    
    public InventoryItemSessionLocal getInventoryItemSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIStringNoLoader);
            }
            return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup InventoryItemSession", ne);
        }
        throw new RuntimeException("Could not lookup InventoryItemSession");
    }

    public OrderSessionLocal getOrderSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIStringNoLoader);
            }
            return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup OrderSession", ne);
        }
        throw new RuntimeException("Could not lookup OrderSession");
    }

    public ReceivingSessionLocal getReceivingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIStringNoLoader);
            }
            return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup ReceivingSession", ne);
        }
        throw new RuntimeException("Could not lookup ReceivingSession");
    }

    public VendorSessionLocal getVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIStringNoLoader);
            }
            return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup VendorSession", ne);
        }
        throw new RuntimeException("Could not lookup VendorSession");
    }

    public ManifestSessionLocal getManifestSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (ManifestSessionLocal)ctx.lookup(ManifestSession.LocalJNDIStringNoLoader);
            }
            return (ManifestSessionLocal)ctx.lookup(ManifestSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup ManifestSession", ne);
        }
        throw new RuntimeException("Could not lookup ManifestSession");
    }

    public UserSessionLocal getUserSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIStringNoLoader);
            }
            return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup UserSession", ne);
        }
        throw new RuntimeException("Could not lookup UserSession");
    }

    public CustomerSessionLocal getCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIStringNoLoader);
            }
            return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }

    public BellInventorySessionLocal getBellInventorySession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellInventorySessionLocal)ctx.lookup(BellInventorySession.LocalJNDIStringNoLoader);
            }
            return (BellInventorySessionLocal)ctx.lookup(BellInventorySession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellInventorySession", ne);
        }
        throw new RuntimeException("Could not lookup BellInventorySession");
    }

    
}
