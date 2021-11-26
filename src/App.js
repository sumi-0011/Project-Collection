import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import { Main, NoSiderBarPage,SocialDistance,ConfirmeState,MapView } from "./pages/index";
import { Header, Footer } from "./components/index";
import "./assets/css/main.css";
import {authService} from './firebase';
function App() {
 
  console.log(authService.currentUser);

  return (
    <BrowserRouter>
      {/* <a href='html\index.html'>sss</a> */}
      <Header />
      <RouterList />
      {/* <TestNav/>
     <TestRouter/> */}

      {/* <Footer/> */}
    </BrowserRouter>
  );
}
function RouterList() {
  return (
    <Switch>
      <Route exact path="/" component={Main} />
      <Route exact path="/main" component={Main} />
      <Route exact path="/clinic" component={ConfirmeState} />
      <Route exact path="/map" component={MapView} />
      <Route exact path="/distance" component={SocialDistance} />
    </Switch>
  );
}


export default App;
