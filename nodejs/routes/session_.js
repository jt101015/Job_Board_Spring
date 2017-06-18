/*jslint node: true */
'use strict';

exports.session = function (req, res) {
    
    if ((null === req.session) || (undefined === req.session)) {
        res.send({expired: true});
    } else {
        res.send({email: req.session.email});
    }
};