$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // fetch facilities and populate table
    fetchFacilities();

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/facility/create'
    });
});

function createRow(facility) {
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(facility.name));
    row.append($("<td></td>").text(facility.facilityType.join(", ")));
    row.append($("<td></td>").text(facility.roomId));
    row.append($("<td></td>").html((facility.disabled ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html((facility.reserved ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit facility" onclick="onEditClick(' + facility.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete facility" onclick="onDeleteClick(' + facility.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchFacilities() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(facility => {
                $('#tableBody').append(createRow(facility));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="6">No data has been found!</td></tr>');
        }
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }
    fetchWithSelectors('GET', '/api/facility/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/facility/edit/' + id;
}

function onDeleteClick(id) {
    Swal.fire({
        title: 'Do you want to save the changes? Note, that if you delete this facility all future existing reservations for it will not be handled by the system!',
        confirmButtonText: 'Delete',
        showCancelButton: true,
    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed) {
            const onsuccess = function () {
                fetchFacilities();
                addSuccessToast("You successfully deleted a facility");
            }
            fetchWithSelectors('DELETE', '/api/facility/' + id, null, onsuccess, null);
        }
    })

}

