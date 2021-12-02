import React, { useState, useEffect } from "react";
import * as ConfirmStateDB from "../backend/ConfirmStateAPI.js";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from "recharts";
import { ConfirmeState } from ".";
// import
import "../css/Main.css";
import { dbService } from "../firebase.js";

function Main() {
  return (
    <div>
      <Tempelate />
    </div>
  );
}
let DaylistTest = [
  {
    name: "Page A",
    DECIDE_CNT: 10,
  },
  {
    name: "Page B",
    DECIDE_CNT: 5,
  },
  {
    name: "Page C",
    DECIDE_CNT: 25,
  },
];
let listTest = [
  {
    name: "Page A",
    DECIDE_CNT: 10,
  },
  {
    name: "Page B",
    DECIDE_CNT: 15,
  },
  {
    name: "Page C",
    DECIDE_CNT: 40,
  },
];
const onSubmit = (e) => {
  e.preventDefault();
  ConfirmStateDB.ConfirmStateAPI();
};
function Tempelate() {
  //
  // DB에서 데이터 가져오는 부분!
  //
  const [datas, setDatas] = useState(null);
  const [ydatas, setYdatas] = useState(null);
  const [yydatas, setYYdatas] = useState(null);

  useEffect(() => {
    ConfirmStateDB.getDB(function(data){
      setDatas(data);
      DaylistTest[2].DECIDE_CNT = data.incDec;
      listTest[2].DECIDE_CNT = data.totalCnt;
    }, function(data2){
      setYdatas(data2);
      DaylistTest[1].DECIDE_CNT = data2.incDec;
      listTest[1].DECIDE_CNT = data2.totalCnt;
    }, function(data3){
      setYYdatas(data3);
      DaylistTest[0].DECIDE_CNT = data3.incDec;
      listTest[0].DECIDE_CNT = data3.totalCnt;
    });
  }, [])
  //
  //
  //
  if(!datas || !ydatas || !yydatas) return <h1>Loadings...</h1>
  return (
    <div id="page-wrapper">
      {/* <MainContainer /> */}
      <div className="ConfirmedState-container">
        <div className="setion-containter">
          <div className="sortation">
            <div className="CS__title">구분</div>
            <div className="CS__content">일일</div>
          </div>

          <div className="item">
            <div className="item__title">확진자수</div>
            <div className="item__content">{datas.incDec}</div>
          </div>
          <div className="item">
            <div className="item__title">치료중 환자수</div>
            <div className="item__content">{datas.isolCnt}</div>
          </div>
          <div className="item">
            <div className="item__title">사망자수</div>
            <div className="item__content">{datas.deathCnt - ydatas.deathCnt}</div>
          </div>
        </div>
        <div className="setion-containter">
          <div className="item">
            <div className="item__title">누적 확진자수</div>
            <div className="item__content">{datas.totalCnt}</div>
          </div>
          <div className="item">
            <div className="item__title">누적 사망자수</div>
            <div className="item__content">{datas.deathCnt}</div>
          </div>
        </div>
      </div>
      {/* 리액트 반응형 웹도 있는것 같지만 여기서는 쓰지 말고 익숙한 것으로 하자아~  */}

      {<MobileGraph list={DaylistTest} />}
      <div className="graph-container">
        <div className="graph-item">
          <div className="graph__title">일별 확진자 수</div>
          <div className="graph__content"> <Graph list={DaylistTest} /></div>
        </div>
        <div className="graph-item">
          <div className="graph__title">누적 확진자 수</div>
          <div className="graph__content"> <Graph list={listTest} /></div>
        </div>
      </div>
      <button
        onClick={onSubmit}>
          update
      </button>
    </div>
  );
}
function MobileGraph({ list }) {
  const [selectGraph, setSelectGraph] = useState(0);

  return (
    <div className="graph-container-mobile">
      <div className="graph__title-container">
        <div
          className={`graph__title ${selectGraph === 0 ? "current" : ""}`}
          onClick={() => setSelectGraph(0)}
        >
          일별
        </div>
        <div
          className={`graph__title ${selectGraph === 1 ? "current" : ""}`}
          onClick={() => setSelectGraph(1)}
        >
          누적
        </div>
      </div>
      <div className="graph__content">
        {
          selectGraph == 0? <Graph list={DaylistTest} /> : <Graph list={listTest} />
        }
        
      </div>
    </div>
  );
}
function Graph({ list }) {
  return (
    <LineChart
      width={350}
      height={300}
      data={list}
      margin={{
        top: 5,
        right: 30,
        left: 20,
        bottom: 5,
      }}
      className="line-chart"
    >
      <CartesianGrid strokeDasharray="3 3" />
      <XAxis dataKey="name" />
      <YAxis />
      <Tooltip />
      {/* <Legend /> */}
      <Line
        type="monotone"
        dataKey="DECIDE_CNT"
        stroke="#8884d8"
        // activeDot={{ r: 8 }}
      />
      {/* <Line type="monotone" dataKey="uv" stroke="#82ca9d" /> */}
    </LineChart>
  );
}
export default Main;
