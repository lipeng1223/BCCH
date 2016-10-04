package com.bc.orm;


import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_order_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellOrderItem extends BaseEntity implements Auditable, Serializable {

    private BellOrder bellOrder;
    @Expose private BellInventory bellInventory;
    @Expose private String isbn;
    @Expose private String sku;
    private String displayIsbn;
    @Expose private String title;
    private String type;
    private Boolean credit;
    private Integer category;
    @Expose private Integer quantity;
    @Expose private Integer filled;
    private Integer shippedQuantity;
    private Float cost;
    private Float discount;
    private Float price;
    private Float extended;
    private String bin;
    private String vendorpo;
    private Integer backorder;
    private String isbn13;
    
    @Expose private Boolean success;
    

    private BigDecimal totalPrice;
    private BigDecimal totalExtended;
    private BigDecimal totalPriceNonShipped;
    private BigDecimal totalExtendedNonShipped;
    
    
    
    public BellOrderItem() {
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
    @JoinColumn(name="bell_order_id")
    public BellOrder getBellOrder() {
        return this.bellOrder;
    }
    
    public void setBellOrder(BellOrder bellOrder) {
        this.bellOrder = bellOrder;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inventory_id")
    public BellInventory getBellInventory() {
        return this.bellInventory;
    }
    
    public void setBellInventory(BellInventory bellInventory) {
        this.bellInventory = bellInventory;
    }
    
    @Column(name="isbn", length=50)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Column(name="sku", length=50)
    public String getSku() {
        return this.sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    @Column(name="display_isbn", length=50)
    public String getDisplayIsbn() {
        return this.displayIsbn;
    }
    
    public void setDisplayIsbn(String displayIsbn) {
        this.displayIsbn = displayIsbn;
    }
    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Column(name="type", length=20)
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Column(name="credit")
    public Boolean getCredit() {
        return this.credit;
    }
    
    public void setCredit(Boolean credit) {
        this.credit = credit;
    }
    
    @Column(name="category")
    public Integer getCategory() {
        return this.category;
    }
    
    public void setCategory(Integer category) {
        this.category = category;
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
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }
    
    @Column(name="discount", precision=10)
    public Float getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
    
    @Transient
    public Float getPriceWithCredit(){
        if (credit != null && credit) {
            return -getPrice();
        }
        return getPrice();
    }
    
    @Column(name="price", precision=10)
    public Float getPrice() {
        return this.price;
    }
    
    public void setPrice(Float price) {
        this.price = price;
    }
    
    @Column(name="extended", precision=10)
    public Float getExtended() {
        return this.extended;
    }
    
    public void setExtended(Float extended) {
        this.extended = extended;
    }
    
    @Column(name="bin", length=20)
    public String getBin() {
        return this.bin;
    }
    
    public void setBin(String bin) {
        this.bin = bin;
    }
    
    @Column(name="vendorpo", length=20)
    public String getVendorpo() {
        return this.vendorpo;
    }
    
    public void setVendorpo(String vendorpo) {
        this.vendorpo = vendorpo;
    }
    @Column(name="backorder")
    public Integer getBackorder() {
        return this.backorder;
    }
    
    public void setBackorder(Integer backorder) {
        this.backorder = backorder;
    }
    
    @Column(name="isbn13", length=50)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public BigDecimal getTotalExtended() {
        return totalExtended;
    }

    public void setTotalExtended(BigDecimal totalExtended) {
        this.totalExtended = totalExtended;
    }

    public BigDecimal getTotalExtendedNonShipped() {
        return totalExtendedNonShipped;
    }

    public void setTotalExtendedNonShipped(BigDecimal totalExtendedNonShipped) {
        this.totalExtendedNonShipped = totalExtendedNonShipped;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Transient
    public BigDecimal getTotalPriceNonShippedWithCredit(){
        if (credit != null && credit){
            return new BigDecimal(-getTotalPriceNonShipped().floatValue());
        }
        return getTotalPriceNonShipped();
    }
    
    public BigDecimal getTotalPriceNonShipped() {
        return totalPriceNonShipped;
    }

    public void setTotalPriceNonShipped(BigDecimal totalPriceNonShipped) {
        this.totalPriceNonShipped = totalPriceNonShipped;
    }

    @Transient
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
}


