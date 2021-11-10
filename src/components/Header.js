import { Dropdown, Button } from "react-bootstrap";
import "../assets/all.css";
import { Link } from "react-router-dom";

function Header() {
  return (
    <div id="header-wrapper">
      <header id="header" className="container">
        {/* <!-- Logo --> */}
        <div id="logo">
          <h1>
            <a href="index.html">Corona</a>
          </h1>
          <span>by sumi😚</span>
        </div>

        {/* <!-- Nav --> */}
        {<Nav />}
      </header>
    </div>
  );
}
function Nav() {
  return (
    <nav id="nav">
      <ul>
        <li className="current">
          <Link
            to={{
              pathname: `/main`,
              state: {
               
              },
            }}
            key='mainPage'
          >
            Main
          </Link>
          {/* <a href="index.html">Welcome</a> */}
        </li>

        <li>
          <Link
            to={{
              pathname: `/main/10`,
              state: {
              },
            }}
            key={10}
          >
            10
          </Link>
          <a href="left-sidebar.html">Left Sidebar</a>
        </li>
        <li>
          <a href="right-sidebar.html">Right Sidebar</a>
        </li>
        <li>
          <a href="no-sidebar.html">No Sidebar</a>
        </li>
      </ul>
    </nav>
  );
}

export default Header;

// 드롭다운 예시
{/* <Dropdown className="d-inline mx-2">
    <Dropdown.Toggle id="dropdown-autoclose-true">
      Default Dropdown
    </Dropdown.Toggle>

    <Dropdown.Menu>
      <Link
            to={{
              pathname: `/main/1`,
              state: {
               
              },
            }}
            key='fisrt'
          >
            first
          </Link>
      <Link
            to={{
              pathname: `/main/2`,
              state: {
               
              },
            }}
            key='sec'
          >
            sec
          </Link>
      <Link
            to={{
              pathname: `/main/3`,
              state: {
               
              },
            }}
            key='third'
          >
            third
          </Link>
      {/* <Dropdown.Item >Menu Item</Dropdown.Item>
      <Dropdown.Item >Menu Item</Dropdown.Item> */}
  //   </Dropdown.Menu>
  // </Dropdown> */}