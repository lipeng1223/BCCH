package com.bc.jasper;

public class BellwetherShippingData implements Comparable {

    
    public int compareTo(Object ob) {
        if (ob instanceof BellwetherShippingData){
            int c = ((BellwetherShippingData)ob).getSkuAndItemName().compareTo(getSkuAndItemName());
            if (c == 0){
                return 0;
            }
            return -c;
        }
        return 0;
    }

    private String skuAndItemName;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String orderid;
    private String recipient;
    
    public String getCityStateZip(){
        StringBuilder sb = new StringBuilder();
        sb.append(city);
        sb.append(", ");
        sb.append(state);
        sb.append(". ");
        sb.append(zip);
        return sb.toString();
    }
    
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSkuAndItemName() {
        return skuAndItemName;
    }

    public void setSkuAndItemName(String skuAndItemName) {
        this.skuAndItemName = skuAndItemName;
    }
}
