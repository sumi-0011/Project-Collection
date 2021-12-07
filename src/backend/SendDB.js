import {dbService} from '../firebase';
import axios from 'axios';

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