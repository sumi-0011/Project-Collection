const billInputEl = document.querySelector('#bill_input');
const TipBtnEls = document.querySelectorAll('.tip_btn');
const tipCustomBtnEl = document.querySelector('#tip_custom.tip_btn');
const people_numEl = document.querySelector('#select_people_num');
const tipAmountEl = document.querySelector('#result .tip_amount .calculate_daler');
const totalEl = document.querySelector('#result .total .calculate_daler');
const resetBtEl = document.querySelector('section.reset input[type="button"]');
let people_num = 0;
let bill = 0;
let tip = 0;
function resetDisable() {
  if(document.querySelector('section.reset input[type="button"].btn-disabled')) {
    resetBtEl.classList.remove('btn-disabled');
    resetBtEl.disabled = false;
    console.log('asd');
  }
}
resetBtEl.addEventListener('click',function() {
  resetBtEl.classList.add('btn-disabled');
  resetBtEl.disabled  = true;

  billInputEl.value =null;
  people_numEl.value =null;
  tipCustomBtnEl.value =null;
  if(document.querySelector('.tip_btn.btn_selected')) {
    document.querySelector('.tip_btn.btn_selected').classList.remove('btn_selected');
  }
  tipAmountEl.innerHTML = '$0.00';
  totalEl.innerHTML ='$0.00';
})
function calculate() {
  console.log(`bill : ${bill} tip : ${tip} people : ${people_num} `);
  console.log(typeof(bill),typeof(tip),typeof(people_num));
  if(people_num!=0) {
    resetDisable();
    let total_tipAmount = bill*tip/100;
    let tipAmount = Math.floor(total_tipAmount/people_num*100)/100;
    let total = Math.round((bill/people_num + total_tipAmount/people_num)*100)/100;
    tipAmountEl.innerHTML = `\$${tipAmount}`;
    // tipAmountEl.innerHTML = `\$${tipAmount}`;
    totalEl.innerHTML = `\$${total}`;

  }
  // let total = 
}
billInputEl.addEventListener('input',function() {
  bill =Number( billInputEl.value);
  calculate();
})
tipCustomBtnEl.addEventListener('input',function(e) {
  tip = Number(tipCustomBtnEl.value);
  calculate();
})
TipBtnEls.forEach(function(elem) {
  elem.addEventListener('click',function(e) {
    // 원래 선택된 것은 선택 해제
    if(document.querySelector('.tip_btn.btn_selected')) {

      document.querySelector('.tip_btn.btn_selected').classList.remove('btn_selected');
    }
    // 선택
    e.target.classList.add('btn_selected');
    if(e.target ==tipCustomBtnEl ) {
      return;
    }
    tip =  Number(e.target.value.slice(0,-1));
    calculate();
  })
})


people_numEl.addEventListener('input',function() {
  if(people_numEl.value == 0) {
    people_numEl.classList.add('input_zero_error');
    document.querySelector('.number_people h4').classList.add('input_zero_error');
  }
  else {
    people_numEl.classList.remove('input_zero_error');
    document.querySelector('.number_people h4').classList.remove('input_zero_error');
  }
  people_num =Number( people_numEl.value);
  calculate();
});
