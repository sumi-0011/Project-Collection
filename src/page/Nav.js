import React from "react";
import { Link } from "react-router-dom";
import '../assets/header.css';
export default function Header() {
  return (
    // <!-- Header -->
    <nav className="navbar navbar-expand-lg navbar-light shadow">
      <div className="container d-flex justify-content-between align-items-center">
        <a
          className="navbar-brand text-success logo h1 align-self-center"
          href="/"
        >
          Happy Cycle
        </a>


        <div
          className="align-self-center collapse navbar-collapse flex-fill  d-lg-flex justify-content-lg-between"
          id="templatemo_main_nav"
        >
          <div className="flex-fill">
            <ul className="nav navbar-nav d-flex justify-content-between mx-lg-auto">
              <li className="nav-item">
                <Link to="/">
                  <span className="nav-link">내 재활품의 종류는?</span>
                </Link>
              </li>
              <li className="nav-item">
                <Link to="/map">
                  <span className="nav-link">수거함 위치 확인</span>
                </Link>
              </li>
              <li className="nav-item">
                <Link to="/info">
                  <span className="nav-link">재활용품 분리 배출 방법</span>
                </Link>
              </li>
              <li className="nav-item">
                <Link to="/user">
                  <span className="nav-link">마이페이지</span>
                </Link>
              </li>
            </ul>
          </div>

        </div>
      </div>
    </nav>
  );
}
