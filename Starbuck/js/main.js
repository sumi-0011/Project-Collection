// gsap
const visual_set_commonEl = document.querySelectorAll('.set__common');
const scroll_leftEls = document.querySelectorAll('.scroll-left');
const scroll_rightEls = document.querySelectorAll('.scroll-right');
visual_set_commonEl.forEach(function(elem, index) {
  gsap.to(elem,{
    delay: (index+1) *0.7,
    opacity:1
  })
})
scroll_leftEls.forEach(function(elem) {
gsap.registerPlugin(ScrollTrigger);

  gsap.to(elem, {
    scrollTrigger: {
      trigger: elem,
      // markers: true,
      start: "top 80%",
      scrub: 5,
      end: "+=50px"
    },
    x: 1000, 
    opacity:1
  
  });
})
gsap.registerPlugin(ScrollTrigger);

scroll_rightEls.forEach(function(elem) {
  gsap.registerPlugin(ScrollTrigger);

  gsap.to(elem, {
    scrollTrigger: {
      trigger: elem,
      // markers: true,
      start: "top 80%",
      scrub: 5,
      end: "+=50px"

    },
    x: -1000, 
    opacity:1
    
  });
})


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