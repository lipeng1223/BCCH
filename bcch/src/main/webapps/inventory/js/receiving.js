var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a receiving to view it\'s detail...</div>';
var noitemselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a receiving item to view it\'s detail...</div>';
var currentlyBlank = true;
var currentDetailId = -1;
var currentItemDetailId = -1;

function exportCost(id, ponum){
    Ext.MessageBox.confirm("Export", "Export receiving items to Excel for Cost Update?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting data, Please wait...", "Working");
            
            //console.log("exportAction: %s", action+"?exportToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
            
            var loc = "receiving!listItemData.bc?exportToExcel=true&exportCost=true&id="+id+"&filename=ReceivingCost-"+ponum;
            
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

function importCost(id){
    // ?id=
    Ext.Updater.defaults.loadScripts = true;
    Ext.uploadWindow = new Ext.Window({
        id: 'uploadwindow',
        title: 'Upload Receiving Items Cost Update',
        width:600,
        height:200,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receiving!importCostPage.bc?id='+id
    });
    Ext.uploadWindow.show(Ext.grid.receivingitemsGrid);
}

function viewInvItemButtonClick(){
    if (Ext.grid.receivingitemsGrid){
        var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Receiving Item to view it\'s Inventory Item information.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        if (selected.get("inventoryItem_id") == undefined || selected.get("inventoryItem_id").length == 0){
            interPageMove("inventoryitem!view.bc?isbn="+selected.get("isbn")+"&condition="+selected.get("cond"));
        } else {
            interPageMove("inventoryitem!view.bc?id="+selected.get("inventoryItem_id"));
        }
    }    
}
function viewInvItemNewWindowButtonClick(){
    if (Ext.grid.receivingitemsGrid){
        var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Receiving Item to view it\'s Inventory Item information.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        if (selected.get("inventoryItem_id") == undefined || selected.get("inventoryItem_id").length == 0){
            window.open("inventoryitem!view.bc?isbn="+selected.get("isbn")+"&condition="+selected.get("cond"), "_blank");
        } else {
            window.open("inventoryitem!view.bc?id="+selected.get("inventoryItem_id"), "_blank");
        }
    }    
}


function rowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.receivingsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("receiving!view.bc?id="+selected.get("id"));
        }
    }
}

function exportWithItemsButtonClick(){
    var ds = Ext.grid.receivingsGrid.getStore();
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" receivings with items to Excel?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting receivings with items data, Please wait...", "Working");
            
            //console.log("exportAction: %s", action+"?exportToExcel=true&"+Ext.urlEncode(ds.lastOptions.params));
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
         
            var idx = ds.proxy.conn.url.indexOf("?");
            var opts = ds.proxy.conn.url.substr(idx+1);
            var loc = "receiving!listData.bc?exportWithItemsToExcel=true&"+opts+"&"+Ext.urlEncode(ds.lastOptions.params);
            
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
    var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Receiving to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showHistory("received", selected.get("id"), "Receiving Audit History", "received_item", "Receiving Audit History", "Received Items Audit History");
}

function showReceivedItemHistory(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Receiving Item Audit History',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: '/secure/entityhistory.bc?tableId='+id+'&tableName='+"received_item"
    });
    Ext.auditWindow.show();
}
function itemHistoryButtonClick(){
    var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Receiving Item to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showReceivedItemHistory(selected.get("id"));
}


function editButtonClick(){
    var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Receiving to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    if (selected.get('posted') == "true" || selected.get('posted') == true){
        Ext.Msg.show({
            title:'Error',
            msg: 'You cannot edit a posted receiving.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Receiving',
        width:600,
        height:530,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receiving!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.receivingsGrid);
}
function deleteButtonClick(){
    if (Ext.grid.receivingsGrid){
        var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Receiving to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        if (selected.get('posted') == "true" || selected.get('posted') == true){
            Ext.Msg.show({
                title:'Error',
                msg: 'You cannot delete a posted receiving.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Receiving id: '+selected.get('id')+' PO: '+selected.get('poNumber')+' ?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    timeout: 300,
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Receiving...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.receivingsGridDs.reload();
                    }
                });
            }
        });
    }
}

function viewButtonClick(){
    var selections = Ext.grid.receivingsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("receiving!view.bc?id="+selected.get("id"));
        }
    } else {
        Ext.MessageBox.alert('Error', "You must select a receiving to view.");
    }
}

function viewNewWinButtonClick(){
    if (Ext.grid.receivingsGrid){
        var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Inventory Item to view it.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        window.open("receiving!view.bc?id="+selected.get("id"), "_blank");
    }
}

function updateReceivingDetail(force){
    var selections = Ext.grid.receivingsGrid.getSelectionModel().getSelections();
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
                       url:'receiving!detail.bc',
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

function updateRecItemDetail(force){
    var selections = Ext.grid.receivingitemsGrid.getSelectionModel().getSelections();
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
                       url:'receivingitem!detail.bc',
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
    if (Ext.grid.receivingitemsGrid){
        var selModel = Ext.grid.receivingitemsGrid.getSelectionModel();
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
                title: 'Edit Receiving Item',
                width:800,
                height:560,
                modal:true,
                stateful:false,
                autoScroll:true,
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: 'receivingitem!edit.bc?id='+selected.get('id')
            });
            Ext.crudWindow.show(Ext.grid.receivingitemsGrid);
        }
    }    
}
function editItemButtonClick(){
    if (Ext.grid.receivingitemsGrid){
        var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Received Item to edit.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.Updater.defaults.loadScripts = true;
        Ext.crudWindow = new Ext.Window({
            id: 'edititemwindow',
            title: 'Edit Receiving Item',
            width:800,
            height:560,
            modal:true,
            stateful:false,
            autoScroll:true,
            bbar:[],
            bodyStyle:'background-color:#fbfbfb',
            autoLoad: 'receivingitem!edit.bc?id='+selected.get('id')
        });
        Ext.crudWindow.show(Ext.grid.receivingitemsGrid);
    }
}


function deleteItemButtonClick(){
    if (Ext.grid.receivingitemsGrid){
        var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelections();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Received Item to delete.',
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
        
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the selected Received Items?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected();
                Ext.form.deleteItemForm.submit({
                    timeout: 300,
                    params:{'selectionIds':selectionIds},
                    waitMsg:'Deleting Received Items...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.receivingitemsGridDs.reload();
                    }
                });
            }
        });
    } 
}

function unpostButtonClick(){
    if (Ext.grid.receivingsGrid){
        var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a receiving to unpost.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        if (selected.get('posted') == false){
            Ext.Msg.show({
                title:'Error',
                msg: 'This receiving is already unposted.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to unpost the Order: '+selected.get('invoiceNumber')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.receivingsGrid.getSelectionModel().getSelected();
                Ext.form.unpostForm.submit({
                    timeout: 300,
                    params:{'id':selected.get('id')},
                    waitMsg:'Unposting receiving...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.receivingsGrid.reload();
                    }
                });
            }
        });
    }
}