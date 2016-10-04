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

import com.bc.util.IsbnUtil;
import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_inventory")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellInventory extends BaseEntity implements Auditable, Serializable {

    private Date lastRecDate;
    private Float lastRecPrice = 0F;
    private Float lastRecQuantity = 0F;
    private String lastPo;
    private Date lastPoDate;
    @Expose private String cover;
    @Expose private Float sellPrice = 0F;
    @Expose private Float listPrice = 0F;
    @Expose private Float cost = 0F;
    @Expose private String bin;
    @Expose private String isbn;
    @Expose private String title;
    @Expose private String publisher;
    @Expose private String author;
    @Expose private String sku;
    private String amazonPage;
    private String amazonImage;
    private String bellcomment;
    private Integer bellcondition;
    private Boolean bellBook = false;
    private Boolean skid;
    private Integer skidPieces;
    @Expose private Integer onhand = 0;
    private Integer salesrank;
    private Float skidPiecePrice = 0F;
    private Float skidPieceCost = 0F;
    private Integer listed = 0;
    private Date lastListDate;
    private Float lowPrice = 0F;
    private Date lastAmzCheck;
    private Boolean noamazon;
    private Float lowNew = 0F;
    private Float lowUsed = 0F;
    private Float lowCollectible = 0F;
    private Float lowRefurb = 0F;
    private String lowNewFormat;
    private String lowUsedFormat;
    private String lowCollectibleFormat;
    private String lowRefurbFormat;
    private Integer priceAdjust;
    private Integer totalNew = 0;
    private Integer totalUsed = 0;
    private Integer totalCollectible = 0;
    private Integer totalRefurb = 0;
    private String category;
    @Expose private Integer committed = 0;
    @Expose private Integer available = 0;
    private Float receivedPrice = 0F;
    private Float receivedDiscount = 0F;
    private Float weight;
    private Float costPerPound;
    @Expose private String isbn13;
    private Set<BellSku> bellSkus = new HashSet<BellSku>(0);
    private Set<BellOrderItem> bellOrderItems = new HashSet<BellOrderItem>(0);
    
    public BellInventory() {
    }
    
    @Transient
    public String getAuditMessage(){
        return "isbn: "+isbn;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_rec_date", length=19)
    public Date getLastRecDate() {
        return this.lastRecDate;
    }
    
    public void setLastRecDate(Date lastRecDate) {
        this.lastRecDate = lastRecDate;
    }
    
    @Column(name="last_rec_price", precision=10)
    public Float getLastRecPrice() {
        return this.lastRecPrice;
    }
    
    public void setLastRecPrice(Float lastRecPrice) {
        this.lastRecPrice = lastRecPrice;
    }
    
    @Column(name="last_rec_quantity", precision=12, scale=0)
    public Float getLastRecQuantity() {
        return this.lastRecQuantity;
    }
    
    public void setLastRecQuantity(Float lastRecQuantity) {
        this.lastRecQuantity = lastRecQuantity;
    }
    
    @Column(name="last_po", length=64)
    public String getLastPo() {
        return this.lastPo;
    }
    
    public void setLastPo(String lastPo) {
        this.lastPo = lastPo;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_po_date", length=19)
    public Date getLastPoDate() {
        return this.lastPoDate;
    }
    
    public void setLastPoDate(Date lastPoDate) {
        this.lastPoDate = lastPoDate;
    }
    
    @Column(name="cover", length=10)
    public String getCover() {
        return this.cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    @Column(name="sell_price", precision=10)
    public Float getSellPrice() {
        return this.sellPrice;
    }
    
    public void setSellPrice(Float sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    @Column(name="list_price", precision=10)
    public Float getListPrice() {
        return this.listPrice;
    }
    
    public void setListPrice(Float listPrice) {
        this.listPrice = listPrice;
    }
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }
    
    @Column(name="bin", length=20)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        this.bin = bin;
    }
    
    @Column(name="isbn", unique=true, length=48)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Column(name="title", length=65535)
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Column(name="publisher", length=128)
    public String getPublisher() {
        return this.publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    @Column(name="author", length=65535)
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Column(name="sku", length=128)
    public String getSku() {
        return this.sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    @Column(name="amazonPage", length=65535)
    public String getAmazonPage() {
        return this.amazonPage;
    }
    
    public void setAmazonPage(String amazonPage) {
        this.amazonPage = amazonPage;
    }
    
    @Column(name="amazonImage", length=65535)
    public String getAmazonImage() {
        return this.amazonImage;
    }
    
    public void setAmazonImage(String amazonImage) {
        this.amazonImage = amazonImage;
    }
    
    @Column(name="bellcomment", length=128)
    public String getBellcomment() {
        return this.bellcomment;
    }
    
    public void setBellcomment(String bellcomment) {
        this.bellcomment = bellcomment;
    }
    
    @Column(name="bellcondition")
    public Integer getBellcondition() {
        return this.bellcondition;
    }
    
    public void setBellcondition(Integer bellcondition) {
        this.bellcondition = bellcondition;
    }
    
    @Column(name="bell_book")
    public Boolean getBellBook() {
        return this.bellBook;
    }
    
    public void setBellBook(Boolean bellBook) {
        this.bellBook = bellBook;
    }
    
    @Column(name="skid")
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skid_pieces")
    public Integer getSkidPieces() {
        return this.skidPieces;
    }
    
    public void setSkidPieces(Integer skidPieces) {
        this.skidPieces = skidPieces;
    }
    
    @Column(name="onhand")
    public Integer getOnhand() {
        return this.onhand;
    }
    
    public void setOnhand(Integer onhand) {
        this.onhand = onhand;
    }
    
    @Column(name="salesrank")
    public Integer getSalesrank() {
        return this.salesrank;
    }
    
    public void setSalesrank(Integer salesrank) {
        this.salesrank = salesrank;
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
    
    @Column(name="listed")
    public Integer getListed() {
        return this.listed;
    }
    
    public void setListed(Integer listed) {
        this.listed = listed;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_list_date", length=19)
    public Date getLastListDate() {
        return this.lastListDate;
    }
    
    public void setLastListDate(Date lastListDate) {
        this.lastListDate = lastListDate;
    }
    
    @Column(name="low_price", precision=12, scale=0)
    public Float getLowPrice() {
        return this.lowPrice;
    }
    
    public void setLowPrice(Float lowPrice) {
        this.lowPrice = lowPrice;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastAmzCheck", length=19)
    public Date getLastAmzCheck() {
        return this.lastAmzCheck;
    }
    
    public void setLastAmzCheck(Date lastAmzCheck) {
        this.lastAmzCheck = lastAmzCheck;
    }
    
    @Column(name="noamazon")
    public Boolean getNoamazon() {
        return this.noamazon;
    }
    
    public void setNoamazon(Boolean noamazon) {
        this.noamazon = noamazon;
    }
    
    @Column(name="lowNew", precision=12, scale=0)
    public Float getLowNew() {
        return this.lowNew;
    }
    
    public void setLowNew(Float lowNew) {
        this.lowNew = lowNew;
    }
    
    @Column(name="lowUsed", precision=12, scale=0)
    public Float getLowUsed() {
        return this.lowUsed;
    }
    
    public void setLowUsed(Float lowUsed) {
        this.lowUsed = lowUsed;
    }
    
    @Column(name="lowCollectible", precision=12, scale=0)
    public Float getLowCollectible() {
        return this.lowCollectible;
    }
    
    public void setLowCollectible(Float lowCollectible) {
        this.lowCollectible = lowCollectible;
    }
    
    @Column(name="lowRefurb", precision=12, scale=0)
    public Float getLowRefurb() {
        return this.lowRefurb;
    }
    
    public void setLowRefurb(Float lowRefurb) {
        this.lowRefurb = lowRefurb;
    }
    
    @Column(name="lowNewFormat", length=15)
    public String getLowNewFormat() {
        return this.lowNewFormat;
    }
    
    public void setLowNewFormat(String lowNewFormat) {
        this.lowNewFormat = lowNewFormat;
    }
    
    @Column(name="lowUsedFormat", length=15)
    public String getLowUsedFormat() {
        return this.lowUsedFormat;
    }
    
    public void setLowUsedFormat(String lowUsedFormat) {
        this.lowUsedFormat = lowUsedFormat;
    }
    
    @Column(name="lowCollectibleFormat", length=15)
    public String getLowCollectibleFormat() {
        return this.lowCollectibleFormat;
    }
    
    public void setLowCollectibleFormat(String lowCollectibleFormat) {
        this.lowCollectibleFormat = lowCollectibleFormat;
    }
    
    @Column(name="lowRefurbFormat", length=15)
    public String getLowRefurbFormat() {
        return this.lowRefurbFormat;
    }
    
    public void setLowRefurbFormat(String lowRefurbFormat) {
        this.lowRefurbFormat = lowRefurbFormat;
    }
    
    @Column(name="priceAdjust")
    public Integer getPriceAdjust() {
        return this.priceAdjust;
    }
    
    public void setPriceAdjust(Integer priceAdjust) {
        this.priceAdjust = priceAdjust;
    }
    
    @Column(name="totalNew")
    public Integer getTotalNew() {
        return this.totalNew;
    }
    
    public void setTotalNew(Integer totalNew) {
        this.totalNew = totalNew;
    }
    
    @Column(name="totalUsed")
    public Integer getTotalUsed() {
        return this.totalUsed;
    }
    
    public void setTotalUsed(Integer totalUsed) {
        this.totalUsed = totalUsed;
    }
    
    @Column(name="totalCollectible")
    public Integer getTotalCollectible() {
        return this.totalCollectible;
    }
    
    public void setTotalCollectible(Integer totalCollectible) {
        this.totalCollectible = totalCollectible;
    }
    
    @Column(name="totalRefurb")
    public Integer getTotalRefurb() {
        return this.totalRefurb;
    }
    
    public void setTotalRefurb(Integer totalRefurb) {
        this.totalRefurb = totalRefurb;
    }
    
    @Column(name="category", length=20)
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Column(name="committed")
    public Integer getCommitted() {
        return this.committed;
    }
    
    public void setCommitted(Integer committed) {
        this.committed = committed;
    }
    
    @Column(name="available")
    public Integer getAvailable() {
        return this.available;
    }
    
    public void setAvailable(Integer available) {
        this.available = available;
    }
    
    @Column(name="receivedPrice", precision=10)
    public Float getReceivedPrice() {
        return this.receivedPrice;
    }
    
    public void setReceivedPrice(Float receivedPrice) {
        this.receivedPrice = receivedPrice;
    }
    
    @Column(name="receivedDiscount", precision=10)
    public Float getReceivedDiscount() {
        return this.receivedDiscount;
    }
    
    public void setReceivedDiscount(Float receivedDiscount) {
        this.receivedDiscount = receivedDiscount;
    }
    
    @Column(name="weight", precision=12, scale=0)
    public Float getWeight() {
        return this.weight;
    }
    
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    
    @Column(name="costPerPound", precision=12, scale=0)
    public Float getCostPerPound() {
        return this.costPerPound;
    }
    
    public void setCostPerPound(Float costPerPound) {
        this.costPerPound = costPerPound;
    }
    
    @Column(name="isbn13", length=15)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="bellInventory")
    public Set<BellSku> getBellSkus() {
        return this.bellSkus;
    }
    
    public void setBellSkus(Set<BellSku> bellSkus) {
        this.bellSkus = bellSkus;
    }
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="bellInventory")
    public Set<BellOrderItem> getBellOrderItems() {
        return this.bellOrderItems;
    }
    
    public void setBellOrderItems(Set<BellOrderItem> bellOrderItems) {
        this.bellOrderItems = bellOrderItems;
    }

    @Transient
    public String getIsbn10() {
        return IsbnUtil.getIsbn10(isbn);
    }


    @Transient
    public String getConditionString(){
        if (bellcondition == null) return "";
        if (bellcondition == 1) {
            return "Used; Like New";
        } else if (bellcondition == 2) {
            return "Used; Very Good";
        } else if (bellcondition == 3) {
            return "Used; Good";
        } else if (bellcondition == 4) {
            return "Used; Acceptable";
        } else if (bellcondition == 5) {
            return "Collectible; Like New";
        } else if (bellcondition == 6) {
            return "Collectible; Very Good";
        } else if (bellcondition == 7) {
            return "Collectible; Good";
        } else if (bellcondition == 8) {
            return "Collectible; Acceptable";
        } else if (bellcondition == 11) {
            return "New";
        }
        return "";
    }
    
    @Transient
    public Float getSellPricePercentList() {
        if (this.listPrice != null && this.sellPrice != null && this.listPrice > 0 && this.sellPrice > 0){
            return (this.sellPrice / this.listPrice) * 100;
        }
        return 0F;
    }

    @Transient
    public Float getCostPercentList(){
        Float cost = getCost();
        if (cost != 0F && listPrice != null && listPrice != 0F){
            return (cost/listPrice) * 100F;
        }
        return 0F;
    }
    
}


