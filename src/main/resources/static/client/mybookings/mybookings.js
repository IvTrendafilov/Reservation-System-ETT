$(function () {
    //
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/client/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");
    $('.datetimepicker').datetimepicker({
        format: 'd/m/Y H:i'
    });
    fetchBookings(null,null, null, null, 1, sort);

    const from = $("#fromPicker");
    const to = $("#toPicker");
    const status = $("#statusPicker");
    const searchQuery = $("#searchQuery");


    $(".filter").on('change', function () {
        fetchBookings(searchQuery.val(), from.val(), to.val(), status.val(), 1, sort);
    });

    $("#deviceBookings").on('click', function () {
        $(this).toggleClass('active');
        $("#facilityBookings").toggleClass('active');
        $("#deviceColumn").show();
        fetchBookings(searchQuery.val(), from.val(), to.val(), status.val(), 1, sort);

    });

    $("#facilityBookings").on('click', function () {
        $(this).toggleClass('active');
        $("#deviceBookings").toggleClass('active');
        $("#deviceColumn").hide();
        fetchBookings(searchQuery.val(), from.val(), to.val(), status.val(), 1, sort);
    });

    $(".close-modal").on('click', function () {
        $('#exampleModal').modal('hide');
    });

    $("#confirmButton").on('click', function () {
        $('#exampleModal').modal('hide');
    });


    $(".sortable").on('click', function () {
        sortOn($(this));
        fetchBookings(searchQuery.val(), from.val(), to.val(), status.val(), 1, sort);
    })

    $(".sortable").append(function () {
        const name = $(this).attr('id');
        const sortType = sort.find(x => x.startsWith(name));
        if (sortType) {
            switch (sortType.split(";")[1]) {
                case "ASC":
                    return "<i class=\"bi bi-arrow-up sortIcon\"></i>"
                case "DESC":
                    return "<i class=\"bi bi-arrow-down sortIcon\"></i>"
            }
        }
        return null;
    });
});

let page = 1;
let sort = JSON.parse(localStorage.getItem("mybookingsSorting")) || [];

function sortOn(selector) {
    const name = selector.attr('id');
    if (sort.some(x => x.startsWith(name))) {
        const isAsc = sort.some(x => x === (name + ";ASC"));
        if (isAsc) {
            sort = sort.map(x => {
                if (x.startsWith(name)) return name + ";DESC"
                return x
            });
            selector.find(".sortIcon").remove();
            selector.append("" +
                "<i class=\"bi bi-arrow-down sortIcon\"></i>")
        } else {
            sort = sort.filter(x => !x.startsWith(name));
            selector.find(".sortIcon").remove();
        }
    } else {
        sort.push(name + ";ASC");
        selector.find(".sortIcon").remove();
        selector.append("" +
            "<i class=\"bi bi-arrow-up sortIcon\"></i>")
    }
    localStorage.setItem("mybookingsSorting", JSON.stringify(sort));
}

function createRow(booking) {
    const isDevice = $("#deviceBookings").hasClass("active");
    const row = $("<tr></tr>");
    row.append($("<td></td>").text(booking.from));
    row.append($("<td></td>").text(booking.to));
    if (isDevice) row.append($("<td></td>").text(booking.deviceCodes.join(", ")));
    row.append($("<td></td>").text((booking.remarks ? booking.remarks : ' ')));
    row.append($("<td></td>").text(booking.status));

    row.append($("<td></td>").html(
        '<button' + ((booking.status === 'CANCELLED' || booking.status === 'REJECTED') ? ' disabled' : '') + ' type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="Cancel booking" onclick="onDeleteClick(' + booking.id + ')" class="btn btn-primary table-buttons"><i class="bi bi-x-lg"></i></button>' +
        '<button type="button" data-bs-toggle="tooltip" data-bs-placement="top" title="See location" onclick="showMapWithDevicePositions([' + (isDevice ? booking.devices.toString() : booking.facilityId) + '])" class="btn btn-primary table-buttons"><i class="bi bi-geo-alt"></i></button>'
    ));
    return  row;
}

function fetchBookings(query, from, to, status, page, sort) {
    const onsuccess = function (data) {
        $('#tableBody').empty();
        if (data.numberOfElements !== 0) {
            data.content.forEach(booking => {
                $('#tableBody').append(createRow(booking));
            });
        } else {
            $('#tableBody').append('<tr><td colspan="6">No data has been found!</td></tr>');
        }

        $('#demo').pagination({
            dataSource: function(done){
                var result = [];
                for (var i = 1; i <= data.totalElements ; i++) {
                    result.push(i);
                }
                console.log(result);
                done(result);
            },
            pageNumber: page,
            pageSize: 10,
            callback: function(data, pagination) {
                if (pagination.pageNumber !== page) {
                    page = pagination.pageNumber;
                    fetchBookings(query, from, to, status, pagination.pageNumber, sort);
                }
            }
        });
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }

    const filters = {
        type: $("#deviceBookings").hasClass("active") ? 'Device' : 'Facility',
        page: page,
        size: 10,
        sortBy: (sort || []).join(","),
    }

    if (query) filters['query'] = query;
    if (from) filters['from'] = from;
    if (to) filters['to'] = to;
    if (status) filters['status'] = status;

    fetchWithSelectors('GET', '/api/reservation/?' + $.param(filters), null, onsuccess, null);
}

function onDeleteClick(id) {
    const onsuccess = function () {
        fetchBookings($("#searchQuery").val(), $("#fromPicker").val(), $("#toPicker").val(), $("#statusPicker").val(), page, sort);
        addSuccessToast("You successfully cancelled a booking!");

    }
    fetch('POST', '/api/reservation/status/' + id + '/CANCELLED', null, onsuccess, null);
}

function showMapWithDevicePositions(entities) {
    const isDevice = $("#deviceBookings").hasClass("active");
    const mapContainer = $("#mapContainer");
    mapContainer.empty();

    $("#modal-title").text(isDevice ? "Device location" : "Facility location");
    if (isDevice) {
        function onsuccess(devices) {
            // add the map
            mapContainer.append("" +
                "<canvas id=\"canvas\" style=\"position: absolute\">\n" +
                "</canvas>\n" +
                "<div id=\"interactive-map\">\n" +
                "</div>");

            // initialize tha map
            renderInteractiveMap("#canvas", 1.2, [ ], [ ])
            $('.modal-dialog').css({
                width: 800 * 1.2 + 100 + 'px',
                height: 754 * 1.2 + 100 + 'px',
                margin: 'auto',
            });
            $("#mapContainer").css({
                position: 'relative',
                height: 754 * 1.2 + 'px',
            });
            $("#interactive-map").css({
                position: 'absolute',
                width: 800 * 1.2 + 'px',
                height: 754 * 1.2 + 'px',
            });

            // add all the devices
            devices.forEach(function (device) {
                if (device && device.position) {
                    const tooltipTitle = "Code: " + device.code + "<br>" + "Type: " + device.type.name + "<br>";
                    const isSelected = entities.includes(device.id);
                    const pc =  $("<i/>")
                        .attr('data-bs-toggle', 'tooltip')
                        .attr('data-bs-placement', 'top')
                        .attr('title', tooltipTitle)
                        .attr('data-bs-html', 'true')
                        .attr('id', device.id)
                        .addClass(device.type.imageClass)
                        .addClass('draggable-device' + (device.color === 'BLACK' ? ' device' : ''))
                        .css({ color: (isSelected ? 'green' : 'black'), position: 'relative', top: device.position.top + "px", left: device.position.left + "px" });

                    $("#interactive-map").append(pc);
                }
            });
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
            tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
            });
            $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
        }
        fetchWithSelectors('GET', '/api/device/query', null, onsuccess, null);
    } else {
        function onsuccess(facility) {
            // add the map
            mapContainer.append("" +
                "<canvas id=\"canvas\" style=\"position: absolute\">\n" +
                "</canvas>\n" +
                "<div id=\"interactive-map\">\n" +
                "</div>");

            // initialize tha map
            renderInteractiveMap("#canvas", 1.2, [ facility.roomId ], [ ])
            $('.modal-dialog').css({
                width: 800 * 1.2 + 100 + 'px',
                height: 754 * 1.2 + 100 + 'px',
                margin: 'auto',
            });
            $("#mapContainer").css({
                position: 'relative',
                height: 754 * 1.2 + 'px',
            });
            $("#interactive-map").css({
                position: 'absolute',
                width: 800 * 1.2 + 'px',
                height: 754 * 1.2 + 'px',
            });
        }
        fetchWithSelectors('GET', '/api/facility/' + entities , null, onsuccess, null);
    }

    $('#exampleModal').modal('show');
}