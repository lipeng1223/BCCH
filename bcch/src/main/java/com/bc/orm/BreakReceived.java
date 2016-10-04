package com.bc.orm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

import com.bc.util.DateFormat;

@SuppressWarnings("serial")
@Entity
@Table(name="break_received")
@Inheritance(strategy=InheritanceType.JOINED)
public class BreakReceived extends BaseEntity implements Serializable {

    private Vendor vendor;
    private String comment;
    private String vendorCode;
    private String publisherCode;
    private String poNumber;
    private Date poDate;
    private Float poTotal;
    private String clerk;
    private Boolean skid = false;
    private String skidbarcode;
    private Integer skidpiececount;
    private Date date;
    private String skidCondition;
    private List<BreakReceivedItem> breakReceivedItems = new ArrayList<BreakReceivedItem>(0);

    public BreakReceived() {
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
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
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
    
    @Column(name="skid")
    public Boolean getSkid() {
        return this.skid;
    }
    
    public void setSkid(Boolean skid) {
        this.skid = skid;
    }
    
    @Column(name="skidbarcode", length=50)
    public String getSkidbarcode() {
        return this.skidbarcode;
    }
    
    public void setSkidbarcode(String skidbarcode) {
        this.skidbarcode = skidbarcode;
    }
    
    @Column(name="skidpiececount")
    public Integer getSkidpiececount() {
        return this.skidpiececount;
    }
    
    public void setSkidpiececount(Integer skidpiececount) {
        this.skidpiececount = skidpiececount;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Column(name="skidCondition", length=50)
    public String getSkidCondition() {
        return this.skidCondition;
    }
    
    public void setSkidCondition(String skidCondition) {
        this.skidCondition = skidCondition;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="breakReceived")
    public List<BreakReceivedItem> getBreakReceivedItems() {
        return this.breakReceivedItems;
    }
    
    public void setBreakReceivedItems(List<BreakReceivedItem> breakReceivedItems) {
        this.breakReceivedItems = breakReceivedItems;
    }

    @Transient
    public String getDatePoListView(){
        return DateFormat.format(getPoDate())+" - "+getPoNumber();
    }
    
}


