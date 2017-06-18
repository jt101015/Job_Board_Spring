/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('companyApplication', []);


app.controller('myCtrl', function ($scope, $http) {
    
    $scope.accept = function (id) {
        $http({
            method : "PUT",
            url : "/company/application/" + id + "/Offered"
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                window.location.reload();

            }


        }).error(function (error) {


        });
    };
    
    $scope.signOut = function () {
        var url = "/signOut";
        
        
        $http({
            method : "POST",
            url : url
        }).success(function (response) {


            window.location.assign('/signIn');


        }).error(function (error) {


        });
    };
    
    $scope.reject = function (id) {
        $http({
            method : "PUT",
            url : "/company/application/" + id + "/Rejected"
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                window.location.reload();

            }


        }).error(function (error) {


        });
    };
    
    $scope.back = function () {
        window.location.assign('/company');
    };

});
