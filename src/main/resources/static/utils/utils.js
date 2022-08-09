// Utility function for fetching fron BE
// requestType - Must be a valid HTTP Request type (GET, PUT, POST, DELETE)
// url - the url to hit the backend
// data - if you want to pass any data, otherwise leave null, IMPORTANT the values must be a jquery selector to the input field!!!!!
// EXAMPLE OF DATA
// {
//   name: $('#createPersonNameInput'),
//   age: $('#createPersonAgeInput'),
//   bsn: $('#createPersonBsnInput'),
// }
// onsuccess callback
// onerror callback
function fetchWithSelectors(requestType, url, data, onsuccess, onerror) {
    
    let formattedData = null;
    if (data) {

        formattedData = { };
        Object.entries(data).forEach(function ([key, selector]) {
            if (key === 'id') {
                formattedData[key] = selector;
            }
            else if (selector.attr('type') === 'checkbox') {
                formattedData[key] = selector.is(":checked");
            } else {
                formattedData[key] = selector.val();
            }
        });
    }
    $.ajax({
        url: url,
        type: requestType,
        contentType: "application/json; charset=utf-8",
        dataType: requestType === 'DELETE' ? "text" : "json",
        data: formattedData !== null ? JSON.stringify(formattedData) : undefined,
        success: function(data){
            if (onsuccess) {
                onsuccess(data);
            }
        },
        error: function(error){
            if (onerror) {
                onerror(error);
            } else {
                if (error.responseJSON) {
                    addErrorToast(error.responseJSON.message)
                } else if (error.message) {
                    addErrorToast(error.message)
                } else if (error.responseText) {
                    addErrorToast(JSON.parse(error.responseText).message);
                }
            }
        },
    });
};


async function syncFetch(requestType, url, data, onsuccess, onerror) {
    
    let result = null;
    try {
        result = await $.ajax({
            url: url,
            type: requestType,
            contentType: "application/json; charset=utf-8",
            dataType: requestType === 'DELETE' ? "text" : "json",
            data: data !== null ? JSON.stringify(data) : undefined,
            success: function(data){
                return data;
            },
            error: function(error){
                if (onerror) {
                    onerror(error);
                } else {
                    if (error.responseJSON) {
                        addErrorToast(error.responseJSON.message)
                    } else if (error.message) {
                        addErrorToast(error.message)
                    } else {
                        addErrorToast(JSON.parse(error.responseText).message);;
                    }
                }
            },
        });
    } catch (error) {
        console.error(error);
    }
    return result;
}
//fetch without selectors, but with normal JSON as data.
function fetch(requestType, url, data, onsuccess, onerror) {
    $.ajax({
        url: url,
        type: requestType,
        contentType: "application/json; charset=utf-8",
        dataType: requestType === 'DELETE' ? "text" : "json",
        data: data !== null ? JSON.stringify(data) : undefined,
        success: function(data){
            if (onsuccess) {
                onsuccess(data);
            }
        },
        error: function(error){
            if (onerror) {
                onerror(error);
            } else {
                if (error.responseJSON) {
                    addErrorToast(error.responseJSON.message)
                } else if (error.message) {
                    addErrorToast(error.message)
                } else {
                    addErrorToast(JSON.parse(error.responseText).message);;
                }
            }
        },
    });
};

function getUrlPathId(window) {
    return window.location.pathname.split("/").pop();
};

function addSuccessToast(message) {
    $(document.body).append("" +
        "<div class=\"toast\" id=\"myToast\" style=\"position: absolute; top: 10px; right: 10px; background-color: green;\">" +
        "    <div class=\"toast-header w-100\" style=\"background-color: green;\">" +
        "        <i class=\"bi bi-check-lg-square\" style=\"color: black;\"></i>&nbsp;" +
        "        <strong class=\"mr-auto\" style=\"color: black;\">Success</strong>" +
        "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"toast\" style=\"color: black; position: absolute; top: 5px; right: 10px;\"></button>" +
        "    </div>" +
        "    <div class=\"toast-body\">" +
        ""       + message + "" +
        "    </div>" +
        "</div>")
    $("#myToast").toast({
        delay: 2000,

    });
    $("#myToast").toast("show");
}


function addErrorToast(message) {
    $(document.body).append("" +
        "<div class=\"toast\" id=\"myToast\" style=\"position: absolute; top: 10px; right: 10px; background-color: red;\">\n" +
        "    <div class=\"toast-header w-100\" style=\"background-color: red;\">\n" +
        "        <i class=\"bi bi-check-lg-square\" style=\"color: black;\"></i>&nbsp;\n" +
        "        <strong class=\"mr-auto\" style=\"color: black;\">Error</strong>\n" +
        "            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"toast\" style=\"color: black; position: absolute; top: 5px; right: 10px;\"></button>\n" +
        "    </div>\n" +
        "    <div class=\"toast-body\">\n" +
        "" + message + "" +
        "    </div>\n" +
        "</div>")
    $("#myToast").toast({
        delay: 2000,

    });
    $("#myToast").toast("show");
}

function formatDate(date, pattern = null) {
    const month = ((date.getMonth() < 10) ? '0' : '') + (date.getMonth() + 1);
    const day = ((date.getDate() < 10) ? '0' : '') + date.getDate();
    const year = date.getFullYear();
    if (pattern) {
        return pattern.replace('d', day).replace('m', month).replace('y', year);
    }
    return month + '/' + day + '/' + year;
}

function getMonthRange(date) {
    var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    return { from: formatDate(firstDay), to: formatDate(lastDay) };
}

function isGlobalAdmin() {
    const userDetails = JSON.parse(localStorage.getItem('userDetails'))
    if (userDetails) {
        return userDetails.authorities.map(x => x.authority).find(x => x === 'GLOBAL_ADMIN');
    }
    return false;
}

// SECURITY UTILS

function logout() {
    window.location.href = "/logout";
}
