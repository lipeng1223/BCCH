package com.bc.excel;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class ExcelDataReader implements Iterator
{
    private static final Logger log = Logger.getLogger(ExcelDataReader.class);
    private List data;
    private int idx = 0;

    /**
     *  Default ctor.
     */

    public ExcelDataReader () { }

    public ExcelDataReader (List data) {
        setDataList(data);
    }

    public int getCurrentIndex() { return idx; }

    /**
     * Set the data list to be processed by the reader
     * @param data
     */
    public void setDataList(List data) { this.data = data; this.idx = 0; }

    /**
     * Check if the data reader has any more items to process
     * @return boolean true if the data reader has more items, false otherwise
     */
    public boolean hasNext() {
        if (data == null)
            return false;
        return idx < data.size();
    }

    /**
     * Advance the data reader to the next item
     */
    public Object next() {
        Object o = data.get(idx);
        idx++;
        return o;
    }

    /**
     * Get a property value from the data reader's current item
     * @param excelCol the ExcelColumn
     * @param propertyName the name of the property value to be read
     * @return String the value of the property.  May be null
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public String getPropertyValue(ExcelColumn excelCol,
                                   String propertyName)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
    	try {
            // This is a hack so we can get dynamic attribute columns in place
            if (propertyName.startsWith("attribute-")){
                String attname = propertyName.substring(propertyName.indexOf("-")+1);
                attname = attname.replace("_sp_", " ");
                Set<Object> atts = (Set<Object>)PropertyUtils.getProperty(data.get(idx), "attributes");
                for(Object ob : atts){
                    if (attname.equals(PropertyUtils.getProperty(ob, "name"))){
                        return PropertyUtils.getProperty(ob, "value").toString();
                    }
                }
                return "";
            }
            Object result = null;
            if(!excelCol.isIgnoreValInExport()) {
                result = PropertyUtils.getProperty(data.get(idx), propertyName);
            }
    		if (result == null) return "";
    		return result.toString();
    	} catch(NestedNullException nne) {
    		// Handle case of null company, etc
    		return "";
    	} catch (NoSuchMethodException nsme){
    	    return "";
    	}
    }

    public void remove() {}
}                                    // ExcelDataReader