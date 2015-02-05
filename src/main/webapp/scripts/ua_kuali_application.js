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