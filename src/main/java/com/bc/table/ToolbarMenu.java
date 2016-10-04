package com.bc.table;

import java.util.ArrayList;
import java.util.List;

public class ToolbarMenu extends ToolbarButton {
    private List<ToolbarButton> buttons;

    /**
     * Default ctor.
     */
    public ToolbarMenu() { }

    /**
     * @param text
     * @param iconCls
     * @param tooltip
     */
    public ToolbarMenu(String text, String iconCls, String tooltip) {
        super(text, iconCls, tooltip);
    }

    /**
     * @param text
     * @param handler
     * @param iconCls
     * @param tooltip
     */
    public ToolbarMenu(String text, String handler, String iconCls, String tooltip) {
        super(text, handler, iconCls, tooltip);
    }

    /**
     * @param text
     * @param handler
     * @param iconCls
     * @param tooltip
     * @param right
     */
    public ToolbarMenu(String text, String handler, String iconCls, String tooltip, Boolean right) {
        super(text, handler, iconCls, tooltip, right);
    }

    /**
     * @param text
     * @param iconCls
     * @param tooltip
     * @param right
     * @param linkButton
     * @param linkHref
     */
    public ToolbarMenu(String text, String iconCls, String tooltip, Boolean right, Boolean linkButton, String linkHref) {
        super(text, iconCls, tooltip, right, linkButton, linkHref);
    }
    
    /**
     * @return
     */
    public List<ToolbarButton> getButtons() {
        return buttons;
    }

    /**
     * @param buttons
     * @return
     */
    public ToolbarMenu setButtons(List<ToolbarButton> buttons) {
        this.buttons = buttons;
        return this;
    }
    
    /**
     * @param button
     * @return
     */
    public ToolbarMenu addButton(ToolbarButton button) {
        if (buttons == null)
            buttons = new ArrayList<ToolbarButton>();
        buttons.add(button);
        return this;
    }
    
    public String getFilterText(String key){
        for (ToolbarButton tb : buttons){
            if (tb.isFilter() && tb.getFilter().getName().equals(key)){
                return tb.getFilterText();
            }
        }
        return null;
    }
}
