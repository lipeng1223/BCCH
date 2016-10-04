package com.bc.amazon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazon.xml.AWSECommerceService.ErrorsError;
import com.amazon.xml.AWSECommerceService.Item;
import com.amazon.xml.AWSECommerceService.ItemAttributes;
import com.amazon.xml.AWSECommerceService.ItemLookupResponse;
import com.amazon.xml.AWSECommerceService.OfferSummary;
import com.google.gson.Gson;


public class AmazonData {

    private static final Logger logger = Logger.getLogger(AmazonData.class);
    
    private Boolean dataLoaded = false;
    private Boolean notFound = false;
    private Date checkTime = Calendar.getInstance().getTime();

    /* Medium response */
    private String detailPage;
    private String salesRank;
    private String asin;
    
    private String[] authors;
    private String authorString;
    private String binding;
    private String ean;
    private String isbn;
    private String listPrice;
    private Integer numberOfPages;
    private Float height;
    private Float length;
    private Float width;
    private Float weight;
    private String productGroup;
    private String publicationDate;
    private String publisher;
    private String title;
    
    private String smallImageUrl;
    private Float smallImageHeight;
    private Float smallImageWidth;
    private String mediumImageUrl;
    private Float mediumImageHeight;
    private Float mediumImageWidth;
    private String largeImageUrl;
    private Float largeImageHeight;
    private Float largeImageWidth;
    
    private BigInteger lowestNewPriceAmmount;
    private String lowestNewPriceCurrencyCode;
    private String lowestNewPriceFormatted = "$0";
    private BigInteger lowestUsedPriceAmmount;
    private String lowestUsedPriceCurrencyCode;
    private String lowestUsedPriceFormatted = "$0";
    private BigInteger lowestCollectiblePriceAmmount;
    private String lowestCollectiblePriceCurrencyCode;
    private String lowestCollectiblePriceFormatted = "$0";
    private BigInteger lowestRefurbishedPriceAmmount;
    private String lowestRefurbishedPriceCurrencyCode;
    private String lowestRefurbishedPriceFormatted = "$0";
    private String totalNew = "0";
    private String totalUsed = "0";
    private String totalCollectible = "0";
    private String totalRefurbished = "0";
    /* End Medium response */
    
    // categories
    private List<String> categories = new ArrayList<String>();

    public AmazonData(ItemLookupResponse response) {
        loadData(response);
    }
    
    public void loadData(ItemLookupResponse response) {
        if (response == null) {
            logger.error("Response is null");
            return;
        }
        try {
            if (response.getItems() != null && response.getItems(0) != null) {
                if (response.getItems(0).getItem() == null){
                    for (ErrorsError ee : response.getItems(0).getRequest().getErrors()){
                        if ("AWS.InvalidParameterValue".equals(ee.getCode())){
                            // not in amazon
                            notFound = true;
                            break;
                        }
                        logger.error("ErrorsError code: "+ee.getCode());
                        logger.error("ErrorsError message: "+ee.getMessage());
                    }
                    return;
                }
            }
            if (response.getItems() != null && response.getItems(0) != null && response.getItems(0).getItem() != null && response.getItems(0).getItem(0) != null){
                Item item = response.getItems(0).getItem(0);
                
                asin = item.getASIN();
                detailPage = item.getDetailPageURL();
                salesRank = item.getSalesRank();
                
                OfferSummary os = item.getOfferSummary();
                if (os != null){
                    totalNew = os.getTotalNew();
                    totalUsed = os.getTotalUsed();
                    totalCollectible = os.getTotalCollectible();
                    totalRefurbished = os.getTotalRefurbished();
                    if (os.getLowestNewPrice() != null){
                        lowestNewPriceAmmount = os.getLowestNewPrice().getAmount();
                        lowestNewPriceCurrencyCode = os.getLowestNewPrice().getCurrencyCode();
                        lowestNewPriceFormatted = os.getLowestNewPrice().getFormattedPrice();
                    }
                    if (os.getLowestUsedPrice() != null){
                        lowestUsedPriceAmmount = os.getLowestUsedPrice().getAmount();
                        lowestUsedPriceCurrencyCode = os.getLowestUsedPrice().getCurrencyCode();
                        lowestUsedPriceFormatted = os.getLowestUsedPrice().getFormattedPrice();
                    }
                    if (os.getLowestCollectiblePrice() != null){
                        lowestCollectiblePriceAmmount = os.getLowestCollectiblePrice().getAmount();
                        lowestCollectiblePriceCurrencyCode = os.getLowestCollectiblePrice().getCurrencyCode();
                        lowestCollectiblePriceFormatted = os.getLowestCollectiblePrice().getFormattedPrice();
                    }
                    if (os.getLowestRefurbishedPrice() != null){
                        lowestRefurbishedPriceAmmount = os.getLowestRefurbishedPrice().getAmount();
                        lowestRefurbishedPriceCurrencyCode = os.getLowestRefurbishedPrice().getCurrencyCode();
                        lowestRefurbishedPriceFormatted = os.getLowestRefurbishedPrice().getFormattedPrice();
                    }
                }
                ItemAttributes ia = item.getItemAttributes();
                if (ia != null){
                    StringBuilder sb = new StringBuilder();
                    if (ia.getAuthor() != null){
                        authors = ia.getAuthor();
                        for (String a : ia.getAuthor()){
                            if (authorString != null){
                                // add a comma
                                sb.append(", ");
                            }
                            sb.append(a);
                        }
                        authorString = sb.toString();
                    }
                    binding = ia.getBinding();
                    ean = ia.getEAN();
                    isbn = ia.getISBN();
                    if (ia.getListPrice() != null){
                        listPrice = ia.getListPrice().getFormattedPrice();
                    }
                    if (ia.getNumberOfPages() != null) {
                        numberOfPages = ia.getNumberOfPages().intValue();
                    }
                    
                    if (ia.getItemDimensions() != null){
                        if (ia.getItemDimensions().getHeight() != null) {
                            height = ia.getItemDimensions().getHeight().get_value().floatValue() / 100F;
                            //logger.info("height units: "+ia.getItemDimensions().getHeight().getUnits());
                        }
                        if (ia.getItemDimensions().getLength() != null)
                            length = ia.getItemDimensions().getLength().get_value().floatValue() / 100F;
                        if (ia.getItemDimensions().getWidth() != null)
                            width = ia.getItemDimensions().getWidth().get_value().floatValue() / 100F;
                        if (ia.getItemDimensions().getWeight() != null)
                            weight = ia.getItemDimensions().getWeight().get_value().floatValue() / 100F;
                    } else if (ia.getPackageDimensions() != null){
                        if (ia.getPackageDimensions().getHeight() != null) {
                            height = ia.getPackageDimensions().getHeight().get_value().floatValue() / 100F;
                            //logger.info("height units: "+ia.getPackageDimensions().getHeight().getUnits());
                        }
                        if (ia.getPackageDimensions().getLength() != null)
                            length = ia.getPackageDimensions().getLength().get_value().floatValue() / 100F;
                        if (ia.getPackageDimensions().getWidth() != null)
                            width = ia.getPackageDimensions().getWidth().get_value().floatValue() / 100F;
                        if (ia.getPackageDimensions().getWeight() != null)
                            weight = ia.getPackageDimensions().getWeight().get_value().floatValue() / 100F;
                    }
                    productGroup = ia.getProductGroup();
                    publicationDate = ia.getPublicationDate();
                    publisher = ia.getPublisher();
                    title = ia.getTitle();
                }
                
                if (item.getSmallImage() != null){
                    smallImageUrl = item.getSmallImage().getURL();
                    //logger.error("small image exists: "+smallImageUrl);
                    if (item.getSmallImage().getHeight() != null)
                        smallImageHeight = item.getSmallImage().getHeight().get_value().floatValue();
                    if (item.getSmallImage().getWidth() != null)
                        smallImageWidth = item.getSmallImage().getWidth().get_value().floatValue();
                }
                if (item.getMediumImage() != null){
                    mediumImageUrl = item.getMediumImage().getURL();
                    //logger.error("medium image exists: "+mediumImageUrl);
                    if (item.getMediumImage().getHeight() != null)
                        mediumImageHeight = item.getMediumImage().getHeight().get_value().floatValue();
                    if (item.getMediumImage().getWidth() != null)
                        mediumImageWidth = item.getMediumImage().getWidth().get_value().floatValue();
                }
                if (item.getLargeImage() != null){
                    largeImageUrl = item.getLargeImage().getURL();
                    //logger.error("large image exists: "+largeImageUrl);
                    if (item.getLargeImage().getHeight() != null)
                        largeImageHeight = item.getLargeImage().getHeight().get_value().floatValue();
                    if (item.getLargeImage().getWidth() != null)
                        largeImageWidth = item.getLargeImage().getWidth().get_value().floatValue();
                }
                
                dataLoaded = true;
            } else {
                logger.error("Response item is null");
            }
        } catch (Throwable t){
            logger.error("Could not load response data", t);
        }
    }
    
    
    public String getDetailPage() {
        return detailPage;
    }
    public void setDetailPage(String detailPage) {
        this.detailPage = detailPage;
    }
    public String getSalesRank() {
        return salesRank;
    }
    public void setSalesRank(String salesRank) {
        this.salesRank = salesRank;
    }
    public String getAsin() {
        return asin;
    }
    public void setAsin(String asin) {
        this.asin = asin;
    }
    public String getBinding() {
        return binding;
    }
    public void setBinding(String binding) {
        this.binding = binding;
    }
    public String getEan() {
        return ean;
    }
    public void setEan(String ean) {
        this.ean = ean;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getListPrice() {
        return listPrice;
    }
    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }
    public Integer getNumberOfPages() {
        return numberOfPages;
    }
    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
    public Float getHeight() {
        return height;
    }
    public void setHeight(Float height) {
        this.height = height;
    }
    public Float getLength() {
        return length;
    }
    public void setLength(Float length) {
        this.length = length;
    }
    public Float getWidth() {
        return width;
    }
    public void setWidth(Float width) {
        this.width = width;
    }
    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    public String getProductGroup() {
        return productGroup;
    }
    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }
    public String getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSmallImageUrl() {
        return smallImageUrl;
    }
    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }
    public Float getSmallImageHeight() {
        return smallImageHeight;
    }
    public void setSmallImageHeight(Float smallImageHeight) {
        this.smallImageHeight = smallImageHeight;
    }
    public Float getSmallImageWidth() {
        return smallImageWidth;
    }
    public void setSmallImageWidth(Float smallImageWidth) {
        this.smallImageWidth = smallImageWidth;
    }
    public String getMediumImageUrl() {
        return mediumImageUrl;
    }
    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }
    public Float getMediumImageHeight() {
        return mediumImageHeight;
    }
    public void setMediumImageHeight(Float mediumImageHeight) {
        this.mediumImageHeight = mediumImageHeight;
    }
    public Float getMediumImageWidth() {
        return mediumImageWidth;
    }
    public void setMediumImageWidth(Float mediumImageWidth) {
        this.mediumImageWidth = mediumImageWidth;
    }
    public String getLargeImageUrl() {
        return largeImageUrl;
    }
    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }
    public Float getLargeImageHeight() {
        return largeImageHeight;
    }
    public void setLargeImageHeight(Float largeImageHeight) {
        this.largeImageHeight = largeImageHeight;
    }
    public Float getLargeImageWidth() {
        return largeImageWidth;
    }
    public void setLargeImageWidth(Float largeImageWidth) {
        this.largeImageWidth = largeImageWidth;
    }
    public BigInteger getLowestNewPriceAmmount() {
        return lowestNewPriceAmmount;
    }
    public void setLowestNewPriceAmmount(BigInteger lowestNewPriceAmmount) {
        this.lowestNewPriceAmmount = lowestNewPriceAmmount;
    }
    public String getLowestNewPriceCurrencyCode() {
        return lowestNewPriceCurrencyCode;
    }
    public void setLowestNewPriceCurrencyCode(String lowestNewPriceCurrencyCode) {
        this.lowestNewPriceCurrencyCode = lowestNewPriceCurrencyCode;
    }
    public String getLowestNewPriceFormatted() {
        return lowestNewPriceFormatted;
    }
    public void setLowestNewPriceFormatted(String lowestNewPriceFormatted) {
        this.lowestNewPriceFormatted = lowestNewPriceFormatted;
    }
    public BigInteger getLowestUsedPriceAmmount() {
        return lowestUsedPriceAmmount;
    }
    public void setLowestUsedPriceAmmount(BigInteger lowestUsedPriceAmmount) {
        this.lowestUsedPriceAmmount = lowestUsedPriceAmmount;
    }
    public String getLowestUsedPriceCurrencyCode() {
        return lowestUsedPriceCurrencyCode;
    }
    public void setLowestUsedPriceCurrencyCode(String lowestUsedPriceCurrencyCode) {
        this.lowestUsedPriceCurrencyCode = lowestUsedPriceCurrencyCode;
    }
    public String getLowestUsedPriceFormatted() {
        return lowestUsedPriceFormatted;
    }
    public void setLowestUsedPriceFormatted(String lowestUsedPriceFormatted) {
        this.lowestUsedPriceFormatted = lowestUsedPriceFormatted;
    }
    public BigInteger getLowestCollectiblePriceAmmount() {
        return lowestCollectiblePriceAmmount;
    }
    public void setLowestCollectiblePriceAmmount(
            BigInteger lowestCollectiblePriceAmmount) {
        this.lowestCollectiblePriceAmmount = lowestCollectiblePriceAmmount;
    }
    public String getLowestCollectiblePriceCurrencyCode() {
        return lowestCollectiblePriceCurrencyCode;
    }
    public void setLowestCollectiblePriceCurrencyCode(
            String lowestCollectiblePriceCurrencyCode) {
        this.lowestCollectiblePriceCurrencyCode = lowestCollectiblePriceCurrencyCode;
    }
    public String getLowestCollectiblePriceFormatted() {
        return lowestCollectiblePriceFormatted;
    }
    public void setLowestCollectiblePriceFormatted(
            String lowestCollectiblePriceFormatted) {
        this.lowestCollectiblePriceFormatted = lowestCollectiblePriceFormatted;
    }
    public BigInteger getLowestRefurbishedPriceAmmount() {
        return lowestRefurbishedPriceAmmount;
    }
    public void setLowestRefurbishedPriceAmmount(
            BigInteger lowestRefurbishedPriceAmmount) {
        this.lowestRefurbishedPriceAmmount = lowestRefurbishedPriceAmmount;
    }
    public String getLowestRefurbishedPriceCurrencyCode() {
        return lowestRefurbishedPriceCurrencyCode;
    }
    public void setLowestRefurbishedPriceCurrencyCode(
            String lowestRefurbishedPriceCurrencyCode) {
        this.lowestRefurbishedPriceCurrencyCode = lowestRefurbishedPriceCurrencyCode;
    }
    public String getLowestRefurbishedPriceFormatted() {
        return lowestRefurbishedPriceFormatted;
    }
    public void setLowestRefurbishedPriceFormatted(
            String lowestRefurbishedPriceFormatted) {
        this.lowestRefurbishedPriceFormatted = lowestRefurbishedPriceFormatted;
    }
    public String getTotalNew() {
        return totalNew;
    }
    public void setTotalNew(String totalNew) {
        this.totalNew = totalNew;
    }
    public String getTotalUsed() {
        return totalUsed;
    }
    public void setTotalUsed(String totalUsed) {
        this.totalUsed = totalUsed;
    }
    public String getTotalCollectible() {
        return totalCollectible;
    }
    public void setTotalCollectible(String totalCollectible) {
        this.totalCollectible = totalCollectible;
    }
    public String getTotalRefurbished() {
        return totalRefurbished;
    }
    public void setTotalRefurbished(String totalRefurbished) {
        this.totalRefurbished = totalRefurbished;
    }
    public Boolean getDataLoaded() {
        return dataLoaded;
    }
    public void setDataLoaded(Boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }
    public String[] getAuthors() {
        return authors;
    }
    public void setAuthors(String[] authors) {
        this.authors = authors;
    }
    public String getAuthorString() {
        return authorString;
    }
    public void setAuthorString(String authorString) {
        this.authorString = authorString;
    }
    
    public String debugString(){
        StringBuilder sb = new StringBuilder("Amazon Data");
        
        sb.append("\n");
        sb.append("dataLoaded: ");
        sb.append(dataLoaded);
        sb.append("\n");
        sb.append("asin: ");
        sb.append(asin);
        sb.append("\n");
        sb.append("detailPage: ");
        sb.append(detailPage);
        sb.append("\n");
        sb.append("salesRank: ");
        sb.append(salesRank);
        sb.append("\n");
        sb.append("authorString: ");
        sb.append(authorString);
        sb.append("\n");
        sb.append("binding: ");
        sb.append(binding);
        sb.append("\n");
        sb.append("ean: ");
        sb.append(ean);
        sb.append("\n");
        sb.append("isbn: ");
        sb.append(isbn);
        sb.append("\n");
        sb.append("listPrice: ");
        sb.append(listPrice);
        sb.append("\n");
        sb.append("numberOfPages: ");
        sb.append(numberOfPages);
        sb.append("\n");
        sb.append("height: ");
        sb.append(height);
        sb.append("\n");
        sb.append("length: ");
        sb.append(length);
        sb.append("\n");
        sb.append("width: ");
        sb.append(width);
        sb.append("\n");
        sb.append("weight: ");
        sb.append(weight);
        sb.append("\n");
        sb.append("productGroup: ");
        sb.append(productGroup);
        sb.append("\n");
        sb.append("publicationDate: ");
        sb.append(publicationDate);
        sb.append("\n");
        sb.append("publisher: ");
        sb.append(publisher);
        sb.append("\n");
        sb.append("title: ");
        sb.append(title);
        sb.append("\n");
        sb.append("smallImageUrl: ");
        sb.append(smallImageUrl);
        sb.append("\n");
        sb.append("smallImageHeight: ");
        sb.append(smallImageHeight);
        sb.append("\n");
        sb.append("smallImageWidth: ");
        sb.append(smallImageWidth);
        sb.append("\n");
        sb.append("mediumImageUrl: ");
        sb.append(mediumImageUrl);
        sb.append("\n");
        sb.append("mediumImageHeight: ");
        sb.append(mediumImageHeight);
        sb.append("\n");
        sb.append("mediumImageWidth: ");
        sb.append(mediumImageWidth);
        sb.append("\n");
        sb.append("largeImageUrl: ");
        sb.append(largeImageUrl);
        sb.append("\n");
        sb.append("largeImageHeight: ");
        sb.append(largeImageHeight);
        sb.append("\n");
        sb.append("largeImageWidth: ");
        sb.append(largeImageWidth);
        sb.append("\n");
        sb.append("lowestNewPriceAmmount: ");
        sb.append(lowestNewPriceAmmount);
        sb.append("\n");
        sb.append("lowestNewPriceCurrencyCode: ");
        sb.append(lowestNewPriceCurrencyCode);
        sb.append("\n");
        sb.append("lowestNewPriceFormatted: ");
        sb.append(lowestNewPriceFormatted);
        sb.append("\n");
        sb.append("lowestUsedPriceAmmount: ");
        sb.append(lowestUsedPriceAmmount);
        sb.append("\n");
        sb.append("lowestUsedPriceCurrencyCode: ");
        sb.append(lowestUsedPriceCurrencyCode);
        sb.append("\n");
        sb.append("lowestUsedPriceFormatted: ");
        sb.append(lowestUsedPriceFormatted);
        sb.append("\n");
        sb.append("lowestCollectiblePriceAmmount: ");
        sb.append(lowestCollectiblePriceAmmount);
        sb.append("\n");
        sb.append("lowestCollectiblePriceCurrencyCode: ");
        sb.append(lowestCollectiblePriceCurrencyCode);
        sb.append("\n");
        sb.append("lowestCollectiblePriceFormatted: ");
        sb.append(lowestCollectiblePriceFormatted);
        sb.append("\n");
        sb.append("lowestRefurbishedPriceAmmount: ");
        sb.append(lowestRefurbishedPriceAmmount);
        sb.append("\n");
        sb.append("lowestRefurbishedPriceCurrencyCode: ");
        sb.append(lowestRefurbishedPriceCurrencyCode);
        sb.append("\n");
        sb.append("lowestRefurbishedPriceFormatted: ");
        sb.append(lowestRefurbishedPriceFormatted);
        sb.append("\n");
        sb.append("totalNew: ");
        sb.append(totalNew);
        sb.append("\n");
        sb.append("totalUsed: ");
        sb.append(totalUsed);
        sb.append("\n");
        sb.append("totalCollectible: ");
        sb.append(totalCollectible);
        sb.append("\n");
        sb.append("totalRefurbished: ");
        sb.append(totalRefurbished);
        sb.append("\n");
        
        return sb.toString();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Boolean getNotFound() {
        return notFound;
    }

    public void setNotFound(Boolean notFound) {
        this.notFound = notFound;
    }
    
    public String getEntityJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
