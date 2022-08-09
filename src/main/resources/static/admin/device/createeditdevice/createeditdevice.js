$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../../navbar/navbar.html");

    fetchDeviceTypes();

    // add header depending on create/edit
    const isEdit = /[0-9]+$/.test(window.location.pathname);
    if (isEdit) {
        $("#bodyHeader").text("Edit Device");
        $("#submitButton").text("Edit");

        const id = getUrlPathId(window);
        const onsuccess = function (data) {
            $("#codeInput").val(data.code ? data.code : '');
            $("#deviceType").val(JSON.stringify(data.type));
            $("#disabledCheck").attr('checked', data.disabled);
        };

        fetchWithSelectors('GET', '/api/device/' + id, null, onsuccess, null);
    } else {
        $("#bodyHeader").text("Create Device")
        $("#submitButton").text("Create");
    }

    $("#submitButton").on('click', function (e) {
        e.preventDefault()

        // validation
        if (!$("#codeInput").val()) {
            $("#codeInput").addClass("is-invalid");
            return;
        } else {
            $("#codeInput").removeClass("is-invalid");
        }

        if (!$("#deviceType").val()) {
            $("#deviceType").addClass("is-invalid");
            return;
        } else {
            $("#deviceType").removeClass("is-invalid");
        }

        // requests on success validation
        if (isEdit) {
            const onsuccess = function () {
                window.location.pathname = "/admin/device"
            };
            const id = getUrlPathId(window);
            fetch('PUT', "/api/device/", { id: id, code: $("#codeInput").val(), type: JSON.parse($("#deviceType").val()), disabled: $("#disabledCheck").is(":checked"), }, onsuccess, null);
        } else {
            const onsuccess = function () {
                window.location.pathname = "/admin/device"
            };
            fetch('POST', "/api/device/", { code: $("#codeInput").val(), type: JSON.parse($("#deviceType").val()), disabled: $("#disabledCheck").is(":checked"), }, onsuccess, null);
        }

    })
});

function createOption(deviceType) {
    return $("<option/>").attr('value', JSON.stringify(deviceType)).text(deviceType.name);
}

function fetchDeviceTypes() {
    const onsuccess = function (data) {
        if (data.length !== 0) {
            data.forEach(deviceType => {
                $('#deviceType').append(createOption(deviceType));
            });
        }

    }
    fetchWithSelectors('GET', '/api/devicetypes/', null, onsuccess, null);
}