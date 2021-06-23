<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="./bootstrap.css">
        <link rel="stylesheet" href="./book.css">
    <title>Main</title>
    <?php
    session_start();
    ?>
</head>

<body>
    <!-- <nav class="navbar navbar-expand-lg navbar-light bg-light"> -->
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">

        <div class="container-fluid">
          <!-- 수정하던 것이 지워진다는 알림? 넣을까 -->
          <a class="navbar-brand" href="./main.php">Library</a>
          <button class="navbar-toggler collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#navbarColor03" aria-controls="navbarColor03" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
      
          <div class="navbar-collapse collapse" id="navbarColor03" style="">
            <ul class="navbar-nav me-auto">
              <li class="nav-item">
                <a class="nav-link active" href="main.php">모든 도서</a>
              </li>
              <li class="nav-item">
                <a class="nav-link active" href="book_search.html">도서 검색</a>
              </li>
            <!-- 내 서재 -->
            <?php 
            // 로그인이 되어있지 않으면 내서재를 비활성화
            $isLogin='';
            $goLogin='';
            if(!isset($_SESSION['LOGIN_USER'])){
              $isLogin = 'disabled';
              $goLogin = '    로그인이 필요합니다. ';
            }
            ?>
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle <?=$isLogin?>" 
                data-bs-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">내 서재</a>  
                <!-- <?=$goLogin?> -->
                <div class="dropdown-menu">
                  <a class="dropdown-item" href="#">대출 기록</a>
                  <a class="dropdown-item" href="#">예약 도서</a>
                  <div class="dropdown-divider"></div>
                  <a class="dropdown-item" href="#">내 정보</a>
                  <a class="dropdown-item" href="#">Separated link</a>
                </div> 
              </li> 
              <!-- 로그인 로그아웃 회원가입 -->
              <li class="nav-item">
                <?php
                  if(isset($_SESSION['LOGIN_USER'])) {
                    // 로그인 값이 존재하는 경우
                    ?>
                    <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#logoutConfirmModal">로그아웃</button> 
                    <?php
                  }
                  else{  //로그인이 안되있는 경우
                    ?>
                     <a class="nav-link active" href="login.php">로그인</a>
                     <?php
                  }
                  ?>
              </li>
            </ul>
            <form class="d-flex">
              <input class="form-control me-sm-2" type="text" placeholder="Search">
              <button class="btn btn-secondary my-2 my-sm-0" type="submit">Search</button>
            </form>
          </div>
        </div>
        

        <div class="modal fade" id="logoutConfirmModal" aria-labelledby="logoutConfirmModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="logoutConfirmModalLabel">
                    
                </h5> 
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body"> 
                  로그아웃 하시겠습니까?
              </div>
              <div class="modal-footer">
                 <!-- //세션을 삭제하고, 로그아웃 창을 나타낸다.  -->
                <a class="btn btn-warning" href="logout.php" role="button">네</a>
                <a class="btn btn-warning" href="" role="button">아니요</a>
              </div>
          </div>
    </div>


      </nav>
    <!-- modal -->
    
<?php
var_dump( $_SESSION );
require './connect.php';
?>