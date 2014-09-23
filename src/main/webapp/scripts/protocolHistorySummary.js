jQuery(document).ready(function(){
	var protocolActionsARHistoryLoaded = false;
	jQuery('#innerTab-head-\\:AmendmentRenewalHistory input[type="image"]').click(function(){
		
		if (!protocolActionsARHistoryLoaded) {
			createLoading(true);
			
			var reqParams = {
					'methodToCall': 'ajaxLoadVersionHistory'
			}
			
			jQuery.each(jQuery('#kualiForm').serializeArray(), function(_, kv) {
			  reqParams[kv.name] = kv.value;
			});
			
			jQuery( '#tab-\\:AmendmentRenewalHistory-div' ).load( 'protocolProtocolActions.do?methodToCall=ajaxLoadVersionHistory&docId='+reqParams['docNum'], function() {
				createLoading(false);
				protocolActionsARHistoryLoaded = true;
			});
		}
	});
});

jQuery.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    jQuery.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};