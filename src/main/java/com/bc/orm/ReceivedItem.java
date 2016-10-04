package com.bc.orm;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

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
@Table(name="received_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class ReceivedItem extends BaseEntity implements Auditable, Serializable, Comparable<ReceivedItem> {

    private Received received;
    private Integer parentId;
    private Float discount;
    private String bin;
    private String type;
    private String bookType;
    private String coverType;
    private String poNumber;
    private Boolean deleted = false;
    private Boolean breakroom = false;
    private Boolean skid = false;
    private Float skidPrice;
    private Float skidCost;
    private Float lbs;
    private Float lbsPrice;
    private Float lbsCost;
    private Integer pieces;
    private String transno;
    private String title = "";
    private Date date;
    private String isbn;
    private Integer orderedQuantity = 0;
    private Integer quantity = 0;
    private Integer available = 0;
    private Float listPrice = 0F;
    private Float sellPrice = 0F;
    private String invoicenumber;
    private Float cost = 0F;
    private Float extendedCost = 0F;
    private Integer skidPieceCount;
    private Float skidPiecePrice;
    private Float skidPieceCost;
    private String isbn13;
    private Float percentageList;
    private String cond;
    
    private InventoryItem inventoryItem;

    // transient
    private Integer preQuantity = 0;
    private Boolean bellbook = false;
    private Boolean higherEducation = false;
    private Boolean restricted = false;
    private Float costPerLb;
    private HashSet<String> updated = new HashSet<String>();
    
    public ReceivedItem() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("isbn13", isbn13));
        sb.append(getColAudit("condition", cond));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }
    
    private void checkUpdate(Object incoming, Object compare, String col){
        if (incoming != null && compare == null) updated.add(col);
        else if (compare != null && !compare.equals(incoming)) updated.add(col);
    }
    
    public Boolean isUpdated(String col){
        return updated.contains(col);
    }
    
    @Override
    public int compareTo(ReceivedItem ri) {
        if (ri == null) return 0;
        if (title != null && ri.getTitle() != null){
            return title.compareTo(ri.getTitle());
        }
        return 0;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="received_id")
    public Received getReceived() {
        return this.received;
    }
    
    public void setReceived(Received received) {
        this.received = received;
    }
    
    @Column(name="parent_id")
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    @Column(name="discount", precision=10)
    public Float getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
    
    @Column(name="bin", length=20)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        checkUpdate(bin, this.bin, "bin");
        this.bin = bin;
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
        checkUpdate(coverType, this.coverType, "coverType");
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
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
    }
    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Column(name="isbn", length=50)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Column(name="ordered_quantity")
    public Integer getOrderedQuantity() {
        return this.orderedQuantity;
    }
    
    public void setOrderedQuantity(Integer orderedQuantity) {
        checkUpdate(orderedQuantity, this.orderedQuantity, "orderedQuantity");
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
        checkUpdate(sellPrice, this.sellPrice, "sellPrice");
        this.sellPrice = sellPrice;
    }
    
    @Column(name="invoicenumber", length=20)
    public String getInvoicenumber() {
        return this.invoicenumber;
    }
    
    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        if (cost == null)
            return new Float(0);        
        return this.cost;
    }
    
    public void setCost(Float cost) {
        checkUpdate(cost, this.cost, "cost");
        this.cost = cost;
    }
    
    @Column(name="skid_piece_count")
    public Integer getSkidPieceCount() {
        return this.skidPieceCount;
    }
    
    public void setSkidPieceCount(Integer skidPieceCount) {
        checkUpdate(skidPiecePrice, this.skidPiecePrice, "skidPieceCount");
        this.skidPieceCount = skidPieceCount;
    }
    
    @Column(name="skid_piece_price", precision=10)
    public Float getSkidPiecePrice() {
        return this.skidPiecePrice;
    }
    
    public void setSkidPiecePrice(Float skidPiecePrice) {
        checkUpdate(skidPiecePrice, this.skidPiecePrice, "skidPiecePrice");
        this.skidPiecePrice = skidPiecePrice;
    }
    
    @Column(name="skid_piece_cost", precision=10)
    public Float getSkidPieceCost() {
        return this.skidPieceCost;
    }
    
    public void setSkidPieceCost(Float skidPieceCost) {
        checkUpdate(skidPieceCost, this.skidPieceCost, "skidPieceCost");
        this.skidPieceCost = skidPieceCost;
    }
    
    @Column(name="isbn13", length=13)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    @Column(name="percentage_list", precision=12, scale=0)
    public Float getPercentageList() {
        return this.percentageList;
    }
    
    public void setPercentageList(Float percentageList) {
        this.percentageList = percentageList;
    }
    
    @Column(name="cond", length=50)
    public String getCond() {
        return this.cond;
    }
    
    public void setCond(String cond) {
        this.cond = cond;
    }

    @Transient
    public Integer getPreQuantity() {
        return preQuantity;
    }

    public void setPreQuantity(Integer preQuantity) {
        this.preQuantity = preQuantity;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inventory_item_id")
    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    @Transient
    public Boolean getBellbook() {
        return bellbook;
    }

    public void setBellbook(Boolean bellbook) {
        this.bellbook = bellbook;
    }

    @Transient
    public Boolean getHigherEducation() {
        return higherEducation;
    }

    public void setHigherEducation(Boolean higherEducation) {
        this.higherEducation = higherEducation;
    }

    @Transient
    public Float getCostPerLb() {
        return costPerLb;
    }

    public void setCostPerLb(Float costPerLb) {
        this.costPerLb = costPerLb;
    }

    public Float getExtendedCost() {
        return extendedCost;
    }

    public void setExtendedCost(Float extendedCost) {
        this.extendedCost = extendedCost;
    }

    @Transient
    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }
    
    @Transient
    public HashSet<String> getUpdated(){
        return updated;
    }
}


