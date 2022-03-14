const express = require("express");
const app = express();
const port = 3000;

app.use(express.static("public")); //public 이라는 폴더를 정적인 파일이 존재하는 폴더로 하겠다.

app.get("/", (req, res) => {
  res.send("Hello World!");
});
app.get("/route", (req, res) => {
  res.send("Hello Router! <img src='/peace.jpg'/>");
});
app.get("/dynamic", (req, res) => {
  let html = `<!DOCTYPE html>
  <html lang="en">
  
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
  </head>
  
  <body>
    Hello static
  </body>
  
  </html>`;
  res.send(html);
});

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
