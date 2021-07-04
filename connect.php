<?php
//데이터 베이스에 로그인 하는부분
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