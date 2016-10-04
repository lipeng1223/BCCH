package com.bc.struts;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bc.dao.DaoResults;
import com.bc.table.ColumnModel;
import com.bc.table.Table;
import com.bc.table.ToolbarButton;
import com.bc.table.ToolbarMenu;

public class QueryResults {
    private static Logger logger = Logger.getLogger(QueryResults.class);

    private DaoResults results;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    private String filterText;
    private Table tableConfig;

    private String[] htmlEntities = new String[]{"&quot;","&amp;","&lt;","&gt;","&nbsp;","&iexcl;","&cent;","&pound;","&curren;","&yen;","&brvbar;","&sect;","&uml;","&copy;","&ordf;","&laquo;","&not;","&shy;","&reg;","&macr;","&deg;","&plusmn;","&sup2;","&sup3;","&acute;","&micro;","&para;","&middot;","&cedil;","&sup1;","&ordm;","&raquo;","&frac14;","&frac12;","&frac34;","&iquest;","&Agrave;","&Aacute;","&Acirc;","&Atilde;","&Auml;","&Aring;","&AElig;","&Ccedil;","&Egrave;","&Eacute;","&Ecirc;","&Euml;","&Igrave;","&Iacute;","&Icirc;","&Iuml;","&ETH;","&Ntilde;","&Ograve;","&Oacute;","&Ocirc;","&Otilde;","&Ouml;","&times;","&Oslash;","&Ugrave;","&Uacute;","&Ucirc;","&Uuml;","&Yacute;","&THORN;","&szlig;","&agrave;","&aacute;","&acirc;","&atilde;","&auml;","&aring;","&aelig;","&ccedil;","&egrave;","&eacute;","&ecirc;","&euml;","&igrave;","&iacute;","&icirc;","&iuml;","&eth;","&ntilde;","&ograve;","&oacute;","&ocirc;","&otilde;","&ouml;","&divide;","&oslash;","&ugrave;","&uacute;","&ucirc;","&uuml;","&yacute;","&thorn;","&yuml;","&euro;"};
    private String[] xmlEntities = new String[]{"&#34;","&#38;","&#60;","&#62;","&#160;","&#161;","&#162;","&#163;","&#164;","&#165;","&#166;","&#167;","&#168;","&#169;","&#170;","&#171;","&#172;","&#173;","&#174;","&#175;","&#176;","&#177;","&#178;","&#179;","&#180;","&#181;","&#182;","&#183;","&#184;","&#185;","&#186;","&#187;","&#188;","&#189;","&#190;","&#191;","&#192;","&#193;","&#194;","&#195;","&#196;","&#197;","&#198;","&#199;","&#200;","&#201;","&#202;","&#203;","&#204;","&#205;","&#206;","&#207;","&#208;","&#209;","&#210;","&#211;","&#212;","&#213;","&#214;","&#215;","&#216;","&#217;","&#218;","&#219;","&#220;","&#221;","&#222;","&#223;","&#224;","&#225;","&#226;","&#227;","&#228;","&#229;","&#230;","&#231;","&#232;","&#233;","&#234;","&#235;","&#236;","&#237;","&#238;","&#239;","&#240;","&#241;","&#242;","&#243;","&#244;","&#245;","&#246;","&#247;","&#248;","&#249;","&#250;","&#251;","&#252;","&#253;","&#254;","&#255;","&#8364;"};
        
    private QueryResults() {
        // no default 
    }
    
    public QueryResults(DaoResults results) {
        this.results = results;
    }
    
    public DaoResults getResults(){
        return results;
    }
    
    public List getData() {
        return Collections.unmodifiableList(this.results.getData());
    }
    
    public Integer getFirstResult() {
        return this.results.getFirstResult();
    }
    public Integer getMaxResults() {
        return this.results.getMaxResults();
    }
    public Integer getTotalRecords() {
        return this.results.getTotalRecords(); 
    }

    public Object getSummary(String name){
        if (results.getSummary() != null && results.getSummary().containsKey(name)){
            return results.getSummary().get(name);
        }
        return "";
    }
    
    public Object get(Object data, String name){
        try {
            // This is a hack so we can get dynamic attribute columns in place
            if (name.startsWith("attribute-")){
                String attname = name.substring(name.indexOf("-")+1);
                attname = attname.replace("_sp_", " ");
                Set<Object> atts = (Set<Object>)PropertyUtils.getProperty(data, "attributes");
                for(Object ob : atts){
                    if (attname.equals(PropertyUtils.getProperty(ob, "name"))){
                        return PropertyUtils.getProperty(ob, "value");
                    }
                }
                return "";
            }
            Object ob = PropertyUtils.getProperty(data, name);
            if (ob instanceof Date){
                return sdf.format((Date)ob);
            }
            if (ob instanceof String){
                ob = StringEscapeUtils.escapeXml((String)ob);
            }
            return ob;
        } catch (NestedNullException nne){
            // we will expect nested null exceptions
            return "";
        } catch (Throwable t){
            logger.error("Could not find data for column with name: "+name+" on "+data.getClass().getSimpleName());
            return "";
        }
    }
    
    public String getFilterText() {
        return filterText;
    }
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }
    
    public Table getTableConfig() {
        return tableConfig;
    }
    
    public void setTableConfig(Table tableConfig) {
        this.tableConfig = tableConfig;
    }
    
    public void setTableConfig(Table tableConfig, List<HashMap<String,Object>> filterParams) {
        setTableConfig(tableConfig, filterParams, null);
    }
    
    public void setTableConfig(Table tableConfig, List<HashMap<String,Object>> filterParams, String extraFilterText) {
        this.tableConfig = tableConfig;

        StringBuilder sb = new StringBuilder();
        StringBuilder rsb = new StringBuilder();
        for (HashMap<String, Object> filterMap : filterParams) {
            String type = (String) filterMap.get("type");
            String className = (String) filterMap.get("className");
            String fieldName = (String) filterMap.get("field");

            fieldName = fieldName.replace('.', '_');
            ColumnModel cm = tableConfig.getColumnModel(fieldName);
            String field = fieldName;
            if (cm != null){
                field = new StringBuilder().append('\'').append(cm.getHeader()).append('\'').toString();
            } else {
                // try and get the field name through the ToolbarMenu filterText
                String found = null;
                if (tableConfig.getHasToolbar()){
                    for (ToolbarButton tb : tableConfig.getToolbar().getButtons()) {
                        if (tb instanceof ToolbarMenu){
                            if (((ToolbarMenu)tb).getFilterText(fieldName) != null){
                                found = ((ToolbarMenu)tb).getFilterText(fieldName);
                            }
                        }
                    }
                    if (found != null) field = found;
                }
            }

            String value = String.valueOf(filterMap.get("value"));

            if ("string".equals(type)) {
                rsb.append(field).append(" contains ").append('\'').append(value).append('\'');
            } else if ("int".equals(type)){
                rsb.append(field).append(" is ").append(value);
            } else if ("intin".equals(type)){
                rsb.append(field).append(" is one of ").append(value);
            } else if ("numeric".equals(type) || "long".equals(type) || "float".equals(type) ||
                       "double".equals(type) || "integer".equals(type)  || "bigdecimal".equals(type) ||
                       "size".equals(type))
            {
                // if ("size".equals(type)) rsb.append(" size of ");
                String comparison = (String) filterMap.get("comparison");
                if ("gt".equals(comparison)) {
                    rsb.append(field).append(" is > ").append(value);
                } else if ("lt".equals(comparison)) {
                    rsb.append(field).append(" is < ").append(value);
                } else if ("eq".equals(comparison)) {
                    rsb.append(field).append(" is = ").append(value);
                }
            } else if ("date".equals(type)){
                String comparison = (String) filterMap.get("comparison");
                if ("gt".equals(comparison)) {
                    rsb.append(field).append(" is after ").append(value);
                } else if ("lt".equals(comparison)) {
                    rsb.append(field).append(" is before ").append(value);
                } else if ("eq".equals(comparison)) {
                    rsb.append(field).append(" is ").append(value);
                }
            } else if ("list".equals(type)) {
                /*
                StringBuilder lsb = new StringBuilder();
                Object[] values = (Object[])filterMap.get("value");
                // XXX: Should we limit display to first N values,
                //      or text size limit, or always display static text?
                boolean formatted = false;
                if (! StringUtils.isEmpty(className)) {
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(className);
                        if (Selectable.class.isAssignableFrom(clazz)) {
                            if (Enum.class.isAssignableFrom(clazz)) {
                                Class <? extends Enum> enumClass = (Class<? extends Enum>) clazz;
                                lsb.append(EnumUtil.getFilterText(enumClass,(String []) values));
                            } else {
                                Method m = clazz.getMethod("getFilterText", (new String[0]).getClass());
                                lsb.append(m.invoke(null, (Object)values));
                            }
                            
                            formatted = true;
                        }
                    } catch (ClassNotFoundException e) {
                        logger.warn("Exception caught for class: "+className, e);
                    } catch (Exception e) {
                        // Selectable is not required (and may not be able) to implement getFilterText
                        if (clazz != null) {
                            lsb.append(" the selected values");
                            formatted = true;
                        }
                        logger.info("list exception", e);
                    }
                }
                if (! formatted) {
                    lsb.append('\'').append(StringUtils.join(values, "', '")).append('\'');
                }
                rsb.append(field).append(" is one of ").append(lsb.toString());
                */
            } else if ("boolean".equals(type)){
                rsb.append(field);
                rsb.append(" is ");
                if ("true".equals(String.valueOf(filterMap.get("value")))){
                    rsb.append("Yes");
                } else {
                    rsb.append("No");
                }
            }

            if (rsb.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(" and ");
                }
                sb.append(rsb.toString());
                rsb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            this.filterText = "Current Filter:  " + sb.toString() + ((extraFilterText != null) ? " and " + extraFilterText : "");
        } else {
            this.filterText = ((extraFilterText != null) ? "Filter:  " + extraFilterText : " ");
        }
    }
}
