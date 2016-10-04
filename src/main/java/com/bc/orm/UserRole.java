package com.bc.orm;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="userrole")
@Inheritance(strategy=InheritanceType.JOINED)
public class UserRole extends BaseEntity implements Serializable, Comparable<UserRole> {

    private User user;
    private String role;
    private String username;

    public UserRole() {
    }
    
    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("username", username));
        sb.append(getColAudit("role", role));
        return sb.toString();
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    @ManyToOne(fetch=FetchType.LAZY)
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(UserRole ur) {
        return this.role.compareTo(ur.getRole());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}


