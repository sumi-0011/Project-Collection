import React, { useState, useEffect } from "react";
import axios from 'axios';

function ConfirmeState() {
    const [datas, setDatas] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUsers = async () => {
        try {
            var url = '/openapi/service/rest/Covid19/getCovid19SidoInfStateJson';
            var queryParams = '?' + encodeURIComponent('serviceKey') + '=' +process.env.REACT_APP_InfState_API; /* Service Key*/
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
            setDatas(response.data); // 데이터는 response.data 안에 들어있습니다.
        } catch (e) {
            setError(e);
        }
        setLoading(false);
        };

        fetchUsers();
    }, []);

    if (loading) return <div>로딩중..</div>;
    if (error) return <div>에러가 발생했습니다</div>;
    if (!datas) return null;
    return (
        <ul>
          {datas.map(user => (
            <li key={user.id}>
              {user.gubun} ({user.deathCnt})
            </li>
          ))}
        </ul>
    );
}
export default ConfirmeState;