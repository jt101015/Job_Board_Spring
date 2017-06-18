/*global angular */
/*jslint node: true */
/*jslint devel: true */
'use strict';

var app = angular.module('seeker', ['ngFileUpload']);


app.controller('myCtrl', function ($scope, Upload, $http, $window) {
    $scope.tab = 1;
    $scope.jobs = undefined;
    $scope.seeker = undefined;
    $scope.email = undefined;
    $scope.applications = undefined;
    $scope.page = 1;
    $scope.pager = {};
    $scope.interestJobs = {};
    
    function GetPager(totalItems, currentPage, pageSize) {
        // default to first page
        currentPage = currentPage || 1;
        // default page size is 10
        pageSize = pageSize || 10;
        // calculate total pages
        var totalPages = Math.ceil(totalItems / pageSize),
            startPage,
            endPage;
        if (totalPages <= 10) {
            // less than 10 total pages so show all
            startPage = 1;
            endPage = totalPages;
        } else {
            // more than 10 total pages so calculate start and end pages
            if (currentPage <= 6) {
                startPage = 1;
                endPage = 10;
            } else if (currentPage + 4 >= totalPages) {
                startPage = totalPages - 9;
                endPage = totalPages;
            } else {
                startPage = currentPage - 5;
                endPage = currentPage + 4;
            }
        }

        // calculate start and end item indexes
        var startIndex = (currentPage - 1) * pageSize,
            endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

        // create an array of pages to ng-repeat in the pager control
        var pages = [];
        var i;
        for (i = startPage; i < endPage + 1; i = i + 1) { pages.push(i); }

        // return object with all pager properties required by the view
        return {
            totalItems: totalItems,
            currentPage: currentPage,
            pageSize: pageSize,
            totalPages: totalPages,
            startPage: startPage,
            endPage: endPage,
            startIndex: startIndex,
            endIndex: endIndex,
            pages: pages
        };
    }

    $scope.showJob = function (job) {
        console.log(job);
        $window.location.assign('/job/' + job.id);
    };
    
    function setPage(page) {
        console.log("got setPage!!!");
        if (page < 1 || page > $scope.pager.totalPages) {
            console.log("no Page!!!");
            return;
        }
        console.log($scope.jobs);
        // get pager object from service
        if ($scope.jobs !== undefined) {
            console.log($scope.jobs.length);
            $scope.pager = GetPager($scope.jobs.length, page, 10);
            console.log($scope.pager);
            // get current page of items
            $scope.pagejobs = $scope.jobs.slice($scope.pager.startIndex, $scope.pager.endIndex + 1);
            console.log($scope.pagejobs);
        }
    }


    function getSearchURL(keyWords, companyName, location, minSalary, maxSalary) {
        var url = "",
            i = 0;
        console.log(keyWords);
        console.log(companyName);
        console.log(location);
        console.log(minSalary);
        console.log(maxSalary);

        if (keyWords) {
            url = url + "?text=";
            for (i = 0; i < keyWords.length; i = i + 1) {
                if (i !== keyWords.length - 1) {
                    url = url + keyWords[i] + ",";
                } else {url = url + keyWords[i]; }
            }
        }

        if (companyName) {
            if (url !== "") {
                url = url + "&&companyName=";
            } else { url = url + "?companyName="; }
            for (i = 0; i < companyName.length; i = i + 1) {
                if (i !== companyName.length - 1) {
                    url = url + companyName[i] + ",";
                } else {url = url + companyName[i]; }
            }
        }

        if (location) {
            if (url !== "") {
                url = url + "&&location=";
            } else { url = url + "?location="; }
            for (i = 0; i < location.length; i = i + 1) {
                if (i !== location.length - 1) {
                    url = url + location[i] + ",";
                } else {url = url + location[i]; }
            }
        }

        if (minSalary) {
            if (url !== "") {
                url = url + "&&minSalary=" + minSalary;
            } else {
                url = url + "?minSalary=" + minSalary;
            }
        }

        if (maxSalary) {
            if (url !== "") {
                url = url + "&&maxSalary=" + maxSalary;
            } else {
                url = url + "?maxSalary=" + maxSalary;
            }
        }

        console.log(url);
        return "/jobs" + url;
    }
    
    $scope.search = function () {
         
        var keyWords,
            companyName,
            location,
            minSalary = $scope.minSalary,
            maxSalary = $scope.maxSalary,
            url;
        
        if ($scope.keyWords) { keyWords = ($scope.keyWords).split(" "); }
        if ($scope.companyName) { companyName = ($scope.companyName).split(" "); }
        if ($scope.location) { location = ($scope.location).split(" "); }
        console.log($scope.keyWords, keyWords);
        console.log($scope.location, location);
        console.log($scope.companyName, companyName);
        console.log($scope.minSalary, minSalary);
        console.log($scope.maxSalary, maxSalary);
        $scope.tab = 1;

        url = getSearchURL(keyWords, companyName, location, minSalary, maxSalary);
         
        $http({
			method : "GET",
			url : url
        }).success(function (response) {
            
            
                 
            if (response.expired) {
                     
                window.location.assign('/signIn');


            } else {
                console.log(response);
                $scope.jobs = response.HashSet;
                console.log($scope.jobs);
                getInterests();
                setPage(1);
            }
                 
               
        }).error(function (error) {

            
        });

        
    };
    
    $scope.interests = function () {
        console.log("change to interests!");
        $scope.tab = 3;
        getInterests();
    }

    function getInterests() {
        var url = '/interestJobs';

        $http({
            method : "GET",
            url : url
        }).success(function (response) {

            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                for(var k = 0; k < $scope.jobs.length; k++) {$scope.jobs[k].isFavorite = false;}
                $scope.interestjobs = response.PersistentSet;
                for(var m = 0; m < $scope.interestjobs.length; m++) {$scope.interestjobs[m].isFavorite = true;}
                for(var i = 0; i < $scope.interestjobs.length; i++){
                    for(var j = 0; j < $scope.jobs.length; j++){
                        if($scope.interestjobs[i].id===$scope.jobs[j].id) $scope.jobs[j].isFavorite = true;
                    }
                }
            }

        }).error(function (error) {


        });

    };

     $scope.cancel = function (j) {
        for (var i = 0; i < $scope.applications.length; i = i + 1) {
            if ($scope.applications[i].id == j.id){
                $scope.applications[i].checked=true;
            }
        }
        $scope.cancelSelected();
     }

     $scope.reject = function (j) {
        for (var i = 0; i < $scope.applications.length; i = i + 1) {
            if ($scope.applications[i].id == j.id){
                $scope.applications[i].checked=true;
            } 
        }
        $scope.rejectSelected();
     }

    $scope.cancelSelected = function () {
        var url = "";
        for (var i = 0; i < $scope.applications.length; i = i + 1) {
            if ($scope.applications[i].checked && ($scope.applications[i].status=="Pending" || $scope.applications[i].status=="Offered")){
                if(url=="") url = "?appList=" + $scope.applications[i].id;
                else url = url  + "," + $scope.applications[i].id;
            } 
        }
        if(url!=""){
            $http({
                method : "PUT",
                url : '/seeker/cancel'+url
            }).success(function (response) {
             $scope.getApplications();

            }).error(function (error) {

            });
        }
        
    };

    $scope.rejectSelected = function () {
        var url = "";
        for (var i = 0; i < $scope.applications.length; i = i + 1) {
            if ($scope.applications[i].checked &&  $scope.applications[i].status=="Offered" ){
                if(url=="") url = "?appList="  + $scope.applications[i].id;
                else url = url  + "," + $scope.applications[i].id;
            } 
        }
        if(url!=""){
            $http({
                method : "PUT",
                url : '/seeker/reject' + url
            }).success(function (response) {
             $scope.getApplications();

            }).error(function (error) {

            });
        }
        
    };

    $scope.accept = function (i) {
        var  url = "?appList="  + i.id;
        if(url!=""){
            $http({
                method : "PUT",
                url : '/seeker/accept' + url
            }).success(function (response) {
             $scope.getApplications();

            }).error(function (error) {

            });
        }
        
    };

    $scope.markAs = function (i) {
        var url = "";
        console.log(i.isFavorite);
        if(i.isFavorite) url = url+"/unmarkInterest/"+ i.id;
        else url = url+"/markInterest/"+ i.id;
        i.isFavorite = !i.isFavorite;
        
        $http({
            method : "PUT",
            url : url
        }).success(function (response) {
            if($scope.tab==1){getInterests();}

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
    
    
    $scope.update = function () {
        var url = '/seeker',
            JSONData =
                {
                    firstname: $scope.firstname,
                    lastname: $scope.lastname,
                    selfIntroduction: $scope.selfIntroduction,
                    workExperience: $scope.workExperience,
                    education: $scope.education,
                    skills: $scope.skills,
                    picture: $scope.picture
                 
                };


        $http({
            method : "PUT",
            url : url,
            data: JSONData
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                $scope.firstname = response.Seeker.firstname;
                $scope.lastname = response.Seeker.lastname;
                $scope.selfIntroduction = response.Seeker.selfIntroduction;
                $scope.workExperience = response.Seeker.workExperience;
                $scope.education = response.Seeker.education;
                $scope.skills = response.Seeker.skills;
                $scope.picture = response.Seeker.picture;
                $scope.edit = false;
            }


        }).error(function (error) {


        });
    };
    
    $scope.getApplications = function () {
        var url = '/seeker/applications';

        $scope.tab = 2;

        $http({
            method : "GET",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                console.log(response.PersistentSet);
                $scope.applications = response.PersistentSet;
            }


        }).error(function (error) {


        });

    };
    
    $scope.changePicture = function () { document.getElementById('picture').click(); };
    
    $scope.uploadPicture = function () {
        
        
        
        //$scope.fileInfo = files;
        Upload.upload({
            url: '/seeker/uploadPicture',
            file: $scope.file
                    
        }).success(function (data, status, headers, config) {
            
            if (true === data.expired) {
                window.location.assign('/signIn');
            } else {
                $scope.picture = data.picture;
            }

        }).error(function (data, status, headers, config) {

        });
        
    };
    
    $scope.getAccount = function (showEdit) {
        
        var url = '/seeker/account';
        
        $scope.edit = showEdit;
        
        $scope.tab = 0;
        
        $http({
            method : "GET",
            url : url
        }).success(function (response) {



            if (response.expired) {

                window.location.assign('/signIn');

            } else {
                $scope.firstname = response.Seeker.firstname;
                $scope.lastname = response.Seeker.lastname;
                $scope.selfIntroduction = response.Seeker.selfIntroduction;
                $scope.workExperience = response.Seeker.workExperience;
                $scope.education = response.Seeker.education;
                $scope.skills = response.Seeker.skills;
                $scope.picture = response.Seeker.picture;
            }


        }).error(function (error) {


        });

        
    };


    $scope.search();
    $scope.toPage = function (page) { setPage(page); };
    

    
	
	
	$scope.search();
});
