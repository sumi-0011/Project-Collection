import React,{useEffect, useState} from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Link } from "react-router-dom";
import { Main,SocialDistance,MapView ,ErrorReport,ConfirmeState,ManagerReport} from "./pages/index";
import { Header, Footer } from "./components/index";
import "./assets/css/main.css";
import {authService, dbService} from './firebase';
import Clinic from "./pages/Clinic";
import * as Send from "./backend/SendDB.js";

import { signIn } from './components/test2/auth';
import Profile from './components/test2/Profile';
import AuthRoute from './components/test2/AuthRoute';
import LogoutButton from './components/test2/LogoutButton';
import LoginForm from './components/test2/LoginForm';
function App() {
  useEffect(() => {
    Send.SendConfirmRoute();
  }, [])

  // test
 const [user, setUser] = useState(null);
  const authenticated = user != null;

  const login = ({ email, password }) => setUser(signIn({ email, password }));
  const logout = () => setUser(null);
//
  return (
    <BrowserRouter>
  <div>
          {authenticated ? (
            <LogoutButton logout={logout} />
          ) : (
            <Link to="/login">
              <button>Login</button>
            </Link>
          )}
        </div>
      <Header />
      <RouterList />
  <main>
        <Switch>
          <Route
            path="/login"
            render={props => (
              <LoginForm authenticated={authenticated} login={login} {...props} />
            )}
          />
          <AuthRoute
            authenticated={authenticated}
            path="/profile"
            render={props => <Profile user={user} {...props} />}
          />
        </Switch>
      </main>

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
      <Route exact path="/ManagerReport" component={ManagerReport} />
    </Switch>
  );
}


export default App;
