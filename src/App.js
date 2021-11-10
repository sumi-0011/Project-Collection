import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import PostList from "./components/Test/PostList.js";
import Detail from "./components/Test/Detail.js";

function App() {
  return (
    <BrowserRouter>
     <TestNav/>
     <TestRouter/>
    </BrowserRouter>
  );
}
function TestNav() {
  return (
    <div>
      <Link to="/main">
        <button className="nav-link">리스트 </button>
      </Link>
      <Link to="/main/posts/1">
        <button className="nav-link"> 1 </button>
      </Link>
    </div>
  );
}
function TestRouter() {
  return(
    <Switch>
    <Route exact path="/main" component={PostList} />
    {/* 이건 나중에 바꾸도록? 숫자로? */}
    <Route exact path="/main/posts/:id" component={Detail} />
    
  </Switch>
  )
}
export default App;
