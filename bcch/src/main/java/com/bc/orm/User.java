package com.bc.orm;


import java.io.Serializable;
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

import org.hibernate.annotations.Cascade;

import com.bc.orm.UserRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name="user")
@Inheritance(strategy=InheritanceType.JOINED)
public class User extends BaseEntity implements Auditable, Serializable {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String password;
    private String confirmPassword;
    private Integer pin;
    private Boolean active = true;

    private Set<UserRole> roles;
    
    private Set<Audit> audits;
    
    public User() {
    }
    
    @Transient
    public String getAuditMessage(){
        return "username: "+username;
    }
	
    @Column(name="username", unique=true, nullable=false, length=64)
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Column(name="password", length=64)
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Column(name="pin")
    public Integer getPin() {
        return this.pin;
    }
    
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    @Transient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Transient
    public String getRolesDisplay(){
        StringBuilder sb = new StringBuilder();
        if (roles != null){
            boolean comma = false;
            List<UserRole> rolesList = new ArrayList<UserRole>(roles);
            Collections.sort(rolesList);
            for (UserRole ur : rolesList){
                if (comma) sb.append(", ");
                else comma = true;
                sb.append(ur.getRole());
            }
        }
        return sb.toString();
    }
    
    public Boolean hasRole(String r){
        if (roles != null) {
            for (UserRole ur : roles){
                if (ur.getRole().equals(r))
                    return true;
            }
        }
        return false;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="user")
    public Set<Audit> getAudits() {
        return audits;
    }

    public void setAudits(Set<Audit> audits) {
        this.audits = audits;
    }
}


