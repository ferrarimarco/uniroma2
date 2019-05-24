//Shorthand for $( document ).ready()
$(function() {
	$("#productCategory").prepend("<option value=''></option>").val('');
	$("#productClass").prepend("<option value=''></option>").val('');
	$("#product").prepend("<option value=''></option>").val('');
	$("#statsFormSubmit").attr("disabled", true);

	$("#productCategory").change(function(){
		$("#productClass").val("");
		$("#product").val("");
		$("#statsFormSubmit").removeAttr("disabled");
	});

	$("#productClass").change(function(){
		$("#productCategory").val("");
		$("#product").val("");
		$("#statsFormSubmit").removeAttr("disabled");
	});

	$("#product").change(function(){
		$("#productCategory").val("");
		$("#productClass").val("");
		$("#statsFormSubmit").removeAttr("disabled");
	});
	
	var entityName = $("#statEntityName").text();
	$('option').filter(function () { return $(this).html() == entityName; }).attr('selected', true);
});
