<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<!-- 
<embed src="/audio/beep-7.wav" autostart="false" width="0" height="0" id="beep7" enablejavascript="true">
 -->
 
<script language="JavaScript" type="text/javascript">

var inSubmit = false;
function submit(){
    if (inSubmit) return;
    inSubmit = true;
    var theisbn = trimString(document.getElementById("receivedItem.isbn").value);
    if (theisbn.length == 0){
        var isbnfield = document.getElementById("receivedItem.isbn");
        isbnfield.disabled=false; 
        isbnfield.value = '';
        setFocus('receivedItem.isbn');
        inSubmit = false;
        return;
    }
    
    Ext.form.crudForm.submit({
        params: {"receivedItem.isbn":theisbn, 'receivedItem.cond':'hurt', 'fastReceiving':true, "receivedItem.quantity":1},
        timeout: 300,
        failure: function(form, action){
            if (action.result.error == "noamazondata"){
                Ext.MessageBox.alert('Error', "This ISBN was not found in inventory and could not be found by amazon.");
            } else if (action.result.error == "systemerror"){
                appendHistory(theisbn, false);
                //Ext.MessageBox.alert('Error', "The system could not create the receiving item, there was an internal error.");
            } else {
                //Ext.MessageBox.alert('Status', action.result.error);
                appendHistory(theisbn, false);
            }
            var isbnfield = document.getElementById("receivedItem.isbn");
            isbnfield.disabled=false; 
            setFocus('receivedItem.isbn');
            inSubmit = false;
        },
        success: function(form, action){
            
            var isbnfield = document.getElementById("receivedItem.isbn");
            isbnfield.disabled=false; 
            isbnfield.value = '';
            setFocus('receivedItem.isbn');
            inSubmit = false;
            
            try {
                var sound = document.getElementById("beep7");
                sound.Play();
            } catch (err) {}
            
            appendHistory(theisbn, true);
            
            
        }
    });
}

function appendHistory(isbn, success){
        var table = document.getElementById("fastrechistorytable");
        var rowCount = table.rows.length;
        var row = table.insertRow(0);
        var cell1 = row.insertCell(0);
        var tn1 = document.createTextNode((new Date()).format("Y-m-d H:i:s"));
        cell1.appendChild(tn1);
        var cell2 = row.insertCell(1);
        var tn2 = document.createTextNode(isbn);
        cell2.appendChild(tn2);
        var cell3 = row.insertCell(2);
        if (success){
            cell3.style.color = "#249B24";
            var tn3 = document.createTextNode("success");
            cell3.appendChild(tn3);
        } else {
            cell3.style.color = "#9e0000";
            var tn3 = document.createTextNode("failed");
            cell3.appendChild(tn3);
        }

        if (rowCount > 200){
            table.deleteRow(rowCount);
        }
}

function cancel(){
    Ext.crudWindow.close();
}

Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Close', 
        handler:cancel, 
        disabled:false
    });

    Ext.get("receivedItem.isbn").addKeyListener(13, function(){
            var isbnfield = document.getElementById("receivedItem.isbn");
            isbnfield.blur();
            isbnfield.disabled=true;
            submit();
    });
    
    setFocus('receivedItem.isbn');
    
    document.getElementById("receivedItem.isbn").setAttribute( "autocomplete", "off" );
    
});
</script>

<form action="receivingitem!fastrecSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="id"/>

    <table>
    <tr>
    <td valign="top">
        
    <fieldset>
        <legend>Fast Receiving Item</legend>
        
        <table>
            <tr>
            <td valign="top">
        
                <table>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                            <s:textfield key="receivedItem.isbn" maxlength="50" id="receivedItem.isbn" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:20px;">
                            
                        </td>
                    </tr>
                    
                </table>
            </td>
            </tr>
        </table>

    </fieldset>
                        
    </td>
    </tr>
    
    
    </table>
        
</form>

