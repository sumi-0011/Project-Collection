
// <!-- main section event -->
const items = document.querySelectorAll('#main-section ul li.item');
const MainSContentImg = document.querySelector('#main-section .content-img');

// main section에 이미지 추가
items.forEach(function (currentItem, currentIndex) {
    currentItem.addEventListener('mouseover', function () {
        const value = '<img src="./image/pain_0' + (currentIndex + 1) + '.png" alt="어디가 아프세요?">'
        MainSContentImg.innerHTML = value;
    })

})