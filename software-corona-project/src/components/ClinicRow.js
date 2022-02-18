import React from 'react'

function ClinicRow({item,index,PAGEITEMNUM,currentPage}) {
    return (
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
    )
}

export default ClinicRow
