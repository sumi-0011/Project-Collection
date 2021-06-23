<?php
session_start();
var_dump( $_SESSION );

require './connect.php';


// 대출에 필요한 정보는 회원과 도서 ISBN
$isbn = $_GET['ISBN'];
$user = $_SESSION['LOGIN_USER'];

//대출할수 있는 책인가?

$stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RENTAL WHERE ISBN = '1' "); 
$stmt -> execute(); 
if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
    print_r($row);
   if($row['CNO']==NULL) {
       echo "OK";
   }
   else {
       echo "NO";
   }
}
else {
    echo "NNNNNO";
}
//1. 회원이 빌릴수 있는가?


?>
