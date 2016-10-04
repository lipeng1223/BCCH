<%@ include file="../html-start.jspf" %>
<head>
<%@ include file="../html-head.jspf" %>

<title>Book Country Inventory</title>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'home'}"/>
    <%@ include file="div-header-menu.jspf" %>
    
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
               autoScroll: true,
               type: 'xpanel',
               layout: 'fit',
               margins: '0 0 0 0',
               id: 'listpanel',
               contentEl: 'homepanel'
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
            id: 'contentpanel',
            layout: 'fit',
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

    new Ext.Button({
        id:'invsearchbutton', 
        applyTo:'inventorySearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("inventorysearchform").submit();
        } 
    });
    new Ext.Button({
        id:'recsearchbutton', 
        applyTo:'recSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("receivingsearchform").submit();
        } 
    });
    new Ext.Button({
        id:'ordersearchbutton', 
        applyTo:'orderSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("ordersearchform").submit();
        } 
    });
    new Ext.Button({
        id:'mansearchbutton', 
        applyTo:'manifestSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("manifestsearchform").submit();
        } 
    });
    new Ext.Button({
        id:'multisearchbutton', 
        applyTo:'multiIsbnSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("multiisbnsearchform").submit();
        } 
    });
    
    setFocus("homeInvSearch-searchval0");
    
});
</script>

</head>
<body class="page">

<%@ include file="div-header.jspf" %>

<div style="display:none;">

    <div id="homepanel">
    
        <table style="">
            <tr>
                <td valign="top">
                    <div class="homepanelsearch">
                        <form class="formular" action="inventoryitem!search.bc" id="inventorysearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Inventory Search</legend>
                                <s:set name="invSearchPrefix" value="%{'homeInvSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('inventory')"/>
                                <%@ include file="inventoryitem/search.jspf" %>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
            </tr>
            <tr>
                <td valign="top">
                    <div class="homepanelsearch">
                        <form class="formular" action="receiving!search.bc" id="receivingsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Receiving Search</legend>
                                <s:set name="recSearchPrefix" value="%{'homeRecSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('receiving')"/>
                                <%@ include file="receiving/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
            </tr>
            <tr>
                <td valign="top">
                    <div class="homepanelsearch">
                        <form class="formular" action="order!search.bc" id="ordersearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Order Search</legend>
                                <s:set name="orderSearchPrefix" value="%{'homeOrderSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('order')"/>
                                <%@ include file="orders/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
            </tr>
            <tr>
                <td valign="top">
                    <div class="homepanelsearch">
                        <form class="formular" action="manifest!search.bc" id="manifestsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Manifest Search</legend>
                                <s:set name="manifestSearchPrefix" value="%{'homeManifestSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('manifest')"/>
                                <%@ include file="manifest/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
            </tr>
            <tr>
                <td valign="top">
                    <div class="homepanelsearch">
                        <form class="formular" action="inventoryitem!multiSearch.bc" id="multiisbnsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Inventory Search</legend>
                                <table>
                                    <tr>
                                        <td align="right" valign="top">Multi ISBN: </td>
                                        <td style="padding-left:10px"><textarea name="search.multiIsbn" rows="8"></textarea></td>
                                        <td style="padding-left:10px" valign="top">
                                            <table>
                                            <tr>
                                            <td><s:checkbox name="selectAll" id="selectAll" onchange="javascript:selectAllForMultiSearch();"/></td>
                                            <td style="padding-left:5px;"><label for="selectAll">Select All</label></td>
                                            </tr>
                                            <tr>
                                            <td><s:checkbox name="search.includeBell" id="includeBell"/></td>
                                            <td style="padding-left:5px;"><label for="includeBell">Include Bell Books</label></td>
                                            </tr>
                                            <tr>
                                            <td><s:checkbox name="search.includeRestricted" id="includeRest"/></td>
                                            <td style="padding-left:5px;"><label for="includeRest">Include Restricted</label></td>
                                            </tr>
                                            <tr>
                                            <td><s:checkbox name="search.includeHigherEducation" id="includeHe"/></td>
                                            <td style="padding-left:5px;"><label for="includeHe">Include HigherEducation</label></td>
                                            </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:8px;"></div></td></tr>
                                    <tr>
                                        <td></td>
                                        <td align="right"><div id="multiIsbnSearchSubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                        </form>
                    </div>
                </td>
            </tr>
            
        </table>
        
        
        
    </div>

    
</div>

<%@ include file="../div-footer.jspf" %>

</body>
</html>
