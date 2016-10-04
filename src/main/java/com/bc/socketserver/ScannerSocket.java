package com.bc.socketserver;


public class ScannerSocket extends Thread {
/*
    private ServerSocket serverSocket;
    private BaseDAO dao = null;
    private static final DecimalFormat nosignFormat = new DecimalFormat("0.00");
    private Boolean lockObject = false;

    public ScannerSocket() throws Exception {
        dao = new BaseDAO();
        serverSocket = new ServerSocket(8888);
        System.out.println("Listening on 8888....");
    }

    public void run(){
        while (true){
            try {
                new LockWatcher(new ScannerSocketReader(serverSocket.accept(), System.currentTimeMillis()));
                System.out.println("Accepted socket connect, starting comm...");
            } catch (Throwable t){
                t.printStackTrace();
            }
        }
    }
    
    private class LockWatcher extends Thread {
        private ScannerSocketReader ssr;
        public LockWatcher(ScannerSocketReader ssr){
            this.ssr = ssr;
            this.start();
            ssr.start();
        }
        public void run(){
            boolean done = false;
            long totalTime = 0;
            while (!done){
                try {
                    totalTime += 100;
                    sleep(100);
                } catch (Exception e){}
                if (!ssr.isAlive() || ssr.isFinished()){
                    done = true;
                    System.out.println(ssr.getSid()+" "+Calendar.getInstance().getTime().toString()+" finished lock watch.");
                }
                if (totalTime > 25000){
                    // we have a stuck thread
                    System.out.println(ssr.getSid()+" "+Calendar.getInstance().getTime().toString()+" We have a stuck thread...");
                    System.out.println(ssr.getSid()+" "+Calendar.getInstance().getTime().toString()+" calling close on the SessionUtil...");
                    SessionUtil.close();
                    System.out.println(ssr.getSid()+" "+Calendar.getInstance().getTime().toString()+" calling interrupt on the socket reader...");
                    ssr.interrupt();
                    done = true;
                    System.out.println(ssr.getSid()+" "+Calendar.getInstance().getTime().toString()+" finished lock watch.");
                }
            }
        }
    }

    private class ScannerSocketReader extends Thread {
        private Socket socket;
        private long sid;
        private boolean finished = false;
        
        public ScannerSocketReader(Socket socket, long sid){
            this.socket = socket;
            this.sid = sid;
        }
        
        public long getSid(){
            return sid;
        }
        
        public boolean isFinished(){
            return finished;
        }

        private InventoryItem findInventoryItem(String isbn, String cond){
            InventoryItem ii = null;
            try {
                ii = dao.findInventoryItemByIsbnCond(isbn, cond);
                if (ii == null){
                    // try isbn 10
                    ii = dao.findInventoryItemByIsbnCond(CheckDigit.getIsbn10(isbn), cond);
                    if (ii == null){
                        // try isbn 13
                        ii = dao.findInventoryItemByIsbnCond(CheckDigit.getIsbn13(isbn), cond);
                    }
                }
            } catch (Throwable t){
                t.printStackTrace();
            }
            return ii;
        }

        public void run(){
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                //System.out.println(line);
                Date now = Calendar.getInstance().getTime();
                System.out.println(sid+" "+now.toString()+" start "+line);
                if (line != null){
                    StringTokenizer st = new StringTokenizer(line, ":");
                    if (st.countTokens() == 2){
                        String command = st.nextToken();
                        String input = st.nextToken();
                        if (command.equals("GetOrderItems")){
                            synchronized (lockObject) {
                                getOrderItems(input);
                            }
                        } else if (command.equals("GetRecItems")){
                            getRecItems(input);
                        } else if (command.equals("DeleteFromOrder")){
                            deleteFromOrder(input);
                        } else if (command.equals("DeleteFromRec")){
                            deleteFromRec(input);
                        } else if (command.equals("AddToOrder")){
                            synchronized (lockObject) {
                                addToOrder(input, now);
                            }
                        } else if (command.equals("CreateOrder")){
                            createOrder(input, now);
                        } else if (command.equals("CreateReceiving")){
                            createReceiving(input, now);
                        } else if (command.equals("GetCustomers")){
                            synchronized (lockObject) {
                                getCustomers();
                            }
                        } else if (command.equals("GetVendors")){
                            synchronized (lockObject) {
                                getVendors();
                            }
                        } else if (command.equals("GetPendingOrders")){
                            synchronized (lockObject) {
                                getPendingOrders();
                            }
                        } else if (command.equals("UpdateRecItem")){
                            updateRecItem(input);
                        } else if (command.equals("GetRecItem")){
                            getRecItem(input);
                        } else if (command.equals("GetPendingRecs")){
                            synchronized (lockObject) {
                                getPendingRecs();
                            }
                        } else if (command.equals("GetIsbnInfo")){
                            getIsbnInfo(input);
                        } else if (command.equals("GetIsbnRecInfo")){
                            getIsbnRecInfo(input);
                        } else if (command.equals("UpdateBin")){
                            updateBin(input);
                        } else if (command.equals("AddToRec")){
                            synchronized (lockObject) {
                                addToRec(input, now);
                            }
                        } else if (command.equals("GetManifests")){
                            getManifests();
                        } else if (command.equals("GetManifestItems")){
                            getManifestItems(input);
                        } else if (command.equals("AddManifestItem")){
                            addManifestItem(input);
                        } else if (command.equals("DeleteManifestItem")){
                            deleteManifestItem(input);
                        } else if (command.equals("GetManifestIsbnInfo")){
                            getManifestIsbnInfo(input);
                        } else if (command.equals("GetRecItems")){
                            getRecItems(input);
                        } else if (command.equals("GetBellIsbnInfo")){
                            getBellIsbnInfo(input);
                        //} else if (command.equals("UserPin")){
                        //    userPin(input);
                        } else {
                            System.out.println("DID NOT RECOGNIZE COMMAND: "+command);
                        }
                    } else {
                        System.out.println("BAD COMMAND: "+line);
                    }
                }
                reader.close();
                reader = null;
                
                System.out.println(sid+" "+Calendar.getInstance().getTime().toString()+" end "+line);
            } catch (IOException ioe){
                writeData("Status:Error");
                System.out.println("Could not talk because of IOE: "+ioe.getMessage());
            } catch (Throwable t){
                writeData("Status:Error");
                t.printStackTrace();
            }
            try {
                socket.close();
                socket = null;
            } catch (Exception e){
                System.out.println("Could not close socket");
            }
            
            Runtime.getRuntime().gc();
            //System.out.println("Finished, free mem: "+Runtime.getRuntime().freeMemory());
            finished = true;
        }

        public void writeData(String data) {
            //System.out.println("write data: "+data);
            try {
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                osw.write(data);
                osw.flush();
                osw.close();
            } catch (Throwable t){
                t.printStackTrace();
            }
        }

        public void newInventoryItem(Received received, InventoryItem inventory, String condition){
            inventory.setIsSkid(false);
            inventory.setOnhand(0);
            inventory.setBellbook(0);
            inventory.setCondition(condition);
            AmazonItemLookupSoap.lookupData(inventory, true);
            if (received != null && received.getPublisher() != null && received.getPublisher().length() > 0){
                inventory.setCompanyRec(received.getPublisher());
                inventory.setPublisher(received.getPublisher());
            }
            dao.save(inventory);

            // amazon category and subject load
            AmazonItemLookupSoap.getCategories(dao, inventory);
            AmazonItemLookupSoap.getSubjects(dao, inventory);


        }
        
        private void userPin(String input) throws Exception{
            Integer pin = new Integer(input);
            User user = dao.findUserByPin(pin);
            if (user == null){
                writeData("Error:Invalid Pin");
            } else {
                writeData("Success:User Logged In:UserName:"+user.getUsername()+":UserPin:"+user.getPin());
            }
        }
        
        private void getOrderItems(String input) throws Exception {
            Integer oid = new Integer(input);
            CustomerOrder co = dao.findOrderById(oid);
            if (co == null){
                writeData("Error:The order has been deleted!");
            } else {
                StringBuilder sb = new StringBuilder();
                ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(co.getCustomerOrderItems());
                Object[] itemArray = items.toArray();
                Arrays.sort(itemArray);

                int count = 0;
                int totalItems = 0;
                double totalPrice = 0D;
                for (int i = 0; i < itemArray.length; i++){
                    CustomerOrderItem coi = (CustomerOrderItem)itemArray[i];
                    if (count > 0){
                        sb.append(":");
                    }
                    sb.append(coi.getId());
                    sb.append(":");
                    sb.append(coi.getQuantity());
                    sb.append(" - ");
                    sb.append(coi.getBin());
                    totalItems += coi.getQuantity();
                    totalPrice += coi.getTotalPriceNonShipped();
                    sb.append(" - ");
                    sb.append(coi.getIsbn());
                    sb.append(" - ");
                    if (coi.getTitle() != null){
                        sb.append(coi.getTitle().replace(":", "&colon;"));
                    } else {
                        sb.append("unknown");
                    }
                    count++;
                }
                if (items == null || items.size() == 0){
                    sb.append("Info:No items on this order.");
                } else {
                    sb.append(":TotalItems:");
                    sb.append(totalItems);
                    sb.append(":TotalPrice:");
                    sb.append(PriceFormat.format(totalPrice));
                }
                sb.append(":InvoiceNumber:");
                sb.append(co.getInvoiceNumber());
                writeData(sb.toString());
            }
        }
        
        private void getRecItems(String input) throws Exception {
            Integer oid = new Integer(input);
            Received rec = dao.findRecById(oid);
            if (rec == null){
                writeData("Error:The receiving has been deleted!");
            } else {
                StringBuilder sb = new StringBuilder();
                ArrayList<ReceivedItem> items = new ArrayList<ReceivedItem>(rec.getReceivedItems());

                int count = 0;
                for (ReceivedItem ri : items){
                    if (count > 0){
                        sb.append(":");
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
                    count++;
                }
                if (items == null || items.size() == 0){
                    sb.append("Info:No items on this receiving.");
                }
                sb.append(":RecDate:");
                sb.append(DateFormat.format(rec.getDate()));
                sb.append(":Vendor:");
                if (rec.getVendor() != null){
                    sb.append(rec.getVendor().getVendorName());
                } else {
                    sb.append(" ");
                }
                writeData(sb.toString());
                items = null;
            }            
        }
        
        private void deleteFromOrder(String input) throws Exception {
            Integer oid = new Integer(input);
            CustomerOrderItem coi = dao.findOrderItemById(oid);
            if (coi != null){
                CustomerOrder co = coi.getCustomerOrder();
                InventoryItem ii = coi.getInventoryItem();
                dao.delete(coi);
                // LIFO BEGIN
                int committed = dao.getCommitted(ii.getId());
                ii.setCommitted(committed);
                ii.setAvailable(ii.getOnhand()-committed);
                dao.update(ii);
                // END LIFO
                co = dao.findOrderById(co.getId());
                ArrayList<CustomerOrderItem> items = new ArrayList<CustomerOrderItem>(co.getCustomerOrderItems());
                Object[] itemArray = items.toArray();
                Arrays.sort(itemArray);

                int count = 0;
                int totalItems = 0;
                double totalPrice = 0D;
                for (int i = 0; i < itemArray.length; i++){
                    coi = (CustomerOrderItem)itemArray[i];
                    totalItems += coi.getQuantity();
                    totalPrice += coi.getTotalPriceNonShipped();
                    count++;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Deleted Item from order");
                sb.append(":TotalItems:");
                sb.append(totalItems);
                sb.append(":TotalPrice:");
                sb.append(PriceFormat.format(totalPrice));
                writeData(sb.toString());
            } else {
                writeData("Error: Could not delete item from order");
            }            
        }
        
        private void deleteFromRec(String input) throws Exception {
            Integer oid = new Integer(input);
            ReceivedItem ri = dao.findReceivedItemById(oid);
            if (ri == null){
                System.out.println("ERROR: received item is null for "+input);
                writeData("Error: Could not delete item from receiving");
                return;
            }
            InventoryItem ii = findInventoryItem(ri.getIsbn(), ri.getCondition());
            if (ri != null && ii != null){
                Received rec = ri.getReceived();
                dao.delete(ri);
                // LIFO BEGIN
                ii.setOnhand(ii.getOnhand()-ri.getQuantity());
                int committed = dao.getCommitted(ii.getId());
                ii.setCommitted(committed);
                ii.setAvailable(ii.getOnhand()-committed);
                dao.update(ii);
                // END LIFO
                
                StringBuilder sb = new StringBuilder();
                rec = dao.findRecById(rec.getId());
                ArrayList<ReceivedItem> items = new ArrayList<ReceivedItem>(rec.getReceivedItems());

                int count = 0;
                for (ReceivedItem recitem : items){
                    if (count > 0){
                        sb.append(":");
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
                    count++;
                }
                if (items == null || items.size() == 0){
                    sb.append("Info:No items on this receiving.");
                }
                sb.append(":RecDate:");
                sb.append(DateFormat.format(rec.getDate()));
                if (rec.getVendor() != null){
                    sb.append(":Vendor:");
                    sb.append(rec.getVendor().getVendorName());
                } else {
                    sb.append(":Vendor:");
                    sb.append(" ");
                }
                writeData(sb.toString());
            } else {
                System.out.println("ERROR: invenotry item is null for "+ri.getIsbn()+" "+ri.getCondition());
                writeData("Error: Could not delete item from receiving");
            }
        }
        
        private void addToOrder(String input, Date now) throws Exception {
            // input is the order id, isbn, and quantity to add
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            int count = inputSt.countTokens();
            if (count < 2){
                writeData("Error:Could not add to order");
            } else {
                Integer oid = new Integer(inputSt.nextToken());
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
                    writeData("Error: Could not add, BAD QUANTITY!");
                }
                qty = invqty + breakqty + bellqty;
                try {
                    price = new Float(inputSt.nextToken());
                } catch (Exception e){
                    // bad price
                    writeData("Error: Could not add, BAD PRICE!");
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
                CustomerOrder co = new CustomerOrder();
                if (oid > -1){
                    co = dao.findOrderById(oid);
                }
                InventoryItem ii = findInventoryItem(isbn, cond);
                // if it is not found we need to look it up on amazon
                if (ii == null){
                    //System.out.println("Adding new inventory item: "+isbn);
                    // new inventory item
                    ii = new InventoryItem();
                    isbn = IsbnUtil.getIsbn10(isbn);
                    ii.setIsbn(isbn);
                    newInventoryItem(null, ii, cond);
                    // now we need a new receiving for this order quantity
    **********  commment
                    Received rec = new Received();
                    rec.setPoDate(now);
                    rec.setPoNumber("Scanner");
                    rec.setEnteredDate(now);
                    dao.save(rec);
                    ReceivedItem ri = new ReceivedItem();
                    ri.setIsbn(isbn);
                    ri.setCondition(cond);
                    ri.setTitle(ii.getTitle());
                    ri.setQuantity(qty);
                    ri.setOrderedQuantity(qty);
                    ri.setAvailable(qty);
                    ri.setListPrice(ii.getListPrice());
                    ri.setSellPrice(ii.getSellingPrice());
                    ri.setCoverType(ii.getCover());
                    ri.setBookType(ii.getBiblio());
                    // update inventory item
                    ii.setReceivedQuantity(ri.getQuantity());
                    ii.setLastpo(rec.getPoNumber());
                    ii.setLastpoDate(rec.getPoDate());
                    ii.setReceivedDate(now);
                    ii.setReceivedQuantity(ri.getQuantity());
                    dao.update(ii);

                    ri.setBin(ii.getBin());
                    ri.setCost(ii.getCost());
                    ri.setType("Pieces");
                    ri.setReceived(rec);
                    dao.save(ri);
    **********  commment
                }
                CustomerOrderItem coi = null;
                for (CustomerOrderItem item : (List<CustomerOrderItem>)co.getCustomerOrderItems()){
                    if (item.getIsbn().equals(isbn) && item.getCondition().equals(cond)){
                        coi = item;
                        coi.setQuantity(qty);
                        coi.setInvQuantity(invqty);
                        coi.setBreakQuantity(breakqty);
                        coi.setBellQuantity(bellqty);
                        coi.setPrice(price);
                        coi.setLastUpdate(now);
                        if (title != null && title.trim().length() > 0){
                            coi.setTitle(title);
                        }
                        dao.update(coi);
                        break;
                    }
                }
                if (coi == null){
                    coi = new CustomerOrderItem();
                    coi.setIsbn(isbn);
                    coi.setCondition(cond);
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
                    if (co.getCustomer() != null && co.getCustomer().getDiscount() != null && co.getCustomer().getDiscount() > 0F){
                        coi.setDiscount(new Float(co.getCustomer().getDiscount()));
                    } else {
                        coi.setDiscount(0F);
                    }
                    coi.setCustomerOrder(co);
                    coi.setEnteredDate(now);
                    coi.setLastUpdate(now);
                    coi.setInventoryItem(ii);
                    coi.setPrice(price);
                    if (title != null && title.trim().length() > 0){
                        coi.setTitle(title);
                    }
                    coi.setType("Pieces");
                    dao.save(coi);
                }

                // LIFO BEGIN
                int committed = dao.getCommitted(ii.getId());
                ii.setCommitted(committed);
                ii.setAvailable(ii.getOnhand()-committed);
                dao.update(ii);
                // END LIFO
                writeData("Added "+qty.toString()+" to order.");
            }            
        }
        
        private void createOrder(String input, Date now) throws Exception {
            // the input is the customer oid
            Customer c = null;
            try {
                c = (Customer)dao.findById(Customer.class, new Integer(input));
            } catch (Exception e){}
            if (c == null){
                // return error
                writeData("Error:No Customer.");
            } else {
                // create an order
                InvoiceNumber invNo = new InvoiceNumber();
                Integer invNoId = (Integer)dao.save(invNo);
                CustomerOrder co = new CustomerOrder();
                co.setSalesman(c.getSalesRep());
                co.setShipVia(co.getShipVia());
                co.setCustomer(c);
                // TODO we may need to do shipping over here
                //co.setCustomerShipping(blah);
                co.setInvoiceNumber(invNoId.toString());
                co.setCreditMemo(false);
                co.setPosted(0);
                co.setCustomerCode(c.getCode());
                co.setCustomerVisit(false);
                co.setOrderDate(now);
                co.setEnteredDate(now);
                dao.save(co);
                writeData("Created:"+co.getId().toString());
            }            
        }
        
        private void createReceiving(String input, Date now) throws Exception {
            // the input is the vendor oid
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            String vid = input;
            String ponum = "Scanner";
            if (inputSt.countTokens() == 2){
                vid = inputSt.nextToken();
                ponum = inputSt.nextToken();
            }
            Vendor v = null;
            try {
                v = (Vendor)dao.findById(Vendor.class, new Integer(vid));
            } catch (Exception e){}
            if (v == null){
                // return error
                writeData("Error:No Customer.");
            } else {
                // create a receiving
                Received rec = new Received();
                rec.setVendor(v);
                rec.setVendorCode(v.getCode());
                rec.setPoNumber(ponum);
                rec.setEnteredDate(now);
                rec.setSkid(false);
                rec.setDate(now);
                dao.save(rec);
                writeData("Created:"+rec.getId().toString());
            }            
        }
        
        private void getCustomers() throws Exception {
            List<Customer> customers = dao.findAll(Customer.class, "code", true);
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Customer c : customers){
                if (count > 0){
                    sb.append(":");
                }
                sb.append(c.getId());
                sb.append(":");
                if (c.getCode() != null){
                    sb.append(c.getCode().replace(":", "&colon;"));
                } else {
                    sb.append(c.getCompanyName().replace(":", "&colon;"));
                }
                count++;
            }
            if (customers == null || customers.size() == 0){
                sb.append("Error:Could not find any customers!");
            }
            writeData(sb.toString());            
        }

        private void getVendors() throws Exception {
            List<Vendor> vendors = dao.findAll(Vendor.class, "code", true);
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Vendor v : vendors){
                if (count > 0){
                    sb.append(":");
                }
                sb.append(v.getId());
                sb.append(":");
                if (v.getCode() != null){
                    sb.append(v.getCode().replace(":", "&colon;"));
                } else {
                    sb.append(v.getVendorName().replace(":", "&colon;"));
                }
                count++;
            }
            if (vendors == null || vendors.size() == 0){
                sb.append("Error:Could not find any customers!");
            }
            writeData(sb.toString());            
        }
        
        public void getPendingOrders() throws Exception {
            List<CustomerOrder> orders = dao.findPendingOrdrs();
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (CustomerOrder co : orders){
                if (count > 0){
                    sb.append(":");
                }
                sb.append(co.getId());
                sb.append(":");
                sb.append(DateFormat.format(co.getEnteredDate()));
                sb.append(" - ");
                sb.append(co.getInvoiceNumber());
                sb.append(" - ");
                sb.append(co.getCustomerCode());
                count++;
            }
            if (orders == null || orders.size() == 0){
                sb.append("Error:Could not find any pending orders!");
            }
            writeData(sb.toString());            
        }
        
        private void updateRecItem(String input) throws Exception {
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
                writeData("Error:Quantity must be a number!");
                return;
            }
            ReceivedItem ri = dao.findReceivedItemById(Integer.parseInt(recItemId));
            InventoryItem ii = dao.findInventoryItemByIsbnCond(ri.getIsbn(), ri.getCondition());
            if (ri == null || ii == null){
                writeData("Error:Could not find inventory item or rec item!");
                return;
            }
            ii.setOnhand(ii.getOnhand()-ri.getQuantity());
            ii.setOnhand(ii.getOnhand()+qty);
            int committed = dao.getCommitted(ii.getId());
            ii.setCommitted(committed);
            ii.setAvailable(ii.getOnhand()-committed);
            if (ii.getBin() != bin){
                ii.setBin(bin);
            }
            ri.setQuantity(qty);
            ri.setBin(bin);
            dao.update(ri);
            dao.update(ii);
            dao.updatePendingBins(ii.getIsbn(), ii.getCondition(), ii.getBin());
            writeData("Updated received item");            
        }
        
        private void getRecItem(String input) throws Exception {
            ReceivedItem ri = dao.findReceivedItemById(Integer.parseInt(input));
            if (ri == null){
                writeData("Error:Could not find received item!");
                return;
            }
            InventoryItem ii = dao.findInventoryItemByIsbnCond(ri.getIsbn(), ri.getCondition());
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
            if (ii != null && ii.getBellbookBoolean()){
                addData(sb, "bell", "true");
            } else {
                addData(sb, "bell", "false");
            }
            addData(sb, "bin", ri.getBin());
            addData(sb, "cond", ri.getCondition());
            addData(sb, "title", ri.getTitle(), true, "unknown");
            
            writeData(sb.toString());            
        }
        
        private void getPendingRecs() throws Exception {
            //System.out.println(sid+"  "+now.toString()+"- start of get pending recs");
            List<Received> pending = dao.findPendingRecs();
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Received rec : pending){
                if (count > 0){
                    sb.append(":");
                }
                sb.append(rec.getId());
                sb.append(":");
                sb.append(DateFormat.format(rec.getDate()).replace(":", "&colon;"));
                if (rec.getPoNumber() != null){
                    sb.append(" - ");
                    sb.append(rec.getPoNumber().replace(":", "&colon;"));
                }
                if (rec.getComment() != null && rec.getComment().length() > 0){
                    sb.append(" - ");
                    sb.append(rec.getComment().replace(":", "&colon;"));
                }
                count++;
            }
            if (pending == null || pending.size() == 0){
                sb.append("Error:Could not find any pending!");
            }
            long end = System.currentTimeMillis();
            //System.out.println(sid+"  "+now.toString()+"- pending get time: "+(end-start)/1000.0);
            writeData(sb.toString());            
        }
        
        private void getManifests() throws Exception {
            List<Manifest> manifests = dao.findAll(Manifest.class, "date", true);
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Manifest m : manifests){
                if (count > 0){
                    sb.append(":");
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
                count++;
            }
            if (manifests == null || manifests.size() == 0){
                sb.append("Error:Could not find any manifests!");
            }
            writeData(sb.toString());            
        }
        
        private void getManifestItems(String input) throws Exception {
            Manifest m = dao.findManifestById(Integer.parseInt(input));
            if (m == null){
                writeData("Error:Could not find manifest!");
                return;
            } else {
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (ManifestItem mi : m.getManifestItems()){
                    if (count > 0){
                        sb.append(":");
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
                    count++;
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
                writeData(sb.toString());
            }
        }
        
        private void deleteManifestItem(String input) throws Exception {
            ManifestItem mi = dao.findManifestItemById(Integer.parseInt(input));
            if (mi == null){
                writeData("Error:Could not find manifest item!");
                return;
            } else {
                Integer mid = mi.getManifest().getId();
                dao.delete(mi);
                Manifest m = dao.findManifestById(mid);
                writeData("TotalItems:"+m.getManifestItems().size());
            }
        }
        
        private void getManifestIsbnInfo(String input) throws Exception {
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            Integer manifestId = Integer.parseInt(inputSt.nextToken());
            if (!inputSt.hasMoreTokens()) {
                writeData("Error: No ISBN provided.");
                return;
            }
            String isbn = inputSt.nextToken();
            if (isbn.length() == 13){
                isbn = IsbnUtil.getIsbn10(isbn);
            }
            Manifest m = dao.findManifestById(manifestId);
            ManifestItem foundMi = null;
            for (ManifestItem mi : m.getManifestItems()){
                if (mi.getIsbn() != null && mi.getIsbn().equals(isbn)){
                    foundMi = mi;
                    break;
                }
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
                AmazonItemLookupSoap.lookupData(ii, true);
                addData(sb, "isbn", isbn);
                addData(sb, "10digit", IsbnUtil.getIsbn10(isbn));
                addData(sb, "13digit", IsbnUtil.getIsbn13(isbn));
                addData(sb, "Title", ii.getTitle(), true, "");
            }
            writeData(sb.toString());
        }
        
        private void addManifestItem(String input) throws Exception {
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            Integer manifestId = Integer.parseInt(inputSt.nextToken());
            String isbn = inputSt.nextToken();
            Integer qty = 0;
            try {
                qty = Integer.parseInt(inputSt.nextToken());
            } catch (NumberFormatException ne){
                writeData("Error:Bad Quantity");
                return;
            }
            String title = "";
            if (inputSt.hasMoreTokens()){
                title = inputSt.nextToken().trim();
            }
            System.out.println("addManifestItem: "+manifestId+" "+isbn+" "+qty+" "+title);
            if (isbn.length() == 13){
                isbn = IsbnUtil.getIsbn10(isbn);
            }
            if (isbn == null || isbn.length() == 0){
                writeData("Error:No ISBN to add");
                return;
            }
            if (title.length() == 0){
                InventoryItem ii = new InventoryItem();
                isbn = IsbnUtil.getIsbn10(isbn);
                ii.setIsbn(isbn);
                AmazonItemLookupSoap.lookupData(ii, true);
                title = ii.getTitle();
                if (title == null) title = "";
            }
            Manifest m = dao.findManifestById(manifestId);
            ManifestItem foundMi = null;
            for (ManifestItem mi : m.getManifestItems()){
                if (mi.getIsbn() != null && mi.getIsbn().equals(isbn)){
                    foundMi = mi;
                    break;
                }
            }
            if (foundMi != null){
                foundMi.setTitle(title);
                foundMi.setQuantity(qty);
                dao.update(foundMi);
            } else {
                ManifestItem mi = new ManifestItem();
                mi.setManifest(m);
                mi.setIsbn(isbn);
                mi.setIsbn13(IsbnUtil.getIsbn13(isbn));
                mi.setQuantity(qty);
                mi.setTitle(title);
                dao.save(mi);
            }
        }

        private void updateBin(String input) throws Exception {
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            String cond = inputSt.nextToken();
            String isbn = inputSt.nextToken();
            String bin = inputSt.nextToken();
            InventoryItem ii = findInventoryItem(isbn, cond);
            StringBuilder sb = new StringBuilder();
            if (ii != null && bin != null && bin.length() > 0){
                ii.setBin(bin);
                dao.update(ii);
                dao.updatePendingBins(ii.getIsbn(), ii.getCondition(), ii.getBin());
                sb.append("Success.  Bin updated to: "+bin);
            } else {
                sb.append("ERROR: Did not update the bin.");
            }
            
            writeData(sb.toString());            
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
        
        private void getIsbnRecInfo(String input) throws Exception {
            getIsbnInfo(input, true);
        }
        private void getIsbnInfo(String input) throws Exception {
            getIsbnInfo(input, false);
        }
        private void getIsbnInfo(String input, boolean rec) throws Exception {
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            String cond = inputSt.nextToken();
            String isbn = inputSt.nextToken();
            Boolean checkRec = rec;
            Boolean checkOrder = false;
            Integer oid = -1;
            if (inputSt.hasMoreTokens()){
                oid = new Integer(inputSt.nextToken());
                checkOrder = true;
            }
            InventoryItem ii = findInventoryItem(isbn, cond);
            StringBuilder sb = new StringBuilder();
            if (ii == null){
                ii = new InventoryItem();
                addData(sb, "Status", "NotFound");
                isbn = IsbnUtil.getIsbn10(isbn);
                ii.setIsbn(isbn);
                AmazonItemLookupSoap.lookupData(ii, true);
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
                addData(sb, "salesRank", ii.getAmazonSalesRank());
                addData(sb, "Title", ii.getTitle(), true, "unknown");
                addData(sb, "listPrice", PriceFormat.format(ii.getListPrice()));
                addData(sb, "listPriceNoFormat", nosignFormat.format(ii.getListPrice()));
            } else {
                AmazonItemLookupSoap.lookupData(ii, false);
                addData(sb, "Status", "Found");
                addData(sb, "Bin", ii.getBin());
                addData(sb, "Title", ii.getTitle(), true, "unknown");
                if (ii.getBellbookBoolean()){
                    addData(sb, "bell", "true");
                } else {
                    addData(sb, "bell", "false");
                }                     
                addData(sb, "OnHand", ii.getOnhand());
                addData(sb, "isbn", ii.getIsbn());
                addData(sb, "cover", ii.getCover());
                addData(sb, "listPrice", PriceFormat.format(ii.getListPrice()));
                addData(sb, "listPriceNoFormat", nosignFormat.format(ii.getListPrice()));
                addData(sb, "sellPrice", PriceFormat.format(ii.getSellingPrice()));
                addData(sb, "Price", nosignFormat.format(ii.getSellingPrice()));
                addData(sb, "available", ii.getAvailable());
                addData(sb, "committed", ii.getCommitted());
                addData(sb, "lastRec", DateFormat.format(ii.getReceivedDate()));
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
                addData(sb, "salesRank", ii.getAmazonSalesRank());
            }
            if (checkRec && oid > -1){
                Received received = dao.findRecById(oid);
                for (ReceivedItem ri : (List<ReceivedItem>)received.getReceivedItems()){
                    if (ri.getIsbn().equals(ii.getIsbn()) && ri.getCondition().equals(ii.getCondition())){
                        addData(sb, "quantity", ri.getQuantity());
                        break;
                    }
                }
            } else if (checkOrder && oid > -1){
                // see if this is part of this order
                CustomerOrder co = dao.findOrderById(oid);
                for (CustomerOrderItem coi : (List<CustomerOrderItem>)co.getCustomerOrderItems()){
                    if (coi.getIsbn().equals(ii.getIsbn()) && coi.getCondition().equals(ii.getCondition())){
                        addData(sb, "invQty", coi.getInvQuantity());
                        addData(sb, "bellQty", coi.getBellQuantity());
                        addData(sb, "breakQty", coi.getBreakQuantity());
                        break;
                    }
                }
            }
            //System.out.println(sb.toString());
            writeData(sb.toString());            
        }
        
        private void addToRec(String input, Date now) throws Exception {
            // first token is the receiving id, second is qty and the rest is the isbn to add
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            StringBuilder sb = new StringBuilder();
            if (inputSt.countTokens() < 3){
                writeData("Error, Could not add to receiving");
            } else {
                Integer recId = new Integer(inputSt.nextToken());
                Integer qty = 0;
                try {
                    qty = new Integer(inputSt.nextToken());
                } catch (Exception e){
                    // bad qty
                    writeData("ERROR - Could not add, BAD QUANTITY!");
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
                Received rec = new Received();
                rec.setPoDate(now);
                rec.setEnteredDate(now);
                if (recId > -1){
                    rec = dao.findRecById(recId);
                } else {
                    dao.save(rec);
                }
                InventoryItem ii = findInventoryItem(isbn, cond);
                // if it is not found we need to look it up on amazon
                boolean newInv = false;
                if (ii == null){
                    // new inventory item
                    ii = new InventoryItem();
                    isbn = IsbnUtil.getIsbn10(isbn);
                    ii.setIsbn(isbn);
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
                for (ReceivedItem test : (List<ReceivedItem>)rec.getReceivedItems()){
                    if (test.getIsbn().equals(ii.getIsbn()) && test.getCondition().equals(ii.getCondition())){
                        ri = test;
                        update = true;
                        prevqty = ri.getQuantity();
                        break;
                    }
                }
                ri.setIsbn(isbn);
                ri.setCondition(cond);
                if (cover != null){
                    ri.setCoverType(cover);
                }
                if (override){
                    ri.setTitle(titleOverride);
                } else {
                    ri.setTitle(ii.getTitle());
                }
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
                    dao.updatePendingBins(ii.getIsbn(), ii.getCondition(), ii.getBin());
                }
                // update inventory item
                ii.setReceivedQuantity(ri.getQuantity());
                ii.setLastpo(rec.getPoNumber());
                if (ii.getLastpo().length() > 20){
                    ii.setLastpo(ii.getLastpo().substring(0, 20));
                }
                ii.setLastpoDate(rec.getPoDate());
                ii.setReceivedDate(now);
                ii.setReceivedQuantity(ri.getQuantity());
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
                ri.setType("Pieces");
                ri.setReceived(rec);
                if (update){
                    dao.update(ri);
                } else {
                    dao.save(ri);
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
                    ri.setBackordered(ri.getBackordered()-givenToBackorder);
                }
                if (ii.getOnhand() == null){
                    ii.setOnhand(ri.getQuantity()-prevqty-givenToBackorder);
                } else {
                    ii.setOnhand(ii.getOnhand()-prevqty+ri.getQuantity()-givenToBackorder);
                }
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                }

                ii.setLastpo(rec.getPoNumber());
                if (ii.getLastpo().length() > 20){
                    ii.setLastpo(ii.getLastpo().substring(0, 20));
                }
                ii.setReceivedQuantity(ri.getQuantity());
                ii.setReceivedPrice(ri.getCost());

                int committed = dao.getCommitted(ii.getId());
                ii.setCommitted(committed);
                ii.setAvailable(ii.getOnhand()-committed);

                dao.update(ii);
                // END LIFO


                StringBuilder wd = new StringBuilder();
                if (newInv){
                    wd.append("NewInventory:");
                }
                wd.append("Added ");
                wd.append(qty.toString());
                wd.append(" to receiving.");
                writeData(wd.toString());
            }            
        }
        
        private void getBellIsbnInfo(String input) throws Exception {
            StringTokenizer inputSt = new StringTokenizer(input, ";");
            String isbn = inputSt.nextToken();
            if (isbn.length() == 13 && isbn.startsWith("978")){
                isbn = IsbnUtil.getIsbn10(isbn);
            }
            InventoryItem ii = new InventoryItem();
            ii.setIsbn(isbn);
            DecimalFormat df = new DecimalFormat("###,###,###,###");
            AmazonItemLookupSoap.lookupData(ii, true);
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
            if (ii.getAmazonSalesRank() != null){
                addData(sb, "salesRank", df.format(new Long(ii.getAmazonSalesRank())));
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
                addData(sb, "listPriceNoFormat", nosignFormat.format(ii.getListPrice()));
            } else {
                addData(sb, "listPrice", " ");
                addData(sb, "listPriceNoFormat", " ");
            }
            
            BellInventory bi = dao.findBellInventoryItemByIsbn(isbn);
            String listed = "NO";
            int listedCount = 0;
            if (bi != null && bi.getListed() > 0){
                listed = "YES";
                listedCount = bi.getListed();
            }
            addData(sb, "listed", listed);
            addData(sb, "listedCount", ""+listedCount);
            writeData(sb.toString());            
        }
    }


    public static void main(String[] args){
        try {
            ScannerSocket ss = new ScannerSocket();
            ss.start();
            ss.join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    */
}
