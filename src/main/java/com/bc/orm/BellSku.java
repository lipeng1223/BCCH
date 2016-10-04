package com.bc.orm;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_sku")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellSku extends BaseEntity implements Auditable, Serializable {

    private BellInventory bellInventory;
    private Date lastRecDate;
    private Float lastRecPrice;
    private Float lastRecQuantity;
    private String lastPo;
    private Date lastPoDate;
    private String cover;
    private Float sellPrice;
    private Float listPrice;
    private Float cost;
    private String bin;
    private String isbn;
    private String title;
    private String publisher;
    private String author;
    private String sku;
    private String amazonPage;
    private String amazonImage;
    private String bellcomment;
    private Integer bellcondition;
    private Boolean bellBook;
    private Boolean skid;
    private Integer skidPieces;
    private Float skidPiecePrice;
    private Float skidPieceCost;
    private Integer onhand;
    private Integer salesrank;
    private Date lastListDate;
    private Float lowPrice;
    private Integer listed;
    private Date lastAmzCheck;
    private Boolean lowest = false;
    private String location;

    public BellSku() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("sku", sku));
        return sb.toString();
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inventory_id")
    public BellInventory getBellInventory() {
        return this.bellInventory;
    }
    
    public void setBellInventory(BellInventory bellInventory) {
        this.bellInventory = bellInventory;
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
    
    @Column(name="isbn", length=48)
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
    
    @Column(name="listed")
    public Integer getListed() {
        return this.listed;
    }
    
    public void setListed(Integer listed) {
        this.listed = listed;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastAmzCheck", length=19)
    public Date getLastAmzCheck() {
        return this.lastAmzCheck;
    }
    
    public void setLastAmzCheck(Date lastAmzCheck) {
        this.lastAmzCheck = lastAmzCheck;
    }


    @Transient
    public String getIsLowest(){
        if (bellInventory != null && sellPrice != null && bellInventory.getLowUsed() != null){
            if (sellPrice.floatValue() <= bellInventory.getLowUsed().floatValue()){
                return "<img src='/images/accept.png'/>";
            }
        }
        return "<img src='/images/cross.png'/>";
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

    public Boolean getLowest() {
        return lowest;
    }

    public void setLowest(Boolean lowest) {
        this.lowest = lowest;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
}


