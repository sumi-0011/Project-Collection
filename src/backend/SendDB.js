import {dbService} from '../firebase';
import axios from 'axios';
const cheerio = require("cheerio");

export async function ConfirmStateAPI() {
    var url = 'https://api.corona-19.kr/korea/beta/';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + process.env.REACT_APP_corona19_API; /* Service Key*/
    const response = await axios.get(
        url+queryParams
    );
    // setDatas(response.data.response.body.items.item); // 데이터는 response.data 안에 들어있습니다.
    
    await dbService.collection("ConfirmState").doc((response.data.API.updateTime).substring(23, 28)).set({
        deathCnt: response.data.korea.deathCnt,
        incDec: response.data.korea.incDec,
        incDecF: response.data.korea.incDecF,
        incDecK: response.data.korea.incDecK,
        isolCnt: response.data.korea.isolCnt,
        qurRate: response.data.korea.qurRate,
        recCnt: response.data.korea.recCnt,
        totalCnt: response.data.korea.totalCnt
    });   
}
export async function SendError(data) {
    await dbService.collection("Error").add({
        title: data.inputReportTitle,
        content: data.inputReportContent
    });
}
export async function SendConfirmRoute() {
    await dbService.collection("ConfirmRoute").get().then(val => {
        val.forEach((val) => {
            val.ref.delete();
        })
    })
    const getHtml = async () => {
        try {
          return await axios.get("/api/index.do?menuId=0008");
          // 해당 사이트 html 태그 가져오기
        } catch (error) {
          console.error(error);
        }
    };
    getHtml()
    .then((html) => {
      const $ = cheerio.load(html.data);

      const bodyList = $(".corona tbody tr").map(function (i, element) {
        if (String($(element).find('td:nth-of-type(2)').text()) !== "") {
            dbService.collection("ConfirmRoute").add({
                sigungu: String($(element).find('td:nth-of-type(1)').text()),
                type: String($(element).find('td:nth-of-type(2)').text()),
                name: String($(element).find('td:nth-of-type(3)').text()),
                address: String($(element).find('td:nth-of-type(4)').text()),
                clear: String($(element).find('td:nth-of-type(5)').text()),
                complete: String($(element).find('td:nth-of-type(6)').text())
            });
        }
      });
    });
}