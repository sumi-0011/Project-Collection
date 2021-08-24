const switchBtEl = document.querySelector('#switchBt');
const switchLabelEl = document.querySelector('#switchBt+label');

const screenEl = document.querySelector('.screen div.item');
// 키보드 자판
const keyboardELs = document.querySelectorAll('section.container .item');

let selectTheme = 1;  //기본 테마
let inputNumber ='';  //입력하고 있는 숫자
let lastNum = '';     //마지막으로 연산에 이용된 숫자
let lastOperate = ''  //마지막으로 이용한 연산자
let arr = [];
keyboardELs.forEach(function(el) {
  el.addEventListener('click',function(e) {
    var t = clickKeyBoard(e);
  });
})

switchBtEl.addEventListener('click',function() {
  switchBtEl.classList.remove(`theme${selectTheme}`);
  document.body.classList.remove(`theme${selectTheme}`);
  selectTheme =  selectTheme<3? selectTheme +1 : 1;
  switchBtEl.classList.add(`theme${selectTheme}`);
  document.body.classList.add(`theme${selectTheme}`);

})
function calculated() {
  if(arr.length==0 && inputNumber==''){
    return;
  }
  let firstNum = Number(arr[0]);
  if(lastNum=='') {
    lastNum = inputNumber;
  }
  let secondNum = inputNumber!='' ? Number(inputNumber) : Number(lastNum);
  let result;
  let operate;
  if (arr.length <= 1){
    if(lastOperate=='') return;
    operate = lastOperate;
  }
  else {
    operate = arr[1];
  }
  switch(operate) {
    case '+':
      result = firstNum + secondNum;
      break;
    case '-':
      result =firstNum - secondNum;
      break;
    case '/':
      if(secondNum==0) {
        result = '오류';
      }
      else {
        result =firstNum / secondNum;
      }
      break;
    case 'x':
      result =firstNum * secondNum;
      break;
    default:
      console.error('error');
  }
  screenEl.innerHTML = result;
  arr.length=0;
  arr.push(result);
  inputNumber='';
  lastOperate = operate;
  lastNum = secondNum;
}
function clickKeyBoard(e) {
  const selectKey = e.target.id.replace('select-','');
  //숫자를 클릭한경우
  if(!isNaN(selectKey) || selectKey == '.') {
    inputNumber +=selectKey;
  }
  else if(selectKey == 'reset') {
    arr.length = 0;
    inputNumber ='';
  }
  else if(selectKey == 'del') {                       
    if(arr.length==2) {
      //연산자 제거
      arr.pop();
      return;
    }
    else {
      // 숫자 하나 제거
      inputNumber = inputNumber.slice(0,-1);
      lastNum = inputNumber;
    }
   
  }
  else if(selectKey == 'equl') {
    calculated();
    return;
  }
  else {
    // +/-x
    if(arr.length == 2) {
      arr.pop()
      arr.push(selectKey);
      return;
    }
    else if(arr.length==0) {
      arr.push(inputNumber);
      arr.push(selectKey);
      lastNum = inputNumber;
      inputNumber = '';
      return;
    }
    else {
      // arr.length == 1일때는 계산 한번 한 후
      arr.push(selectKey);
      return;
    }
  }
  screenEl.innerHTML = inputNumber;

}