import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from "recharts";
// import
function Main() {
  return (
    <div>
      <Tempelate />
    </div>
  );
}

function Tempelate() {

  const list = [
    {
      name: "Page A",
      "DECIDE_CNT": 10,
      
    },
    {
      name: "Page B",
      "DECIDE_CNT": 5,
      
    },
    {
      name: "Page C",
      "DECIDE_CNT": 25,
      
    },
  ]
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
            <div className="item__content">11111</div>
          </div>
          <div className="item">
            <div className="item__title">치료중 환자수</div>
            <div className="item__content">11111</div>
          </div>
          <div className="item">
            <div className="item__title">사망자수</div>
            <div className="item__content">11111</div>
          </div>
        </div>
        <div className="setion-containter">
          <div className="item">
            <div className="item__title">누적 확진자수</div>
            <div className="item__content">11111</div>
          </div>
          <div className="item">
            <div className="item__title">누적 사망자수</div>
            <div className="item__content">11111</div>
          </div>
        </div>
      </div>
      <Graph list={list}/>
    </div>
  );
}

const data = [
  {
    name: "Page A",
    uv: 4000,
    pv: 2400,
    amt: 2400
  },
  {
    name: "Page B",
    uv: 3000,
    pv: 1398,
    amt: 2210
  },
  {
    name: "Page C",
    uv: 2000,
    pv: 9800,
    amt: 2290
  },
  
];

function Graph({list}) {
  return (
    <LineChart
      width={500}
      height={300}
      data={list}
      margin={{
        top: 5,
        right: 30,
        left: 20,
        bottom: 5
      }}
    >
      <CartesianGrid strokeDasharray="3 3" />
      <XAxis dataKey="name" />
      <YAxis />
      <Tooltip />
      <Legend />
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
