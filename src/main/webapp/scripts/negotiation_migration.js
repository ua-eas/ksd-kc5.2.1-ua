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