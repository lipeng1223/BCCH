package com.bc.orm;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.log4j.Logger;

import com.bc.util.EscapeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

@MappedSuperclass
@EntityListeners({BaseEntityListener.class})
public abstract class BaseEntity implements Cloneable {

	private Logger logger = Logger.getLogger(BaseEntity.class);
	
    @Expose protected Long id;
    @Expose protected Long version;
    @Expose protected Date lastUpdate;
    @Expose protected String lastUpdateBy;
    @Expose protected Date createTime;
    //protected String createdBy;
    
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", nullable=false)    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public String getLastUpdateBy() {
        return lastUpdateBy;
    }
    
    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    @Column(name="createTimeBc")
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    // TODO update the db to what is on the currrent dev server and switch this to versionbc
    @Version
    @Column(name="versionbc")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Transient
    public String getCreateTimeFormatted(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        return sdf.format(createTime);
    }

    /*
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    */
    
    @Transient
    public String formatPercent(Double d){
        if (d == Double.NEGATIVE_INFINITY) d = 0D;
        else if (d == Double.POSITIVE_INFINITY) d = 0D;
        NumberFormat nf = DecimalFormat.getPercentInstance();
        nf.setMaximumFractionDigits(0);
        return nf.format(d);
    }

    @Transient
    public String formatPercent(BigDecimal bd){
        if (bd == null) bd = BigDecimal.valueOf(0D);
        return formatPercent(bd.doubleValue());
    }

    @Transient
    public String formatMoney(Double d){
        if (d == Double.NEGATIVE_INFINITY) d = 0D;
        else if (d == Double.POSITIVE_INFINITY) d = 0D;
        DecimalFormat df = new DecimalFormat("$#,##0.00;-$#,##0.00");
        return df.format(d);
    }
    
    @Transient
    public String formatMoney(BigDecimal bd){
        if (bd == null) bd = BigDecimal.valueOf(0D);
        return formatMoney(bd.doubleValue());
    }
    
    @Transient 
    public String formatTwoDecimals(BigDecimal bd){
        if (bd == null) bd = BigDecimal.valueOf(0d);
        return formatTwoDecimals(bd.doubleValue());
    }

    @Transient 
    public String formatTwoDecimals(Double d){
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        return nf.format(d);
    }
    

    @Transient 
    public String formatNoDecimals(BigDecimal bd){
        if (bd == null) bd = BigDecimal.valueOf(0d);
        return formatNoDecimals(bd.doubleValue());
    }

    @Transient 
    public String formatNoDecimals(Double d){
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);
        return nf.format(d);
    }
    
    @Transient
    public Double zeroMin(Double d){
        if (d < 0D){
            return 0D;
        }
        return d;
    }    
    
    @Transient
    public Double zeroMin(Float f){
        if (f < 0F){
            return 0D;
        }
        return f.doubleValue();
    }    
    
    @Transient
    public Double zeroMin(Integer i){
        if (i < 0){
            return 0D;
        }
        return i.doubleValue();
    }    
    
    @Transient
    public Double zeroMin(BigDecimal bd){
        if (bd.doubleValue() < 0D){
            return 0D;
        }
        return bd.doubleValue();
    }    
    
    @Transient
    public String escapeJavaScript(String str){
        return EscapeUtil.escapeJavaScript(str);
    }
    
    @Transient
    public String escapeHtml(String str){
        return EscapeUtil.escapeHtml(str);
    }

    @Transient
    public String getEntityJson(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
    
    // override for more information to the audit message
    @Transient 
    public String getAuditMessage(){
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    protected String getColAudit(String col, Object val){
        StringBuilder sb = new StringBuilder("");
        if (val != null){
            sb.append(col);
            sb.append(": ");
            sb.append(val);
            sb.append(", ");
        }
        return sb.toString();
    }
    
}
