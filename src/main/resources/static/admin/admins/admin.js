$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // fetch admins and populate table
    fetchAdmins();

    $("#createNewButton").attr('disabled', !isGlobalAdmin())
    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/admin/create'
    });
});

function createRow(admin) {
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(admin.name));
    row.append($("<td></td>").text(admin.email));
    row.append($("<td></td>").html(
        '<button' + (!isGlobalAdmin() ? ' disabled ' : ' ') + 'type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete admin" onclick="onDeleteClick(' + admin.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));
    return  row;
}

function fetchAdmins() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(admin => {
                $('#tableBody').append(createRow(admin));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="3">No data has been found!</td></tr>');
        }
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover', container: 'body' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }
    fetchWithSelectors('GET', '/api/userdetails/admin', null, onsuccess, null);
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchAdmins();
        addSuccessToast("You successfully deleted a admin");
    }
    fetchWithSelectors('DELETE', '/api/userdetails/admin/' + id, null, onsuccess, null);
}

