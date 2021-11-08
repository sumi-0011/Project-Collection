/*global kakao*/
import React, { Component } from 'react';

class map_page extends Component {
  constructor(props) {
    super(props)
    this.state = {
    }
  }
  componentDidMount() {
    var container = document.getElementById('map');
    var options = {
      center: new kakao.maps.LatLng(36.36213763445908, 127.3502067935294),
      level: 3
    };

    var map = new kakao.maps.Map(container, options);
    var markerPosition = new kakao.maps.LatLng(36.36213763445908, 127.3502067935294);
    var marker = new kakao.maps.Marker({
      position: markerPosition
    });
    marker.setMap(map);
    var positions = [
      {
        title: '배출 장소',
        latlng: new kakao.maps.LatLng(36.36275992324461, 127.3485020503911)
      },
      {
        title: '배출 장소',
        latlng: new kakao.maps.LatLng(36.36146435746823, 127.35162158935422)
      },
      {
        title: '배출 장소',
        latlng: new kakao.maps.LatLng(36.3639679578962, 127.35142114897597)
      },
    ];

    // 마커 이미지의 이미지 주소입니다
    var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";

    for (var i = 0; i < positions.length; i++) {

      // 마커 이미지의 이미지 크기 입니다
      var imageSize = new kakao.maps.Size(24, 35);

      // 마커 이미지를 생성합니다    
      var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

      // 마커를 생성합니다
      var marker = new kakao.maps.Marker({
        map: map, // 마커를 표시할 지도
        position: positions[i].latlng, // 마커를 표시할 위치
        title: positions[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됩니다
        image: markerImage // 마커 이미지 
      });
      
    }
  }

  // _check_location = () => {
  //   var mapContainer = document.getElementById('map'), // 지도를 표시할 div  
  //     mapOption = {
  //       center: new kakao.maps.LatLng(36.36213763445908, 127.3502067935294), // 지도의 중심좌표
  //       level: 3 // 지도의 확대 레벨
  //     };

  //   var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
  //   var markerPosition = new kakao.maps.LatLng(36.36213763445908, 127.3502067935294);
  //   var marker = new kakao.maps.Marker({
  //     position: markerPosition
  //   });
  //   marker.setMap(map);
  //   // 마커를 표시할 위치와 title 객체 배열입니다 
  //   var positions = [
  //     {
  //       title: '배출 장소',
  //       latlng: new kakao.maps.LatLng(36.36275992324461, 127.3485020503911)
  //     },
  //     {
  //       title: '배출 장소',
  //       latlng: new kakao.maps.LatLng(36.36146435746823, 127.35162158935422)
  //     },
  //     {
  //       title: '배출 장소',
  //       latlng: new kakao.maps.LatLng(36.3639679578962, 127.35142114897597)
  //     },
  //   ];

  //   // 마커 이미지의 이미지 주소입니다
  //   var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";

  //   for (var i = 0; i < positions.length; i++) {

  //     // 마커 이미지의 이미지 크기 입니다
  //     var imageSize = new kakao.maps.Size(24, 35);

  //     // 마커 이미지를 생성합니다    
  //     var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

  //     // 마커를 생성합니다
  //     var marker = new kakao.maps.Marker({
  //       map: map, // 마커를 표시할 지도
  //       position: positions[i].latlng, // 마커를 표시할 위치
  //       title: positions[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됩니다
  //       image: markerImage // 마커 이미지 
  //     });
      
  //   }
  // }

  render() {
    return (
      <div>
        <div id='map'
          style={{ margin: '0 auto', width: window.innerWidth / 2, height: window.innerWidth / 3 }}>
        </div>
        {/* <div>
          <input type='button' value='배출 장소 확인' onClick={()=>this._check_location()} />
        </div> */}
      </div>
    )
  }
}

export default map_page;