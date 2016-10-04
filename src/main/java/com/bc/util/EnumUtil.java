package com.bc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bc.util.ObjectToStringComparator;
import com.bc.util.Selectable;
import com.bc.util.SelectableComparator;


public class EnumUtil {
    private static Logger logger = Logger.getLogger(EnumUtil.class);

    public final static Comparator<Object> DEFAULT_COMPARATOR = new ObjectToStringComparator();
    public final static Comparator<Selectable> SELECTABLE_COMPARATOR = new SelectableComparator();

    @SuppressWarnings("unchecked")
    private final static Map<Class<? extends Enum>,Collection<Enum>> allEnums = new HashMap<Class<? extends Enum>,Collection<Enum>>();
    @SuppressWarnings("unchecked")
    private final static Map<Class<? extends Enum>,Collection<Enum>> enums = new HashMap<Class<? extends Enum>,Collection<Enum>>();
    @SuppressWarnings("unchecked")
    private final static Map<Class<? extends Enum>,Map<Comparator,Collection<Enum>>> allOrderedEnums = new HashMap<Class<? extends Enum>,Map<Comparator,Collection<Enum>>>();
    @SuppressWarnings("unchecked")
    private final static Map<Class<? extends Enum>,Map<Comparator,Collection<Enum>>> orderedEnums = new HashMap<Class<? extends Enum>,Map<Comparator,Collection<Enum>>>();
    
    /**
     * Get enum constants in the order they were defined, excluding deprecated values.
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends Enum> getEnums(Class<? extends Enum> clazz) {
        return getEnums(clazz, false);
    }
    
    /**
     * Get enum constants in the order they were defined, optionally including deprecated values.
     * @param clazz
     * @param includeDeprecated
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends Enum> getEnums(Class<? extends Enum> clazz, boolean includeDeprecated) {
        Map<Class<? extends Enum>,Collection<Enum>> cache = includeDeprecated ? allEnums : enums;
        Collection<Enum> values = cache.get(clazz);
        if (values == null) {
            cache.put(clazz, values = new TreeSet<Enum>());
            for (Enum e : clazz.getEnumConstants()) {
                if (includeDeprecated || !isDeprecated(e))
                    values.add(e);
            }

        }
        return values;
    }
    
    /**
     * Get enum constants in order by their displayName/toString, excluding deprecated values.
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends Enum> getSortedEnums(Class<? extends Enum> clazz) {
        return getSortedEnums(clazz, Selectable.class.isAssignableFrom(clazz) ? SELECTABLE_COMPARATOR : DEFAULT_COMPARATOR, false);
    }
    
    /**
     * Get ordered enum constants, optionally including deprecated values.
     * @param clazz
     * @param comparator
     * @param includeDeprecated
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends Enum> getSortedEnums(Class<? extends Enum> clazz, Comparator comparator, boolean includeDeprecated) {
        Map<Class<? extends Enum>,Map<Comparator,Collection<Enum>>> cache = includeDeprecated ? allOrderedEnums : orderedEnums;
        Map<Comparator,Collection<Enum>> map = cache.get(clazz);
        if (map == null)
            cache.put(clazz, map = new HashMap<Comparator,Collection<Enum>>());
        Collection<Enum> values = map.get(comparator);
        if (values == null) {
            map.put(comparator, values = new TreeSet<Enum>(comparator));
            values.addAll(getEnums(clazz, includeDeprecated));
        }
        return values;
    }

    /**
     * @param e
     * @return true if the <code>Enum</code> has been annotated as deprecated.
     */
    public static boolean isDeprecated(Enum<?> e) {
        try {
            return e.getClass().getField(e.name()).isAnnotationPresent(Deprecated.class);
        } catch (Throwable t) { } // ignored;
        return false;
    }
    
    /**
     * @param clazz
     * @param names
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getFilterText(Class<? extends Enum> clazz, String ... names) {
        Object converted = convert(clazz, names);
        if (converted instanceof Collection) {
            converted = StringUtils.join((Collection)converted, "', '");
        }
        return new StringBuilder().append('\'').append(converted).append('\'').toString();
    }

    /**
     * @param clazz
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object convert(Class<? extends Enum> clazz, int ... ordinals) {
        List<Object> results = new ArrayList<Object>();
        Enum<? extends Enum>[] constants = clazz.getEnumConstants();
        for (int ordinal : ordinals) {
            try {
                results.add(constants[ordinal]);
            } catch (Exception e) {
                logger.warn("Could not find "+clazz.getName()+" with ordinal="+ordinal);
            }
        }
        return results.size() > 1 ? results : results.isEmpty() ? null : results.get(0);
    }

    /**
     * @param clazz
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object convert(Class<? extends Enum> clazz, String ... values) {
        List<Object> results = new ArrayList<Object>();
        for (String value : values) {
            try {
                if (value != null && !value.equals(""))
                    results.add(Enum.valueOf((Class)clazz, value));
            } catch (Exception e) {
                logger.warn("Could not convert "+clazz.getName()+"."+value);
            }
        }
        return results.size() > 1 ? results : results.isEmpty() ? null : results.get(0);
    }

}
