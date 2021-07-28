const addItems = document.querySelector('.add-items');
const itemsList = document.querySelector('.plates');
const items = JSON.parse(localStorage.getItem('items')) || [];

addItems.addEventListener('submit',toggleSubmit);
setItems();
function toggleSubmit(e) {
  e.preventDefault();
  console.log('items : ',items);
  let object = {
      text : addItems.querySelector('input[type="text"]').value,
      done : false
  };
  items.push(object);
  console.log(JSON.stringify(items));
  localStorage.setItem('items',JSON.stringify(items));
  addItems.querySelector('input[type="text"]').value = '';
  setItems();
}
function setItems() {
    let ulist = '';
    let cnt =0;
    items.forEach(item => {
        ulist +=`<li>
            <input type="checkbox" data-index="${cnt}" id="item${cnt}" ${item.done ? 'checked' : ''}>
            <label for="item${cnt++}">${item.text}</label>
        </li>`;
    });
    itemsList.innerHTML = ulist;
    console.log(ulist);

    itemsList.querySelectorAll('li input[type="checkbox"]').forEach(chk => {
        chk.addEventListener('click',clickChk);
    });
}
function clickChk(e) {
    items[e.target.dataset.index].done = e.target.checked;
    localStorage.setItem('items',JSON.stringify(items));    //localstorage에 적용
    // console.log(items[e.target.dataset.index]);
   
}