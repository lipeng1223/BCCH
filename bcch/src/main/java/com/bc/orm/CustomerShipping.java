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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="customer_shipping")
@Inheritance(strategy=InheritanceType.JOINED)
public class CustomerShipping extends BaseEntity implements Auditable, Serializable, Comparable<CustomerShipping> {

    private Customer customer;
    private String shippingName;
    private String address3;
    private String comment;
    private String phone;
    private String code;
    private String shippingCompany;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String homePhone;
    private String workPhone;
    private String workExt;
    private String fax;
    private String email;
    private Set<CustomerOrder> customerOrders = new HashSet<CustomerOrder>(0);
    private Boolean deleted = false;
    private Boolean defaultShip = false;

    public CustomerShipping() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("address1", address1));
        sb.append(getColAudit("address2", address2));
        sb.append(getColAudit("city", city));
        sb.append(getColAudit("state", state));
        sb.append(getColAudit("zip", zip));
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
    
    @Column(name="shipping_name", length=100)
    public String getShippingName() {
        return this.shippingName;
    }
    
    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }
    
    @Column(name="address3")
    public String getAddress3() {
        return this.address3;
    }
    
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="phone", length=25)
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
        
    @Column(name="code", length=50)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
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
    
    @Column(name="zip", length=25)
    public String getZip() {
        return this.zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    @Column(name="country", length=50)
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    @Column(name="home_phone", length=15)
    public String getHomePhone() {
        return this.homePhone;
    }
    
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    
    @Column(name="work_phone", length=15)
    public String getWorkPhone() {
        return this.workPhone;
    }
    
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }
    
    @Column(name="work_ext", length=15)
    public String getWorkExt() {
        return this.workExt;
    }
    
    public void setWorkExt(String workExt) {
        this.workExt = workExt;
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
    @OneToMany(fetch=FetchType.LAZY, mappedBy="customerShipping")
    public Set<CustomerOrder> getCustomerOrders() {
        return this.customerOrders;
    }
    
    public void setCustomerOrders(Set<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }

    @Transient
    public String getGridDisplay(){
        StringBuilder sb = new StringBuilder();
        if (shippingName != null && shippingName.length() > 0){
            sb.append(shippingName);
            sb.append(" - ");
        }
        if (shippingCompany != null && shippingCompany.length() > 0){
            sb.append(shippingCompany);
            sb.append(" : ");
        }
        if (address1 != null && address1.length() > 0){
            sb.append(address1);
            sb.append(", ");
        }
        if (city != null && city.length() > 0){
            sb.append(city);
            sb.append(", ");
        }
        if (state != null && state.length() > 0){
            sb.append(state);
            sb.append(". ");
        }
        if (country != null && country.length() > 0){
            sb.append(country);
        }
        return sb.toString();
    }   
    
    @Transient
    public String getGridDisplayNoQuote(){
        return getGridDisplay().replace("'", "&#39;");
    }
    
    @Transient
    public String getViewDisplay(){
        StringBuilder sb = new StringBuilder();
        if (shippingName != null && shippingName.length() > 0){
            sb.append(shippingName);
            sb.append("<br/>");
        }
        if (shippingCompany != null && shippingCompany.length() > 0){
            sb.append(shippingCompany);
            sb.append("<br/>");
        }
        if (address1 != null && address1.length() > 0){
            sb.append(address1);
            sb.append("<br/>");
        }
        if (address2 != null && address2.length() > 0){
            sb.append(address2);
            sb.append("<br/>");
        }
        if (address3 != null && address3.length() > 0){
            sb.append(address3);
            sb.append("<br/>");
        }
        if (city != null && city.length() > 0){
            sb.append(city);
            sb.append(", ");
        }
        if (state != null && state.length() > 0){
            sb.append(state);
            sb.append(". ");
        }
        if (zip != null && zip.length() > 0){
            sb.append(zip);
            sb.append("  ");
        }
        if (country != null && country.length() > 0){
            sb.append(country);
        }
        return sb.toString();
    }

    @Transient
    public String getDisplay(){
        StringBuilder sb = new StringBuilder();
        if (shippingName != null && shippingName.length() > 0){
            sb.append(shippingName);
            sb.append("\n");
        }
        if (shippingCompany != null && shippingCompany.length() > 0){
            sb.append(shippingCompany);
            sb.append("\n");
        }
        if (address1 != null && address1.length() > 0){
            sb.append(address1);
            sb.append("\n");
        }
        if (address2 != null && address2.length() > 0){
            sb.append(address2);
            sb.append("\n");
        }
        if (address3 != null && address3.length() > 0){
            sb.append(address3);
            sb.append("\n");
        }
        if (city != null && city.length() > 0){
            sb.append(city);
            sb.append(", ");
        }
        if (state != null && state.length() > 0){
            sb.append(state);
            sb.append(". ");
        }
        if (zip != null && zip.length() > 0){
            sb.append(zip);
            sb.append("  ");
        }
        if (country != null && country.length() > 0){
            sb.append(country);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(CustomerShipping cs) {
        return this.id.compareTo(cs.getId());
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDefaultShip() {
        return defaultShip;
    }

    public void setDefaultShip(Boolean defaultShip) {
        this.defaultShip = defaultShip;
    }
    
    
}


