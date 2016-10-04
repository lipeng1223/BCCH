package com.bc.table;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;

import com.bc.util.EnumUtil;
import com.bc.util.Selectable;

public class Filter {

    private static Logger logger = Logger.getLogger(Filter.class);

    private String type; // required
    private String name; // required
    private Class<?> javaClass;
    private Object[] options;
    private String optionsJson;
    private String filterSettings;
    private String display; // only used by additional search options

    public Filter(String type, String name){
        this.type = type;
        this.name = name;
    }

    public Filter(String type, String name, Class<?> javaClass){
        this.type = type;
        this.name = name;
        this.javaClass = javaClass;

        if (Enum.class.isAssignableFrom(javaClass)) {
            setOptions(EnumUtil.getSortedEnums(javaClass.asSubclass(Enum.class)).toArray());
        }
    }

    public Filter(String type, String name, String display){
        this.type = type;
        this.name = name;
        this.display = display;
    }

    public String getType() {
        return type;
    }
    public Filter setType(String type) {
        this.type = type;
        return this;
    }
    public String getName() {
        return name;
    }
    public Filter setName(String name) {
        this.name = name;
        return this;
    }
    public Class<?> getJavaClass() {
        return javaClass;
    }
    public Filter setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
        return this;
    }
    public String getClassName() {
        return javaClass != null ? javaClass.getName() : "";
    }
    public Object[] getOptions() {
        return options;
    }
    public Filter setOptions(Object[] options) {
        if (options == null) {
            this.options = null;
            return this;
        }
        try {
            JSONWriter json = new JSONStringer().array();
            for (Object op : options) {
                if (op instanceof Selectable) {
                    json.object()
                        .key("id").value(((Selectable)op).getId())
                        .key("text").value(((Selectable)op).getDisplayName())
                        .endObject();
                } else {
                    json.value(op);
                }
            }
            this.optionsJson = json.endArray().toString();
        } catch (JSONException e) {
            logger.error("Could not produce JSON string for options", e);
        }
        return this;
    }

    /**
     * @return the options as a JSON string
     */
    public String getOptionsJson() {
        return optionsJson;
    }

    public Filter setFilterSettings(String filters) {
        this.filterSettings = filters;
        return this;
    }
    public String getFilterSettings() {
        return this.filterSettings;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

}
