var informationsTable;
var returnMessage;

$(document).ready(function() {

	drawDatatable();

	if (returnMessage) {
		showMessage(returnMessage);
	}

	$("#information-form").validate({
		rules: {
			'title': {
				required: true
			},
			'description': {
				required: true
			},
			'content': {
				required: true
			}

		},
		messages: {
			'title': 'Title is required!',
			'description': 'Description is required!',
			'content': 'Content is required!'
		}
	});
});

function checkForm() {
	if ($("#information-form").valid() !== false) {
		$("#information-form").submit();
	}
}

function showMessage(data) {
	$('#messageModal #messageText').html(data);
	$('#messageModal').modal();
}

function drawDatatable() {
	informationsTable = $('#informations-table').DataTable({
		responsive: true,
		serverSide: true,
		autoWidth: true,
		scrollX: true,
		ajax: {
			'contentType': 'application/json',
			'url': '/informations/datatable',
			'type': 'POST',
			'data': function(d) {
				return JSON.stringify(d);
			}
		},
		columns: [{
			data: 'id',
			name: 'id'
		}, {
			data: 'title',
			name: 'title'
		}, {
			data: 'description',
			name: 'description'
		}, {
			data: 'id',
			className: "text-center",
			render: function(data, type, row) {
				return '<a href="/informations/detail?id='
					+ data + '">' + '<i class="fa fa-info-circle link-irs"></i></a>';
			}
		}, {
			data: 'id',
			className: "text-center",
			render: function(data, type, row) {
				return '<a href="/informations/edit?id='
					+ data + '">' + '<i class="fa fa-edit link-irs"></i></a>';
			}
		}, {
			data: 'id',
			className: "text-center",
			render: function(data, type, row) {
				return '<a href="#" onclick="deleteConfirm('
					+ data
					+ ')"> <i class="fa fa-trash link-irs"></i></a>';
			}
		}
		],
		columnDefs: [{
			targets: [0],
			visible: false,
			searchable: false
		}, {
			targets: [3, 4, 5],
			searchable: false,
			orderable: false
		}
		],
		order: [[0, "asc"]],
		lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]]
	});
}

function deleteConfirm(id) {
	$('#deleteConfirmForm #deletedId').val(id);
	$('#deleteConfirmModal').modal();
}

function deleteData() {
	$.ajax({
		contentType: 'application/json',
		type: 'POST',
		url: "/informations/delete?id=" + $('#deleteConfirmForm #deletedId').val(),
		success: function(data) {
			$('#deleteConfirmModal').modal('hide');
			$('#messageModal #messageText').text(data);
			$('#messageModal').modal();
			informationsTable.ajax.reload();
		},
		error: function(xhr, status, error) {
			$('#deleteConfirmModal').modal('hide');
			$('#messageModal #messageText').text(xhr.responseText);
			$('#messageModal').modal();
		}
	});
}