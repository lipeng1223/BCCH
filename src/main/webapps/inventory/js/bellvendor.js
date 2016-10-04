var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a vendor to view their detail...</div>';
var currentlyBlank = true;
var currentVendorDetailId = -1;

function createButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Vendor',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'vendor!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.bellvendorsGrid);
}
function editButtonClick(){
    var selected = Ext.grid.bellvendorsGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Vendor to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Vendor',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'vendor!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.bellvendorsGrid);
}
function deleteButtonClick(){
    if (Ext.grid.bellvendorsGrid){
        var selected = Ext.grid.bellvendorsGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Vendor to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Vendor: '+selected.get('vendorName')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.bellvendorsGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Vendor...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.bellvendorsGridDs.reload();
                    }
                });
            }
        });
    }
}

function createSkidType(vendorId){
    Ext.MessageBox.prompt('New Skid Type', 'Skid Type:', function(btn, text){
        if (btn == 'ok'){
            
            if (text == null || trimString(text).length == 0){
                Ext.Msg.alert('Error', 'Skid Type must be provided.');
                return;                
            }
            
            if(text.length >= 50) {
                Ext.Msg.alert('Error', 'Skid Type is too long, please try something shorter. (50 character max)');
                return;
            }

            Ext.form.createSkidTypeForm.submit({
                params:{'id':vendorId,'vendorSkidType.skidtype':text},
                waitMsg:'Creating Skid Type...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Error', action.result.error);
                },
                success: function(form, action){
                    updateVendorDetail(true);
                }
            });
        }
    });
}
function editSkidType(skidTypeId, thetype){
    Ext.MessageBox.prompt('Edit Skid Type', 'Skid Type:', function(btn, text){
        if (btn == 'ok'){
            
            if (text == null || trimString(text).length == 0){
                Ext.Msg.alert('Error', 'Skid Type must be provided.');
                return;                
            }
            
            if(text.length >= 50) {
                Ext.Msg.alert('Error', 'Skid Type is too long, please try something shorter. (50 character max)');
                return;
            }

            Ext.form.updateSkidTypeForm.submit({
                params:{'id':skidTypeId,'vendorSkidType.skidtype':text},
                waitMsg:'Updating Skid Type...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Error', action.result.error);
                },
                success: function(form, action){
                    updateVendorDetail(true);
                }
            });
        }
    }, this, false, thetype);
}

function deleteSkidType(skidTypeId){
    Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this Skid Type?', function(btn){
        if (btn == "yes"){
            Ext.form.deleteSkidTypeForm.submit({
                params:{'id':skidTypeId},
                waitMsg:'Deleting Vendor Skid Type...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    updateVendorDetail(true);
                }
            });
        }
    });
}

function updateVendorDetail(force){
    var selections = Ext.grid.bellvendorsGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentVendorDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentVendorDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'vendor!detail.bc',
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
            blankTheVendorDetail();
        }
    } else {
        blankTheVendorDetail();
        
    }
}
function blankTheVendorDetail(){
    if (currentlyBlank) return;
    currentlyBlank = true;
    currentVendorDetailId = -1;
    var panel = Ext.getCmp('detailpanel');
    if(panel) {
        panel.body.update(noselectionhtml);
    }
}