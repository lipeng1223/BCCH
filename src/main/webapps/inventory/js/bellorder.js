var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on an order to view it\'s detail...</div>';
var noitemselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on an order item to view it\'s detail...</div>';
var currentlyBlank = true;
var currentDetailId = -1;
var currentOrderItemSelection = 0;
var salesHistoryCustomerId;
var currentItemDetailId = -1;

function filledRowColors(rec, index, rowParams, store){
    var f = rec.get("filled");
    var q = rec.get("quantity");
    if (f != undefined && q != undefined && f < q){
        return "redrow";
    }
}


function exportOrderPicklist(id){
    working = Ext.MessageBox.wait("Exporting order pick list, Please wait...", "Working");
    
    //console.log("exportAction: %s", action+"?exportToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
    // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
    
    var loc = "orderpicklist!excel.bc?exportToExcel=true&id="+id;
    
    Ext.Ajax.request({
        url: loc,
        timeout: 1200000,
        success: function(result, request){
            hideWorking();
            // actually go after the file
            //console.log("success! Going after file: "+result.responseText);
            //console.log(inspect(result, 3));
            var json = Ext.decode(result.responseText);
            if (Ext.isIE){
                var exportWin = new Ext.Window({
                    title       : 'Download Export',
                    id: 'excelExportWindow',
                    width       : 325,
                    height      : 100,
                    stateful:false,
                    modal       : true,
                    html : '<div style="text-align:center;padding:15px;"><a href="/getfile.exp?fname='+json.filename+'" onClick="Ext.getCmp(\'excelExportWindow\').close();" style="font-size:14px;"><img src="/images/page_white_excel.png"/> Click Here To Download Excel Export <img src="/images/page_white_excel.png"/></a></div>'
                });
                exportWin.show();
            } else {
                document.location.href="/getfile.exp?fname="+json.filename;
            }
        },
        failure: function(result, request){
            hideWorking();
            Ext.MessageBox.alert('Error', "There was a system error and we could not export your information.");
        }
    });
    
}

function exportWithItemsButtonClick(){
    var ds = Ext.grid.bellordersGrid.getStore();
    //console.log("order!listData.bc?exportWithItemsToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
    //console.log(ds.proxy.conn.url);
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" orders with items to Excel?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting orders with items data, Please wait...", "Working");
            
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
            
            var idx = ds.proxy.conn.url.indexOf("?");
            var opts = ds.proxy.conn.url.substr(idx+1);
            var loc = "order!listData.bc?exportWithItemsToExcel=true&"+opts+"&"+Ext.urlEncode(ds.lastOptions.params);
            
            // to handle sales history item export
            if (salesHistoryCustomerId != undefined){
                loc += "&customerId="+salesHistoryCustomerId;
            }
            
            Ext.Ajax.request({
                url: loc,
                timeout: 1200000,
                success: function(result, request){
                    hideWorking();
                    // actually go after the file
                    //console.log("success! Going after file: "+result.responseText);
                    //console.log(inspect(result, 3));
                    var json = Ext.decode(result.responseText);
                    if (Ext.isIE){
                        var exportWin = new Ext.Window({
                            title       : 'Download Export',
                            id: 'excelExportWindow',
                            width       : 325,
                            height      : 100,
                            stateful:false,
                            modal       : true,
                            html : '<div style="text-align:center;padding:15px;"><a href="/getfile.exp?fname='+json.filename+'" onClick="Ext.getCmp(\'excelExportWindow\').close();" style="font-size:14px;"><img src="/images/page_white_excel.png"/> Click Here To Download Excel Export <img src="/images/page_white_excel.png"/></a></div>'
                        });
                        exportWin.show();
                    } else {
                        document.location.href="/getfile.exp?fname="+json.filename;
                    }
                },
                failure: function(result, request){
                    hideWorking();
                    Ext.MessageBox.alert('Error', "There was a system error and we could not export your information.");
                }
            });
        }
    });
    
}

function historyButtonClick(){
    var selected = Ext.grid.bellordersGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Order to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showHistory("customer_order", selected.get("id"), "Order Audit History", "customer_order_item", "Order Audit History", "Order Items Audit History");
}

function showOrderItemHistory(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Order Item Audit History',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: '/secure/entityhistory.bc?tableId='+id+'&tableName='+"customer_order_item"
    });
    Ext.auditWindow.show();
}
function itemHistoryButtonClick(){
    var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Order Item to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showOrderItemHistory(selected.get("id"));
}

function editButtonClick(){
    var selected = Ext.grid.bellordersGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Order to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    if (selected.get('posted') == "true"){
        Ext.Msg.show({
            title:'Error',
            msg: 'You cannot edit a posted order.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    if (selected.get('category') == "Amazon"){
        Ext.Msg.show({
            title:'Error',
            msg: 'You cannot edit an Amazon order.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Order',
        width:640,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'order!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.bellordersGrid);
}
function deleteButtonClick(){
    if (Ext.grid.bellordersGrid){
        var selected = Ext.grid.bellordersGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        if (selected.get('posted') == "true"){
            Ext.Msg.show({
                title:'Error',
                msg: 'You cannot delete a posted order.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Order: '+selected.get('invoiceNumber')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.bellordersGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    timeout: 300,
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Order...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.bellordersGridDs.reload();
                    }
                });
            }
        });
    }
}

function quickViewButtonClick(){
    var selections = Ext.grid.bellordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            Ext.Updater.defaults.loadScripts = true;
            Ext.crudWindow = new Ext.Window({
                id: 'itemswindow',
                title: selected.get('poNumber')+' - Order Items',
                width:750,
                height:540,
                modal:true,
                stateful:false,
                autoScroll:true,
                layout:'fit',
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: 'order!quickItems.bc?id='+selected.get('id')
            });
            Ext.crudWindow.show(Ext.grid.bellordersGrid);
        }
    } else {
        Ext.MessageBox.alert('Error', "You must select an order to view it's items.");
    }
}

function viewButtonClick(){
    var selections = Ext.grid.bellordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("order!view.bc?id="+selected.get("id"));
        }
    } else {
        Ext.MessageBox.alert('Error', "You must select an order to view.");
    }
}

function rowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.bellordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("order!view.bc?id="+selected.get("id"));
        }
    }
}

function updateTitleInventoryItemDetail(force){
    var selections = Ext.grid.belltitlesGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'inventory!detail.bc',
                    params: {id:selected.get("bellInventory_id")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
            }
            
        } else {
            blankTheDetail();
        }
    } else {
        blankTheDetail();
    }
}

function updateOrderDetail(force){
    var selections = Ext.grid.bellordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'order!detail.bc',
                    params: {id:selected.get("id")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
            }
            
        } else {
            blankTheDetail();
        }
    } else {
        blankTheDetail();
    }
}
function blankTheDetail(){
    if (currentlyBlank) return;
    currentlyBlank = true;
    currentDetailId = -1;
    var panel = Ext.getCmp('detailpanel');
    if(panel) {
        panel.body.update(noselectionhtml);
    }
}

function updateOrderItemDetail(force){
    var selections = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentItemDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentItemDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'orderitem!detail.bc',
                    params: {id:selected.get("id")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
            }
            
        } else {
            blankTheItemDetail();
        }
    } else {
        blankTheItemDetail();
        
    }
}
function blankTheItemDetail(){
    if (currentlyBlank) return;
    currentlyBlank = true;
    currentItemDetailId = -1;
    var panel = Ext.getCmp('detailpanel');
    if(panel) {
        panel.body.update(noitemselectionhtml);
    }
}


function editNextItem(){
    if (Ext.grid.bellorderitemsGrid){
        var selModel = Ext.grid.bellorderitemsGrid.getSelectionModel();
        if (!selModel.hasNext()){
            Ext.Msg.show({
                title:'Error',
                msg: 'There are no more items on this page.',
                buttons: Ext.Msg.OK
                });
            return;
        } else {
            selModel.selectNext();
            var selected = selModel.getSelected();
            Ext.Updater.defaults.loadScripts = true;
            Ext.crudWindow = new Ext.Window({
                id: 'edititemwindow',
                title: 'Edit Order Item',
                width:800,
                height:560,
                modal:true,
                stateful:false,
                autoScroll:true,
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: 'orderitem!edit.bc?id='+selected.get('id')
            });
            Ext.crudWindow.show(Ext.grid.bellorderitemsGrid);
        }
    }    
}
function editItemButtonClick(){
    if (Ext.grid.bellorderitemsGrid){
        var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order Item to edit.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.Updater.defaults.loadScripts = true;
        Ext.crudWindow = new Ext.Window({
            id: 'edititemwindow',
            title: 'Edit Order Item',
            width:600,
            height:500,
            modal:true,
            stateful:false,
            autoScroll:true,
            bbar:[],
            bodyStyle:'background-color:#fbfbfb',
            autoLoad: 'orderitem!edit.bc?id='+selected.get('id')
        });
        Ext.crudWindow.show(Ext.grid.bellorderitemsGrid);
    }
}


function deleteItemButtonClick(){
    if (Ext.grid.bellorderitemsGrid){
        var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelections();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order Item to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        
        var selectionIds = "";
        function addToSelectionIds(record, index, allItems){
            if (index > 0){
                selectionIds = selectionIds + ",";
            }
            selectionIds = selectionIds + record.get("id");
        }
        Ext.each(selected, addToSelectionIds);
        
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the selected Order Items?', function(btn){
            if (btn == "yes"){
                Ext.form.deleteItemForm.submit({
                    timeout: 300,
                    params:{'selectionIds':selectionIds},
                    waitMsg:'Deleting Order Items...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.getCmp("orderDetailPanel").getUpdater().refresh();
                        Ext.grid.bellorderitemsGridDs.reload();
                    }
                });
            }
        });
    }
}


function viewInvItemButtonClick(){
    if (Ext.grid.bellorderitemsGrid){
        var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order Item to view it\'s Inventory Item information.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        interPageMove("inventory!view.bc?id="+selected.get("bellInventory_id"));
    }    
}
function viewInvItemNewWindowButtonClick(){
    if (Ext.grid.bellorderitemsGrid){
        var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order Item to view it\'s Inventory Item information.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        window.open("inventory!view.bc?id="+selected.get("bellInventory_id"), "_blank");
    }    
}

function itemRowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            Ext.Updater.defaults.loadScripts = true;
            Ext.crudWindow = new Ext.Window({
                id: 'edititemwindow',
                title: 'Order Item Shipped',
                width:600,
                height:400,
                modal:true,
                stateful:false,
                autoScroll:true,
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: {url:'orderitem!shipped.bc?id='+selected.get('id'), params: Ext.grid.bellorderitemsGridDs.lastOptions.params}
            });
            Ext.crudWindow.show(Ext.grid.bellorderitemsGrid);
        }
    }
}

function editItemShippedButtonClick(){
    if (Ext.grid.bellorderitemsGrid){
        var selected = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Order Item to set shipped.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.Updater.defaults.loadScripts = true;
        Ext.crudWindow = new Ext.Window({
            id: 'edititemwindow',
            title: 'Order Item Shipped',
            width:600,
            height:400,
            modal:true,
            stateful:false,
            autoScroll:true,
            bbar:[],
            bodyStyle:'background-color:#fbfbfb',
            autoLoad: {url:'orderitem!shipped.bc?id='+selected.get('id'), params: Ext.grid.bellorderitemsGridDs.lastOptions.params}
        });
        Ext.crudWindow.show(Ext.grid.bellorderitemsGrid);
        
        
        
        /*
        Ext.shippedWindow = new Ext.Window({
            id: 'shippedwindow',
            title: 'Set Shipped',
            width:340,
            height:150,
            modal:true,
            stateful:false,
            autoScroll:true,
            layout:'fit',
            bbar:['->',{
                text: 'Set Shipped',
                iconCls: 'accept_icon',
                handler: function(){
                    if (!Ext.getCmp('shippedField').isValid()){
                        Ext.Msg.alert('Error', 'Shipped must be a number.');
                        return;
                    }
                    Ext.form.shippedForm.submit({
                        params:{'id':selected.get("id"), 'shipped':Ext.getCmp("shippedField").getValue()},
                        waitMsg:'Setting Ship Date...',
                        failure: function(form, action){
                            Ext.MessageBox.alert('Status', action.result.error);
                        },
                        success: function(form, action){
                            Ext.shippedWindow.close();
                            Ext.grid.bellorderitemsGridDs.reload();
                        }
                    });
                }
            }, '-', {
                text: 'Cancel',
                iconCls: 'delete_icon',
                handler: function(){
                    Ext.shippedWindow.close();
                }
            }],
            bodyStyle:'background-color:#fbfbfb',
            items: [ {
                 xtype: 'form',
                 layout: 'form',
                 labelAlign: 'right',
                 bodyBorder:false,
                 bodyStyle:'background-color:#fbfbfb;padding:15px;',
                 items: [{
                     xtype: 'numberfield',
                     name: 'shipped',
                     allowDecimals: false,
                     id: 'shippedField',
                     fieldLabel: 'Shipped',
                     value: selected.get('filled')
                 }]
            }]
        });
        Ext.shippedWindow.show();
        Ext.get("shippedField").addListener('keyup', function(evt, t, o){
            if (evt.getKey() == 13){
                if (evt.getKey() == 13){
                    if (!Ext.getCmp('shippedField').isValid()){
                        Ext.Msg.alert('Error', 'Shipped must be a number.');
                        return;
                    }
                    Ext.form.shippedForm.submit({
                        params:{'id':selected.get("id"), 'shipped':Ext.getCmp("shippedField").getValue()},
                        waitMsg:'Setting Ship Date...',
                        failure: function(form, action){
                            Ext.MessageBox.alert('Status', action.result.error);
                        },
                        success: function(form, action){
                            Ext.shippedWindow.close();
                            Ext.grid.bellorderitemsGridDs.reload();
                        }
                    });
                }
            }
        });
        Ext.getCmp('shippedField').focus(true, true);
        */

    }
}


function promptForComment(orderId, invNum){
    Ext.MessageBox.prompt('Comment', 'Enter The Comment:', function(btn, text){
        if (btn == 'ok'){
            
            if (text == null || trimString(text).length == 0){
                Ext.Msg.alert('Error', 'Comment must be provided.');
                return;                
            }
            
            document.location = "orderpicklist.bc?comment="+escape(text)+"&id="+orderId+"&filename=OrderPicklist-"+invNum+".pdf";
        }
    }, null, true);
}

function showInventoryHistory(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Inventory Item Audit History',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: '/secure/entityhistory.bc?tableId='+id+'&tableName='+"inventory_item"
    });
    Ext.auditWindow.show();
}