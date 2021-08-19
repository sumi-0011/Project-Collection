const switchBtEl = document.querySelector('#switchBt');
const switchLabelEl = document.querySelector('#switchBt+label');

let selectTheme = 1;  //기본 테마
switchBtEl.addEventListener('click',function() {
  console.log(switchBtEl.classList);
  switchBtEl.classList.remove(`theme${selectTheme}`);
  selectTheme =  selectTheme<3? selectTheme +1 : 1;
  switchBtEl.classList.add(`theme${selectTheme}`);

})