<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/manifestBundle.js"/> 

<s:set name="stateSession" value="%{'manifest-view-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

function createItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Manifest Item',
        width:500,
        height:325,
        minWidth:500,
        minHeight:325,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'manifestitem!create.bc?id=<s:property value="manifest.id"/>'
    });
    Ext.crudWindow.show(Ext.grid.manifestitemsGrid);
}

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'manifests'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    var contentpanel = new Ext.Panel({
        id       : 'dualPanel',
        layout   : 'border',
        border   : false,
        bodyBorder: false,
        items : [
            {
                region: 'center',
                border   : false,
                bodyBorder: false,
                split:true,
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                type: 'xpanel',
                layout: 'border',
                items : [
                         {
                            region: 'center',
                            border   : false,
                            bodyBorder: false,
                            split:true,
                            type: 'xpanel',
                            layout: 'fit',
                            id: 'listpanel'
                         }, {
                             region: 'east',
                             width: 300,
                             title: "Manifest Item Detail",
                             collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                             bodyStyle: 'background:#eaeaea;',
                             border   : false,
                             bodyBorder: false,
                             split:true,
                             type: 'xpanel',
                             layout: 'fit',
                             id: 'itemdetailpanel',
                             collapsed: true,
                             autoScroll: true,
                             html: noitemselectionhtml
                         }
                     ]
            },
            {
                region: 'north',
                title: 'Manifest Detail',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                autoScroll:true,
                id: 'detailpanel',
                height:180,
                minHeight:75,
                split:true,
                autoLoad: 'manifest!detail.bc?id=<s:property value="manifest.id"/>',
                tbar:[
                {
                text: 'History',
                iconCls: 'calendar_icon',
                handler: function(){
                    showHistory("manifest", <s:property value="manifest.id"/>, "Manifest Audit History", "manifest_item", "Manifest Audit History", "Manifest Items Audit History");
                    }
                }
                ]
             }
        ]
    });


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
            items: [contentpanel]
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

    Ext.form.deleteForm = new Ext.form.BasicForm("deleteform");
    Ext.form.uploadForm = new Ext.form.BasicForm("uploadform");
    
});

function uploadButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.uploadWindow = new Ext.Window({
        id: 'uploadwindow',
        title: 'Upload Manifest Items',
        width:600,
        height:200,
        modal:true,
        stateful:false,
        autoScroll:true,
        bodyStyle:'background-color:#fff',
        contentEl: 'manifestupload',
        bbar:[
          '->',
          {
              cls:"x-btn-text-icon", 
              icon:"/images/accept.png", 
              text:'Upload Manifest Items', 
              handler: function(){

                  Ext.form.uploadForm.submit({
                      waitMsg:'Uploading Manifest Items...',
                      failure: function(form, action){
                          Ext.MessageBox.alert('Status', action.result.error);
                      },
                      success: function(form, action){
                          Ext.grid.manifestitemsGridDs.reload();
                      }
                  });
                  
              }
          } 
        ]
    });
    Ext.uploadWindow.show(Ext.grid.manifestitemsGrid);
}
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="manifestupload" style="background:#fff;width:100%;height:100%;">
    <s:form action="manifestitem!upload.bc" method="POST" enctype="multipart/form-data" cssClass="formular" cssStyle="padding-top:5px;padding-bottom:5px;" id="uploadform">
    <s:hidden key="id"/>
    <fieldset>
    <legend>Upload Manifest Items</legend>
    <div style="margin-left:30px;">
        <table>
            <tr>
            <td><s:file name="upload"/></td>
            <td style="padding-left:15px"><a href="/templates/ManifestItemImport.xlsx">Download Manifest Template</a></td>
            </tr>
        </table>
    </div>
    </fieldset>
    </s:form>
</div>

<inv:table tableName="manifestitems" sortable="true" exportable="true" tableTitle="Manifest Items"
dataAction="manifestitem!listData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"
statestartid="${id}"
rowDblClick="itemRowDoubleClick"/>

<form action="manifestitem!delete.bc" name="deleteform" id="deleteform"></form>


</div>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.manifestitemsGrid.getSelectionModel().on('selectionchange', function(){
        setTimeout("updateManifestItemDetail()", 300);
    });
    Ext.grid.manifestitemsGridDs.on("load", function(store, records, options){
        updateManifestItemDetail();
        if (options.params.start != curStart){
            curStart = options.params.start;
        } else if (curSelectedIdx != undefined){
            Ext.grid.manifestitemsGrid.getSelectionModel().selectRow(curSelectedIdx);
            Ext.grid.manifestitemsGrid.getView().focusRow(curSelectedIdx);
        }
    });
    Ext.grid.manifestitemsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
        curSelectedIdx = idx;
        curSelectedRec = rec;
    });
});
</script>


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
