import React, { useState, useEffect } from "react";
import axios from 'axios';
import {dbService} from '../firebase';

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

export async function getDB(callback1, callback2, callback3, callback4, callback5) {
    // const ref1 = dbService.collection('ConfirmState').doc(getToday);
    const ref1 = dbService.collection('ConfirmState').doc('12.01');
    const doc1 = await ref1.get();
    // const ref2 = dbService.collection('ConfirmState').doc(getYesterday);
    const ref2 = dbService.collection('ConfirmState').doc('12.02');
    const doc2 = await ref2.get();
    // const ref3 = dbService.collection('ConfirmState').doc(getBeforeYesterday);
    const ref3 = dbService.collection('ConfirmState').doc('12.03');
    const doc3 = await ref3.get();
    if (!doc1.exists || !doc2.exists || !doc3.exists) {
      console.log('No document!');
    } else {
      callback1(doc1.data());
      callback2(doc2.data());
      callback3(doc3.data());
    }
}

const today = new Date();
let getToday = today.getMonth()+1 + "." + ("0"+today.getDate()).slice(-2);
const yesterday = new Date();
yesterday.setDate(today.getDate()-1);
let getYesterday = yesterday.getMonth()+1 + "." + ("0"+yesterday.getDate()).slice(-2);
const beforeYesterday = new Date();
beforeYesterday.setDate(today.getDate()-2);
let getBeforeYesterday = beforeYesterday.getMonth()+1 + "." + ("0"+beforeYesterday.getDate()).slice(-2);
