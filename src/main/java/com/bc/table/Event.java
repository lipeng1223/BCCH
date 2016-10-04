package com.bc.table;

public class Event {

    private String eventName;
    private String function;
    
    public Event(){}
    
    public Event(String eventName, String function){
        this.eventName = eventName;
        this.function = function;
    }

    public String getEventName() {
        return eventName;
    }

    public Event setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public Event setFunction(String function) {
        this.function = function;
        return this;
    }
    
    
}
