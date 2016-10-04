package com.bc.orm;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="break_received_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class BreakReceivedItem extends BaseEntity implements Serializable {

    private BreakReceived breakReceived;
    private Integer orderedQuantity;
    private Integer quantity;
    private Integer available;
    private Float listPrice;
    private Float sellPrice;
    private Float cost;
    private String invoicenumber;
    private Float discount;
    private String isbn;
    private String breakRoomIsbn;
    private String title;
    private String bin;
    private Date date;
    private String type;
    private String bookType;
    private String coverType;
    private Boolean skid = false;
    private String skidType;
    private Boolean breakRoom = false;
    private Integer pieceCount;
    private String breakRoomCondition;
    private String cond;
    
    private List<BriCount> briCounts;

    public BreakReceivedItem() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="break_received_id")
    public BreakReceived getBreakReceived() {
        return this.breakReceived;
    }
    
    public void setBreakReceived(BreakReceived breakReceived) {
        this.breakReceived = breakReceived;
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
    
    @Column(name="breakRoomIsbn", length=50)
    public String getBreakRoomIsbn() {
        return this.breakRoomIsbn;
    }
    
    public void setBreakRoomIsbn(String breakRoomIsbn) {
        this.breakRoomIsbn = breakRoomIsbn;
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
    
    @Column(name="skid")
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skidType", length=50)
    public String getSkidType() {
        return this.skidType;
    }
    
    public void setSkidType(String skidType) {
        this.skidType = skidType;
    }
    
    @Column(name="breakRoom")
    public Boolean getBreakRoom() {
        return this.breakRoom;
    }
    
    public void setBreakRoom(Boolean breakRoom) {
        this.breakRoom = breakRoom;
    }
    
    @Column(name="pieceCount")
    public Integer getPieceCount() {
        return this.pieceCount;
    }
    
    public void setPieceCount(Integer pieceCount) {
        this.pieceCount = pieceCount;
    }
    
    @Column(name="breakRoomCondition", length=50)
    public String getBreakRoomCondition() {
        return this.breakRoomCondition;
    }
    
    public void setBreakRoomCondition(String breakRoomCondition) {
        this.breakRoomCondition = breakRoomCondition;
    }
    
    @Column(name="cond", length=50)
    public String getCond() {
        return this.cond;
    }
    
    public void setCond(String cond) {
        this.cond = cond;
    }

    @OneToMany(mappedBy="breakReceivedItem")
    public List<BriCount> getBriCounts() {
        return briCounts;
    }

    public void setBriCounts(List<BriCount> briCounts) {
        this.briCounts = briCounts;
    }

    @Transient
    public String getIsbnQuantityListView(){
        return getIsbn()+" - "+getQuantity();
    }


}


