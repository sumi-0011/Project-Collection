<?php
require_once 'vendor/autoload.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

// require './connect.php';    //디비랑 연결
// 실질적으로 이메일을 보내는 부분, $name이 받는사람 $email이 받는 사람 이메일을 뜻한다. 
$mail = new PHPMailer(true);
$mail->isSMTP();

$name=$reserved_name ?? 'manager';
$email=$reserved_email ?? 'selina2000@naver.com';
$isbn=$isbn ?? '000';
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
$mail->AddAddress($email, $name);
// $mail->AddAddress('selina2000@naver.com', $name);
$mail->isHTML(true);

$mail->Subject = '도서번호 ['.$isbn.'] 대출이 가능합니다.';
$mail->Body = $name.'회원님!<br>'.$returned.'로부터 하루안에 도서번호 ['.$isbn.'] 대출이 가능합니다.';
$mail->AltBody = 'This is the body in plain text for non-HTML mail clients';
$mail->Send();

} 
catch (phpmailerException $e) {
    echo $e->errorMessage();
} 
catch (Exception $e) {
    echo $e->getMessage();
}