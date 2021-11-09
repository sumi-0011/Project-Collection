import React, { Component } from 'react';
import Modal from 'react-awesome-modal';

import axios from 'axios';

class user_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
      visible_change_password: false,
      point: 0,
    }
  }
  componentDidMount(){
    this._getPoint();
  }
  _openModal_change_password = function () {
    this.setState({
      visible_change_password: true,
    })
  }
  _closeModal_change_password = function () {
    this.setState({
      visible_change_password: false,
    })
    document.getElementsByName('default_pass')[0].value = '';
    document.getElementsByName('change_password')[0].value = '';
    document.getElementsByName('change_password')[1].value = '';
  }

  _change_password = async function (pw) {
    const before_password = pw;
    const type_before_password = document.getElementsByName('default_pass')[0].value.trim();
    const after_password_one = document.getElementsByName('change_password')[0].value.trim();
    const after_password_two = document.getElementsByName('change_password')[1].value.trim();

    if (before_password !== type_before_password) {
      return alert('기존 비밀번호가 옳지 않습니다.');
    }
    else if (type_before_password === '' || after_password_one === '' || after_password_two === '') {
      return alert('빈칸이 존재합니다');
    }
    else if (after_password_one !== after_password_two) {
      return alert('새로운 비밀번호 2번째가 틀렸습니다.');
    }
    else if (before_password === after_password_one) {
      return alert('기존 비밀번호와 일치합니다.');
    }

    const change_data = { id: this.props.id, pw: after_password_two };
    const change_result = await axios('/change/pw', {
      method: 'POST',
      headers: new Headers(),
      data: change_data,
    })

    if (change_result.data) {
      this._closeModal_change_password();
      return alert('변경 성공');
    }
    else {
      return alert('변경에 실패했습니다.')
    }
  }
  _getPoint = async function(){
    const id = this.props.id;

    const point_result = await axios('/get/point', {
      method: 'POST',
      headers: new Headers(),
      data: {id: id},
    })
    
    this.setState({
      point: point_result.data.point
    })
  }

  render() {
    const { user_name, id, pw } = this.props;
    return (
      <div>
        <div>
          <div>사용자 이름: {user_name}</div>
          <div>사용자 ID: {id}</div>
          <div>포인트: {this.state.point}</div>
        </div>
        <input type='button' value='비밀번호 변경' onClick={() => this._openModal_change_password()}></input>
        <Modal visible={this.state.visible_change_password} width='400' height='300' effect='fadeInDown' onClickAway={() => this._closeModal_change_password()} >
          <div>
            <input type='password' placeholder='기존 비밀번호' name='default_pass' /><br />
            <input type='password' placeholder='새 비밀번호' name='change_password' /><br />
            <input type='password' placeholder='새 비밀번호 다시 입력' name='change_password' /><br />
            <input type='button' value='변경하기' onClick={() => this._change_password(pw)} />
          </div>
        </Modal>
      </div>
    )
  }
}

export default user_page;