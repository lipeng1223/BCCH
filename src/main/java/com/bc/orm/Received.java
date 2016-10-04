package com.bc.orm;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
@Table(name="received")
@Inheritance(strategy=InheritanceType.JOINED)
public class Received extends BaseEntity implements Auditable, Serializable {

    private Vendor vendor;
    private Integer parentId;
    private String comment;
    private Boolean posted = false;
    private String barcode;
    private Boolean deleted;
    private String transno;
    private Date date;
    private String poNumber;
    private String publisherCode;
    private String vendorCode;
    private Date poDate;
    private Float poTotal;
    private String clerk;
    private Date duedate;
    private Date postDate;
    private Boolean skid = false;
    private String skidIsbn;
    private Boolean skidBreak = false;
    private String publisher;
    private String skidCondition;
    private Set<ReceivedItem> receivedItems = new HashSet<ReceivedItem>(0);
    private Boolean holding = false;
    
    private Integer totalItems = 0;
    private Integer totalQuantity = 0;
    private Integer totalOrderedQuantity = 0;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalExtendedCost = BigDecimal.ZERO;
    private BigDecimal totalSellPrice = BigDecimal.ZERO;

    public Received() {
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
    public Vendor getVendor() {
        return this.vendor;
    }
    
    public void setVendor(Vendor vendor) {
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
    
    @Column(name="posted")
    public Boolean getPosted() {
        return this.posted;
    }
    
    public void setPosted(Boolean posted) {
        this.posted = posted;
    }
    
    @Column(name="barcode", length=100)
    public String getBarcode() {
        return this.barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    @Column(name="deleted")
    public Boolean getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Column(name="po_number", length=100)
    public String getPoNumber() {
        return this.poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    @Column(name="publisher_code", length=50)
    public String getPublisherCode() {
        return this.publisherCode;
    }
    
    public void setPublisherCode(String publisherCode) {
        this.publisherCode = publisherCode;
    }
    
    @Column(name="vendor_code", length=50)
    public String getVendorCode() {
        return this.vendorCode;
    }
    
    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="po_date", length=19)
    public Date getPoDate() {
        return this.poDate;
    }
    
    public void setPoDate(Date poDate) {
        this.poDate = poDate;
        this.date = poDate;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="duedate", length=19)
    public Date getDuedate() {
        return this.duedate;
    }
    
    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="post_date", length=19)
    public Date getPostDate() {
        return this.postDate;
    }
    
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
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
    
    @Column(name="skidBreak")
    public Boolean getSkidBreak() {
        return this.skidBreak;
    }
    
    public void setSkidBreak(Boolean skidBreak) {
        this.skidBreak = skidBreak;
    }
    
    @Column(name="publisher")
    public String getPublisher() {
        return this.publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    @Column(name="skidCondition", length=50)
    public String getSkidCondition() {
        return this.skidCondition;
    }
    
    public void setSkidCondition(String skidCondition) {
        this.skidCondition = skidCondition;
    }
    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="received")
    public Set<ReceivedItem> getReceivedItems() {
        return this.receivedItems;
    }
    
    public void setReceivedItems(Set<ReceivedItem> receivedItems) {
        this.receivedItems = receivedItems;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getTotalOrderedQuantity() {
        return totalOrderedQuantity;
    }

    public void setTotalOrderedQuantity(Integer totalOrderedQuantity) {
        this.totalOrderedQuantity = totalOrderedQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalSellPrice() {
        return totalSellPrice;
    }

    public void setTotalSellPrice(BigDecimal totalSellPrice) {
        this.totalSellPrice = totalSellPrice;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalExtendedCost() {
        return totalExtendedCost;
    }

    public void setTotalExtendedCost(BigDecimal totalExtendedCost) {
        this.totalExtendedCost = totalExtendedCost;
    }

    public Boolean getHolding(){
    	return holding;
    }
    
    public void setHolding(Boolean holding){
    	this.holding = holding;
    }

}


