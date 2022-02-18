let titles = document.querySelectorAll('.title');
titles.forEach(element => {
  element.addEventListener('click',clickTitle);
})

function clickTitle(e) {
  e.target.classList.toggle('clicked');
  e.target.nextSibling.nextSibling.classList.toggle("hidden");
}