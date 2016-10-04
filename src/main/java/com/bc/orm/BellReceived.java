package com.bc.orm;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_received")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellReceived extends BaseEntity implements Auditable, Serializable {

    private BellVendor vendor;
    private Integer parentId;
    private String comment;
    private String transno;
    private Boolean posted;
    private String vendorCode;
    private String publisherCode;
    private String poNumber;
    private Date poDate;
    private Float poTotal;
    private String clerk;
    private String barcode;
    private Date date;
    private Date postDate;
    private Date duedate;
    private Byte deleted;
    private Boolean skid;
    private String skidIsbn;
    private Set<BellReceivedItem> bellReceivedItems = new HashSet<BellReceivedItem>(0);

    private Integer totalItems = 0;
    private Integer totalQuantity = 0;
    private Integer totalOrderedQuantity = 0;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalExtendedCost = BigDecimal.ZERO;
    private BigDecimal totalSellPrice = BigDecimal.ZERO;
    
    public BellReceived() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("poNumber", poNumber));
        sb.append(getColAudit("vendorCode", vendorCode));
        return sb.toString();
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="vendor_id")
    public BellVendor getVendor() {
        return this.vendor;
    }
    
    public void setVendor(BellVendor vendor) {
        this.vendor = vendor;
    }

    @Column(name="parent_id")
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
    }
    
    @Column(name="posted")
    public Boolean getPosted() {
        return this.posted;
    }
    
    public void setPosted(Boolean posted) {
        this.posted = posted;
    }
    
    @Column(name="vendor_code", length=50)
    public String getVendorCode() {
        return this.vendorCode;
    }
    
    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }
    
    @Column(name="publisher_code", length=50)
    public String getPublisherCode() {
        return this.publisherCode;
    }
    
    public void setPublisherCode(String publisherCode) {
        this.publisherCode = publisherCode;
    }
    
    @Column(name="po_number", length=100)
    public String getPoNumber() {
        return this.poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="po_date", length=19)
    public Date getPoDate() {
        return this.poDate;
    }
    
    public void setPoDate(Date poDate) {
        this.poDate = poDate;
    }
    
    @Column(name="po_total", precision=10)
    public Float getPoTotal() {
        return this.poTotal;
    }
    
    public void setPoTotal(Float poTotal) {
        this.poTotal = poTotal;
    }
    
    @Column(name="clerk", length=50)
    public String getClerk() {
        return this.clerk;
    }
    
    public void setClerk(String clerk) {
        this.clerk = clerk;
    }
    
    @Column(name="barcode", length=100)
    public String getBarcode() {
        return this.barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="post_date", length=19)
    public Date getPostDate() {
        return this.postDate;
    }
    
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="duedate", length=19)
    public Date getDuedate() {
        return this.duedate;
    }
    
    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
    
    @Column(name="deleted")
    public Byte getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="skid")
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skid_isbn", length=50)
    public String getSkidIsbn() {
        return this.skidIsbn;
    }
    
    public void setSkidIsbn(String skidIsbn) {
        this.skidIsbn = skidIsbn;
    }
    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="bellReceived")
    public Set<BellReceivedItem> getBellReceivedItems() {
        return this.bellReceivedItems;
    }
    
    public void setBellReceivedItems(Set<BellReceivedItem> bellReceivedItems) {
        this.bellReceivedItems = bellReceivedItems;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalOrderedQuantity() {
        return totalOrderedQuantity;
    }

    public void setTotalOrderedQuantity(Integer totalOrderedQuantity) {
        this.totalOrderedQuantity = totalOrderedQuantity;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalSellPrice() {
        return totalSellPrice;
    }

    public void setTotalSellPrice(BigDecimal totalSellPrice) {
        this.totalSellPrice = totalSellPrice;
    }

    public BigDecimal getTotalExtendedCost() {
        return totalExtendedCost;
    }

    public void setTotalExtendedCost(BigDecimal totalExtendedCost) {
        this.totalExtendedCost = totalExtendedCost;
    }

    
}


