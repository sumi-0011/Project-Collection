// import React from "react";

// function ConfirmeState() {
//   return (
//     <div id="main-wrapper">
//       <div className="container">
//         <h1>Welcome, ConfirmeState</h1>
//       </div>
//     </div>
//   );
// }

import React, { useState, useEffect } from "react";
import axios from 'axios';
import {dbService} from '../firebase';

function ConfirmeState() {
    
    const [datas, setDatas] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [confirmState,setConfirmState] = useState("");
    var output;

    //임시 submit, 추후 1시간 간격 작동 설정 필수

    const onSubmit = async (e) => {
      e.preventDefault();
      await dbService.collection("ConfirmState").add({
        accDefRate: datas.accDefRate,
        accExamCnt: datas.accExamCnt,
        accExamCompCnt: datas.accExamCompCnt,
        careCnt: datas.careCnt,
        clearCnt: datas.clearCnt,
        createDt: datas.createDt,
        deathCnt: datas.deathCnt,
        decideCnt: datas.decideCnt,
        examCnt: datas.examCnt
      });
      setConfirmState("");
  
    }
    const onChange = (event) => {
      const {
        target: { value },
      } = event;
      setConfirmState(value);
    };

    useEffect(() => {
        const fetchUsers = async () => {
        try {
            var url = '/openapi/service/rest/Covid19/getCovid19InfStateJson';
            var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + process.env.REACT_APP_InfState_API; /* Service Key*/
            queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /* */
            queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('10'); /* */
            queryParams += '&' + encodeURIComponent('startCreateDt') + '=' + encodeURIComponent('20200410'); /* */
            queryParams += '&' + encodeURIComponent('endCreateDt') + '=' + encodeURIComponent('20200410'); /* */
            // 요청이 시작 할 때에는 error 와 users 를 초기화하고
            setError(null);
            setDatas(null);
            // loading 상태를 true 로 바꿉니다.
            setLoading(true);
            const response = await axios.get(
            url+queryParams
            );
            setDatas(response.data.response.body.items.item); // 데이터는 response.data 안에 들어있습니다.
            


        } catch (e) {
            setError(e);
        }

        setLoading(false);
        };

        fetchUsers();
    }, []);
    if (loading) return <div>로딩중..</div>;
    if (error) {
      return <div>에러가 발생했습니다</div>;
    }
    if (!datas) return null;

    return (
      <div>
        {datas && <textarea rows={7} value={JSON.stringify(datas, null, 2)} readOnly={true} />}
        <form onSubmit={onSubmit}>
          <input type="submit" value="Update" />
        </form>
      </div>
    );
}

export default ConfirmeState;