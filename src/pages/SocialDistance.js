import React from 'react'

import distance from '../assets/json/keepDistance.json';



function SocialDistance() {
  const DaejonList = distance.keepDistance.Daejon;
  
  // console.log(DaejonList.common[1].content.join('\n'));


    return (
        <div id="main-wrapper">
      <div className="container">
        <h2>Daejeon SocialDistance Rule</h2>
        <table className = "socialDistance">
          <thead>
            <tr>
              <th colSpan = "2">구분</th>
              <th>단계적 일상회복 1차 개편 주요 방역수칙</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td rowSpan = {DaejonList.common.length} >공통 방역수칙</td>
              <td>{ DaejonList.common[0].title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
              <td>{ DaejonList.common[0].content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>

            </tr>
            {
               DaejonList.common.map((item,index) => (
                 index >0 && <tr  key={index}>
                   <td>{ item.title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                   <td>{ item.content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                 </tr>
               ))
            }



            <tr>
              <td rowSpan ={DaejonList.meeting.length} >모임 / 행사 / 집회</td>
              <td>{ DaejonList.meeting[0].title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
              <td>{ DaejonList.meeting[0].content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
            </tr>
            {
               DaejonList.meeting.map((item,index) => (
                 index >0 && <tr  key={index}>
                   <td>{ item.title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                   <td>{ item.content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                 </tr>
               ))
            }

            <tr>
              <td rowSpan = {DaejonList.proofOfVaccinationApply.length} >접종증명 / <br/>음성확인제 의무적용시설</td>
              <td>{ DaejonList.proofOfVaccinationApply[0].title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
              <td>{ DaejonList.proofOfVaccinationApply[0].content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
            </tr>
            {
               DaejonList.proofOfVaccinationApply.map((item,index) => (
                 index >0 && <tr  key={index}>
                   <td>{ item.title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                   <td>{ item.content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                 </tr>
               ))
            }

            <tr>
              <td rowSpan = {DaejonList.proofOfVaccinationNotApply.length} >접종증명 / <br/> 음성확인제 미적용시설</td>
              <td>{ DaejonList.proofOfVaccinationNotApply[0].title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
              <td>{ DaejonList.proofOfVaccinationNotApply[0].content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
            </tr>
            {
               DaejonList.proofOfVaccinationNotApply.map((item,index) => (
                 index >0 && <tr  key={index}>
                   <td>{ item.title.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                   <td>{ item.content.map((ic,index) => (
                     <p key={index}>{ic}</p>
                   ))}</td>
                 </tr>
               ))
            }
          </tbody>

        </table>
        </div>
    </div>
    )
}
export default SocialDistance;