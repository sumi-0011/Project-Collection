import React, { Component } from 'react';
import Modal from 'react-awesome-modal';

import axios from 'axios';

import { Position } from '../component'

import default_profile from '../assets/img/default_profile.jpg';
import manage from '../assets/img/manage.png';

class user_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
      visible_change_password: false,
      point: 0,
      map: false,
      status: false,
      king: false,
    }
  }
  componentDidMount() {
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
      return alert('새로운 비밀번호가 일치하지 않습니다.');
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
  _getPoint = async function () {
    const id = this.props.id;

    const point_result = await axios('/get/point', {
      method: 'POST',
      headers: new Headers(),
      data: { id: id },
    })

    this.setState({
      point: point_result.data.point
    })
  }

  _openMap() {
    this.setState({
      map: !this.state.map,
    })
    if (this.state.status) {
      this.setState({
        status: !this.state.status,
      })
    }
    if (this.state.king) {
      this.setState({
        king: !this.state.king,
      })
    }
  }
  _openStatus() {
    this.setState({
      status: !this.state.status,
    })
    if (this.state.map) {
      this.setState({
        map: !this.state.map,
      })
    }
    if (this.state.king) {
      this.setState({
        king: !this.state.king,
      })
    }
  }
  _openKing() {
    this.setState({
      king: !this.state.king,
    })
    if (this.state.map) {
      this.setState({
        map: !this.state.map,
      })
    }
    if (this.state.status) {
      this.setState({
        status: !this.state.status,
      })
    }
  }

  render() {
    const { id, pw } = this.props;
    return (
      <div>
        <div style={{ margin: '0 auto', textAlign: 'center' }}>
          <div >
            <img src={default_profile} alt='profile' style={{ width: window.innerWidth / 10 }} />
          </div>
          <div style={{ textAlign: 'center', marginBottom:'10px' }}>
            <div style={{ fontSize: '30px' }}>안녕하세요 <b>{id}님</b></div>
            <div style={{ fontSize: '20px' }}>
              포인트: <b>{this.state.point}</b>
              <label htmlFor='manage'>
                <img style={{width:'20px', marginLeft:'50px'}} src={manage} alt='manage' />
              </label>
              <input style={{visibility:'hidden'}} type='button' id='manage' onClick={() => this._openModal_change_password()}></input>
            </div>
          </div>
        </div>
        <div style={{marginBottom:'15px'}}>
          <input style={{ fontSize: '20px', width: '220px', marginRight: '10px', borderRadius: '20px', backgroundColor: '#F7F8E0' }} type='button' value='자주 가는 수거함' onClick={() => this._openMap()} />
          <input style={{ fontSize: '20px', width: '220px', marginRight: '10px', borderRadius: '20px', backgroundColor: '#CEECF5' }} type='button' value='월간 분리 배출 현황' onClick={() => this._openStatus()} />
          <input style={{ fontSize: '20px', width: '220px', borderRadius: '20px', backgroundColor: '#F8E0E6' }} type='button' value='월간 분리 배출 왕' onClick={() => this._openKing()} />
        </div>

        {this.state.map
          ? <Position />
          : null
        }
        {this.state.status
          ?
          <div>
            <div style={{ width: '50%', margin: '0 auto' }} >
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>날짜</div>
                <div style={{ width: '25%', margin: '0 auto' }}>수거함 위치</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-01</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-03</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-05</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-07</div>
                <div style={{ width: '25%', margin: '0 auto' }}>봉암 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-09</div>
                <div style={{ width: '25%', margin: '0 auto' }}>봉암 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-09</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-10</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
              <div style={{ display: 'flex' }}>
                <div style={{ width: '25%', margin: '0 auto' }}>2021-11-11</div>
                <div style={{ width: '25%', margin: '0 auto' }}>욧골 수거함</div>
              </div>
            </div>
            <div>
              분리 배출 총 횟수: 8
            </div>
          </div>
          : null
        }
        {this.state.king
          ?
          <div style={{ width: '50%', margin: '0 auto' }}>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>사용자</div>
              <div style={{ width: '25%', margin: '0 auto' }}>분리 배출 횟수</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>김아무개</div>
              <div style={{ width: '25%', margin: '0 auto' }}>80회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>홍길동</div>
              <div style={{ width: '25%', margin: '0 auto' }}>70회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>이수진</div>
              <div style={{ width: '25%', margin: '0 auto' }}>60회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>이민수</div>
              <div style={{ width: '25%', margin: '0 auto' }}>50회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>김민정</div>
              <div style={{ width: '25%', margin: '0 auto' }}>40회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>김민재</div>
              <div style={{ width: '25%', margin: '0 auto' }}>30회</div>
            </div>
            <div style={{ display: 'flex' }}>
              <div style={{ width: '25%', margin: '0 auto' }}>박지훈</div>
              <div style={{ width: '25%', margin: '0 auto' }}>20회</div>
            </div>
          </div>
          : null
        }


        <Modal visible={this.state.visible_change_password} width='400' height='300' effect='fadeInDown' onClickAway={() => this._closeModal_change_password()} >
          <div>
            <input style={{marginTop:'80px'}} type='password' placeholder='기존 비밀번호' name='default_pass' /><br />
            <input type='password' placeholder='새 비밀번호' name='change_password' /><br />
            <input type='password' placeholder='새 비밀번호 확인' name='change_password' /><br />
            <input type='button' value='변경하기' onClick={() => this._change_password(pw)} />
          </div>
        </Modal>
      </div>
    )
  }
}

export default user_page;