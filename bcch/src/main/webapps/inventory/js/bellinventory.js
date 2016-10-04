var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on an item to view it\'s detail...</div>';
var clickloadhtml = '<div><img src="/images/arrow_up.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on Load to view it\'s Amazon detail...</div>';
var currentlyBlank = true;
var currentItemDetailId = -1;

function rowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.bellinventoryGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("inventory!view.bc?id="+selected.get("id"));
        }
    }
}

function printButtonClick(){
    var selected = Ext.grid.bellinventoryGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Inventory Item to show print view.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showPrintBell(selected.get("id"));
}

function bulkExportButtonClick(){
    var ds = Ext.grid.bellinventoryGrid.getStore();
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" items to Excel?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting bulk change data, Please wait...", "Working");
            
            //console.log("exportAction: %s", action+"?exportToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
            
            var loc = "inventory!listData.bc?exportBulkToExcel=true&"+Ext.urlEncode(ds.lastOptions.params);
            
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

function countExportButtonClick(){
    var ds = Ext.grid.inventoryGrid.getStore();
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" items to Excel?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting count change data, Please wait...", "Working");
            
            //console.log("exportAction: %s", action+"?exportToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
            
            var loc = "inventory!listData.bc?exportCountToExcel=true&"+Ext.urlEncode(ds.lastOptions.params);
            
            Ext.Ajax.request({
                url: loc,
                timeout: 1200000,
                success: function(result, request){
                    hideWorking();
                    // actually go after the file
                    //console.log("success! Going after file: "+result.responseText);
                    //console.log(inspect(result, 3));
                    if (Ext.isIE){
                        var json = Ext.decode(result.responseText);
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
                        var json = Ext.decode(result.responseText);
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

function historyButtonClick(){
    var selected = Ext.grid.bellinventoryGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Inventory Item to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showInventoryHistory(selected.get("id"));
}

function editInventoryItem(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Update Inventory Item',
        width:800,
        height:560,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'inventory!edit.bc?id='+id
    });
    Ext.crudWindow.show();
}
function editButtonClick(){
    var selected = Ext.grid.bellinventoryGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Inventory Item to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    editInventoryItem(selected.get("id"));
}

function deleteInventoryItem(id, isbn, cond){
    Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Inventory Item: '+isbn+' '+cond+'?', function(btn){
        if (btn == "yes"){
            Ext.form.deleteInventoryForm.submit({
                params:{'id':id},
                waitMsg:'Deleting Inventory Item...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    if (Ext.grid.bellinventoryGridDs){
                        Ext.grid.bellinventoryGridDs.reload();
                    } else {
                        interPageMove("inventory!list.bc");
                    }
                }
            });
        }
    });
}
function deleteButtonClick(){
    if (Ext.grid.bellinventoryGrid){
        var selected = Ext.grid.bellinventoryGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Inventory Item to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        deleteInventoryItem(selected.get('id'), selected.get('isbn'), selected.get('cond'));
    }
}

function viewButtonClick(){
    var selected = Ext.grid.bellinventoryGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Inventory Item to view.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    interPageMove("inventory!view.bc?id="+selected.get("id"));
}

function receivingViewButtonClick(){
    var selections = Ext.grid.receivingsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("receiving!view.bc?id="+selected.get("received_id"));
        }
    } else {
        Ext.MessageBox.alert('Error', "You must select a receiving to view.");
    }
}
function receivingRowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.receivingsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("received_id") != null){
            interPageMove("receiving!view.bc?id="+selected.get("received_id"));
        }
    }
}

function orderViewButtonClick(){
    var selections = Ext.grid.ordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("order!view.bc?id="+selected.get("customerOrder_id"));
        }
    } else {
        Ext.MessageBox.alert('Error', "You must select an order to view.");
    }
}
function orderRowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.ordersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("order!view.bc?id="+selected.get("customerOrder_id"));
        }
    }
}

function amzLoadCallback(el, success, response, options){
    if (success){
        var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
        if (amzrefresh) amzrefresh.enable();
    }
}

function updateInventoryDetail(force){
    var selections = selections = Ext.grid.bellinventoryGrid.getSelectionModel().getSelections();
    
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
                       url:'inventory!detail.bc',
                    params: {id:selected.get("id")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
                var amzpanel = Ext.getCmp('amazonpanel');
                if (amzpanel){
                    var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
                    if (amzrefresh) amzrefresh.disable();
                    var amzload = Ext.getCmp('loadAmazonDataButton');
                    if (amzload) amzload.enable();
                    
                    amzpanel.body.update(clickloadhtml);
                    //amzpanel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                    
                    
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
        panel.body.update(noselectionhtml);
    }
    var amzpanel = Ext.getCmp('amazonpanel');
    if (amzpanel){
        amzpanel.body.update(clickloadhtml);
    }
    var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
    if (amzrefresh) amzrefresh.disable();
    var amzload = Ext.getCmp('loadAmazonDataButton');
    if (amzload) amzload.disable();
}

function updateAmazonDetail(anisbn){
    var blankit = false;
    var isbn;
    if (anisbn){
        isbn = anisbn;
    } else {
        var selections = null;
        if (Ext.grid.bellinventoryGrid)
            selections = Ext.grid.bellinventoryGrid.getSelectionModel().getSelections();
        if (Ext.grid.inventoryskusGrid)
            selections = Ext.grid.inventoryskusGrid.getSelectionModel().getSelections();
        
        if (selections && selections.length == 1){
            var selected = selections[0];
            if (selected && selected.get("id") != null){
                isbn = selected.get("isbn");
                var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
                if (amzrefresh) amzrefresh.disable();
                var amzload = Ext.getCmp('loadAmazonDataButton');
                if (amzload) amzload.disable();
            } else {
                blankit = true;
            }
        } else {
            blankit = true;
        }
    }
    if (isbn){
        var amzpanel = Ext.getCmp('amazonpanel');
        if (amzpanel){
            amzpanel.getUpdater().update({
               url:'inventory!amazonDetail.bc',
                params: {isbn:isbn},
                //text: 'Loading Data for: '+selected.get('name'),
                nocache: true,
                timeout: 30,
                scripts: true,
                callback: amzLoadCallback
            });
            //amzpanel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
            
        }
    } else {
        blankit = true;
    }
    
    if (blankit){
        var amzpanel = Ext.getCmp('amazonpanel');
        if (amzpanel){
            amzpanel.body.update(clickloadhtml);
        }
        var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
        if (amzrefresh) amzrefresh.disable();
        var amzload = Ext.getCmp('loadAmazonDataButton');
        if (amzload) amzload.disable();
    }        
}
function updateInventorySkuDetail(force){
    var selections = Ext.grid.inventoryskusGrid.getSelectionModel().getSelections();
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
                       url:'inventory!detail.bc',
                    params: {id:selected.get("bellInventory_id")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
                var amzpanel = Ext.getCmp('amazonpanel');
                if (amzpanel){
                    var amzrefresh = Ext.getCmp('refreshAmazonDataButton');
                    if (amzrefresh) amzrefresh.disable();
                    var amzload = Ext.getCmp('loadAmazonDataButton');
                    if (amzload) amzload.enable();
                    amzpanel.body.update(clickloadhtml);
                    //amzpanel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                    
                    
                }
                
            }
            
        } else {
            blankTheItemDetail();
        }
    } else {
        blankTheItemDetail();
        
    }
}
