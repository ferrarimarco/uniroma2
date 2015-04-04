$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var modal = $('#editEntityModal');
	$('#productName', modal).val(button.data('entity-title'));
	$('#productBarcode', modal).val(button.data('entity-barcode'));
	$('#productBrand', modal).val(button.data('entity-brand'));
	$('#productAmount', modal).val(button.data('entity-amount'));
	$('#productId', modal).val(button.data('entity-id'));
	
	// Hide amount field if editing
	if(typeof button.data('entity-id') !== typeof undefined && button.data('entity-id') !== false)
		$("#productAmountField", modal).hide();
	
	// Set entity class
	$("#productClass option:contains(" + button.data('entity-clazz') + ")", modal).attr('selected', true);
});