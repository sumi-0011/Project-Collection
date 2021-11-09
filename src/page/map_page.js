/*global kakao*/
import React, { Component } from 'react';

class map_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
      select_position: '',
    }
  }
  componentDidMount() {
    var container = document.getElementById('map');
    var options = {   // 지도 중앙
      center: new kakao.maps.LatLng(36.36213763445908, 127.3502067935294),
      level: 3
    };

    var map = new kakao.maps.Map(container, options);
    var markerPosition = new kakao.maps.LatLng(36.36213763445908, 127.3502067935294);   // 내 위치
    var my_position = new kakao.maps.Marker({
      position: markerPosition
    });
    my_position.setMap(map);
    var positions = [
      {
        title: '욧골 수거함',
        latlng: new kakao.maps.LatLng(36.36275992324461, 127.3485020503911)
      },
      {
        title: '봉암 수거함',
        latlng: new kakao.maps.LatLng(36.36146435746823, 127.35162158935422)
      },
      {
        title: '활골 수거함',
        latlng: new kakao.maps.LatLng(36.3639679578962, 127.35142114897597)
      },
    ];

    // 마커 이미지의 이미지 주소입니다
    var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
    var imageSize = new kakao.maps.Size(24, 35);
    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
    for (var i = 0; i < positions.length; i++) {
      // 마커를 생성합니다
      const marker = new kakao.maps.Marker({
        map: map, // 마커를 표시할 지도
        position: positions[i].latlng, // 마커를 표시할 위치
        title: positions[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됩니다
        image: markerImage // 마커 이미지 
      });

      kakao.maps.event.addListener(marker, 'click', function () {
        var resultDiv = document.getElementById('temp');
        resultDiv.innerHTML = marker.Gb;
      });
    }
  }

  _click() {
    console.log('click')
  }

  render() {
    return (
      <div>
        <div id='map'
          style={{ margin: '0 auto', width: window.innerWidth / 2, height: window.innerWidth / 3 }}>
        </div>
        <div>
          선택한 수거함: <b id='temp'></b>
        </div>
        <input type='button' value='수거요청' />
      </div>
    )
  }
}

export default map_page;