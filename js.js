{/* <div class="clock">
      <div class="clock-face">
        <div class="hand hour-hand"></div>
        <div class="hand min-hand"></div>
        <div class="hand second-hand"></div> */}

// 시간에 따라 설정해야함

function setTime() {
  var today = new Date();
  //1시간 = 30도 ,1분 = 6도,1초 = 6도
  var s = (today.getSeconds()/60 )*360 +90;   //Date 객체의 초 (0~59)
  var m = (today.getMinutes()/60 )*360 +90;   //Date 객체의 분 (0~59)
  var h = (today.getHours() /12 )*360 +90;   //Date 객체의 시 (0~23)

  const hh = document.querySelector('.hour-hand');
  const mh = document.querySelector('.min-hand');
  const sh = document.querySelector('.second-hand');

  console.log(h,m,s);
  hh.style.transform = `rotateZ(${h}deg)`;
  mh.style.transform = `rotateZ(${m}deg)`;
  sh.style.transform = `rotateZ(${s}deg)`;


}
setTime();
setInterval(setTime,1000);