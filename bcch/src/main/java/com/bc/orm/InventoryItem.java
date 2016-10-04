package com.bc.orm;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.validator.routines.ISBNValidator;
import org.hibernate.annotations.Cascade;

import com.google.gson.annotations.Expose;


@SuppressWarnings("serial")
@Entity
@Table(name="inventory_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class InventoryItem extends BaseEntity implements Auditable, Serializable {

    private Integer parentId;
    @Expose private Integer committed = 0;
    private Boolean deleted;
    @Expose private Boolean bellbook = false;
    private Float receivedDiscount;
    @Expose private String companyRec;
    @Expose private String imprintRec;
    @Expose private String isbn;
    @Expose private String title = "";
    @Expose private String author;
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    @Expose private String publisher;
    @Expose private Float listPrice;
    @Expose private Float sellingPrice;
    @Expose private Float sellPricePercentList;
    @Expose private Date receivedDate;
    @Expose private Integer receivedQuantity;
    @Expose private String bin;
    private String biblio;
    @Expose private String comment;
    @Expose private String cover;
    private Date publishDate;
    private Float dbfRecPrice1;
    private Float dbfRecPrice2;
    @Expose private Float receivedPrice;
    @Expose private Integer onhand = 0;
    private Integer quantity;
    @Expose private Integer available = 0;
    private Integer onorder;
    private Integer sporder;
    private Integer backorder;
    private Integer aveSaletime;
    private String lastpo;
    private Date lastpoDate;
    @Expose private Boolean skid = false;
    private String breakroomisbn;
    @Expose private Integer salesRank;
    @Expose private String isbn13;
    @Expose private String cond;
    private String breakRoomCondition;
    private String bccategory;
    @Expose private Boolean restricted = false;
    private Boolean pendingReceiving;
    @Expose private String isbn10;
    @Expose private Boolean he = false;
    private Set<CustomerOrderItem> customerOrderItems = new HashSet<CustomerOrderItem>(0);
    private Set<ReceivedItem> receivedItems = new HashSet<ReceivedItem>(0);
    @Expose private Boolean backStock = false;

    @Expose private Integer numberOfPages;
    @Expose private Float length; // inches
    @Expose private Float width; // inches
    @Expose private Float height; // inches
    @Expose private Float weight; // lbs
    private Date lastAmazonUpdate;
    private String smallImage;
    private String mediumImage;
    
    private Integer skidId;
    @Expose private Float skidPiecePrice;
    @Expose private Float skidPieceCost;
    @Expose private Integer skidPieceCount;

    @Expose private Integer nightlyAmazonTotalNew = 0;
    @Expose private Integer nightlyAmazonTotalUsed = 0;
    @Expose private Integer nightlyAmazonTotalCollectible = 0;
    @Expose private Float nightlyAmazonLowestNewPrice = 0F;
    @Expose private Float nightlyAmazonLowestUsedPrice = 0F;
    @Expose private Float nightlyAmazonLowestCollectiblePrice = 0F;
    
    // Transient amazon data
    private String amazonTotalNew = "0";
    private String amazonTotalUsed = "0";
    private String amazonTotalCollectible = "0";
    private String amazonLowestNewPrice = "0.00";
    private String amazonLowestUsedPrice = "0.00";
    private String amazonLowestCollectiblePrice = "0.00";
    private Boolean amazonDataLoaded = false;
       
    public InventoryItem() {
    }
    
    @Transient
    public String getAuditMessage(){
        return "isbn: "+isbn+" condition: "+cond;
    }

    @Column(name="skid_id")
    public Integer getSkidId() {
        return this.skidId;
    }
    
    public void setSkidId(Integer skidId) {
        this.skidId = skidId;
    }
    
    @Column(name="parent_id")
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    @Column(name="category3", length=250)
    public String getCategory3() {
        return this.category3;
    }
    
    public void setCategory3(String category3) {
        this.category3 = category3;
    }
    
    @Column(name="category4", length=250)
    public String getCategory4() {
        return this.category4;
    }
    
    public void setCategory4(String category4) {
        this.category4 = category4;
    }
    
    @Column(name="commited")
    public Integer getCommitted() {
        return this.committed;
    }
    
    public void setCommitted(Integer committed) {
        this.committed = committed;
    }
    
    @Column(name="deleted")
    public Boolean getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="bellbook")
    public Boolean getBellbook() {
        return this.bellbook;
    }
    
    public void setBellbook(Boolean bellbook) {
        this.bellbook = bellbook;
    }
    
    @Column(name="received_discount", precision=10)
    public Float getReceivedDiscount() {
        return this.receivedDiscount;
    }
    
    public void setReceivedDiscount(Float receivedDiscount) {
        this.receivedDiscount = receivedDiscount;
    }
    
    @Column(name="companyRec", length=120)
    public String getCompanyRec() {
        return this.companyRec;
    }
    
    public void setCompanyRec(String companyRec) {
        this.companyRec = companyRec;
    }
    
    @Column(name="imprintRec", length=120)
    public String getImprintRec() {
        return this.imprintRec;
    }
    
    public void setImprintRec(String imprintRec) {
        this.imprintRec = imprintRec;
    }
    @Column(name="isbn", length=50)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        if (this.title != null && this.title.length() > 255){
            this.title = this.title.substring(0, 255);
        }
    }
    
    @Column(name="author")
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Column(name="category1", length=250)
    public String getCategory1() {
        return this.category1;
    }
    
    public void setCategory1(String category1) {
        this.category1 = category1;
    }
    
    @Column(name="category2", length=250)
    public String getCategory2() {
        return this.category2;
    }
    
    public void setCategory2(String category2) {
        this.category2 = category2;
    }
    
    @Column(name="publisher", length=100)
    public String getPublisher() {
        return this.publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    @Column(name="list_price", precision=10)
    public Float getListPrice() {
        return this.listPrice;
    }
    
    public void setListPrice(Float listPrice) {
        this.listPrice = listPrice;
        if (this.listPrice != null && this.sellingPrice != null && this.listPrice > 0 && this.sellingPrice > 0){
            setSellPricePercentList((this.sellingPrice / this.listPrice) * 100);
        }
    }
    
    @Column(name="selling_price", precision=10)
    public Float getSellingPrice() {
        return this.sellingPrice;
    }
    
    public void setSellingPrice(Float sellingPrice) {
        this.sellingPrice = sellingPrice;
        if (this.listPrice != null && this.sellingPrice != null && this.listPrice > 0 && this.sellingPrice > 0){
            setSellPricePercentList((this.sellingPrice / this.listPrice) * 100);
        }
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="received_date", length=19)
    public Date getReceivedDate() {
        return this.receivedDate;
    }
    
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
    
    @Column(name="received_quantity")
    public Integer getReceivedQuantity() {
        return this.receivedQuantity;
    }
    
    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
    
    @Column(name="bin", length=64)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        this.bin = bin;
    }
    
    @Column(name="biblio", length=10)
    public String getBiblio() {
        return this.biblio;
    }
    
    public void setBiblio(String biblio) {
        this.biblio = biblio;
    }
    
    @Column(name="comment", length=128)
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="cover", length=10)
    public String getCover() {
        return this.cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="publish_date", length=19)
    public Date getPublishDate() {
        return this.publishDate;
    }
    
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
    
    @Column(name="dbf_rec_price1", precision=10)
    public Float getDbfRecPrice1() {
        return this.dbfRecPrice1;
    }
    
    public void setDbfRecPrice1(Float dbfRecPrice1) {
        this.dbfRecPrice1 = dbfRecPrice1;
    }
    
    @Column(name="dbf_rec_price2", precision=10)
    public Float getDbfRecPrice2() {
        return this.dbfRecPrice2;
    }
    
    public void setDbfRecPrice2(Float dbfRecPrice2) {
        this.dbfRecPrice2 = dbfRecPrice2;
    }
    
    @Column(name="received_price", precision=10)
    public Float getReceivedPrice() {
        return this.receivedPrice;
    }
    
    public void setReceivedPrice(Float receivedPrice) {
        this.receivedPrice = receivedPrice;
    }
    
    @Column(name="onhand")
    public Integer getOnhand() {
        return this.onhand;
    }
    
    public void setOnhand(Integer onhand) {
        this.onhand = onhand;
    }
    
    @Column(name="quantity")
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Column(name="available")
    public Integer getAvailable() {
        return this.available;
    }
    
    public void setAvailable(Integer available) {
        this.available = available;
    }
    
    @Column(name="onorder")
    public Integer getOnorder() {
        return this.onorder;
    }
    
    public void setOnorder(Integer onorder) {
        this.onorder = onorder;
    }
    
    @Column(name="sporder")
    public Integer getSporder() {
        return this.sporder;
    }
    
    public void setSporder(Integer sporder) {
        this.sporder = sporder;
    }
    
    @Column(name="backorder")
    public Integer getBackorder() {
        return this.backorder;
    }
    
    public void setBackorder(Integer backorder) {
        this.backorder = backorder;
    }
    
    @Column(name="ave_saletime")
    public Integer getAveSaletime() {
        return this.aveSaletime;
    }
    
    public void setAveSaletime(Integer aveSaletime) {
        this.aveSaletime = aveSaletime;
    }
    
    @Column(name="lastpo", length=128)
    public String getLastpo() {
        return this.lastpo;
    }
    
    public void setLastpo(String lastpo) {
        this.lastpo = lastpo;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastpo_date", length=19)
    public Date getLastpoDate() {
        return this.lastpoDate;
    }
    
    public void setLastpoDate(Date lastpoDate) {
        this.lastpoDate = lastpoDate;
    }
    
    @Column(name="skid")
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skid_piece_price", precision=10)
    public Float getSkidPiecePrice() {
        return this.skidPiecePrice;
    }
    
    public void setSkidPiecePrice(Float skidPiecePrice) {
        this.skidPiecePrice = skidPiecePrice;
    }
    
    @Column(name="skid_piece_cost", precision=10)
    public Float getSkidPieceCost() {
        return this.skidPieceCost;
    }
    
    public void setSkidPieceCost(Float skidPieceCost) {
        this.skidPieceCost = skidPieceCost;
    }
    
    @Column(name="skid_piece_count")
    public Integer getSkidPieceCount() {
        return this.skidPieceCount;
    }
    
    public void setSkidPieceCount(Integer skidPieceCount) {
        this.skidPieceCount = skidPieceCount;
    }
    
    @Column(name="breakroomisbn", length=50)
    public String getBreakroomIsbn() {
        return this.breakroomisbn;
    }
    
    public void setBreakroomIsbn(String breakroomisbn) {
        this.breakroomisbn = breakroomisbn;
    }
    
    @Column(name="salesRank")
    public Integer getSalesRank() {
        return this.salesRank;
    }
    
    public void setSalesRank(Integer salesRank) {
        this.salesRank = salesRank;
    }
    
    @Column(name="isbn13", length=13)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    @Column(name="cond", length=50)
    public String getCond() {
        return this.cond;
    }
    
    public void setCond(String cond) {
        this.cond = cond;
    }
    
    @Column(name="breakRoomCondition", length=50)
    public String getBreakRoomCondition() {
        return this.breakRoomCondition;
    }
    
    public void setBreakRoomCondition(String breakRoomCondition) {
        this.breakRoomCondition = breakRoomCondition;
    }
    
    @Column(name="bccategory")
    public String getBccategory() {
        return this.bccategory;
    }
    
    public void setBccategory(String bccategory) {
        this.bccategory = bccategory;
    }
    
    @Column(name="restricted")
    public Boolean getRestricted() {
        return this.restricted;
    }
    
    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }
    
    @Column(name="pendingReceiving")
    public Boolean getPendingReceiving() {
        return this.pendingReceiving;
    }
    
    public void setPendingReceiving(Boolean pendingReceiving) {
        this.pendingReceiving = pendingReceiving;
    }
    
    @Column(name="isbn10", length=10)
    public String getIsbn10() {
        return this.isbn10;
    }
    
    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }
    
    @Column(name="he")
    public Boolean getHe() {
        return this.he;
    }
    
    public void setHe(Boolean he) {
        this.he = he;
    }
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="inventoryItem", cascade=CascadeType.PERSIST)
    @Cascade(value={org.hibernate.annotations.CascadeType.PERSIST})
    public Set<CustomerOrderItem> getCustomerOrderItems() {
        return this.customerOrderItems;
    }
    
    public void setCustomerOrderItems(Set<CustomerOrderItem> customerOrderItems) {
        this.customerOrderItems = customerOrderItems;
    }

    @Transient
    public Boolean getIsValidIsbn(){
        return ISBNValidator.getInstance().isValid(isbn);
    }
    
    @Transient
    public Boolean getIsValidIsbn10(){
        return ISBNValidator.getInstance().isValid(isbn10);
    }
    
    @Transient
    public Boolean getIsValidIsbn13(){
        return ISBNValidator.getInstance().isValid(isbn13);
    }
    
    @Transient
    public String getValidIsbn(){
        if (isbn != null){
            return ISBNValidator.getInstance(true).validate(isbn);
        } else if (isbn13 != null){
            return ISBNValidator.getInstance(true).validate(isbn13);
        } else if (isbn10 != null){
            return ISBNValidator.getInstance(true).validate(isbn10);
        }
        return null;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
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

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Date getLastAmazonUpdate() {
        return lastAmazonUpdate;
    }

    public void setLastAmazonUpdate(Date lastAmazonUpdate) {
        this.lastAmazonUpdate = lastAmazonUpdate;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getMediumImage() {
        return mediumImage;
    }

    public void setMediumImage(String mediumImage) {
        this.mediumImage = mediumImage;
    }

    @Transient
    public Float getCost(){
        Float cost = 0F;
        if (receivedPrice != null) {
            if (receivedDiscount != null && receivedDiscount > 0){
                cost = new Float(receivedPrice-(receivedPrice*(receivedDiscount/100.0)));
            } else {
                cost = receivedPrice;
            }
        }
        return cost;
    }

    @Transient
    public Float getCostPercentList(){
        Float cost = getCost();
        if (cost != 0F && listPrice != null && listPrice != 0F){
            return (cost/listPrice) * 100F;
        }
        return 0F;
    }
    
    @Transient
    public String getAmazonTotalNew() {
        return amazonTotalNew;
    }

    public void setAmazonTotalNew(String amazonTotalNew) {
        this.amazonTotalNew = amazonTotalNew;
    }

    @Transient
    public String getAmazonTotalUsed() {
        return amazonTotalUsed;
    }

    public void setAmazonTotalUsed(String amazonTotalUsed) {
        this.amazonTotalUsed = amazonTotalUsed;
    }

    @Transient
    public String getAmazonTotalCollectible() {
        return amazonTotalCollectible;
    }

    public void setAmazonTotalCollectible(String amazonTotalCollectible) {
        this.amazonTotalCollectible = amazonTotalCollectible;
    }

    @Transient
    public String getAmazonLowestNewPrice() {
        return amazonLowestNewPrice;
    }

    public void setAmazonLowestNewPrice(String amazonLowestNewPrice) {
        this.amazonLowestNewPrice = amazonLowestNewPrice;
    }

    @Transient
    public String getAmazonLowestUsedPrice() {
        return amazonLowestUsedPrice;
    }

    public void setAmazonLowestUsedPrice(String amazonLowestUsedPrice) {
        this.amazonLowestUsedPrice = amazonLowestUsedPrice;
    }

    @Transient
    public String getAmazonLowestCollectiblePrice() {
        return amazonLowestCollectiblePrice;
    }

    public void setAmazonLowestCollectiblePrice(String amazonLowestCollectiblePrice) {
        this.amazonLowestCollectiblePrice = amazonLowestCollectiblePrice;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="inventoryItem")
    public Set<ReceivedItem> getReceivedItems() {
        return receivedItems;
    }

    public void setReceivedItems(Set<ReceivedItem> receivedItems) {
        this.receivedItems = receivedItems;
    }

    @Transient
    public String getBlank1(){
        return "";
    }
    
    @Transient
    public String getBlank2(){
        return "";
    }

    public Float getSellPricePercentList() {
        return sellPricePercentList;
    }

    public void setSellPricePercentList(Float sellPricePercentList) {
        this.sellPricePercentList = sellPricePercentList;
    }

    @Transient
    public Boolean getAmazonDataLoaded() {
        return amazonDataLoaded;
    }

    public void setAmazonDataLoaded(Boolean amazonDataLoaded) {
        this.amazonDataLoaded = amazonDataLoaded;
    }

    @Transient
    public String getAmazonLink(){
        StringBuffer link = new StringBuffer();
        link.append("http://www.amazon.com/exec/obidos/ASIN/");
        link.append(getIsbn());
        return link.toString();
    }

    public Float getNightlyAmazonLowestCollectiblePrice() {
        return nightlyAmazonLowestCollectiblePrice;
    }

    public void setNightlyAmazonLowestCollectiblePrice(Float nightlyAmazonLowestCollectiblePrice) {
        this.nightlyAmazonLowestCollectiblePrice = nightlyAmazonLowestCollectiblePrice;
    }

    public Float getNightlyAmazonLowestNewPrice() {
        return nightlyAmazonLowestNewPrice;
    }

    public void setNightlyAmazonLowestNewPrice(Float nightlyAmazonLowestNewPrice) {
        this.nightlyAmazonLowestNewPrice = nightlyAmazonLowestNewPrice;
    }

    public Float getNightlyAmazonLowestUsedPrice() {
        return nightlyAmazonLowestUsedPrice;
    }

    public void setNightlyAmazonLowestUsedPrice(Float nightlyAmazonLowestUsedPrice) {
        this.nightlyAmazonLowestUsedPrice = nightlyAmazonLowestUsedPrice;
    }

    public Integer getNightlyAmazonTotalCollectible() {
        return nightlyAmazonTotalCollectible;
    }

    public void setNightlyAmazonTotalCollectible(Integer nightlyAmazonTotalCollectible) {
        this.nightlyAmazonTotalCollectible = nightlyAmazonTotalCollectible;
    }

    public Integer getNightlyAmazonTotalNew() {
        return nightlyAmazonTotalNew;
    }

    public void setNightlyAmazonTotalNew(Integer nightlyAmazonTotalNew) {
        this.nightlyAmazonTotalNew = nightlyAmazonTotalNew;
    }

    public Integer getNightlyAmazonTotalUsed() {
        return nightlyAmazonTotalUsed;
    }

    public void setNightlyAmazonTotalUsed(Integer nightlyAmazonTotalUsed) {
        this.nightlyAmazonTotalUsed = nightlyAmazonTotalUsed;
    }

    public Boolean getBackStock() {
        return backStock;
    }

    public void setBackStock(Boolean backStock) {
        this.backStock = backStock;
    }
    
}


