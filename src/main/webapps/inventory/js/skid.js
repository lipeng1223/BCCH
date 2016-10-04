var noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on a skid to view it\'s detail...</div>';
var currentlyBlank = true;
var currentDetailId = -1;

function createButtonClick(){
    
}

function editButtonClick(){
    
}

function deleteButtonClick(){
    
}

function viewButtonClick(){
    
}

function updateSkidDetail(force){
    var selections = Ext.grid.skidsGrid.getSelectionModel().getSelections();
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
                       url:'skid!detail.bc',
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

