/*jslint node: true */
'use strict';

var randomstring = require("randomstring"),
    request = require('request'),
    serverURL = "http://localhost:8080",
    crypto = require('crypto'),
    formidable = require('formidable'),
    nodemailer = require('nodemailer');

var smtpConfig = {
    host: 'smtp.gmail.com',
    port: 465,
    secure: true, // use SSL
    auth: {
        user: 'gdtosilicon2016@gmail.com',
        pass: 'cmpe275jb'
    }
};

var transporter = nodemailer.createTransport(smtpConfig);

function sendmail(email, subject, text) {

  var mailOptions = {
      from: '"gdtosilicon2016@gmail.com', // sender address
      to: email, // list of receivers
      subject: subject, // Subject line
      text: text , // plain text body
      //html: '<b>Warning!!!</b>' // html body
  };
  transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
          return console.log(error);
      }
      console.log('Message %s sent: %s', info.messageId, info.response);
    });

};

exports.sendmail = sendmail;

exports.signInView = function (req, res) {
    res.render('signIn');
};


exports.signUpView = function (req, res) {
    res.render('signUp');
};

exports.signOut = function (req, res) {
    req.session.destroy();
    res.send({});
}



exports.signUp = function (req, res) {
    
    var code = randomstring.generate({length: 6, charset: 'numeric'}),
        url = serverURL + '/' + req.params.role + '/' + req.body.email;
    
    
    request({url: url}, function (err, response, body) {
        
        
        if (err) {
            console.log(err);
        } else {
            console.log(body);
            body = JSON.parse(body);
            console.log(body.Response);
            if (undefined === body.Response) {
            
                
                res.send({msg: "email fails"});
                
            } else {
                
                req.session.code = code;
                console.log(code);
                sendmail(req.body.email, "signUp pls verify fist!", "pls sign up with this code:" + code)
                req.session.email = req.body.email;
                req.session.role = req.body.role;
                res.send({});

                
            }
            
        }
    });
    
};

exports.signIn = function (req, res) {
    
    var url = serverURL + '/' + req.params.role + '/' + req.body.email,
        passwordMD5 = crypto.createHash('md5').update(req.body.password).digest("hex");
    

        
    request({url: url}, function (err, response, body) {


        if (err) {
            console.log(err);
        } else {
            body = JSON.parse(body);
            console.log(body);
            if (undefined !== body.Response) {
                res.send({checked: false});
                return;
            }
            if ("seeker" === req.params.role) {
                if ((body.Seeker.email === req.body.email) && (passwordMD5 === body.Seeker.password)) {
                
                    req.session.destroy();
                    req.session.email = req.body.email;
                    req.session.role = req.body.role;
                    res.send({checked: true});
                } else {
                    res.send({checked: false});
                }
            } else {
                if ((body.company.email === req.body.email) && (passwordMD5 === body.company.password)) {
                
                    req.session.destroy();
                    req.session.email = req.body.email;
                    req.session.role = req.body.role;
                    res.send({checked: true});
                } else {
                    res.send({checked: false});
                }
            }
            
        }
    });
    
    
};

exports.update = function (req, res) {
    
    var url,
        options;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    url = serverURL + '/' + req.params.role + '/' + req.session.email;
    options = {url: url, method: "PUT", json: true, body: req.body};
    
    console.log("update");
    console.log(options);
    
    request(options, function (err, response, body) {


        if (err) {
            console.log(err);
        } else {
            console.log(body);
            //body = JSON.parse(body);
            res.send(body);
        }
    });
    
};

exports.account = function (req, res) {
    var url;
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    url = serverURL + '/' + req.params.role + '/' + req.session.email;
    
    console.log("account account account");
    console.log(url);
    
    request({url: url}, function (err, response, body) {


        if (err) {
            console.log(err);
        } else {
            console.log(body);
            body = JSON.parse(body);
            res.send(body);
        }
    });
};

exports.verify = function (req, res) {
    
    var options,
        url = serverURL + '/' + req.params.role;
    
    console.log(req.body);
    console.log(req.session);
    
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
        return;
    }
    
    if ((req.session.code === req.body.code)
            && (req.session.email === req.body.email)
            && (req.session.role === req.body.role)) {
        
        req.body.password = crypto.createHash('md5').update(req.body.password).digest("hex");
        
        options = {url: url, method: "POST", json: true, body: req.body};
        
        request(options, function (err, response, body) {
        
        
            if (err) {
                console.log(err);
            } else {
                req.session.email = req.body.email;
                req.session.role = req.body.role;
                delete req.session.code;
                sendmail(req.session.email, "account verified", "your account is now verified, you can now sign in and use job board now!!");
                res.send({code: 200, msg: "verify success"});
            }
        });

        
    } else {
        res.send({code: 404, msg: "verify fail"});
    }
    
};
