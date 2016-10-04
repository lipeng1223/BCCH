<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">
function submit(){

    Ext.form.uploadForm.submit({
        timeout: 600,
        waitMsg:'Uploading Order Items...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            //console.log(inspect(action.result, 3));
            //console.log("errors? "+(action.result.hasImportErrors == true));
            var html = action.result.errors.replace(/&lt;/g, "<");
            html = html.replace(/&gt;/g, ">");
            html = html.replace(/&quote;/g, ">");
            if (action.result.hasImportErrors){
                Ext.uploadErrorsWindow = new Ext.Window({
                    id: 'uploaderrors',
                    title: 'Upload Errors',
                    width:500,
                    height:300,
                    modal:false,
                    stateful:false,
                    autoScroll:true,
                    bbar:['->',{
                        cls:"x-btn-text-icon", 
                        icon:"/images/cancel.png", 
                        text:'Close', 
                        handler: function(){Ext.uploadErrorsWindow.close();}
                    }],
                    bodyStyle:'background-color:#ffffff',
                    html: html
                });
                Ext.uploadErrorsWindow.show(Ext.grid.orderitemsGrid);
            }
            Ext.grid.bellorderitemsGridDs.reload();
            Ext.uploadWindow.close();
        }
    });
}
function cancel(){
    Ext.uploadWindow.close();
}
Ext.onReady(function(){
    Ext.form.uploadForm = new Ext.form.BasicForm("uploadform");
    
    Ext.uploadWindow.getBottomToolbar().addFill();
    Ext.uploadWindow.getBottomToolbar().add({
        id:'submitButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Upload', 
        handler:submit, 
        disabled:false
    });
    Ext.uploadWindow.getBottomToolbar().addSeparator();
    Ext.uploadWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });
    
});
</script>

<div style="padding-left:15px;padding-top:15px;">
    <form action="orderitem!upload.bc" name="uploadform" id="uploadform"  method="POST" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<s:property value="id"/>"/>
        
        <br/>
        <table>
        <tr><td align="right">ISBN Col:</td><td align="left" style="padding-left:10px;"><s:textfield name="isbnCol" value="A" cssStyle="width:40px;"/></td></tr>
        <tr><td align="right">Quantity Col:</td><td align="left" style="padding-left:10px;"><s:textfield name="qtyCol" value="B" cssStyle="width:40px;"/></td></tr>
        <tr><td align="right">Selling Price Col:</td><td align="left" style="padding-left:10px;"><s:textfield name="priceCol" value="C" cssStyle="width:40px;"/></td></tr>
        <tr><td align="right">Row Start:</td><td align="left" style="padding-left:10px;"><s:textfield name="startRow" value="2" cssStyle="width:40px;"/></td></tr>
        </table>
        <br/><br/>
        
        Order Item Excel: <s:file name="upload" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
        <br/><br/>
        <!--
        Example Order Item Import Excel: <a href="/templates/OrderItemImport.xls">Order Item Upload Template</a>
        -->
    </form>
</div>
