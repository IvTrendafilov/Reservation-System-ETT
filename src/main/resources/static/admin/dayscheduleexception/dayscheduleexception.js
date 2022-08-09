$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // fetch dayscheduleexceptions and populate table
    fetchDayScheduleExceptions();

    $("#createNewButton").on('click', function () {
        window.location.pathname = '/admin/dayscheduleexception/create'
    });
});

function createRow(dayscheduleexception) {
    const formattedWorkingTimes = dayscheduleexception.workingTimes.filter(x => !!x).reduce((previousValue, currentValue, i) => {
        if (i % 2) {
            previousValue[previousValue.length - 1].push(currentValue.substring(0,5));
        } else {
            previousValue.push([currentValue.substring(0,5)]);
        }
        return previousValue;
    }, [ ]).map(pair => pair.join(" - "));

    const row = $("<tr></tr>");
    row.append($("<td></td>").text(dayscheduleexception.date));
    row.append($("<td></td>").text(formattedWorkingTimes.join("/")));
    row.append($("<td></td>").html((dayscheduleexception.isClosed ? '<i class="bi bi-check-lg"></i>' : '<i class="bi bi-x-lg"></i>')));
    row.append($("<td></td>").html(
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Edit day schedule exception" onclick="onEditClick(' + dayscheduleexception.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-pencil-square"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Delete day schedule exception" onclick="onDeleteClick(' + dayscheduleexception.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>'
    ));

    return  row;
}

function fetchDayScheduleExceptions() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(dayscheduleexception => {
                $('#tableBody').append(createRow(dayscheduleexception));
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
    fetchWithSelectors('GET', '/api/dayscheduleexception/', null, onsuccess, null);
}

function onEditClick(id) {
    window.location.pathname = '/admin/dayscheduleexception/edit/' + id;
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchDayScheduleExceptions();
        addSuccessToast("You successfully deleted a day schedule exception");
    }
    fetchWithSelectors('DELETE', '/api/dayscheduleexception/' + id, null, onsuccess, null);
}

