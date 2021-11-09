import React, { Component } from 'react';
import Modal from 'react-awesome-modal';


import axios from 'axios';

class user_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
      visible_change_password: false,
    }
  }
  _openModal_change_password = function(){
    this.setState({
      visible_change_password: true,
    })
  }
  _closeModal_change_password = function(){
    this.setState({
      visible_change_password: false,
    })
    document.getElementsByName('default_pass')[0].value = '';
    document.getElementsByName('change_password')[0].value = '';
    document.getElementsByName('change_password')[1].value = '';
  }

  _change_password = async function(proper){
    const before_password = proper;
    const type_before_password = document.getElementsByName('default_pass')[0].value.trim();
    const after_password_one = document.getElementsByName('change_password')[0].value.trim();
    const after_password_two = document.getElementsByName('change_password')[1].value.trim();
    if(before_password !== type_before_password){
      alert('기존 비밀번호가 옳지 않습니다.');
    }else if(type_before_password === '' || after_password_one === '' || after_password_two === ''){
      alert('빈칸이 존재합니다');
    }else if(after_password_one !== after_password_two){
      alert('새로운 비밀번호 2번째가 틀렸습니다.');
    }else if(before_password === after_password_one){
      alert('기존 비밀번호와 일치합니다.');
    }else{
      //채워야됨
      alert('변경 완료');
    }
    
  }
  render() {
    const { user_name, user_id, user_password } = this.props;
    return (
      <div>
        <table>
          <tr>
            <td>이름 : </td>
            <td>{user_name}</td>
          </tr>
          <tr>
            <td>아이디 : </td>
            <td>{user_id}</td>
          </tr>
          <tr>
            <td>비밀번호 변경 : </td>
            <td><input type='button' value='비밀번호 변경' onClick={() => this._openModal_change_password()}></input></td>
          </tr>
        </table>
        <Modal visible={this.state.visible_change_password} width='400' height='300' effect='fadeInDown' onClickAway={() => this._closeModal_change_password()} >
          <div>
            <input type='password' placeholder='기존 비밀번호' name='default_pass'/><br/>
            <input type='password' placeholder='새 비밀번호' name='change_password'/><br/>
            <input type='password' placeholder='새 비밀번호 다시 입력' name='change_password'/><br/>
            <input type='button' value='변경하기' onClick={() => this._change_password({user_password})}/>
          </div>
        </Modal>
      </div>
    )
  }
}

export default user_page;