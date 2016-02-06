var SubNodes = function SubNodes(){

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

    var addSubNode = function(child){
        var smallest;
        this.cols.each(function(){
          if(smallest == undefined){
            smallest = this;
          }else if(smallest.childElementCount > this.childElementCount){
            smallest = this;
          }
        });
        drawSubNode.call(this,smallest, child)
    };

    var drawSubNode = function drawSubNode(parent, child) {
        var template = '<div class="panel panel-default"><div class="panel-heading">{{title}}</div><div class="panel-body">{{desc}}<span onclick="SubNodes.showForId(\'{{id}}\')" class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span></div></div>';
        Mustache.parse(template);
        var rendered = Mustache.render(template, {title: child.displayName, desc: child.displayName, id: child.id});
        $(parent).append(rendered);
    };

    var draw = function draw(id) {
        clearCols.call(this);
        var parent = this.model
        if(id != undefined) {
            var stack = [];
            stack.push(parent);
            var curChild;
            console.log("bob!");
            while(stack.length != 0) {
                curChild = stack.shift();
                if(curChild.id === id) {
                    parent = curChild
                    break;
                }
                stack = stack.concat(curChild.children);
            }
        }

        var children = parent.children;
        var leaves =[];
        var branches = [];
        children.forEach(function(child){
            if(child.params.extensible === "true") {
                branches.push(child);
            } else {
                leaves.push(child);
            }
        });



        if(branches.length === 0 ) {
            this.branch.append("<p id=\"no-nodes\"> No Sub-Nodes</p>");
        }else{
            branches.forEach(function(branch) {
                addSubNode.call(this, branch);
            }.bind(this));
        }

        clearTable.call(this)
        var template = "<tr> <td>{{title}}</td></td>";
        Mustache.parse(template);

        leaves.forEach(function(leaf) {
            var rendered = Mustache.render(template, {title: leaf.displayName});
            this.leafTable.children('tbody:last-child').append(rendered);
        }.bind(this))
    };

    var clearCols = function clearCols() {
        this.cols.each(function(){
            while(this.firstChild) {
                this.removeChild(this.firstChild);
            }
        });
    }

    var clearTable = function clearTable() {
        this.leafTable.children("tbody tr").remove();
    };

    return {
        init : init,
        showForId : showForId
    };
}();