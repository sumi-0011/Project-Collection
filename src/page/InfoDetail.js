import React from "react";
import { useLocation } from "react-router-dom";
import '../assets/test.css';
import recyleInfo from '../assets/recyleInfo.json';

function InfoDetail() {
  // const storeId = match.params;
  // link, location으로 값 받아오는게 잘 안되서 일단 path를 직접읽어서 하기로 했습니다.
    
  const list = {
      plastic : {
          type : 'plastic',
          info : [
            {
                recycleOk : "페트병, 요구르트병, 세제 통 등 플라스틱 용기류",
                recycleMethod : "내용물 다 비우고 다른 재질로 된 뚜껑, 부착 상표 등 제거 후 배출",
                precaution:false,
                imgUrl : ['plastic_containers.jpg']
            },
            {
                recycleOk :  "투명 페트병",
                recycleMethod : "내용물을 비운 후 겉비닐 라벨 제거. 뚜껑을 닫아 압착한 뒤 전용 수거함(투명 페트병 별도 분리배출함)에 넣기. (뚜껑 분리는 자유)",
                precaution:false,
                imgUrl : ['transparent_plastic_bottle.jpg']
            },
            {
                recycleOk : "빨대나 일회용 숟가락 등 크기가 작은 플라스틱",
                recycleMethod : "종량제 봉투에 담아서 버리기",
                precaution:false,
                imgUrl : ['straw.jpg']
            },
            {
                recycleOk : "주의 사항: OTHER로 표기된 플라스틱 (즉석밥 용기나 바나나맛 우유 용기 등)",
                recycleMethod :  "일반쓰레기로 배출해야 함",
                precaution:true,
                imgUrl : ['Instant_Rice_Container.jpg','Instant_Rice_Container2.jpg']
            }
          ]
      }
  }

  const pathName = useLocation().pathname.split("/")[2]; //pathname : info/plasfic
  var jsonEncode = JSON.stringify(list);


  console.log(recyleInfo);
  const listValues = Object.values(recyleInfo)
  const selectItem = listValues.filter((obj) => obj.type === 'plastic')[0].info;

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
