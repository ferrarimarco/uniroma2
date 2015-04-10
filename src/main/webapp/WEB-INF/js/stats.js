//Shorthand for $( document ).ready()
$(function() {
	$("#productCategory").prepend("<option value=''></option>").val('');
	$("#productClass").prepend("<option value=''></option>").val('');
	$("#product").prepend("<option value=''></option>").val('');

	$("#productCategory").change(function(){
		$("#productClass").val("");
		$("#product").val("");
	});

	$("#productClass").change(function(){
		$("#productCategory").val("");
		$("#product").val("");
	});

	$("#product").change(function(){
		$("#productCategory").val("");
		$("#productClass").val("");
	});
});
