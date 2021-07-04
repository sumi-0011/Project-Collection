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
        $stmt = $dbh -> prepare('UPDATE EBOOK_DETAIL_VEIW SET TITLE = :title, PUBLISHER = :publisher, BYEAR = :year,AUTHOR = :author,IMG = :img WHERE ISBN = :isbn');           
        $stmt->bindParam(':title',$title);           
        $stmt->bindParam(':publisher',$publisher);           
        $stmt->bindParam(':year',$year);           
        $stmt->bindParam(':author',$author);         
        $stmt->bindParam(':img',$img);         
        $stmt->bindParam(':isbn',$isbn);         
        $title = $_POST['changeBookName'];           
        $publisher = $_POST['changePublisher'];           
        $year = $_POST['changeYear'];   
        $author = $_POST['changeAuthor'];   
        $img = $_POST['changeImg'];   
        $isbn = $_GET['ISBN'];    
        $stmt->execute();           
        header("Location: main.php");           

        break;
}

?>