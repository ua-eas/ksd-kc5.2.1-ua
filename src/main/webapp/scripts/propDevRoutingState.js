jQuery(document).ready(function() {
    jQuery("[title^=ordexp]").click(function(event) {
	event.preventDefault();
	event.stopPropagation();
	showSelector(this);
    });
    jQuery("[title^=spsrew]").click(function(event) {
	event.preventDefault();
	event.stopPropagation();
	showSelector(this);
    });

});


/*
 * Function that shows either SPS or ORDExp selector depending on the passed id
 */
function showSelector(element){
    var isSpsRew = element.title.indexOf("spsrew") > -1;
    if (isSpsRew){ showSpsRewSelector(element);
    } else {
	showOrdExpSelector(element);
    }
}


/*
 * Function that replaces the anchor with the SelectORDExpedited control
 */
function showOrdExpSelector(element) {
    var curentId = element.title //contains propNumber
    var value = element.text
    var selectControl = buildOrdExpSelect(curentId, value);
    // add hidden input with oldValue
    buildHiddenInput(curentId, value);
    //replace anchor with select and focus on it
    jQuery(element).replaceWith( selectControl );
    document.getElementById(curentId).focus();
}



/*
 * Function that replaces the anchor with the SelectSPSReviewer control
 */
function showSpsRewSelector(element) {
    var curentId = element.title //contains propNumber
    var personId = jQuery(element).attr('href');
    var value = element.text //name of SPS Reviewer
    var selectControl = buildSpsRewSelect(curentId, personId, element);
    if (selectControl && selectControl.length>0 ){
	// add hidden input with oldValue
	buildHiddenInput(curentId, personId+":"+value);
	//replace anchor with select and focus on it
	jQuery(element).replaceWith( selectControl ); 
	document.getElementById(curentId).focus();
    }
}


/*
 * Function that builds a hidden input to store previous value for OrdExp or SpsRew attributes
 */
function buildHiddenInput(ctrlName, ctrlValue){
    var hiddenCtrl = "<input type=\"hidden\" name=\""+ctrlName+"\" value=\""+ctrlValue+"\" />";
    jQuery('form').append(hiddenCtrl);
}

/*
 * Function that builds the select for SelectORDExpedited control
 */
function buildOrdExpSelect(curentId, value){
    var selectControl = "<select id=\""+curentId+"\" onchange=\"selectProcessing = true;setORDExpedited('"
    selectControl += curentId+"');\" onblur=\"resetSelectState('"+curentId+"');\" ><option value=\"Yes\"";
    if ( value==="Yes") selectControl += " selected ";
    selectControl +=">Yes</option><option value=\"No\"";
    if ( value==="No") selectControl += " selected ";
    selectControl += ">No</option></select>";
    return selectControl;
}

/*
 * Function that builds the select for SelectORDExpedited control
 */
function buildSpsRewSelect(id, selectedKey, element){
    var optionsArray = parseData(element);

    if ( optionsArray && !jQuery.isEmptyObject(optionsArray)){
	if ( jQuery("#"+id).length ){
	    //select has already been added. Exit
	    return;
	}
	var selectString = "<select id=\""+id+"\" onchange=\"selectProcessing = true;setSPSReviewer('"+id
	selectString += "');\" onblur=\"resetSelectState('"+id+"');\" >";
	selectString +="<option value=\"0\">Select</option>";
	for (var key in optionsArray){
	    selectString +="<option value=\""+key+"\""
	    if ( selectedKey && selectedKey.length>0 && key===selectedKey){
		selectString +=" selected"
	    }
	    selectString +=">"+optionsArray[key]
	    selectString +="</option>"
	}
	selectString +="</select>";
	return selectString;
    }
}

/*
 * Function that builds the hidden cache for the SPSReviewers values
 */
function buildHiddenSPSReviewersData(dataArray){
    var cachedValues="";
    for ( var idx in dataArray){
	var person = dataArray[idx];
	cachedValues += person.principalId+":"+person.fullName+",";
    }
    //remove last comma
    cachedValues = cachedValues.substring(0, cachedValues.length-1);
    if ( jQuery("#SPSReviewersData").length ){
	jQuery("#SPSReviewersData").val(cachedValues);
    } else {
	var hiddenCtrl = "<input type=\"hidden\" id=\"SPSReviewersData\" value=\""+cachedValues+"\" />";
	jQuery('form').append(hiddenCtrl);
    }
}


/*
 * Helper function that parses the SPSReviewers data into an array of key/value pairs for displaying the Select SPSReviewers control
 */
function parseData(element) {
    var result = [];
    if ( !jQuery("#SPSReviewersData").length ){
	getSPSReviewersData(element);
	return;
    }
    var rew = jQuery("#SPSReviewersData").val();
    var arrayOfData = rew.split(",");
    for (var idx in arrayOfData){
	var id = arrayOfData[idx].split(":")[0];
	var name = arrayOfData[idx].split(":")[1];
	result[id]=name;
    }
    return result;
}

/*
 * Function that builds the initial anchor elements for either SPS or OrdExp
 */
function buildAnchor(elementId, valueToSet){
    var isSpsRew = elementId.indexOf("spsrew") > -1;
    if (!valueToSet || 0 === valueToSet.length){
	valueToSet = jQuery("[name="+elementId+"]").val();
    }
    if (isSpsRew){
	var val = valueToSet.split(":");
	var personId = val[0];
	var personName = val[1];
	return "<a href=\""+personId+"\" target=\"_self\" title=\""+elementId+"\">"+personName+"</a>"
    } else {
	//ordExp
	return "<a href=\""+valueToSet+"\" target=\"_self\" title=\""+elementId+"\">"+valueToSet+"</a>";
    }
}


//this flag is set to prevent onBlur when the select is removed from the DOM tree to make an additional call to relesetSelect().
var selectProcessing = false;

/*
 * Function that resets the select control to an anchor when focus is lost or after service call
 */
function resetSelectState( selectId, valueToSet) {
    if (selectProcessing){ return ;}
    try{
	//setting the flag to prevent onBlur fired when ordExpSelect is replaced by the anchor to try and remove the element twice
	selectProcessing = true;
	var selectControl = jQuery('#'+selectId)
	if ( selectControl.length ){
	    var anchorControl=buildAnchor( selectId, valueToSet);
	    //try preventing firing onBlur when select gets replaced - this does not work in Chrome!
	    jQuery(selectControl).off();
	    jQuery(selectControl).replaceWith(anchorControl)
	    jQuery(document).one("click","[title="+selectId+"]",function(event){
		event.preventDefault();
		event.stopPropagation();
		showSelector(this);
	    });  
	}
    } catch (e){
	console.error(e.stack);
    } finally {
	selectProcessing = false;
    }
}

function deleteHiddenCtrl(selectId){
    //clean up and remove hidden control containing old option for this select
    var hiddenCtrl = jQuery("[name="+selectId+"]")
    if ( hiddenCtrl.length ){
	//try preventing firing onBlur when select gets replaced - this does not work in Chrome!
	jQuery(hiddenCtrl).off();
	jQuery(hiddenCtrl).remove();
    }
}


/*
 * Function that retrieves the SPSReviewers options and caches them in a hidden input on the page
 */
function getSPSReviewersData(element){
    var dwrReply = {
	    callback:function(data) {
		if ( data != null && data.length>0) {
		    buildHiddenSPSReviewersData(data);
		    showSpsRewSelector(element);
		} else {
		    console.error("Null data retrieved from PropDevRoutingStateService.findPSReviewers()");
		    alert("Could not retrieve SPS Reviewers information!");
		}
	    },
	    errorHandler:function( errorMessage ) {
		window.status = wrapError(errorMessage);
		alert( "Could not retrieve SPS Reviewers information!\n "+errorMessage );
	    }
    };
    PropDevRoutingStateService.findSPSReviewers(dwrReply);
}

/*
 * Function that sets ORDExpedited attribute on a Proposal
 */
function setORDExpedited( ordExpeditedId ) {
    selectProcessing = true;
    var ordExpeditedField = jQuery('#'+ordExpeditedId)
    //stop the select from handling any events - Note: for some reason, this is not really working...
    jQuery(ordExpeditedField).off();
    var ordExp = ordExpeditedField.val();
    var res = ordExpeditedId.split("_");
    var proposalNumber = res[1];

    var userConfirmation = confirm("Please confirm setting ORDExpedited to "+ordExp.toUpperCase()+ " for proposal "+proposalNumber+".\n");
    if (userConfirmation == true) {
	var dwrReply = {
		callback:function(data) {
		    try{
			deleteHiddenCtrl(ordExpeditedId);
		    } catch (err){
			console.error("Error at resetting ordExpState for "+proposalNumber+"\n");
			console.error(err.stack);
		    } finally {
			selectProcessing = false;
		    }
		},
		errorHandler:function( errorMessage ) {
		    window.status = wrapError(errorMessage);
		    selectProcessing = false;
		    resetSelectState(ordExpeditedId);
		    deleteHiddenCtrl(ordExpeditedId);
		    alert( "Error at setting ORDExpedited to "+ordExp.toUpperCase()+ " for proposal "+proposalNumber+".\n" );
		    console.error(errorMessage);
		}
	};
	PropDevRoutingStateService.setORDExpedited(proposalNumber, Boolean(ordExp==='Yes'), dwrReply);
	selectProcessing = false;
	resetSelectState(ordExpeditedId,ordExp);
    } else {
	selectProcessing = false;
	resetSelectState(ordExpeditedId);
    }
}


/*
 * Function that sets SPSReviewer attribute on a Proposal
 */
function setSPSReviewer( spsRewSelectId ) {
    selectProcessing = true;
    var spsReviewerField = jQuery('#'+spsRewSelectId)
    //stop the select from handling any events - Note: for some reason, this is not really working...
    jQuery(spsReviewerField).off();
    var spsReviewerId = spsReviewerField.val();
    if (spsReviewerId === "0"){
	//user selected empty SPS reviewer - do nothing
	resetSelectState(spsRewSelectId);
	return;
    }
    var spsReviewerName =  jQuery('#'+spsRewSelectId+" option:selected").text();
    var res = spsRewSelectId.split("_");
    var proposalNumber = res[1];

    var userConfirmation = confirm("Please confirm setting SPSReviewer to "+spsReviewerName+ " for proposal "+proposalNumber+".\n");
    if (userConfirmation == true) {
	var dwrReply = {
		callback:function(data) {
		    try{
			deleteHiddenCtrl(spsRewSelectId);
		    } catch (err){
			console.error("Error at resetting Select for "+spsRewSelectId+"\n");
			console.error(err.stack);
		    } finally {
			selectProcessing = false;
		    }
		},
		errorHandler:function( errorMessage ) {
		    window.status = wrapError(errorMessage);
		    alert( "Error at setting SPSReviewer to "+spsReviewerName+ " for proposal "+proposalNumber+".\n" );
		    console.error(errorMessage);
		    selectProcessing = false;
		    resetSelectState(spsRewSelectId);
		    deleteHiddenCtrl(spsRewSelectId);
		}};
	PropDevRoutingStateService.setSPSReviewer(proposalNumber, spsReviewerId, dwrReply);
	var selectedKeyVal = spsReviewerId+":"+spsReviewerName
	selectProcessing = false;
	resetSelectState(spsRewSelectId,selectedKeyVal);
    } else {
	selectProcessing = false;
	resetSelectState(spsRewSelectId);
    }
}

