$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../../navbar/navbar.html");

    // add header depending on create/edit
    const isEdit = /[0-9]+$/.test(window.location.pathname);

    if (isEdit) {
        $("#bodyHeader").text("Edit Device Type");
        $("#submitButton").text("Edit");

        const id = getUrlPathId(window);
        const onsuccess = function (data) {
            $("#nameInput").val(data.name ? data.name : '');
            $("#imageClassInput").val(data.imageClass ? data.imageClass : '');
        };

        fetchWithSelectors('GET', '/api/devicetypes/' + id, null, onsuccess, null);
    } else {
        $("#bodyHeader").text("Create Device Type")
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

        if (!$("#imageClassInput").val()) {
            $("#imageClassInput").addClass("is-invalid");
            return;
        } else {
            $("#imageClassInput").removeClass("is-invalid");
        }

        // requests on success validation
        if (isEdit) {
            const onsuccess = function () {
                window.location.pathname = "/admin/devicetypes"
            };
            const id = getUrlPathId(window);
            fetchWithSelectors('PUT', "/api/devicetypes/", { id: id, name: $("#nameInput"), imageClass: $("#imageClassInput"), }, onsuccess, null);
        } else {
            const onsuccess = function () {
                window.location.pathname = "/admin/devicetypes"
            };
            fetchWithSelectors('POST', "/api/devicetypes/", { name: $("#nameInput"), imageClass: $("#imageClassInput"), }, onsuccess, null);
        }

    })
});