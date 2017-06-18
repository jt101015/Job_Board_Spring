/*jslint node: true */
'use strict';
var express = require('express'),
    path = require('path'),
    favicon = require('serve-favicon'),
    logger = require('morgan'),
    cookieParser = require('cookie-parser'),
    bodyParser = require('body-parser'),
    session = require('client-sessions'),
    index = require('./routes/index'),
    users = require('./routes/users'),
    seeker = require('./routes/seeker'),
    session_ = require('./routes/session_'),
    company = require('./routes/company'),
    http = require('http'),
    user = require('./routes/user');

var app = express();

// view engine setup
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', index);
app.use('/users', users);

app.use(session({
	cookieName: 'session',
	secret: 'cmpe275',
	duration: 30 * 60 * 1000,
	activeDuration: 5 * 60 * 1000
}));

app.get('/signIn', user.signInView);
app.get('/signUp', user.signUpView);
app.get('/seeker', seeker.seekerView);
app.get('/:role/account', user.account);
app.post('/:role/signIn', user.signIn);
app.post('/:role/signUp', user.signUp);
app.post('/:role/verify', user.verify);
app.get('/seeker/applications', seeker.applications);
app.put('/seeker/cancel', seeker.cancel);
app.put('/seeker/reject', seeker.reject);
app.put('/seeker/accept', seeker.accept);
app.post('/seeker/apply/:jobId', seeker.apply);
app.get('/jobs', seeker.jobs);
app.get('/job/:jobId', seeker.job);
app.get('/job/company/:jobId', company.jobDetail);
app.post('/seeker/uploadPicture', seeker.uploadPicture);
app.put('/:role', user.update);
app.post('/seeker/resume', seeker.resume);
app.get('/company', company.companyView);
app.post('/job', company.job);
app.get('/jobsByCompany', company.jobs);
app.put('/job/:id', company.updateJob);
app.post('/signOut', user.signOut);
app.get('/interestJobs', seeker.interestJobs);
app.put('/markInterest/:id', seeker.markInterest);
app.put('/unmarkInterest/:id', seeker.unmarkInterest);
app.put('/job/:id/filled', company.status);
app.put('/job/:id/cancel', company.status);
app.get('/company/application/:id', company.application);
app.get('/file/download/:url/:name', company.download);
app.put('/company/application/:id/:status', company.applicationStatus);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
    res.status(err.status || 500);
    res.render('error');
});

module.exports = app;

http.createServer(app).listen(app.get('port'), function () {
    console.log('Express server listening on port ' + app.get('port'));
});
