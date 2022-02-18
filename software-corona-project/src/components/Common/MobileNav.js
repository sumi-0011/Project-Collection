import React, {useState} from "react";
import { Link } from "react-router-dom";

function MobileNav() {
	const [navOn,setNavOn] = useState(false);
  const toggleClick = (e) => {
		setNavOn(!navOn);
		console.log(e.target);
	};

  return (
    <div id="mobile-nav">
      <div id="navToggle" onClick={toggleClick}>
        <a  className="toggle"></a>
      </div>
      <div id="navPanel" className={`${navOn ? 'on' : ''} `}>
        <nav>
          <button onClick={toggleClick}>X</button>
          {/* <h1>모바일용 Nav</h1> */}
          <ul onClick={toggleClick}>
          {/* 메인 + 확진자 현황 */}
          <li className="current">
            <Link
              to={{
                pathname: `/main`,
                state: {},
              }}
              key="mainPage"
            >
              Main
            </Link>
            {/* <a href="index.html">Welcome</a> */}
          </li>
          {/* 선별진료소 */}
          <li>
            <Link
              to={{
                pathname: `/clinic`,
                state: {},
              }}
              key="clinic"
            >
              선별진료소
            </Link>
          </li>
          {/* 지도 */}
          <li>
            <Link
              to={{
                pathname: `/map`,
                state: {},
              }}
              key="map"
            >
              지도
            </Link>
          </li>
          {/*  거리두기 */}
          <li>
            <Link
              to={{
                pathname: `/distance`,
                state: {},
              }}
              key="distance"
            >
              거리두기
            </Link>
          </li>
        </ul>
        </nav>
      </div>
    </div>
  );
}

export default MobileNav;
