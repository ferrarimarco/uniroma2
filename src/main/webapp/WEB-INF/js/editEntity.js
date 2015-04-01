$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget) // Button that triggered the modal
	var entityName = button.data('entity-name')
	var entityId = button.data('entity-id')
	loadProduct(entityName, entityId)
});

function loadProduct(entityName, entityId) {
	$.ajax({
		url: entityName + "/" + entityId,
		dataType: 'json',
		success: function(result){
			$('#productName').val(result.name)
			$("#productClass").val(result.clazz.id)
			$('#productBarcode').val(result.barCode)
			$('#productBrand').val(result.brand)
			$('#productAmount').val(result.amount)
		},
		error: function(request, textStatus, errorThrown) {
			alert(textStatus);
		}
	});
}