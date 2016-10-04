var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a customer to view their detail...</div>';
var currentlyBlank = true;
var currentCustomerDetailId = -1;

function createShippingAddress(customerId){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Customer Shipping Address',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'customershipping!create.bc?id='+customerId
    });
    Ext.crudWindow.show(Ext.grid.bellcustomersGrid);
}
function editShippingAddress(shippingAddressId){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Update Customer Shipping Address',
        width:800,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'customershipping!edit.bc?id='+shippingAddressId
    });
    Ext.crudWindow.show(Ext.grid.bellcustomersGrid);
}
function createButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Customer',
        width:800,
        height:540,
        minWidth:800,
        minHeight:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'customer!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.bellcustomersGrid);
}
function editButtonClick(){
    var selected = Ext.grid.bellcustomersGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Customer to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'editwindow',
        title: 'Edit Customer',
        width:800,
        height:540,
        minWidth:800,
        minHeight:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'customer!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.bellcustomersGrid);
}
function deleteButtonClick(){
    if (Ext.grid.bellcustomersGrid){
        var selected = Ext.grid.bellcustomersGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a Customer to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Customer: '+selected.get('companyName')+'?', function(btn){
            if (btn == "yes"){
                var selected = Ext.grid.bellcustomersGrid.getSelectionModel().getSelected();
                Ext.form.deleteForm.submit({
                    params:{'id':selected.get('id')},
                    waitMsg:'Deleting Customer...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.grid.bellcustomersGridDs.reload();
                    }
                });
            }
        });
    }
}

function deleteShippingAddress(shippingAddressId){
    Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this Shipping Address?', function(btn){
        if (btn == "yes"){
            Ext.form.deleteShippingAddressForm.submit({
                params:{'id':shippingAddressId},
                waitMsg:'Deleting Customer Shipping Address...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    updateCustomerDetail(true);
                }
            });
        }
    });
}

function updateCustomerDetail(force){
    var selections = Ext.grid.bellcustomersGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentCustomerDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentCustomerDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'customer!detail.bc',
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
            blankTheCustomerDetail();
        }
    } else {
        blankTheCustomerDetail();
        
    }
}
function blankTheCustomerDetail(){
    if (currentlyBlank) return;
    currentlyBlank = true;
    currentCustomerDetailId = -1;
    var panel = Ext.getCmp('detailpanel');
    if(panel) {
        panel.body.update(noselectionhtml);
    }
}

function salesHistoryButtonClick(){
    var selected = Ext.grid.bellcustomersGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Customer.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    interPageMove("history!sales.bc?id="+selected.get("id"));
}

function titleHistoryButtonClick(){
    var selected = Ext.grid.bellcustomersGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a Customer.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    interPageMove("history!title.bc?id="+selected.get("id"));
}