/**
 * Node
 * @namespace scout.utils.directives
 */
(function () {
  'use strict';

  angular
    .module('scout.utils.directives')
    .directive('breadcrumbs', breadcrumbs);

  /**
   * @namespace Node
   */
  function breadcrumbs() {
    /**
     * @name directive
     * @desc The directive to be returned
     * @memberOf scout.utils.directives.Breadcrumbs
     */
    var directive = {
      restrict: 'E',
      scope: {
        breadcrumbs: '='
      },
      templateUrl: 'assets/templates/breadcrumbs.html'
    };

    return directive;
  }
})();
