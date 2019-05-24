$(function(){
	$('[data-toggle="confirmation"]').confirmation();
});

$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var modal = $('#editEntityModal');
	$('#productName', modal).val(button.data('entity-title'));
	$('#productBarcode', modal).val(button.data('entity-barcode'));
	$('#productBrand', modal).val(button.data('entity-brand'));
	
	if(button.data('entity-clazz') != undefined){
		// Set entity class
		$("#productClass option:contains(" + button.data('entity-clazz') + ")", modal).attr('selected', true);		
	}else
		$('#deleteEntityButton').prop('disabled', true);
	
	$('#productId', modal).val(button.data('entity-id'));
	
	var legend = $("#editProductModalLegend", modal).text();
	legend = legend.substr(0, legend.indexOf(': ') + 2);
	$("#editProductModalLegend", modal).text(legend + button.data('entity-title'));
});