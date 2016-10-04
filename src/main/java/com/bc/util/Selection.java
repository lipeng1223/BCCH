package com.bc.util;

public class Selection implements Selectable {
    public static final String NULL = "<null>";
    
    private String id;
    private String displayName;

    public Selection() { }
    
    public Selection(String id, String displayName) {
        super();
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDisplayName();
    }

    public static boolean containsNull(Object value) {
        if (value instanceof String[]) {
            for (String v : (String[])value)
                if (isNull(v))
                    return true;
        }
        return false;
    }

    public static boolean isNull(Object value) {
        return (value instanceof String && NULL.equals(value)) ||
            (value instanceof String[] && ((String[])value).length == 1 &&
                NULL.equals(((String[])value)[0]));
    }
}
