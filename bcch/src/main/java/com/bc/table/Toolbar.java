package com.bc.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Toolbar {
    
    private List<ToolbarButton> buttons;
    private List<String> text;
    
    public List<ToolbarButton> getButtons() {
        return buttons;
    }

    public Toolbar setButtons(List<ToolbarButton> buttons) {
        this.buttons = buttons;
        return this;
    }
    
    public Toolbar addButton(ToolbarButton button) {
        if (buttons == null)
            buttons = new ArrayList<ToolbarButton>();
        buttons.add(button);
        return this;
    }

    public List<String> getText() {
        return text;
    }

    public Toolbar setText(List<String> text) {
        this.text = text;
        return this;
    }
    
    public Toolbar setText(String... text){
        this.text = Arrays.asList(text);
        return this;
    }

}
