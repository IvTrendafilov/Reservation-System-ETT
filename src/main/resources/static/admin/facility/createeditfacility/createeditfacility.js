$(function () {
    // initialize everything needed
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../../navbar/navbar.html");
    $('.js-example-basic-multiple').select2();

    // add header depending on create/edit
    const isEdit = /[0-9]+$/.test(window.location.pathname)

    let getActiveRoom;
    let inuseRoomIds = [ ]
    const onsuccess = function (data) {
        inuseRoomIds = data.map(facility => facility.roomId);

        if (isEdit) {
            $("#bodyHeader").text("Edit Facility");
            $("#submitButton").text("Edit");

            const id = getUrlPathId(window);
            const onsuccess = function (data) {
                $("#nameInput").val(data.name ? data.name : '');
                $("#facilityTypeSelect").val(data.facilityType);
                $("#facilityTypeSelect").trigger('change');
                $("#disabledCheck").attr('checked', data.disabled);
                getActiveRoom = renderInteractiveMap("#canvas", 1.2, [ data.roomId ], inuseRoomIds)
            };

            fetchWithSelectors('GET', '/api/facility/' + id, null, onsuccess, null);
        } else {
            $("#bodyHeader").text("Create Facility")
            $("#submitButton").text("Create");
            getActiveRoom = renderInteractiveMap("#canvas", 1.2, [ ], inuseRoomIds)
        }
    }
    fetchWithSelectors('GET', '/api/facility/query?' + $.param({ disabled: false }), null, onsuccess, null);


    $("#submitButton").on('click', function (event) {
        event.preventDefault();
        const room = getActiveRoom();

        // validation
        if (!$("#nameInput").val()) {
            $("#nameInput").addClass("is-invalid");
            return;
        } else {
            $("#nameInput").removeClass("is-invalid");
        }

        // validation
        if ($("#facilityTypeSelect").val().length === 0) {
            $("#facilityTypeSelect").addClass("is-invalid");
            return;
        } else {
            $("#facilityTypeSelect").removeClass("is-invalid");
        }

        if (!room) {
            alert("No room chosen");
        }

        const onsuccess = function () {
            window.location.pathname = "/admin/facility"
        };

        // requests on success validation
        if (isEdit) {
            const id = getUrlPathId(window);
            fetch('PUT', "/api/facility/", { id: id, name: $("#nameInput").val(), facilityType: $("#facilityTypeSelect").val(),  disabled: $("#disabledCheck").is(":checked"), roomId: room.id }, onsuccess, null);
        } else {
            fetch('POST', "/api/facility/", { name: $("#nameInput").val(), facilityType: $("#facilityTypeSelect").val(),  disabled: $("#disabledCheck").is(":checked"), roomId: room.id }, onsuccess, null);
        }
    })

});