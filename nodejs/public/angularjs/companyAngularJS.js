/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('company', []);

app.controller('myCtrl', function ($scope, $http) {
    $scope.tab = 1;
    $scope.edit = false;
    $scope.jobs = undefined;
    $scope.joboptions = [{status:"All"}, {status:"Open"},{status:"Filled"},{status:"Cancelled"}];

    
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

    $scope.getAccount = function (showEdit) {
        
        var url = '/company/account';
        
        $scope.edit = showEdit;
        
        $scope.tab = 0;
        
        $http({
            method : "GET",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                console.log(response);
                $scope.Companyname = response.company.companyname;
                $scope.website = response.company.website;
                $scope.logo = response.company.logo;
                $scope.address = response.company.address;
                $scope.description = response.company.description;
            }


        }).error(function (error) {


        });

        
    };
    
    $scope.showJob = function (job) {
        console.log(job);
        window.location.assign('/job/company/' + job.id);
    };
    
    $scope.getJobs = function () {
        
        var url = '/jobsByCompany';
        
        $scope.tab = 1;

        $http({
            method : "GET",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                console.log(response);
                $scope.jobs = response;
                
            }


        }).error(function (error) {


        });
    };
    
    $scope.update = function () {
        var url = '/company',
            JSONData =
                {
                    companyname: $scope.Companyname,
                    website: $scope.website,
                    logo: $scope.logo,
                    address: $scope.address,
                    description: $scope.description
                 
                };


        $http({
            method : "PUT",
            url : url,
            data: JSONData
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                console.log(response);
                $scope.Companyname = response.company.companyname;
                $scope.website = response.company.website;
                $scope.logo = response.company.logo;
                $scope.address = response.company.address;
                $scope.description = response.company.description;
                $scope.edit = false;
            }


        }).error(function (error) {


        });
    };
    
    $scope.post = function () {
        $scope.tab = 2;
    };
    
    $scope.create = function () {
        var url = '/job',
            JSONData =
                {
                    title: $scope.title,
                    salary: $scope.salary,
                    responsibilities: $scope.responsibilities,
                    location: $scope.location,
                    description: $scope.jobDescription
                 
                };
        
        $http({
            method : "POST",
            url : url,
            data: JSONData
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                console.log(response);
                window.location.reload();

            }


        }).error(function (error) {


        });
    };
    
    $scope.getJobs();


});
