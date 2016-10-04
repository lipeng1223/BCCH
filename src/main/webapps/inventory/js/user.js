function createButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createuserwindow',
        title: 'Create User',
        width:600,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'user!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.usersGrid);
}
function editButtonClick(){
    var selected = Ext.grid.usersGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a User to edit.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'edituserwindow',
        title: 'Edit User',
        width:600,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'user!edit.bc?id='+selected.get('id')
    });
    Ext.crudWindow.show(Ext.grid.usersGrid);
}
function enableDisableButtonClick(){
    var selected = Ext.grid.usersGrid.getSelectionModel().getSelected();
    if (!selected){
        Ext.Msg.show({
            title:'Error',
            msg: 'You must select a User to Enable / Disable.',
            buttons: Ext.Msg.OK
            });
        return;
    }
    Ext.form.enableDisableForm.submit({
        params:{'id':selected.get('id')},
        waitMsg:'Enabling / Disabling User...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.usersGridDs.reload();
        }
    });
}
function deleteButtonClick(){
    if (Ext.grid.usersGrid){
        var selected = Ext.grid.usersGrid.getSelectionModel().getSelected();
        if (!selected){
            Ext.Msg.show({
                title:'Error',
                msg: 'You must select a User to delete.',
                buttons: Ext.Msg.OK
                });
            return;
        }
        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the User: '+selected.get('username')+'?', confirmDelete);
    }
}
function confirmDelete(btn){
    if (btn == "yes"){
        var selected = Ext.grid.usersGrid.getSelectionModel().getSelected();
        Ext.form.deleteUserForm.submit({
            params:{'id':selected.get('id')},
            waitMsg:'Deleting User...',
            failure: function(form, action){
                Ext.MessageBox.alert('Status', action.result.error);
            },
            success: function(form, action){
                Ext.grid.usersGridDs.reload();
            }
        });
    }
}
