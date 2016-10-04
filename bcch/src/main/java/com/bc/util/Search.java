package com.bc.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.table.Table;

public class Search {
    
    private Logger log = Logger.getLogger(Search.class);

    public enum Modifier {
        BEGINS_WITH, ENDS_WITH, LIKE, NOT_LIKE, EQUALS, NOT_EQUAL, GT, LT, GTE, LTE; 
    }
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    
    private String[] names;
    private String[] andOrs;
    private Modifier[] modifiers;
    private String[] values;
    
    private String isbn;
    private String multiIsbn;
    private String otherName;
    private String otherValue;
    private Modifier modifier;
    
    private Boolean includeBell;
    private Boolean includeRestricted;
    private Boolean includeHigherEducation;

    
    private String dateFrom;
    private String dateTo;
    private String minQty, maxQty;

   
    
    public Criterion getRestriction(Table tableConfig, String col, String val){
        return getRestriction(tableConfig, col, val, modifier);
    }

    public Criterion getRestriction(Table tableConfig, String col, String val, Modifier m){
        if (m != null){
            try {
                Object ob = val;
                String ctype = tableConfig.getColumnType(col);
                if (ctype != null){
                    if (ctype.equals("date")){
                        Date d = sdf.parse(val);
                        // if we actually parse it we want to use the date as the ob
                        ob = d;
                    } else if (ctype.equals("int")){
                        ob = Integer.parseInt(val);
                    } else if (ctype.equals("long")){
                        ob = Long.parseLong(val);
                    } else if (ctype.equals("float")){
                        ob = Float.parseFloat(val);
                    } else if (ctype.equals("boolean")){
                        ob = Boolean.valueOf(val);
                    }
                }
                //log.info("ctype: "+ctype);
                //log.info("modifier: "+m);
                //log.info("equals like: "+(m.equals(Modifier.LIKE)));
                if (m.equals(Modifier.EQUALS))
                    return Restrictions.eq(col, ob);
                else if (m.equals(Modifier.LIKE))
                    return Restrictions.ilike(col, val, MatchMode.ANYWHERE);
                else if (m.equals(Modifier.NOT_LIKE))
                    return Restrictions.not(Restrictions.ilike(col, val, MatchMode.ANYWHERE));
                else if (m.equals(Modifier.BEGINS_WITH))
                    return Restrictions.ilike(col, val, MatchMode.START);
                else if (m.equals(Modifier.ENDS_WITH))
                    return Restrictions.ilike(col, val, MatchMode.END);
                else if (m.equals(Modifier.NOT_EQUAL))
                    return Restrictions.ne(col, ob);
                else if (m.equals(Modifier.GT))
                    return Restrictions.gt(col, ob);
                else if (m.equals(Modifier.LT))
                    return Restrictions.lt(col, ob);
                else if (m.equals(Modifier.GTE))
                    return Restrictions.ge(col, ob);
                else if (m.equals(Modifier.LTE))
                    return Restrictions.le(col, ob);
            } catch (Exception e){
                log.error("Bad modifier: "+m+" for col: "+col+" val: "+val+" "+e.getMessage());
                return null;
            }
        }
        // default to equal
        return Restrictions.eq(col, val);
    }
    
    public List<Criterion> getRestrictions(Table tableConfig){
        return getRestrictions(tableConfig, null);
    }
    public List<Criterion> getRestrictions(Table tableConfig, String onlyName){
        List<Criterion> ands = new ArrayList<Criterion>();
        List<Criterion> ors = new ArrayList<Criterion>();
        if (names != null && modifiers != null && values != null && names.length == modifiers.length && modifiers.length == values.length) {
            boolean takeFromAnd = false;
            for (int i = 0; i < names.length; i++){
                String n = names[i];
                Modifier m = modifiers[i];
                String v = values[i];
                Boolean and = true;
                if (i > 0 && andOrs[i-1].equals("OR")) and = false;
                if (v == null || v.length() == 0) continue;
                
                if (onlyName != null && !n.startsWith(onlyName)){
                    continue;
                } else if (onlyName != null && n.startsWith(onlyName)){
                    n = n.substring(onlyName.length());
                }
                
                if (n.contains("isbn") && IsbnUtil.isValid(v)){
                    Disjunction dis = Restrictions.disjunction();
                    if (IsbnUtil.isValid10(v)) {
                        Criterion c = getRestriction(tableConfig, n, v, m);
                        if (c != null)
                            dis.add(c);
                        Criterion c2 = getRestriction(tableConfig, n, IsbnUtil.getIsbn13(v), m);
                        if (c2 != null)
                            dis.add(c2);
                    } else {
                        Criterion c = getRestriction(tableConfig, n, v, m);
                        if (c != null)
                            dis.add(c);
                        Criterion c2 = getRestriction(tableConfig, n, IsbnUtil.getIsbn10(v), m);
                        if (c2 != null)
                            dis.add(c2);
                    }
                    if (and)
                        ands.add(dis);
                    else
                        ors.add(dis);
                } else {
                    Criterion r = getRestriction(tableConfig, n, v, m);
                    if (r != null){
                        if (and) {
                            ands.add(r);
                            takeFromAnd = true;
                        }
                        else {
                            if (takeFromAnd) {
                                takeFromAnd = false;
                                ors.add(ands.remove(ands.size()-1));
                            }
                            ors.add(r);
                        }
                    }
                }
            }
        }
        if (ors.size() == 0) return ands;
        
        //Conjunction con = Restrictions.conjunction();
        List<Criterion> restrictions = new ArrayList<Criterion>();
        for (Criterion c : ands) {
            restrictions.add(c);
            //con.add(c);
            //log.error("AND: "+c.toString());
        }
        //log.error("Ands added to conjunction: "+ands.size());
        Disjunction dis = Restrictions.disjunction();
        //dis.add(con);
        for (Criterion c : ors) {
            dis.add(c);
            //log.error("OR: "+c.toString());
        }
        //log.error("Ors added to disjunction: "+ors.size());
        restrictions.add(dis);
        
        //log.error("restriction size: "+restrictions.size());
        return restrictions;
    }
    
    public String getQueryString(){
        StringBuilder sb = new StringBuilder();
        if (names != null && modifiers != null && values != null && names.length == modifiers.length && modifiers.length == values.length) {
            boolean amp = false;
            for (int i = 0; i < names.length; i++){
                if (amp) sb.append("&");
                sb.append("search.names=");
                sb.append(names[i]);
                sb.append("&search.modifiers=");
                sb.append(modifiers[i]);
                if (i > 0){
                    sb.append("&search.andOrs=");
                    sb.append(andOrs[i-1]);
                }
                sb.append("&search.values=");
                sb.append(StringEscapeUtils.escapeHtml(values[i]));
                amp = true;
            }
        }
        
        return sb.toString();
    }
    
    public String getIsbnEscaped(){
        return StringEscapeUtils.escapeHtml(isbn);
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getOtherNameEscaped() {
        return StringEscapeUtils.escapeHtml(otherName);
    }
    public String getOtherName() {
        return otherName;
    }
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }
    public String getOtherValueEscaped(){
        return StringEscapeUtils.escapeHtml(otherValue);
    }
    public String getOtherValue() {
        return otherValue;
    }
    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }
    public String getMultiIsbnEscaped(){
        return StringEscapeUtils.escapeHtml(multiIsbn.replace("\n", ";").replace("\r", ";").replace(";;", ";"));
    }
    public String getMultiIsbn() {
        return multiIsbn;
    }
    public void setMultiIsbn(String multiIsbn) {
        this.multiIsbn = multiIsbn;
    }
    public Boolean getIncludeBell() {
        return includeBell;
    }
    public void setIncludeBell(Boolean includeBell) {
        this.includeBell = includeBell;
    }
    public Boolean getIncludeRestricted() {
        return includeRestricted;
    }
    public void setIncludeRestricted(Boolean includeRestricted) {
        this.includeRestricted = includeRestricted;
    }
    public Boolean getIncludeHigherEducation() {
        return includeHigherEducation;
    }
    public void setIncludeHigherEducation(Boolean includeHigherEducation) {
        this.includeHigherEducation = includeHigherEducation;
    }
    public Modifier getModifier() {
        return modifier;
    }
    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(Modifier[] modifiers) {
        this.modifiers = modifiers;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String[] getAndOrs() {
        return andOrs;
    }

    public void setAndOrs(String[] andOrs) {
        this.andOrs = andOrs;
    }
    
    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
    
    public String getMinQty() {
        return minQty;
    }

    public void setMinQty(String minQty) {
        this.minQty = minQty;
    }

    public String getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(String maxQty) {
        this.maxQty = maxQty;
    }

}
