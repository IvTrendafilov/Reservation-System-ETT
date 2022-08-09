$(function () {
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/admin/navbar/navbar.html");
    $("#navbar").load("../../navbar/navbar.html");

    $("#bodyHeader").text("Create Admin")
    $("#submitButton").text("Create");

    $("#submitButton").on('click', function (e) {
        e.preventDefault()

        // validation
        if (!$("#emailInput").val()) {
            $("#codeInput").addClass("is-invalid");
            return;
        } else {
            $("#emailInput").removeClass("is-invalid");
        }

        if (!$("#nameInput").val()) {
            $("#codeInput").addClass("is-invalid");
            return;
        } else {
            $("#nameInput").removeClass("is-invalid");
        }

        const onsuccess = function () {
            window.location.pathname = "/admin/admin"
        };
        fetchWithSelectors('POST', "/api/userdetails/admin", { name: $("#nameInput"), email: $("#emailInput"), }, onsuccess, null);
    })
});