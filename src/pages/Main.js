import React from "react";
// import
function Main() {
  return (
    <div>
      <Tempelate />
    </div>
  );
}

function Tempelate() {
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
    </div>
  );
}

export default Main;
