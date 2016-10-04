Ext.namespace("Ext.ux.grid");
Ext.ux.grid.CheckColumn = function(config){
    Ext.apply(this, config);
    if(!this.id){
        this.id = Ext.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Ext.extend(Ext.ux.grid.CheckColumn, Ext.util.Observable, {
    /**
     * @cfg {Boolean} readonly
     * True to prevent value from being changed (i.e. mouse events are ignored).
     */
    readonly : false,
    
    init : function(grid){
        this.grid = grid;
        this.grid.on('render', function(){
            this.grid.getView().mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
            e.stopEvent();
            var record = this.grid.store.getAt(this.grid.getView().findRowIndex(t));
            var ro = Ext.type(this.readonly) ==  'function' ? this.readonly(record) : this.readonly;
            if (! ro)
                record.set(this.dataIndex, !record.data[this.dataIndex]);
        }
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td'; 
        return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
    }
});