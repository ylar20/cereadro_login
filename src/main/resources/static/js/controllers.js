var app;
app = angular.module('cereadroApp', ['ngRoute'])
    .config(function ($routeProvider, $httpProvider) {
        $routeProvider.when('/', {
            templateUrl: 'home.html',
            controller: 'navigation'
        }).when('/login', {
            templateUrl: 'login.html',
            controller: 'navigation'
        }).when('/register', {
            templateUrl: 'register.html',
            controller: 'navigation'
        }).when('/upload', {
            templateUrl: 'upload.html',
            controller: 'navigation'
        }).otherwise('/');

        $httpProvider.interceptors.push('TokenAuthInterceptor');
    }).factory('TokenStorage', function () {

        var storageKey = 'auth_token';
        return {
            store: function (token) {
                return localStorage.setItem(storageKey, token);
            },
            retrieve: function () {
                return localStorage.getItem(storageKey);
            },
            clear: function () {
                return localStorage.removeItem(storageKey);
            }
        };
    }).factory('TokenAuthInterceptor', function ($q, TokenStorage) {
        return {
            request: function (config) {
                var authToken = TokenStorage.retrieve();
                if (authToken) {
                    config.headers['X-AUTH-TOKEN'] = authToken;
                }
                return config;
            },
            responseError: function (error) {
                if (error.status === 401 || error.status === 403) {
                    TokenStorage.clear();
                }
                return $q.reject(error);
            }
        };
    });

app.controller('navigation', function ($scope, $rootScope, $http, TokenStorage) {
	//$rootScope.authenticated = false;
	$scope.token;
    //$scope.registered = false;
	
	$scope.init = function () {
		$http.get('/api/users/current').success(function (user) {
			if(user.username !== 'anonymousUser'){
				$rootScope.authenticated = true;
                $rootScope.username = user.username;
				$scope.username = user.username;
				$scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
			}
		});
	};

	$scope.login = function () {
		$http
            .post('/api/login', { username: $scope.username, password: $scope.password })
            .success(function (result, status, headers) {
			    $rootScope.authenticated = true;
                $rootScope.username = $scope.username;
			    TokenStorage.store(headers('X-AUTH-TOKEN'));
			    $scope.token = JSON.parse(atob(TokenStorage.retrieve().split('.')[0]));
		    })
            .error(function() {
                $rootScope.authenticated = false;
                $rootScope.error = true;
            });
	};

    $scope.register = function () {
        $http
            .post('/api/users/register', {username: $scope.username, password:$scope.password,
                email:$scope.email, firstName:$scope.firstName, lastName:$scope.lastName})
        .success(function () {
            $rootScope.registered = true;
        })
        .error(function(result, status) {
            $rootScope.registered = false;
            $rootScope.error = true;
            $rootScope.errorMsg = { message: result, status: status};
            console.log($rootScope.errorMsg);
        });
    };

	$scope.logout = function () {
		TokenStorage.clear();	
		$rootScope.authenticated = false;
	};
});

app.directive('fileModel', [ '$parse', function($parse) {
    return {
        restrict : 'A',
        link : function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function() {
                scope.$apply(function() {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
} ]);

app.service('ArchiveService', [ '$http', '$rootScope', function($http, $rootScope) {
    this.search = function(name, date) {
        $http.get("/archive/files", {
            params : {
                person : name,
                date : date
            }
        }).success(function(response) {
            $rootScope.metadataList = response;
        }).error(function(response, status) {
            $rootScope.error = true;
            $rootScope.errorMsg = { message: response, status: status};
            console.log($rootScope.errorMsg.message);
        });
    }
}]);

app.service('fileUpload', ['$http','ArchiveService', '$rootScope', function($http, $rootScope, ArchiveService) {
    this.uploadFileToUrl = function(uploadUrl, file) {
        var fd = new FormData();
        fd.append('file', file);
        $http.post(uploadUrl, fd, {
            transformRequest : angular.identity,
            headers : {
                'Content-Type' : undefined
            }
        }).success(function() {
            ArchiveService.search(null, null);
        }).error(function(response, status) {
            $rootScope.error = true;
            $rootScope.errorMsg = { message: response, status: status};
            console.log($rootScope.errorMsg.message);
        });
    }
} ]);

app.controller('UploadCtrl', [ '$scope', 'fileUpload',
    function($scope, fileUpload) {
        $scope.uploadFile = function() {
            var file = $scope.myFile;
            console.log('file is ' + JSON.stringify(file));
            var uploadUrl = "/archive/upload";
            fileUpload.uploadFileToUrl(uploadUrl, file);
        };
    } ]);

app.controller('ArchiveCtrl', function($scope, $http) {
    $scope.search = function(name, date) {
        $http.get("/archive/files", {
            params : {
                person : name,
                date : date
            }
        }).success(function(response) {
            $scope.metadataList = response;
        });
    };
});

app.run(function($rootScope, $http) {
    $http.get("/archive/files").success(
        function(response) {
            $rootScope.metadataList = response;
        });
});

function sortByLabel(claims) {
    claims.sort(function(a, b) {
        var labelA = a.label.toLowerCase(), labelB = b.label.toLowerCase();
        if (labelA < labelB) // sort string ascending
            return -1;
        if (labelA > labelB)
            return 1;
        return 0; // default return value (no sorting)
    });
}