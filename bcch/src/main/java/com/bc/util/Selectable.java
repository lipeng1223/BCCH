package com.bc.util;

/**
 * This interface is used to designate objects that are used for selection lists.
 * The <code>id</code> is used to identify the record, while the <code>text</code> is used
 * as the display text for the user selection.
 * 
 */
public interface Selectable {
    /**
     * @return the id
     */
    public String getId();

    /**
     * @return the user display text
     */
    public String getDisplayName();
    
    /*
     * Optionally, the following static method can be defined to provide a textual
     * representation of the specified selected values.  EnumUtil provides an
     * implementation for all Enums, so this method only needs to be implemented
     * by Selectables that are not Enums.
     * 
     * public static String getFilterText(String ... ids);
     */
}
