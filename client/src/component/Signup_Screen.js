import React from 'react';
import { View, Text, TextInput, TouchableOpacity, Dimensions, StyleSheet, Button } from 'react-native';

const Signup_Screen = ({ navigation }) => {
    return (
        <div className="screen signup-screen" >
            <p>회원가입 페이지</p>
            <input className="input-id" type="text" placeholder='아이디' />
            <button className="btn-check" onClick={() => alert('중복 체크 완료')} >중복 체크</button>
            <input className="input-pw" type="text"  placeholder='비밀번호'  />
            <input className="input-pw-test" type="text"  placeholder='비밀번호 확인' />
            <button  >회원가입</button>
        </div>
    );
};

export default Signup_Screen;