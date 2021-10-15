import React from 'react';


const My_Screen = ({ navigation }) => {
    return (
        <div className="my-screen screen">
            <div className="my-screen-id">
                <p>아이디</p>
                <p>default ID</p>
            </div>
            <div className="my-screen-btn-container">
            <button  onClick={() => navigation.navigate('Select_menu_Screen')} ><p>로그아웃</p></button>
            </div>
        </div>
    );
};


export default My_Screen;