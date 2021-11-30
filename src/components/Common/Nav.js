import React from "react";
import { Link } from "react-router-dom";
import { Dropdown, Button } from "react-bootstrap";

function Nav() {

 

  return (
    <nav id="nav">
      <ul >
        {/* 메인 + 확진자 현황 */}
        <li>
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
        <li>
          <Link
            to={{
              pathname: `/errorReport`,
              state: {},
            }}
            key="errorReport"
          >
            오류리포트
          </Link>
        </li>
      </ul>
    </nav>
  );
}

export default Nav;
