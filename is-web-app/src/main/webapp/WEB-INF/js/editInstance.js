$('#editInstancesModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var modal = $('#editInstancesModal');
	
	var legend = $("#editInstanceModalLegend", modal).text();
	legend = legend.substr(0, legend.indexOf(': ') + 2);
	$("#editInstanceModalLegend", modal).text(legend + button.data('entity-title'));
	$('#productId', modal).val(button.data('entity-id'));
	
	$("input[name='productAmount']").TouchSpin({
	      verticalbuttons: true,
	      verticalupclass: 'glyphicon glyphicon-plus',
	      verticaldownclass: 'glyphicon glyphicon-minus',
	      min: 0
	    });
	
	$("#productExpirationDateField", modal).hide();

	$("#operationRadioAdd", modal).click(
			function(){
				var modal = $('#editInstancesModal');
				$("#productExpirationDateField", modal).show();
				$("#expirationDatePicker", modal).datetimepicker({
	                viewMode: 'days',
	                format: 'DD/MM/YYYY'
	            });
				$("#expirationDatePicker", modal).data("DateTimePicker").clear();
			});
	$("#operationRadioRemove", modal).click(
			function(){
				var modal = $('#editInstancesModal');
				$("#expirationDatePicker", modal).data("DateTimePicker").clear();
				$("#productExpirationDateField", modal).hide();
			});
});