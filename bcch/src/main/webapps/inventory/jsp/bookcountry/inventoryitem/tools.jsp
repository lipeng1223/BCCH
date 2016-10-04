<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/inventoryBundle.js"/> 

<script language="JavaScript" type="text/javascript">


Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'inventory'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    var vp = new Ext.Viewport({
        id: 'mainviewport',
        layout: 'border',
        layoutConfig: {
            minWidth: 800,
            minHeight: 500
        },
        //bufferResize:true,
        items: [{
            region: 'north',
            id: 'northpanel',
            border: false,
            collapsible: false,
            layout: 'fit',
            height:60,
            margins: '0 0 0 0',
            cls: 'invtoolbar',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [headermenu]
        },{
            region: 'center',
            margins: '0 0 0 0',
            collapsible: false,
            border: true,
            layout: 'fit',
            id: 'contentpanel',
            items: [{
                title: 'Inventory Item Tools',
                iconCls: 'cog_icon',
                region: 'center',
                border   : false,
                bodyBorder: false,
                autoScroll: true,
                type: 'xpanel',
                layout: 'fit',
                contentEl: 'toolsdiv'
            }]
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            bodyCfg : {style: {'background-color':'#ddd'} },
            id: 'footerpanel',
            layout: 'fit',
            contentEl: 'invfooter'
        }]
    });


    new Ext.Button({
        applyTo:'invCountUpdateButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Load Inventory Count Excel', 
        disabled:false,
        handler: function(){
            Ext.form.countUpdateForm.submit({
                timeout: 600,
                waitMsg:'Uploading Count Updates...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    Ext.MessageBox.alert('Success', "Successfully uploaded the count changes.");
                }
            });
        } 
    });
    
    new Ext.Button({
        applyTo:'invBulkUpdateButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Load Inventory Bulk Update Excel', 
        disabled:false,
        handler: function(){
            Ext.form.bulkUpdateForm.submit({
                timeout: 600,
                waitMsg:'Uploading Bulk Updates...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    Ext.MessageBox.alert('Success', "Successfully uploaded the bulk changes.");
                }
            });
        } 
    });
    
    new Ext.Button({
        applyTo:'invUpdateButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Update Inventory', 
        disabled:false,
        handler: function(){
            Ext.form.multiUpdateForm.submit({
                waitMsg:'Updating Inventory Items...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    Ext.MessageBox.alert('Success', "Successfully update the Inventory Items.");
                }
            });
        } 
    });

    document.getElementById("restrictedDo Nothing").checked = true;
    document.getElementById("bellbookDo Nothing").checked = true;
    document.getElementById("higherEducationDo Nothing").checked = true;

    Ext.form.countUpdateForm = new Ext.form.BasicForm("countuploadform");
    Ext.form.bulkUpdateForm = new Ext.form.BasicForm("bulkuploadform");
    Ext.form.multiUpdateForm = new Ext.form.BasicForm("updateform");
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="toolsdiv" style="margin:8px;margin-left:25px;">

    <form action="inventorycountupdate!upload.bc" name="countuploadform" id="countuploadform"  method="POST" enctype="multipart/form-data" class="formular">
        <fieldset>
            <legend>Inventory Count Update</legend>
            
            <input type="hidden" name="id" value="<s:property value="id"/>"/>
            Inventory Count Excel: <s:file name="upload" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
        
            <div style="height:15px;"></div>
            <div id="invCountUpdateButtonDiv"></div>
        </fieldset>
    </form>

    <form action="inventorybulkupdate!upload.bc" name="bulkuploadform" id="bulkuploadform"  method="POST" enctype="multipart/form-data" class="formular">
        <fieldset>
            <legend>Inventory Bulk Update</legend>
            
            <input type="hidden" name="id" value="<s:property value="id"/>"/>
            Inventory Bulk Update Excel: <s:file name="upload" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
        
            <div style="height:15px;"></div>
            <div id="invBulkUpdateButtonDiv"></div>
        </fieldset>
    </form>
    
    
    <div style="height:20px;"></div>
    <form action="inventorytools!update.bc" name="updateform" id="updateform"  method="POST" enctype="multipart/form-data" class="formular">
        <fieldset>
            <legend>Multi ISBN Inventory Update</legend>
            
            <table>
                <tr>
                    <td align="right" valign="top">Multi ISBN: </td>
                    <td style="padding-left:10px"><s:textarea name="multiIsbn" id="multiIsbn" rows="8" cssStyle="width:200px;"/></td>
                    <td style="padding-left:10px" valign="top">
                        <table>
                        <tr>
                        <td align="right">Bin:</td>
                        <td style="padding-left:5px;"><s:textfield name="bin"/></td>
                        </tr>
                        <tr><td style="height:5px;"></td></tr>
                        <tr>
                        <td align="right">Selling Price:</td>
                        <td style="padding-left:5px;"><s:textfield name="sellPrice"/></td>
                        </tr>
                        <tr><td style="height:5px;"></td></tr>
                        <tr>
                        <td align="right">Restricted:</td>
                        <td style="padding-left:5px;"><s:radio name="restricted" list="{'True', 'False', 'Do Nothing'}" value="Do Nothing" cssStyle="display:inline;"/></td>
                        </tr>
                        <tr><td style="height:5px;"></td></tr>
                        <tr>
                        <td align="right">Bell Book:</td>
                        <td style="padding-left:5px;"><s:radio name="bellbook" list="{'True', 'False', 'Do Nothing'}" value="Do Nothing" cssStyle="display:inline;"/></td>
                        </tr>
                        <tr><td style="height:5px;"></td></tr>
                        <tr>
                        <td align="right">Higher Education:</td>
                        <td style="padding-left:5px;"><s:radio name="higherEducation" list="{'True', 'False', 'Do Nothing'}" value="Do Nothing" cssStyle="display:inline;"/></td>
                        </tr>
                        <tr><td style="height:5px;"></td></tr>
                        <tr>
                        <td align="right">Bc Category:</td>
                        <td style="padding-left:5px;"><s:textfield name="bcCategory"/></td>
                        </tr>
                        </table>
                    </td>
                </tr>
            </table>
        
            <div style="height:15px;"></div>
            <div id="invUpdateButtonDiv"></div>
        </fieldset>
    </form>
    
 
</div>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
