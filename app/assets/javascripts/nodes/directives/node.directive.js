/**
 * Node
 * @namespace scout.nodes.directives
 */
(function () {
  'use strict';

  angular
    .module('scout.nodes.directives')
    .directive('node', node);

  /**
   * @namespace Node
   */
  function node() {
    /**
     * @name directive
     * @desc The directive to be returned
     * @memberOf scout.nodes.directives.Node
     */
    var directive = {
      restrict: 'E',
      scope: {
        node: '='
      },
      templateUrl: 'assets/templates/subnode.html'
    };

    return directive;
  }
})();
