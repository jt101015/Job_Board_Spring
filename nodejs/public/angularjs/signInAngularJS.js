/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';
var app = angular.module('signIn', []);


app.controller('myCtrl', function ($scope, $http) {
    
    $scope.signInSubmit = function () {
         
        var dataJson =
            {
                "email" : $scope.email,
                "password" : $scope.password
            },
            url = '/' + $scope.role + '/signIn';
         
        $http({
			method : "POST",
			url : url,
			data : dataJson
        }).success(function (response) {
            console.log(response);
                 
            if (response.checked) {
                     
                if ("seeker" === $scope.role) {
                    window.location.assign('/seeker');

                }

                if ("company" === $scope.role) {
                    window.location.assign('/company');

                }

            } else {
                alert("The Name or Password is incorrect!");
            }
                 
               
        }).error(function (error) {


        });

        
    };
    
    
});
