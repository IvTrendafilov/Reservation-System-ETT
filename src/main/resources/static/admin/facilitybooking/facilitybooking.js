$(function () {
    
    const navbar = $("#navbar");
    const datepicker = $("#datepicker");
    const from = $( "#from" );
    const to = $("#to");
    const settingsPromise = fetchSettings();

    navbar.load("/admin/navbar/navbar.html");
    navbar.load("../navbar/navbar.html");
    // render map and div for pcs
    getRoom = renderInteractiveMap("#canvas", 1.2, [ ], [ ])
    $("#mapContainer").css({
        position: 'relative',
        height: 754 * 1.2 + 'px',
    });
    $("#interactive-map").css({
        position: 'absolute',
        width: 800 * 1.2 + 'px',
        height: 754 * 1.2 + 'px',
    });
    $(".time-picker").attr('disabled', true);
    const onsuccess = function (data) {
        settingsPromise.then(function (settings) {
            datepicker.datepicker({
                minDate: 0,
                beforeShowDay: function (date) {
                    const dayException = data.find(x => x.date === formatDate(date, 'd/m/y'));
                    if (dayException) {
                        return [!dayException.isClosed, 'disabled', (dayException.isClosed ? 'We are closed on this date!' : 'Book me!')]
                    }
                    const day = date.getDay();
                    let dayName = null;
                    switch (day) {
                        case 0:
                            dayName = 'sunday';
                            break;
                        case 1:
                            dayName = 'monday';
                            break;
                        case 2:
                            dayName = 'tuesday';
                            break;
                        case 3:
                            dayName = 'wednesday';
                            break;
                        case 4:
                            dayName = 'thursday';
                            break;
                        case 5:
                            dayName = 'friday';
                            break;
                        case 6:
                            dayName = 'saturday';
                            break;
                        default:
                            new Error("Non existent day!");
                    }
                    const isDayClosed = settings.loungeSchedule[dayName].isClosed
                    return [!isDayClosed, 'disabled', (isDayClosed ? 'We are closed on this date!' : 'Book me!')]
                }
            });
        });
    }
    fetch('GET', '/api/dayscheduleexception/?' + $.param({ from: formatDate(new Date()) }), null, onsuccess, null);

    datepicker.on('change', function () {
        const _this = $(this)
        const day = new Date(_this.val()).getDay();
        let dayName = null;
        switch (day) {
            case 0:
                dayName = 'sunday';
                break;
            case 1:
                dayName = 'monday';
                break;
            case 2:
                dayName = 'tuesday';
                break;
            case 3:
                dayName = 'wednesday';
                break;
            case 4:
                dayName = 'thursday';
                break;
            case 5:
                dayName = 'friday';
                break;
            case 6:
                dayName = 'saturday';
                break;
            default:
                new Error("Non existent day!");
        }
        settingsPromise
            .then((settings) => settings.loungeSchedule[dayName])
            .then((daySettings) => {
                const onsuccess = function (dayscheduleexception) {
                    if (dayscheduleexception.length !== 0) {
                        daySettings = dayscheduleexception[0];
                    }

                    const disabledTimeRanges = daySettings.workingTimes.filter(x => !!x).reduce((previousValue, currentValue, currentIndex) => {
                        if (currentIndex === 0 || currentIndex === (daySettings.workingTimes.length - 1)) return previousValue;
                        if (previousValue.length === 0 || previousValue[previousValue.length - 1].length === 0) {
                            previousValue.push([ currentValue ]);
                        } else {
                            previousValue[previousValue.length - 1].push(currentValue);
                        }
                        return previousValue;
                    }, []);

                    const options = {
                        timeFormat: 'H:i',
                        step: 15,
                        minTime: daySettings.workingTimes[0],
                        maxTime: daySettings.workingTimes[daySettings.workingTimes.length - 1],
                        disableTimeRanges: disabledTimeRanges,
                        disableTextInput:true,
                    };
                    from.attr('disabled', false);
                    from.timepicker(options);

                    from.off('changeTime').on('changeTime', function(_) {
                        let toMaxTime = daySettings.workingTimes.filter((_, i) => i % 2).find(wt => wt > $(this).val());
                        settingsPromise.then((settings) => {
                            toMaxTime = toMaxTime < addTime($(this).val(), settings.maxBookingTimeLength) ? toMaxTime : addTime($(this).val(), settings.maxBookingTimeLength);
                            to.attr('disabled', false);
                            to.timepicker({
                                timeFormat: 'H:i',
                                step: 15,
                                minTime: $(this).val(),
                                maxTime: toMaxTime,
                                showDuration: true,
                                disableTextInput:true,
                            });
                            fetchFacilities(from.val(), to.val(), datepicker.val())
                        });
                    });

                    to.off('changeTime').on('changeTime', function (_) {
                        fetchFacilities(from.val(), to.val(), datepicker.val())
                    });
                }
                fetch('GET', '/api/dayscheduleexception/?' + $.param({ date: _this.val(), }), null, onsuccess, null)
            });
        fetchFacilities(from.val(), to.val(), datepicker.val())
    });

    $("#submitButton").on('click', function () {
        const facilityId = getRoom();
        if (facilityId === null || !from.val() || !to.val || !datepicker.val()) {
            addErrorToast("There is a wrong field!")
        }
        $('#modalBody').empty().append(
            $("<div/>").text("Date: " + datepicker.val()),
            $("<div/>").text("From: " + from.val()),
            $("<div/>").text("To: " + to.val()),
            $("<div/>").text("Comments: " + $("#commentsTextarea").val())
        );
        $('#exampleModal').modal('show');

    });

    $(".close-modal").on('click', function () {
        $('#exampleModal').modal('hide');
    })

    $("#confirmButton").on('click', function () {
        $('#exampleModal').modal('hide');
        function onsuccess() {
            Swal.fire(
                'Good job!',
                'You successfully created a reservation!',
                'success'
            ).then(() => {
                window.location.pathname = "/admin/mybookings"
            });
        }
        function onerror(error) {
            Swal.fire(
                'Error!',
                error.responseJSON.message || 'An error occurred while making this reservation!',
                'error'
            );
        }

        const date = datepicker.val();
        const fromDate = date + " " + from.val();
        const toDate = date + " " + to.val();
        const data = {
            from: fromDate,
            to: toDate,
            remarks: $("#commentsTextarea").val(),
            status: 'PENDING',
            facilityId: facilityPerRoom[getRoom().id].id,
        }
        fetch('POST', '/api/reservation/facility', data, onsuccess, onerror)
    });

});
const getFacilityType = function () {
    const loc = window.location.pathname;
    if (loc.includes("broadcast")) return "BROADCAST"
    else if (loc.includes("tournament")) return "TOURNAMENT"
}

// WE DO NO CARE ABOUT ROOM ID BUT FACILITY ID;
let getRoom = function () {
    return null;
};

let facilityPerRoom = {

};

function fetchSettings() {
    return syncFetch('GET', '/api/settings/', null, null, null)
}


function fetchFacilities(from, to, date) {
    if (!from || !to || !date) return;
    const onsuccess = function (data) {
        const allRooms = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
        const free = data.filter(f => f.color === 'BLACK').map(f => f.roomId);
        const busy = data.filter(f => f.color === 'RED').map(f => f.roomId);
        const partly = data.filter(f => f.color === 'YELLOW').map(f => f.roomId);
        const notAvailable = allRooms.filter(id => !busy.includes(id) && !free.includes(id) && !partly.includes(id));
        facilityPerRoom = { }
        data.forEach(f => facilityPerRoom[f.roomId] = f);
        getRoom = renderInteractiveMap("#canvas", 1.2, [ ], [...busy, ...notAvailable ], partly)
        $("#interactive-map").hide();
    };

    const fromDate = date + " " + from;
    const toDate = date + " " + to;
    fetch('GET', '/api/facility/reservation?' + $.param({ from: fromDate.toString(), to: toDate.toString(), type: getFacilityType() }), null, onsuccess, null)
}

function addTime(time, minutes) {
    let splitTime = time.split(":");
    const toAddMinutes = minutes % 60;
    const toAddHours = Math.floor(minutes / 60);
    const newMinutes = (parseInt(splitTime[1]) + toAddMinutes) % 60;
    const newHours = (parseInt(splitTime[0]) + toAddHours + Math.floor((parseInt(splitTime[1]) + toAddMinutes) / 60)) % 24;
    const hours = newHours.toString();
    const mins = newMinutes.toString();
    return [hours.length === 1 ? ('0' + hours) : hours, mins.length === 1 ? ('0' + mins) : mins].join(":")
}


