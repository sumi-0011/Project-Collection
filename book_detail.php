<?php
require 'header.php';
// ISBN을 가지고 원하는 도서의 상세한 정보들을 가져온다.
$ISBN = $_GET['ISBN'];
$stmt = $conn -> prepare("SELECT * FROM EBOOK_DETAIL_VEIW WHERE ISBN = {$ISBN} "); 
$ISBN = $_GET['ISBN'];

$possible_rental = TRUE; // 대출가능한 도서인지 확인
$possible_reserve = TRUE; //예약가능한 도서인지 ? = 내가 대출한 도서인지
$user = $_SESSION['LOGIN_USER'] ?? '';

$stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RENTAL WHERE ISBN = {$ISBN}"); 
$stmt -> execute();
$cno = '';
if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {  // 빌릴수 있는 도서인경우 대출 가능, 예약 불가능
  $stmt = $conn -> prepare("SELECT * FROM reserve WHERE ISBN = {$ISBN}"); 
  $stmt -> execute();
  // echo "ddd";

  if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {  //만약 예약되어서 기다리는 도서이면 대출 불가능
    $possible_rental = FALSE;
    $possible_reserve = FALSE;
  }
  else {
    //그렇지 않으면 대출은 가능하고 예약은 불가능하다.
    $possible_rental = TRUE;
    $possible_reserve = FALSE;
    $error = "대출부터해야댕";
  }
  
}
else {  
  // 빌릴수 없는 도서는 예약이가능할 수도 있다. 
    $possible_rental = FALSE; //빌릴수 없는 도서는 대출은 불가능함, 예약의 가능 여부만 따지면 된다. 
    //일단 회원 번호를 회원 이름을 이용해 찾는다. 
  
    if($_SESSION['LOGIN_USER'] ?? '') {   //로그인 되어있다면
      // echo $_SESSION['LOGIN_USER'];
      $stmt = $conn -> prepare("SELECT * FROM CUSTOMER WHERE NAME = '{$_SESSION['LOGIN_USER']}'"); 
      $stmt -> execute(); 
      if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
          $cno = $row['CNO']; 
          //예약 가능한지 확인
          //1. 이미 대출한 도서인지
          $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RESERVE WHERE CNO= '{$cno}' and isbn ='{$ISBN}'");
          $stmt -> execute();
          // 내가 대출한 책이면 예약이 불가능하다. 
          if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
            $possible_reserve = FALSE;
            $error = "이미 내가 대출한책";
            // echo '예약 불가능';
          }
          //2. 이 도서를 이미 예약했는지
          $stmt = $conn -> prepare("SELECT * FROM reserve WHERE CNO= '{$cno}' and isbn ='{$ISBN}'");
          $stmt -> execute();
          //이미 예약한 책이면 예약이 물가능하다
          if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
            $possible_reserve = FALSE;
            $error = "이미 내가 예약한 책";
            // echo '예약 불가능';
          }
          
      }
    }
    else {
      $possible_reserve = FALSE;
    }
}
// echo $user;
if($user!='') {
  $error = "     ~";
}
else {
  $possible_rental = FALSE;
  $possible_reserve = FALSE;
}
if( $user =='manager') {
  $possible_rental = FALSE;
  $possible_reserve = FALSE;
}
// ISBN을 가지고 원하는 도서의 상세한 정보들을 가져온다.
$ISBN = $_GET['ISBN'];
 // 대출가능한 도서인지 확인
 $possible_rental = TRUE;
 $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RENTAL WHERE ISBN = {$ISBN}"); 
 $stmt -> execute();
 if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
   // 빌릴수 있는 도서인경우
    //  echo $possible_rental;
    $possible_rental = TRUE;
 }
 else {
   // 빌릴수 없는 도서
     $possible_rental = FALSE; //버튼 디스플레이를 위해서
 }
 

// ISBN을 가지고 원하는 도서의 상세한 정보들을 가져온다.

$stmt = $conn -> prepare("SELECT EBOOK.ISBN, EBOOK.TITLE, EBOOK.PUBLISHER,EBOOK.YEAR,EBOOK.IMG,authors.author FROM EBOOK LEFT JOIN AUTHORS ON EBOOK.ISBN = authors.isbn  WHERE EBOOK.ISBN = {$ISBN} "); 
$stmt -> execute(); 
$bookName = ''; 
$publisher = ''; 
$year = ''; 
$author= '';


if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
     $bookName = $row['TITLE'];     
     $publisher = $row['PUBLISHER'];     
     $year = $row['BYEAR']; 
     $author = $row['AUTHOR']; 

    $bookName = $row['TITLE'];     
    $publisher = $row['PUBLISHER'];     
    $year = $row['YEAR']; 
    $author = $row['AUTHOR']; 
     
   
?>
     
     <div class="div-detail">
      <h2><?=$bookName?></h2>
      <hr>
      <div class="detail">
        <div class="detail-book-img">
          <img src=<?=$row['IMG']?> onerror="this.src='http://www.visioncyber.co.kr/rtimages/n_sub/no_detail_img.gif'" alt="이미지 없음">
        </div>
        <div class="detail-box">
          <p> 

          <!-- 대출, 예약 가능 여부에 따라 태그들을 화면에 표시한다.  -->
            <span class="badge bg-primary" style="display:<?=($possible_rental)?'' :'none' ?>">대출 가능</span>
            <span class="badge bg-warning " style="display:<?=($possible_rental)?'none' :'' ?>">대출 불가능</span>
            <span class="badge bg-primary" style="display:<?=($possible_reserve)?'' :'none' ?>">예약 가능</span>
            <span class="badge bg-warning " style="display:<?=($possible_reserve)?'none' :'' ?>">예약 불가능</span>
            

          </p>
          <!-- 위의 php코드 부분에서 디비에서 정보를 찾아 웹에 출력해준다.  -->
            <div  class="detail-word">
              <p>책 이름: <?=$bookName?></p>
              <p>저자 :  <?=$author?></p>  
              <p>출판사 :  <?=$publisher?></p>  
              <p>발행 일자 :  <?=$year?></p>
            </div>
<?php  
}  
?> 

            <div class="detail-button">
              <!--  <a> 요소에 버튼 클래스를 사용할 경우, 이런 링크에는 role="button" 이라는 역할을 줌 -->

                <!-- $possible_rental가 FALSE 즉 대출을 할수 없는 도서의 경우 대출버튼을 비활성화하였다. -->
              <a class="btn btn-outline-primary" href="RR_process.php?mode=rental&ISBN=<?=$ISBN?>" role="button" 
              style="pointer-events:<?=($possible_rental)?'' :'none' ?>">대출</a>
              <!-- 대출 가능이면 예약 불가능! 예약 버튼 비활성화 + 이미 내가대출한 책이거나 예약한거면 불가능 -->
              <a class="btn btn-outline-primary" href="RR_process.php?mode=reserve&ISBN=<?=$ISBN?>" role="button" 
              style="pointer-events:<?=($possible_reserve)?'' :'none' ?>">예약</a>

              
            </div>
        </div>
<!--         
        <div class="modal fade" id="deleteConfirmModal" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="deleteConfirmModalLabel">
                </h5> 
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body"> 
                  위의 책을 삭제하시겠습니까? 
              </div>
              <div class="modal-footer">
                <form action="process.php?mode=delete" method="post" class="row"> -->
                  <!-- 삭제 할때 ISBN를 가져가서 삭제하기 위해 숨겨둔 값 -->
                  <!-- <input type="hidden" name="ISBN" value=""> 
                  <button type="submit" class="btn btn-danger">삭제</button> 
                </form> 
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" >취소</button>
              </div>
          </div> -->
    </div>
</div>

      </div>
    </div>
      



   <?php
   
   require './footer.php';
   ?>