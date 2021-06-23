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
$dbh = new PDO($dsn, $username, $password);

switch($_GET['mode']){ 
    case 'modify':
        $ISBN = $_GET['ISBN'];
        $stmt = $conn -> prepare("SELECT EBOOK.ISBN, EBOOK.TITLE, EBOOK.PUBLISHER,EBOOK.YEAR,EBOOK.IMG,authors.author FROM EBOOK LEFT JOIN AUTHORS ON EBOOK.ISBN = authors.isbn  WHERE EBOOK.ISBN = {$ISBN} "); 
        $stmt -> execute(); 
        
        break;
}

?>