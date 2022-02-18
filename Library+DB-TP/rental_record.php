<?php

require 'header.php';   //session_start포함
// $user = $_SESSION['LOGIN_USER'];
//일단 회원 번호부터 찾자  $cno 구하기
$stmt = $conn -> prepare("SELECT * FROM CUSTOMER WHERE NAME = '{$_SESSION['LOGIN_USER']}'"); 
$stmt -> execute(); 
$cno = '';
if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
    $cno = $row['CNO']; 
}
switch($_GET['mode']){       
    // 대출 현황
    case 'present': 
?>
        <div class="div-record ">
            <h2>
            <?php
            if($cno==0) {
                //관리자
                ?>전체 대출 목록 <?php
            }else {
                ?>대출 현황 <?php
            }
            ?>
            </h2>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">ISBN 번호</th>
                <th scope="col">제목</th>
                <th scope="col">대출 날짜</th>
                <th scope="col">반납 날짜</th>
                <th scope="col">연장 횟수</th>
                <th scope="col"><?=($cno)?'':'회원번호'?></th>
                </tr>
            </thead>
            <tbody>
                <?php
                // POSSIBLE_RESERVE는 예약 가능한 도서 즉 빌려진 도서 목록 => 중 사용자가 빌린 도서를 찾는다
                if($cno == 0) {
                    // 관리자의 경우 모든 회원의 대출 목록을 구하고
                    $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RESERVE");
                }
                else{
                    //관리작 아니면 현재 로그인된 회원의 cno에 맞는 대출목록을 구한다. 
                    $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RESERVE WHERE CNO='{$cno}'");
                }
                $stmt -> execute();
                // select결과 나온 모든 도서들을 출력한다. 
                while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
                    // print_r($row);
                    $my_isbn = $row['ISBN'];
                ?>

                <tr class="table-light">
                    <th scope="row"><?= $row['ISBN'] ?></th>
                    <td><?= $row['TITLE'] ?></td>
                    <td><?= $row['DATERENTED'] ?></td>
                    <td><?= $row['DATEDUE'] ?></td>
                    <td><?= $row['EXTTIMES'] ?></td>
                    
                    <td>
                        <?php
                        if($cno == 0) { ?>
                            <?= $row['CNO'] ?>
                        <?php
                        }else {
                        ?>
                        <!-- 반납, 연장을 수행하는 버튼 isbn을 가지고 수행해 어떤 도서를 반납, 연장할거인지 알수있게한다.  -->
                        <a class="btn btn-outline-primary" href="RR_process.php?mode=return&ISBN=<?=$my_isbn?>" role="button">반납</a> 
                        <a class="btn btn-outline-primary" href="RR_process.php?mode=extend&ISBN=<?=$my_isbn?>" role="button">연장</a> 
                        <?php
                        }?>
                        
                    </td>
                </tr>
                
        <?php
      }
      ?>
      </tbody>
            </table>    


        </div>
        <?php
      break;
    case 'reserve': 
        ?>
        <div class="div-record ">
            <h2>
            <?php
            if($cno==0) {
                //관리자
                ?>전체 예약 도서 <?php
            }else {
                ?>예약 도서 <?php
            }
            ?>    
            </h2>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">ISBN 번호</th>
                <th scope="col">제목</th>
                <th scope="col">예약 날짜</th>
                <th scope="col"><?=($cno)?'':'회원번호'?></th>
                <th scope="col"></th>
                </tr>
            </thead>
            <tbody>
                <?php
                // 사용자가 예약한 도서를 찾는다. 
                if($cno == 0) {
                    //관리자의 경우 모든 회원의 예약도서를 선택하고
                    $stmt = $conn -> prepare("SELECT * FROM RESERVE_DETAIL");
                }
                else{
                    // 관리자가 아니면 현재 로그인된 회원의 예약도서 목록을 선택한다. 
                    $stmt = $conn -> prepare("SELECT * FROM RESERVE_DETAIL WHERE CNO= '{$cno}'");
                }
                $stmt -> execute();
                while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
                    // print_r($row);
                    $my_isbn = $row['ISBN'];
                ?>

                <tr class="table-light">
                    <th scope="row"><?= $row['ISBN'] ?></th>
                    <td><?= $row['TITLE'] ?></td>
                    <td><?= $row['DATETIME'] ?></td>
                    
                    <td>
                    <?php
                        if($cno == 0) { ?>
                            <?= $row['CNO'] ?>
                        <?php
                        }else {
                        ?>
                        <a class="btn btn-outline-primary" href="book_detail.php?ISBN=<?= $row['ISBN'] ?>" role="button">정보</a> 
                        <?php
                        }
                    ?>
                    </td>
                    <td>
                    <?php
                        if($cno == 0) { ?>
                        <!-- 클릭하면 선택한 도서의 상세페이지로 넘어간다. -->
                        <a class="btn btn-outline-primary" href="book_detail.php?ISBN=<?= $row['ISBN'] ?>" role="button">정보</a> 

                        <?php
                        }else {
                        ?>
                        <!-- 로그인된 회원의 선택한 도서릐 예약을 취소한다. -->
                        <a class="btn btn-outline-primary" href="RR_process.php?ISBN=<?= $row['ISBN']?>&mode=reserve_cancle" role="button">예약 취소</a> 
                        <?php
                        }?>

                        </td>
                </tr>
                
        <?php
      }
      ?>
      </tbody>
            </table>    


        </div>
        <?php
        break;
    case 'previous': 
        ?>
        <div class="div-record ">
            <h2>
            <?php
             if($cno==0) {
                //관리자
                ?>전체 이전 대출 기록 <?php
            }else {
                ?>이전 대출 기록<?php
            }
            ?>    
            </h2>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">ISBN 번호</th>
                <th scope="col">제목</th>
                <th scope="col">대출 날짜</th>
                <th scope="col">반납 날짜</th>
                <th scope="col"><?=($cno)?'':'회원번호'?></th>
                </tr>
            </thead>
            <tbody>
                <?php
                if($cno == 0) {
                    // 모든 회원의 이전대출기록 목록 선택
                    $stmt = $conn -> prepare("SELECT * FROM PREVIOUS_RENTAL_DETAIL");
                }
                else{
                    //로그인도니 회원의 이전 대출 기록 목록 선택
                    $stmt = $conn -> prepare("SELECT * FROM PREVIOUS_RENTAL_DETAIL WHERE CNO= '{$cno}'");
                }
                // 사용자가 빌렸던 도서를 찾는다
               
                $stmt -> execute();
                while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
                    // print_r($row);
                    $my_isbn = $row['ISBN'];
                ?>

                <tr class="table-light">
                    <th scope="row"><?= $row['ISBN'] ?></th>
                    <td><?= $row['TITLE'] ?></td>
                    <td><?= $row['DATERENTED'] ?></td>
                    <td><?= $row['DATERETURNED'] ?></td>
                    
                    <td>
                    <?php
                        if($cno == 0) { ?>
                            <?= $row['CNO'] ?>
                        <?php
                        }else {
                        ?>
                        <a class="btn btn-outline-primary" href="book_detail.php?ISBN=<?= $row['ISBN'] ?>" role="button">정보</a> 
                        <?php
                        }?>
                     
                    </td>
                </tr>
                
        <?php
      }
      ?>
      </tbody>
            </table>    


        </div>
        <?php
        break;
}
   require './footer.php';
?>