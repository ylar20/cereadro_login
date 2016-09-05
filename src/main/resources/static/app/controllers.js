var app = angular.module('cereadroApp', ['ngRoute'])
    .factory('TokenStorage', tokenStorage)
    .factory('TokenAuthInterceptor', tokenAuthInterceptor)
    .config(function($routeProvider, $httpProvider) {
        $routeProvider.when('/home', {
            templateUrl : '/app/home/home.html',
            controller : 'homeController'
        }).otherwise({
            template : "<h1>None</h1><p>Nothing has been selected,</p>"
        });

        $httpProvider.interceptors.push('TokenAuthInterceptor');
    });

app.controller('navigation', controller_navigation);
app.controller('AuthCtrl', controller_auth);
app.controller('homeController', controller_home);

//
// Definitions follow
//

function tokenStorage() {
    var storageKey = 'auth_token';
    return {
        store : function(token) {
            return localStorage.setItem(storageKey, token);
        },
        retrieve : function() {
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
            var authToken = TokenStorage.retrieve();
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
function controller_navigation($scope, $http, TokenStorage) {
    $scope.authenticated = false;
    $scope.token = null;

    $scope.init = function () {
        $http.get('/api/users/current').success(function (user) {
            if(user.username !== 'anonymousUser'){
                $scope.authenticated = true;
                $scope.username = user.username;
                $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
            }
        });
    };

    $scope.login = function () {
        $http
            .post('/api/login', { username: $scope.username, password: $scope.password })
            .then(function (result, status, headers) {
                    $scope.authenticated = true;
                    TokenStorage.store(headers('X-AUTH-TOKEN'));
                    $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
                }
                ,function() {
                    $rootScope.authenticated = false;
                    $scope.error = true;
                }
            );
    };

    $scope.logout = function () {
        TokenStorage.clear();
        $scope.authenticated = false;
    };
}
function controller_auth($scope, $http, TokenStorage) {
    $scope.authenticated = false;
    $scope.token; // For display purposes only

    $scope.init = function () {
        $http.get('/api/users/current').success(function (user) {
            if (user.username !== 'anonymousUser') {
                $scope.authenticated = true;
                $scope.username = user.username;

                // For display purposes only
                $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
            }
        });
    };

    $scope.login = function () {
        $http.post('/api/login', {
            username: $scope.username,
            password: $scope.password
        }).success(function (result, status, headers) {
            $scope.authenticated = true;
            TokenStorage.store(headers('X-AUTH-TOKEN'));

            // For display purposes only
            $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
        });
    };

    $scope.logout = function () {
        // Just clear the local storage
        TokenStorage.clear();
        $scope.authenticated = false;
    };
}