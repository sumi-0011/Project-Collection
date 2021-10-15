import React from 'react';

const Select_menu_Screen = ({ navigation }) => {
    return (
        <div className="screen select-menu-screen"  >
            <p>로그인 / 회원가입 선택 페이지</p>
            <input type="text" className="input-id"  placeholder='아이디'/>
            <input type="password" className="input-pw" placeholder='비밀번호'  />
            <button className="btn-login" onClick={() => navigation.navigate('Main_Screen')} >
                <p>로그인</p></button>
            <button className="btn-join" onClick={() => navigation.navigate('Signup_Screen')} >
                <p>회원가입</p></button>
        </div>
    );
};



export default Select_menu_Screen;