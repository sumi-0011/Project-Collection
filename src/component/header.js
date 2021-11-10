import React, { Component } from 'react';
import Modal from 'react-awesome-modal';
import '../assets/header.css';
import axios from 'axios';

class header extends Component {
  constructor(props) {
    super(props)
    this.state = {
      visible_login: false,
      visible_signup: false,
      check_id: false,
    }
  }
  _openModal_login = function () {
    this.setState({
      visible_login: true,
    })
  }
  _closeModal_login = function () {
    this.setState({
      visible_login: false,
    })
    document.getElementsByName('login_id')[0].value = '';
    document.getElementsByName('login_pw')[0].value = '';
  }
  _openModal_signup = function () {
    this.setState({
      visible_signup: true,
    })
  }
  _closeModal_signup = function () {
    this.setState({
      visible_signup: false,
    })
    document.getElementsByName('signup_name')[0].value = '';
    document.getElementsByName('signup_id')[0].value = '';
    document.getElementsByName('signup_pw')[0].value = '';
    document.getElementsByName('signup_pw_check')[0].value = '';
  }

  _loginUser = async () => {
    const id = document.getElementsByName('login_id')[0].value.trim();
    const pw = document.getElementsByName('login_pw')[0].value.trim();

    if (id === '') {
      return alert('아이디를 입력해주세요.');
    }
    else if (pw === '') {
      return alert('비밀번호를 입력해주세요');
    }

    const login_data = { id: id, pw: pw };
    const login_result = await axios('/login/user', {
      method: 'POST',
      headers: new Headers(),
      data: login_data,
    })

    const data = {id: id, pw: pw, user_name: login_result.data.name}
    if (login_result.data.result) {
      this.props._login(data);
      this._closeModal_login();
      alert('로그인 성공')
    }
    else {
      alert('로그인 실패');
    }
  }

  _check_exist_id = async function () {
    const signup_id = document.getElementsByName('signup_id')[0].value.trim();

    if(signup_id === ''){
      return alert('아이디를 입력해주세요.')
    }

    const check_result = await axios('/check/id', {
      method: 'POST',
      headers: new Headers(),
      data: { id: signup_id },
    })

    if (check_result.data) {
      this.setState({
        check_id: true,
      })
      alert('사용가능한 아이디');
    }
    else {
      alert('중복된 아이디');
    }
  }
  _signup = async function () {
    const signup_name = document.getElementsByName('signup_name')[0].value.trim();  //최대 20자
    const signup_id = document.getElementsByName('signup_id')[0].value.trim();  //최대 30자
    const signup_pw = document.getElementsByName('signup_pw')[0].value.trim();  //최대 50자
    const signup_pw_check = document.getElementsByName('signup_pw_check')[0].value.trim();  //최대 50자

    if(signup_name === ''){
      return alert('이름을 입력해주세요.')
    }
    else if(signup_id === ''){
      return alert('아이디를 입력해주세요.')
    }
    else if(signup_pw === ''){
      return alert('비밀번호를 입력해주세요')
    }
    else if(signup_pw !== signup_pw_check){
      return alert('비밀번호가 다릅니다.')
    }
    else if(!this.state.check_id){
      return alert('아이디 중복확인을 해주세요')
    }

    const signup_data = { name: signup_name, id: signup_id, pw: signup_pw }

    const signup_result = await axios('/signup/user', {
      method: 'POST',
      headers: new Headers(),
      data: signup_data,
    })

    if (signup_result.data) {
      this._closeModal_signup();
      alert('회원가입 완료')
    }
    else {
      alert('이미 존재하는 아이디');
    }
  }

  render() {
    const { is_login, _logout, id } = this.props;
    return (
      <nav className="navbar navbar-expand-lg bg-dark navbar-light d-none d-lg-block" id="templatemo_nav_top">
      <div className="container text-light">
          <div className="w-100 d-flex justify-content-between">
        {is_login
          ? <div className="header-container">
            <span className="navbar-sm-brand text-light text-decoration-none">Welcome {id}!</span>
            <input className="header-logoutBT" type='button' value='로그아웃' onClick={() => _logout()} />
          </div>
          : <div className="header-container">
            <input type='button' value='로그인' onClick={() => this._openModal_login()} />
            <input type='button' value='회원가입' onClick={() => this._openModal_signup()} />
          </div>
        }
        <Modal visible={this.state.visible_login} width='400' height='300' effect='fadeInDown' onClickAway={() => this._closeModal_login()} >
          <div style={{ textAlign: 'center', }}>
            <input type='text' placeholder='아이디' name='login_id' />
            <input type='password' placeholder='비밀번호' name='login_pw' />
            <input type='button' value='로그인' name='login_button' onClick={() => this._loginUser()} />
          </div>
        </Modal>
        <Modal visible={this.state.visible_signup} width='400' height='300' effect='fadeInDown' onClickAway={() => this._closeModal_signup()} >
          <div style={{ textAlign: 'center', }}>
            <input type='text' placeholder='이름' name='signup_name' />
            <div>
              <input type='text' placeholder='아이디' name='signup_id' />
              <input type='button' value='중복확인' onClick={() => this._check_exist_id()} />
            </div>
            <input type='password' placeholder='비밀번호' name='signup_pw' />
            <input type='password' placeholder='비밀번호 확인' name='signup_pw_check' />
            <input type='button' value='회원가입' name='signup_button' onClick={() => this._signup()} />
          </div>
        </Modal>
        </div>
        </div>
    </nav>
    );
  }
}
export default header;