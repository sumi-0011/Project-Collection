import React from "react";
import { Image, Map, Info, User } from "./index";
import { Link } from "react-router-dom";
export default function Header() {
  return (
    // <!-- Header -->
    <nav className="navbar navbar-expand-lg navbar-light shadow">
      <div className="container d-flex justify-content-between align-items-center">
        <a
          className="navbar-brand text-success logo h1 align-self-center"
          href="index.html"
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
                  <span className="nav-link">사물인식</span>
                </Link>
              </li>
              <li className="nav-item">
                <Link to="/map">
                  <span className="nav-link">배출 장소 확인</span>
                </Link>
              </li>
              <li className="nav-item">
                <Link to="/info">
                  <span className="nav-link">배출방법</span>
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
