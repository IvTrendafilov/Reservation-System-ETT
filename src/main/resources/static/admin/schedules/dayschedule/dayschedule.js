$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    fetchDaySchedules();

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/dayschedule/create'
    });
});

function createRow(daySchedule) {
    const formattedWorkingTimes = daySchedule.workingTimes.filter(x => !!x).reduce((previousValue, currentValue, i) => {
        if (i % 2) {
            previousValue[previousValue.length - 1].push(currentValue.substring(0,5));
        } else {
            previousValue.push([currentValue.substring(0,5)]);
        }
        return previousValue;
    }, [ ]).map(pair => pair.join(" - "));

    const row = $("<tr></tr>");
    row.append($("<td></td>").text(daySchedule.name));
    row.append($("<td></td>").text(formattedWorkingTimes.join("/")));
    row.append($("<td></td>").html((daySchedule.isClosed ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit day schedule" onclick="onEditClick(' + daySchedule.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete day schedule" onclick="onDeleteClick(' + daySchedule.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchDaySchedules() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(daySchedule => {
                $('#tableBody').append(createRow(daySchedule));
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
    fetchWithSelectors('GET', '/api/dayschedule/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/dayschedule/edit/' + id;
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchDaySchedules();
        addSuccessToast("You successfully deleted a day schedule!");
    }
    fetchWithSelectors('DELETE', '/api/dayschedule/' + id, null, onsuccess, null);
}
