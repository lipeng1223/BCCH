/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.servlet;

import com.bc.actions.AmazonLookup;
import com.bc.amazon.*;
import com.bc.dao.DaoResults;
import com.bc.ejb.ApiSession;
import com.bc.ejb.AuditSessionLocal;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.ejb.VendorSession;
import com.bc.ejb.VendorSessionLocal;
import com.bc.orm.Audit;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.CustomerShipping;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.User;
import com.bc.orm.Vendor;
import com.bc.struts.QueryInput;
import com.bc.util.DateFormat;
import com.bc.util.IsbnUtil;
import com.bc.util.ThreadContext;
import com.bc.util.Timing;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bc.util.cache.VendorCache;
import java.text.SimpleDateFormat;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Alex
 */
public class ApiServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    ApiSession apiSession;
    private static Logger logger = Logger.getLogger(ApiServlet.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            apiSession = new ApiSession();
            String api = request.getParameter("cmd");
            logger.info("api = " + api);
            if (api == null)
            {
                RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/jsp/api.jsp");
                view.forward(request, response);
            } else if (api.equalsIgnoreCase("login")){
                out.print(this.apiLogin(request));
            } else{ 
                if (checkLogin(request)){
                    if (api.equalsIgnoreCase("getItemFromIsbn")){
                        out.print(this.apiGetItemFromIsbn(request));
                    } else if (api.equalsIgnoreCase("updateInventoryItem")){
                        out.print(this.apiUpdateInventoryItem(request));
                    }
                    else if (api.equalsIgnoreCase("getCustomers")){
                        out.print(this.apiGetCustomers(request));
                    } else if (api.equalsIgnoreCase("createOrder")) {
                        out.print(this.apiCreateOrder(request));
                    } else if (api.equalsIgnoreCase("createReceiving")){
                        out.print(this.apiCreateReceiving(request));
                    } else if (api.equalsIgnoreCase("getVendors")){
                        out.print(this.apiGetVendors(request));
                    } else if (api.equalsIgnoreCase("getReceivings")){
                        out.print(this.apiGetReceivings(request));
                    } else if (api.equalsIgnoreCase("getReceivingItems")){
                        out.print(this.apiGetReceivingItems(request));
                    } else if (api.equalsIgnoreCase("updateReceiving")){
                        out.print(this.apiUpdateReceiving(request));
                    } else if (api.equalsIgnoreCase("updateReceivingItem")){
                        out.print(this.apiUpdateReceivingItem(request));
                    } else if (api.equalsIgnoreCase("getInventoryItemHistory")){
                        out.print(this.apiGetInventoryHistory(request));
                    }
                } else{
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("message", "Failed to Login");
                        obj.put("state", "fail");
                        out.print(obj.toString());
                    } catch (JSONException ex) {
                        logger.info(ex.getMessage());
                    }
                }
            }
        } 
        finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "BCCH API Servlet";
    }// </editor-fold>
    
    
    
    private String apiLogin(HttpServletRequest request){
        JSONObject obj = new JSONObject();
        try{
            String username = request.getParameter("name");
            String password = request.getParameter("password");
            if (username == null || password == null){
                obj.put("message", "Failed to Login - empty parameter");
                obj.put("state", "fail");
            }else{
                User user = apiSession.findByUsernamePassword(username, password);
                if (user != null){
                    obj.put("message", "Successfully logged in");
                    obj.put("state", "ok");
                } else{               
                    obj.put("message", "Failed to Login - wrong credentials");
                    obj.put("state", "fail");
                }
            }
        } catch(Exception e){
            
        }
        
        return obj.toString();
    }
    
    private String apiGetItemFromIsbn(HttpServletRequest request){
        JSONObject obj = new JSONObject();
        try{
            String isbn = request.getParameter("isbn");
            String cond = request.getParameter("cond");
            if (isbn == null){
                obj.put("message", "Missing Parameter");
                obj.put("state", "fail");
            } else {
                if (isbn.length() == 13){
                    isbn = IsbnUtil.getIsbn10(isbn);
                }
                AmazonData adata = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
//                List<InventoryItem> items = apiSession.findBunchIsbn(isbn);
                InventoryItem ii = apiSession.findByIsbnCond(isbn, cond);

                if (adata != null){
                    JSONObject aobj = new JSONObject();
                    aobj.put("author", adata.getAuthorString());
                    aobj.put("image", adata.getLargeImageUrl());
                    aobj.put("title", adata.getTitle());
                    aobj.put("width", adata.getWidth());
                    aobj.put("height", adata.getHeight());
                    aobj.put("length", adata.getLength());
                    aobj.put("listprice", adata.getListPrice());
                    aobj.put("weight", adata.getWeight());
                    aobj.put("pubdate", adata.getPublicationDate());
                    aobj.put("publisher", adata.getPublisher());
                    aobj.put("pages",adata.getNumberOfPages());
                    aobj.put("rank", adata.getSalesRank());
                    obj.put("amazon", aobj);
                }
                JSONArray arr = new JSONArray();
                JSONObject iobj = new JSONObject();
                
                if (ii != null){
                    iobj.put("id", ii.getId());
                    iobj.put("title", ii.getTitle() != null ? ii.getTitle() : "");
                    iobj.put("author", ii.getAuthor());
                    iobj.put("image", ii.getMediumImage());
                    iobj.put("publisher", ii.getPublisher());
                    iobj.put("pubdate", ii.getPublishDate());
                    iobj.put("pages", ii.getNumberOfPages());
                    iobj.put("bin", ii.getBin() == null ? "" : ii.getBin());
                    iobj.put("cond", ii.getCond() == null? "" : ii.getCond());
                    iobj.put("cover", ii.getCover()== null? "" : ii.getCover());

                    iobj.put("category", ii.getBccategory());

                    iobj.put("list_price", ii.getListPrice());                          // needed for receiving
                    iobj.put("selling_price", ii.getSellingPrice());                    // needed for receiving
                    iobj.put("sellpercentlist", ii.getSellPricePercentList());
                    iobj.put("cost", ii.getCost());                                     // needed for receiving
    //                    iobj.put("costpercentlist", ii.getCostPercentList());               

                    iobj.put("quantity", ii.getQuantity());                             // needed for receiving
                    iobj.put("available", ii.getAvailable());                           // needed for receiving
                    iobj.put("onhand", ii.getOnhand());                                 // needed for receiving
                    iobj.put("committed", ii.getCommitted());                           // needed for receiving

                    iobj.put("bell_book", ii.getBellbook());                            // needed for receiving
                    iobj.put("skid", ii.getSkid());                                     // needed for receiving
                    iobj.put("restricted", ii.getRestricted());                         // needed for receiving
                    iobj.put("higher_education", ii.getHe());                           // needed for receiving

                    iobj.put("width", ii.getWidth());
                    iobj.put("height", ii.getHeight());
                    iobj.put("length", ii.getLength());
                    iobj.put("weight", ii.getWeight());

                    iobj.put("amazon_link", ii.getAmazonLink());
                }
                

                arr.put(iobj);

                obj.put("inventory", arr);
                obj.put("message", "Successfully fetched items.");
                obj.put("state", "ok");
            }
        } catch(Exception e){
        }
        return obj.toString();
    }
    
    private String apiUpdateInventoryItem(HttpServletRequest request){
        JSONObject obj = new JSONObject();
        try{
            long id = Long.parseLong(request.getParameter("id"));
//            String isbn = request.getParameter("isbn");
//            String cond = request.getParameter("cond");
//            String title = request.getParameter("title");
//            String author = request.getParameter("author");
//            String publisher = request.getParameter("publisher");
//            String category = request.getParameter("category");
//            int pages = Integer.parseInt(request.getParameter("number_of_pages"));
            
//            float lprice = Float.parseFloat(request.getParameter("list_price"));
//            float sprice = Float.parseFloat(request.getParameter("selling_price"));
            
//            int onhand = Integer.parseInt(request.getParameter("on_hand"));
            String bin = request.getParameter("bin");
//            String cover = request.getParameter("cover");
//            
//            Boolean bb = request.getParameter("bell_book").equalsIgnoreCase("true");
//            Boolean skid = request.getParameter("skid").equalsIgnoreCase("true");
//            Boolean rest = request.getParameter("restricted").equalsIgnoreCase("true");
//            Boolean he = request.getParameter("higher_education").equalsIgnoreCase("true");
//                        
//            float length = Float.parseFloat(request.getParameter("length"));
//            float width = Float.parseFloat(request.getParameter("width"));
//            float weight = Float.parseFloat(request.getParameter("weight"));
//            float height = Float.parseFloat(request.getParameter("height"));
            
//            apiSession.updateInventory(id, isbn, cond, title, author, publisher, lprice, sprice, onhand, bin, cover, bb, skid, rest, he, category, pages, length, width, height, weight);
            apiSession.updateInventory(id, bin);
            obj.put("status", "ok");
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return obj.toString();
    }
    
    private String apiGetCustomers(HttpServletRequest request){
        List<Customer> customers = apiSession.getCustomers();
        JSONObject obj = new JSONObject();
        try{
            JSONArray arr = new JSONArray();
            JSONObject cobj = null;
            JSONArray sarr = new JSONArray();
            for (Customer customer: customers){
                cobj = new JSONObject();
                sarr = new JSONArray();
                cobj.put("id", customer.getId());
                cobj.put("name", customer.getCompanyName() + " - " + customer.getCode());
                for (CustomerShipping cs : customer.getCustomerShippings()){
                    JSONObject sobj = new JSONObject();
                    sobj.put("id", cs.getId());
                    sobj.put("name", cs.getShippingCompany() + ", " + cs.getAddress1() + ", " + cs.getAddress2());
                    sarr.put(sobj);
                }
                cobj.put("ship", sarr);
                arr.put(cobj);
            }
            
            obj.put("customers", arr);
            obj.put("state", "ok");
            obj.put("message", "Successfully fetched customers");
            
        } catch(Exception e){
            logger.info(e.getMessage());
        }
        return obj.toString();
    }
    
    private String apiCreateOrder(HttpServletRequest request){
        JSONObject obj = new JSONObject();
        try{
            String data = request.getParameter("order");
            logger.info(data);
            JSONObject jsonInput = new JSONObject(data);
            JSONObject jsonOrder = jsonInput.getJSONObject("order");
            JSONArray jsonItems = jsonInput.getJSONArray("items");
            
            CustomerOrder order = generateOrderFromJson(jsonOrder);
            order.setCreateTime(new Date());
            apiSession.createOrder(order);
            logger.info("Order " + order.getId() + " has created.");
            for (int i = 0; i < jsonItems.length(); i++){
                JSONObject itemObject = jsonItems.getJSONObject(i);
                CustomerOrderItem coi = generateOrderItemFromJson(order, itemObject);
                if (coi != null)
                    apiSession.createOrderItem(coi);
                apiSession.recalculateOrderItemTotals(order.getId());
            }
            
            obj.put("state", "ok");
            obj.put("message", "Successfully created an order");
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiGetVendors(HttpServletRequest request){
//        List<Vendor> vendors = apiSession.findAllVendors();
        List<Vendor> vendors = VendorCache.getVendors();
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        try{
            for(Vendor v : vendors){
                JSONObject vobj = new JSONObject();
                vobj.put("id", v.getId());
                vobj.put("name", v.getCode() + " - " + v.getVendorName());
                arr.put(vobj);
            }
            obj.put("state", "ok");
            obj.put("vendors", arr);
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiCreateReceiving(HttpServletRequest request){
        JSONObject obj = new JSONObject();
        try{
            String data = request.getParameter("receiving");
            logger.info(data);
            JSONObject jsonInput = new JSONObject(data);
            JSONObject jsonRec = jsonInput.getJSONObject("receiving");
            JSONArray jsonItems = jsonInput.getJSONArray("items");
            
            Received r = generateReceivingFromJson(jsonRec);
            if (r.getId() == 0){
                r.setCreateTime(new Date());
                apiSession.createReceiving(r);
                logger.info("Receiving " + r.getId() + " has created.");
            } else {
                r = apiSession.getReceivingSession().findById(r.getId());
                logger.info("Receiving " + r.getId() + " has been fetched for updating.");
            }
            
            List<ReceivedItem> items = new ArrayList<ReceivedItem>();
            List<Long> ids = new ArrayList<Long>();
            
            for (int i = 0; i < jsonItems.length(); i++){
                JSONObject itemObject = jsonItems.getJSONObject(i);
                ReceivedItem ri = generateReceivingItemFromJson(r, itemObject);
                if (ri != null)
                    apiSession.createReceivingItem(ri);
                items.add(ri);
                ids.add(ri.getInventoryItem().getId());
            }
            
            if (items.size() > 0){
                try {
                    logger.info("update existing items");
                    List<ReceivedItem> newItems = apiSession.getReceivingSession().updateWithLifo(items, r.getId());
                    logger.info("creating any new items");
                    
                    if (newItems.size() > 0){
                        Timing t = new Timing("Add Received Items");
                        t.start();
                        if (apiSession.addReceivedItems(newItems)){
                            t.stop();

                            Timing lt = new Timing("Lifo Creates");
                            lt.start();
                            
                            apiSession.createReceivedItems(newItems, ids);
                            lt.stop();
                            
                            logger.info("recalculating the received");
                            apiSession.getReceivingSession().recalculateReceived(r.getId());
                            
                            obj.put("state", "ok");
                            obj.put("message", "Successfully created a receiving");
                        } else {
                            obj.put("state", "fail");
                            obj.put("message", "Failed to create a receiving");
                        }
                    } else{
                        obj.put("state", "ok");
                        obj.put("message", "Successfully created a receiving");
                    }
                } catch (Throwable t){
                    logger.error("Error processing lifo or adding new rec items", t);
                }
            }
            
            
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiGetReceivings(HttpServletRequest request){
        logger.info("apiGetReceivings");
        String page = request.getParameter("page");
        List<Received> receivings = apiSession.getReceivings(Integer.parseInt(page));
        logger.info("Got receivings " + receivings.size());
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        try{
            for(Received r : receivings){
                JSONObject robj = new JSONObject();
                robj.put("id", r.getId());
                robj.put("po_number", r.getPoNumber());
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                robj.put("po_date", r.getPoDate() != null ? df.format(r.getPoDate()) : "");
                
                if (r.getVendor() != null)
                    robj.put("vendor_id", r.getVendor().getId());
                else
                    robj.put("vendor_id", "0");
                robj.put("vendor_code", r.getVendorCode() != null ? r.getVendorCode() : "");
                robj.put("publisher", r.getPublisher() != null ? r.getPublisher() : "");
                robj.put("comment", r.getComment() != null ? r.getComment() : "");
                robj.put("on_hold", r.getHolding() != null ? r.getHolding() : "false");
                arr.put(robj);
            }
            obj.put("state", "ok");
            obj.put("receivings", arr);
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiUpdateReceiving(HttpServletRequest request){
        logger.info("apiUpdateReceivings");
        JSONObject obj = new JSONObject();
        try{
            long id = Long.parseLong(request.getParameter("rid"));
            String pono = request.getParameter("po_number");
            String podatestr = request.getParameter("po_date");
            Date podate = null;
            if (podatestr != null && !podatestr.equals(""))
                podate = DateFormat.parse(request.getParameter("po_date"));
            long vid = Long.parseLong(request.getParameter("vendor"));
            String publisher = request.getParameter("publisher");
            String comment = request.getParameter("comment");
            apiSession.updateReceiving(id, pono, podate, vid, publisher, comment);
            obj.put("state", "ok");
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiGetReceivingItems(HttpServletRequest request){
        logger.info("apiGetReceivingItems");
        String rid = request.getParameter("rid");
        logger.info("ID : " + rid);
        String page = request.getParameter("page");
        List<ReceivedItem> receivingItems = apiSession.getReceivingItems(Long.parseLong(rid), Integer.parseInt(page));
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        logger.info("receving count : " + receivingItems.size());
        try{
            for(ReceivedItem ri : receivingItems){
                JSONObject robj = new JSONObject();
                robj.put("id", ri.getId());
                robj.put("received_quantity", ri.getQuantity() == null ? "" : ri.getQuantity());
                robj.put("ordered_quantity", ri.getOrderedQuantity() == null ? "" : ri.getOrderedQuantity());
                robj.put("percentage_list", ri.getPercentageList() == null ? "" : ri.getPercentageList());
                robj.put("cost_per_lb", ri.getCostPerLb() == null ? "" : ri.getCostPerLb());
                robj.put("cost", ri.getCost() == null ? "" : ri.getCost());
                robj.put("title", ri.getTitle() != null ? ri.getTitle() : "");
                robj.put("isbn", ri.getIsbn() != null ? ri.getIsbn() : "");
                robj.put("cond", ri.getCond() != null ? ri.getCond() : "");
                robj.put("bin", ri.getBin() != null ? ri.getBin() : "");
                robj.put("list_price", ri.getListPrice() != null ? ri.getListPrice() : "");
                robj.put("selling_price", ri.getSellPrice() != null ? ri.getSellPrice() : "");
                robj.put("cover", ri.getCoverType() != null ? ri.getCoverType() : "");
                robj.put("bell_book", ri.getBellbook() == null ? "" : ri.getBellbook());
                robj.put("break_room", ri.getBreakroom() == null ? "" : ri.getBreakroom());
                robj.put("higher_education", ri.getHigherEducation() != null ? ri.getHigherEducation() : "");
                robj.put("restricted", ri.getRestricted() != null ? ri.getRestricted() : "");
                arr.put(robj);
            }
            obj.put("state", "ok");
            obj.put("receivings", arr);
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj.toString();
    }
    
    private String apiUpdateReceivingItem(HttpServletRequest request){
        logger.info("apiUpdateReceivingItems");
        JSONObject obj = new JSONObject();
        try{
            long id = Long.parseLong(request.getParameter("rid"));
            int quantity = Integer.parseInt(request.getParameter("received_quantity"));
            int orderedqty = Integer.parseInt(request.getParameter("ordered_quantity"));
            Float percent = Float.parseFloat(request.getParameter("percentage_list"));
            Float costperlb = Float.parseFloat(request.getParameter("cost_per_lb"));
            Float cost = Float.parseFloat(request.getParameter("cost"));
            String title = request.getParameter("title");
            String bin = request.getParameter("bin");
            Float listprice = Float.parseFloat(request.getParameter("list_price"));
            Float sellingprice = Float.parseFloat(request.getParameter("selling_price"));
            String cover = request.getParameter("cover");
            Boolean bb = request.getParameter("bell_book").equalsIgnoreCase("true");
            Boolean br = request.getParameter("break_room").equalsIgnoreCase("true");
            Boolean he = request.getParameter("higher_education").equalsIgnoreCase("true");
            Boolean rest = request.getParameter("restricted").equalsIgnoreCase("true");
            apiSession.updateReceivedItem(id, quantity, orderedqty, percent, costperlb, cost, title, bin, listprice, sellingprice, cover, bb, br, he, rest);
            obj.put("state", "ok");
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return obj.toString();
    }
    
    private String apiGetInventoryHistory(HttpServletRequest request){
        logger.info("apiGetInventoryHistory");
        String tableName = "inventory_item";
        String iid = request.getParameter("id");
        Long tableId = Long.parseLong(iid);
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        
        try {
            if (tableName != null){
                AuditSessionLocal auditSession = apiSession.getAuditSession();

                QueryInput queryInput;
                                
                queryInput = new QueryInput();
                queryInput.addAndCriterion(Restrictions.eq("tableName", tableName));
                queryInput.addAndCriterion(Restrictions.eq("tableId", tableId));

                queryInput.setLimit(200);
                queryInput.setSortCol("auditTime");
                queryInput.setSortDir(QueryInput.SORT_DESC);
                DaoResults daoResults = auditSession.findAll(queryInput);
                
                List<Audit>audits = daoResults.getData();
                for (Audit audit : audits){
                    JSONObject aobj = new JSONObject();
                    aobj.put("time", audit.getAuditTime());
                    aobj.put("user", audit.getUsername());
                    
                    JSONArray carr = new JSONArray();
                    
                    for (int i = 1; i <= 20; i++){
                        JSONObject cobj = new JSONObject();
                        String colName = audit.getColumnName(i);
                        if (colName == null){
                            continue;
                        }
                        String preValue = audit.getPreviousValue(i);
                        String curValue = audit.getCurrentValue(i);
                        
                        cobj.put("property", colName);
                        cobj.put("old", preValue);
                        cobj.put("new", curValue);
                        
                        carr.put(cobj);
                    }
                    
                    if (carr.length() == 0){
                        continue;
                    }
                    aobj.put("changes", carr);
                    arr.put(aobj);
                }
            }
            obj.put("state", "ok");
            obj.put("result", arr);
            
        } catch (Exception e){
            logger.error("Could not get history for table name: "+tableName+" id: "+tableId, e);
        }
        return obj.toString();
    }
    
    
    
    
    
    
    
    private CustomerOrder generateOrderFromJson(JSONObject obj) throws JSONException, ParseException{
        CustomerOrder order = new CustomerOrder();
        String t = obj.getString("credit_memo");
        if (t.equals("true"))
            order.setCreditMemo(true);
        else order.setCreditMemo(false);
        t = obj.getString("customer");
        long customerId = Long.parseLong(t);
        Customer customer = apiSession.findCustomerById(customerId);
        if (customer == null){
            return null;
        }
        order.setCustomer(customer);
        t = obj.getString("customer_shipping");
        if (!t.equals("")){
            long csid = Long.parseLong(t);
            CustomerShipping cs = apiSession.findCustomerShippingById(csid);
            order.setCustomerShipping(cs);
        }
        t = obj.getString("customer_po_number");
        order.setPoNumber(t);
        t = obj.getString("sales_rep");
        order.setSalesman(t);
        t = obj.getString("picklist_comment");
        order.setComment2(t);
        t = obj.getString("comment");
        order.setComment(t);
        t = obj.getString("ship_via");
        order.setShipVia(t);
        t = obj.getString("shipping_charges");
        order.setShippingCharges(Float.parseFloat(t));
        t = obj.getString("pallete_charge");
        order.setPalleteCharge(Float.parseFloat(t));
        t = obj.getString("deposit_amount");
        order.setDepositAmmount(Float.parseFloat(t));
        t = obj.getString("order_date");
        if (t.length() > 0)
            order.setOrderDate(DateFormat.parse(t));
        t = obj.getString("customer_visit");
        if (t.equals("true"))
            order.setCustomerVisit(Boolean.TRUE);
        else
            order.setCustomerVisit(Boolean.FALSE);
        t = obj.getString("picker_1");
        order.setPicker1(t);
        t = obj.getString("picker_2");
        order.setPicker2(t);
        t = obj.getString("quality_control");
        order.setQualityControl(t);
        t = obj.getString("status");
        order.setStatus(t);
        return order;
    }
    
    private CustomerOrderItem generateOrderItemFromJson(CustomerOrder order, JSONObject obj) throws JSONException{
        CustomerOrderItem coi = new CustomerOrderItem();
        OrderSessionLocal oSession = apiSession.getOrderSession();
        String isbn = obj.has("isbn") ? obj.getString("isbn") : "";
        String cond = obj.has("condition") ? obj.getString("condition") : "";
        String q = obj.has("quantity") ? obj.getString("quantity") : "0";
        String p = obj.has("selling_price") ? obj.getString("selling_price") : "0";
        
        
        InventoryItem ii = apiSession.findByIsbnCond(isbn.trim(), cond);
        if (ii == null){
            logger.info("There was no Item in Inventory that matched the ISBN: " + isbn + " and Condition: " + cond);
            return null;
        }
        CustomerOrderItem exists = apiSession.findOrderItemByIsbnCond(order, isbn, cond);
        if (exists != null){
            exists.setQuantity(exists.getQuantity()+Integer.parseInt(q));
            exists.setPrice(Float.parseFloat(p));
            oSession.update(exists);
            apiSession.recalculateCommitted(ii.getId());
            return null;
        } else{
            coi.setExtended(0F);
            coi.setTotalPrice(BigDecimal.ZERO);
            coi.setCustomerOrder(order);
            coi.setInventoryItem(ii);
            coi.setIsbn(ii.getIsbn());
            coi.setIsbn13(ii.getIsbn13());
            coi.setBin(ii.getBin());
            coi.setFilled(0);
            coi.setCredit(order.getCreditMemo());
            if (order.getCreditMemo()) {
                coi.setCredit(true);
                // setting filled and allowed to item quantity
                coi.setFilled(coi.getQuantity());
            }

            if (order.getCreditMemo() && (coi.getCreditDamage() || coi.getCreditShortage())){
                // negative price
                if (coi.getPrice() > 0F)
                    coi.setPrice(-coi.getPrice());
            }
        }
        return coi;
    }
    
    private Received generateReceivingFromJson(JSONObject obj) throws JSONException, ParseException{
        Received r = new Received();
        if (obj.has("id")){
            String tid = obj.getString("id");
            r.setId(Long.parseLong(tid));
        }
        String t = obj.getString("holding");
        if (t.equalsIgnoreCase("true")){
            r.setHolding(Boolean.TRUE);
        } else{
            r.setHolding(Boolean.FALSE);
        }
        t = obj.getString("po_number");
        r.setPoNumber(t);
        t = obj.getString("po_date");
        if (t.length() > 0)
            r.setPoDate(DateFormat.parse(t));
        t = obj.getString("vendor");
        long vid = Long.parseLong(t);
        r.setVendor(apiSession.findVendorById(vid));
        t = obj.getString("publisher");
        r.setPublisher(t);
        t = obj.getString("comment");
        r.setComment(t);
        return r;
    }
    
    private ReceivedItem generateReceivingItemFromJson(Received receiving, JSONObject obj) throws JSONException{
        InventoryItemSessionLocal iSession = apiSession.getInventoryItemSession();
        ReceivedItem ri = new ReceivedItem();
        ri.setReceived(receiving);
        String t = obj.getString("isbn");
        ri.setIsbn(IsbnUtil.getIsbn10(t));
        if (IsbnUtil.isValid13(IsbnUtil.getIsbn13(t)))
            ri.setIsbn13(IsbnUtil.getIsbn13(t));
        if (receiving.getHolding()){
            ri.setIsbn(ri.getIsbn().toUpperCase());
        }
        t = obj.getString("condition");
        ri.setCond(t);
        t = obj.getString("quantity");
        ri.setQuantity(Integer.parseInt(t));
        t = obj.getString("bin");
        ri.setBin(t);
        t = obj.getString("title");
        ri.setTitle(t);
        t = obj.getString("cost");
        ri.setCost(Float.parseFloat(t));
        t = obj.getString("selling_price");
        ri.setSellPrice(Float.parseFloat(t));
        String cover = obj.getString("cover");
        ri.setCoverType(cover);
        
        InventoryItem ii = apiSession.findByIsbnCond(ri.getIsbn(), ri.getCond());
        
        if (ii == null){
            logger.info("Creating new inventory item for the isbn: " + ri.getIsbn()+" cond: " + ri.getCond());
            ii = new InventoryItem();
            ii.setCond(ri.getCond());
            ii.setIsbn(ri.getIsbn());
            if (IsbnUtil.isValid10(ii.getIsbn())){
                ii.setIsbn10(ii.getIsbn());
            }
            ii.setCover(cover);
            ii.setIsbn13(ri.getIsbn13());
            ii.setBin(ri.getBin());
            ii.setSellingPrice(ri.getSellPrice());
            ii.setReceivedPrice(ri.getCost());
            t = obj.getString("onhand");
            ii.setOnhand(Integer.parseInt(t));
            t = obj.getString("available");
            ii.setAvailable(Integer.parseInt(t));
            t = obj.getString("committed");
            ii.setCommitted(Integer.parseInt(t));
            try {
                AmazonLookup.getInstance().lookupData(ii, true);
                List<String> cats = AmazonLookup.getInstance().lookupCategories(ii.getIsbn());
                if (cats.size() > 0) ii.setCategory1(cats.get(0));
                if (cats.size() > 1) ii.setCategory2(cats.get(1));
                if (cats.size() > 2) ii.setCategory3(cats.get(2));
                if (cats.size() > 3) ii.setCategory4(cats.get(3));
            } finally {
                // waiting 1 second - throttling
                try {
                    Thread.sleep(1000);
                } catch (Exception e){}
            }
            if (ii.getTitle() == null || ii.getTitle().length() == 0){
                ii.setTitle(ri.getTitle());
            }
            iSession.create(ii);
        }
        if (ri.getTitle() != null && ri.getTitle().length() > 0 && ii.getTitle() == null || ii.getTitle().length() == 0){
                ii.setTitle(ri.getTitle());
                iSession.update(ii);
        }
        // making sure we get the bin of the item
        if (ri.getBin() == null || ri.getBin().length() == 0)
            ri.setBin(ii.getBin());

        ri.setListPrice(ii.getListPrice());
        if (!ri.isUpdated("cost"))
                ri.setCost(ii.getCost());
        if (!ri.isUpdated("sellPrice"))
            ri.setSellPrice(ii.getSellingPrice());
        ri.setCoverType(ii.getCover());
        ri.setType("Pieces");
        t = obj.getString("onhand");
        ii.setOnhand(Integer.parseInt(t) + ii.getOnhand());
        t = obj.getString("available");
        ii.setAvailable(Integer.parseInt(t) + ii.getAvailable());
        t = obj.getString("committed");
        ii.setCommitted(Integer.parseInt(t) + ii.getCommitted());

        ri.setReceived(receiving);
        ri.setPoNumber(receiving.getPoNumber());

        if (ri.getTitle() == null || ri.getTitle().length() == 0)
            ri.setTitle(ii.getTitle());
        ri.setInventoryItem(ii);

        return ri;
    }
    
    
    
    private Boolean checkLogin(HttpServletRequest request){
        String username = request.getParameter("name");
        String password = request.getParameter("password");
        if (username == null || password == null)
            return false;
        User user = apiSession.findByUsernamePassword(username, password);
        if (user == null)
            return false;
        ThreadContext.setContext(user.getId(), username, "api");
        return true;
    }
    

}
