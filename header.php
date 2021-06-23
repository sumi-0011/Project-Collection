<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="./bootstrap.css">
        <link rel="stylesheet" href="./book.css">
    <title>Main</title>
    
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
              
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">내 서재</a>
                <div class="dropdown-menu">
                  <a class="dropdown-item" href="#">Action</a>
                  <a class="dropdown-item" href="#">Another action</a>
                  <a class="dropdown-item" href="#">Something else here</a>
                  <div class="dropdown-divider"></div>
                  <a class="dropdown-item" href="#">Separated link</a>
                </div>
              </li>
            </ul>
            <form class="d-flex">
              <input class="form-control me-sm-2" type="text" placeholder="Search">
              <button class="btn btn-secondary my-2 my-sm-0" type="submit">Search</button>
            </form>
          </div>
        </div>
      </nav>
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