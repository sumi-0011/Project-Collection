// gsap
const visual_set_commonEl = document.querySelectorAll('.set__common');

visual_set_commonEl.forEach(function(elem, index) {
  gsap.to(elem,{
    delay: (index+1) *0.7,
    opacity:1
  })
})
gsap.registerPlugin(ScrollTrigger);

gsap.to(".scroll-left", {
  scrollTrigger: {
    trigger: ".scroll-left",
    start: "top 80%",
    scrub: 5,
    end: 100
  },
  x: 1000, 
  opacity:1

});
gsap.to(".scroll-right", {
  scrollTrigger: {
    trigger: ".scroll-right",
    // markers: true,
    start: "top 80%",
    scrub: 5,
    end: 100
  },
  x: -1000, 
  opacity:1
  
});
gsap.to(".scroll-opacity", {
  scrollTrigger: {
    trigger: ".scroll-opacity",
    // markers: true,
    start: "top 80%",
    scrub: 5,
    end: 100
  },
  opacity:1
  
});

// SWIPER 
new Swiper('.notice-line .swiper-container', {
  direction: 'vertical',
  autoplay: true,
  loop: true
});