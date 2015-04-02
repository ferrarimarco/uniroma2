$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget) // Button that triggered the modal
	$('#productName').val(button.data('entity-instance-name'))
	$('#productBarcode').val(button.data('entity-barcode'))
	$('#productBrand').val(button.data('entity-brand'))
	$('#productAmount').val(button.data('entity-amount'))
	
	// Set entity class
	$("#productClass").filter(function() {
	    return this.text == button.data('entity-class'); 
	}).attr('selected', true);
	
	// Append entity ID to form action
	var action = $("editProductForm").attr('action')
	action = action + "/" + button.data('entity-id')
	$("#search-form").attr("action", action);
});