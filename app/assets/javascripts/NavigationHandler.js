function loadUrl(url, func) {
  if (func) {
    $("#buttstallion").load(url, func);
  } else {
    $("#buttstallion").load(url);
  }
}

var loadedHtml = "";

var mappedFunction = {
    "node" : SubNodes.showForPath
}

function interceptBack() {
  var url = window.location.hash;
  createBreadcrumbs(url);

  if (url === "") {
    if (loadedHtml !== "node") {
      loadUrl('/home', function() {
        mappedFunction["node"].call(SubNodes, "");
      });
      loadedHtml = "node";
    } else {
      mappedFunction["node"].call(SubNodes, "");
    }
  } else if (url.startsWith('#')) {
    var path = url.substring(2);
    var firstSlashIdx = path.indexOf('/');
    var rest = "";
    var firstPart = path;
    if (firstSlashIdx > 0) {
      firstPart = path.substring(0, firstSlashIdx);
      rest = path.substring(firstSlashIdx + 1);
    }
    if (firstPart === "" || firstPart === "node") {
      if (loadedHtml !== "node") {
        loadUrl('/home', function() {
          mappedFunction["node"].call(SubNodes, rest);
        });
        loadedHtml = "node";
      } else {
        mappedFunction["node"].call(SubNodes, rest);
      }
    } else if (firstPart === "create") {
      handleCreatePages(rest);
      loadedHtml = "create";
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
    name : "Home"
  });
  bcs.append(rendered);
  elements.forEach(function(el) {
    rendered = Mustache.render(template, {
      url : curPath,
      name : el
    });
    bcs.append(rendered);
    curPath += '/' + elements[count];
    count++;
  });
};