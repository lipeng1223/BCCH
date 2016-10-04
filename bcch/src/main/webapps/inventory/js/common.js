// fixes secure / nonsecure error in IE
Ext.SSL_SECURE_URL='/images/blank.gif';
Ext.BLANK_IMAGE_URL='/images/blank.gif';

function getInternetExplorerVersion()
// Returns the version of Internet Explorer or a -1
// (indicating the use of another browser).
{
  var rv = -1; // Return value assumes failure.
  if (navigator.appName == 'Microsoft Internet Explorer')
  {
    var ua = navigator.userAgent;
    var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
    if (re.exec(ua) != null)
      rv = parseFloat( RegExp.$1 );
  }
  return rv;
}

var ieversion = getInternetExplorerVersion();
if (ieversion > 0){
    try {
        if (Range != undefined && typeof Range.prototype.createContextualFragment == "undefined") {
            Range.prototype.createContextualFragment = function (html) {
                var doc = window.document;
                var container = doc.createElement("div");
                container.innerHTML = html;
                var frag = doc.createDocumentFragment(), n;
                while ((n = container.firstChild)) {
                    frag.appendChild(n);
                }
                return frag;
            };
        }
    } catch (err){}
}

function selectAllForMultiSearch(){
    var val = true;
    var elem = document.getElementById("selectAll");
    if (elem != undefined) val = elem.checked;
    elem = document.getElementById("includeBell");
    if (elem != undefined) elem.checked = val;
    elem = document.getElementById("includeRest");
    if (elem != undefined) elem.checked = val;
    elem = document.getElementById("includeHe");
    if (elem != undefined) elem.checked = val;
}

function showPrintInv(id){
    window.open ("/secure/bookcountry/inventoryitem!print.bc?id="+id, "_blank");
}

function showPrintBell(id){
    window.open ("/secure/bellwether/inventory!print.bc?id="+id, "_blank");
}

function createOrderButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Order',
        width:640,
        height:540,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'order!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.ordersGrid);
}
function createRecButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createwindow',
        title: 'Create Receiving',
        width:600,
        height:530,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receiving!create.bc'
    });
    Ext.crudWindow.show(Ext.grid.receivingsGrid);
}


function showHistory(tableName, tableId, title, childTableName, auditTitle, childAuditTitle){
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
        autoLoad: '/secure/entityhistory.bc?tableId='+tableId+'&tableName='+tableName+"&childTableName="+childTableName+"&auditTitle="+auditTitle+"&childAuditTitle="+childAuditTitle
    });
    Ext.auditWindow.show();
}


function clearGridFilters(tableName){
    try {
        eval("Ext.grid."+tableName+"GridFilters.clearFilters()");
        var grid = Ext.getCmp(tableName+"-gridid");
        grid.getStore().reload();
    }  
    catch(err)
    {
        // could not clear filters
    }
    
}

var searchDatePicker;
function searchDateSelect(searchValId){
    var field = document.getElementById(searchValId);
    
    if (field == undefined) {
        return;
    }
    
    var dpicker = new Ext.DatePicker({
        listeners: {
            select: function(dp, date){
                field.value = date.format('m/d/Y');
                searchDatePicker.close();
            }
        }
    });
    searchDatePicker = new Ext.Window({
       title: 'Select Date',
       modal:true,
       items: dpicker
    });
    searchDatePicker.show();
}

var searchRowNum = 0;
function addSearchRow(prefix){
    searchRowNum++;
    var st = document.getElementById(prefix+'searchTable');
    var root=st.getElementsByTagName('tr')[0].parentNode;//the TBODY
    
    var firstRow = st.getElementsByTagName('tr')[0];
    var aoRow = st.insertRow(-1);
    aoRow.id = prefix+"-andOr-"+searchRowNum;
    var select = document.createElement("select");
    select.setAttribute("name", "search.andOrs");
    var opt1 = document.createElement("option");
    opt1.setAttribute("value", "AND");
    opt1.innerHTML = "And";
    select.appendChild(opt1);
    var opt2 = document.createElement("option");
    opt2.setAttribute("value", "OR");
    opt2.innerHTML = "Or";
    select.appendChild(opt2);
    var aocell = document.createElement("td");
    aocell.appendChild(select);
    aoRow.appendChild(aocell);
    
    var newRow = st.insertRow(-1);
    newRow.id = prefix+"searchRow"+searchRowNum;

    var sub;
    for(var i = 0; i < firstRow.cells.length; i++) {
        // Clone each cell and append clone to new Row
        var cloneCell = firstRow.cells.item(i).cloneNode(true);
        if (cloneCell.firstChild != undefined && cloneCell.firstChild.name == "search.values"){
            sub = cloneCell.firstChild.id.substring(0, cloneCell.firstChild.id.indexOf("searchval")+9);
            cloneCell.firstChild.id = sub+searchRowNum;
        }
        if (cloneCell.firstChild != undefined && cloneCell.firstChild.tagName == "A"){
            cloneCell.firstChild.onclick = function(){
                searchDateSelect(sub+searchRowNum);
                return false;
            };
        }
        newRow.appendChild(cloneCell);
    }

    // Add last cell
    var delCell = newRow.insertCell(-1); // add new cell to hold delete button
    delCell.innerHTML = '<div style="padding-left:5px;"><a href="#" onclick="javascript:deleteSearchRow(\''+prefix+'\', '+searchRowNum+');return false;"><img src="/images/delete.png" title="Remove Search Criteria" /></a></div>';
    
        
}
function deleteSearchRow(prefix, rownum){
    var el = document.getElementById(prefix+"searchRow"+rownum);
    if (el != undefined)
        el.parentNode.removeChild(el);
    el = document.getElementById(prefix+"-andOr-"+rownum);
    if (el != undefined)
        el.parentNode.removeChild(el);
}


function roundNumber(val, decimals){
    return Math.round(val*Math.pow(10,decimals))/Math.pow(10,decimals);
}

function setFocus(id){
    var elem = document.getElementById(id);
    if (elem != null && !elem.disabled){
        elem.focus();
    }
}

function showLargeImage(url){
    //console.log("showLargeImage: "+url);
    if (url && url.length > 0){
        var imagewin = new Ext.Window({
            //title       : title,
            id: 'largeImageWin',
            width       : 700,
            height      : 580,
            autoScroll  : true,
            stateful:false,
            modal       : true,
            constrain:true,
            bbar:[ 
              '->',
              {
              cls:"x-btn-text-icon", 
              iconCls:"cancel_icon", 
              text:'Close', 
              handler:function(){
                  imagewin.close();
              }
            }
            ],
            html        : '<div style="text-align:center;padding:15px;"><img src="'+url+'" border="0"/><br/><br/></div>'
        });
        imagewin.on('show',function(){
            imagewin.center();
        });    
        imagewin.show();
    }
}

/**
 * Used to prevent user from typing in file upload fields in IE
 * From: http://support.microsoft.com/kb/892442
 */
function ieCheckFileUpload() {
    if(!Ext.isIE) return true; // If not IE, return true;
    
    if( event.keyCode == 8 ) {
        return false;
    }
    return true;   
}

function bellConditionRenderer(v, p, record){
    if (v == undefined) return "";
    if (v == 1) {
        return 'Used; Like New';
    } else if (v == 2) {
        return 'Used; Very Good';
    } else if (v == 3) {
        return 'Used; Good';
    } else if (v == 4) {
        return 'Used; Acceptable';
    } else if (v == 5) {
        return 'Collectible; Like New';
    } else if (v == 6) {
        return 'Collectible; Very Good';
    } else if (v == 7) {
        return 'Collectible; Good';
    } else if (v == 8) {
        return 'Collectible; Acceptable';
    } else if (v == 11) {
        return 'New';
    }
    return '';
}

function conditionRenderer(v, p, record){
    if (!v || v.length == 0) return "";
    if (v == "hurt") {
        return '<span class="hurttext">'+v+'</span>';
    } else if (v == "unjacketed") {
        return '<span class="unjacketedtext">'+v+'</span>';
    } else if (v == "overstock") {
        return '<span class="overstocktext">'+v+'</span>';
    }
    return v;
}
function dateRenderer(v, p, record){
    if (!v || v.length == 0) return "";
    var d = new Date(v);
    return d.format("m/d/Y");
}
function dateTimeRenderer(v, p, record){
    if (!v || v.length == 0) return "";
    var d = new Date(v);
    return d.format("m/d/Y h:MM TT");
}
function booleanRenderer(v, p, record){
    if (v == undefined || v.length == 0) return "";
    if (v == "true"){
        return "<span class='greentext'>Yes</span>";
    } else if (v == "false"){
        return "<span class='redtext'>No</span>";
    } else if (v == true){
        return "<span class='greentext'>Yes</span>";
    } else if (v == false){
        return "<span class='redtext'>No</span>";
    }
    return "";
}
function creditTypeRenderer(v, p, record){
    if (v == undefined || v.length == 0) return "";
    if (v == "damage"){
        return "<span class='bluetext'>Damage</span>";
    } else if (v == "shortage"){
        return "<span class='bluetext'>Shortage</span>";
    } else if (v == "recNoBill"){
        return "<span class='greentext'>Received But Not Billed</span>";
    }
    return "";
}

function moneyRenderer(v, p, record){
	if (v == undefined || v.length == 0) return "n/a";
	if (parseFloat(v) < 0){
	    return "<span class='redtext'>"+Ext.util.Format.usMoney(v)+"</span>";
	}
	return "<span class='greentext'>"+Ext.util.Format.usMoney(v)+"</span>";
}

function amzMoneyRenderer(v, p, record){
	if (v == undefined || v.length == 0) return "n/a";
    var divided = parseFloat(v) / 100;
	if (divided < 0){
	    return "<span class='redtext'>"+Ext.util.Format.usMoney(v)+"</span>";
	}
	return "<span class='greentext'>"+Ext.util.Format.usMoney(divided)+"</span>";
}

function percentRenderer(v, p, record){
    //if (!v || v.length == 0) return "n/a";
    if (v == undefined || v.length == 0) return "0%";
    return Math.round(parseFloat(v) * 100)+"%";
}

function percentNoModRenderer(v, p, record){
    //if (!v || v.length == 0) return "n/a";
    if (v == undefined || v.length == 0) return "";
    return Math.round(parseFloat(v))+"%";
}

function moneyRendererRedZero(v, p, record){
    if (v == undefined || v.length == 0) return "n/a";
    if (parseFloat(v) <= 0){
        return "<span class='redtext'>"+Ext.util.Format.usMoney(v)+"</span>";
    }
    return "<span class='greentext'>"+Ext.util.Format.usMoney(v)+"</span>";
}

function moneyRendererRedBoldZero(v, p, record){
    if (v == undefined || v.length == 0) return "n/a";
    if (parseFloat(v) == 0){
        return "<span class='redtext' style='font-weight:bold;'>"+Ext.util.Format.usMoney(v)+"</span>";
    } else if (parseFloat(v) < 0){
        return "<span class='redtext'>"+Ext.util.Format.usMoney(v)+"</span>";
    }
    return "<span class='greentext'>"+Ext.util.Format.usMoney(v)+"</span>";
}


/**
 * Check that s is an integer.  This method allows blank values.
 * @param s The value to check
 * @param allowNeg If true, allow a minus sign in front of the number
 * @return True if s is an integer (or blank)
 */

function checkInt(s, allowNeg){ 
    var re;
    if(allowNeg)
        re = /^[-]?\d+$/; 
    else 
        re = /^\d+$/;
    
    return re.test(s);
}
function checkPositiveInt(s){ 
    if (s.indexOf("e") > -1 || s.indexOf("E") > -1) {
        return false;
    }
    re = /^\d+$/;
    if (re.test(s)){
        return parseInt(s, 10) > 0;
    }
    return false;
}

function checkFloat(s){
    if(isNaN(s)) {
       return false; 
    }
    return true;
}    

function checkStringWithError(val, str){
    if(!val || val.length == 0) {
        Ext.Msg.alert('Error', str+' must be provided.');
        return false;
    }
    if (trimString(val).length == 0){
        Ext.Msg.alert('Error', str+' must be provided.');
        return false;
    }
    return true;
}
function checkPositiveFloatAllowZero(val){
    if (!val) return false;
    if(val.length == 0) {
        return false;
    }
    if (!checkFloat(val)){
        return false;   
    }
    if (val < 0){
        return false;   
    }
    return true;
}
function checkPositiveFloat(val){
    if (!val) return false;
    if(val.length == 0) {
        return false;
    }
    if (!checkFloat(val)){
        return false;   
    }
    if (val <= 0){
        return false;   
    }
    return true;
}
function checkPositiveFloatIfExistsWithError(val, str){
    if (!val) return true;
    if(val.length == 0) {
        return true;
    }

    if (!checkFloat(val)){
        Ext.Msg.alert('Error', str+' must be a numeric value.');
        return false;   
    }
    if (val <= 0){
        Ext.Msg.alert('Error', str+' must be a positive numeric value.');
        return false;   
    }
    return true;
}
function checkPositiveFloatWithError(val, str){
    if(!val || val.length == 0) {
        Ext.Msg.alert('Error', str+' must be provided.');
        return false;
    }

    if (!checkFloat(val)){
        Ext.Msg.alert('Error', str+' must be a numeric value.');
        return false;   
    }
    if (val <= 0){
        Ext.Msg.alert('Error', str+' must be a positive numeric value.');
        return false;   
    }
    return true;
}

function trimString(str){
    if (!str) return str;
    return str.replace(/^\s+|\s+$/g, '');
}

// From: http://en.wikibooks.org/wiki/JavaScript/Best_Practices#Email_validation
function isValidEmail(str) {
      // These comments use the following terms from RFC2822:
      // local-part, domain, domain-literal and dot-atom.
      // Does the address contain a local-part followed an @ followed by a domain?
      // Note the use of lastIndexOf to find the last @ in the address
      // since a valid email address may have a quoted @ in the local-part.
      // Does the domain name have at least two parts, i.e. at least one dot,
      // after the @? If not, is it a domain-literal?
      // This will accept some invalid email addresses
      // BUT it doesn't reject valid ones. 
      var atSym = str.lastIndexOf("@");
      if (atSym < 1) { return false; } // no local-part
      if (atSym == str.length - 1) { return false; } // no domain
      if (atSym > 64) { return false; } // there may only be 64 octets in the local-part
      if (str.length - atSym > 255) { return false; } // there may only be 255 octets in the domain

      // Is the domain plausible?
      var lastDot = str.lastIndexOf(".");
      // Check if it is a dot-atom such as example.com
      if (lastDot > atSym + 1 && lastDot < str.length - 1) { return true; }
      //  Check if could be a domain-literal.
      if (str.charAt(atSym + 1) == '[' &&  str.charAt(str.length - 1) == ']') { return true; }
      return false;
}

function specialCharacterSearch(str, fieldname, quoteCheck){
    if (quoteCheck == null || quoteCheck == true){
        var pos = str.search(/\"/);
        if (pos > -1){
            Ext.Msg.alert('Error', fieldname+' cannot contain double or single quotes.');
            return false;
        }
        pos = str.search(/\'/);
        if (pos > -1){
            Ext.Msg.alert('Error', fieldname+' cannot contain double or single quotes.');
            return false;
        }
    }
    pos = str.search(/>/);
    if (pos > -1){
        Ext.Msg.alert('Error', fieldname+' cannot contain < or > symbols.');
        return false;
    }
    pos = str.search(/</);
    if (pos > -1){
        Ext.Msg.alert('Error', fieldname+' cannot contain < or > symbols.');
        return false;
    }
    pos = str.search(/;/);
    if (pos > -1){
        Ext.Msg.alert('Error', fieldname+' cannot contain semi-colon.');
        return false;
    }
    return true;
}

function formSpecialCharacterCheck(form){
    if (form && form.elements){
        for (var i=0; i < form.elements.length; i++) {
            var elem = form.elements[i];
            if (elem.type == "text" || elem.type == "textarea"){
                if (!specialCharacterSearch(elem.value, "Inputs", false)) {
                    return false;
                }
            }
        }
    }
    return true;
}



function inspect(obj, maxLevels, level){
  var str = '', type, msg;

    // Start Input Validations
    // Don't touch, we start iterating at level zero
    if(level == null)  level = 0;

    // At least you want to show the first level
    if(maxLevels == null) maxLevels = 1;
    if(maxLevels < 1)     
        return 'Error: Levels number must be > 0';

    // We start with a non null object
    if(obj == null)
    return 'Error: Object NULL';
    // End Input Validations

    // Start iterations for all objects in obj
    for(var property in obj)
    {
      // indentation
      for(var i = 0; i < level; i++){
        str += '    ';
      }
      try
      {
          // Show "property" and "type property"
          type =  typeof(obj[property]);
          
          str += '(' + type + ') ' + property;
          if (obj[property]==null) str += ': null';
          else if (type=='string' || type=='number' || type=='boolean') str += ': '+obj[property];
          str += '\n';
          
      //          ( (obj[property]==null)?(': null'):(type=='string'?' = '+obj[property]:'')) + '\n';

          // We keep iterating if this property is an Object, non null
          // and we are inside the required number of levels
          if((type == 'object') && (obj[property] != null) && (level+1 < maxLevels))
          str += inspect(obj[property], maxLevels, level+1);
      }
      catch(err)
      {
        // Is there some properties in obj we can't access? Print it red.
        if(typeof(err) == 'string') msg = err;
        else if(err.message)        msg = err.message;
        else if(err.description)    msg = err.description;
        else                        msg = 'Unknown';

        str += '(Error) ' + property + ': ' + msg +'\n';
      }
    }

    return str;
}    



var pageLoadingMask;
function interPageMove(url){
    
	pageLoadingMask.show();
	/*
    Ext.MessageBox.show({
        closable:false,
        msg:'<span style="font-size:20px;color:#333;">Loading ...</span>',
        buttons:false,
        modal:true
    });
    */
    setTimeout(function(){
        document.location = url;
    }, 500);
}

var working;
function hideWorking(){
    if (working)
        working.hide();
}

function exportGridToExcel(gridId, action){
    var grid = Ext.getCmp(gridId);
    if (grid == null){
        alert("System Error, There is no grid to export to excel!");
        return;
    }
    var ds = grid.getStore();
    
    var cm = grid.getColumnModel();
    
    var loc = action;
    if (loc.indexOf("?", 0) < 0){
        loc = loc+"?";
    } else {
        loc = loc+"&";
    }
    var urlParams = new Object();
    for (var name in ds.lastOptions.params) {
        if (name != "search.multiIsbn")
            urlParams[name] = ds.lastOptions.params[name];
    }
    loc = loc+"exportToExcel=true&"+Ext.urlEncode(urlParams);
    
    var checkboxHtml = '<form action="'+loc+'" id="exportForm-'+gridId+'" method="POST"><table style="margin:10px;">';
    if (ds.baseParams != undefined){
        if (ds.baseParams["search.multiIsbn"] != undefined){
            checkboxHtml += '<input type="hidden" name="search.multiIsbn" value="'+ds.baseParams["search.multiIsbn"]+'"></input>';
        }
    }
    celNameCode = 'A'.charCodeAt(0);
    celNameBase = '';
    for (var i = 0; i < cm.getColumnCount(); i++){
        if (cm.getColumnId(i) == "checker") continue;
        checkboxHtml += '<tr>';
//        checkboxHtml += '<td style="padding-top:5px;"><input type="checkbox" name="exportColumns" value="'+cm.getColumnId(i)+'" checked="checked" id="exportColumn-'+gridId+'-'+i+'"></td>';
        checkboxHtml += '<td style="padding-top:5px;"><input type="checkbox" name="chkExportColumns" value="'+cm.getColumnId(i)+'" checked="checked" id="exportColumn-'+gridId+'-'+i+'"></td>';
        checkboxHtml += '<td><label for="exportColumn-'+gridId+'-'+i+'">'+cm.getColumnHeader(i)+'</label>';
        checkboxHtml += '<input type="hidden" name="exportColumns" value="'+cm.getColumnId(i)+'" id="exportColumn-'+gridId+'-'+i+'"></td>';
        checkboxHtml += '<input type="hidden" name="exportColumnNames" value="'+cm.getColumnId(i)+'" id="exportColumn-'+gridId+'-'+i+'">';
        celName = celNameBase + String.fromCharCode(celNameCode);
        celNameCode++;
        if (celNameCode > 'Z'.charCodeAt(0)){
            celNameCode = 'A'.charCodeAt(0);
            celNameBase = 'A';
        }
            
        checkboxHtml += '<td style="padding-left:8px;"><input type="text" style="width:40px;" name="txtExportColumnNames" value="' + celName + '" onchange="refreshCheck(' + "'exportForm-" + gridId + "'," + i + ')"/></td>';
        checkboxHtml += '</tr>';
    }
    checkboxHtml += '<tr><td><label for="exportColumn-">Start Row</label>' + '</td>';
//    checkboxHtml += '<td><input type="text" name="startRow" value="1" style="width:40px;"/></td>';
    checkboxHtml += '<td></td><td></td>';
    checkboxHtml += '</tr>';
    checkboxHtml += '</table>';
    checkboxHtml += '<div style="padding-left:10px; padding-top:8px;"><label for="exportColumn-">Start Row</label><input type="text" name="startRow" value="1" style="width:40px;"/></div>';
    checkboxHtml += '</form>';
    
    var checkPanel = new Ext.Panel({
        autoScroll: true,
        html: checkboxHtml
    });
    
//    if (ds.getTotalCount() >= 65400){
//        Ext.MessageBox.alert('Error', "You cannot export more than 65400 items.");
//        return;
//    }
    
    Ext.MessageBox.confirm("Export", "Export "+ds.getTotalCount()+" items to Excel?", function(btn, text){
        if (btn == 'yes'){

                var win = new Ext.Window({
                    modal:true,
                    width:400,
                    height:500,
                    title: 'Select the columns to export',
                    layout: 'fit',
                    autoScroll: false,
                    tbar:[ 
                    {
                    cls:"x-btn-text-icon", 
                    iconCls:"accept_icon", 
                    text:'Select All', 
                    handler:function(){
                        var f = document.getElementById("exportForm-"+gridId);
                        celNameCode = 'A'.charCodeAt(0);
                        celNameBase = '';
                        for (var i = 0; i < f.exportColumns.length; i++){
                            f.chkExportColumns[i].checked = true;
                            celName = celNameBase + String.fromCharCode(celNameCode);
                            celNameCode++;
                            if (celNameCode > 'Z'.charCodeAt(0)){
                                celNameCode = 'A'.charCodeAt(0);
                                celNameBase = 'A';
                            }
                            f.txtExportColumnNames[i].value = celName;
                        }
                        }
                    },
                    {
                    cls:"x-btn-text-icon", 
                    iconCls:"cancel_icon", 
                    text:'Select None', 
                    handler:function(){
                        var f = document.getElementById("exportForm-"+gridId);
                        for (var i = 0; i < f.exportColumns.length; i++){
                            f.chkExportColumns[i].checked = false ;
                            f.txtExportColumnNames[i].value = "";
                        }
                        }
                    }
                    ],
                    bbar:[ 
                    '->',
                    {
                    cls:"x-btn-text-icon", 
                    iconCls:"excel_icon", 
                    text:'Export', 
                    handler:function(){
                            var f = document.getElementById("exportForm-"+gridId);
                            for (var i = 0; i < f.exportColumns.length; i++){
                                if (f.chkExportColumns[i].checked == false){
                                    f.exportColumnNames[i].value = '';
                                } else{
                                    if (f.txtExportColumnNames[i].value == ''){
                                        Ext.MessageBox.alert("Input Error", "Please insert export cell names you've checked.");
                                        return;
                                    }
                                    f.exportColumnNames[i].value = f.txtExportColumnNames[i].value.toUpperCase();
                                }
                            }
                            var form = new Ext.form.BasicForm('exportForm-'+gridId, {
                                timeout: 1200000
                            });
                            working = Ext.MessageBox.wait("Exporting data, Please wait...", "Working");
                            form.submit({
                                success: function(form, action){
                                    win.close();
                                    hideWorking();
                                    // actually go after the file
                                    //
                                    var json = Ext.decode(action.response.responseText);
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
                                failure: function(form, action){
                                    win.close();
                                    hideWorking();
                                    Ext.MessageBox.alert('Error', "There was a system error and we could not export your information.");
                                }
                            });

                        }
                    },
                    {
                    cls:"x-btn-text-icon", 
                    iconCls:"cancel_icon", 
                    text:'Cancel', 
                    handler:function(){
                            win.close();
                        }
                    }
                    ],
                    items: checkPanel
                })
                win.show();
        }
    });
    
}

/**
 * Used to prevent user from typing in file upload fields in IE
 * From: http://support.microsoft.com/kb/892442
 */
function ieCheckFileUpload() {
    if(!Ext.isIE) return true; // If not IE, return true;
    
    if( event.keyCode == 8 ) {
        return false;
    }
    return true;   
}




// ext override for minWidth on viewport
/*
Ext.override(Ext.layout.BorderLayout, {
    onLayout : function(ct, target){
        var collapsed;
        if(!this.rendered){
            target.position();
            target.addClass('x-border-layout-ct');
            var items = ct.items.items;
            collapsed = [];
            for(var i = 0, len = items.length; i < len; i++) {
                var c = items[i];
                var pos = c.region;
                if(c.collapsed){
                    collapsed.push(c);
                }
                c.collapsed = false;
                if(!c.rendered){
                    c.cls = c.cls ? c.cls +' x-border-panel' : 'x-border-panel';
                    c.render(target, i);
                }
                this[pos] = pos != 'center' && c.split ?
                    new Ext.layout.BorderLayout.SplitRegion(this, c.initialConfig, pos) :
                    new Ext.layout.BorderLayout.Region(this, c.initialConfig, pos);
                this[pos].render(target, c);
            }
            this.rendered = true;
        }

        var size = target.getViewSize();
        if (size.width < this.minWidth) {
            target.setStyle('width', this.minWidth + 'px');
            size.width = this.minWidth;
            target.up('').setStyle('overflow', 'auto');
        } else {
            target.setStyle('width', '');
        }
        if (size.height < this.minHeight) {
            target.setStyle('height', this.minHeight + 'px');
            size.height = this.minHeight;
            target.up('').setStyle('overflow', 'auto');
        } else {
            target.setStyle('width', '');
        }
        if(size.width < 20 || size.height < 20){ // display none?
            if(collapsed){
                this.restoreCollapsed = collapsed;
            }
            return;
        }else if(this.restoreCollapsed){
            collapsed = this.restoreCollapsed;
            delete this.restoreCollapsed;
        }

        var w = size.width, h = size.height;
        var centerW = w, centerH = h, centerY = 0, centerX = 0;

        var n = this.north, s = this.south, west = this.west, e = this.east, c = this.center;
        if(!c){
            throw 'No center region defined in BorderLayout ' + ct.id;
        }

        if(n && n.isVisible()){
            var b = n.getSize();
            var m = n.getMargins();
            b.width = w - (m.left+m.right);
            b.x = m.left;
            b.y = m.top;
            centerY = b.height + b.y + m.bottom;
            centerH -= centerY;
            n.applyLayout(b);
        }
        if(s && s.isVisible()){
            var b = s.getSize();
            var m = s.getMargins();
            b.width = w - (m.left+m.right);
            b.x = m.left;
            var totalHeight = (b.height + m.top + m.bottom);
            b.y = h - totalHeight + m.top;
            centerH -= totalHeight;
            s.applyLayout(b);
        }
        if(west && west.isVisible()){
            var b = west.getSize();
            var m = west.getMargins();
            b.height = centerH - (m.top+m.bottom);
            b.x = m.left;
            b.y = centerY + m.top;
            var totalWidth = (b.width + m.left + m.right);
            centerX += totalWidth;
            centerW -= totalWidth;
            west.applyLayout(b);
        }
        if(e && e.isVisible()){
            var b = e.getSize();
            var m = e.getMargins();
            b.height = centerH - (m.top+m.bottom);
            var totalWidth = (b.width + m.left + m.right);
            b.x = w - totalWidth + m.left;
            b.y = centerY + m.top;
            centerW -= totalWidth;
            e.applyLayout(b);
        }

        var m = c.getMargins();
        var centerBox = {
            x: centerX + m.left,
            y: centerY + m.top,
            width: centerW - (m.left+m.right),
            height: centerH - (m.top+m.bottom)
        };
        c.applyLayout(centerBox);

        if(collapsed){
            for(var i = 0, len = collapsed.length; i < len; i++){
                collapsed[i].collapse(false);
            }
        }

        if(Ext.isIE && Ext.isStrict){ // workaround IE strict repainting issue
            target.repaint();
        }
    }
});
*/
Ext.LinkButton = Ext.extend(Ext.Button, {
    template: new Ext.Template(
        '<table border="0" cellpadding="0" cellspacing="0" class="x-btn-wrap"><tbody><tr>',
        '<td class="x-btn-left"><i> </i></td><td class="x-btn-center"><a class="x-btn-text" href="{1}" target="{2}">{0}</a></td><td class="x-btn-right"><i> </i></td>',
        "</tr></tbody></table>"),
    onRender:   function(ct, position){
        var btn, targs = [this.text || ' ', this.href, this.target || "_self"];
        if(position){
            btn = this.template.insertBefore(position, targs, true);
        }else{
            btn = this.template.append(ct, targs, true);
        }
        var btnEl = btn.child("a:first");
        btnEl.on('focus', this.onFocus, this);
        btnEl.on('blur', this.onBlur, this);
        this.initButtonEl(btn, btnEl);
        btn.un(this.clickEvent, this.onClick, this);
        Ext.ButtonToggleMgr.register(this);
    }
});

Ext.PaddedLinkButton = Ext.extend(Ext.Button, {
    template: new Ext.Template(
        '<table border="0" cellpadding="0" cellspacing="0" class="x-btn-wrap"><tbody><tr>',
        '<td class="x-btn-left"><i> </i></td><td class="x-btn-center" style="padding-right:20px"><a class="x-btn-text" href="{1}" target="{2}">{0}</a></td><td class="x-btn-right"><i> </i></td>',
        "</tr></tbody></table>"),
    onRender:   function(ct, position){
        var btn, targs = [this.text || ' ', this.href, this.target || "_self"];
        if(position){
            btn = this.template.insertBefore(position, targs, true);
        }else{
            btn = this.template.append(ct, targs, true);
        }
        var btnEl = btn.child("a:first");
        btnEl.on('focus', this.onFocus, this);
        btnEl.on('blur', this.onBlur, this);

        this.initButtonEl(btn, btnEl);
        btn.un(this.clickEvent, this.onClick, this);
        Ext.ButtonToggleMgr.register(this);
    }
});

Ext.Ajax.timeout = 120000;
/*
 * This will catch any requests that sat for too long and would get back the login screen
 */
Ext.Ajax.on({
    /*
    'beforerequest':{
        fn: function(con, options){
            console.log("options: "+inspect(options, 4));
            console.log("con: "+inspect(con, 4));
        }
    }, 
    */
    'requestcomplete': {
        fn: function(con, response, options){
                var sub = response.responseText.substring(0, 15);
            if(sub.indexOf("<!-- LOGIN -->") == 0){
                document.location = "/";
            } else if (sub.indexOf("<!-- 403 -->") == 0){
                document.location = "/error.fi?e=403";
            } else if (sub.indexOf("<!-- 404 -->") == 0){
                document.location = "/error.fi?e=404";
            } else if (sub.indexOf("<!-- 500 -->") == 0){
                document.location = "/error.fi?e=500";
            } else if (sub.indexOf("<!-- ERROR -->") == 0){
                var cs = response.responseText.indexOf('<!-- start error messages -->', 0);
                var ce = response.responseText.indexOf('<!-- end error messages -->', cs);
                var errorText = response.responseText.substring(cs, ce);
                var win = new Ext.Window({
                    id: 'errorwindow',
                    title: 'Error',
                    width:750,
                    height:550,
                    minWidth:750,
                    minHeight:550,
                    modal:true,
                    autoScroll:true,
                    html: errorText,
                    bodyStyle:'background-color:#fff'
                });
                win.show();
            }
        }
    }
});


Ext.override(Ext.grid.RowSelectionModel, {
    selectRow : function(index, keepExisting, preventViewNotify){
        if(this.isLocked() || (index < 0 || index >= this.grid.store.getCount()) ||
            (keepExisting && this.isSelected(index))) return;
        var r = this.grid.store.getAt(index);
        if(r && this.fireEvent("beforerowselect", this, index, keepExisting, r) !== false){
            if(!keepExisting || this.singleSelect){
                this.clearSelections();
            }
            this.selections.add(r);
            this.last = this.lastActive = index;
            if(!preventViewNotify){
                this.grid.getView().onRowSelect(index);
            }
            this.fireEvent("rowselect", this, index, r);
            this.fireEvent("selectionchange", this);
        }
    }
});


/*
*  This is to fix the problem with the grid CheckBoxSelectionModel and Drag and Drog, for some reason they are incompatible
*/
Ext.grid.RowSelectionModel.override({
    // FIX: added this function so it could be overrided in CheckboxSelectionModel
    handleDDRowClick: function(grid, rowIndex, e)
    {
        if(e.button === 0 && !e.shiftKey && !e.ctrlKey) {
            this.selectRow(rowIndex, false);
            grid.view.focusRow(rowIndex);
        }
    },
    
    initEvents: function ()
    {
        if(!this.grid.enableDragDrop && !this.grid.enableDrag){
            this.grid.on("rowmousedown", this.handleMouseDown, this);
        }else{ // allow click to work like normal
            // FIX: made this handler function overrideable
            this.grid.on("rowclick", this.handleDDRowClick, this);
        }

        this.rowNav = new Ext.KeyNav(this.grid.getGridEl(), {
            "up" : function(e){
                if(!e.shiftKey){
                    this.selectPrevious(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive-1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            "down" : function(e){
                if(!e.shiftKey){
                    this.selectNext(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive+1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            scope: this
        });

        var view = this.grid.view;
        view.on("refresh", this.onRefresh, this);
        view.on("rowupdated", this.onRowUpdated, this);
        view.on("rowremoved", this.onRemove, this);
    }
});

Ext.grid.CheckboxSelectionModel.override(
        {
            parentNode:null,
            currentlyChecked: false,
            // FIX: added this function to check if the click occured on the checkbox.
            //      If so, then this handler should do nothing...
            handleDDRowClick: function(grid, rowIndex, e)
            {
                var t = Ext.lib.Event.getTarget(e);
                if (t.className != "x-grid3-row-checker") {
                    Ext.grid.CheckboxSelectionModel.superclass.handleDDRowClick.apply(this, arguments);
                }
            },
            unselectSelectAll: function(){
                if (this.parentNode){
                    if(this.currentlyChecked){
                        var hd = Ext.fly(this.parentNode);
                        //console.log("hasClass: "+hd.hasClass('x-grid3-hd-checker-on'));
                        this.currentlyChecked = false;
                        hd.removeClass('x-grid3-hd-checker-on');
                    }
                }
            },
            onMouseDown : function(e, t){
                if(e.button === 0 && t.className == 'x-grid3-row-checker'){ // Only fire if left-click
                    e.stopEvent();
                    var row = e.getTarget('.x-grid3-row');
                    if(row){
                        var index = row.rowIndex;
                        if(this.isSelected(index)){
                            this.deselectRow(index);
                        }else{
                            this.selectRow(index, true);
                        }
                    }
                }
                this.unselectSelectAll();
            },
            // private
            onHdMouseDown : function(e, t){
                if(t.className == 'x-grid3-hd-checker'){
                    e.stopEvent();
                    this.parentNode = t.parentNode;
                    var hd = Ext.fly(t.parentNode);
                    var isChecked = hd.hasClass('x-grid3-hd-checker-on');
                    if(isChecked){
                        this.currentlyChecked = false;
                        hd.removeClass('x-grid3-hd-checker-on');
                        this.clearSelections();
                    }else{
                        this.currentlyChecked = true;
                        hd.addClass('x-grid3-hd-checker-on');
                        this.selectAll();
                    }
                }
            }
        });

Ext.grid.GridDragZone.override(
{
    getDragData: function (e)
    {
        var t = Ext.lib.Event.getTarget(e);
        var rowIndex = this.view.findRowIndex(t);
        if(rowIndex !== false){
            var sm = this.grid.selModel;
            // FIX: Added additional check (t.className != "x-grid3-row-checker"). It may not
            //      be beautiful solution but it solves my problem at the moment.
            if ( (t.className != "x-grid3-row-checker") && (!sm.isSelected(rowIndex) || e.hasModifier()) ){
                sm.handleMouseDown(this.grid, rowIndex, e);
            }
            return {grid: this.grid, ddel: this.ddel, rowIndex: rowIndex, selections:sm.getSelections()};
        }

        return false;
    }
});

// These next 2 overrides make the id of the html element be what is passed in as id in the ext config
// This allows for easier test script recording / playing

Ext.override(Ext.Button, {
    initButtonEl : function(btn, btnEl){
        this.el = btn;
        btn.addClass("x-btn");
    
        if(this.id){
            //this.el.dom.id = this.el.id = this.id;
            // override
            btnEl.dom.id = btnEl.id = this.id;
            // end override
        }
        if(this.icon){
            btnEl.setStyle('background-image', 'url(' +this.icon +')');
        }
        if(this.iconCls){
            btnEl.addClass(this.iconCls);
            if(!this.cls){
                btn.addClass(this.text ? 'x-btn-text-icon' : 'x-btn-icon');
            }
        }
        if(this.tabIndex !== undefined){
            btnEl.dom.tabIndex = this.tabIndex;
        }
        if(this.tooltip){
            if(typeof this.tooltip == 'object'){
                Ext.QuickTips.register(Ext.apply({
            target: btnEl.id
        }, this.tooltip));
        } else {
        btnEl.dom[this.tooltipType] = this.tooltip;
        }
        }
        
        if(this.pressed){
        this.el.addClass("x-btn-pressed");
        }
        
        if(this.handleMouseEvents){
        btn.on("mouseover", this.onMouseOver, this);
        // new functionality for monitoring on the document level
        //btn.on("mouseout", this.onMouseOut, this);
        btn.on("mousedown", this.onMouseDown, this);
        }
        
        if(this.menu){
        this.menu.on("show", this.onMenuShow, this);
        this.menu.on("hide", this.onMenuHide, this);
        }
        
        if(this.repeat){
        var repeater = new Ext.util.ClickRepeater(btn,
        typeof this.repeat == "object" ? this.repeat : {}
        );
        repeater.on("click", this.onClick,  this);
        }
        
        btn.on(this.clickEvent, this.onClick, this);
    }
});
Ext.override(Ext.menu.Item, {
    onRender : function(container, position){
        var el = document.createElement("a");
        el.hideFocus = true;
        el.unselectable = "on";
        el.href = this.href || "#";
        if(this.hrefTarget){
            el.target = this.hrefTarget;
        }
        el.className = this.itemCls + (this.menu ?  " x-menu-item-arrow" : "") + (this.cls ?  " " + this.cls : "");
        // override
        if (this.id){
            el.id = this.id;
        }
        // end override
        el.innerHTML = String.format(
                '<img src="{0}" class="x-menu-item-icon {2}" />{1}',
                this.icon || Ext.BLANK_IMAGE_URL, this.itemText||this.text, this.iconCls || '');
        this.el = el;
        Ext.menu.Item.superclass.onRender.call(this, container, position);
    }    
});

//see http://extjs.com/forum/showthread.php?p=97692#post97692
Ext.Panel.prototype.afterRender = Ext.Panel.prototype.afterRender.createInterceptor(function() {
    if(this.autoScroll && Ext.isIE) {
        this.body.dom.style.position = 'relative';
    }
});

Ext.override(Ext.menu.Menu, {
    showAt : function(xy, parentMenu, /* private: */_e)
    {
      this.parentMenu = parentMenu;
      if (!this.el)
      {
        this.render();
      }
      if (_e !== false)
      {
        this.fireEvent("beforeshow", this);
        xy = this.el.adjustForConstraints(xy);
      }
      this.el.setXY(xy);
      this.assertMenuHeight(this);
      this.el.show();
      this.hidden = false;
      this.focus();
      this.fireEvent("show", this);
    },

    assertMenuHeight : function(m)
    {
      var maxHeight = Ext.getBody().getHeight() - 120;
      if (m.el.getHeight() > maxHeight)
      {
        m.el.setHeight(maxHeight);
        m.el.applyStyles('overflow-y:auto;');
      }
    }
  });




/*
 * Converts a isbn10 number into a isbn13.
 * The isbn10 is a string of length 10 and must be a legal isbn10. No dashes.
 */
function ISBN10toISBN13(isbn10) {
     
    var sum = 38 + 3 * (parseInt(isbn10[0]) + parseInt(isbn10[2]) + parseInt(isbn10[4]) + parseInt(isbn10[6]) 
                + parseInt(isbn10[8])) + parseInt(isbn10[1]) + parseInt(isbn10[3]) + parseInt(isbn10[5]) + parseInt(isbn10[7]);
     
    var checkDig = (10 - (sum % 10)) % 10;
     
    return "978" + isbn10.substring(0, 9) + checkDig;
}
 
/*
 * Converts a isbn13 into an isbn10.
 * The isbn13 is a string of length 13 and must be a legal isbn13. No dashes.
 */
function ISBN13toISBN10(isbn13) {
 
    if (isbn13.length == 10) return isbn13;
    
    var start = isbn13.substring(3, 12);
    var sum = 0;
    var mul = 10;
    var i;
     
    for(i = 0; i < 9; i++) {
        sum = sum + (mul * parseInt(start[i]));
        mul -= 1;
    }
     
    var checkDig = 11 - (sum % 11);
    if (checkDig == 10) {
        checkDig = "X";
    } else if (checkDig == 11) {
        checkDig = "0";
    }
     
    return start + checkDig;
}

function refreshCheck(form, index){
    var f = document.getElementById(form);
    if (f.txtExportColumnNames[index].value == ''){
        f.chkExportColumns[index].checked = false;
    } else{
        f.chkExportColumns[index].checked = true;
    }
}