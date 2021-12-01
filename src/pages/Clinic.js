import React, { useState } from "react";

import "../css/Clinic.css";

function Clinic() {
  // 객체 이름
  // 시도 : city
  // 시군구 : distric
  // 의료기관명 : clinicName
  // 주소 : address
  // 평일 운영시간 : operationHour
  // 토/일 운영시간 : operationHourSS
  // 전화번호 : phoneNumber
  // 장애인 편의사항 : ref
  const PAGEITEMNUM = 5;
  const [currentPage, setcurrentPage] = useState(1);
  const [dataList, setdataList] = useState(TestdataList);
  const mapDataList = dataList.map((item, index) => (
    <tr
      key={index}
      data-page={parseInt(index / PAGEITEMNUM) + 1}
      className={
        currentPage == parseInt(index / PAGEITEMNUM) + 1 ? "current" : ""
      }
    >
      <td scope="row">{index + 1}</td>
      <td>{item.city}</td>
      <td>{item.distric}</td>
      <td>{item.clinicName}</td>
      <td>{item.address}</td>
      <td>{item.operationHour}</td>
      <td>{item.operationHourSS}</td>
      <td>{item.phoneNumber}</td>
      <td>{item.ref}</td>
    </tr>
  ));
  const paginationNum = Array.from(
    { length: parseInt(dataList.length / PAGEITEMNUM) + 1 },
    (v, i) => i + 1
  ); //page개수 만큼의 배열 생성 ex)0-5
  const paginationJSX = paginationNum.map((item) => (
    <li className="page-item">
      <button className="page-link" onClick={() => setcurrentPage(item)}>
        {item}
      </button>
    </li>
  ));
  console.log(paginationNum);
  return (
    <div id="main-wrapper">
      <div id="clinic-container">
        <div className="container">
          <h1>선별 진료소 리스트</h1>
          <nav aria-label="Page navigation example">
            <ul className="pagination">
              <li className="page-item">
                <button className="page-link" aria-label="Previous" onClick={() => 
                  currentPage !=1 && setcurrentPage(currentPage-1)
                }>
                  <span aria-hidden="true">&laquo;</span>
                </button>
              </li>
              {paginationJSX}
              <li className="page-item">
                <button className="page-link"  aria-label="Next" onClick={() => (
                   currentPage !=paginationNum.length && setcurrentPage(currentPage+1)
                )}>
                  <span aria-hidden="true">&raquo;</span>
                </button>
              </li>
            </ul>
          </nav>
          <table className="table table-hover">
            <thead>
              <tr>
                <th scope="col" className="tableIndex">
                  #
                </th>
                <th scope="col">시도</th>
                <th scope="col">시군구</th>
                <th scope="col">의료기관명</th>
                <th scope="col">주소</th>
                <th scope="col">평일 운영시간</th>
                <th scope="col">토/일 운영시간</th>
                <th scope="col">전화번호</th>
                <th scope="col" className="tableRef">
                  장애인 <br />
                  편의사항
                </th>
              </tr>
            </thead>
            <tbody>{mapDataList}</tbody>
          </table>
      
        </div>
      </div>
    </div>
  );
}
const TestdataList = [
  {
    city: "대전",
    distric: "대덕구",
    clinicName: "대덕구보건소",
    address: "대전 대덕구 석봉로38번길55 (석봉동)",
    operationHour: "09:00~12:00,\n 13:00~16:00",
    operationHourSS: "09:00~12:00",
    phoneNumber: "042-608-4451~4455",
    ref: "예약가능,\n 장애인 우선검사 실시",
  },
  {
    city: "대전",
    distric: "대덕구",
    clinicName: "근로복지공단 대전병원",
    address: "대덕구 계족로 637",
    operationHour: "08:30~12:30",
    operationHourSS: "미운영",
    phoneNumber: "042-670-5119(응급실)",
    ref: "방문시 우선검사 시행",
  },
  {
    city: "대전",
    distric: "대덕구",
    clinicName: "대전보훈병원",
    address: "대덕구 대청로 82번길 147",
    operationHour: "08:30~17:00",
    operationHourSS: "미운영",
    phoneNumber: "042-939-0118,0100",
    ref: "장애인주차, 휠체어가능,\n 의사소통 도움안내책자",
  },
  {
    city: "대전",
    distric: "유성구",
    clinicName: "대전광역시 유성구보건소",
    address: "대전 유성구 노은동 270 월드컵보조경기장 P2주차장",
    operationHour: "09:30~11:30,\n14:00~20:00",
    operationHourSS: "09:30~11:30,\n12:00~16:00",
    phoneNumber: "042-611-5000",
    ref: "전자문진표 이용,\n 도움인력O",
  },
  {
    city: "대전",
    distric: "유성구",
    clinicName: "유성선병원",
    address: "북유성대로93",
    operationHour: "09:00~12:00,\n13:30~16:30",
    operationHourSS: "미운영",
    phoneNumber: "042-609-1120",
    ref: "",
  },
  {
    city: "대전",
    distric: "서구",
    clinicName: "대전광역시 서구보건소",
    address: "대전 서구 만년동 340번지",
    operationHour: "09:30~11:30,\n14:00~16:00",
    operationHourSS: "09:00~12:00 ",
    phoneNumber: "042-288-4520",
    ref: "",
  },
  {
    city: "대전",
    distric: "서구",
    clinicName: "대전 서구 만년동 340번지",
    address: "대전 서구 둔산서로 95",
    operationHour: "08:30~17:30",
    operationHourSS: "미운영",
    phoneNumber: "1899-0001",
    ref: "",
  },
];
export default Clinic;
