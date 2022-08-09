const settingsPromise = fetchSettings();
$(function () {
    const navbar = $("#navbar");
    const datepicker = $("#datepicker");
    const from = $( "#from" );
    const to = $("#to");

    navbar.load("/client/navbar/navbar.html");
    navbar.load("../navbar/navbar.html");
    // render map and div for pcs
    renderInteractiveMap("#canvas", 1.2, [ ], [ ])
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
                            deviceIds = [];
                            fetchDevices(from.val(), to.val(), datepicker.val())
                        });
                    });

                    to.off('changeTime').on('changeTime', function (_) {
                        deviceIds = [];
                        fetchDevices(from.val(), to.val(), datepicker.val())
                    });
                }
                fetch('GET', '/api/dayscheduleexception/?' + $.param({ date: _this.val(), }), null, onsuccess, null)
            });
        deviceIds = [];
        fetchDevices(from.val(), to.val(), datepicker.val())
    });

    $("#submitButton").on('click', function () {
        if (deviceIds.length === 0 || !from.val() || !to.val || !datepicker.val()) {
            addErrorToast("There is a wrong field!")
        }
        $('#modalBody').empty().append(
            $("<div/>").text("Date: " + datepicker.val()),
            $("<div/>").text("From: " + from.val()),
            $("<div/>").text("To: " + to.val()),
            $("<div/>").text("# of devices: " + deviceIds.length),
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
                window.location.pathname = "/mybookings"
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
            status: 'PENDING', //TODO: MUST CHANGE IT AS WE SHOULD SET IT ONLY IN BE
            devices: deviceIds,
        }
       fetch('POST', '/api/reservation/device', data, onsuccess, onerror)
    });

});

function fetchSettings() {
    return syncFetch('GET', '/api/settings/', null, null, null)
}

let deviceIds = [ ];
function fetchDevices(from, to, date) {
    if (!from || !to || !date) return;
    // TODO: ADD SMART TOOLTIPS SHOWING WHEN ARE THE INTERFERING RESERVATIONS!
    const onsuccess = function (data) {
        $("#interactive-map").empty();
        data.forEach(function (device) {
            if (device && device.position) {
                const tooltipHtml = "Code: " + device.code + "<br>" + "Type: " + device.type.name + "<br>" +
                    ((device.color === 'YELLOW' || device.color === 'RED') ?
                        'The device has interfering reservations with the following timeslots: <br>' +
                        device.interferingReservationTimeslots.flatMap(pair => Object.values(pair))
                            .map(time => time.substr(0, 5))
                            .map((time, i) => (i % 2) ? ('To: ' + time + '<br>') : ('From: ' + time)).join(" ") :
                        '');
                const pc =  $("<i/>")
                    .attr('data-bs-toggle', 'tooltip')
                    .attr('data-bs-placement', 'top')
                    .attr('data-bs-html', 'true')
                    .attr('title', tooltipHtml)
                    .attr('id', device.id)
                    .addClass(device.type.imageClass)
                    .addClass('draggable-device' + (device.color === 'BLACK' ? ' device' : ''))
                    .css({ color: device.color.toLowerCase(), position: 'relative', top: device.position.top + "px", left: device.position.left + "px" });

                $("#interactive-map").append(pc);
            }
        });
        $(".device").off('click').on('click', function () {
            settingsPromise.then(settings => {
                if (deviceIds.includes($(this).attr('id'))) {
                    $(this).css({
                        color: 'black',
                    });
                    deviceIds = deviceIds.filter(id => id !== $(this).attr('id'));
                } else {
                    if (settings.maxDevicesPerPerson <= deviceIds.length) {
                        addErrorToast("You have reached the maximum amount of devices!");
                        return;
                    }
                    $(this).css({
                        color: 'green',
                    });
                    deviceIds = [ ...deviceIds, $(this).attr('id') ];
                }
            });
        });
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    };

    const fromDate = date + " " + from;
    const toDate = date + " " + to;
    fetch('GET', '/api/device/reservation?' + $.param({ from: fromDate.toString(), to: toDate.toString() }), null, onsuccess, null)
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


