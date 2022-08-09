$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    fetchWeekSchedules()

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/weekschedules/create'
    });
});

function createRow(weekSchedule) {
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(weekSchedule.name));
    row.append($("<td></td>").text(weekSchedule.monday.name));
    row.append($("<td></td>").text(weekSchedule.tuesday.name));
    row.append($("<td></td>").text(weekSchedule.wednesday.name));
    row.append($("<td></td>").text(weekSchedule.thursday.name));
    row.append($("<td></td>").text(weekSchedule.friday.name));
    row.append($("<td></td>").text(weekSchedule.saturday.name));
    row.append($("<td></td>").text(weekSchedule.sunday.name));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit week schedule" onclick="onEditClick(' + weekSchedule.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete week schedule" onclick="onDeleteClick(' + weekSchedule.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchWeekSchedules() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(weekSchedule => {
                $('#tableBody').append(createRow(weekSchedule));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="8">No data has been found!</td></tr>');
        }
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }
    fetchWithSelectors('GET', '/api/weekschedule/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/weekschedules/edit/' + id;
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchWeekSchedules();
        addSuccessToast("You successfully deleted a week schedule");
    }
    fetchWithSelectors('DELETE', '/api/weekschedule/' + id, null, onsuccess, null);
}