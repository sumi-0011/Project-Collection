<?php
// 세션을 삭제해 로그아웃을 수행한다. 

    session_start();
    session_destroy();
?>
<meta http-equiv="refresh" content="0;url=main.php" />