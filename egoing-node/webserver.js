const http = require("http");

const hostname = "127.0.0.1";
const port = 1337;

// http
//   .createServer((req, res) => {
//     res.writeHead(200, { "Content-Type": "text/plain" });
//     res.end("Hello World\n");
//   })
//   .listen(port, hostname, () => {
//     console.log(`Server running at http://${hostname}:${port}/`);
//   });

//server라는 객체를 통해 서버를 조작할수 있게 된다. 위에 있는 내용과 같은 의미
let server = http.createServer(function (req, res) {
  res.writeHead(200, { "Content-Type": "text/plain" });
  res.end("Hello World\n");
});
server.listen(port, hostname, function () {
  //server가 listeing에 성공했을때
  console.log(`Server running at http://${hostname}:${port}/`);
});
