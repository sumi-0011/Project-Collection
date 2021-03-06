/*global kakao*/
import React, { useEffect,useState} from 'react';
import { useForm } from "react-hook-form";
import '../css/MapView.css';
import * as getDB from "../backend/GetDB.js"
export default function MapView(){
    // 작은 window 정보
    const [detailInfo, setdetailInfo] = useState("");


    var markers = [];
    const { register, handleSubmit } = useForm();
    const onSubmit = (data) => {
        searchPlaces(data);
    }
    
    var map;
    // 검색 결과 목록이나 마커를 클릭했을 때 장소명을 표출할 인포윈도우를 생성합니다
    var infowindow = new kakao.maps.InfoWindow({zIndex:1});
    // 장소 검색 객체를 생성합니다
    var ps = new kakao.maps.services.Places();  
    // 주소-좌표 변환 객체를 생성합니다
    var geocoder = new kakao.maps.services.Geocoder();
    // 키워드 검색을 요청하는 함수입니다
    function searchPlaces(data) {

        //var keyword = document.getElementById('keyword').value;
        var keyword = data.keyword;

        if (!keyword.replace(/^\s+|\s+$/g, '')) {
            alert('키워드를 입력해주세요!');
            return false;
        }

        // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
        ps.keywordSearch( keyword, placesSearchCB); 
    }
    // 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
    function placesSearchCB(data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {

            // 정상적으로 검색이 완료됐으면
            // 검색 목록과 마커를 표출합니다
            displayPlaces(data);

            // 페이지 번호를 표출합니다
            displayPagination(pagination);

        } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

            alert('검색 결과가 존재하지 않습니다.');
            return;

        } else if (status === kakao.maps.services.Status.ERROR) {

            alert('검색 결과 중 오류가 발생했습니다.');
            return;

        }
    } 
    // 검색 결과 목록과 마커를 표출하는 함수입니다
    function displayPlaces(places) {

        var listEl = document.getElementById('placesList'), 
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(), 
        bounds = new kakao.maps.LatLngBounds(), 
        listStr = '';
        
        // 검색 결과 목록에 추가된 항목들을 제거합니다
        removeAllChildNods(listEl);

        // 지도에 표시되고 있는 마커를 제거합니다
        removeMarker();
        
        for ( var i=0; i<places.length; i++ ) {

            // 마커를 생성하고 지도에 표시합니다
            var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
                marker = addMarker(placePosition, i), 
                itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다

            // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
            // LatLngBounds 객체에 좌표를 추가합니다
            bounds.extend(placePosition);

            // 마커와 검색결과 항목에 mouseover 했을때
            // 해당 장소에 인포윈도우에 장소명을 표시합니다
            // mouseout 했을 때는 인포윈도우를 닫습니다
            (function(marker, title) {
                kakao.maps.event.addListener(marker, 'mouseover', function() {
                    displayInfowindow(marker, title);
                });

                kakao.maps.event.addListener(marker, 'mouseout', function() {
                    infowindow.close();
                });

                itemEl.onmouseover =  function () {
                    displayInfowindow(marker, title);
                };

                itemEl.onmouseout =  function () {
                    infowindow.close();
                };
            })(marker, places[i].place_name);

            fragment.appendChild(itemEl);
        }

        // 검색결과 항목들을 검색결과 목록 Elemnet에 추가합니다
        listEl.appendChild(fragment);
        menuEl.scrollTop = 0;

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        map.setBounds(bounds);
    }
    // 검색결과 항목을 Element로 반환하는 함수입니다
    function getListItem(index, places) {

        var el = document.createElement('li'),
        itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
                    '<div class="info">' +
                    '   <h5>' + places.place_name + '</h5>';

        if (places.road_address_name) {
            itemStr += '    <span>' + places.road_address_name + '</span>' +
                        '   <span class="jibun gray">' +  places.address_name  + '</span>';
        } else {
            itemStr += '    <span>' +  places.address_name  + '</span>'; 
        }
                    
        itemStr += '  <span class="tel">' + places.phone  + '</span>' +
                    '</div>';           

        el.innerHTML = itemStr;
        el.className = 'item';

        return el;
    }
    // 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
    function addMarker(position, idx, title) {
        var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
            imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
            imgOptions =  {
                spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
                spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
                offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
            },
            markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
                marker = new kakao.maps.Marker({
                position: position, // 마커의 위치
                image: markerImage 
            });

        marker.setMap(map); // 지도 위에 마커를 표출합니다
        markers.push(marker);  // 배열에 생성된 마커를 추가합니다

        return marker;
    }
    // 지도 위에 표시되고 있는 마커를 모두 제거합니다
    function removeMarker() {
        for ( var i = 0; i < markers.length; i++ ) {
            markers[i].setMap(null);
        }   
        markers = [];
        setdetailInfo("");
    }
    // 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
    function displayPagination(pagination) {
        var paginationEl = document.getElementById('pagination'),
            fragment = document.createDocumentFragment(),
            i; 

        // 기존에 추가된 페이지번호를 삭제합니다
        while (paginationEl.hasChildNodes()) {
            paginationEl.removeChild (paginationEl.lastChild);
        }

        for (i=1; i<=pagination.last; i++) {
            var el = document.createElement('a');
            el.href = "#";
            el.innerHTML = i;

            if (i===pagination.current) {
                el.className = 'on';
            } else {
                el.onclick = (function(i) {
                    return function() {
                        pagination.gotoPage(i);
                    }
                })(i);
            }

            fragment.appendChild(el);
        }
        paginationEl.appendChild(fragment);
    }

    // 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
    // 인포윈도우에 장소명을 표시합니다
    function displayInfowindow(marker, title) {
        var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

        infowindow.setContent(content);
        infowindow.open(map, marker);
    }

    // 검색결과 목록의 자식 Element를 제거하는 함수입니다
    function removeAllChildNods(el) {   
        while (el.hasChildNodes()) {
            el.removeChild (el.lastChild);
        }
    }
    //마커 생성
    function displayMarker(locPosition, message) {

        // 마커를 생성합니다
        var marker = new kakao.maps.Marker({  
            map: map, 
            position: locPosition
        }); 
        
        var iwContent = message, // 인포윈도우에 표시할 내용
            iwRemoveable = true;
    
        // 인포윈도우를 생성합니다
        var infowindow = new kakao.maps.InfoWindow({
            content : iwContent,
            removable : iwRemoveable
        });
        
        // 인포윈도우를 마커위에 표시합니다 
        infowindow.open(map, marker);
        
        // 지도 중심좌표를 접속위치로 변경합니다
        map.setCenter(locPosition);      
    }  
    // 주소로 좌표를 검색합니다
    function addressSearch(data, data2, data3, data4) {
        removeMarker();
        geocoder.addressSearch(data, function(result, status) {

            // 정상적으로 검색이 완료됐으면 
            if (status === kakao.maps.services.Status.OK) {
    
                var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                //
                //
                //
                // 아이콘 추가해줄 사람!!!!!!!!!!!!!!!!!!
                //
                //
                // 소독완료 초록색 아이콘 추가
                if (data3 =="소독완료") {
                    var imageSrc = 'https://user-images.githubusercontent.com/49177223/146444414-0071e505-4d7f-45ff-ac3c-8511cd0615ef.png', // 마커이미지의 주소입니다    
                    imageSize = new kakao.maps.Size(30, 30), // 마커이미지의 크기입니다
                    imageOption = {offset: new kakao.maps.Point(27, 69)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.

                // 선별진료소 아이콘 추가
                    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
                } else if (data3 === "선별진료소"){
                    var imageSrc = 'https://user-images.githubusercontent.com/49177223/146445481-4d5029b7-f73d-424d-a703-b134eaaa363a.png', // 마커이미지의 주소입니다    
                    imageSize = new kakao.maps.Size(30, 30), // 마커이미지의 크기입니다
                    imageOption = {offset: new kakao.maps.Point(27, 69)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.

                // 소독미완료 붉은 병균 아이콘 추가
                    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
                } else {
                    var imageSrc = 'https://user-images.githubusercontent.com/49177223/146444340-8ced6dba-29a0-4c8e-bd4a-0989a1374e03.png', // 마커이미지의 주소입니다    
                    imageSize = new kakao.maps.Size(30, 30), // 마커이미지의 크기입니다
                    imageOption = {offset: new kakao.maps.Point(27, 69)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.

                    // 마커의 이미지정보를 가지고 있는 마커이미지를 생성합니다
                    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
                }
                // 결과값으로 받은 위치를 마커로 표시합니다
                var marker = new kakao.maps.Marker({
                    map: map,
                    position: coords,
                    image: markerImage
                });
                // 인포윈도우로 장소에 대한 설명을 표시합니다
                var content= '<div style="width:150px;text-align:center;padding:6px 0;">'+data2+'</div>'
                kakao.maps.event.addListener(marker, 'mouseover', function() {
                    displayInfowindow(marker, content);
                });
                // 마커에 클릭이벤트를 등록합니다
                kakao.maps.event.addListener(marker, 'click', function() {
                    // 마커 위에 인포윈도우를 표시합니다
                    infowindow.open(map, marker);  
                    setdetailInfo(data4);

                });
                
                markers.push(marker);
            } 
        }); 
    }

    //
    function setClinicMarker() {
        getDB.getClinic(function(data){
            addressSearch(data.address, data.clinicName, "선별진료소", data);
        });
    }

    function setRouteMarker() {
        getDB.getRoute(function(data){
            addressSearch(data.address, data.name, data.complete.trim(), data);
        });
    }
    // 페이지 로딩
    useEffect(() => {
        var mapContainer = document.getElementById('map') // 지도를 표시할 div 
        var mapOption = {
                center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
                level: 3 // 지도의 확대 레벨
        }; 
        // 지도를 생성합니다    
        map = new kakao.maps.Map(mapContainer, mapOption);
    
        if (navigator.geolocation) {
    
            // GeoLocation을 이용해서 접속 위치를 얻어옵니다
            navigator.geolocation.getCurrentPosition(function(position) {
                
                var lat = position.coords.latitude, // 위도
                    lon = position.coords.longitude; // 경도
                
                var locPosition = new kakao.maps.LatLng(lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
                    message = '<div style="padding:5px;">여기에 계신가요?!</div>'; // 인포윈도우에 표시될 내용입니다
                
                // 마커와 인포윈도우를 표시합니다
                displayMarker(locPosition, message);
                    
              });
            
        } else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다
            
            var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),    
                message = 'geolocation을 사용할수 없어요..'
                
            displayMarker(locPosition, message);
        }
    }, [map]);

    return (
        <div  id="main-wrapper">
           <div className="map-container"> 
           <div id="map" > </div>
           {detailInfo !== "" && (
                <div id="subMap">
                {/* 이쪽에 해주면 됭 태욱 */}
                {                    console.log(detailInfo.clinicName)}
                {   
                    detailInfo.clinicName!==undefined ? 
                    <div>
                        <h2>{detailInfo.clinicName}</h2>
                        <p>
                            주소 : {detailInfo.address}
                        </p>
                        <p>전화 번호 :  {detailInfo.phoneNumber}</p>
                        <h3>운영시간</h3>
                        <ul>  
                            <li>{detailInfo.operationHour}</li>
                            <li>{detailInfo.operationHourSun}</li>
                            <li>{detailInfo.operationHourSun}</li>
                        </ul>

                        
                        <p>참고 :  {detailInfo.ref}</p>
                    </div> :
                        <div>
                            <h2>{detailInfo.name}</h2>
                            <p>
                                주소 : {detailInfo.address}
                            </p>
                            <p>일자 :  {detailInfo.clear}</p>
                            <p>소독 여부 :  {detailInfo.complete}</p>
                            <p>type :  {detailInfo.type}</p>
                        </div>
                }
               
                {console.log(detailInfo)}
              </div> 
           ) }
           
                </div>
        
            <div id="menu_wrap">
                    <div className="btn-container">
                        <button onClick={setClinicMarker}>선별진료소</button>
                        <button onClick={setRouteMarker}>확진자이동장소</button>
                        <button onClick={removeMarker}>CLEAR</button>
                    </div>
                    <div>
                        <form onSubmit={handleSubmit(onSubmit)}>
                        <input type="text" id="keyword" size="15" {...register("keyword")}/>
                        <button type="submit">검색하기</button> 
                        </form>
                    </div>
                <ul id="placesList"></ul>
                <div id="pagination"></div>
            </div>
        </div>
    )
}
