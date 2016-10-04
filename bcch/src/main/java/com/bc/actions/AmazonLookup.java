package com.bc.actions;

import com.amazon.xml.AWSECommerceService.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.InventoryItem;
import com.bc.util.ActionRole;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;

public class AmazonLookup extends AmazonItemLookupSoap {

    private static final Logger logger = Logger.getLogger(AmazonLookup.class);
    
    private static final String browseNodes = "BrowseNodes";
    private static final String subjects = "Subjects";
    
    protected static AmazonLookup instance;
    
    private AmazonLookup(){}
    
    public static AmazonLookup getInstance(){ 
        if (instance == null){
            instance = new AmazonLookup();
            try {
                EngineConfiguration serviceConfig = new FileProvider(WSDD);
                AWSECommerceService locator = new AWSECommerceServiceLocator(serviceConfig);
                servicePort = locator.getAWSECommerceServicePort(PORT_ADDRESS);
            } catch (Exception e){
                throw new RuntimeException("Could not initialize the amazon services.", e);
            }
        }
        return instance; 
    }
    
    /**
     * Looks up data from amazon and stuffs it back into item
     *
     * @param item
     */
    @ActionRole({"WebUser"})
    public void lookupData(InventoryItem item){
        lookupData(item, false);
    }

    @ActionRole({"WebUser"})
    public void lookupData(InventoryItem item, boolean getTitle){
        try {
            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{item.getIsbn()};
            request[0].setItemId(items);
            request[0].setIdType(ItemLookupRequestIdType.ASIN);
            // This should be "All" if you want more than just amazon
            String[] rg = new String[]{RESPONSE_GROUP};
            request[0].setResponseGroup(rg);
            itemLookup.setRequest(request);
             
            ItemLookupResponse response = servicePort.itemLookup(itemLookup);
            Items[] responseItems = response.getItems();
            if (responseItems.length >= 1){
                Items ris = responseItems[0];
                Item[] ri = ris.getItem();
                if (ri != null && ri.length >= 1){
                    Item i = ri[0];
                    try {
                        item.setSalesRank(Integer.parseInt(i.getSalesRank()));
                        item.setAmazonDataLoaded(true);
                    } catch (Exception e){}
                    //item.setAmazonDetailPageUrl(i.getDetailPageURL());
                    if (i.getItemAttributes() != null && getTitle){
                        item.setTitle(i.getItemAttributes().getTitle());
                        item.setPublisher(i.getItemAttributes().getPublisher());
                        if (i.getItemAttributes().getListPrice() != null) item.setListPrice((i.getItemAttributes().getListPrice().getAmount().intValue())/100F);
                        StringBuilder author = new StringBuilder();
                        if (i.getItemAttributes().getAuthor() != null && i.getItemAttributes().getAuthor().length > 0){
                            for (int j = 0; j < i.getItemAttributes().getAuthor().length; j++){
                                if (j > 0) {
                                    author.append(", ");
                                }
                                author.append(i.getItemAttributes().getAuthor(j));
                            }
                            item.setAuthor(author.toString());
                        }
                    }

                    if (i.getOfferSummary() != null){
                        OfferSummary summary = i.getOfferSummary();
                        item.setAmazonTotalNew(summary.getTotalNew());
                        item.setAmazonTotalUsed(summary.getTotalUsed());
                        item.setAmazonTotalCollectible(summary.getTotalCollectible());
                        if (summary.getLowestNewPrice() != null){
                            Price p = summary.getLowestNewPrice();
                            item.setAmazonLowestNewPrice(p.getCurrencyCode()+" "+
                                p.getFormattedPrice());
                        }
                        if (summary.getLowestUsedPrice() != null){
                            Price p = summary.getLowestUsedPrice();
                            item.setAmazonLowestUsedPrice(p.getCurrencyCode()+" "+
                                p.getFormattedPrice());
                        }
                        if (summary.getLowestCollectiblePrice() != null){
                            Price p = summary.getLowestCollectiblePrice();
                            item.setAmazonLowestCollectiblePrice(p.getCurrencyCode()+" "+
                                p.getFormattedPrice());
                        }
                    }
                    if (i.getSmallImage() != null){
                        item.setSmallImage(i.getSmallImage().getURL());
                    }
                    if (i.getLargeImage() != null){
                        item.setMediumImage(i.getMediumImage().getURL());
                    }
                }
            }
        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+
                item.getIsbn()+" message: "+t.getMessage(), t);
        }

    }

    @ActionRole({"WebUser"})
    public void lookupData(BellInventory item, boolean getTitle){
        try {
            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{item.getIsbn()};
            request[0].setItemId(items);
            request[0].setIdType(ItemLookupRequestIdType.ASIN);
            // This should be "All" if you want more than just amazon
            String[] rg = new String[]{RESPONSE_GROUP};
            request[0].setResponseGroup(rg);
            itemLookup.setRequest(request);
             
            ItemLookupResponse response = servicePort.itemLookup(itemLookup);
            Items[] responseItems = response.getItems();
            if (responseItems.length >= 1){
                Items ris = responseItems[0];
                Item[] ri = ris.getItem();
                if (ri.length >= 1){
                    Item i = ri[0];
                    //item.setAmazonDetailPageUrl(i.getDetailPageURL());
                    if (i.getItemAttributes() != null && getTitle){
                        item.setTitle(i.getItemAttributes().getTitle());
                        item.setPublisher(i.getItemAttributes().getPublisher());
                        item.setListPrice((i.getItemAttributes().getListPrice().getAmount().intValue())/100F);
                        StringBuilder author = new StringBuilder();
                        if (i.getItemAttributes().getAuthor() != null && i.getItemAttributes().getAuthor().length > 0){
                            for (int j = 0; j < i.getItemAttributes().getAuthor().length; j++){
                                if (j > 0) {
                                    author.append(", ");
                                }
                                author.append(i.getItemAttributes().getAuthor(j));
                            }
                            item.setAuthor(author.toString());
                        }
                    }
                }
            }
        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+
                item.getIsbn()+" message: "+t.getMessage(), t);
        }

    }
    
    /**
     * Looks up data from amazon and stuffs it back into items, list can only be 10 items max
     *
     * @param item
     */
    @ActionRole({"WebUser"})
    public void lookupPublisherData(InventoryItem item){
        try {
            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] isbnList = new String[]{item.getIsbn()};
            request[0].setItemId(isbnList);
            String[] rg = new String[]{"ItemAttributes"};
            request[0].setResponseGroup(rg);
            request[0].setIdType(ItemLookupRequestIdType.ASIN);
            itemLookup.setRequest(request);
            ItemLookupResponse response = servicePort.itemLookup(itemLookup);
            Items[] responseItems = response.getItems();
            if (responseItems.length >= 1){
                Items ris = responseItems[0];
                Item[] ri = ris.getItem();
                if (ri.length >= 1){
                    Item i = ri[0];
                    if (i.getItemAttributes() != null){
                        item.setPublisher(i.getItemAttributes().getPublisher());
                    }
                }
            }
        } catch (Throwable t){
            // not printing this
            //logger.error("Could not get response from amazon", t);
        }

    }

    @ActionRole({"WebUser"})
    public void getCategories(BaseDao dao, InventoryItem item){
        try {
            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{item.getIsbn()};
            request[0].setItemId(items);
            String[] rg = new String[]{browseNodes};
            request[0].setResponseGroup(rg);
            request[0].setIdType(ItemLookupRequestIdType.ASIN);
            itemLookup.setRequest(request);
            ItemLookupResponse resp = servicePort.itemLookup(itemLookup);

            if (resp != null && resp.getItems() != null){
                for (Items respItems : resp.getItems()){
                    if (respItems.getItem() != null){
                        for (Item respItem : respItems.getItem()){
                            String isbn = respItem.getASIN();
                            if (respItem.getBrowseNodes() != null){
                                BrowseNodes nodes = respItem.getBrowseNodes();
                                if (nodes.getBrowseNode() != null){
                                    HashMap<Integer, List<String>> nodeMap = new HashMap<Integer, List<String>>();
                                    recurseNodes(nodes.getBrowseNode(), nodeMap, 1);

                                    // now we would want to save this node map for this isbn
                                    for (Integer key : nodeMap.keySet()){
                                        List<String> cats = nodeMap.get(key);
                                        if (cats.size() > 1 && !cats.contains("Stores")){
                                            /*
                                            AmzCategory ac = new AmzCategory();
                                            ac.setInventoryItem(item);
                                            dao.save(ac);
                                            Collections.reverse(cats);
                                            int catcount = 0;
                                            for (String cat : cats){
                                                if (!cat.equals("Subjects") && !cat.equals("Books")){
                                                    AmzCategoryAncestor anc = new AmzCategoryAncestor();
                                                    anc.setName(cat);
                                                    anc.setLevel(catcount++);
                                                    anc.setAmzCategory(ac);
                                                    dao.save(anc);
                                                }
                                            }
                                            */
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Could not find categories at amazon for: "+item.getIsbn());
            }

        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+
                item.getIsbn()+" message: "+t.getMessage());
        }

    }


    private void recurseNodes(BrowseNode[] nodes, HashMap<Integer, List<String>> nodeMap, int level){
        if (nodes != null){
            for (BrowseNode node : nodes){
                if (level == 1){
                    nodeMap.put(nodeMap.size()+1, new ArrayList<String>());
                }
                List<String> list = nodeMap.get(nodeMap.size());
                list.add(node.getName());
                recurseNodes(node.getAncestors(), nodeMap, level+1);
            }
        }
    }

    @ActionRole({"WebUser"})
    public void getSubjects(BaseDao dao, InventoryItem item){
        try {
            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{item.getIsbn()};
            request[0].setItemId(items);
            String[] rg = new String[]{subjects};
            request[0].setResponseGroup(rg);
            request[0].setIdType(ItemLookupRequestIdType.ASIN);
            itemLookup.setRequest(request);
            ItemLookupResponse resp = servicePort.itemLookup(itemLookup);


            if (resp != null && resp.getItems() != null){
                for (Items respItems : resp.getItems()){
                    if (respItems.getItem() != null){
                        for (Item respItem : respItems.getItem()){
                            String isbn = respItem.getASIN();
                            if (respItem.getSubjects() != null){
                                String[] subjects = respItem.getSubjects();
                                for (String subject : subjects){
                                    /*
                                    AmzSubject amzSub = new AmzSubject();
                                    amzSub.setInventoryItem(item);
                                    amzSub.setSubject(subject);
                                    dao.save(amzSub);
                                    */
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Could not find subjects at amazon for: "+item.getIsbn());
            }

        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+
                item.getIsbn()+" message: "+t.getMessage());
        }

    }

    
}
