var SubNodeWithColumns = function(cols, title, desc){
    var smallest;
    cols.each(function(){
      if(smallest == undefined){
        smallest = this;
      }else if(smallest.childElementCount > this.childElementCount){
        smallest = this;
      }
    });
    SubNode(smallest, title, desc)
}

var SubNode = function SubNode(parent, title, desc) {
    var template = '<div class="panel panel-default"><div class="panel-heading">{{title}}</div><div class="panel-body">{{desc}}</div></div>';
    Mustache.parse(template);
    var rendered = Mustache.render(template, {title: title, desc: desc});
    $(parent).append(rendered);
};
