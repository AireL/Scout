function loadTable(containerName) {
    $("#" + containerName).load("/table");
};

function updateTable() {
    var service = 'http://localhost:9000/ints';
    jQuery.support.cors = true;

    $.ajax(
    {
        type: "GET",
        url: service,
        data: "{}",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        cache: false,
        success: function(data) {
            createTableWithData(data);
        },
        error: function (msg) {
            alert(msg.responseText);
        }
    });
};

function createTableWithData(data) {
   var table = document.getElementById("tableOne");
   emptyTable(table);
   $.each(data, function (i, item) {
       var row = table.insertRow(i+1);
       var cell = row.insertCell(0);
       cell.innerHTML = data[i];
   });
};

function emptyTable(table){
    for(var i = table.rows.length - 1; i > 0; i--)
   {
       table.deleteRow(i);
   }
};