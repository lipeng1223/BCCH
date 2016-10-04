var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a Back Stock to view it\'s locations...</div>';
var noitemselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a Back Stock  to view it\'s locations...</div>';
var currentlyBlank = true;
var currentDetailId = -1;
var currentItemDetailId = -1;

function historyButtonClick(){
    var selected = Ext.grid.backStockGrid.getSelectionModel().getSelected();
    if (!selected){
        isbnHistoryButtonClick();
    } else {
        showBsHistory("", "backstock_item", selected.get("id"), "Back Stock Item History", "backstock_location", "Back Stock Item History", "Back Stock Locations Audit History");
    }
}

function isbnHistoryButtonClick(){
    Ext.Msg.prompt('ISBN', 'Back Stock History For ISBN:', function(btn, theisbn){
        if (btn == 'ok'){
            // process text value and close...
            showBsHistory(theisbn, "backstock_item", -1, "Back Stock Item History", "backstock_location", "Back Stock Item History", "Back Stock Locations Audit History");
        }
    });
}

function showBsHistory(isbn, tableName, tableId, title, childTableName, auditTitle, childAuditTitle){
    isbn10 = ISBN13toISBN10(isbn);
    auditTitle = auditTitle != undefined && auditTitle.length > 0 ? escape(auditTitle) : ""; 
    childAuditTitle = childAuditTitle != undefined && childAuditTitle.length > 0 ? escape(childAuditTitle) : ""; 
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: title,
        width:800,
        height:540,
        modal:true,
        maximizable:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: {
            url: '/secure/entityhistory.bc?isbn='+isbn10+'&tableId='+tableId+'&tableName='+tableName+"&childTableName="+childTableName+"&auditTitle="+auditTitle+"&childAuditTitle="+childAuditTitle, 
            timeout: 300,
            callback: function(el, success, response, opts){
                if (!success){
                    Ext.Msg.show({
                        title:'Error',
                        msg: 'Could not get the information, it took too long.',
                        buttons: Ext.Msg.OK
                        });
                }
            }
        }
    });
    Ext.auditWindow.show();
}


function createButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Back Stock',
        width:800,
        height:400,
        modal:true,
        closable: false,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'backstock!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.manifestsGrid);
}
function editButtonClick(){
    var selected = Ext.grid.backStockGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Back Stock Item to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Back Stock',
        width:800,
        height:400,
        modal:true,
        closable: false,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'backstock!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.manifestsGrid);
}
function deleteButtonClick(){
    if (Ext.grid.backStockGrid){
        var selected = Ext.grid.backStockGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Back Stock Item to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Back Stock Item: '+selected.get('title')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.backStockGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Back Stock Item...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        blankTheDetail();
                        Ext.grid.backStockGridDs.reload();
                    }
                });
            }
        });
    }
}

function updateBackStockDetail(force){
    var selections = Ext.grid.backStockGrid.getSelectionModel().getSelections();
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
                       url:'backstock!detail.bc',
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

function editBackStockLocation(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'crudwindow',
        title: 'Edit Back Stock Location',
        width:500,
        height:300,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'backstock!editLocation.bc?id='+id
    });
    Ext.crudWindow.show(Ext.grid.backStockGrid);
}
function deleteBackStockLocation(id, location){
    Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Back Stock Location: '+location+'?', function(btn){
        if (btn == "yes"){
            Ext.form.deleteLocationForm.submit({
                params:{'id':id},
                waitMsg:'Deleting Back Stock Location...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    updateBackStockDetail(true);
                    Ext.grid.backStockGridDs.reload();
                }
            });
        }
    });
}
function addBackStockLocation(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'crudwindow',
        title: 'Create Back Stock Location',
        width:500,
        height:300,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'backstock!createLocation.bc?id='+currentDetailId
    });
    Ext.crudWindow.show(Ext.grid.backStockGrid);
}

function exportWithLocationsButtonClick(){
    var ds = Ext.grid.backStockGridDs;
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" Back Stock Items with Locations to Excel?", function(btn, text){
        if (btn == 'yes'){
            
            working = Ext.MessageBox.wait("Exporting Back Stock Items with Locations, Please wait...", "Working");
            
            // now we can take all of the ds params and send it over to the action, the interceptor will take care of the rest
            var idx = ds.proxy.conn.url.indexOf("?");
            var opts = ds.proxy.conn.url.substr(idx+1);
            var loc = "backstock!listData.bc?&exportToExcel=true&exportWithLocationsToExcel=true&"+opts+"&"+Ext.urlEncode(ds.lastOptions.params);
            
            Ext.Ajax.request({
                url: loc,
                timeout: 1200000,
                success: function(result, request){
                    hideWorking();
                    // actually go after the file
                    //console.log("success! Going after file: "+result.responseText);
                    //console.log(inspect(result, 3));
                    var json = Ext.decode(result.responseText);
                    if (json.exportLimitExceeded != undefined && json.exportLimitExceeded){
//                        Ext.MessageBox.alert('Excel Limit Reached', "You cannot execute this export, the Excel row limit of 65536 has been reached.");
                        Ext.MessageBox.alert('Excel Limit Reached', "You cannot execute this export, you can't export entire records.");
                        return;
                    }
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