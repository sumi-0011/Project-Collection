window.onload = () => {
  document.querySelector('img').style.border = `${changeSpacing()}px solid ${getColor()}`;
  changeBlur();
  
}
const changeSpacing = () => {
  var space = document.querySelector('#spacing').value;
  document.querySelector('img').style.borderWidth = `${space}px`;
  return space;
}
const changeBlur = () => {
  // https://blog.naver.com/jsy930609/221671999277
  var blur = document.querySelector('#blur').value;
  document.querySelector('img').style.filter = `blur(${blur}px)`;

}
const getColor = () => {
  var color = document.querySelector("#base").value;
  document.querySelector('img').style.borderColor = `${color}`;
  document.querySelector('.hl').style.color = `${color}`;

  return color;
}

// range 는 움직일때 input 으로 감지함 
// change 이벤트로 하니까 바를 움직이는 동안에는 변화가 감지되지 않는 문제가 있었다. 
document.querySelector('#spacing').addEventListener("input",changeSpacing);
document.querySelector('#blur').addEventListener("input",changeBlur);
// click으로 하였더니 선택하기 전 color을 가져오더라
// change후에 선택된 색상을 받아와 테두리의 색깔을 바꾸어주어야 한다. 
document.querySelector('#base').addEventListener("change",getColor);
