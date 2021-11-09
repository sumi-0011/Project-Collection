import React from "react";
import { useLocation } from "react-router-dom";
import '../assets/test.css';
import recyleInfo from '../assets/recyleInfo.json';

function InfoDetail() {
  // const storeId = match.params;
  // link, location으로 값 받아오는게 잘 안되서 일단 path를 직접읽어서 하기로 했습니다.
 

  const pathName = useLocation().pathname.split("/")[2]; //pathname : info/plasfic
//   var jsonEncode = JSON.stringify(list);/
//   console.log(jsonEncode);

//   console.log(recyleInfo);
  const listValues = Object.values(recyleInfo)
  const selectItem = listValues.filter((obj) => obj.type === pathName )[0].info;

  const selectItemJSX = selectItem.map((item,index) => (<div key={`item${index}`} className="recyle-item container">
        <div className={`col-md-${11-item.imgUrl.length *2}`}>
        <h3 className='recycleOk'>{item.recycleOk}</h3>
        <ul>
          <li>
          <p className="recycleMethod">{item.recycleMethod}</p>
          </li>
      </ul>
        </div>
        {(item.imgUrl).map((url,index) => (
             <div className={`col-md-2 `} key={`itemImg${index}`} >
                <img src={require(`../assets/img/${url}`).default} alt={`img${index}`} />
            
             </div>
              
            ))}
  </div>))
  return (
    
    <div className="recyle-detail-container ">
      <h1>{pathName}</h1>
      {selectItemJSX}
    </div>
  );
}
export default InfoDetail;
