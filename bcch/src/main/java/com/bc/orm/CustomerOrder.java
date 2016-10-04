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
@Table(name="customer_order")
@Inheritance(strategy=InheritanceType.JOINED)
public class CustomerOrder extends BaseEntity implements Auditable, Serializable {

    private Customer customer;
    private CustomerShipping customerShipping;
    private Boolean deleted = false;
    private Boolean creditMemo = false;
    private String creditMemoType = "Damaged";
    private Float tax = 1F;
    private Boolean posted = false;
    private Integer backordered;
    private Boolean shipped = false;
    private Float cost = 0F;
    private Float extended = 0F;
    private Integer discount = 0;
    private String terms;
    private String poNumber;
    private String salesman;
    private String comment;
    private String comment2;
    private Boolean customerVisit;
    private Date postDate;
    private Float shippingCharges = 0F;
    private Float depositAmmount = 0F;
    private String invoiceNumber;
    private String transno;
    private Date shipDate;
    private Date orderDate;
    private String customerCode;
    private String shipVia;
    private String picker1;
    private String picker2;
    private String qualityControl;
    private String status;
    private Float palleteCharge = 0F;
    private String postedBy;
    private Date postedByDate;
    private Set<CustomerOrderItem> customerOrderItems = new HashSet<CustomerOrderItem>(0);
    
    private Boolean debitMemo = false;
    private String debitMemoType = "recNoInv";
    
    private Integer totalItems = 0;
    private Integer totalQuantity;
    private Integer totalNonShippedQuantity;
    private BigDecimal totalCost;
    private BigDecimal totalPricePreTax;
    private BigDecimal totalPrice;
    private BigDecimal balanceDue;
    private BigDecimal totalTax;
    private BigDecimal totalPriceNonShipped;
    private BigDecimal totalExtended;

    public CustomerOrder() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("poNumber", poNumber));
        return sb.toString();
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_id")
    public Customer getCustomer() {
        return this.customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_shipping_id")
    public CustomerShipping getCustomerShipping() {
        return this.customerShipping;
    }
    
    public void setCustomerShipping(CustomerShipping customerShipping) {
        this.customerShipping = customerShipping;
    }
    
    @Column(name="deleted")
    public Boolean getDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="credit_memo")
    public Boolean getCreditMemo() {
        return this.creditMemo;
    }
    
    public void setCreditMemo(Boolean creditMemo) {
        this.creditMemo = creditMemo;
    }
    
    @Column(name="posted")
    public Boolean getPosted() {
        return this.posted;
    }
    
    public void setPosted(Boolean posted) {
        this.posted = posted;
    }
    
    @Column(name="backordered")
    public Integer getBackordered() {
        return this.backordered;
    }
    
    public void setBackordered(Integer backordered) {
        this.backordered = backordered;
    }
    
    @Column(name="shipped")
    public Boolean getShipped() {
        return this.shipped;
    }
    
    public void setShipped(Boolean shipped) {
        this.shipped = shipped;
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
    
    @Column(name="discount")
    public Integer getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
    
    @Column(name="terms", length=50)
    public String getTerms() {
        return this.terms;
    }
    
    public void setTerms(String terms) {
        this.terms = terms;
    }
    
    @Column(name="po_number", length=100)
    public String getPoNumber() {
        return this.poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    @Column(name="salesman", length=50)
    public String getSalesman() {
        return this.salesman;
    }
    
    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="comment2")
    public String getComment2() {
        return this.comment2;
    }
    
    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }
    
    @Column(name="customer_visit")
    public Boolean getCustomerVisit() {
        return this.customerVisit;
    }
    
    public void setCustomerVisit(Boolean customerVisit) {
        this.customerVisit = customerVisit;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="post_date", length=19)
    public Date getPostDate() {
        return this.postDate;
    }
    
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
    
    @Column(name="shipping_charges", precision=10)
    public Float getShippingCharges() {
        return this.shippingCharges;
    }
    
    public void setShippingCharges(Float shippingCharges) {
        this.shippingCharges = shippingCharges;
    }
    
    @Column(name="deposit_ammount", precision=10)
    public Float getDepositAmmount() {
        return this.depositAmmount;
    }
    
    public void setDepositAmmount(Float depositAmmount) {
        this.depositAmmount = depositAmmount;
    }
    
    @Column(name="invoice_number", length=15)
    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    @Column(name="transno", length=12)
    public String getTransno() {
        return this.transno;
    }
    
    public void setTransno(String transno) {
        this.transno = transno;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ship_date", length=19)
    public Date getShipDate() {
        return this.shipDate;
    }
    
    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="order_date", length=19)
    public Date getOrderDate() {
        return this.orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    @Column(name="customer_code", length=50)
    public String getCustomerCode() {
        return this.customerCode;
    }
    
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    
    @Column(name="ship_via", length=100)
    public String getShipVia() {
        return this.shipVia;
    }
    
    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
    }
    
    @Column(name="picker1", length=2)
    public String getPicker1() {
        return this.picker1;
    }
    
    public void setPicker1(String picker1) {
        this.picker1 = picker1;
    }
    
    @Column(name="picker2", length=2)
    public String getPicker2() {
        return this.picker2;
    }
    
    public void setPicker2(String picker2) {
        this.picker2 = picker2;
    }
    
    @Column(name="qualityControl", length=2)
    public String getQualityControl() {
        return this.qualityControl;
    }
    
    public void setQualityControl(String qualityControl) {
        this.qualityControl = qualityControl;
    }
    
    @Column(name="status", length=48)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Column(name="palleteCharge", precision=12, scale=0)
    public Float getPalleteCharge() {
        return this.palleteCharge;
    }
    
    public void setPalleteCharge(Float palleteCharge) {
        this.palleteCharge = palleteCharge;
    }
    
    @Column(name="posted_by", length=128)
    public String getPostedBy() {
        return this.postedBy;
    }
    
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="posted_by_date", length=19)
    public Date getPostedByDate() {
        return this.postedByDate;
    }
    
    public void setPostedByDate(Date postedByDate) {
        this.postedByDate = postedByDate;
    }
    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="customerOrder")
    public Set<CustomerOrderItem> getCustomerOrderItems() {
        return this.customerOrderItems;
    }
    
    public void setCustomerOrderItems(Set<CustomerOrderItem> customerOrderItems) {
        this.customerOrderItems = customerOrderItems;
    }


    public Integer getTotalQuantity() {
        return totalQuantity;
    }


    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }


    public Integer getTotalNonShippedQuantity() {
        return totalNonShippedQuantity;
    }


    public void setTotalNonShippedQuantity(Integer totalNonShippedQuantity) {
        this.totalNonShippedQuantity = totalNonShippedQuantity;
    }


    public BigDecimal getTotalCost() {
        return totalCost;
    }


    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
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


    public Integer getTotalItems() {
        return totalItems;
    }


    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }


    public BigDecimal getTotalPriceNonShipped() {
        return totalPriceNonShipped;
    }


    public void setTotalPriceNonShipped(BigDecimal totalPriceNonShipped) {
        this.totalPriceNonShipped = totalPriceNonShipped;
    }


    public Float getTax() {
        return tax;
    }


    public void setTax(Float tax) {
        this.tax = tax;
    }


    public BigDecimal getTotalPricePreTax() {
        return totalPricePreTax;
    }


    public void setTotalPricePreTax(BigDecimal totalPricePreTax) {
        this.totalPricePreTax = totalPricePreTax;
    }


    public BigDecimal getBalanceDue() {
        return balanceDue;
    }


    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }


    public BigDecimal getTotalTax() {
        return totalTax;
    }


    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public String getCreditMemoType() {
        return creditMemoType;
    }

    public void setCreditMemoType(String creditMemoType) {
        this.creditMemoType = creditMemoType;
    }
    
    @Transient
    public String getCreditMemoTypeDisplay(){
        if (creditMemoType == null) return "";
        
        if (creditMemoType.equals("recNoBill")){
            return "Received But Not Billed";
        } else if (creditMemoType.equals("damage")){
            return "Damage";
        } else if (creditMemoType.equals("shortage")){
            return "Shortage";
        }
        
        return "";
    }
    
    @Column(name="debit_memo")
    public Boolean getDebitMemo() {
        return this.debitMemo;
    }
    
    public void setDebitMemo(Boolean debitMemo) {
        this.debitMemo = debitMemo;
    }
    
    public String getDebitMemoType() {
        return debitMemoType;
    }

    public void setDebitMemoType(String debitMemoType) {
        this.debitMemoType = debitMemoType;
    }

    /* This does not work, need some more logi
    @Transient
    public Float getProfitLoss(){
        if (totalPrice.doubleValue() < 0D){
            return totalPrice.add(totalExtended).floatValue();
        }
        return totalPrice.subtract(totalExtended).floatValue();
    }
    */

}


