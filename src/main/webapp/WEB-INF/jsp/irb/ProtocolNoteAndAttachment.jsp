<%--
 Copyright 2005-2014 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.osedu.org/licenses/ECL-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="readOnly" value="${!KualiForm.notesAttachmentsHelper.modifyAttachments}" scope="request" />
<kul:documentPage
	showDocumentInfo="true"
	htmlFormAction="protocolNoteAndAttachment"
	documentTypeName="ProtocolDocument"
	renderMultipart="true"
	showTabButtons="true"
	auditCount="0"
  	headerDispatch="${KualiForm.headerDispatch}"
  	headerTabActive="noteAndAttachment">

<!--
"ATTP", "Attachment Type"
"DESC", "Description"
"LAUP", "Last Updated"
"UPBY", "Last Updated By" 
 -->
 
<script type="text/javascript">
    var $j = jQuery.noConflict();
    
    //This function is tied to the filter by drop down box on the
    //existing protocol attachments section
    function filterTableByCriteria(data) { 
    	if (data.value == "") {
    		showAllAttachmentRows();
    	} else {
            var label = getSelectedLabel(data, data.value);
    		filterAttachments(label);
    	}
    }
    
    //This function pulls the option label given the selected option value
    function getSelectedLabel(data, selectedValue) {
        for (var i = 0; i < data.options.length; i++) {
        	if (data.options[i].value == selectedValue) {
                return data.options[i].innerHTML;
        	}
        }    	
    }
    
    //This function makes all the attachment rows visible.  This is used when the
    //"select" option is chosen in the filter by drop down
    function showAllAttachmentRows() {
    	$j("#protocol-attachment-table tr.fake-class-level-1").each(function(index) {
    		   $j(this).show();
    	});
    }
    
    //This function takes the attachment type label and attempts to hide any rows
    //which do not match.  Added a fake css class to the "tr" element to make the
    //lookup easier.
    function filterAttachments(type) {
    	$j("#protocol-attachment-table tr.fake-class-level-1").each(function(index) {
    		    var rowId = $j(this).attr("id");
    	        var index = (rowId.split("-"))[3];
    		    var rowAttachmentType = getAttachmentType(index);
    		    if (type === rowAttachmentType) {
    		    	$j("#protocol-attachment-row-" + index).show();
    		    } else {
                    $j("#protocol-attachment-row-" + index).hide();
    		    }
    	    });
    }
    
    //This function extracts the attachment type from the div within the current row.
    function getAttachmentType(index) {
    	var attachmentType = $j("#attachment-type-" +index).text();
    	return $j.trim(attachmentType);
    }
    
    // This function extracts the Amendment/Renewal number type from the div within the current row.
    function getAmendRenewNum(index) {
    	var attachmentType = $j("#amend-renew-num-" +index).text();
        return $j.trim(attachmentType);
    }
    
    //Sorts the table via the four possible criterias:
    //"ARNO", "Amend/Renewal Number"
    //"ATTP", "Attachment Type"
    //"DESC", "Description"
    //"LAUP", "Last Updated"
    //"UPBY", "Last Updated By" 
    //
    //Also handles the "None" case...
    function sortTableByCriteria(data) {
        if(data.value == "ARNO"){
        	sortByAmendRenewNum()
        } else if (data.value == "ATTP") {
    		sortByAttachmentType();
    	} else if (data.value == "DESC") {
    		sortByDescription();
    	} else if (data.value == "LAUP") {
    		sortByLastUpdated();
    	} else if (data.value == "UPBY") {
    		sortByUpdatedBy();
    	} else {
    		//do nothing?
    	}
    }
    
    function sortByAmendRenewNum() {
    	var rowList = new Array();
        var sortingList = new Array();
        $j("#protocol-attachment-table tr.fake-class-level-1").each(function(idx) {
            var rowId = $j(this).attr("id");
            var index = (rowId.split("-"))[3];
            var rowAmendRenewNum = getAmendRenewNum(index);
            rowList[index] = rowId;
            sortingList[index] = rowAmendRenewNum;            
        });
                
        sortLists(rowList, sortingList);        
        sortTableRows(rowList, sortingList);
    }
    
    function sortByAttachmentType() {
    	var rowList = new Array();
    	var sortingList = new Array();
    	$j("#protocol-attachment-table tr.fake-class-level-1").each(function(idx) {
            var rowId = $j(this).attr("id");
            var index = (rowId.split("-"))[3];
            var rowAttachmentType = getAttachmentType(index);
            rowList[index] = rowId;
            sortingList[index] = rowAttachmentType;            
    	});
    	    	
    	sortLists(rowList, sortingList);    	
    	sortTableRows(rowList, sortingList);
    }
    
    function sortByDescription() {
        var rowList = new Array();
        var sortingList = new Array();
        $j("#protocol-attachment-table tr.fake-class-level-1").each(function(idx) {
            var rowId = $j(this).attr("id");
            var index = (rowId.split("-"))[3];
            var rowDescription = getDescription(index);
            rowList[index] = rowId;
            sortingList[index] = rowDescription;            
        });
        
        sortLists(rowList, sortingList); 
        sortTableRows(rowList, sortingList);
    }

    function sortByLastUpdated() {
        var rowList = new Array();
        var sortingList = new Array();
        $j("#protocol-attachment-table tr.fake-class-level-1").each(function(idx) {
            var rowId = $j(this).attr("id");
            var index = (rowId.split("-"))[3];
            var lastUpdated = getLastUpdated(index);
            rowList[index] = rowId;
            sortingList[index] = lastUpdated;            
        });
        
        sortListsByDate(rowList, sortingList); 
        sortTableRows(rowList, sortingList);
    }
    
    function sortByUpdatedBy() {
        var rowList = new Array();
        var sortingList = new Array();
        $j("#protocol-attachment-table tr.fake-class-level-1").each(function(idx) {
            var rowId = $j(this).attr("id");
            var index = (rowId.split("-"))[3];
            var updatedBy = getUpdatedBy(index);
            rowList[index] = rowId;
            sortingList[index] = updatedBy;            
        });
        
        sortLists(rowList, sortingList); 
        sortTableRows(rowList, sortingList);
    }    

    function sortLists(rowList, sortingList) {
    	for (var i = 0; i < sortingList.length-1; i++) {
    		for (var j = i+1; j < sortingList.length; j++) {
    			var result = compareToStr(sortingList[i], sortingList[j]);
    			if (result > 0) {
    				swapItems(i, j, sortingList);
    				swapItems(i, j, rowList);
    			}
    		}
    	}
    }
    
    function sortListsByDate(rowList, sortingList) {
        for (var i = 0; i < sortingList.length-1; i++) {
            for (var j = i+1; j < sortingList.length; j++) {
                var result = compareToDate(sortingList[i], sortingList[j]);
                if (result > 0) {
                    swapItems(i, j, sortingList);
                    swapItems(i, j, rowList);
                }
            }
        }
    }    
    
    function swapItems(idx1, idx2, sortList) {
    	var tmpItem = sortList[idx1];
    	sortList[idx1] = sortList[idx2];
    	sortList[idx2] = tmpItem;
    }
    
    function compareToStr(str1, str2) {
    	return str1.localeCompare(str2);
    }

    function compareNumber(x, y) {
        if (parseInt(x) < parseInt(y)){
            return -1;
        }
        if (parseInt(x) > parseInt(y)){
            return 1;
        }
        return 0;
    }

    function compareToDate(date1, date2) {
    	var dateParts1 = date1.split(" ");
    	var dateParts2 = date2.split(" ");
    	
    	var mmddyyyy1 = dateParts1[0].split("/");
    	var mmddyyyy2 = dateParts2[0].split("/");

    	var yyCompare = compareNumber(mmddyyyy1[2], mmddyyyy2[2]); 
    	var mmCompare = compareNumber(mmddyyyy1[0], mmddyyyy2[0]);
    	var ddCompare = compareNumber(mmddyyyy1[1], mmddyyyy2[1]);
    	var timeCompare = compareTime(dateParts1[1], dateParts1[2], dateParts2[1], dateParts2[2]);

    	if (yyCompare != 0)   {    return yyCompare;    }
    	if (mmCompare != 0)   {    return mmCompare;    }
    	if (ddCompare != 0)   {    return ddCompare;    }
    	if (timeCompate != 0) {    return timeCompare;  }
    	return 0;
    }
    
    function compareTime(hoursMinutes1, ampm1, hoursMinutes2, ampm2) {
    	if (ampm1 != ampm2) {
    		if (ampm1 == 'AM') {
    			return -1;
    		}
    		return 1;
    	}
    	var time1 = (hoursMinutes1.split(":"));
    	var time2 = (hoursMinutes2.split(":"));
    	var hhCompare = compareNumber(time1[0], time2[0]); 
    	var mmCompare = compareNumber(time1[1], time2[1]); 
        
        if (hhCompare != 0)   {    return hhCompare;    }
        if (mmCompare != 0)   {    return mmCompare;    }
        
        return 0;
    }
    
    function sortTableRows(rowList, sortingList) {
    	var rowEl;
    	for (var i = 0; i < rowList.length; i++) {
            rowEl = $j("#" + rowList[i]).detach();
            $j("#protocol-attachment-table > tbody").append(rowEl);
    	}
    }
    
    function getDescription(index) {
        var description = $j("#row-description-" +index).text();
        return $j.trim(description);    	
    }
    
    function getUpdatedBy(index) {
        var updatedBy = $j("#updated-by-" +index).text();
        return $j.trim(updatedBy);        
    }
    
    function getLastUpdated(index) {
        var lastUpdated = $j("#last-updated-" +index).text();
        return $j.trim(lastUpdated);
    }
    
    function alertList(label, theList) {
        var str = label + " : {";
        for (var x = 0; x < theList.length; x++) {
            if (x == (theList.length - 1)) {
                str += theList[x] + "}";
            } else {
                str += theList[x] + ", ";
            }
        }
        
        alert (str);
    }    
    
    $j(document).ready(function() {
    	filterTableByCriteria(document.getElementById("notesAttachmentsHelper.newAttachmentFilter.filterBy"));
    	sortTableByCriteria(document.getElementById("notesAttachmentsHelper.newAttachmentFilter.sortBy"));
    });
</script>


  	
<div align="right"><kul:help documentTypeName="ProtocolDocument" pageName="Notes%20%26%20Attachments" /></div>
<div id="workarea">
<kra-irb:protocolAttachmentProtocol /> 
<kra-irb:protocolNotes />
<kul:panelFooter />
</div>
	<kul:documentControls 
		transactionalDocument="false"
		suppressRoutingControls="true"
		extraButtonSource="${extraButtonSource}"
		extraButtonProperty="${extraButtonProperty}"
		extraButtonAlt="${extraButtonAlt}"
		viewOnly="${KualiForm.editingMode['viewOnly']}"
		/>

</kul:documentPage>
