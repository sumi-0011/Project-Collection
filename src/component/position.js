/*global kakao*/
import React, { Component } from 'react';

class position extends Component {
  constructor(props) {
    super(props)
    this.state = {
      select_position: '',
    }
  }
  componentDidMount() {
    var container = document.getElementById('map');
    var options = {   // 지도 중앙
      center: new kakao.maps.LatLng(36.36275992324461, 127.3485020503911),
      level: 3
    };
    var map = new kakao.maps.Map(container, options);
    var positions = [
      {
        title: '욧골 수거함',
        latlng: new kakao.maps.LatLng(36.36275992324461, 127.3485020503911)
      },
    ];
    // 마커 이미지의 이미지 주소입니다
    var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
    var imageSize = new kakao.maps.Size(24, 35);
    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
    const marker = new kakao.maps.Marker({
      map: map,
      position: positions[0].latlng,
      title: positions[0].title,
      image: markerImage,
    })
  }

  render() {
    return (
      <div id='map'
        style={{margin: '0 auto', width: '500px', height: '500px' }}>
      </div>
    )
  }
}

export default position;