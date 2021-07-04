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
            // 로그인이 되어있는지 확인한다. 

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

                  
                  <a class="dropdown-item" href="rental_record.php?mode=present">대출 현황</a>
                  <a class="dropdown-item" href="rental_record.php?mode=previous">대출 기록</a>
                  <a class="dropdown-item" href="rental_record.php?mode=reserve">예약 도서</a>
                  <?php
                  
                    $user = $_SESSION['LOGIN_USER'] ?? '';
                    if($user == 'manager') { ?>
                      <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="statistics.php">통계 정보</a>
                    <?php
                    }?>

                </div> 
              </li> 
              <!-- 로그인 로그아웃 회원가입 -->
              <li class="nav-item">
                <?php
                  if(isset($_SESSION['LOGIN_USER'])) {
                    // 로그인 값이 존재하는 경우
                    ?>
                    <button type="button" class="btn btn-primary " data-bs-toggle="modal" data-bs-target="#logoutConfirmModal">로그아웃</button> 
                    <?php
                  }
                  else{  
                    //로그인이 안되있는 경우
                    ?>
                     <a class="nav-link active" href="login.php?l_mode=autoReturn">로그인</a>
                     <!-- echo ('<meta http-equiv="refresh" content="0;url=login.php?l_mode=autoReturn" />'); -->

                     <?php
                  }
                  ?>

              </li>
            </ul>
            <!-- <form class="d-flex">
              <input class="form-control me-sm-2" type="text" placeholder="Search">
              <button class="btn btn-primary my-2 my-sm-0" type="submit">Search</button>
            </form> -->
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
$tns = "
(DESCRIPTION=
     (ADDRESS_LIST= (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521)))
     (CONNECT_DATA= (SERVICE_NAME=XE))
 ) 
";
$dsn = "oci:dbname=".$tns.";charset=utf8";
$username = 'd201902698';
$password = 'nemo0408';

try {
    $conn = new PDO($dsn, $username, $password);
    // echo $username;
} catch (PDOException $e) {     
   echo("에러 내용: ".$e -> getMessage()); 
} 
?>
        
                <!-- 로그아웃 모달 -->
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
                <a class="btn btn-primary" href="logout.php" role="button">네</a>
                <a class="btn btn-primary" href="" role="button">아니요</a>
              </div>
          </div>
    </div>


      </nav>

   
<?php
// var_dump( $_SESSION );
require './connect.php';

$up_alert = $_GET['msg'] ?? '';
$up_alert2 = $_GET['msg2'] ?? '';
?>

 <!-- alert -->
<!-- 첫번째 알림창 get방식으로 msg가 존재하면 msg를 내용으로 알림창을 띄운다. 없으면 display:none -->
 <div class="alert alert-dismissible alert-warning div-alert" style="display:<?=($up_alert)?'' :'none' ?>">
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      <strong><?=$up_alert?></strong> 
</div>
<!-- 두번째 알림창 -->
<div class="alert alert-dismissible alert-warning div-alert" style="display:<?=($up_alert2)?'' :'none' ?>">
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      <strong><?=$up_alert2?></strong> 
      <!-- <strong>Well done!</strong> You successfully read <a href="#" class="alert-link">this important alert message</a>. -->
</div>
var_dump( $_SESSION );
require './connect.php';
?>
