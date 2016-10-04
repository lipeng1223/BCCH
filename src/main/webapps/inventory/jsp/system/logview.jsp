<%@ include file="../html-start.jspf" %>
<head>
<%@ include file="../html-head.jspf" %>

<script language="JavaScript" type="text/javascript">

var currentTimeout;
var updateInterval = 10000;
var stop = false;
var scrollDown = true;

function readFromLog(filename, filepos){
    document.getElementById("loadstatus").innerHTML = "<span class='greentext'>Loading <img src='/images/loading-small.gif'/></span>";
    Ext.Ajax.request({
        url: 'logview!view.bc',
        params: {'logToView':filename, 'filePos':filepos},
        success: function(response, options){
            var elem = Ext.get("logtext");
            elem.insertHtml("beforeEnd", response.responseText);

            var scrollIt = document.getElementById("scrollwithcontent").checked;
            if (scrollDown || scrollIt){
                var panel = Ext.getCmp('logotextpanel');
                panel.body.scroll("b", elem.getHeight(), true);
            }
            
            var divelem = document.getElementById("logtext");
            var currentFilePos = document.getElementById("currentFilePos").innerHTML;
            var completedRead = document.getElementById("completedRead").innerHTML;
            divelem.removeChild(document.getElementById("currentFilePos"));
            divelem.removeChild(document.getElementById("completedRead"));
            //console.log("currentFilePos: "+currentFilePos);
            //console.log("completedRead: "+completedRead);
            if (completedRead == "false"){
                // continue reading in the log file
                readFromLog(filename, currentFilePos);
            } else {
                scrollDown = false;
                var now = new Date();
                document.getElementById("loadstatus").innerHTML = "<span class='bluetext'>Waiting To Update For "+(updateInterval / 1000)+" seconds, last update: "+now.toTimeString()+"</span>";
                // we have read the entire thing, now only update every X often
                if (!stop){
                    currentTimeout = setTimeout("readFromLog('"+filename+"', "+currentFilePos+")", updateInterval);
                }
            }
        },
        failure: function(response, options){
            Ext.MessageBox.alert('Error', "There was a system error and we could not view the log file data.");
        }
    });
}

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'system'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    new Ext.Button({
        id:'viewlogbutton', 
        applyTo:'viewlogdiv', 
        cls:"x-btn-text-icon", 
        iconCls:"zoom_icon", 
        text:'View Selected Log', 
        disabled:false,
        handler: function(){
            this.disable();
            // wipe out what is currently in the log text
            document.getElementById("logtext").innerHTML = "";
            stop = false;
            scrollDown = true;
            var ui = document.getElementById("updateInterval").value;
            if (checkPositiveInt(ui)){
                updateInterval = parseInt(ui, 10);
            }
            if (currentTimeout) {
                clearTimeout(currentTimeout);
            }
            var sel = document.getElementById("logfileselect");
            var file = sel.options[sel.selectedIndex].value;
            readFromLog(file, 0);
            Ext.getCmp("cancelbutton").enable();
        } 
    });

    new Ext.Button({
        id:'cancelbutton', 
        applyTo:'canceldiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/stop.png", 
        text:'Stop Watching Log', 
        disabled:true,
        handler: function(){
            this.disable();
            stop = true;
            if (currentTimeout) {
                clearTimeout(currentTimeout);
            }
            document.getElementById("loadstatus").innerHTML = "<span class='bluetext'>Not Updating.</span>";
            Ext.getCmp("viewlogbutton").enable();
        } 
    });

    var dataPanel = new Ext.Panel({
        id       : 'datapanel',
        layout   : 'border',
        border   : false,
        bodyBorder: false,
        items : [
             {
                 region: 'north',
                 title: 'Log Files',
                 height: 120,
                 autoScroll:true,
                 collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                 stateful:false,
                 split:true,
                 type:'panel',
                 layout:'fit',
                 bodyCfg : {style: {'background':'#eee'} },
                 contentEl: 'logfiles'
             },{
               region: 'center',
               minSize: 300,
               layout: 'fit',
               autoScroll:true,
               title: 'Log Text',
               id: 'logotextpanel',
               bodyCfg : {style: {'background':'#000'} },
               contentEl: 'logtext'
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
            height:40,
            margins: '0 0 0 0',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
        },{
            region: 'center',
            margins: '0 0 0 0',
            collapsible: false,
            border: true,
            id: 'contentpanel',
            layout: 'fit',
            items: [dataPanel]
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

       
});
</script>

</head>

<body class="page">

<div style="display:none">

    <div id="logfiles">
        <div style="margin-left:50px;margin-top:15px;margin-bottom:10px;">
        <table>
            <tr>
                <td>
                    <select name="logfileselect" id="logfileselect">
                        <s:iterator value="availableLogFiles" var="alf">
                            <option value="<s:property value="alf"/>"><s:property value="alf"/></option>
                        </s:iterator>
                    </select>
                </td>
                <td style="padding-left:15px;">
                    <input type="text" name="updateInterval" id="updateInterval" value="10000" maxlength="6"/>
                </td>
                <td style="padding-left:15px;">
                    <input type="checkbox" name="scrollwithcontent" id="scrollwithcontent" checked="checked"/> Scroll With Input
                </td>
                <td style="padding-left:15px;">
                    <div id="viewlogdiv"></div>
                </td>
                <td style="padding-left:15px;">
                    <div id="canceldiv"></div>
                </td>
            </tr>
        </table>
        </div>
    
        <div style="margin-left:50px;margin-top:15px;margin-bottom:10px;">Status: <span id="loadstatus"><span class="bluetext">Waiting for action...</span></span></div>
    </div>

    <div id="logtext" style="padding:5px;background:#000;color:#ccc;">
    </div>
    
</div>

<%@ include file="../div-header.jspf"%>

<%@ include file="../div-footer.jspf" %>

</body>


