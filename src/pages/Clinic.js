

import React, { useState } from "react";

import "../css/Clinic.css";
import ClinicJson from '../assets/json/Clinic.json';
function Clinic() {
  // 객체 이름
  // 시도 : city
  // 시군구 : distric
  // 의료기관명 : clinicName
  // 주소 : address
  // 평일 운영시간 : operationHour
  // 토 운영시간 : operationHourSat
  // 일 운영시간 : operationHourSun
  // 전화번호 : phoneNumber
  // 장애인 편의사항 : ref
  const PAGEITEMNUM = 5;
  const [currentPage, setcurrentPage] = useState(1);
  const [dataList, setdataList] = useState(ClinicJson);
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
      <td>{item.operationHourSat}</td>
      <td>{item.operationHourSun}</td>
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
  console.log(ClinicJson);
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
                <th scope="col">토요일 운영시간</th>
                <th scope="col">일요일 운영시간</th>
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
export default Clinic;
