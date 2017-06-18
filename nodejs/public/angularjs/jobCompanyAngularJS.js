/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('jobCompany', []);


app.controller('myCtrl', function ($scope, $http) {
    
    $scope.edit = false;

    $scope.back = function () {
        window.location.assign('/company');
    };
    
    $scope.edit = function (title, description, responsibilities, location, salary) {
        
        $scope.title = title;
        $scope.description = description;
        $scope.responsibilities = responsibilities;
        $scope.location = location;
        $scope.salary = parseInt(salary, 10);
        $scope.edit = true;
        
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
    
    
    $scope.filled = function (id) {
        
        var url = "/job/" + id + "/filled";
        
        $http({
            method : "PUT",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                if ("Update successfully." === response.msg) {
                    window.location.reload();
                    $scope.edit = false;
                } else {
                    alert(response.msg);
                }
            }


        }).error(function (error) {


        });
        
        
    };
    
    $scope.cancel = function (id) {
        
        var url = "/job/" + id + "/cancel";
        
        $http({
            method : "PUT",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                
                if ("Update successfully." === response.msg) {
                    window.location.reload();
                    $scope.edit = false;
                } else {
                    alert(response.msg);
                }
                
            }


        }).error(function (error) {


        });
        
        
    };
    
    $scope.getApplication = function (id) {
        window.location.assign('/company/application/' + id);
    };
    
    
    $scope.update = function (id) {
        
        var url = "/job/" + id,
            JSONData =
                {
                    title: $scope.title,
                    description: $scope.description,
                    responsibilities: $scope.responsibilities,
                    location: $scope.location,
                    salary: $scope.salary
                 
                };
        
        $http({
            method : "PUT",
            url : url,
            data: JSONData
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                window.location.reload();
                $scope.edit = false;
            }


        }).error(function (error) {


        });
        
    };
    
    

    //$scope.search();


});
