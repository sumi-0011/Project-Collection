import React,{useState} from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import { Main,SocialDistance,MapView ,ErrorReport,ConfirmeState} from "./pages/index";
import { Header, Footer } from "./components/index";
import "./assets/css/main.css";
import {authService, dbService} from './firebase';
import Clinic from "./pages/Clinic";
function App() {
 

  return (
    <BrowserRouter>
      <Header />
      <RouterList />
     {/* <TestRouter/> / */}

    </BrowserRouter>
  );
}
function TestDB() {
  // #3.1 Nweeting! (06:37) 어떻게 가져올지는 이 다음에
  const [nweet,setNweet] = useState("sumi");

  const onSubmit = async (e) => {
    e.preventDefault();
    await dbService.collection("nweets").add({
      nweet,
      createdAt: Date.now(),
    });
    setNweet("");

  }
  const onChange = (event) => {
    const {
      target: { value },
    } = event;
    setNweet(value);
  };
  return (
    <div>
    <form onSubmit={onSubmit}>
      <input
        value={nweet}
        onChange={onChange}
        type="text"
        placeholder="What's on your mind?"
        maxLength={120}
      />
      <input type="submit" value="Nweet" />
    </form>
  </div>
  )
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
