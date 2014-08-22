jQuery( document ).ready(function() {
	
	/* Show/Hide the footer */
	jQuery("#footer-toggle").click(function(event){
		event.preventDefault();
		newHeight = "110px";
		if ( jQuery("#footer-toggle").hasClass("icon-arrow-down2") ) {
			newHeight = "30px";
		}
		
		jQuery("#footer-toggle").toggleClass("icon-arrow-up2");
		jQuery("#footer-toggle").toggleClass("icon-arrow-down2");
		
		jQuery("#footer-container").animate({
		    height: newHeight
		  }, 300);
	});

});