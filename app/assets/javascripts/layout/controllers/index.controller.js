/**
 * IndexController
 * @namespace scout.layout.controllers
 */
(function () {
  'use strict';

  angular
    .module('scout.layout.controllers')
    .controller('IndexController', IndexController);

  IndexController.$inject = ['$scope'];

  /**
   * @namespace IndexController
   */
  function IndexController($scope) {
    var vm = this;

    activate();

    /**
     * @name activate
     * @desc Actions to be performed when this controller is instantiated
     * @memberOf scout.layout.controllers.IndexController
     */
    function activate() {

    }
  }
})();
