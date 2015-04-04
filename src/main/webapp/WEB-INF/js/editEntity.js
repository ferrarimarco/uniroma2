$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var modal = $('#editEntityModal');
	$('#productName', modal).val(button.data('entity-title'));
	$('#productBarcode', modal).val(button.data('entity-barcode'));
	$('#productBrand', modal).val(button.data('entity-brand'));
	$('#productId', modal).val(button.data('entity-id'));
	
	// Set entity class
	$("#productClass option:contains(" + button.data('entity-clazz') + ")", modal).attr('selected', true);
});