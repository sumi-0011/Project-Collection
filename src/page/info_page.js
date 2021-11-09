import React from 'react'
import '../assets/test.css';
import { Link,Route } from "react-router-dom";
export default function info_page() {
  return (
    <div className="container infoPage-container">
    <div className="row border d-flex justify-content-around">
       <CategoryComponent name="플라스틱류" type="Plastic" />
       <CategoryComponent name="비닐 포장재 및 일회용 비닐 봉투"/>
       <CategoryComponent name="스티로폼"/>
       <CategoryComponent name="금속캔류"/>
       <CategoryComponent name="고철류"/>
       <CategoryComponent name="유리병류"/>
       <CategoryComponent name="폐형광등류"/>
       <CategoryComponent name="종이류"/>
       <CategoryComponent name="전자류"/>
    </div>
</div>
  )
}
function CategoryComponent({name,type}) {

  return(
    <div className="col-md-3" >
      <Link to={{
          pathname: `/info/${type}`,
            state: { 
                type : type,
            }
        }}>

        {name}
      </Link>
      
      <div></div>
    </div>
  )
}