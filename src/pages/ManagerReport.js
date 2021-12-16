import React, { useEffect } from 'react'
import * as getDB from "../backend/GetDB.js"

function ManagerReport() {
    useEffect(() => {
    var listEl = document.getElementById('errorTable');
    getDB.getError(function(data){
        var tbody=document.createElement('tbody');
        var tr = document.createElement('tr');
        var td0 = document.createElement('td');
        var td1 = document.createElement('td');
        td0.setAttribute("scope", "col");
        td1.setAttribute("scope", "col");
        td0.appendChild(document.createTextNode(data.title));
        td1.appendChild(document.createTextNode(data.content));
        tr.appendChild(td0);
        tr.appendChild(td1);
        tbody.appendChild(tr);
        listEl.appendChild(tbody);
    });
    }, []);
    return (
          <div id="main-wrapper">
      <div className="container">

        <div>
            <h1>관리자오류 리포트</h1>

              <table className="table table-hover" id="errorTable">
            <thead>
              <tr>
            
                <th scope="col" style={{textAlign: "center"}}>제목 </th>
                <th scope="col" style={{textAlign: "center"}}>내용</th>
             
              </tr>
            </thead>
          </table>
        </div>
        </div>
        </div>
    )
}

export default ManagerReport
