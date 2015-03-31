$('#editEntityModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget) // Button that triggered the modal
	var entityName = button.data('entity-name')
	var entityId = button.data('entity-id')
	loadProduct(entityName, entityId)
});

function loadProduct(entityName, entityId) {
	$.getJSON(entityName + "/" + entityId), {
		name : $('#name').val()
	}, function(entity) {
		var modal = $(this)
		modal.find('#productName').text(entity.name)
		modal.find('#productClass').val(reci)
		modal.find('#productBarcode').text(entity.barCode)
		modal.find('#productBrand').text(entity.brand)
		modal.find('#productAmount').text(entity.amount)
	};
}