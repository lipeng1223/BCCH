package com.bc.orm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="vendor")
@Inheritance(strategy=InheritanceType.JOINED)
public class Vendor extends BaseEntity implements Auditable, Serializable, Comparable<Vendor> {

    private String code;
    private String vendorName;
    private String accountNumber;
    private String shippingCompany;
    private String terms;
    
    private String email1;
    private String email2;
    
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zip;
    
    private String workPhone;
    private String cellPhone;
    private String homePhone;
    private String fax;
    
    private Set<BreakReceived> breakReceiveds = new HashSet<BreakReceived>(0);
    private Set<VendorSkidType> vendorSkidTypes = new HashSet<VendorSkidType>(0);
    private Set<Received> receiveds = new HashSet<Received>(0);
    
    public Vendor() {
    }

    @Transient
    public String getAuditMessage(){
        return "name: "+vendorName;
    }
    
    @Column(name="address3")
    public String getAddress3() {
        return this.address3;
    }
    
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    
    @Column(name="cell_phone", length=25)
    public String getCellPhone() {
        return this.cellPhone;
    }
    
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
    
    @Column(name="email1", length=128)
    public String getEmail1() {
        return this.email1;
    }
    
    public void setEmail1(String email1) {
        this.email1 = email1;
    }
    
    @Column(name="email2", length=128)
    public String getEmail2() {
        return this.email2;
    }
    
    public void setEmail2(String email2) {
        this.email2 = email2;
    }
    
    @Column(name="code", length=50)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="vendor_name", length=100)
    public String getVendorName() {
        return this.vendorName;
    }
    
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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
    
    @Column(name="shipping_company", length=100)
    public String getShippingCompany() {
        return this.shippingCompany;
    }
    
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    
    @Column(name="terms", length=50)
    public String getTerms() {
        return this.terms;
    }
    
    public void setTerms(String terms) {
        this.terms = terms;
    }
    
    @Column(name="home_phone", length=25)
    public String getHomePhone() {
        return this.homePhone;
    }
    
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    
    @Column(name="account_number", length=100)
    public String getAccountNumber() {
        return this.accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="vendor")
    public Set<BreakReceived> getBreakReceiveds() {
        return this.breakReceiveds;
    }
    
    public void setBreakReceiveds(Set<BreakReceived> breakReceiveds) {
        this.breakReceiveds = breakReceiveds;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="vendor")
    public Set<VendorSkidType> getVendorSkidTypes() {
        return this.vendorSkidTypes;
    }
    
    public void setVendorSkidTypes(Set<VendorSkidType> vendorSkidTypes) {
        this.vendorSkidTypes = vendorSkidTypes;
    }
    @OneToMany(fetch=FetchType.LAZY, mappedBy="vendor")
    public Set<Received> getReceiveds() {
        return this.receiveds;
    }
    
    public void setReceiveds(Set<Received> receiveds) {
        this.receiveds = receiveds;
    }

    @Transient
    public List<VendorSkidType> getVendorSkidTypesOrdered() {
        List<VendorSkidType> sorted = new ArrayList<VendorSkidType>(vendorSkidTypes);
        Collections.sort(sorted);
        return sorted;
    }
    
    @Transient
    public String getShippingViewDisplay(){
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    @Transient
    public String getShippingDisplay(){
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    @Override
    @Transient
    public int compareTo(Vendor vend) {
        return this.code.compareTo(vend.getCode());
    }

    @Transient
    public String getCodePlusName(){
        if (code != null){
            return code+" - "+vendorName;
        }
        return vendorName;
    }
}


