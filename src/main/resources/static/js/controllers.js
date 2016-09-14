var app = angular.module('cereadroApp', ['ngRoute'])
    .config(function($routeProvider, $httpProvider) {
        $routeProvider.when('/', {
            templateUrl : 'home.html',
            controller : 'navigation'
        }).when('/login', {
            templateUrl : 'login.html',
            controller : 'navigation'
        }).when('/register', {
            templateUrl : 'register.html',
            controller : 'navigation'
        }).otherwise('/');

        $httpProvider.interceptors.push('TokenAuthInterceptor');
    }).factory('TokenStorage', function() {

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
}).factory('TokenAuthInterceptor', function($q, TokenStorage) {
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
});

app.controller('navigation', function ($scope, $http, TokenStorage) {
	$scope.authenticated = false;
	$scope.token;
    $scope.registered = false;
	
	$scope.init = function () {
		$http.get('/api/users/current').success(function (user) {
			if(user.username !== 'anonymousUser'){
				$scope.authenticated = true;
				$scope.username = user.username;
				$scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
                /*var rootEle = document.querySelector("html");
                var ele = angular.element(rootEle);
                var scope = ele.scope();
                debugger;*/
			}
		});
	};

	$scope.login = function () {
		$http
            .post('/api/login', { username: $scope.username, password: $scope.password })
            .success(function (result, status, headers) {
			    $scope.authenticated = true;
			    TokenStorage.store(headers('X-AUTH-TOKEN'));
			    $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
		    })
            .error(function() {
                $scope.authenticated = false;
                $scope.error = true;
            });
	};

    $scope.register = function () {
        $http
            .post('/api/users/register', {username: $scope.username, password:$scope.password,
                email:$scope.email, firstname:$scope.firstname, lastname:$scope.lastname})
        .success(function () {
            $scope.registered = true;
            debugger;
        })
        .error(function(result, status) {
            $scope.registered = false;
            $scope.error = true;
            $scope.errorMsg = { message: result, status: status};
            console.log($scope.errorMsg);
            debugger;
        });
    };

	$scope.logout = function () {
		TokenStorage.clear();	
		$scope.authenticated = false;
	};
});
app.controller('home', function($scope, $http) {

});