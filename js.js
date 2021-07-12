// 사진처럼 해당하는 키보드를 누르면 화면에 효과와 함께 소리가 나는 드럼 킷을 만드는 것
// 해당하는 문자를 누르면 사용자 정의 속성인 data-key가 같은 sound파일을 실행하게 한다.
// 그리고 sound가 재생중임을 보이기 위해 화면의 해당하는 문자의 부분을 .playing으로 설정한다. 
// 
// 키보드에서 문자를 누른경우 실행되는 이벤트
addEventListener('keydown', getEventType);
// 오디오 재생시의 이벤트 
const keys = Array.from(document.querySelectorAll('.key'));
keys.forEach(key => key.addEventListener('transitionend', removeTransition));
function removeTransition(e) {
    // css 파일의 .playing {transform: scale(1.1); } [일부]
    // transition이 완료된 이후에 발생하는 이벤트, transition 완료를 감지하여 playing클래스를 제거한다. 
    if (e.propertyName !== 'transform') return;
    e.target.classList.remove('playing');
  }

function getEventType(e) {
    // e.type : 발생한 이벤트의 종류
    // e.keyCode : 눌린 keycode
    // 1. 키보드에서 누른 key를 알아온다. 
    // console.log(e.keyCode);

    // 2. 누른 키에 맞는 오디오 재생
    // 객체 예시 : ` <audio data-key="65" src="sounds/clap.wav"></audio>` 
    // data-key 속성으로 객체를 가져올수 있다. 
    const audio = document.querySelector(`audio[data-key="${e.keyCode}"]`);
    // 오디오 객체가 없다면 리턴
    if(!audio) return;
    
    audio.currentTime = 0;
    audio.play();

    // 3. 누른 키에 해당하는 div태그를 playing표시하기
    // div태그 예시 : <div data-key="76" class="key">
    document.querySelector(`div[data-key="${e.keyCode}"]`).classList.add('playing');

    
}
// 오디오 재생종료시 실행되는 함수
