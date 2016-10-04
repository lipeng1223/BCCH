package com.bc.table;

/**
 * A Render is a pluggable render function in the ext grid
 * They look like this:
 * 
    // pluggable renders
    function renderTopic(value, p, record){
        return String.format('<b>{0}</b>{1}', value, record.data['excerpt']);
    }
    function renderTopicPlain(value){
        return String.format('<b><i>{0}</i></b>', value);
    }
    function renderLast(value, p, r){
        return String.format('{0}<br/>by {1}', value.dateFormat('M j, Y, g:i a'), r.data['author']);
    }
    function renderLastPlain(value){
        return value.dateFormat('M j, Y, g:i a');
    }
    
 */
public class Render {

    private String renderFunction;
    
    public Render(String renderFunction){
        this.renderFunction = renderFunction;
    }

    public String getRenderFunction() {
        return renderFunction;
    }

    public Render setRenderFunction(String renderFunction) {
        this.renderFunction = renderFunction;
        return this;
    }
    
    
}
