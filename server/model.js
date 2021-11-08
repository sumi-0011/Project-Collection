const sequelize = require('./models').sequelize;
const path = require('path');
const fs = require('fs');

const {
  User,
  Sequelize: { Op }
} = require('./models');
sequelize.query('SET NAMES utf8;');

const projectId = 'collathon';
const keyFilename = path.join(__dirname, 'config', 'ocr_key.json')

async function google_vision(file) {
  const vision = require('@google-cloud/vision');

  const client = new vision.ImageAnnotatorClient({ projectId, keyFilename });
  const request = {
    image: { content: file },
  };

  const [result] = await client.objectLocalization(request);
  const objects = result.localizedObjectAnnotations;
  const detect_result = [];
  objects.forEach(object => {
    detect_result.push(object.name);
  })

  return detect_result;
}

module.exports = {
  check: {
    id: (id, callback) => {
      User.count({
        where: { id: id }
      })
        .then(cnt => {
          if (cnt > 0) {
            callback(false);
          }
          else {
            callback(true);
          }
        })
        .catch(err => { throw err; })
    }
  },
  signup: {
    user: (name, id, pw, now_date, callback) => {
      User.count({
        where: { id: id }
      })
        .then(cnt => {
          if (cnt > 0) {
            callback(false);
          }
          else {
            User.create({
              name: name,
              id: id,
              pw: pw,
              signup_date: now_date,
            })
              .then(() => callback(true));
          }
        })
        .catch(err => { throw err; })
    }
  },
  login: {
    user: (id, pw, callback) => {
      User.findAll({
        where: { [Op.and]: [{ id: id, pw: pw }] }
      })
        .then(result => {
          callback(result);
        })
        .catch(err => { throw err; })
    }
  },
  detect: {
    image: (file_base64, callback) => {
      google_vision(file_base64).then(result => {
        callback(result);
      })
    }
  },
}