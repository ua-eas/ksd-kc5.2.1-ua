//replace special word characters with plain text equivalents
var replaceWordChars = function(taElement) {
    var s = taElement.value;
    // smart single quotes and apostrophe
    s = s.replace(/[\u2018\u2019\u201A]/g, "\'")
    // smart double quotes
    s = s.replace(/[\u201C\u201D\u201E]/g, "\"");
    // ellipsis
    s = s.replace(/\u2026/g, "...");
    // dashes
    s = s.replace(/[\u2013\u2014]/g, "-");
    // circumflex
    s = s.replace(/\u02C6/g, "^");
    // open angle bracket
    s = s.replace(/\u2039/g, "<");
    // close angle bracket
    s = s.replace(/\u203A/g, ">");
    // spaces
    s = s.replace(/[\u02DC\u00A0]/g, " ");
    // Replace unicode characters white listed.
    s = s.replace(/[\u2265]/g, ">=");
    s = s.replace(/[\u2264]/g, "<=");
    s = s.replace(/[\u00b1]/g, "+/-");
    // Replace unicode characters with empty string.
    s = s.replace(/[^\u0000-\u007F]*/g, '');
    // Replace all non ascii characters with empty string.
    //s = s.replace(/[^A-valueZa-z 0-9 \.,\?""!@#\$%\^&\*\(\)-_=\+;:<>\/\\\|\}\{\[\]`~]*/g, '');
    taElement.value = s;
    return taElement;
}

jQuery( document ).ready(function() {
	
	/* Show/Hide search criteria on the lookups */
	jQuery("#lookup-criteria-toggle").click(function(event){
		
		jQuery("#lookup-criteria-toggle").toggleClass("icon-arrow-up2");
		jQuery("#lookup-criteria-toggle").toggleClass("icon-arrow-down2");
		
		currentStatus = jQuery( "#lookup-criteria-toggle" ).text();
		if (currentStatus == "hide") {
			jQuery("#lookup-criteria").slideUp();
			jQuery( "#lookup-criteria-toggle" ).text("show");
		}
		else {
			jQuery("#lookup-criteria").slideDown();
			jQuery( "#lookup-criteria-toggle" ).text("hide");
		}
	});

});

/*
 * Load the Lead Unit field based on the Lead Unit passed in.
 */
function loadLeadUnitName(unitNumberFieldName, leadUnitNameFieldName ) {
	var unitNumber = dwr.util.getValue( unitNumberFieldName );

	if (unitNumber=='') {
		clearRecipients( leadUnitNameFieldName, "" );
	} else {
		var dwrReply = {
			callback:function(data) {
				if ( data != null ) {
					if ( leadUnitNameFieldName != null && leadUnitNameFieldName != "" ) {
						setRecipientValue( leadUnitNameFieldName, data );
					}
				} else {
					if ( leadUnitNameFieldName != null && leadUnitNameFieldName != "" ) {
						setRecipientValue(  leadUnitNameFieldName, wrapError( "not found" ), true );
					}
				}
			},
			errorHandler:function( errorMessage ) {
				window.status = errorMessage;
				setRecipientValue( leadUnitNameFieldName, wrapError( "not found" ), true );
			}
		};
		UnitService.getUnitName(unitNumber,dwrReply);
	}
}

function loadFundingSourceAwardNumber(awardNumberElementId, awardNumberHiddenElementId, accountNumberElementId, accountNumberHiddenElementId,
					awardStatusElementId, awardStatusHiddenElementId, amountElementId, amountHiddenElementId,
					obligationExpirationDateElementId, obligationExpirationDateHiddenElementId, sponsorElementId,
					sponsorCodeElementId, sponsorNameElementId, awardDocumentNumberElementId, awardIdElementId) {
    var awardNumber = dwr.util.getValue( awardNumberElementId );
 
    if (awardNumber != null ) {
	//don't perform any searches for an empty field.
	if ( awardNumber.length === 0 || !awardNumber.trim() ){ return };
	
	//don't perform any searches for an already found element.
	var oldAwardNumber = dwr.util.getValue( awardNumberHiddenElementId );
	if ( oldAwardNumber.length>0 && awardNumber.valueOf()===oldAwardNumber.valueOf()){ return };
	
	//TODO: Maybe display busy icon		
	var awardNumberHiddenElement = document.getElementById(awardNumberHiddenElementId);

	var accountNumberElement = document.getElementById(accountNumberElementId);
	var accountNumberHiddenElement = document.getElementById(accountNumberHiddenElementId);

	var awardStatusElement= document.getElementById(awardStatusElementId);
	var awardStatusHiddenElement = document.getElementById(awardStatusHiddenElementId);
	
	var sponsorElement = document.getElementById(sponsorElementId);
	var sponsorCodeElement = document.getElementById(sponsorCodeElementId);
	var sponsorNameElement = document.getElementById(sponsorNameElementId);

	var amountElement = document.getElementById(amountElementId);
	var amountHiddenElement = document.getElementById(amountHiddenElementId);

	var obligationExpirationDateElement = document.getElementById(obligationExpirationDateElementId);
	var obligationExpirationDateHiddenElement = document.getElementById(obligationExpirationDateHiddenElementId);

	var awardIdElement = document.getElementById(awardIdElementId);
	var awardDocumentNumberElement = document.getElementById(awardDocumentNumberElementId);
	var sponsorConcatString = " : ";

	var dwrReply = {
		callback:function(data) {
		    //TODO: Remove busy icon if displayed
		    if ( data != null ) {
			if (awardNumber != null ) awardNumberHiddenElement.value = data.awardNumber;

			if (accountNumberElement != null) accountNumberElement.innerHTML = data.accountNumber;
			if (accountNumberHiddenElement != null) accountNumberHiddenElement.value = data.accountNumber;

			if (awardStatusElement != null) {
			    if (data.statusCode == "1") {
				awardStatusElement.innerHTML = "Active";
			    }
			    else if (data.statusCode == "3") {
				awardStatusElement.innerHTML = "Saved";
			    }
			    else if (data.statusCode == "6") {
				awardStatusElement.innerHTML = "Closed";
			    }
			}
			if (awardStatusHiddenElement != null) {
			    if (data.statusCode == "1") {
				awardStatusHiddenElement.value = "Active";
			    }
			    else if (data.statusCode == "3") {
				awardStatusHiddenElement.value = "Saved";
			    }
			    else if (data.statusCode == "6") {
				awardStatusHiddenElement.value = "Closed";
			    }
			}
			if (amountElement != null) {
			    amountElement.innerHTML = data.obligatedTotalStr;
			    amountElement.value = data.obligatedTotalStr;
			}

			if (amountHiddenElement != null) {
			    amountHiddenElement.innerHTML = data.obligatedTotalStr;
			    amountHiddenElement.value = data.obligatedTotalStr;
			}

			if (obligationExpirationDateElement != null) obligationExpirationDateElement.innerHTML = formattedDate(data.obligationExpirationDate);
			if (awardIdElement != null) awardIdElement.value = data.awardId;
			if (sponsorElement != null) sponsorElement.innerHTML = (data.sponsorCode.concat(sponsorConcatString)).concat(data.sponsorName);
			if (sponsorCodeElement != null) { sponsorCodeElement.value = data.sponsorCode; sponsorCodeElement.innerHTML = data.sponsorCode; }
			if (sponsorNameElement != null) { sponsorNameElement.value = data.sponsorName; sponsorNameElement.innerHTML = data.sponsorName; }

			if (obligationExpirationDateHiddenElement != null) {
			    obligationExpirationDateHiddenElement.value = formattedDate(data.obligationExpirationDate);
			    obligationExpirationDateHiddenElement.innerHTML = formattedDate(data.obligationExpirationDate);
			}
			if (awardDocumentNumberElement != null) {
			    awardDocumentNumberElement.value = data.awardDocument['documentNumber'];
			    awardDocumentNumberElement.innerHTML = data.awardDocument['documentNumber'];
			}
		    }
		    else {
			if (accountNumberElement != null) accountNumberElement.innerHTML = wrapError( "not found" );
			if (awardStatusElement != null) awardStatusElement.innerHTML = wrapError( "not found" );
			if (sponsorElement != null) sponsorElement.innerHTML = wrapError( "not found" );
			if (amountElement != null) amountElement.innerHTML = wrapError( "not found" );
			if (obligationExpirationDateElement != null) obligationExpirationDateElement.innerHTML = wrapError( "not found" );
			if (awardDocumentNumberElement != null) awardDocumentNumberElement.value = "";
			if (awardIdElement != null) awardIdElement.value = "";
		    }
		},
		errorHandler:function( errorMessage ) {
		    window.status = errorMessage;
		    if (accountNumberElement != null) accountNumberElement.innerHTML = wrapError( "not found" );
		    if (awardStatusElement != null) awardStatusElement.innerHTML = wrapError( "not found" );
		    if (sponsorElement != null) sponsorElement.innerHTML = wrapError( "not found" );
		    if (amountElement != null) amountElement.innerHTML = wrapError( "not found" );
		    if (obligationExpirationDateElement != null) obligationExpirationDateElement.innerHTML = wrapError( "not found" );
		    if (awardDocumentNumberElement != null) awardDocumentNumberElement.value = "";
		    if (awardIdElement != null) awardIdElement.value = null;
		}
	};
	AwardService.getActiveOrNewestAward(awardNumber, dwrReply);
    }
}

function formattedDate(date) {
	var dateValue = new Date(date || Date.now()),
		month = '' + (dateValue.getMonth() + 1),
		day   = '' + dateValue.getDate(),
		year  = dateValue.getFullYear();

	if (month.length < 2) month = '0' + month;
	if (day.length < 2)   day   = '0' + day;

	return [month, day, year].join('/');
}