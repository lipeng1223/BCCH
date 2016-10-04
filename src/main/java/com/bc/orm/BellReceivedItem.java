package com.bc.orm;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_received_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellReceivedItem extends BaseEntity implements Auditable, Serializable {


    private BellReceived bellReceived;
    private BellInventory bellInventory;
    private String transno;
    private Integer parentId;
    private Integer orderedQuantity;
    private Integer quantity;
    private Integer available;
    private Float listPrice;
    private Float sellPrice;
    private Float cost;
    private String invoicenumber;
    private Float discount;
    private String isbn;
    private String title;
    private String bin;
    private Date date;
    private String type;
    private String bookType;
    private String coverType;
    private String poNumber;
    private Boolean deleted;
    private Boolean breakroom;
    private Boolean skid;
    private Float skidPrice;
    private Float skidCost;
    private Integer skidPieceCount;
    private Float skidPiecePrice;
    private Float skidPieceCost;
    private Float lbs;
    private Float lbsPrice;
    private Float lbsCost;
    private Integer pieces;
    private String lister;
    private String listerStatus;
    private String isbn13;
    private Float extendedCost = 0F;

    // transient
    private Integer preQuantity;


    public BellReceivedItem() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("isbn13", isbn13));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="received_id")
    public BellReceived getBellReceived() {
        return this.bellReceived;
    }
    
    public void setBellReceived(BellReceived bellReceived) {
        this.bellReceived = bellReceived;
    }
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
    }
    
    @Column(name="parent_id")
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    @Column(name="ordered_quantity")
    public Integer getOrderedQuantity() {
        return this.orderedQuantity;
    }
    
    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
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
    
    @Column(name="list_price", precision=10)
    public Float getListPrice() {
        return this.listPrice;
    }
    
    public void setListPrice(Float listPrice) {
        this.listPrice = listPrice;
    }
    
    @Column(name="sell_price", precision=10)
    public Float getSellPrice() {
        return this.sellPrice;
    }
    
    public void setSellPrice(Float sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }
    
    @Column(name="invoicenumber", length=20)
    public String getInvoicenumber() {
        return this.invoicenumber;
    }
    
    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }
    
    @Column(name="discount", precision=10)
    public Float getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Float discount) {
        this.discount = discount;
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
    }
    
    @Column(name="bin", length=20)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        this.bin = bin;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Column(name="type", length=20)
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Column(name="book_type", length=20)
    public String getBookType() {
        return this.bookType;
    }
    
    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
    
    @Column(name="cover_type", length=20)
    public String getCoverType() {
        return this.coverType;
    }
    
    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }
    
    @Column(name="po_number", length=100)
    public String getPoNumber() {
        return this.poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    @Column(name="deleted")
    public Boolean getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="breakroom")
    public Boolean getBreakroom() {
        return this.breakroom;
    }
    
    public void setBreakroom(Boolean breakroom) {
        this.breakroom = breakroom;
    }
    
    @Column(name="skid", length=20)
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skid_price", precision=10)
    public Float getSkidPrice() {
        return this.skidPrice;
    }
    
    public void setSkidPrice(Float skidPrice) {
        this.skidPrice = skidPrice;
    }
    
    @Column(name="skid_cost", precision=10)
    public Float getSkidCost() {
        return this.skidCost;
    }
    
    public void setSkidCost(Float skidCost) {
        this.skidCost = skidCost;
    }
    
    @Column(name="skid_piece_count")
    public Integer getSkidPieceCount() {
        return this.skidPieceCount;
    }
    
    public void setSkidPieceCount(Integer skidPieceCount) {
        this.skidPieceCount = skidPieceCount;
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
    
    @Column(name="lbs", precision=12, scale=0)
    public Float getLbs() {
        return this.lbs;
    }
    
    public void setLbs(Float lbs) {
        this.lbs = lbs;
    }
    
    @Column(name="lbs_price", precision=10)
    public Float getLbsPrice() {
        return this.lbsPrice;
    }
    
    public void setLbsPrice(Float lbsPrice) {
        this.lbsPrice = lbsPrice;
    }
    
    @Column(name="lbs_cost", precision=10)
    public Float getLbsCost() {
        return this.lbsCost;
    }
    
    public void setLbsCost(Float lbsCost) {
        this.lbsCost = lbsCost;
    }
    
    @Column(name="pieces")
    public Integer getPieces() {
        return this.pieces;
    }
    
    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }
    
    @Column(name="lister", length=64)
    public String getLister() {
        return this.lister;
    }
    
    public void setLister(String lister) {
        this.lister = lister;
    }
    
    @Column(name="lister_status", length=20)
    public String getListerStatus() {
        return this.listerStatus;
    }
    
    public void setListerStatus(String listerStatus) {
        this.listerStatus = listerStatus;
    }
    
    @Column(name="isbn13", length=50)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public Float getExtendedCost() {
        return extendedCost;
    }

    public void setExtendedCost(Float extendedCost) {
        this.extendedCost = extendedCost;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inventory_id")
    public BellInventory getBellInventory() {
        return bellInventory;
    }

    public void setBellInventory(BellInventory bellInventory) {
        this.bellInventory = bellInventory;
    }

    @Transient
    public Integer getPreQuantity() {
        return preQuantity;
    }

    public void setPreQuantity(Integer preQuantity) {
        this.preQuantity = preQuantity;
    }
    
}


