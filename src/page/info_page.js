import React from 'react'
import '../assets/test.css';
import { Link } from "react-router-dom";
export default function info_page() {
  return (
    <div className="container infoPage-container">
    <div className="row border d-flex justify-content-around">
       <CategoryComponent name="플라스틱류" type="plastic" />
       <CategoryComponent name="비닐 포장재 및 일회용 비닐 봉투" type="vinyl"/>
       <CategoryComponent name="스티로폼" type="Styrofoam"/>
       <CategoryComponent name="금속캔류" type="can"/>
       <CategoryComponent name="고철류" type="metal"/>
       <CategoryComponent name="유리병류" type="glass"/>
       <CategoryComponent name="폐형광등류" type="lamp"/>
       <CategoryComponent name="종이류" type="paper"/>
       <CategoryComponent name="전자류" type="battery"/>
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
        }}
        className="category-item vc hc">
          <div>
          {name}
                 </div>
        
      </Link>
    </div>
  )
}