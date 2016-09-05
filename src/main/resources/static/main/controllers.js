var app = angular.module('cereadroApp', ['ngRoute','ngMaterial','ngMessages'])
    .factory('TokenStorage', tokenStorage)
    .factory('TokenAuthInterceptor', tokenAuthInterceptor)
    .factory('LoginService',loginService)
    .config(function($routeProvider, $httpProvider) {
        $httpProvider.interceptors.push('TokenAuthInterceptor');
    })
    .directive('ngEquals', [function () {
        return {
            restrict: 'A',
            scope: true,
            require: 'ngModel',
            link: function (scope, elem , attrs, control) {
                var checker = function () {
                    var e1 = scope.$eval(attrs.ngModel); //get the value of the first password
                    var e2 = scope.$eval(attrs.ngEquals); //get the value of the other password
                    return e1 == e2;
                };
                scope.$watch(checker, function (n) {
                    //set the form control to valid if both
                    //passwords are the same, else invalid
                    control.$setValidity("equality", n);
                });
            }
        };
    }])
    .controller("mainCtrl",function($scope, $mdDialog, TokenStorage, LoginService){
        var $mainScope = $scope;
        $mainScope.authenticated = false;
        $mainScope.user = TokenStorage.getObject();

        $mainScope.init = function () {
            LoginService.getCurrentUser().then(function(user){
                if(user.username !== 'anonymousUser'){
                    $mainScope.authenticated = true;
                    $mainScope.user = TokenStorage.getObject();
                }
            });
        };

        $mainScope.goToApp = function(e){
            window.location.href = "/app/index.html";
        };
        $mainScope.logOut = function () {
            // TODO: Invalidate the session on the server side as well
            //window.location.reload();  // TODO: Create feedback for the user
            // Feedback for the user - logoff traditionally reloads the page
            // If we don't do this, it doesn't feel like having logged off at all
            TokenStorage.clear();
            $mainScope.authenticated = false;
        };
        $mainScope.showLoginDialog = function(ev){showDialog(ev,0);};
        $mainScope.showSignupDialog = function(ev){showDialog(ev,1);};

        function showDialog(ev,index) {
            $mdDialog.show({
                controller: dialogController(index),
                templateUrl: '/main/dialog/dialog.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true
            })
            .then(function(answer){
                TokenStorage.store(answer);
                $mainScope.authenticated = true;
                $mainScope.user = TokenStorage.getObject();
                //$mainScope.goToApp();
            }, function(error){
                // Login cancelled
            });
        }
    });
//
// Definitions follow
//

function dialogController(index) {
    return function($scope, $http, $mdDialog, TokenStorage, LoginService){
        $scope.user = {username:"", password:""};
        $scope.password_repeat = "";
        $scope.logging_in = false;
        $scope.loginError = false;

        $scope.registering = false;
        $scope.registerError = false;

        $scope.login = function(){
            $scope.logging_in = true;
            $scope.loginError = false;
            LoginService.login($scope.user)
                .then(function(token){
                    TokenStorage.store(token);
                    $mdDialog.hide(token); // Dialog (promise) returns token
                })
                .catch(function(error){
                    $scope.loginError = true;
                })
                .then(function(){
                    $scope.logging_in = false;
                })
        };
        $scope.register = function(){

        };
        $scope.selected = index;
        $scope.closeDialog = $mdDialog.cancel;
    }
}

function tokenStorage() {
    var storageKey = 'auth_token';
    return {
        store : function(token) {
            return localStorage.setItem(storageKey, token);
        },
        getObject : function() {
            try{
                return JSON.parse(atob(localStorage.getItem(storageKey).split(".")[0]));
            } catch(e) {
                return null;
            }
        },
        getToken : function() {
            return localStorage.getItem(storageKey);
        },
        clear : function() {
            return localStorage.removeItem(storageKey);
        }
    };
}
function tokenAuthInterceptor($q, TokenStorage) {
    return {
        request: function(config) {
            var authToken = TokenStorage.getToken();
            if (authToken) {
                config.headers['X-AUTH-TOKEN'] = authToken;
            }
            return config;
        },
        responseError: function(error) {
            if (error.status === 401 || error.status === 403) {
                TokenStorage.clear();
            }
            return $q.reject(error);
        }
    };
}
function loginService($http) {
    return {
        getCurrentUser: function(){
            return $http.get('/api/users/current')
                .then(function (response) {
                    return response.data;
                }, function(error){
                    throw new Error("Failed to get current user", error);
                });
        },

        login: function(user){
            return $http
                .post('/api/login', { username: user.username, password: user.password })
                .then(function (result) {
                    return result.headers('X-AUTH-TOKEN');
                });
        }
    }
}