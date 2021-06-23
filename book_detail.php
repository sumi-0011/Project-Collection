<?php
require 'header.php';
// ISBN을 가지고 원하는 도서의 상세한 정보들을 가져온다.
$ISBN = $_GET['ISBN'];
$stmt = $conn -> prepare("SELECT * FROM EBOOK_DETAIL_VEIW WHERE ISBN = {$ISBN} "); 
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
            <span class="badge bg-primary">대출 가능</span>
            <span class="badge bg-warning">대출 불가능</span>
          </p>

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
              <button type="button" class="btn btn-outline-primary">대출</button>
              <button type="button" class="btn btn-outline-primary">예약</button>

              <a class="btn btn-warning" href="change.php?mode=modify&ISBN=<?=$ISBN?>" role="button">수정</a>
              <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteConfirmModal">삭제</button> 

            </div>
        </div>
        
        <div class="modal fade" id="deleteConfirmModal" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="deleteConfirmModalLabel">
                    <?= $bookName ?>
                </h5> 
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body"> 
                  위의 책을 삭제하시겠습니까? 
              </div>
              <div class="modal-footer">
                <form action="process.php?mode=delete" method="post" class="row">
                  <!-- 삭제 할때 ISBN를 가져가서 삭제하기 위해 숨겨둔 값 -->
                  <input type="hidden" name="ISBN" value="<?= $ISBN ?>"> 
                  <button type="submit" class="btn btn-danger">삭제</button> 
                </form> 
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" >취소</button>
              </div>
          </div>
    </div>
</div>

      </div>
    </div>
      



   <?php
   require './footer.php';
   ?>