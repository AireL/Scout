/**
 * NodesController
 * @namespace scout.nodes.controllers
 */
(function () {
  'use strict';

  angular
    .module('scout.nodes.controllers')
    .controller('NodesController', NodesController);

  NodesController.$inject = ['$scope', '$routeParams', 'Nodes'];

  /**
   * @namespace NodesController
   */
  function NodesController($scope, $routeParams, Nodes) {
    var vm = this;

    vm.columns = [];
    vm.root;
    vm.parent;
    vm.path;
    vm.leaves;

    activate();


    /**
     * @name activate
     * @desc Actions to be performed when this controller is instantiated
     * @memberOf scout.nodes.controllers.NodesController
     */
    function activate() {
      Nodes.root().then(modelLoaded);
      vm.path = $routeParams.nodePath;
      $scope.$watch(function () { return $scope.branches; }, render);
      $scope.$watch(function () { return vm.leaves; }, render);
    }
    
    /**
     * @name calculateNumberOfColumns
     * @desc Calculate number of columns based on screen width
     * @returns {Number} The number of columns containing Nodes
     * @memberOf Scout.nodes.controllers.NodesControllers
     */
    function calculateNumberOfColumns() {
      var width = $(window).width();

      if (width >= 1200) {
        return 3;
      } else if (width >= 992) {
        return 2;
      } else {
        return 1;
      }
    }

    /**
     * @name render
     * @desc Renders Nodes
     * @memberOf scout.nodes.controllers.NodesController
     */
    function render(current, original) {
      if(current != original) {
        vm.columns = [];
      
        var numCols = calculateNumberOfColumns();
        for (var i = 0; i < numCols; ++i) {
          vm.columns.push([]);
        }

        
        for (var i = 0; i < $scope.branches.length; i++) {
          vm.columns[i % numCols].push($scope.branches[i]);  
        }
        
      }
    }

    function modelLoaded(data, status, headers, config) {
      vm.root = data.data;
      sortChildren();
    }

    function sortChildren() {
      var parent = findChildFromPath(vm.path)
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

      vm.leaves = leaves;
      $scope.branches = branches;
      vm.parent = parent;
    }

    function findChildFromPath(path) {
      var curNode = vm.root;
      if (path === "" || path === undefined) {
        return curNode;
      }
      var pathParts = path.split("/");
      var queue = [];
      var curChild;
      var curPart = pathParts.shift();

      queue = queue.concat(curNode.children);
      while (queue.length > 0) {
        curChild = queue.shift();
        if (curChild.displayName === curPart) {
          queue = curChild.children;
          curPart = pathParts.shift();
          if (curPart === undefined) {
            break;
          }
        }
      }

      if (pathParts.length !== 0) {
        // warn user
        alert("Couldn't find: " + path);
        return model;
      } else {
        return curChild;
      }
    }
  }
})();
