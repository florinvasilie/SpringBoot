var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('get', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
        var status = xhr.status;
        if (status == 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send();
};
getJSON('http://localhost:8080/', function(err, data) {
    if (err != null) {
        alert('Something went wrong: ' + err);
    } else {
        alert('Your Json result is:  ' + data.result);
        // document.getElementById("rezultatul").innerHTML = data.result;
        result.innerText = data.result;
    }
});