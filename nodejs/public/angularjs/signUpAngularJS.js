/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('signUp', []);


app.controller('myCtrl', function ($scope, $http) {
    
    $scope.showCode = false;
    $scope.showPassword = true;
    
    $scope.signUpSubmit = function () {
        
        if ($scope.password2 !== $scope.password) {
            alert("The two passwords are different!");
            return;
        }

        if (undefined === $scope.email) {
            alert("The email is invalid");
            return;
        }
        
        var dataJson =
            {
                "email" : $scope.email,
                "password" : $scope.password,
                "role": $scope.role
            },
            url = '/' + $scope.role;
        
        if ($scope.showCode) {
            
            dataJson.code = $scope.code;
            $http({
                method : "POST",
                url : url + '/verify',
                data : dataJson
		    }).success(function (response) {
                 
                if ("verify success" === response.msg) {
                     
                    if ("seeker" === $scope.role) {
                        window.location.assign('/seeker');
                    }
                     
                    if ("company" === $scope.role) {
                        window.location.assign('/company');
                    }
                     
                } else if ("verify fail" === response.msg) {
                    alert("Code Error");
                    
                } else if ("expired" === response.msg) {
                    alert("Code expires, Please check your email and input the code again.");
                }
                 
               
		    }).error(function (error) {

            
		    });
            
        } else {
            
            $http({
                method : "POST",
                url : url + '/signUp',
                data : dataJson
		    }).success(function (response) {
                 
                if ("email fails" === response.msg) {
                    alert("The email has already exist");
                } else {
                    
                    $scope.showCode = true;
                    $scope.showPassword = false;
                }
                 
               
		    }).error(function (error) {

            
		    });
            
        }
        

        
    };
    
    
});
