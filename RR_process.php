<?php
require_once 'vendor/autoload.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

session_start();

require './connect.php';
$isbn = $_GET['ISBN'];
$user = $_SESSION['LOGIN_USER'];
$cno = '';
$targetTitle = '';
//일단 회원 번호부터 찾아 $cno에 저장한다. 
$stmt = $conn -> prepare("SELECT * FROM CUSTOMER WHERE NAME = '{$user}'"); 
$stmt -> execute(); 
$cno = '';
if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
    $cno = $row['CNO']; 
} else {
    // 회원이 없다는 말?
    echo "회원이 없다고?";
}
// isbn에 맞는 도서의 제목을 찾는다 (알림창을 위해서)
$stmt = $conn -> prepare("select TITLE from ebook where isbn ={$isbn}"); 
$stmt -> execute(); 
if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
    $targetTitle = $row['TITLE']; 
}
switch($_GET['mode']){    
    // 대출 모드   
    case 'rental':  
        // 대출에 필요한 정보는 회원과 도서 ISBN
        // 1. 회원이 빌릴수 있는가? CUSTOMER_RENTAL_COUNT_VIEW = 회원들이 빌린 도서의 개수들 나타내는 뷰
        $stmt = $conn -> prepare("SELECT * FROM CUSTOMER_RENTAL_COUNT_VIEW WHERE CNO = '{$cno}'"); 
        $stmt -> execute(); 
        $flag = true;
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            //CNO와 CNT있음
            if( $row['CNT'] >= 3) {
                // 더이상 못빌림
                $flag = false;
                header("Location: book_detail.php?ISBN={$isbn}&msg={$user}님은 도서를 더 이상 빌리지 못합니다");
            }
        }
        // flag가 true인경우에만 대출이 가능하다. 
        if($flag) {
        // 대출 : ebook 을 업데이트만 해주면 된다. 그리고 이전페이지로이동한다. 
        $stmt = $conn -> prepare("UPDATE EBOOK SET CNO = :cno, EXTTIMES = :exttimes, daterented = :daterented, datedue = :datedue WHERE ISBN = :isbn"); 

        $stmt->bindParam(':cno', $new_cno);           
        $stmt->bindParam(':exttimes', $new_exttimes);           
        $stmt->bindParam(':daterented', $new_daterented);           
        $stmt->bindParam(':datedue', $new_datedue);           
        $stmt->bindParam(':isbn', $new_isbn);           
        $new_cno = $cno;           
        $new_exttimes = 0;                                             //연장횟수 초기값은0
        $new_daterented = date("y/m/d");                            //대출일은 오늘 날짜      
        $new_datedue = date("y/m/d",strtotime("+10 days"));        //반납기일은 현재로부터 10일 뒤   
        $new_isbn = $_GET['ISBN'];

        $stmt -> execute(); 

        //ebook을 업데이트후 이전페이지에서 알림창을 띄우기 위한 msg를 만든다
        header("Location: book_detail.php?ISBN={$isbn}&msg={$user}님 [{$targetTitle}] 도서를 대출하였습니다.");
        }
        break;
    case 'return': 
        // 빌려진 도서 목록에서 현재 도서를 찾아 반납할 책의 대출일을 저장한다. 
        $auto = $_GET['auto'] ?? false;
        $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RESERVE WHERE ISBN = '{$isbn}'");  
        $stmt -> execute(); 
        $daterented = '';
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            $daterented = $row['DATERENTED'];
        }
        //1. 먼저 이전 대출기록에 추가
        $stmt = $conn -> prepare("INSERT INTO previousrental VALUES (:new_isbn,:new_daterented,:new_datereturned,:new_cno)"); 
        $stmt->bindParam(':new_isbn',$new_isbn);           
        $stmt->bindParam(':new_daterented',$new_daterented);           
        $stmt->bindParam(':new_datereturned',$new_datereturned);           
        $stmt->bindParam(':new_cno',$new_cno);           
        $new_isbn = $isbn ;
        $new_daterented =  $daterented;   //대출일자       
        $new_datereturned = date("y/m/d");       //반납일자는 오늘     
        $new_cno = $cno;                         //반납한 회원         
        $stmt->execute();    

        //2. ebook에서 삭제, 대출하였다는 부분을 없애기 위해 CNO,daterented,EXTTIMES,datedue null로 바꾼다
        $stmt = $conn->prepare('UPDATE EBOOK SET CNO = NULL, EXTTIMES = NULL, daterented = NULL, datedue = NULL WHERE ISBN = :update_isbn');           
        $stmt->bindParam(':update_isbn', $update_isbn);           
        $update_isbn = $isbn;           
        $stmt->execute();           
       
        //반납한 도서가 예약이 있는 도서였던경우 첫번째 순위 사용자를 가져온다.  
        //첫번째 에약 순위 사용자을 가져오는 것
        $stmt = $conn -> prepare("
        select *
        from Resinfo
        where isbn = '{$isbn}' and RowNum <= 1
        ORDER by Datetime
        "); 
        $reserved_email = '';
        $reserved_name = '';
        $stmt -> execute(); 
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {  //예약이 있는 도서인경우
            $reserved_cno = $row['CNO'];    //예약한 회원
            $reserved_name = $row['NAME'];
            $reserved_email = $row['EMAIL'];
            
            //이메일을 보낸다. 
            require './index.php';

            //reserve테이블 업데이트
            $stmt = $conn->prepare('UPDATE reserve SET e_send_date = :e_send_date WHERE CNO = :targetcno and isbn = :targetisbn');           
            $stmt->bindParam(':e_send_date', $e_send_date);           
            $stmt->bindParam(':targetcno', $targetcno);           
            $stmt->bindParam(':targetisbn', $targetisbn);           
            $e_send_date = date("y/m/d");        
            $targetcno =$reserved_cno;  
            $targetisbn =  $isbn;   
            
            $stmt->execute();           

            // echo $reserved_email;

                header("Location: rental_record.php?mode=present&msg={$user}님 [{$targetTitle}] 도서를 반납하였습니다.&msg2={$row['NAME']}님에게 이메일을 발송하였습니다");

        }else { //예약이 없는 도서 -> 반납
                header("Location: rental_record.php?mode=present&msg={$user}님 [{$targetTitle}] 도서를 반납하였습니다.");
        }

        break;
    case 'extend': 
        echo '연장입니당';
        //이미 대출된 책들중에 회원이 대출한 책의 연장횟수를 구한다. isbn을 get으로 받아왔다
        $old_EXTTIMES = 0;
        $old_DATEDUE = '';

        $stmt = $conn -> prepare("SELECT EXTTIMES, DATEDUE FROM POSSIBLE_RESERVE WHERE CNO = '{$cno}' and isbn = '{$isbn}'"); 
        $stmt -> execute(); 
        //1. 더 연장 가능한가? 두번까지 연장가능히다
        $flag = true;
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            // 이미 연장횟수가 2번이면
            if( $row['EXTTIMES'] > 2) {
                // 더이상 연장할수 없다. 
                $flag = false;
                header("Location: rental_record.php?mode=present&msg=도서[{$targetTitle}]는 더이상 반납일자를 연장 할 수 없습니다.");
            }
            $old_EXTTIMES = $row['EXTTIMES'];
            $old_DATEDUE = DateTime::createFromFormat("y/m/d" , $row['DATEDUE'])->format('Y-m-d');  //날짜포맷변경
             
        }
        //2. 예약안되어 있는 도서인가?
        // 예약목록에 도서의 isbn이 존재하면 예약이 되어있는 도서이다. 
        $stmt = $conn -> prepare("
        select *
        from reserve 
        where isbn ={$isbn}
        "); 
        $stmt -> execute(); 
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            //예약되어있는 도서이다.
            $flag = false;
            header("Location: rental_record.php?mode=present&msg=도서[{$targetTitle}]는 예약되어있어 연장 할 수 없습니다.");
            
        }
        // flag가 true인 경우에만 예약이 가능한 상태이다. 
        if($flag) {

        
        // 3. 연장해! exttimes이랑 datedue만 바꾸면 됨
        $stmt = $conn -> prepare("UPDATE EBOOK SET EXTTIMES = :new_exttimes, datedue = :new_datedue WHERE ISBN = :new_isbn"); 
        $stmt->bindParam(':new_exttimes', $new_exttimes);           
        $stmt->bindParam(':new_datedue', $new_datedue);           
        $stmt->bindParam(':new_isbn', $new_isbn);           
        $new_exttimes = $old_EXTTIMES+1;           
        $new_datedue = date("y/m/d",strtotime("{$old_DATEDUE} +10 days",));   //10일을 더해서 다시 포맷 변경
        $new_isbn = $_GET['ISBN'];

        $stmt -> execute(); 
        header("Location: rental_record.php?mode=present&msg=도서[{$targetTitle}]의 반납일자를 연장하였습니다. 현재 {$new_exttimes}번째 연장입니다.");

        // echo ("<script>alert('연장되었습니다. 현재 {$new_exttimes}번째 연장')</script>");
        // // header("Location: book_detail.php?ISBN={$isbn}");
        // echo ('<meta http-equiv="refresh" content="0;url=rental_record.php?mode=present" />');
        }
        break;
    case 'reserve': 
        echo '예약입니당';
        // 1. 회원이 예약할 수 있는가? CUSTOMER_RENTAL_COUNT_VIEW = 회원들이 예약한 도서의 개수들이 3을 넘으면 안된다 (취소후 예약가능)
        //회원이 예약한 도서 개수 확인
        $stmt = $conn -> prepare("SELECT * FROM CUSTOMER_RESERVE_COUNT_VIEW WHERE CNO = '{$cno}'"); 
        $stmt -> execute(); 
        $flag = true;
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            //CNO와 CNT있음
            // count가 3개를 넘으면 이미 3개의 에약을 한 상태이므로 추가로 예약이 불가능하다. 
            echo 'count는 = '.$row['CNT'];
            if( $row['CNT'] >= 3) {
                $flag = false;
                header("Location: book_detail.php?ISBN={$isbn}&예약안됨");

            }
        }
        //예약가능한 경우 예약하면된다. flag = true인 경우
        //reserve 테이블에 값을 넣으면 된다
        if($flag) {
            $stmt = $conn -> prepare("INSERT INTO reserve 
            (isbn, cno, datetime) 
            VALUES (:new_isbn, :new_cno, :new_datetime)"); 
            $stmt->bindParam(':new_isbn',$new_isbn);           
            $stmt->bindParam(':new_cno',$new_cno);           
            $stmt->bindParam(':new_datetime',$new_datetime);           
            $new_isbn = $isbn;
            $new_cno =  $cno;          
            $new_datetime = date("y/m/d");       //예약일자는 오늘     
            $stmt->execute();    

            header("Location: book_detail.php?ISBN={$isbn}&msg={$user}님 [{$targetTitle}] 도서를 예약하였습니다.");
        }
        
        break;
    case 'reserve_cancle': 
        //예약 취소
        //reserve테이블에서 예약 데이터를 삭제하면된다. 
        $stmt = $conn->prepare('DELETE FROM reserve WHERE isbn = :t_isbn and cno = :t_cno');           
        $stmt->bindParam(':t_isbn', $t_isbn);           
        $stmt->bindParam(':t_cno', $t_cno);           
        $t_cno = $cno;           
        $t_isbn = $isbn;           
        $stmt->execute();
        header("Location: rental_record.php?mode=reserve&msg={$user}님 [{$targetTitle}] 도서의 예약을 취소하였습니다.");
        break;

    default:
        echo 'default';
}

?>
