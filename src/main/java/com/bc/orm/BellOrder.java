package com.bc.orm;


import java.io.Serializable;
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

import com.bc.util.DateFormat;
import com.bc.util.DateFormatLong;
import com.bc.util.IsbnUtil;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_order")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellOrder extends BaseEntity implements Auditable, Serializable {


    private BellCustomer bellCustomer;
    private BellCustomerShipping bellCustomerShipping;
    private Integer userId;
    private String paymentsStatus;
    private String orderId;
    private String orderItemId;
    private Date paymentsDate;
    private String paymentsTransactionId;
    private String itemName;
    private String listingId;
    private String sku;
    private Float price;
    private Float shippingFee;
    private Integer quantityPurchased;
    private Float totalPrice;
    private Date purchaseDate;
    private String batchId;
    private String buyerEmail;
    private String buyerName;
    private String recipientName;
    private String shipAddress1;
    private String shipAddress2;
    private String shipCity;
    private String shipState;
    private String shipZip;
    private String shipCountry;
    private String specialComments;
    private String upc;
    private String shipMethod;
    private Integer orderHandlingState;
    private String orderHandler;
    private Float partialRefund;
    private String category;
    private String invoiceNumber;
    private String salesman;
    private String comment;
    private String comment2;
    private String poNumber;
    private String customerCode;
    private String shipVia;
    private Float shippingCharges;
    private Float depositAmmount;
    private Boolean customerVisit = false;
    private Boolean creditMemo = false;
    private Boolean posted = false;
    private String refundCategory;
    private Date shipDate;
    private Date orderDate;
    private Date postDate;
    private Float extended;
    private Integer backordered;
    private Float cost;
    private String location;
    private Set<BellOrderItem> bellOrderItems = new HashSet<BellOrderItem>(0);
    
    
    private Integer totalItems = 0;
    private Integer totalQuantity;
    private Integer totalNonShippedQuantity;
    private BigDecimal totalCost;
    private BigDecimal totalPricePreTax;
    private BigDecimal balanceDue;
    private BigDecimal totalTax;
    private BigDecimal totalPriceNonShipped;
    private BigDecimal totalExtended;

    
    // transient fillz data
    private String fillzStatus;
    private String tracking;
    private String buyerNote;
    private String sellerNote;
    private String productId;
    private String paymentMethod;
    private Integer itemCondition;
    private String itemSource;
    private Float fillzCost;
    private String isBook;
    private String sellerId;
    
    
    public BellOrder() {
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
    public BellCustomer getBellCustomer() {
        return this.bellCustomer;
    }
    
    public void setBellCustomer(BellCustomer bellCustomer) {
        this.bellCustomer = bellCustomer;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="shipping_id")
    public BellCustomerShipping getBellCustomerShipping() {
        return this.bellCustomerShipping;
    }
    
    public void setBellCustomerShipping(BellCustomerShipping bellCustomerShipping) {
        this.bellCustomerShipping = bellCustomerShipping;
    }
    
    @Column(name="userId")
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    @Column(name="paymentsStatus", length=30)
    public String getPaymentsStatus() {
        return this.paymentsStatus;
    }
    
    public void setPaymentsStatus(String paymentsStatus) {
        this.paymentsStatus = paymentsStatus;
    }
    
    @Column(name="orderId", length=30)
    public String getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    @Column(name="orderItemId", length=30)
    public String getOrderItemId() {
        return this.orderItemId;
    }
    
    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="paymentsDate", length=19)
    public Date getPaymentsDate() {
        return this.paymentsDate;
    }
    
    public void setPaymentsDate(Date paymentsDate) {
        this.paymentsDate = paymentsDate;
    }
    
    @Column(name="paymentsTransactionId", length=30)
    public String getPaymentsTransactionId() {
        return this.paymentsTransactionId;
    }
    
    public void setPaymentsTransactionId(String paymentsTransactionId) {
        this.paymentsTransactionId = paymentsTransactionId;
    }
    
    @Column(name="itemName", length=100)
    public String getItemName() {
        return this.itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    @Column(name="listingId", length=20)
    public String getListingId() {
        return this.listingId;
    }
    
    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
    
    @Column(name="sku", length=50)
    public String getSku() {
        return this.sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    @Column(name="price", precision=10)
    public Float getPrice() {
        return this.price;
    }
    
    public void setPrice(Float price) {
        this.price = price;
    }
    
    @Column(name="shippingFee", precision=10)
    public Float getShippingFee() {
        return this.shippingFee;
    }
    
    public void setShippingFee(Float shippingFee) {
        this.shippingFee = shippingFee;
    }
    
    @Column(name="quantityPurchased")
    public Integer getQuantityPurchased() {
        return this.quantityPurchased;
    }
    
    public void setQuantityPurchased(Integer quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }
    
    @Column(name="totalPrice", precision=10)
    public Float getTotalPrice() {
        return this.totalPrice;
    }
    
    @Transient
    public Float getTotalPriceForDisplay(){
        if (category != null && category.equals("Amazon")){
            float tot = price * quantityPurchased;
            if (shippingFee != null) tot+=shippingFee;
            return tot;
        }
        return totalPrice;
    }
    
    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="purchaseDate", length=19)
    public Date getPurchaseDate() {
        return this.purchaseDate;
    }
    
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    @Column(name="batchId", length=12)
    public String getBatchId() {
        return this.batchId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    @Column(name="buyerEmail")
    public String getBuyerEmail() {
        return this.buyerEmail;
    }
    
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }
    
    @Column(name="buyerName", length=128)
    public String getBuyerName() {
        return this.buyerName;
    }
    
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
    
    @Column(name="recipientName", length=128)
    public String getRecipientName() {
        return this.recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    @Column(name="shipAddress1", length=128)
    public String getShipAddress1() {
        return this.shipAddress1;
    }
    
    public void setShipAddress1(String shipAddress1) {
        this.shipAddress1 = shipAddress1;
    }
    
    @Column(name="shipAddress2", length=128)
    public String getShipAddress2() {
        return this.shipAddress2;
    }
    
    public void setShipAddress2(String shipAddress2) {
        this.shipAddress2 = shipAddress2;
    }
    
    @Column(name="shipCity", length=50)
    public String getShipCity() {
        return this.shipCity;
    }
    
    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }
    
    @Column(name="shipState", length=50)
    public String getShipState() {
        return this.shipState;
    }
    
    public void setShipState(String shipState) {
        this.shipState = shipState;
    }
    
    @Column(name="shipZip", length=30)
    public String getShipZip() {
        return this.shipZip;
    }
    
    public void setShipZip(String shipZip) {
        this.shipZip = shipZip;
    }
    
    @Column(name="shipCountry", length=30)
    public String getShipCountry() {
        return this.shipCountry;
    }
    
    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
    }
    
    @Column(name="specialComments")
    public String getSpecialComments() {
        return this.specialComments;
    }
    
    public void setSpecialComments(String specialComments) {
        this.specialComments = specialComments;
    }
    
    @Column(name="upc", length=20)
    public String getUpc() {
        return this.upc;
    }
    
    public void setUpc(String upc) {
        this.upc = upc;
    }
    
    @Column(name="shipMethod", length=30)
    public String getShipMethod() {
        return this.shipMethod;
    }
    
    public void setShipMethod(String shipMethod) {
        this.shipMethod = shipMethod;
    }
    
    @Column(name="orderHandlingState")
    public Integer getOrderHandlingState() {
        return this.orderHandlingState;
    }
    
    public void setOrderHandlingState(Integer orderHandlingState) {
        this.orderHandlingState = orderHandlingState;
    }
    
    @Column(name="orderHandler", length=64)
    public String getOrderHandler() {
        return this.orderHandler;
    }
    
    public void setOrderHandler(String orderHandler) {
        this.orderHandler = orderHandler;
    }
    
    @Column(name="partialRefund", precision=10)
    public Float getPartialRefund() {
        return this.partialRefund;
    }
    
    public void setPartialRefund(Float partialRefund) {
        this.partialRefund = partialRefund;
    }
    
    @Column(name="category", length=20)
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Column(name="invoiceNumber", length=20)
    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
    
    @Column(name="poNumber", length=100)
    public String getPoNumber() {
        return this.poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    @Column(name="customerCode", length=50)
    public String getCustomerCode() {
        return this.customerCode;
    }
    
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    
    @Column(name="shipVia", length=100)
    public String getShipVia() {
        return this.shipVia;
    }
    
    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
    }
    
    @Column(name="shippingCharges", precision=10)
    public Float getShippingCharges() {
        return this.shippingCharges;
    }
    
    public void setShippingCharges(Float shippingCharges) {
        this.shippingCharges = shippingCharges;
    }
    
    @Column(name="depositAmmount", precision=10)
    public Float getDepositAmmount() {
        return this.depositAmmount;
    }
    
    public void setDepositAmmount(Float depositAmmount) {
        this.depositAmmount = depositAmmount;
    }
    
    @Column(name="customerVisit")
    public Boolean getCustomerVisit() {
        return this.customerVisit;
    }
    
    public void setCustomerVisit(Boolean customerVisit) {
        this.customerVisit = customerVisit;
    }
    
    @Column(name="creditMemo")
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
    
    @Column(name="refundCategory", length=64)
    public String getRefundCategory() {
        return this.refundCategory;
    }
    
    public void setRefundCategory(String refundCategory) {
        this.refundCategory = refundCategory;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="shipDate", length=19)
    public Date getShipDate() {
        return this.shipDate;
    }
    
    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="orderDate", length=19)
    public Date getOrderDate() {
        return this.orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="postDate", length=19)
    public Date getPostDate() {
        return this.postDate;
    }
    
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
    
    @Column(name="extended", precision=10)
    public Float getExtended() {
        return this.extended;
    }
    
    public void setExtended(Float extended) {
        this.extended = extended;
    }
    
    @Column(name="backordered")
    public Integer getBackordered() {
        return this.backordered;
    }
    
    public void setBackordered(Integer backordered) {
        this.backordered = backordered;
    }
    
    @Column(name="cost", precision=12, scale=0)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }
    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="bellOrder")
    public Set<BellOrderItem> getBellOrderItems() {
        return this.bellOrderItems;
    }
    
    public void setBellOrderItems(Set<BellOrderItem> bellOrderItems) {
        this.bellOrderItems = bellOrderItems;
    }

    @Transient
    public String getFillzStatus() {
        return fillzStatus;
    }

    public void setFillzStatus(String fillzStatus) {
        this.fillzStatus = fillzStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Transient
    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    @Transient
    public String getBuyerNote() {
        return buyerNote;
    }

    public void setBuyerNote(String buyerNote) {
        this.buyerNote = buyerNote;
    }

    @Transient
    public String getSellerNote() {
        return sellerNote;
    }

    public void setSellerNote(String sellerNote) {
        this.sellerNote = sellerNote;
    }

    @Transient
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Transient
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Transient
    public Integer getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(Integer itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Transient
    public String getItemSource() {
        return itemSource;
    }

    public void setItemSource(String itemSource) {
        this.itemSource = itemSource;
    }

    @Transient
    public Float getFillzCost() {
        return fillzCost;
    }

    public void setFillzCost(Float fillzCost) {
        this.fillzCost = fillzCost;
    }

    @Transient
    public String getIsBook() {
        return isBook;
    }

    public void setIsBook(String isBook) {
        this.isBook = isBook;
    }

    @Transient
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Transient
    public String getPaymentsDateFormattedWithTz(){
        return DateFormatLong.formatWithTz(paymentsDate);
    }
    
    @Transient
    public String getPaymentsDateFormattedWithTzPst(){
        return DateFormatLong.formatWithTzPst(paymentsDate);
    }
    
    @Transient
    public String getPurchaseDateFormattedWithTzPst(){
        if (category != null && category.equals("Internal")){
            return DateFormat.format(purchaseDate);
        }
        return DateFormatLong.formatWithTzPst(purchaseDate);
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
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

    public Integer getTotalNonShippedQuantity() {
        return totalNonShippedQuantity;
    }

    public void setTotalNonShippedQuantity(Integer totalNonShippedQuantity) {
        this.totalNonShippedQuantity = totalNonShippedQuantity;
    }

    public BigDecimal getTotalPriceNonShipped() {
        return totalPriceNonShipped;
    }

    public void setTotalPriceNonShipped(BigDecimal totalPriceNonShipped) {
        this.totalPriceNonShipped = totalPriceNonShipped;
    }

    public BigDecimal getTotalPricePreTax() {
        return totalPricePreTax;
    }

    public void setTotalPricePreTax(BigDecimal totalPricePreTax) {
        this.totalPricePreTax = totalPricePreTax;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    @Transient
    public String getIsbn(){
        if (sku != null && sku.length() >= 10){
            if (sku.length() >=13){
                String look = sku.substring(sku.length()-13, sku.length());
                if (look.startsWith("978")){
                    return IsbnUtil.getIsbn10(look);
                }
            }
            return sku.substring(sku.length()-10, sku.length());
        }
        return "";
    }
    
    @Transient
    public String getIsbn13(){
        String isbn = getIsbn();
        if (isbn.length() == 10){
            return IsbnUtil.getIsbn13(isbn);
        }
        return "";
    }    
}


