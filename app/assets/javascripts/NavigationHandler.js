function loadUrl(url) {
    $("#buttstallion").load(url);
};

function interceptBack() {
    var url = window.location.hash;
    createBreadcrumbs(url);

    if (url === "") {
        loadUrl('/home');
    } else if (url.startsWith('#')) {
        var path = url.substring(2);
        var firstSlashIdx = path.indexOf('/');
        var rest = "";
        var firstPart = path;
        if (firstSlashIdx > 0) {
            firstPart = path.substring(0, firstSlashIdx);
            rest = path.substring(firstSlashIdx + 1);
        }
        if (firstPart === "") {
            loadUrl('/home');
        } else if (firstPart === "node") {
            SubNodes.showForPath(rest);
        } else if (firstPart === "create") {
            handleCreatePages(rest);
        }
    }
};

function handleCreatePages(path) {
    if (path === "") {
        loadUrl('/create');
    } else {
        loadUrl('/nodeForm');
    }
};

function createBreadcrumbs(url) {
    var elements = url.substring(url.indexOf('/') + 1).split('/');
    var bcs = $("#breadcrumbs");
    bcs.children().remove();
    var template = '<li><a href="#/{{url}}">{{name}}</a></li>';
    var curPath = elements[0];
    Mustache.parse(template);
    var count = 1;
    var rendered = Mustache.render(template, {
      url : "",
      name : "home"
    });
    bcs.append(rendered);
    elements.forEach ( function(el) {
        rendered = Mustache.render(template, {
          url : curPath,
          name : el
        });
        bcs.append(rendered);
        curPath += '/' + elements[count];
        count++;
    });
};