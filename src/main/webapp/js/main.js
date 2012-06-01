
$(document).ready(function() {
	
	var paramId = $.urlParam("id");
	if (paramId == null) {
		paramId = "";
	}
	

	$.getJSON('rest/rep/' + paramId, function(data) {
		var container = $("#for_iteration");
		if (data.file instanceof Array) {
			parseJSONArray(data.file);
		} else {
			parseJSONFileObject(data.file, 0);
		}
		
		container.find("#file_name").attr("href", "?id="+data.dirParentId);
		
		var fileUpload = $("#file_upload");
		fileUpload.attr("href", fileUpload.attr("href") + "?id="+data.dirId);
		
	});	
 });


$.urlParam = function(name) { 
	var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(top.window.location.href); 
	return (results !== null) ? results[1] : null; 
}


function parseJSONArray(array) {
	  $.each(array, function(index, file) {
		  parseJSONFileObject(file, index);
	  });
	
}

function parseJSONFileObject(file, index) {
	      var container = $("#for_iteration");
		  var cContainer = container.clone();
		  cContainer.attr("id", cContainer.attr("id") + index);
		  var fileId = null;
		  var isFolder = false;
		  $.each(file, function(key, value) {
			  var el = null;
			  if (key == "@id") {
				  fileId = value;
			  } else if (key == "@name") {
				  el = cContainer.find("#file_name");
				  el.html(value);
			  } else if (key == "@size") {
				  el = cContainer.find("#file_size");
				  el.html(value);
			  } else if (key == "@dateModified") {
				  el = cContainer.find("#file_modified");
				  el.html(value);
			  } else if (key == "@uri") {
				  cContainer.find("#file_name").attr("href", value);
			  } else if (key == "@isDirectory") {
				  isFolder = value == "true";
			  }
		  });
		  
		  if (isFolder) {
			  cContainer.find("#file_name").attr("href", "?id="+fileId);
		  } else {
			  cContainer.find("#folder").remove();
		  }
		  
		  
		  var el = cContainer.find("#file_name"); 
		  el.attr("id", el.attr("id") + index);

		  el = cContainer.find("#file_size"); 
		  el.attr("id", el.attr("id") + index);

		  el = cContainer.find("#file_modified"); 
		  el.attr("id", el.attr("id") + index);

		  cContainer.appendTo(container.parent());
		  
}


