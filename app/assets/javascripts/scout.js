(function () {
  'use strict';

  angular
    .module('scout', [
      'scout.config',
      'scout.routes',
      'scout.layout',
      'scout.nodes',
      'scout.utils'
    ]);

  angular
    .module('scout.config', []);

  angular
    .module('scout.routes', ['ngRoute']);

  angular
    .module('scout')
    .run(run);

  run.$inject = ['$http'];

  /**
   * @name run
   * @desc Update xsrf $http headers to align with Django's defaults
   */
  function run($http) {
  }
})();
