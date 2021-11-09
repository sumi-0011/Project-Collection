const path = require('path');
const model = require('./model');
let moment = require('moment');
require('moment-timezone');
moment.tz.setDefault("Asia/Seoul");

const now_date = moment().format('YYYY-MM-DD HH:mm:ss');

const AWS = require('aws-sdk');

AWS.config.loadFromPath(
  path.join(__dirname, 'config', 'awsConfig.json')
);

module.exports = {
  needs: () => upload,
  check: {
    id: (req, res) => {
      const id = req.body.id;

      model.check.id(id, result => {
        res.send(result);
      })
    }
  },
  signup: {
    user: (req, res) => {
      const name = req.body.name;
      const id = req.body.id;
      const pw = req.body.pw;

      model.signup.user(name, id, pw, now_date, result => {
        res.send(result);
      })
    }
  },
  login: {
    user: (req, res) => {
      const id = req.body.id;
      const pw = req.body.pw;

      model.login.user(id, pw, result => {
        if (result[0]) {
          res.send({ result: true, name: result[0].dataValues.name });
        }
        else {
          res.send(false);
        }
      })
    }
  },
  detect: {
    image: (req, res) => {
      const file_base64 = req.body.file_base64

      model.detect.image(file_base64, result => {
        res.send(result);
      })
    }
  },
  change:{
    pw: (req, res) => {
      const id = req.body.id;
      const pw = req.body.pw;

      model.change.pw(id, pw, result => {
        res.send(result)
      })
    }
  },
  get: {
    point: (req, res) => {
      const id = req.body.id;

      model.get.point(id, result => {
        res.send(result[0].dataValues);
      })
    }
  }
}