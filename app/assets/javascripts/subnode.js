var SubNodes = function SubNodes() {

  var init = function(branch, leaf, model) {
    this.model = model;
    this.branch = branch;
    this.leafTable = leaf;
    this.cols = branch.children();
    draw.call(this)
  };

  var showForId = function(id) {
    draw.call(this, id);
  };
  
  var showForPath = function(path) {
    draw.call(this, findChildFromPath(path).id);
  }
  
  var getIdForPath = function(path) {
    return findChildFromPath.call(this,path).id
  }

  var findChildFromPath = function(path) {
    var curNode = this.model;
    if(path === "") {
      return curNode;
    }
    var pathParts = path.split("/");
    var queue = [];
    var curChild;
    var curPart = pathParts.shift();
    
    queue = queue.concat(curNode.children);
    while(queue.length > 0) {
      curChild = queue.shift();
      if(curChild.displayName === curPart) {
        queue = curChild.children;
        curPart = pathParts.shift();
        if(curPart === undefined) { 
          break;
        }
      }
    }
    
    if(pathParts.length !==0) {
      // warn user
      alert("Couldn't find: " + path);
      return model;
    } else {
      return curChild;
    }
    
  }
  
  var addSubNode = function(child) {
    var smallest;
    this.cols.each(function() {
      if (smallest == undefined) {
        smallest = this;
      } else if (smallest.childElementCount > this.childElementCount) {
        smallest = this;
      }
    });
    drawSubNode.call(this, smallest, child)
  };

  var drawSubNode = function drawSubNode(parent, child) {
    var hash = window.location.hash;
    if(hash === "") {
      hash = "#";
    }
    var template = '<div class="panel panel-default"><div class="panel-heading">{{title}}</div><div class="panel-body">{{desc}}<a href="{{url}}/{{desc}}" onclick="SubNodes.showForId(\'{{id}}\')"><span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span></a></div></div>';
    Mustache.parse(template);
    var rendered = Mustache.render(template, {
      title : child.displayName,
      desc : child.displayName,
      url : hash,
      id : child.id
    });
    $(parent).append(rendered);
  };

  var draw = function draw(id) {
    clearCols.call(this);
    var parent = this.model
    if (id != undefined) {
      var stack = [];
      stack.push(parent);
      var curChild;
      console.log("bob!");
      while (stack.length != 0) {
        curChild = stack.shift();
        if (curChild.id === id) {
          parent = curChild
          break;
        }
        stack = stack.concat(curChild.children);
      }
    }

    var children = parent.children;
    var leaves = [];
    var branches = [];
    children.forEach(function(child) {
      if (child.params.extensible === "true") {
        branches.push(child);
      } else {
        leaves.push(child);
      }
    });

    if (branches.length === 0) {
      this.branch.append("<p id=\"no-nodes\"> No Sub-Nodes</p>");
    } else {
      branches.forEach(function(branch) {
        addSubNode.call(this, branch);
      }.bind(this));
    }

    clearTable.call(this)
    var template = "<tr> <td>{{title}}</td></td>";
    Mustache.parse(template);

    leaves.forEach(function(leaf) {
      var rendered = Mustache.render(template, {
        title : leaf.displayName
      });
      this.leafTable.children('tbody:last-child').append(rendered);
    }.bind(this))
  };

  var clearCols = function clearCols() {
    this.cols.each(function() {
      while (this.firstChild) {
        this.removeChild(this.firstChild);
      }
    });
  }

  var clearTable = function clearTable() {
    this.leafTable.children("tbody tr").remove();
  };

  return {
    init : init,
    showForId : showForId,
    showForPath : showForPath,
    getIdForPath : getIdForPath
  };
}();