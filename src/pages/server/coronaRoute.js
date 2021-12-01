const request = require("request");
const cheerio = require("cheerio");
 
coronaRoute = {
  'distric': '',
  'placeType': '',
  'placeName': '',
  'location': '',
  'date':'',
  'ref':'' //소독여부
}

function getData() {
  request("nhttps://www.daejeon.go.kr/corona19/index.do?menuId=0008", function (err, res, body) {
      const $ = cheerio.load(body);
      const bodyList = $(".corona").map(function (i, element) {
          coronaRoute['distric'] = String($(element).find('td:nth-of-type(1)').text());
          coronaRoute['placeType'] =  String($(element).find('td:nth-of-type(2)').text());
          coronaRoute['placeName'] =  String($(element).find('td:nth-of-type(3)').text());
          coronaRoute['location'] =  String($(element).find('td:nth-of-type(4)').text());
          coronaRoute['date'] =  String($(element).find('td:nth-of-type(5)').text());
          coronaRoute['ref'] =  String($(element).find('td:nth-of-type(6)').text());

          console.log(coronaRoute)
      });s
  });
}

getData();
