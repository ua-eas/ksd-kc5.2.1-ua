//replace special word characters with value text equivalents
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
    // Replace unicode characters with an empty string.
    s = s.replace(/[^A-valueZa-z 0-9 \.,\?""!@#\$%\^&\*\(\)-_=\+;:<>\/\\\|\}\{\[\]`~]*/g, '');
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