<?php
//email을 보내기 위한 라이브러리
require_once 'vendor/autoload.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

require './connect.php';    //디비 로그인
$mode = $_GET['mode'] ?? '';    
$today = date("y/m/d"); //오늘 날짜를 y/m/d형식으로 나타내 변수에 저장한다. 

switch($mode){   
    case 'autoReturn':
        //매일 사이트를 들어와서 이 부분을 실행한다고 가정
        //반납일이 오늘보다 전이면 자동 반납. 
        //반납일 = datedue가 today보다 작은날들의 isbn을 리턴해 isbn을 기준으로 반납을 수행한다. 
        $stmt = $conn -> prepare("
        select isbn
        from ebook
        where datedue < '{$today}'
        "); 
        $stmt -> execute();
        if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
            $isbn = $row['ISBN'];
            //도서 번호가 isbn인 도서들을 반납한다. 
           // 빌려진 도서 목록에서 현재 도서를 찾아 반납할 책의 대출일을 저장한다. 
            $auto = $_GET['auto'] ?? false;
            $stmt = $conn -> prepare("SELECT * FROM POSSIBLE_RESERVE WHERE ISBN = '{$isbn}'");  
            $stmt -> execute(); 
            $daterented = '';
            $cno = '';
            if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {
                $daterented = $row['DATERENTED'];
                $cno = $row['CNO'];
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
                // header("Location: rental_record.php?mode=present&msg={$user}님 [{$targetTitle}] 도서를 반납하였습니다.&msg2={$row['NAME']}님에게 이메일을 발송하였습니다");
                echo ('<meta http-equiv="refresh" content="0;url=auto.php?mode=autoReturn" />');
            }else { //예약이 없는 도서 -> 반
                    // header("Location: rental_record.php?mode=present&msg={$user}님 [{$targetTitle}] 도서를 반납하였습니다.");
            }
            echo ('<meta http-equiv="refresh" content="0;url=auto.php?mode=autoReturn" />');
            }
        //반납이 모두 끝나면 auto_no를 TRUE로 수행해 또 자동 반납을 수행하지 않게 한다. 그리고 다시 로그인 페이지로 돌아간다. 
        // header("Location: login.php?auto_no=TRUE");
        break;
    case 'autoReserveCancle':
        //이메일을 보냈지금 하루가 지나고도 대출을 하지않아 예약이 삭제되는 경우
        //이메일을 보낸 것들중에 기한이 지난것은 삭제하고 다음것을 보낸다.
        //도서마다 예약 우선순위가 1위인 회원들의 집합 사이에서 e_send_date이 널이 아닌 즉, 이메일을 보낸 예약을 이메일 전송 날짜를 기점으로 정렬하여  * 선택
        $stmt = $conn -> prepare("select * from FIRST_RESERVE_VIEW where e_send_date is not null order by e_send_date "); 
        $stmt -> execute();
        $flag = true;
        while ($row = $stmt -> fetch(PDO::FETCH_ASSOC) and $flag) {
            // print_r($row);
            $ISBN = $row['ISBN'];                       //책 번호
            $E_SEND_DATE = $row['E_SEND_DATE'];         //전송일
            $CNO = $row['CNO'];     
            $temp= DateTime::createFromFormat("y/m/d" , $E_SEND_DATE)->format('Y-m-d');                    //예약회원
            $plus_one_day = date("y/m/d",strtotime("{$temp} +1 days",));    //전송일로부터 하루뒤, 예약의 만료 기한
            //예약의 만료기한이 넘으면
            if($plus_one_day < $today) {    //반납일자+1< 오늘 => 다음 회원에게 이메일 통보 +reserve에서 삭제
                // reserve에서 해당 예약을 삭제
                $stmt = $conn->prepare('DELETE FROM reserve WHERE isbn = :t_isbn and cno = :t_cno');           
                $stmt->bindParam(':t_isbn', $t_isbn);           
                $stmt->bindParam(':t_cno', $t_cno);           
                $t_cno = $CNO;           
                $t_isbn = $ISBN;           
                $stmt->execute();
                // 다음 예약 회원에게 이메일 통보을 통보한다. 
                $stmt = $conn -> prepare("
                    select *
                    from Resinfo
                    where isbn = '{$ISBN}' and RowNum <= 1
                    ORDER by Datetime
                "); 
                $reserved_email = '';
                $reserved_name = '';
                $stmt -> execute(); 
                if ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) {  //예약이 있는 도서인경우
                    $reserved_cno = $row['CNO'];                //예약한 회원
                    $reserved_name = $row['NAME'];
                    $reserved_email = $row['EMAIL'];
                    // echo "통보완료".$reserved_cno;
                   
                    //reserve테이블 수정한다. 지금 이메일을 보내는 것이기 때문에 e_send_date을 지금 날짜로 설정한다. 
                    $stmt = $conn->prepare('UPDATE reserve SET e_send_date = :e_send_date WHERE CNO = :targetcno and isbn = :targetisbn');           
                    $stmt->bindParam(':e_send_date', $e_send_date);           
                    $stmt->bindParam(':targetcno', $targetcno);           
                    $stmt->bindParam(':targetisbn', $targetisbn);           
                    $e_send_date = date("y/m/d");        
                    $targetcno =$reserved_cno;  
                    $targetisbn =  $ISBN;   
                    $stmt->execute();

                     //이메일을 보낸다.  PHPMAILER 라이브러리를 사용
                     $mail = new PHPMailer(true);
                     $mail->isSMTP();

                     $name=$reserved_name ?? 'manager';
                     $email=$reserved_email ?? 'selina2000@naver.com';
                     $isbn=$ISBN ?? '000';
                     $returned= date("y/m/d") ?? '[날짜]';
                     try {
                     
                     // 구글 smtp 설정
                     $mail->Host = "smtp.gmail.com";
                     $mail->SMTPAuth = true;
                     $mail->Port = 465;
                     $mail->SMTPSecure = "ssl";
                     $mail->Username = "selina202015@gmail.com";
                     $mail->Password ="Nemo0408@@";
                     $mail->CharSet = 'utf-8';
                     $mail->Encoding = "base64";
                     $mail->setFrom('selina202015@gmail.com', 'Library');
                     
                     // 받는 사람
                     // $mail->AddAddress($email, $name);
                     $mail->AddAddress('selina2000@naver.com', $name);
                     $mail->isHTML(true);
                     
                     $mail->Subject = '도서번호 ['.$ISBN.'] 대출이 가능합니다.';
                     $mail->Body = $name.'회원님!<br>'.$returned.'로부터 하루안에 도서번호 ['.$ISBN.'] 대출이 가능합니다.';
                     $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';
                     $mail->Send();
                     
                     } 
                    //  예외처리
                     catch (phpmailerException $e) {
                         echo $e->errorMessage();
                     } 
                     catch (Exception $e) {
                         echo $e->getMessage();
                     }
                     header("Location: auto.php?mode=autoReserveCancle");
                    //  $flag = false;
                     break;
                    // echo "통보완료".$reserved_cno;
                }
            }
            //더이상 만료되는 예약이 없으면 로그인 화면으로 돌아간다, 다시 실행되는 것을 막기위해 auto_no를 True로 설정하여 이동한다. 
            header("Location: login.php?auto_no=TRUE");
            
        }
        header("Location: login.php?auto_no=TRUE");
        break;
    default:
        echo 'default';
        break;
}

