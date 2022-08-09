$(function () { 
    
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");
    fetchSettings();
    $("#submitButton").on('click', function (e) {
        e.preventDefault()

        // validation
        if (!$("#maxBookingTimeLengthInput").val() || $("#maxBookingTimeLengthInput").val() < 0) {
            $("#maxBookingTimeLengthInput").addClass("is-invalid");
            return;
        } else {
            $("#maxBookingTimeLengthInput").removeClass("is-invalid");
        }

        if (!$("#maxNumberOfDevicesPerReservationInput").val() || $("#maxNumberOfDevicesPerReservationInput").val() < 0) {
            $("#maxNumberOfDevicesPerReservationInput").addClass("is-invalid");
            return;
        } else {
            $("#maxNumberOfDevicesPerReservationInput").removeClass("is-invalid");
        }

        const onsuccess = function () {
            addSuccessToast("You successfully updated settings!");
        };

        const data = {
            autoAcceptanceOfDeviceReservations: $("#autoAcceptanceOfDeviceReservation").is(':checked'),
            autoAcceptanceOfFacilityReservations: $("#autoAcceptanceOfFacilityReservation").is(':checked'),
            maxDevicesPerPerson: $("#maxNumberOfDevicesPerReservationInput").val(),
            maxBookingTimeLength:  $("#maxBookingTimeLengthInput").val(),
            loungeSchedule: JSON.parse($("#weekScheduleSelect").val()),
        }

        fetch('PUT', "/api/settings/", data, onsuccess, null);


    })
});

function fetchSettings() {
    const onsuccess = function (data) {
        fetchWeekSchedules(JSON.stringify(data.loungeSchedule));
        $("#autoAcceptanceOfDeviceReservation").attr('checked', data.autoAcceptanceOfDeviceReservations);
        $("#autoAcceptanceOfFacilityReservation").attr('checked', data.autoAcceptanceOfFacilityReservations);
        $("#maxBookingTimeLengthInput").val(data.maxBookingTimeLength);
        $("#maxNumberOfDevicesPerReservationInput").val(data.maxDevicesPerPerson);
        $("#availableSoonTimeLengthInput").val(data.availableSoonTimeLength);
    }
    fetchWithSelectors('GET', '/api/settings/', null, onsuccess, null);
}

function createOption(weekSchedule, isSelected) {
    return $("<option/>")
        .attr('value', JSON.stringify(weekSchedule))
        .attr('selected', isSelected)
        .text(weekSchedule.name);
}

function fetchWeekSchedules(selected) {
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(weekSchedule => {
                $('#weekScheduleSelect').append(createOption(weekSchedule, JSON.stringify(weekSchedule) === selected));
            });
        }

    }
    fetchWithSelectors('GET', '/api/weekschedule/', null, onsuccess, null);
}