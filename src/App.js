import React,{useState} from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import { Main, NoSiderBarPage,SocialDistance,ConfirmeState,MapView } from "./pages/index";
import { Header, Footer } from "./components/index";
import "./assets/css/main.css";
import {authService, dbService} from './firebase';
function App() {
 
  console.log(authService.currentUser);

  return (
    <BrowserRouter>
      {/* <a href='html\index.html'>sss</a> */}
      <Header />
      <RouterList />
      <TestD/>
      {/* <TestNav/>
     <TestRouter/> */}

      {/* <Footer/> */}
    </BrowserRouter>
  );
}
function TestD() {
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
      <Route exact path="/main" component={Main} />
      <Route exact path="/clinic" component={ConfirmeState} />
      <Route exact path="/map" component={MapView} />
      <Route exact path="/distance" component={SocialDistance} />
    </Switch>
  );
}


export default App;
