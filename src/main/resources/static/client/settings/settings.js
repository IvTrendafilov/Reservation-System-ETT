$(function () { 
    // we need the first one for spring boot to fetch it
    $("#navbar").load("/client/navbar/navbar.html");
    $("#navbar").load("../navbar/navbar.html");
});