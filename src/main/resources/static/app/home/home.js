/**
 * @param $scope $rootScope
 * @param $http $httpProvider
 */
function controller_home($scope, $http){
    $scope.test = "blah";
    $scope.doalert = function(){
        alert($scope.test);
    }
}