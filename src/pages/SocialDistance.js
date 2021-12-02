import React from 'react'

import distance from '../assets/json/keepDistance_Daejon.json';



function SocialDistance() {
    return (
        <div id="main-wrapper">
      <div className="container">
        <h2>Daejeon SocialDistance Rule</h2>
        <table class = "socialDistance">
          <thead>
            <tr>
              <th colspan = "2">구분</th>
              <th>단계적 일상회복 1차 개편 주요 방역수칙</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td rowspan="2">공통 방역수칙</td>
              <td>마스크착용</td>
              <td></td>
            </tr>
            <tr>
              <td>기본방역수칙</td>
              <td></td>
            </tr>
            <tr>
              <td rowspan = "2">모임 / 행사 / 집회</td>
              <td>사적모임</td>
              <td></td>
            </tr>
            <tr>
              <td>기타 행사 / 모임</td>
              <td></td>
            </tr>
            <tr>
              <td rowspan = "2">접종증명 / 음성확인제 의무적용시설</td>
              <td>유흥시설</td>
              <td></td>
            </tr>
            <tr>
              <td>기타 행사 / 모임</td>
              <td></td>
            </tr>
            <tr>
              <td rowspan = "4">접종증명 / 음성확인제 미적용시설</td>
              <td>식당 / 카페</td>
              <td></td>
            </tr>
            <tr>
              <td>영화관 / 공연장</td>
              <td></td>
            </tr>
            <tr>
              <td>스포츠경기장</td>
              <td></td>
            </tr>
            <tr>
              <td>학원 등 독서실 / 스터디카페 / PC방</td>
              <td></td>
            </tr>
          </tbody>

        </table>
      </div>
    </div>
    )
}

export default SocialDistance;
