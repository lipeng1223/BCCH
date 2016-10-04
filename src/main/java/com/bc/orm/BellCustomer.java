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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_customer")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellCustomer extends BaseEntity implements Serializable, Comparable<BellCustomer> {

    private String code;
    private String contactName;
    private String companyName;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String homePhone;
    private String workPhone;
    private String fax;
    private String terms;
    private Integer discount;
    private Boolean maillist;
    private Boolean tax;
    private String comment1;
    private String comment2;
    private String picklistComment;
    private Boolean bookclub;
    private Boolean bookfair;
    private String email1;
    private String email2;
    private String cellPhone;
    private Integer backorder;
    private String salesRep;
    private Integer hold;
    private Integer avedays;
    private Float creditLimit;
    private Date lastActivity;
    private Float balance;
    private Float salesYtd;
    private Float salesPyr;
    private Set<BellOrder> bellOrders = new HashSet<BellOrder>(0);
    private Set<BellCustomerShipping> bellCustomerShippings = new HashSet<BellCustomerShipping>(0);

    public BellCustomer() {
    }
    
    @Override
    public int compareTo(BellCustomer cust) {
        return this.companyName.compareTo(cust.getCompanyName());
    }

    @Column(name="code", length=50)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="contact_name", length=100)
    public String getContactName() {
        return this.contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    @Column(name="company_name", length=100)
    public String getCompanyName() {
        return this.companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
    
    @Column(name="terms", length=50)
    public String getTerms() {
        return this.terms;
    }
    
    public void setTerms(String terms) {
        this.terms = terms;
    }
    
    @Column(name="discount")
    public Integer getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
    
    @Column(name="maillist", length=1)
    public Boolean getMaillist() {
        return this.maillist;
    }
    
    public void setMaillist(Boolean maillist) {
        this.maillist = maillist;
    }
    
    @Column(name="tax", length=1)
    public Boolean getTax() {
        return this.tax;
    }
    
    public void setTax(Boolean tax) {
        this.tax = tax;
    }
    
    @Column(name="comment1", length=65535)
    public String getComment1() {
        return this.comment1;
    }
    
    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }
    
    @Column(name="comment2", length=65535)
    public String getComment2() {
        return this.comment2;
    }
    
    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }
    
    @Column(name="picklist_comment", length=65535)
    public String getPicklistComment() {
        return this.picklistComment;
    }
    
    public void setPicklistComment(String picklistComment) {
        this.picklistComment = picklistComment;
    }
    
    @Column(name="bookclub", length=1)
    public Boolean getBookclub() {
        return this.bookclub;
    }
    
    public void setBookclub(Boolean bookclub) {
        this.bookclub = bookclub;
    }
    
    @Column(name="bookfair", length=1)
    public Boolean getBookfair() {
        return this.bookfair;
    }
    
    public void setBookfair(Boolean bookfair) {
        this.bookfair = bookfair;
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
    
    @Column(name="cell_phone", length=25)
    public String getCellPhone() {
        return this.cellPhone;
    }
    
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
    
    @Column(name="backorder")
    public Integer getBackorder() {
        return this.backorder;
    }
    
    public void setBackorder(Integer backorder) {
        this.backorder = backorder;
    }
    
    @Column(name="sales_rep", length=50)
    public String getSalesRep() {
        return this.salesRep;
    }
    
    public void setSalesRep(String salesRep) {
        this.salesRep = salesRep;
    }
    
    @Column(name="hold")
    public Integer getHold() {
        return this.hold;
    }
    
    public void setHold(Integer hold) {
        this.hold = hold;
    }
    
    @Column(name="avedays")
    public Integer getAvedays() {
        return this.avedays;
    }
    
    public void setAvedays(Integer avedays) {
        this.avedays = avedays;
    }
    
    @Column(name="credit_limit", precision=10)
    public Float getCreditLimit() {
        return this.creditLimit;
    }
    
    public void setCreditLimit(Float creditLimit) {
        this.creditLimit = creditLimit;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_activity", length=19)
    public Date getLastActivity() {
        return this.lastActivity;
    }
    
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    @Column(name="balance", precision=10)
    public Float getBalance() {
        return this.balance;
    }
    
    public void setBalance(Float balance) {
        this.balance = balance;
    }
    
    @Column(name="sales_ytd", precision=10)
    public Float getSalesYtd() {
        return this.salesYtd;
    }
    
    public void setSalesYtd(Float salesYtd) {
        this.salesYtd = salesYtd;
    }
    
    @Column(name="sales_pyr", precision=10)
    public Float getSalesPyr() {
        return this.salesPyr;
    }
    
    public void setSalesPyr(Float salesPyr) {
        this.salesPyr = salesPyr;
    }
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="bellCustomer")
    public Set<BellOrder> getBellOrders() {
        return this.bellOrders;
    }
    
    public void setBellOrders(Set<BellOrder> bellOrders) {
        this.bellOrders = bellOrders;
    }


    @OneToMany(fetch=FetchType.LAZY, mappedBy="bellCustomer")
    public Set<BellCustomerShipping> getBellCustomerShippings() {
        return this.bellCustomerShippings;
    }
    
    public void setBellCustomerShippings(Set<BellCustomerShipping> bellCustomerShippings) {
        this.bellCustomerShippings = bellCustomerShippings;
    }


}


