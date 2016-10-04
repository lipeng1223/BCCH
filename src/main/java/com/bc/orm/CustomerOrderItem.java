package com.bc.orm;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
@Entity
@Table(name="customer_order_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class CustomerOrderItem extends BaseEntity implements Auditable, Serializable, Comparable<CustomerOrderItem> {

    @Expose private InventoryItem inventoryItem;
    private CustomerOrder customerOrder;
    private Boolean credit = false;
    private Integer backorder;
    private Boolean deleted = false;
    private String bin;
    private String vendorpo;
    private String transno;
    @Expose private String isbn;
    @Expose private String title = "";
    @Expose private Integer quantity = 0;
    @Expose private Integer filled = 0;
    private Integer shippedQuantity = 0;
    private Float price = 0F;
    private Float cost = 0F;
    private Float extended = 0F;
    private Float discount = 0F;
    private String type;
    private Integer category;
    private String displayIsbn;
    private String isbn13;
    private Boolean creditDamage = false;
    private Boolean creditShortage = false;
    private Boolean creditRecNoBill = false;
    @Expose private String cond;
    private Integer invQuantity;
    private Integer bellQuantity;
    private Integer breakQuantity;
    private Long inventoryItemId;
    
    private Float latestCost;
    private BigDecimal totalPrice;
    private BigDecimal totalExtended;
    private BigDecimal totalPriceNonShipped;
    private BigDecimal totalExtendedNonShipped;

    @Expose private Boolean success;
    
    private String creditType;
    
    public CustomerOrderItem() {
    }
    
    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("condition", cond));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }
    
    @Override
    @Transient
    public int compareTo(CustomerOrderItem coi) {
        if (coi == null) return 0;
//        if (bin != null && coi.getBin() != null){
//            return bin.compareTo(coi.getBin());
//        }
//        return 0;
        if (bin == null)
            return 0;
        if (coi.bin == null)
            return 0;
        return bin.compareTo(coi.bin);
    }

    

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inventory_item_id")
    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }
    
    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
        this.inventoryItemId = inventoryItem.id;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_order_id")
    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }
    
    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }
    
    @Column(name="credit")
    public Boolean getCredit() {
        return this.credit;
    }
    
    public void setCredit(Boolean credit) {
        this.credit = credit;
    }
    
    @Column(name="backorder")
    public Integer getBackorder() {
        return this.backorder;
    }
    
    public void setBackorder(Integer backorder) {
        this.backorder = backorder;
    }
    
    @Column(name="deleted")
    public Boolean getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="bin", length=20)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        this.bin = bin;
    }
    
    @Column(name="vendorpo", length=100)
    public String getVendorpo() {
        return this.vendorpo;
    }
    
    public void setVendorpo(String vendorpo) {
        this.vendorpo = vendorpo;
    }
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
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
    
    @Column(name="quantity")
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Column(name="filled")
    public Integer getFilled() {
        return this.filled;
    }
    
    public void setFilled(Integer filled) {
        this.filled = filled;
    }
    
    @Column(name="shipped_quantity")
    public Integer getShippedQuantity() {
        return this.shippedQuantity;
    }
    
    public void setShippedQuantity(Integer shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }
    
    @Column(name="price", precision=10)
    public Float getPrice() {
        return this.price;
    }
    
    public void setPrice(Float price) {
        this.price = price;
    }
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }
    
    @Column(name="extended", precision=10)
    public Float getExtended() {
        return this.extended;
    }
    
    public void setExtended(Float extended) {
        this.extended = extended;
    }
    
    @Column(name="discount", precision=10)
    public Float getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
    
    @Column(name="type", length=20)
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Column(name="category")
    public Integer getCategory() {
        return this.category;
    }
    
    public void setCategory(Integer category) {
        this.category = category;
    }
    
    @Column(name="display_isbn", length=50)
    public String getDisplayIsbn() {
        return this.displayIsbn;
    }
    
    public void setDisplayIsbn(String displayIsbn) {
        this.displayIsbn = displayIsbn;
    }
    
    @Column(name="isbn13", length=13)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    @Column(name="creditDamage")
    public Boolean getCreditDamage() {
        return this.creditDamage;
    }
    
    public void setCreditDamage(Boolean creditDamage) {
        this.creditDamage = creditDamage;
    }
    
    @Column(name="creditShortage")
    public Boolean getCreditShortage() {
        return this.creditShortage;
    }
    
    public void setCreditShortage(Boolean creditShortage) {
        this.creditShortage = creditShortage;
    }
    
    @Column(name="creditRecNoBill")
    public Boolean getCreditRecNoBill() {
        return this.creditRecNoBill;
    }
    
    public void setCreditRecNoBill(Boolean creditRecNoBill) {
        this.creditRecNoBill = creditRecNoBill;
    }
    
    @Column(name="cond", length=50)
    public String getCond() {
        return this.cond;
    }
    
    public void setCond(String cond) {
        this.cond = cond;
    }
    
    @Column(name="invQuantity")
    public Integer getInvQuantity() {
        return this.invQuantity;
    }
    
    public void setInvQuantity(Integer invQuantity) {
        this.invQuantity = invQuantity;
    }

    @Transient
    public Integer getInvQuantityNoZero() {
        if (this.invQuantity != null && this.invQuantity == 0) return null;
        return this.invQuantity;
    }
    
    @Column(name="bellQuantity")
    public Integer getBellQuantity() {
        return this.bellQuantity;
    }
    
    public void setBellQuantity(Integer bellQuantity) {
        this.bellQuantity = bellQuantity;
    }

    @Transient
    public Integer getBellQuantityNoZero() {
        if (this.bellQuantity != null && this.bellQuantity == 0) return null;
        return this.bellQuantity;
    }
    
    @Column(name="breakQuantity")
    public Integer getBreakQuantity() {
        return this.breakQuantity;
    }
    
    public void setBreakQuantity(Integer breakQuantity) {
        this.breakQuantity = breakQuantity;
    }
    
    @Transient
    public Integer getBreakQuantityNoZero() {
        if (this.breakQuantity != null && this.breakQuantity == 0) return null;
        return this.breakQuantity;
    }

    public Float getLatestCost() {
        return latestCost;
    }

    public void setLatestCost(Float latestCost) {
        this.latestCost = latestCost;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalExtended() {
        return totalExtended;
    }

    public void setTotalExtended(BigDecimal totalExtended) {
        this.totalExtended = totalExtended;
    }

    @Transient
    public BigDecimal getTotalPriceNonShippedWithCredit(){
        if (credit){
            if (creditDamage || creditShortage){
                return new BigDecimal(-getTotalPriceNonShipped().floatValue());
            }
        }
        return getTotalPriceNonShipped();
    }
    
    public BigDecimal getTotalPriceNonShipped() {
        return totalPriceNonShipped;
    }

    public void setTotalPriceNonShipped(BigDecimal totalPriceNonShipped) {
        this.totalPriceNonShipped = totalPriceNonShipped;
    }

    public BigDecimal getTotalExtendedNonShipped() {
        return totalExtendedNonShipped;
    }

    public void setTotalExtendedNonShipped(BigDecimal totalExtendedNonShipped) {
        this.totalExtendedNonShipped = totalExtendedNonShipped;
    }

    @Transient
    public Integer getCurrentAllowed(){
        if (customerOrder != null && customerOrder.getPosted()) return null;
        if (inventoryItem != null){
            if (inventoryItem.getOnhand() - filled < 0){
                return inventoryItem.getOnhand();
            }
            return filled;
        }
        return 0;
    }

    @Transient
    public Float getPriceWithCredit(){
        if (credit && (creditDamage || creditShortage)) {
            return -getPrice();
        }
        return getPrice();
    }

    @Transient
    public Float getTotalPriceWithCredit(){
        if (credit){
            if (creditDamage || creditShortage){
                return -getTotalPrice().floatValue();
            }
        }
        return getTotalPrice().floatValue();
    }

    @Transient
    public String getBlank1() { return ""; }
    
    @Transient
    public String getBlank2() { return  ""; }
    
    @Transient
    public String getBlank3() { return ""; }

    @Transient
    public Boolean getAllFilled(){
        if (quantity != null && filled != null) return filled >= quantity;
        return false;
    }

    @Transient
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Transient
    public String getCreditType() {
        if (creditType == null){
            if (creditDamage) return "damage";
            else if (creditShortage) return "shortage";
            else if (creditRecNoBill) return "recNoBill";
        }
        return creditType;
    }

    public void setCreditType(String creditType) {
        if (creditType != null){
            if (creditType.equals("damage")) { creditDamage = true; credit = true; }
            else if (creditType.equals("shortage")) { creditShortage = true; credit = true; }
            else if (creditType.equals("recNoBill")) { creditRecNoBill = true; credit = true; }
        }
        this.creditType = creditType;
    }
}


