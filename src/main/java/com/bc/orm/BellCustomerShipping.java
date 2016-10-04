package com.bc.orm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_customer_shipping")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellCustomerShipping extends BaseEntity implements Serializable {

    private BellCustomer bellCustomer;
    private String code;
    private String shippingName;
    private String shippingCompany;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String country;
    private String comment;
    private String zip;
    private String phone;
    private String homePhone;
    private String workPhone;
    private String fax;
    private String email;
    private Set<BellOrder> bellOrders = new HashSet<BellOrder>(0);

    public BellCustomerShipping() {
    }

    @Column(name="code", length=50)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="shipping_name", length=100)
    public String getShippingName() {
        return this.shippingName;
    }
    
    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }
    
    @Column(name="shipping_company", length=100)
    public String getShippingCompany() {
        return this.shippingCompany;
    }
    
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    
    @Column(name="address1")
    public String getAddress1() {
        return this.address1;
    }
    
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    @Column(name="address2")
    public String getAddress2() {
        return this.address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    @Column(name="address3")
    public String getAddress3() {
        return this.address3;
    }
    
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    
    @Column(name="city", length=50)
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    @Column(name="state", length=50)
    public String getState() {
        return this.state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    @Column(name="country", length=50)
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="zip", length=25)
    public String getZip() {
        return this.zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    @Column(name="phone", length=25)
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Column(name="home_phone", length=25)
    public String getHomePhone() {
        return this.homePhone;
    }
    
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    
    @Column(name="work_phone", length=25)
    public String getWorkPhone() {
        return this.workPhone;
    }
    
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }
    
    @Column(name="fax", length=25)
    public String getFax() {
        return this.fax;
    }
    
    public void setFax(String fax) {
        this.fax = fax;
    }
    
    @Column(name="email", length=128)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="bellCustomerShipping")
    public Set<BellOrder> getBellOrders() {
        return this.bellOrders;
    }
    
    public void setBellOrders(Set<BellOrder> bellOrders) {
        this.bellOrders = bellOrders;
    }

    public BellCustomer getBellCustomer() {
        return bellCustomer;
    }

    public void setBellCustomer(BellCustomer bellCustomer) {
        this.bellCustomer = bellCustomer;
    }




}


