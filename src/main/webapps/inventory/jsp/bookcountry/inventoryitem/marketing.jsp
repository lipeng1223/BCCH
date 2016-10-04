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
                title: 'Inventory Marketing Generator',
                iconCls: 'word_icon',
                region: 'center',
                border   : false,
                bodyBorder: false,
                autoScroll: true,
                type: 'xpanel',
                layout: 'fit',
                contentEl: 'generatediv'
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
        applyTo:'generateButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Generate Inventory Marketing DOC', 
        disabled:false,
        handler: function(){
            Ext.form.generateForm.submit({
                //waitMsg:'Generating...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });
        } 
    });
    Ext.form.generateForm = new Ext.form.BasicForm("generateForm");
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="generatediv" style="margin:8px;margin-left:25px;">

    <div style="height:20px;"></div>
    <form action="inventorytools!generateMarketing.bc" name="generateForm" id="generateForm"  method="POST" enctype="multipart/form-data" class="formular">
        <fieldset>
            <legend>Inventory Marketing Generator</legend>
            
            <table>
                <tr>
                    <td align="right" valign="top">ISBNs For Marketing DOC: </td>
                    <td style="padding-left:10px"><s:textarea name="multiIsbn" id="multiIsbn" rows="8" cssStyle="width:200px;"/></td>
                </tr>
            </table>
        
            <div style="height:15px;"></div>
            <div id="generateButtonDiv"></div>
        </fieldset>
    </form>
    
 
</div>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
