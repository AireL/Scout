/**
 * NodesController
 * @namespace scout.utils.controllers
 */
(function() {
    'use strict';
    
    angular
    .module('scout.utils.controllers')
    .controller('BreadcrumbsController', BreadcrumbsController);
    
    BreadcrumbsController.$inject = ['$scope', '$location'];
    
    /**
   * @namespace BreadcrumbsController
   */
    function BreadcrumbsController($scope, $location) {
        
        activate();
        
        /**
     * @name activate
     * @desc Actions to be performed when this controller is instantiated
     * @memberOf scout.utils.controllers.BreadcrumbsController
     */
        function activate() {
            $scope.$on('$locationChangeStart', makeCrumbs)
        
        }
        
        function makeCrumbs() {
            var path = $location.url();
            
            var crumbs = [];
            if (path.endsWith("/")) {
                path = path.substring(0, path.length - 1);
            }
            var pathPaths = path.split('/');
            
            for (var i = 0; i < pathPaths.length; i++) {
                var part = pathPaths[i];
                var name = part.toUpperCase();
                var link = pathPaths.slice(0, i + 1).join('/');
                if (name.length === 0) {
                    name = "HOME";
                    link = "/";
                }
                crumbs.push({
                    "name": name,
                    "url": link
                });
            }
            $scope.breadcrumbs = crumbs;
        }
    }
})();
