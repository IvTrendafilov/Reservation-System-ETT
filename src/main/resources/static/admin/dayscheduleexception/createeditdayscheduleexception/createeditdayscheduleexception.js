$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../../navbar/navbar.html");
    $("#datepicker").datepicker({
        minDate: 0,
        dateFormat: 'dd/mm/yy'
    });
    const isEdit = /[0-9]+$/.test(window.location.pathname);
    if (isEdit) {
        $("#bodyHeader").text("Edit Day schedule exception");
        $("#submitButton").text("Edit");

        const id = getUrlPathId(window);
        const onsuccess = function (data) {
            $("#datepicker").val(data.date ? data.date : '');
            data.workingTimes.forEach((time, index) => {
                $(".form-group_timepicker").append("" +
                    "<label>" + ((index % 2) ? 'To' : 'From') + "</label>\n" +
                    "<input placeholder=\"Selected time\" value=\"" + time + "\" type=\"text\"\n class=\"form-control timepicker\">\n"
                )
                $('.timepicker').timepicker({
                    timeFormat: 'HH:mm',
                    interval: 15,
                    disableTextInput:true,
                });
            })
            $("#dayOffCheck").attr('checked', data.isClosed);
        };

        fetchWithSelectors('GET', '/api/dayscheduleexception/' + id, null, onsuccess, null);
    } else {
        $("#bodyHeader").text("Create Day schedule exception")
        $("#submitButton").text("Create");
        $(".form-group_timepicker").append("" +
            "<label>From</label>\n" +
            "<input placeholder=\"Selected time\" type=\"text\"\n class=\"form-control timepicker\">\n" +
            "<label>To</label>\n" +
            "<input placeholder=\"Selected time\" type=\"text\" class=\"form-control timepicker\">")
    }

    $("#submitButton").on('click', function (e) {
        e.preventDefault()
        console.log($("#datepicker").val());
        // validation
        if (!$("#datepicker").val()) {
            $("#datepicker").addClass("is-invalid");
            return;
        } else {
            $("#datepicker").removeClass("is-invalid");
        }
        const schedules = []
        $( ".timepicker" ).each(function () {
            const schedule = $(this);
            // save id and position of device
            schedules.push(schedule.val());
        });

        // requests on success validation
        if (isEdit) {
            const onsuccess = function () {
                window.location.pathname = "/admin/dayscheduleexception"
            };
            const id = getUrlPathId(window);
            fetch('PUT', "/api/dayscheduleexception/", {
                id: id,
                date: $("#datepicker").val(),
                isClosed: $("#dayOffCheck").is(":checked"),
                workingTimes: schedules.filter(x => !!x),
            }, onsuccess, null);
        } else {
            const onsuccess = function () {
                window.location.pathname = "/admin/dayscheduleexception"
            };

            fetch('POST', "/api/dayscheduleexception/", {
                date: $("#datepicker").val(),
                isClosed: $("#dayOffCheck").is(":checked"),
                workingTimes: schedules.filter(x => !!x),
            }, onsuccess, null);

        }

    })
});

$(function () {
    $('#createTimePicker').click(function () {
        $(".form-group_timepicker").append
        ("<label class = 'label-test'>Select</label><input class = 'form-control timepicker' type='text' placeholder='Select time'>",
            ("<label class = 'label-test'>Select</label><input class = 'form-control timepicker' type='text' placeholder='Select time'>"));

        $('.timepicker').timepicker({
            timeFormat: 'HH:mm',
            interval: 15,
            disableTextInput:true,
        });

        $(".label-test").each(function (index) {
            if (index % 2 === 0) {
                $(this).text("From");
            } else {
                $(this).text("To");
            }
        });
    });
});

$(function () {
    $('.timepicker').timepicker({
        timeFormat: 'HH:mm',
        interval: 15,
        disableTextInput:true,
    });
});