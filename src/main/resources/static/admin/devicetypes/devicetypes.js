$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // fetch devices and populate table
    fetchDeviceTypes();

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/devicetypes/create'
    });
});

function createRow(deviceType) {
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(deviceType.name));
    row.append($("<td></td>").text(deviceType.imageClass));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit Device Type" onclick="onEditClick(' + deviceType.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete Device Type" onclick="onDeleteClick(' + deviceType.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchDeviceTypes() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(deviceType => {
                $('#tableBody').append(createRow(deviceType));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="4">No data has been found!</td></tr>');
        }
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }
    fetchWithSelectors('GET', '/api/devicetypes/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/devicetypes/edit/' + id;
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchDeviceTypes();
        addSuccessToast("You successfully deleted a Device type");
    }
    fetchWithSelectors('DELETE', '/api/devicetypes/' + id, null, onsuccess, null);
}

