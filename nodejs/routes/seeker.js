/*jslint node: true */
'use strict';

var randomstring = require("randomstring"),
    request = require('request'),
    serverURL = "http://localhost:8080",
    crypto = require('crypto'),
    formidable = require('formidable'),
    user = require('./user');

exports.markInterest = function (req, res) {
    var url = "/seeker/"+ req.session.email + "/markInterest/" + req.params.id;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    console.log(serverURL + url);

    request({url: serverURL + url, method:"PUT"}, function (err, response, body) {
        console.log(url);
        if (err) {
            console.log(err);
        } else {
            
            console.log(body);
            
            res.send(body);
        }
    });

};

exports.unmarkInterest = function (req, res) {
    var url = "/seeker/"+ req.session.email + "/unmarkInterest/" + req.params.id;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    console.log(serverURL + url);

    request({url: serverURL + url, method:"PUT"}, function (err, response, body) {
        console.log(url);
        if (err) {
            console.log(err);
        } else {
            
            console.log(body);
            
            res.send(body);
        }
    });

};

exports.interestJobs = function (req, res) {
    var url = req.url;
    console.log(serverURL + "/" + req.session.email + url);
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }

    console.log(serverURL + '/seeker/' + req.session.email + url);

    request({url: serverURL + '/seeker/' + req.session.email + url, method:"GET"}, function (err, response, body) {
        console.log(url);
        if (err) {
            console.log(err);
        } else {
            
            console.log(body);
            
            res.send(body);
        }
    });

};

exports.jobs = function (req, res) {
    var url = req.url;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    console.log(serverURL + url);

    request({url: serverURL + url}, function (err, response, body) {
        console.log(url);
        if (err) {
            console.log(err);
        } else {
            
            console.log(body);
            
            res.send(body);
        }
    });

};

exports.job = function (req, res) {
    console.log("seeker.js get jobapply");
    console.log(req.session.email);
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
        
        url = serverURL + '/seeker/' + req.session.email;
        request({url: url}, function (err, seekerResponse, seekerBody) {
            
            seekerBody = JSON.parse(seekerBody);
            console.log(seekerBody);
            
            res.render('jobapply', {job: jobBody.Job, seeker: seekerBody.Seeker});

        });
        //res.render('jobapply', {seeker: body});
    });
};


exports.uploadPicture = function (req, res) {
    
    var form = new formidable.IncomingForm(),
        filePath = __dirname + '/../public/';
        
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    console.log(req.session);
    form.encoding = 'utf-8';
    form.keepExtensions = true;
    form.parse(req);
    
    form.on('fileBegin', function (name, file) { file.path = filePath + req.session.email + ".png"; });
    
    form.on('end', function () {
        
        req.session.picture = '/' + req.session.email + ".png";
        res.send({picture: '/' + req.session.email + ".png"});
    });

};

exports.resume = function (req, res) {
    
    var form = new formidable.IncomingForm(),
        filePath = __dirname + '/../public/',
        fileId = crypto.randomBytes(16).toString('hex'),
        fileName;
        
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    console.log(req.session);
    form.encoding = 'utf-8';
    form.keepExtensions = true;
    form.parse(req);
    
    form.on('fileBegin', function (name, file) {
        fileName = file.name;
        file.path = filePath + fileId + ".pdf";
    });
    
    form.on('end', function () {
        res.send({fileURL: '/' + fileId + ".pdf", fileName: fileName});
    });
};



exports.apply = function (req, res) {
    
    var url,
        options,
        jobId = req.params.jobId;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    url = serverURL + '/seeker/' + req.session.email + '/apply/' + jobId;
    options = {url: url, method: "POST", json: true, body: req.body};
    console.log(options);
    
    request(options, function (err, response, body) {


        if (err) {
            console.log(err);
        } else {
            console.log(body);
            if ("Apply successfully." === body.Response.msg) {
                console.log("11111");
                user.sendmail(req.session.email, "Apply Successfully", "Thank you for applying the position, we will contact you if there is a match");
            }
            res.send(body.Response);
        }
    });
    
};






exports.applications = function (req, res) {
    
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    var url = serverURL + '/seeker/' + req.session.email + '/applications';
    
    console.log(url);
    
    request({url: url}, function (err, response, body) {
        console.log(body);
        res.send(body);
    });
};

exports.cancel = function (req, res) {
    
    console.log("get cancel request!!  " + req.url);
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    var url = serverURL + '/seeker/' + req.session.email + req.url.substring(7);
    
    console.log(url);
    
    request({url: url, method:"PUT"}, function (err, response, body) {
        console.log(body);
        if(!err) user.sendmail(req.session.email, "Cancel Successfully", "The applications are canceled!!");
        res.send(body);
    });
};

exports.reject = function (req, res) {
    
    console.log("get reject request!!  " + req.url);
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    var url = serverURL + '/seeker/' + req.session.email + req.url.substring(7);
    
    console.log(url);
    
    request({url: url, method:"PUT"}, function (err, response, body) {
        console.log(body);
        if(!err) user.sendmail(req.session.email, "Cancel Successfully", "The applications are rejected!!");
        res.send(body);
    });
};

exports.accept = function (req, res) {
    
    console.log("get reject request!!  " + req.url);
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    var url = serverURL + '/seeker/' + req.session.email + req.url.substring(7);
    
    console.log(url);
    
    request({url: url, method:"PUT"}, function (err, response, body) {
        console.log(body);
        if(!err) user.sendmail(req.session.email, "Accept Successfully", "The application is accepted!!");
        res.send(body);
    });
};


exports.seekerView = function (req, res) {
    res.render('seeker');
};
