$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // fetch devices and populate table
    fetchDevices();

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/device/create'
    });
});

function createRow(device) {
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(device.code));
    row.append($("<td></td>").text(device.type.name));
    row.append($("<td></td>").html((device.disabled ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html((device.reserved ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit device" onclick="onEditClick(' + device.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete device" onclick="onDeleteClick(' + device.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchDevices() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(device => {
                $('#tableBody').append(createRow(device));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="7">No data has been found!</td></tr>');
        }
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }
    fetchWithSelectors('GET', '/api/device/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/device/edit/' + id;
}

function onDeleteClick(id) {
    Swal.fire({
        title: 'Do you want to save the changes? Note, that if you delete this device all future existing reservations for it will not be handled by the system!',
        confirmButtonText: 'Delete',
        showCancelButton: true,
    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed) {
            const onsuccess = function () {
                fetchDevices();
                addSuccessToast("You successfully deleted a device");
            }
            fetchWithSelectors('DELETE', '/api/device/' + id, null, onsuccess, null);
        }
    })

}

