    const slide = new Swiper('#visual-slide', {
        // 다양한 옵션 설정, 
        // 아래에서 설명하는 옵션들은 해당 위치에 들어갑니다!!
        slidesPerView: 1,
        spaceBetween: 30,
        loop: true,
        loopAdditionalSlides: 1,
        pagination: false, // pager 여부
        autoplay: {  // 자동 슬라이드 설정 , 비 활성화 시 false
            delay: 3000,   // 시간 설정
            disableOnInteraction: false,  // false로 설정하면 스와이프 후 자동 재생이 비활성화 되지 않음
        },
        navigation: {   // 버튼 사용자 지정
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    })