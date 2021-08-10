
const visual_set_commonEl = document.querySelectorAll('.set__common');

visual_set_commonEl.forEach(function(elem, index) {
  gsap.to(elem,{
    delay: (index+1) *0.7,
    opacity:1
  })
})


// SWIPER 
new Swiper('.notice-line .swiper-container', {
  direction: 'vertical',
  autoplay: true,
  loop: true
});