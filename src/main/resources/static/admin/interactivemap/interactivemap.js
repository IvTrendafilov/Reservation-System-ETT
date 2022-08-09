$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");

    // render map and div for pcs
    renderInteractiveMap("#canvas", 1.2, [ ], [ ])
    $("#interactive-map").attr("width", 800 * 1.2);
    $("#interactive-map").css({
        position: 'absolute',
        width: 800 * 1.2 + 'px',
        height: 754 * 1.2 + 'px',
    })

    // fetch all devices with their positioning
    fetchDevices()
    $('[data-bs-toggle="tooltip"]').tooltip();
    $("#submitButton").on('click', function () {
        const devices = []
        $( ".draggable-device" ).each(function () {
            const device = $(this);
            // save id and position of device
            devices.push({
                id: parseInt(device.attr("id")),
                position: {
                    top: device.position().top,
                    left: device.position().left,
                },
            });
        });

        function onsuccess() {
            addSuccessToast("Successfully saved device positions!");
        }
        // send them to backend
        fetch('PUT', '/api/device/positions', devices, onsuccess , null);
    });
});

function fetchDevices() {
    const onsuccess = function (data) {
       data.forEach(function (device) {
           const tooltipTitle = "Code: " + device.code + "<br>" + "Type: " + device.type.name + "<br>";
           const pc =  $("<i/>")
               .attr('data-bs-toggle', 'tooltip')
               .attr('data-bs-placement', 'top')
               .attr('data-bs-html', 'true')
               .attr('title', tooltipTitle)
               .addClass(device.type.imageClass)
               .addClass('draggable-device' + (device.color === 'BLACK' ? ' device' : ''))
               .attr('id', device.id);

           if (device && device.position) {
               pc.css({ color: 'black', position: 'relative', top: device.position.top + "px", left: device.position.left + "px" })
                $("#interactive-map").append(pc);
           } else {
               $("#interactive-map").append(pc);
           }
       });
        $( ".draggable-device" ).draggable({ containment: "parent" });
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, { trigger: 'hover' });
        });
        $('[data-bs-toggle="tooltip"]').on('click', function () { $(this).tooltip('hide') });
    }

    fetchWithSelectors('GET', '/api/device/query?' + $.param({ disabled: false }), null, onsuccess, null);
}