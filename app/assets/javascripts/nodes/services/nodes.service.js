/**
 * Posts
 * @namespace scout.nodes.services
 */
(function () {
  'use strict';

  angular
    .module('scout.nodes.services')
    .factory('Nodes', Nodes);

  Nodes.$inject = ['$http'];

  /**
   * @namespace Node
   * @returns {Factory}
   */
  function Nodes($http) {
    var Nodes = {
      root: root
    };

    return Nodes;

    ////////////////////
    
    /**
     * @name root
     * @desc Get root Node
     * @returns {Promise}
     * @memberOf scout.nodes.services.Posts
     */
    function root() {
      return $http.get('/api/v1/root');
    }
  }
})();
