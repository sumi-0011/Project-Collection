const express = require('express');
const router = express.Router();
const controller = require('./controller');

router.post('/check/id', controller.check.id);
router.post('/signup/user', controller.signup.user);
router.post('/login/user', controller.login.user);
router.post('/detect/image', controller.detect.image);

module.exports = router;