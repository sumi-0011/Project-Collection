import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import PostList from "./components/PostList.js";
import Detail from "./components/Detail.js";

function App() {
  return (
    <BrowserRouter>
     <TestNav/>
      <Switch>
        <Route exact path="/main" component={PostList} />
        {/* 이건 나중에 바꾸도록? 숫자로? */}
        <Route exact path="/main/posts/:id" component={Detail} />
        
      </Switch>
    </BrowserRouter>
  );
}
function TestNav() {
  return (
    <div>
      <Link to="/main">
        <button className="nav-link">게시글 리스트 화면</button>
      </Link>
      <Link to="/main/posts/1">
        <button className="nav-link">게시글 1 </button>
      </Link>
    </div>
  );
}

export default App;
