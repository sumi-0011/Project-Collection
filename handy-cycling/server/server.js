const express = require('express');
const app = express();
const fs = require('fs');
const router = require('./route');
const cors = require('cors');

const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');

const sequelize = require('./models').sequelize;
sequelize.sync();

app.use(express.json({ limit: 10000000}));
app.use(cookieParser());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json({ limit: 10000000 }));
app.use(cors());

app.use('/', router);

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log(`Server On : http://localhost:${PORT}/`);
})