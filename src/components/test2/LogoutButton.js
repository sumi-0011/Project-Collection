import React from "react";
import { withRouter } from "react-router-dom";

function LogoutButton({ logout, history }) {
  const handleClick = () => {
    logout();
    history.push("/");
  };
  return <span onClick={handleClick}>Logout</span>;
}

export default withRouter(LogoutButton);