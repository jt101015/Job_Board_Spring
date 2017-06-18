/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('jobapply', ['ngFileUpload']);


app.controller('myCtrl', function ($scope, Upload, $http, $window) {


    $scope.fileURL = undefined;
    $scope.fileName = undefined;

   
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




    $scope.uploadResume = function () {
        
        Upload.upload({
            url: '/seeker/resume',
            file: $scope.file
                    
        }).success(function (data, status, headers, config) {
            
            if (true === data.expired) {
                window.location.assign('/signIn');
            } else {
                console.log("!!!!!!!");
                console.log(data.fileURL);
                $scope.fileURL = data.fileURL;
                $scope.fileName = data.fileName;
            }
                    
            //$scope.uploadImg = data;
        }).error(function (data, status, headers, config) {

        });
        
    };
    
    $scope.submit = function (jobId) {
        
        var JSONData =
            { },
            url = '/seeker/apply/' + jobId;
        
        if (undefined !== $scope.fileName) { JSONData.fileName = $scope.fileName; }
        if (undefined !== $scope.fileURL) { JSONData.fileURL = $scope.fileURL; }
        console.log(JSONData);
        
        $http({
            method : "POST",
            url : url,
            data: JSONData
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                
                console.log(response);
                if ("Apply successfully." === response.msg) {
                    window.location.assign('/seeker');
                } else {
                    alert(response.msg);
                }

            }


        }).error(function (error) {


        });
        
    };


    //$scope.search();


});
