/*jslint node: true */
'use strict';
var request = require('request'),
    serverURL = "http://localhost:8080",
    user = require('./user');

exports.companyView = function (req, res) {
    res.render('company');
};

exports.job = function (req, res) {
    
    console.log("post a job");
    var url,
        options;
    
    if ((null === req.session) || (undefined === req.session)) {
        console.log("session expire");
        res.send({expired: true});
        return;
    }
    
    
    
    url = serverURL + '/job/' + req.session.email;
    options = {url: url, method: "POST", json: true, body: req.body};
    console.log(options);
    
    
    request(options, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            res.send({});
        }
    });
};

exports.application = function (req, res) {
    
    var url = serverURL + req.url;
    console.log(url);
    
    request({url: url}, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            body = JSON.parse(body);
            console.log(body);
            res.render('companyApplication', {application: body.Application});
        }
    });
};

exports.jobDetail = function (req, res) {
    

    if ((null === req.session) || (undefined === req.session)) {
        res.render('signIn');
        return;
    }

    var url = serverURL + '/job/' + req.params.jobId,
        canApply = true,
        i = 0;


    request({url: url}, function (err, jobResponse, jobBody) {
        jobBody = JSON.parse(jobBody);
        console.log(jobBody);
        
            
        res.render('jobCompany', {job: jobBody.Job});

        //res.render('jobapply', {seeker: body});
    });
};

exports.updateJob = function (req, res) {
    
    var url,
        options,
        i = 0;
    console.log("find jobs by company");
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    url = serverURL + '/job/' + req.params.id;
    options = {url: url, method: "PUT", json: true, body: req.body};
    console.log(options);
    
    request(options, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            url = serverURL + '/emails/' + req.params.id;
            request({url: url}, function (emailErr, emailResponse, emailBody) {
        
                if (err) {
                    console.log(err);
                } else {
                    console.log(emailBody);
                    emailBody = JSON.parse(emailBody);
                    for (i = 0; i < emailBody.HashSet.length; i = i + 1) {
                        user.sendmail(emailBody.HashSet[i], "Job Update", "One of the job you apply has been updated, please sign in and have a check");
                    }
                    
                    res.send({});
                    

                }
            });
        }
    });
    
};

exports.download = function (req, res) {
    
    var filepath = __dirname + "/../public/" + req.params.url;
    res.download(filepath, req.params.name);
};

exports.status = function (req, res) {
    
    var url = serverURL + req.url,
        options = {url: url, method: "PUT"};
    
    console.log(url);
    
    request(options, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            body = JSON.parse(body);
            res.send(body.Response);
        }
    });
    
};

exports.applicationStatus = function (req, res) {
    
    var url = serverURL + req.url,
        options = {url: url, method: "PUT"};
    
    console.log(url);
    
    request(options, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            res.send({});
        }
    });
    
};

exports.jobs = function (req, res) {
    
    var url;
    console.log("find jobs by company");
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    url = serverURL + '/jobs/' + req.session.email;
    console.log(url);
    
    
    request({url: url}, function (err, response, body) {
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            body = JSON.parse(body);
            res.send(body.HashSet);
        }
    });
};