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

const url = "https://www.daejeon.go.kr/corona19/index.do?menuId=0008";
request(url, (error, response, body) => {
  if (error) throw error;

  let $ = cheerio.load(body);

  try {

    $('table').find('tr').each(function (index, elem) {
      coronaRoute['distric'] = $(this).find('td').text().trim();
      coronaRoute['placeType'] =  $(this).find('td').next().text().trim();
      coronaRoute['placeName'] =  $(this).find('td').next().next().text().trim();
      coronaRoute['location'] =  $(this).find('td').next().next().next().text().trim();
      coronaRoute['date'] =  $(this).find('td').next().next().next().next().text().trim();
      coronaRoute['ref'] =  $(this).find('td').next().next().next().next().next().text().trim();
      console.log(coronaRoute)
    });
  } catch (error) {
    console.error(error);
  }
});
