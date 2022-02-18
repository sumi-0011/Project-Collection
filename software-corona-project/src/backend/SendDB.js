import {dbService} from '../firebase';
import axios from 'axios';
const cheerio = require("cheerio");

const today = new Date();
let getToday = today.getMonth()+1 + "." + ("0"+today.getDate()).slice(-2);
const yesterday = new Date();
yesterday.setDate(today.getDate()-1);
let getYesterday = yesterday.getMonth()+1 + "." + ("0"+yesterday.getDate()).slice(-2);

export async function ConfirmStateAPI() {
    var url = 'https://api.corona-19.kr/korea/country/new/';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + process.env.REACT_APP_corona19_API; /* Service Key*/
    const response = await axios.get(
        url+queryParams
    );
    // setDatas(response.data.response.body.items.item); // 데이터는 response.data 안에 들어있습니다.
    await dbService.collection("ConfirmState").doc(getToday).set({
        deathCnt: response.data.korea.death,
        incDec: response.data.korea.newCase,
        incDecF: response.data.korea.newFcase,
        incDecK: response.data.korea.newCcase,
        isolCnt: response.data.korea.recovered,
        qurRate: response.data.korea.percentage,
        recCnt: response.data.korea.recovered,
        totalCnt: response.data.korea.totalCase
    });
    await dbService.collection("SeoulState").doc(getToday).set({
        deathCnt: response.data.seoul.death,
        incDec: response.data.seoul.newCase,
        incDecF: response.data.seoul.newFcase,
        incDecK: response.data.seoul.newCcase,
        isolCnt: response.data.seoul.recovered,
        qurRate: response.data.seoul.percentage,
        recCnt: response.data.seoul.recovered,
        totalCnt: response.data.seoul.totalCase
    });
    await dbService.collection("BusanState").doc(getToday).set({
        deathCnt: response.data.busan.death,
        incDec: response.data.busan.newCase,
        incDecF: response.data.busan.newFcase,
        incDecK: response.data.busan.newCcase,
        isolCnt: response.data.busan.recovered,
        qurRate: response.data.busan.percentage,
        recCnt: response.data.busan.recovered,
        totalCnt: response.data.busan.totalCase
    });
    await dbService.collection("DaeguState").doc(getToday).set({
        deathCnt: response.data.daegu.death,
        incDec: response.data.daegu.newCase,
        incDecF: response.data.daegu.newFcase,
        incDecK: response.data.daegu.newCcase,
        isolCnt: response.data.daegu.recovered,
        qurRate: response.data.daegu.percentage,
        recCnt: response.data.daegu.recovered,
        totalCnt: response.data.daegu.totalCase
    });
    await dbService.collection("IncheonState").doc(getToday).set({
        deathCnt: response.data.incheon.death,
        incDec: response.data.incheon.newCase,
        incDecF: response.data.incheon.newFcase,
        incDecK: response.data.incheon.newCcase,
        isolCnt: response.data.incheon.recovered,
        qurRate: response.data.incheon.percentage,
        recCnt: response.data.incheon.recovered,
        totalCnt: response.data.incheon.totalCase
    });
    await dbService.collection("GwangjuState").doc(getToday).set({
        deathCnt: response.data.gwangju.death,
        incDec: response.data.gwangju.newCase,
        incDecF: response.data.gwangju.newFcase,
        incDecK: response.data.gwangju.newCcase,
        isolCnt: response.data.gwangju.recovered,
        qurRate: response.data.gwangju.percentage,
        recCnt: response.data.gwangju.recovered,
        totalCnt: response.data.gwangju.totalCase
    });
    await dbService.collection("DaejeonState").doc(getToday).set({
        deathCnt: response.data.daejeon.death,
        incDec: response.data.daejeon.newCase,
        incDecF: response.data.daejeon.newFcase,
        incDecK: response.data.daejeon.newCcase,
        isolCnt: response.data.daejeon.recovered,
        qurRate: response.data.daejeon.percentage,
        recCnt: response.data.daejeon.recovered,
        totalCnt: response.data.daejeon.totalCase
    });
    await dbService.collection("UlsanState").doc(getToday).set({
        deathCnt: response.data.ulsan.death,
        incDec: response.data.ulsan.newCase,
        incDecF: response.data.ulsan.newFcase,
        incDecK: response.data.ulsan.newCcase,
        isolCnt: response.data.ulsan.recovered,
        qurRate: response.data.ulsan.percentage,
        recCnt: response.data.ulsan.recovered,
        totalCnt: response.data.ulsan.totalCase
    });
    await dbService.collection("SejongState").doc(getToday).set({
        deathCnt: response.data.sejong.death,
        incDec: response.data.sejong.newCase,
        incDecF: response.data.sejong.newFcase,
        incDecK: response.data.sejong.newCcase,
        isolCnt: response.data.sejong.recovered,
        qurRate: response.data.sejong.percentage,
        recCnt: response.data.sejong.recovered,
        totalCnt: response.data.sejong.totalCase
    });
    await dbService.collection("GyeonggiState").doc(getToday).set({
        deathCnt: response.data.gyeonggi.death,
        incDec: response.data.gyeonggi.newCase,
        incDecF: response.data.gyeonggi.newFcase,
        incDecK: response.data.gyeonggi.newCcase,
        isolCnt: response.data.gyeonggi.recovered,
        qurRate: response.data.gyeonggi.percentage,
        recCnt: response.data.gyeonggi.recovered,
        totalCnt: response.data.gyeonggi.totalCase
    });
    await dbService.collection("GangwonState").doc(getToday).set({
        deathCnt: response.data.gangwon.death,
        incDec: response.data.gangwon.newCase,
        incDecF: response.data.gangwon.newFcase,
        incDecK: response.data.gangwon.newCcase,
        isolCnt: response.data.gangwon.recovered,
        qurRate: response.data.gangwon.percentage,
        recCnt: response.data.gangwon.recovered,
        totalCnt: response.data.gangwon.totalCase
    });
    await dbService.collection("ChungbukState").doc(getToday).set({
        deathCnt: response.data.chungbuk.death,
        incDec: response.data.chungbuk.newCase,
        incDecF: response.data.chungbuk.newFcase,
        incDecK: response.data.chungbuk.newCcase,
        isolCnt: response.data.chungbuk.recovered,
        qurRate: response.data.chungbuk.percentage,
        recCnt: response.data.chungbuk.recovered,
        totalCnt: response.data.chungbuk.totalCase
    });
    await dbService.collection("ChungnamState").doc(getToday).set({
        deathCnt: response.data.chungnam.death,
        incDec: response.data.chungnam.newCase,
        incDecF: response.data.chungnam.newFcase,
        incDecK: response.data.chungnam.newCcase,
        isolCnt: response.data.chungnam.recovered,
        qurRate: response.data.chungnam.percentage,
        recCnt: response.data.chungnam.recovered,
        totalCnt: response.data.chungnam.totalCase
    });
    await dbService.collection("JeonbukState").doc(getToday).set({
        deathCnt: response.data.jeonbuk.death,
        incDec: response.data.jeonbuk.newCase,
        incDecF: response.data.jeonbuk.newFcase,
        incDecK: response.data.jeonbuk.newCcase,
        isolCnt: response.data.jeonbuk.recovered,
        qurRate: response.data.jeonbuk.percentage,
        recCnt: response.data.jeonbuk.recovered,
        totalCnt: response.data.jeonbuk.totalCase
    });
    await dbService.collection("JeonnamState").doc(getToday).set({
        deathCnt: response.data.jeonnam.death,
        incDec: response.data.jeonnam.newCase,
        incDecF: response.data.jeonnam.newFcase,
        incDecK: response.data.jeonnam.newCcase,
        isolCnt: response.data.jeonnam.recovered,
        qurRate: response.data.jeonnam.percentage,
        recCnt: response.data.jeonnam.recovered,
        totalCnt: response.data.jeonnam.totalCase
    });
    await dbService.collection("GyeongbukState").doc(getToday).set({
        deathCnt: response.data.gyeongbuk.death,
        incDec: response.data.gyeongbuk.newCase,
        incDecF: response.data.gyeongbuk.newFcase,
        incDecK: response.data.gyeongbuk.newCcase,
        isolCnt: response.data.gyeongbuk.recovered,
        qurRate: response.data.gyeongbuk.percentage,
        recCnt: response.data.gyeongbuk.recovered,
        totalCnt: response.data.gyeongbuk.totalCase
    });
    await dbService.collection("GyeongnamState").doc(getToday).set({
        deathCnt: response.data.gyeongnam.death,
        incDec: response.data.gyeongnam.newCase,
        incDecF: response.data.gyeongnam.newFcase,
        incDecK: response.data.gyeongnam.newCcase,
        isolCnt: response.data.gyeongnam.recovered,
        qurRate: response.data.gyeongnam.percentage,
        recCnt: response.data.gyeongnam.recovered,
        totalCnt: response.data.gyeongnam.totalCase
    });
    await dbService.collection("jejuState").doc(getToday).set({
        deathCnt: response.data.jeju.death,
        incDec: response.data.jeju.newCase,
        incDecF: response.data.jeju.newFcase,
        incDecK: response.data.jeju.newCcase,
        isolCnt: response.data.jeju.recovered,
        qurRate: response.data.jeju.percentage,
        recCnt: response.data.jeju.recovered,
        totalCnt: response.data.jeju.totalCase
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