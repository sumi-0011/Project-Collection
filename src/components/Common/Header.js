import "../../css/all.css";
import { Link } from "react-router-dom";
import Nav from './Nav';
import MobileNav from "./MobileNav";
function Header() {
  return (
    <div id="header-wrapper">
      <header id="header" className="container">
        {/* <!-- Logo --> */}
        <div id="logo">
          <h1>
          <Link
            to={{
              pathname: `/main`,
              state: {},
            }}
            key="mainPage"
          >
            Corona
          </Link>
          </h1>
          <span>by sumi😚</span>
        </div>

        {/* <!-- Nav --> */}
        {<Nav />}

        {/* Mobile Nav */}
        <MobileNav/>
      </header>
    </div>
  );
}


export default Header;

// 드롭다운 예시
{
  /* <Dropdown className="d-inline mx-2">
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
      <Dropdown.Item >Menu Item</Dropdown.Item> */
}
//   </Dropdown.Menu>
// </Dropdown> */}
