$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    fetchDaySchedules();
    const isEdit = /[0-9]+$/.test(window.location.pathname);

    if (isEdit) {
        $("#bodyHeader").text("Edit Week schedule");
        $("#submitButton").text("Edit");

        const id = getUrlPathId(window);
        const onsuccess = function (data) {
            $("#nameInput").val(data.name);
            $("#monday").val(JSON.stringify(data.monday));
            $("#tuesday").val(JSON.stringify(data.tuesday));
            $("#wednesday").val(JSON.stringify(data.wednesday));
            $("#thursday").val(JSON.stringify(data.thursday));
            $("#friday").val(JSON.stringify(data.friday));
            $("#saturday").val(JSON.stringify(data.saturday));
            $("#sunday").val(JSON.stringify(data.sunday));
        };

        fetchWithSelectors('GET', '/api/weekschedule/' + id, null, onsuccess, null);
    } else {
        $("#bodyHeader").text("Create Week schedule")
        $("#submitButton").text("Create");
    }

    $("#submitButton").on('click', function (e) {
        e.preventDefault()

        // validation
        if (!$("#nameInput").val()) {
            $("#nameInput").addClass("is-invalid");
            return;
        } else {
            $("#nameInput").removeClass("is-invalid");
        }

        // requests on success validation
        if (isEdit) {
            const onsuccess = function () {
                window.location.pathname = "/admin/weekschedules"
            };
            const id = getUrlPathId(window);
            fetch('PUT', "/api/weekschedule/", {
                id: id,
                name: $("#nameInput").val(),
                monday: JSON.parse($("#monday").val()),
                tuesday: JSON.parse($("#tuesday").val()),
                wednesday: JSON.parse($("#wednesday").val()),
                thursday: JSON.parse($("#thursday").val()),
                friday: JSON.parse($("#friday").val()),
                saturday: JSON.parse($("#saturday").val()),
                sunday: JSON.parse($("#sunday").val()),
            }, onsuccess, null);
        } else {
            const onsuccess = function () {
                window.location.pathname = "/admin/weekschedules"
            };

            fetch('POST', "/api/weekschedule/", {
                name: $("#nameInput").val(),
                monday: JSON.parse($("#monday").val()),
                tuesday: JSON.parse($("#tuesday").val()),
                wednesday: JSON.parse($("#wednesday").val()),
                thursday: JSON.parse($("#thursday").val()),
                friday: JSON.parse($("#friday").val()),
                saturday: JSON.parse($("#saturday").val()),
                sunday: JSON.parse($("#sunday").val()),
            }, onsuccess, null);

        }

    })

});

function createOption(daySchedule) {
    return $("<option/>").attr('value', JSON.stringify(daySchedule)).text(daySchedule.name);
}

function fetchDaySchedules() {
    $('#tableBody').empty();
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(daySchedule => {
                $('.form-select').append(createOption(daySchedule));
            });
        }

    }
    fetchWithSelectors('GET', '/api/dayschedule/', null, onsuccess, null);
}

