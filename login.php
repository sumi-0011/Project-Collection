<?php
require './header.php';
$l_mode = $_GET['l_mode'] ?? '';
if($l_mode =='login') { 
    $stmt = $conn -> prepare("SELECT CNO ,PASSWD,NAME FROM CUSTOMER WHERE CNO = '{$_POST['loginId']}' AND PASSWD = '{$_POST['loginPwd']}'"); 
    $stmt -> execute(); 
    // 일치하는 것이 있으면 로그인 성공
    if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
        $_SESSION['LOGIN_USER'] =  $row['NAME']; 
        echo ('<meta http-equiv="refresh" content="0;url=main.php" />');
    }
    else {
        //로그인 실패
        echo ("<script>alert('로그인에 실패하였습니다')</script>");
        echo ('<meta http-equiv="refresh" content="0;url=login.php" />');
    }
}else {
    //로그인 폼
?>
<div class="div-login">
    <!-- process.php로 수정값, 추가값을 넘기고 mode를 같이 넘긴다.  -->
<form method="post" action="login.php?l_mode=login">
  <fieldset>
    <legend>login</legend>
    <div class="form-group row change-input-text">
      <label for="loginId" class="col-sm-2 col-form-label">id</label>
      <div class="col-sm-10">
        <input type="text"class="form-control-plaintext" id="loginId"  name="loginId" placeholder='회원번호를 입력하세요.'>
      </div>
    </div>
    <div class="form-group row change-input-text">
      <label for="loginPwd" class="col-sm-2 col-form-label">password</label>
      <div class="col-sm-10">
        <input type="text"  class="form-control-plaintext" id="loginPwd" name="loginPwd" placeholder='비밀번호를 입력하세요.'>
      </div>
    </div>
    <button type="submit" class="btn btn-primary">login</button>
    </fieldset>
</form>
</div>
<?php
}
require 'footer.php';

?>