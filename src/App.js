import React,{useEffect, useState} from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import { Main,SocialDistance,MapView ,ErrorReport,ConfirmeState} from "./pages/index";
import { Header, Footer } from "./components/index";
import "./assets/css/main.css";
import {authService, dbService} from './firebase';
import Clinic from "./pages/Clinic";
import * as Send from "./backend/SendDB.js";
function App() {
  useEffect(() => {
    Send.SendConfirmRoute();
  }, [])

  return (
    <BrowserRouter>
      <Header />
      <RouterList />
     {/* <TestRouter/> / */}

    </BrowserRouter>
  );
}

function RouterList() {
  return (
    <Switch>
      <Route exact path="/" component={Main} />
      <Route exact path="/confirmeState" component={ConfirmeState} />
      <Route exact path="/clinic" component={Clinic} />
      <Route exact path="/map" component={MapView} />
      <Route exact path="/distance" component={SocialDistance} />
      <Route exact path="/errorReport" component={ErrorReport} />
    </Switch>
  );
}


export default App;
