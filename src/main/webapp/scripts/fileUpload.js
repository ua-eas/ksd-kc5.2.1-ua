/*
 * Vaidation on the file being uploaded. If the file is larger than the max attachment size,
 * a warning message is given and the 'add' button(s) are disabled.
 * 
 * File: the file being uploaded
 * addButtonIndexes: The indexes of which add button(s) to disable
 * lableId: the id of the warning label
 */


function selectFile(file, addButtonIndexes, labelId) {
	if(file.files.length > 0) {
		if(file.files[0].size > document.getElementById("maxAttachmentSize").value) {
			for(var i = 0; i < addButtonIndexes.length; i++) {
				document.getElementsByClassName("addButton")[addButtonIndexes[i]].disabled = true;
			}
			var errorMessage = "Uploaded file '" + file.files[0].name + "' was too large. Maximum size is " + document.getElementById("maxAttachmentSize").value + " bytes.";
			document.getElementById(labelId).innerHTML = errorMessage;
			document.getElementById(labelId).style.visibility = "";
		}
		else {
			for(var i = 0; i < addButtonIndexes.length; i++) {
				document.getElementsByClassName("addButton")[addButtonIndexes[i]].disabled = false;
			}
			document.getElementById(labelId).innerHTML = "";
			document.getElementById(labelId).style.visibility = "hidden";
		}
	}
}