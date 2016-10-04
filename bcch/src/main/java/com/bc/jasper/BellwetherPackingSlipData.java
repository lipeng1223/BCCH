package com.bc.jasper;

import java.util.StringTokenizer;

public class BellwetherPackingSlipData implements Comparable {

    public int compareTo(Object ob) {
        if (ob instanceof BellwetherPackingSlipData){
            int c = ((BellwetherPackingSlipData)ob).getSkuAndItemName().compareTo(getSkuAndItemName());
            if (c == 0){
                return 0;
            }
            return -c;
        }
        return 0;
    }
    
    private String skuAndItemName;
    private String orderNumber;
    private String quantity;
    private String title;
    private String sku;
    private String listingId;
    private String comment;
    private String recipient;
    private String ship;
    private String buyer;
    private String location;

    /**
     * @return Returns the buyer.
     */
    public String getBuyer() {
        return buyer;
    }
    /**
     * @param buyer The buyer to set.
     */
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
    /**
     * @return Returns the listingId.
     */
    public String getListingId() {
        return listingId;
    }
    /**
     * @param listingId The listingId to set.
     */
    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
    /**
     * @return Returns the orderNumber.
     */
    public String getOrderNumber() {
        return orderNumber;
    }
    /**
     * @param orderNumber The orderNumber to set.
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    /**
     * @return Returns the quantity.
     */
    public String getQuantity() {
        return quantity;
    }
    /**
     * @param quantity The quantity to set.
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    /**
     * @return Returns the recipient.
     */
    public String getRecipient() {
        return recipient;
    }
    /**
     * @param recipient The recipient to set.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    /**
     * @return Returns the ship.
     */
    public String getShip() {
        return ship;
    }
    /**
     * @param ship The ship to set.
     */
    public void setShip(String ship) {
        this.ship = ship;
    }
    /**
     * @return Returns the sku.
     */
    public String getSku() {
        return sku;
    }
    /**
     * @param sku The sku to set.
     */
    public void setSku(String sku) {
        this.sku = sku;
    }
    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }


    public String getOrderNumberNoDashes(){
        StringBuilder on = new StringBuilder();
        StringTokenizer st = new StringTokenizer(orderNumber, "-");
        while (st.hasMoreTokens()){
            on.append(st.nextToken());
        }
        return on.toString();
    }
    public String getSkuAndItemName() {
        return skuAndItemName;
    }
    public void setSkuAndItemName(String skuAndItemName) {
        this.skuAndItemName = skuAndItemName;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location = location;
    }
}
