

import React, { useState } from "react";
import { ClinicRow } from "../components/index";
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
    <ClinicRow key={item.address+index}item={item} index={index} PAGEITEMNUM={PAGEITEMNUM} currentPage={currentPage}/>
  ));
  const paginationNum = Array.from(
    { length:5 },
    (v, i) => (i+currentPage)
  ); //page개수 만큼의 배열 생성 ex)0-5
  const paginationJSX = paginationNum.map((item,index) => {
    // let itemIndex = item;
    if(item-1 <= parseInt(dataList.length/PAGEITEMNUM )) {
      return (
        <li className="page-item" key={`pageLink${index}`}>
          <button className="page-link" onClick={() => setcurrentPage(item)}>
            {item}
          </button>
        </li>
      )
    }
   
   
    
  });
  // console.log(ClinicJson);
  return (
    <div id="main-wrapper">
      <div id="clinic-container">
        <div className="container">
          <h1>선별 진료소 리스트</h1>
          <nav aria-label="Page navigation example">
            <ul className="pagination">
              <li className="page-item">
                <button className="page-link" aria-label="Previous" onClick={() => {
                    if(currentPage>1) {
                    currentPage !=1 && setcurrentPage(currentPage-1)
                  }
                }
                 
                }>
                  <span aria-hidden="true">&laquo;</span>
                </button>
              </li>
              {paginationJSX}
              <li className="page-item">
                <button className="page-link"  aria-label="Next" onClick={() => {
                  if(currentPage!=parseInt(dataList.length/PAGEITEMNUM )+1) { 
                    setcurrentPage(currentPage+1)
                  }
                }}>
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
                <th scope="col"><div>시도</div></th>
                <th scope="col"><div>시군구</div></th>
                <th scope="col"><div>의료기관명</div></th>
                <th scope="col"><div>주소</div></th>
                <th scope="col"><div>평일 운영시간</div></th>
                <th scope="col"><div>토요일 운영시간</div></th>
                <th scope="col"><div>일요일 운영시간</div></th>
                <th scope="col"><div>전화번호</div></th>
                <th scope="col"><div>장애인 편의사항</div></th>

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
