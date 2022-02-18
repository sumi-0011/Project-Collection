<?php
require 'header.php';   //nav까지 있음

$ISBN = $_GET['ISBN'] ?? ''; 
$mode = $_GET['mode'] ?? '';  //insert or modify
$bookName = ''; 
$publisher = ''; 
$year = ''; 
$author= '';
$IMG = '';
if($mode == 'modify') {
    $stmt = $conn -> prepare("SELECT * FROM EBOOK_DETAIL_VEIW  WHERE ISBN = {$ISBN} "); 
    $stmt -> execute(); 
    
    if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
        $bookName = $row['TITLE'];     
        $publisher = $row['PUBLISHER'];     
        // 날짜는 format형식 변환
        $year = DateTime::createFromFormat("y/m/d" , $row['BYEAR'])->format('Y-m-d');
        $author = $row['AUTHOR']; 
        $IMG =  $row['IMG']; 
    }
}

?>
<div class="div-change">
    <!-- process.php로 수정값, 추가값을 넘기고 mode를 같이 넘긴다.  -->
<form method="post" action="process.php?mode=<?= $mode ?>&ISBN=<?=$ISBN?>">
  <fieldset>
    <legend>도서 수정</legend>
    <div class="form-group row change-input-text">
      <label for="changeBookName" class="col-sm-2 col-form-label">제목</label>
      <div class="col-sm-10">
        <input type="text"class="form-control-plaintext" id="changeBookName" value="<?=$bookName?>" name ="changeBookName">
      </div>
    </div>
    <div class="form-group row change-input-text">
      <label for="changeAuthor" class="col-sm-2 col-form-label">저자</label>
      <div class="col-sm-10">
        <input type="text"  class="form-control-plaintext" id="changeAuthor" value="<?=$author?>" name ="changeAuthor">
      </div>
    </div>
    <div class="form-group row change-input-text">
      <label for="changePublisher" class="col-sm-2 col-form-label">출판사</label>
      <div class="col-sm-10">
        <input type="text" class="form-control-plaintext" id="changePublisher" value="<?=$publisher?>" name ="changePublisher">
      </div>
    </div>
    <div class="form-group row change-input-text">
      <label for="changeYear" class="col-sm-2 col-form-label">발행일자</label>
      <div class="col-sm-10">
        <input type="date" class="form-control-plaintext" id="changeYear" value="<?=$year?>" name ="changeYear">
        
      </div>
    </div>
    <div class="form-group row change-input-text">
      <label for="changeImg" class="col-sm-2 col-form-label">이미지 주소</label>
      <div class="col-sm-10">
        <input type="text"  class="form-control-plaintext" id="changeImg" value="<?=$IMG?>" name ="changeImg">
      </div>
    </div>
    <button type="submit" class="btn btn-primary"><?= $mode == 'insert' ? '등록' : '수정'?></button>
    </fieldset>
</form>
</div>


<?php
   require './footer.php';
?>