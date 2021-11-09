import React, { Component } from 'react';
import './App.css';
import './assets/css/bootstrap.min.css';  //부트스트랩
import { Head } from './component';
import { Image, Map, Info, User ,Nav } from './page/index.js';

import { Routes, Route, Link } from 'react-router-dom';

class App extends Component {
  constructor(props) {
    super(props)
    this.state = {
      is_login: false,
      user_name: '',
    }
  }
  componentDidMount() {
    if (sessionStorage.login) {
      this.setState({
        is_login: true,
        user_name: sessionStorage.login,
      })
    }
  }

  _login = (data) => {
    sessionStorage.setItem('login', data);
    this.setState({
      is_login: true,
      user_name: data
    })
    window.location.reload()
  }
  _logout = () => {
    sessionStorage.removeItem('login');
    this.setState({
      is_login: false,
    })
    window.location.href = '/';
  }

  render() {
    const { is_login, user_name } = this.state;
    const { _login, _logout } = this;
    return (
      <div style={{ textAlign: 'center' }}>
        <div>
          <Head _login={_login} _logout={_logout} is_login={is_login} user_name={user_name} />
        </div>
        {is_login
          ? <div>
            <div>
              {/* navigation부분 => link태그 옮김 */}
              <Nav/>
            </div>
            <Routes>
              <Route path='/' element={<Image />} />
              <Route path='/map' element={<Map />} />
              <Route path='/info' element={<Info />} />
              <Route path='/user' element={<User />} />
            </Routes>
          </div>
          : <div>
            로그인을 먼저 해주세요
          </div>
        }
      </div>
    )
  }
}

export default App;