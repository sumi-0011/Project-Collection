import React, { useState, useEffect } from "react";
import axios from 'axios';
import {dbService} from '../firebase';

/*특기사항:
시간이 늦으면 아직 오늘자 확진자가 나오지 않았을 수 있고, 이는 데이터 update가 안되는 현상 발생
그래서 기준을 하루전으로 잡음, 추후 업데이트 시간이 정해져 있으면 그 시간 이후 오늘자 확진자를 가져오는 방식
구현할수도 있고~ 안할수도 있고~ 아몰랑 잘거야.*/
// 오구오구 잘했당
// 업데이트는 따로 안해도 ㄱㅊ을거같애 데모니까 뭐,,? 그치 않을까
export async function ConfirmStateAPI() {
    let today = new Date();
    today.setDate(today.getDate()-1);
    let year = today.getFullYear();
    let month = today.getMonth()+1;
    let date = today.getDate();
    var url = '/openapi/service/rest/Covid19/getCovid19InfStateJson';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + process.env.REACT_APP_InfState_API; /* Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /* */
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('10'); /* */
    queryParams += '&' + encodeURIComponent('startCreateDt') + '=' + encodeURIComponent(`${year}${month}${date}`); /* */
    queryParams += '&' + encodeURIComponent('endCreateDt') + '=' + encodeURIComponent(`${year}${month}${date}`); /* */
    const response = await axios.get(
        url+queryParams
    );
    // setDatas(response.data.response.body.items.item); // 데이터는 response.data 안에 들어있습니다.
    
    await dbService.collection("ConfirmState").add({
        accDefRate: response.data.response.body.items.item.accDefRate,
        accExamCnt: response.data.response.body.items.item.accExamCnt,
        accExamCompCnt: response.data.response.body.items.item.accExamCompCnt,
        careCnt: response.data.response.body.items.item.careCnt,
        clearCnt: response.data.response.body.items.item.clearCnt,
        createDt: response.data.response.body.items.item.createDt,
        deathCnt: response.data.response.body.items.item.deathCnt,
        decideCnt: response.data.response.body.items.item.decideCnt,
        examCnt: response.data.response.body.items.item.examCnt
    });   
}