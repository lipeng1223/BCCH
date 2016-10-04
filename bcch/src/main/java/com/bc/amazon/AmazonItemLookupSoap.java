package com.bc.amazon;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.log4j.Logger;

import com.amazon.xml.AWSECommerceService.AWSECommerceService;
import com.amazon.xml.AWSECommerceService.AWSECommerceServiceLocator;
import com.amazon.xml.AWSECommerceService.AWSECommerceServicePortType;
import com.amazon.xml.AWSECommerceService.BrowseNode;
import com.amazon.xml.AWSECommerceService.BrowseNodes;
import com.amazon.xml.AWSECommerceService.Condition;
import com.amazon.xml.AWSECommerceService.Item;
import com.amazon.xml.AWSECommerceService.ItemLookup;
import com.amazon.xml.AWSECommerceService.ItemLookupRequest;
import com.amazon.xml.AWSECommerceService.ItemLookupRequestIdType;
import com.amazon.xml.AWSECommerceService.ItemLookupResponse;
import com.amazon.xml.AWSECommerceService.Items;
import org.apache.axis.client.Stub;

/**
 * Class that provides ItemLookup using SOAP.
 */
public class AmazonItemLookupSoap {

    private static final Logger logger = Logger.getLogger(AmazonItemLookupSoap.class);

    protected static AmazonItemLookupSoap instance;
    protected static AWSECommerceServicePortType servicePort;
    protected static AWSECommerceService locator;

    
    //protected static final String ASSOCIATE_TAG = "6465-8603-5133";    
    //protected static final String ACCESS_KEY_ID = "0EMJGE2EMRSCMR945F02";
    protected static final String ASSOCIATE_TAG = "1679-1406-6356";
    protected static final String ACCESS_KEY_ID = "AKIAIXXZGB4UAEDFERYA";
    protected static final String RESPONSE_GROUP = "Medium";
    
    protected static String BROWSE_NODES = "BrowseNodes";
    protected static String SUBJECTS = "Subjects";

    
    protected static final String WSDD = "x509.wsdd";
    protected static final String END_POINT = "soap.amazon.com";
    protected static URL PORT_ADDRESS;

    public static final String US_URL = "https://soap.amazon.com/onca/soap?Service=AWSECommerceService";
    public static final String UK_URL = "https://soap.amazon.co.uk/onca/soap?Service=AWSECommerceService";
    public static final String DE_URL = "https://soap.amazon.de/onca/soap?Service=AWSECommerceService";
    public static final String JP_URL = "https://soap.amazon.co.jp/onca/soap?Service=AWSECommerceService";
    public static final String FR_URL = "https://soap.amazon.fr/onca/soap?Service=AWSECommerceService";
    public static final String CA_URL = "https://soap.amazon.ca/onca/soap?Service=AWSECommerceService";

    static {
        try {
            PORT_ADDRESS = new URL("https://" + END_POINT + "/onca/soap?Service=AWSECommerceService");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    
    public static AmazonItemLookupSoap getInstance(){
        if (instance == null) {
            instance = new AmazonItemLookupSoap();
            try {
                EngineConfiguration serviceConfig = new FileProvider(WSDD);
                locator = new AWSECommerceServiceLocator(serviceConfig);
            } catch (Exception e){
                throw new RuntimeException("Could not initialize the amazon services.", e);
            }
        }
        try {
            servicePort = locator.getAWSECommerceServicePort(PORT_ADDRESS);
            Stub s = (Stub) servicePort;
            s.setTimeout(10000);
        } catch (Exception e){
            throw new RuntimeException("Could not initialize the amazon services", e);
        }
        return instance;
    }
    
    
    
    // used to lookup data and then load this into AmazonData
    public AmazonData lookupAmazonData(String isbn) {
        if (isbn == null) return null;
        return new AmazonData(lookupData(new String[]{isbn}, RESPONSE_GROUP, null, null, ACCESS_KEY_ID));
    }

    // used to lookup data and then load this into AmazonData
    public HashMap<String, String> lookupTitles(String[] isbns) {
        if (isbns == null) return null;
        HashMap<String, String> titles = new HashMap<String, String>(isbns.length);
        ItemLookupResponse response = lookupData(isbns, RESPONSE_GROUP, null, null, ACCESS_KEY_ID);
        if (response.getItems() != null) {
            for (Items ris : response.getItems()){
                if (ris != null && ris.getItem() != null){
                    for (Item i : ris.getItem()){
                        if (i.getItemAttributes() != null){
                            titles.put(i.getASIN(), i.getItemAttributes().getTitle());
                            //System.out.println(i.getItemAttributes().getTitle());
                        }
                    }
                }
            }
        }
        return titles;
    }


    public ItemLookupResponse lookupData(String[] productIds,
                                                String respGroup,
                                                Condition condition,
                                                String merchantId)
    {
        return lookupData(productIds, respGroup, condition, merchantId, ACCESS_KEY_ID);
    }

    public ItemLookupResponse lookupData(String[] productIds,
                                                String respGroup,
                                                Condition condition,
                                                String merchantId,
                                                String sid)
    {
        if (productIds.length == 0) return null;

        try {
            String[] list1 = null;
            String[] list2 = null;
            if (productIds.length > 10){
                list1 = new String[10];
                for (int i = 0; i < list1.length; i++){
                    list1[i] = productIds[i];
                }
                list2 = new String[productIds.length-10];
                for (int i = 0; i < list2.length; i++){
                    list2[i] = productIds[i+10];
                }
            } else {
                list1 = new String[productIds.length];
                for (int i = 0; i < list1.length; i++){
                    list1[i] = productIds[i];
                }
            }

            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(sid);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            if (list2 == null){
                ItemLookupRequest[] request = new ItemLookupRequest[1];
                request[0] = new ItemLookupRequest();
                request[0].setItemId(productIds);
                request[0].setIdType(ItemLookupRequestIdType.ASIN);
                // This should be "All" if you want more than just amazon
                if (merchantId != null){
                    request[0].setMerchantId(merchantId);
                }
                String[] rg = new String[]{respGroup};
                request[0].setResponseGroup(rg);
                if (condition != null){
                    request[0].setCondition(condition);
                }
                itemLookup.setRequest(request);
            } else {
                ItemLookupRequest shared = new ItemLookupRequest();
                String[] rg = new String[]{respGroup};
                shared.setResponseGroup(rg);
                itemLookup.setShared(shared);

                ItemLookupRequest[] request = new ItemLookupRequest[2];
                request[0] = new ItemLookupRequest();
                request[0].setItemId(list1);
                // This should be "All" if you want more than just amazon
                if (merchantId != null){
                    request[0].setMerchantId(merchantId);
                }
                if (condition != null){
                    request[0].setCondition(condition);
                }
                request[1] = new ItemLookupRequest();
                request[1].setItemId(list2);
                // This should be "All" if you want more than just amazon
                request[1].setMerchantId(merchantId);
                request[1].setCondition(condition);
                itemLookup.setRequest(request);
            }
            return servicePort.itemLookup(itemLookup);
        } catch (Throwable t){
            logger.error("Could not get response from amazon, message: "+t.getMessage(), t);
        }
        return null;
    }


    public List<String> lookupCategories(String itemIsbn){
        List<String> catList = new ArrayList<String>();
        try {
            EngineConfiguration serviceConfig = new FileProvider("x509.wsdd");
            AWSECommerceService apd = new AWSECommerceServiceLocator(serviceConfig);
            AWSECommerceServicePortType APD_PORT = apd.getAWSECommerceServicePort(PORT_ADDRESS);

            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{itemIsbn};
            request[0].setItemId(items);
            String[] rg = new String[]{BROWSE_NODES};
            request[0].setResponseGroup(rg);
            itemLookup.setRequest(request);
            ItemLookupResponse resp = APD_PORT.itemLookup(itemLookup);

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

                                    for (Integer key : nodeMap.keySet()){
                                        List<String> cats = nodeMap.get(key);
                                        if (cats.size() > 1 && !cats.contains("Stores")){
                                            Collections.reverse(cats);
                                            for (String cat : cats){
                                                if (!cat.equals("Subjects") && !cat.equals("Books") && !catList.contains(cat)){
                                                    catList.add(cat);
                                                    if (catList.size() == 4) return catList;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Could not find categories at amazon for: "+itemIsbn);
            }

        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+itemIsbn+" message: "+t.getMessage());
        }
        return catList;
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
    
    /**
     * TODO looking into what we need to pull in so we can get the first two levels of subjects in the inventory item data 
     */
    /* subject calls are no longer supported by amazon!!!!
    public List<String> lookupSubjects(String itemIsbn){
        List<String> subjectsList = new ArrayList<String>();
        try {
            EngineConfiguration serviceConfig = new FileProvider("x509.wsdd");
            AWSECommerceService apd = new AWSECommerceServiceLocator(serviceConfig);
            AWSECommerceServicePortType APD_PORT = apd.getAWSECommerceServicePort(PORT_ADDRESS);

            ItemLookup itemLookup = new ItemLookup();

            //Setting common parameters
            itemLookup.setAWSAccessKeyId(ACCESS_KEY_ID);
            itemLookup.setAssociateTag(ASSOCIATE_TAG);

            //Setting query specific parameters
            ItemLookupRequest[] request = new ItemLookupRequest[1];
            request[0] = new ItemLookupRequest();
            String[] items = new String[]{itemIsbn};
            request[0].setItemId(items);
            String[] rg = new String[]{SUBJECTS};
            request[0].setResponseGroup(rg);
            itemLookup.setRequest(request);
            ItemLookupResponse resp = APD_PORT.itemLookup(itemLookup);


            if (resp != null && resp.getItems() != null){
                for (Items respItems : resp.getItems()){
                    if (respItems.getItem() != null){
                        for (Item respItem : respItems.getItem()){
                            String isbn = respItem.getASIN();
                            if (respItem.getSubjects() != null){
                                String[] subjects = respItem.getSubjects();
                                for (String subject : subjects){
                                    subjectsList.add(subject);
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Could not find subjects at amazon for: "+itemIsbn);
            }

        } catch (Throwable t){
            logger.error("Could not get response from amazon, isbn: "+itemIsbn+" message: "+t.getMessage());
        }
        return subjectsList;
    }
    */
    
    public static void main(String[] args){
        AmazonData amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData("9780812973990");
        System.out.println(amazonData.debugString());
    }
}
