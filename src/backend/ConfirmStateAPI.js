import React, { useState, useEffect } from "react";
import axios from 'axios';
import {dbService} from '../firebase';

export async function ConfirmStateAPI() {
    var url = '/openapi/service/rest/Covid19/getCovid19InfStateJson';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + process.env.REACT_APP_InfState_API; /* Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /* */
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('10'); /* */
    queryParams += '&' + encodeURIComponent('startCreateDt') + '=' + encodeURIComponent('20200410'); /* */
    queryParams += '&' + encodeURIComponent('endCreateDt') + '=' + encodeURIComponent('20200410'); /* */
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