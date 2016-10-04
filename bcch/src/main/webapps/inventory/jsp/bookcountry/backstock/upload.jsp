<%@ include file="../../html-start.jspf" %>

<head>
<%@ include file="../../html-head.jspf" %>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    
    <s:set name="activeMenu" value="%{'inventory'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    new Ext.Viewport({
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
            height:40,
            margins: '0 0 0 0',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
        },{
            region: 'center',
            border   : false,
            bodyBorder: false,
            type: 'xpanel',
            layout: 'fit',
            id: 'contentpanel',
            autoScroll: true,
            contentEl: 'toolsdiv'
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            //bodyCfg : {style: {'background':'#F0F4F5 url(/images/tbbg3.png) repeat-x scroll left top'} },
            bodyCfg : {style: {'background-color':'#ddd'} },
            id: 'footerpanel',
            layout: 'fit',
            contentEl: 'invfooter'
        }]
    });    
    
    new Ext.Button({
        id:'uploadbutton', 
        applyTo:'uploadButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Upload ', 
        disabled:false,
        handler: function(btn){
            btn.disable();
            
            Ext.form.uploadForm.submit({
                timeout: 600,
                waitMsg:'Uploading Back Stock Items...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    Ext.MessageBox.alert('Success', 'Successfully uploaded the back stock items');
                }
            });
        } 
    });
    
    Ext.form.uploadForm = new Ext.form.BasicForm("uploadform");

});
</script>

</head>

<body class="page">

<%@ include file="../div-header.jspf"%>

<div style="display:none;">

<div id="toolsdiv" style="margin:8px;margin-left:25px;">
    
    <div class="homepanelsearch" style="width:700px;">
    <form action="backstock!upload.bc" name="uploadform" id="uploadform"  method="POST" enctype="multipart/form-data" class="formular">
        
        <fieldset>
            <legend>Back Stock Upload</legend>
            
            Back Stock Excel: <s:file name="upload" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
        
            <div style="height:15px;"></div>
            <div id="uploadButtonDiv"></div>
        </fieldset>

    </form>
    </div>
    
    
</div>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>

