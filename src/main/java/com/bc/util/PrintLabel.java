package com.bc.util;

import java.util.Iterator;
import java.util.Map;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * @author Tim
 */
public class PrintLabel {

    private static final String printerName = "DYMO LabelWriter 400 Turbo";
    private static final String labelFile = "C:\\BreakRoomHorizontal.LWL";

    public static boolean dymoPrint(String printer,
                                    String labelFile,
                                    Map<String, String> nameValues)
    {
        // TODO put hte
        boolean ret = true;
        try {
            ComThread.InitSTA();
            ActiveXComponent dymoAddIn = new ActiveXComponent("DYMO.DYMOAddIn");
            ActiveXComponent dymoLabels = new ActiveXComponent("DYMO.DYMOLabels");
            Object dymoAddInOb = dymoAddIn.getObject();
            Dispatch.call(dymoAddInOb, "SelectPrinter", new Variant(printer));
            Dispatch.call(dymoAddInOb, "Open", new Variant(labelFile));
            Iterator<String> names = nameValues.keySet().iterator();
            while (names.hasNext()){
                String name = names.next();
                String val = nameValues.get(name);
                Variant[] args = new Variant[] {new Variant(name), new Variant(val)};
                dymoLabels.invoke("SetField", args);
            }
            // the args here are Copies, ShowDialog
            Variant[] args = new Variant[] {new Variant(1), new Variant(false)};
            dymoAddIn.invoke("Print", args);
        } catch (Throwable t) {
            t.printStackTrace();
            ret = false;
        } finally {
            try {
                ComThread.Release();
            } catch (Throwable t){} // do nothing
        }
        return ret;
    }
}
