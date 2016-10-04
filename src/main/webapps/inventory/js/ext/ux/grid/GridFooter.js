Ext.namespace("Ext.ux.grid");
/**
 * @class Ext.ux.grid.GridFooter
 * @extends Ext.Toolbar
 * A specialized toolbar that is bound to a {@link Ext.data.Store} and provides a footer message for the grid.
 * @constructor
 * Create a new GridFooter
 * @param {Object} config The config object
 */
Ext.ux.grid.GridFooter = Ext.extend(Ext.Toolbar, {
    /**
     * @cfg {Ext.data.Store} store The {@link Ext.data.Store} the paging toolbar should use as its data source (required).
     */
    /**
     * @cfg {Boolean} displayInfo
     * True to display the displayMsg (defaults to true)
     */
    displayInfo : true,
    /**
     * @cfg {String} displayMsg
     * The paging status message to display (defaults to "Displaying {0} - {1} of {2}").  Note that this string is
     * formatted using the braced numbers 0-2 as tokens that are replaced by the values for start, end and total
     * respectively. These tokens should be preserved when overriding this string if showing those values is desired.
     */
    displayMsg : 'Displaying {0} - {1} of {2}',
    /**
     * @cfg {String} emptyMsg
     * The message to display when no records are found (defaults to "No records to display")
     */
    emptyMsg : 'No records to display',

    initComponent : function(){
        Ext.ux.grid.GridFooter.superclass.initComponent.call(this);
        this.bind(this.store);
    },

    // private
    onRender : function(ct, position){
        Ext.ux.grid.GridFooter.superclass.onRender.call(this, ct, position);
        this.el.setHeight(20);
        this.displayEl = Ext.fly(this.el.dom).createChild({cls:'x-paging-info'});
        this.displayEl.setHeight(18);
    },

    // private
    updateInfo : function(){
        if(this.displayEl){
            var count = this.store.getCount();
            var total = this.store.getTotalCount();
            var msg = total < 1 ? this.emptyMsg :
                String.format(this.displayMsg,(count>0?1:count),count,total);
            this.displayEl.update(msg);
        }
    },

    // private
    onLoad : function(store, r, o){
        if(this.rendered) this.updateInfo();
    },

    /**
     * Unbinds the paging toolbar from the specified {@link Ext.data.Store}
     * @param {Ext.data.Store} store The data store to unbind
     */
    unbind : function(store){
        store = Ext.StoreMgr.lookup(store);
        store.un("load", this.onLoad, this);
        this.store = undefined;
    },

    /**
     * Binds the paging toolbar to the specified {@link Ext.data.Store}
     * @param {Ext.data.Store} store The data store to bind
     */
    bind : function(store){
        store = Ext.StoreMgr.lookup(store);
        store.on("load", this.onLoad, this);
        this.store = store;
    }
});
