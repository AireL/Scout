function loadUrl(url) {
    $("#buttstallion").load(url);
}

function interceptBack() {
    var url = window.location.hash

    if (url === "") {
        loadUrl('/home')
    } else if (url.startsWith('#')) {
        var path = url.substring(2);
        var firstSlashIdx = path.indexOf('/');
        var rest = ""
        var firstPart = path
        if (firstSlashIdx > 0) {
            firstPart = path.substring(0, firstSlashIdx);
            rest = path.substring(firstSlashIdx + 1);
        }
        if (firstPart === "node") {
            SubNodes.showForPath(rest);
        }
    }
};