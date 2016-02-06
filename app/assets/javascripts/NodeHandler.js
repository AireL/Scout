function createNode() {
    var nodeName = $("#nodeName").val()
    var createNodeUrl = 'http://localhost:9000/addNode';
    $.ajax (
        {
        type: "POST",
        url: createNodeUrl,
        data: "{ \"nodeName\": \"" + nodeName + "\" }",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        cache: false,
        success: function(data) {},
        error: function (msg) {
            alert(msg.responseText);
        }
    });
};