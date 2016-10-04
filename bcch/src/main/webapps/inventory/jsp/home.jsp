<%@ include file="html-start.jspf" %>
<head>
<%@ include file="html-head.jspf" %>

<title>Inventory</title>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){

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
               type: 'xpanel',
               layout: 'fit',
               id: 'listpanel',
               autoScroll: true,
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
            height:40,
            margins: '0 0 0 0',
            cls: 'invtoolbar',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
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
    
    Ext.form.salesOfferForm = new Ext.form.BasicForm("salesoffer");
    Ext.form.fillzPriorityForm = new Ext.form.BasicForm("fillzpriorityform");
    Ext.form.fillzPriorityToolsForm = new Ext.form.BasicForm("fillzprioritytoolsform");
    Ext.form.fillzImportForm = new Ext.form.BasicForm("fillzimportform");


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
    new Ext.Button({
        id:'multisearchbutton', 
        applyTo:'multiIsbnOrderSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("multiisbnordersearchform").submit();
        } 
    });

    new Ext.Button({
        id:'bellmultisearchbutton', 
        applyTo:'bellmultiIsbnSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/zoom.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("bellmultiisbnsearchform").submit();
        } 
    });

    new Ext.Button({
        id:'fillzprioritybutton', 
        applyTo:'fillzPrioritySubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/page_white_excel.png", 
        text:'Create Priority Excel', 
        disabled:false,
        handler: function(){
            var f = document.getElementById('fillzorderfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Fillz Order Excel file must be provided.');
                return;
            }
            
            // No waitMsg because the form submit causes a file download which prevents Ext from removing the wait message window/mask
            Ext.form.fillzPriorityForm.submit({                
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });
            
        } 
    });
    new Ext.Button({
        id:'fillzpackageoneitembutton', 
        applyTo:'fillzPackageOneItemSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/page_white_acrobat.png", 
        text:'Create Packing Slips - One Item Per Page', 
        disabled:false,
        handler: function(){
            var f = document.getElementById('fillzpriorityfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Fillz Priority Excel file must be provided.');
                return;
            }

            document.getElementById("slipsType").value = "packingoneitem";

            // No waitMsg because the form submit causes a file download which prevents Ext from removing the wait message window/mask
            Ext.form.fillzPriorityToolsForm.submit({                
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });

        } 
    });    
    new Ext.Button({
        id:'fillzpackagebutton', 
        applyTo:'fillzPackageSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/page_white_acrobat.png", 
        text:'Create Packing Slips', 
        disabled:false,
        handler: function(){
            var f = document.getElementById('fillzpriorityfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Fillz Priority Excel file must be provided.');
                return;
            }

            document.getElementById("slipsType").value = "packing";

            // No waitMsg because the form submit causes a file download which prevents Ext from removing the wait message window/mask
            Ext.form.fillzPriorityToolsForm.submit({                
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });

        } 
    });
    new Ext.Button({
        id:'fillzshippingbutton', 
        applyTo:'fillzShippingSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/page_white_acrobat.png", 
        text:'Create Shipping Labels', 
        disabled:false,
        handler: function(){
            
            var f = document.getElementById('fillzpriorityfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Fillz Priority Excel file must be provided.');
                return;
            }
            
            document.getElementById("slipsType").value = "shipping";
            
            // No waitMsg because the form submit causes a file download which prevents Ext from removing the wait message window/mask
            Ext.form.fillzPriorityToolsForm.submit({                
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });
        } 
    });
    new Ext.Button({
        id:'fillzimportbutton', 
        applyTo:'fillzImportSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_up.png", 
        text:'Import Fillz Orders', 
        disabled:false,
        handler: function(){
            var f = document.getElementById('fillzimportfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Fillz Order Excel file must be provided.');
                return;
            }

            Ext.form.fillzImportForm.submit({                
                waitTitle: 'Working',
                waitMsg: 'Importing FillZ Orders...',
                timeout: 600,
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }, 
                success: function(form, action){
                    Ext.MessageBox.alert('Success', 'Imported the FillZ orders.');
                }
            });
        } 
    });
    
    
    new Ext.Button({
        id:'salesofferbutton', 
        applyTo:'salesOfferSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/page_white_excel.png", 
        text:'Sales Offer Comparison', 
        disabled:false,
        handler: function() {
            
            var f = document.getElementById('salesofferfile').value;
            if(!f || f.length == 0) {
                Ext.Msg.alert('Error', 'Sales Offer Excel file must be provided.');
                return;
            }
            
            // No waitMsg because the form submit causes a file download which prevents Ext from removing the wait message window/mask
            Ext.form.salesOfferForm.submit({                
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                }
            });

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
    
        <table>
            <tr>
                <td valign="top" align="left" style="width:300px">
                <table>
                <tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/inventoryitem!search.bc" id="inventorysearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Inventory Search</legend>
                                <s:set name="invSearchPrefix" value="%{'homeInvSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('inventory')"/>
                                <%@ include file="bookcountry/inventoryitem/search.jspf" %>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/order!search.bc" id="ordersearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Order Search</legend>
                                <s:set name="orderSearchPrefix" value="%{'homeOrderSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('order')"/>
                                <%@ include file="bookcountry/orders/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/receiving!search.bc" id="receivingsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Receiving Search</legend>
                                <s:set name="recSearchPrefix" value="%{'homeRecSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('receiving')"/>
                                <%@ include file="bookcountry/receiving/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/manifest!search.bc" id="manifestsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Manifest Search</legend>
                                <s:set name="manifestSearchPrefix" value="%{'homeManifestSearch-'}"/>
                                <s:set name="searchNames" value="searchMap.get('manifest')"/>
                                <%@ include file="bookcountry/manifest/search.jspf" %>
                            </fieldset>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/inventoryitem!multiSearch.bc" id="multiisbnsearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Inventory Search</legend>
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
                <tr>
                <td valign="top" align="left" style="width:100%">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/order!multiSearch.bc" id="multiisbnordersearchform" method="post" style="padding:0px !important;">
                            <fieldset>
                                <legend>Bookcountry Order Search</legend>
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
                                        <td align="right"><div id="multiIsbnOrderSearchSubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                        </form>
                    </div>
                </td>
                </tr>
                <tr>
                <td valign="top" align="left" style="width:100%;margin-left:20px;">
                    <div class="homepanelsearch">
                        <form class="formular" action="bookcountry/sales!offer.bc" name="salesoffer" id="salesoffer" method="post" style="padding:0px !important;" enctype="multipart/form-data">
                            <fieldset>
                                <legend>Sales Offer Comparison</legend>
                                <table>
                                    <tr>
                                        <td colspan="2">
                                        Sales Offer Excel: <s:file name="upload" id="salesofferfile" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:5px;"></div></td></tr>
                                    <tr>                                        
                                        <td colspan="2">ISBN, Title, Quantity, Offer, Total Offer</td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td align="right"><div id="salesOfferSubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
                </tr>
                
                </table>
            </td>
            <td valign="top" align="left" style="width:300px;padding-left:40px;">
                <table>
                <tr>
                <td valign="top" align="left" style="width:100%;margin-left:20px;">
                    <div class="homepanelsearch">
                        <form class="formular" action="bellwether/fillz!priority.bc" name="fillzpriorityform" id="fillzpriorityform" method="post" style="padding:0px !important;" enctype="multipart/form-data">
                            <fieldset>
                                <legend>Bellwether Fillz Priority</legend>
                                <table>
                                    <tr>
                                        <td colspan="2">
                                        Fillz Order Excel: <s:file name="upload"  id="fillzorderfile" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:5px;"></div></td></tr>
                                    <tr>                                        
                                        <td align="right">Weekday Priority Upgrade Price: </td>
                                        <td style="padding-left:10px"><input type="text" name="weekdayUpgrade" id="weekdayUpgrade" value="30"/></td>
                                    </tr>
                                    <tr><td><div style="height:5px;"></div></td></tr>
                                    <tr>                                        
                                        <td align="right">Weekend Priority Upgrade Price: </td>
                                        <td style="padding-left:10px"><input type="text" name="weekendUpgrade" id="weekendUpgrade" value="40"/></td>
                                    </tr>
                                    <tr><td><div style="height:8px;"></div></td></tr>
                                    <tr>
                                        <td></td>
                                        <td align="right"><div id="fillzPrioritySubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%;margin-left:20px;">
                    <div class="homepanelsearch">
                        <form class="formular" action="bellwether/fillz!slips.bc" name="fillzprioritytoolsform" id="fillzprioritytoolsform" method="post" style="padding:0px !important;" enctype="multipart/form-data">
                            <input type="hidden" name="slipsType" value="packing" id="slipsType"/>
                            <fieldset>
                                <legend>Bellwether Fillz Priority Excel Tools</legend>
                                <table>
                                    <tr>
                                        <td colspan="2">
                                        Fillz Priority Excel: <s:file name="upload" id="fillzpriorityfile" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:10px;"></div></td></tr>
                                    <tr>                                        
                                        <td colspan="2">
                                            <table>
                                            <tr>
                                            <td>
                                            <input type="radio" name="excelSheet" value="0" style="display:inline;" checked="checked" id="pt0"/><label for="pt0"> 0 Priority</label>
                                            </td><td style="padding-left:10px">
                                            <input type="radio" name="excelSheet" value="1" style="display:inline;" id="pt1"/><label for="pt1"> 1 Global Priority</label>
                                            </td><td style="padding-left:10px">
                                            <input type="radio" name="excelSheet" value="2" style="display:inline;" id="pt2"/><label for="pt2"> 2 Global</label>
                                            </td>
                                            </tr>
                                            <tr>
                                            <td>
                                            <input type="radio" name="excelSheet" value="3" style="display:inline;" id="pt3"/><label for="pt3"> 3 Standard</label>
                                            </td><td style="padding-left:10px">
                                            <input type="radio" name="excelSheet" value="4" style="display:inline;" id="pt4"/><label for="pt4"> 4 Inv</label>
                                            </td><td style="padding-left:10px">
                                            <input type="radio" name="excelSheet" value="5" style="display:inline;" id="pt5"/><label for="pt5"> 5 Consolidated</label>
                                            </td>
                                            </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:8px;"></div></td></tr>
                                    <tr>
                                        <td align="left"><div id="fillzPackageSubmit"></div></td>
                                    </tr><tr>
                                        <td align="left"><div id="fillzPackageOneItemSubmit"></div></td>
                                    </tr><tr>
                                        <td align="left"><div id="fillzShippingSubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
                </tr><tr>
                <td valign="top" align="left" style="width:100%;margin-left:20px;">
                    <div class="homepanelsearch">
                        <form class="formular" action="bellwether/fillz!importOrders.bc" id="fillzimportform" method="post" style="padding:0px !important;" enctype="multipart/form-data">
                            <fieldset>
                                <legend>Bellwether Fillz Order Import</legend>
                                <table>
                                    <tr>
                                        <td colspan="2">
                                        Fillz Order Excel: <s:file name="upload" id="fillzimportfile" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
                                        </td>
                                    </tr>
                                    <tr><td><div style="height:8px;"></div></td></tr>
                                    <tr>
                                        <td></td>
                                        <td align="right"><div id="fillzImportSubmit"></div></td>
                                    </tr>
                                </table>
                            </fieldset>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                        </form>
                    </div>
                </td>
                </tr>
                
                    <tr>
                        <td valign="top">
                            <div class="homepanelsearch">
                                <form class="formular" action="bellwether/inventory!multiSearch.bc" id="bellmultiisbnsearchform" method="post" style="padding:0px !important;">
                                    <fieldset>
                                        <legend>Bellwether Inventory Search</legend>
                                        <table>
                                            <tr>
                                                <td align="right" valign="top">Multi ISBN: </td>
                                                <td style="padding-left:10px"><textarea name="search.multiIsbn" rows="8"></textarea></td>
                                            </tr>
                                            <tr><td><div style="height:8px;"></div></td></tr>
                                            <tr>
                                                <td></td>
                                                <td align="right"><div id="bellmultiIsbnSearchSubmit"></div></td>
                                            </tr>
                                        </table>
                                    </fieldset>
                                </form>
                            </div>
                        </td>
                    </tr>
                
                </table>
            </td>
            </tr>
        </table>
        
        
        
    </div>

    
</div>


<%@ include file="div-footer.jspf" %>

</body>
</html>
