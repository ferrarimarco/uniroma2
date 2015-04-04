$('#editInstancesModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var modal = $('#editInstancesModal');
	$('#productName', modal).val(button.data('entity-title'));
	$('#productId', modal).val(button.data('entity-id'));
	$('#productPreviousAmount', modal).val(button.data('entity-amount'));
	$('#productAmount', modal).val(button.data('entity-amount'));
	
	$("input[name='productAmount']").TouchSpin({
	      verticalbuttons: true,
	      verticalupclass: 'glyphicon glyphicon-plus',
	      verticaldownclass: 'glyphicon glyphicon-minus',
	      min: 0
	    });
});