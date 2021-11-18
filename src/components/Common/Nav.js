import React from "react";
import { Link } from "react-router-dom";
import { Dropdown, Button } from "react-bootstrap";

function Nav() {

 
  const handleClick = (e) => {
    console.log(e.target);
  };
  return (
    <nav id="nav">
      <ul onClick={handleClick}>
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
  );
}

export default Nav;
