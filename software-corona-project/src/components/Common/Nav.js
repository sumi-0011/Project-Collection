import React from "react";
import { Link } from "react-router-dom";
import { Dropdown, Button } from "react-bootstrap";
import LogoutButton from '../test2/LogoutButton';

function Nav({logout,authenticated}) {

 

  return (
    <nav id="nav">
      <ul >

        {/*  확진자 현황 */}
      
         {authenticated ? (
            <>
  <li>
          <Link
            to={{
              pathname: `/ManagerReport`,
              state: {},
            }}
            key="ManagerReport"
          >
            관리자
          </Link>
        </li>
           <li>
          <LogoutButton logout={logout} />
        </li>
           
            </>

          ) : (
            <>
              <li>
          <Link
            to={{
              pathname: `/confirmeState`,
              state: {},
            }}
            key="confirmeState"
          >
             확진자 현황
          </Link>
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
        <li>
          <Link to="/login">
             Login
            </Link>
        </li>
            </>
           
          )}
       
        
      </ul>
    </nav>
  );
}

export default Nav;
