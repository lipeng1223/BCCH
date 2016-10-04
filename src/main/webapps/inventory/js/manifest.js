var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a manifest to view it\'s detail...</div>';
var noitemselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a manifest item to view it\'s detail...</div>';
var currentlyBlank = true;
var currentDetailId = -1;
var currentItemDetailId = -1;

function rowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.manifestsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            interPageMove("manifestitem!viewManifest.bc?id="+selected.get("id"));
        }
    }
}

function showManifestHistory(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Manifest Audit History',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: '/secure/entityhistory.bc?tableId='+id+'&tableName='+"manifest"
    });
    Ext.auditWindow.show();
}
function historyButtonClick(){
    var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Manifest to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showHistory("manifest", selected.get("id"), "Manifest Audit History", "manifest_item", "Manifest Audit History", "Manifest Items Audit History");
}

function showManifestItemHistory(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.auditWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Manifest Item Audit History',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: '/secure/entityhistory.bc?tableId='+id+'&tableName='+"manifest_item"
    });
    Ext.auditWindow.show();
}
function itemHistoryButtonClick(){
    var selected = Ext.grid.manifestitemsGrid.getSelectionModel().getSelected();
    if (selected == undefined){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Manifest Item to view history.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    showManifestItemHistory(selected.get("id"));
}


function createButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Manifest',
        width:500,
        height:250,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'manifest!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.manifestsGrid);
}
function editButtonClick(){
    var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Manifest to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Manifest',
        width:500,
        height:250,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'manifest!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.manifestsGrid);
}
function deleteButtonClick(){
    if (Ext.grid.manifestsGrid){
        var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Manifest to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Manifest: '+selected.get('name')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Manifest...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.manifestsGridDs.reload();
                    }
                });
            }
        });
    }
}

function viewButtonClick(){
    if (Ext.grid.manifestsGrid){
        var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Manifest to view.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        interPageMove("manifestitem!viewManifest.bc?id="+selected.get("id"));
    }    
}

function viewNewWinButtonClick(){
    if (Ext.grid.manifestsGrid){
        var selected = Ext.grid.manifestsGrid.getSelectionModel().getSelected();
        if (selected == undefined){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Inventory Item to view it.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        window.open("manifestitem!viewManifest.bc?id="+selected.get("id"), "_blank");
    }
}

function updateManifestDetail(force){
    var selections = Ext.grid.manifestsGrid.getSelectionModel().getSelections();
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
                       url:'manifest!detail.bc',
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

function updateManifestItemDetail(force){
    var selections = Ext.grid.manifestitemsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentItemDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentItemDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('itemdetailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'manifestitem!detail.bc',
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
    var panel = Ext.getCmp('itemdetailpanel');
    if(panel) {
        panel.body.update(noitemselectionhtml);
    }
}


function itemRowDoubleClick(grid, rowIndex, e){
    var selections = Ext.grid.manifestitemsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            Ext.Updater.defaults.loadScripts = true;
            Ext.crudWindow = new Ext.Window({
                id: 'editwindow',
                title: 'Edit Manifest Item',
                width:500,
                height:250,
                minWidth:500,
                minHeight:250,
                modal:true,
                stateful:false,
                autoScroll:true,
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: 'manifestitem!edit.bc?id='+selected.get('id')
            });
            Ext.crudWindow.show(Ext.grid.manifestitemsGrid);
        }
    }
}

function editItemButtonClick(){
    var selected = Ext.grid.manifestitemsGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select an Item to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Manifest Item',
        width:500,
        height:250,
        minWidth:500,
        minHeight:250,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'manifestitem!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.manifestitemsGrid);
}
function deleteItemButtonClick(){
    if (Ext.grid.manifestitemsGrid){
        var selected = Ext.grid.manifestitemsGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select an Item to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Manifest Item: '+selected.get('isbn')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.manifestitemsGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Manifest Item...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        var cmp = Ext.getCmp("detailpanel");
                        if (cmp != undefined && cmp.getUpdater() != undefined) cmp.getUpdater().refresh();
                        Ext.grid.manifestitemsGridDs.reload();
                    }
                });
            }
        });
    }
}
