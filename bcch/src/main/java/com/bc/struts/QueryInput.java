package com.bc.struts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import com.bc.util.Selection;

public class QueryInput implements Serializable {
    
    private transient Logger logger = Logger.getLogger(QueryInput.class);
    
    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";
    
    private SimpleDateFormat inputSdf = new SimpleDateFormat("MM/dd/yyyy");
    
    private ArrayList<HashMap<String, Object>> filterParams;
    
    private Integer start = null;  // page offset
    private Integer limit = null;  // page size
    private String groupBy = null; // grouping
    private String sortDir = null; // sort direction
    private String sortCol = null; // sort column
    private String sortDir2 = null; // sort direction 2
    private String sortCol2 = null; // sort column 2
    private List<Criterion> andCriterions = new ArrayList<Criterion>();
    private List<Criterion> orCriterions = new ArrayList<Criterion>();
    private Boolean exportToExcel = false; // only true if this is an export to excel
    private String searchString = null;
    
    public Boolean hasSortCol(){
        return sortCol != null;
    }
    
    public Boolean hasSortCol2(){
        return sortCol2 != null;
    }
    
    public Boolean hasGroupBy(){
        return groupBy != null;
    }
    
    public Boolean isPagingEnabled(){
        return getStart() != null && getStart() >= 0 && getLimit() != null && getLimit() > 0;
    }
    
    public QueryInput addAndCriterion(Criterion crit){
        andCriterions.add(crit);
        return this;
    }
    
    public QueryInput addOrCriterion(Criterion crit){
        orCriterions.add(crit);
        return this;
    }
    
    public List<Criterion> getAndCriterions() {
        return andCriterions;
    }

    public void setAndCriterions(List<Criterion> andCriterions) {
        this.andCriterions = andCriterions;
    }

    public List<Criterion> getOrCriterions() {
        return orCriterions;
    }

    public void setOrCriterions(List<Criterion> orCriterions) {
        this.orCriterions = orCriterions;
    }

    public QueryInput(){
        filterParams = new ArrayList< HashMap<String,Object>>();
    }
    
    public QueryInput(Integer start, Integer limit){
        this();
        this.start = start;
        this.limit = limit;
    }
    
    public List<HashMap<String, Object>> getFilterParams() {
        return filterParams;
    }
    public void addFilterParams(HashMap<String, Object> filterParam) {
        this.filterParams.add(filterParam);
    }
    public Integer getStart() {
        if (exportToExcel) return null;
        return start;
    }
    public void setStart(Integer start) {
        this.start = start;
    }
    public Integer getLimit() {
        if (exportToExcel) return null;
        return limit;
    }
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    public String getSortDir() {
        return sortDir;
    }
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
    public String getSortCol() {
        return sortCol;
    }
    public void setSortCol(String sortCol) {
        this.sortCol = sortCol;
    }

    
    public HashSet<String> applyFilterParams(Criteria crit, Map<String,String> fieldMap) {
        HashSet<String> aliases = new HashSet<String>();
        for (HashMap<String, Object> fvals : getFilterParams()){
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("filter param info: ");
                    logger.debug("fvals.get(field): " + fvals.get("field"));
                    logger.debug("fvals.get(type): " + fvals.get("type"));
                    logger.debug("fvals.get(className): " + fvals.get("className"));
                    logger.debug("fvals.get(value): " + fvals.get("value"));
                    logger.debug("fvals.get(comparison): " + fvals.get("comparison"));
                }
                String name = (String) fvals.get("field");
                String field = aliasIfNeeded(fieldMap != null && fieldMap.containsKey(name) ?
                        fieldMap.get(name) : name, crit, aliases);
                Criterion newcrit =createCriterion((String) fvals.get("type"), field,
                        (String) fvals.get("className"), fvals.get("value"),
                        (String) fvals.get("comparison"));
                if (newcrit != null)
                    crit.add(newcrit);
                logger.debug("added criterion: " + crit.toString());
            } catch (Exception e) {
                logger.warn("Couldn't parse filter restrictions:" + fvals, e);
            }
        }
        return aliases;
    }

    
    private String aliasIfNeeded(String field, Criteria crit, HashSet<String> aliases) {
        String DOT = ".";
        int lastDot = field.lastIndexOf(DOT);
        if (lastDot > 0) {
            String sub = field.substring(0, lastDot);
            String fieldOnly = field.substring(lastDot);
            
            String assoc = null, alias = null;
            for (String part:  sub.split("\\.")) {
                assoc = ((alias == null) ? part : assoc + "." + part);
                if (!aliases.contains(assoc)){
                    String[] parts = assoc.split("\\.");
                    alias = parts[parts.length-1];
                    aliases.add(alias);
                    crit.createAlias(assoc, alias);
                    if (logger.isDebugEnabled()){
                        logger.debug("alias "+assoc+" as "+alias);
                    }
                }
            }

            return alias + fieldOnly;
        }
        return field;
    }

    @SuppressWarnings("unchecked")
    private Criterion createCriterion(String type, String field, String className, Object value, String comparison) throws ParseException {
        if ("string".equals(type)) {
            if (comparison != null) {
                if ("eq".equalsIgnoreCase(comparison)) {
                    String s = value.toString();
                    if (s.startsWith("!")) {
                        s = s.substring(1);
                        return Restrictions.ne(field, value.toString());
                    } else {
                        return Restrictions.eq(field, value.toString());
                    }
                } else if ("in".equalsIgnoreCase(comparison)) {
                    // NB: value should be a Collection or Object[]
                    if (value instanceof Collection)
                        return Restrictions.eq(field, (Collection)value);
                    else
                        return Restrictions.eq(field, (Object[])value);
                }
            }
            return Restrictions.ilike(field, "%" + value.toString() + "%");
        } else if ("boolean".equals(type)){
            return Restrictions.eq(field, Boolean.valueOf((String) value));
        } else if ("intin".equals(type)){
            List<Integer> vals = new ArrayList<Integer>();
            for (String val : (List<String>) value) {
                vals.add(Integer.valueOf(val));
            }
            return Restrictions.in(field, vals);
        } else if ("int".equals(type)){
            return Restrictions.eq(field, Integer.valueOf((String) value));

        } else if ("numeric".equals(type) || "integer".equals(type) ||
                   "long".equals(type) ||"bigdecimal".equals(type) ||
                   "float".equals(type) || "double".equals(type) ||
                   "size".equals(type)) {
            boolean isSize = "size".equals(type);
            Object newValue = null;
            if ("numeric".equals(type)) {
                newValue = value; //this is what was occurring before adding in new types
            } else if ("integer".equals(type) || "size".equals(type))  {
                newValue = Integer.valueOf((String)value);
            } else if ("long".equals(type)) {
                newValue = Long.valueOf((String)value);
            } else if ("bigdecimal".equals(type)) {
                newValue = new BigDecimal((String)value);
            } else if ("double".equals(type)) {
                newValue = Double.valueOf((String)value);
            } else if ("float".equals(type)) {
                newValue = Float.valueOf((String)value);
            }
            //logger.debug(String.format("value is %1$s newValue is %2$s",value,newValue));
            if ("gt".equals(comparison)) {
                return isSize ? Restrictions.sizeGt(field, (Integer)newValue) : Restrictions.gt(field, newValue);
            } else if ("lt".equals(comparison)) {
                return isSize ? Restrictions.sizeLt(field, (Integer)newValue) : Restrictions.lt(field, newValue);
            } else if ("eq".equals(comparison)) {
                return isSize ? Restrictions.sizeEq(field, (Integer)newValue) : Restrictions.eq(field, newValue);
            } else {
                throw new IllegalArgumentException("Invalid comparison:" + comparison);
            }
        } else if ("date".equals(type)){
            long oneDay = 1000L * 60 * 60 * 24;

            Date thedate = inputSdf.parse((String) value);

            if ("gt".equals(comparison)) {
                // use ge the following day
                return Restrictions.ge(field, new Date(thedate.getTime() + oneDay));
            } else if ("lt".equals(comparison)) {
                return Restrictions.lt(field, thedate);
            } else if ("eq".equals(comparison)) {
                // note mysql is inclusive, so don't use next day as outer bounds (subtract 1)
                return Restrictions.between(field, thedate, new Date(thedate.getTime() + oneDay - 1));
            } else {
                throw new IllegalArgumentException("Invalid comparison:" + comparison);
            }
        //} else if ("list".equals(type)) {
            /*
            // TODO: Provide general ConvertUtils class?
            try {
                if (StringUtils.isNotBlank(className)) {
                    Class<?> clazz = Class.forName(className);
                    String name = field;
                    Object converted = null;
                    if (BaseEntity.class.isAssignableFrom(clazz)) {
                        name = field+".oid";
                        converted = BaseEntity.convertFilterValues((String[])value);
                    } else if (Enum.class.isAssignableFrom(clazz)) {
                        converted = EnumUtil.convert((Class<? extends Enum>)clazz, (String[])value);
                    } else {
                        converted = value;
                    }
                    return eqOrIn(name, converted);
                }
            } catch (Exception e) {
                logger.warn("Exception converting filter values for: "+className, e);
            }
            // Defaults to no conversion
            return eqOrIn(field, value);
            */
        } else {
            throw new IllegalArgumentException("Invalid type:" + type + " -valid types: list, date, string, integer, long, double, float, bigdecimal, int, & numeric (which is integer)");
        }
    }

    @SuppressWarnings("unchecked")
    private Criterion eqOrIn(String field, Object values) {
        if (values == null || Selection.isNull(values))
            return Restrictions.isNull(field);
        if (values instanceof String[]) {
            if (Selection.containsNull(values)) {
                Junction crit = Restrictions.disjunction()
                    .add(Restrictions.isNull(field));
                List<String> notNull = new ArrayList<String>();
                for (String v : (String[])values) {
                    if (! Selection.isNull(v))
                        notNull.add(v);
                }
                return crit.add(notNull.size() > 1 ? Restrictions.in(field, notNull) :
                    Restrictions.eq(field,notNull.get(0)));
            }
            return Restrictions.in(field, (String[])values) ;
        }
        if (values instanceof Object[])
            return Restrictions.in(field, (Object[])values) ;
        if (values instanceof Collection)
            return Restrictions.in(field, (Collection)values) ;
        return Restrictions.eq(field, values) ;
    }
    
    
    public Boolean getExportToExcel() {
        return exportToExcel;
    }

    public void setExportToExcel(Boolean exportToExcel) {
        this.exportToExcel = exportToExcel;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSortDir2() {
        return sortDir2;
    }

    public void setSortDir2(String sortDir2) {
        this.sortDir2 = sortDir2;
    }

    public String getSortCol2() {
        return sortCol2;
    }

    public void setSortCol2(String sortCol2) {
        this.sortCol2 = sortCol2;
    }
    
}
