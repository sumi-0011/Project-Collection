import React from 'react'

function ManagerReport() {
    return (
          <div id="main-wrapper">
      <div className="container">

        <div>
            <h1>관리자오류 리포트</h1>

              <table className="table table-hover">
            <thead>
              <tr>
            
                <th scope="col">제목 </th>
                <th scope="col">내용</th>
             
              </tr>
            </thead>
            <tbody>
               <tr> 
               <td scope="col">title </td>
                <td scope="col">content</td>
                </tr>
            </tbody>
          </table>
        </div>
        </div>
        </div>
    )
}

export default ManagerReport
