<?php

require 'header.php';   //session_start포함
$mode = $_GET['mode'] ?? '';
switch($mode){  
    // 통계질의 1
    case 'static1';
        $name = $_POST['name'] ?? '';
        ?>
        <div class="div-record ">
            <h2>통계 질의</h2>
            <p> <h5><?=$name?> 회원이 대출, 반납한 도서들의 ISBN, 제목, 대출일, 반납일</h5></p>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">ISBN</th>
                <th scope="col">제목</th>
                <th scope="col">대출일</th>
                <th scope="col">반납일</th>

                </tr>
            </thead>
            <tbody>
                <?php
                 $stmt = $conn -> prepare("
                    select p.isbn ISBN, e.title TITLE, p.daterented DATERENTED, p.datereturned DATERETURNED
                    from previousrental p join customer c 
                    on (p.cno = c.cno)
                    join ebook e
                    on p.isbn = e.isbn
                    where c.name = '{$name}'
                    UNION
                    select E.ISBN ISBN, E.title TITLE, E.DATERENTED DATERENTED, (NULL)
                    FROM customer c join ebook e
                    on C.CNO = E.CNO
                    where c.name = '{$name}'
                    ORDER BY DATERETURNED DESC
                "); 
                $stmt -> execute();
                while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
                 ?>
                 <tr class="table-light">
                     <th scope="row"><?= $row['ISBN'] ?></th>
                     <td><?= $row['TITLE'] ?></td>
                     <td><?= $row['DATERENTED'] ?></td>
                     <td><?= $row['DATERETURNED'] ??'대출중' ?></td>
     
                 </tr>
                 <?php
                 }
                 ?>
             </tbody>
        </table>
        </div> <?php
        break;
    // 통계질의 2
    case 'static2';
        ?>
        <div class="div-record ">
            <h2>통계 질의</h2>
            <p> <h5>회원이름과 대출일을 기준으로 회원 이름, 대출일, 이전 대출 권수를 출력하고, 회원의 총 이전대출 권수도 출력</h5></p>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">이름</th>
                <th scope="col">대출일</th>
                <th scope="col">대출 책 권수</th>

                </tr>
            </thead>
            <tbody>
        <?php

        $stmt = $conn -> prepare("
            SELECT 
            CASE GROUPING(NAME)
            WHEN 1 THEN '모든 회원'
            ELSE NAME END AS NAME,
            CASE GROUPING(P.daterented)
            WHEN 1 THEN NAME || '님의 총 이전 대출 권수'
            ELSE TO_CHAR(daterented, 'YYYY/MM/DD') END AS DATERENTED,
            COUNT(*) AS COUNT 
            FROM previousrental P ,CUSTOMER C
            WHERE p.cno = c.cno
            GROUP BY ROLLUP (NAME,daterented)
        "); 
        $stmt -> execute();
        while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
            // print_r($row);
            ?>
            <tr class="table-light">
                <th scope="row"><?= $row['NAME'] ?></th>
                <td><?= $row['DATERENTED'] ?></td>
                <td><?= $row['COUNT'] ?></td>

            </tr>
            <?php
            }
            ?>
            </tbody>
        </table>
        </div>
        <?php

        break;
        
    // 통계질의 3
    case 'static3';
        ?>
        <div class="div-record ">
            <h2>통계 질의</h2>
            <p> <h5>이전에 대출한 책 정보에서 대출한 회원의 이름, 책의 이름, 반납한 일자, 그 회원이 마지막으로 책을 반납한 일자를 출력</h5></p>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">이름</th>
                <th scope="col">대출일</th>
                <th scope="col">대출 책 권수</th>

                </tr>
            </thead>
            <tbody>
        <?php

        $stmt = $conn -> prepare("
            SELECT c.name name, e.title title ,P.datereturned DATERETURNED, 
            LAST_VALUE(P.datereturned) OVER
            (PARTITION BY P.CNO ORDER BY P.datereturned DESC 
            ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING) 
            AS LASTDATERETURNED
            FROM previousrental P JOIN CUSTOMER C
            ON P.CNO = C.CNO
            JOIN EBOOK E
            ON p.isbn = e.isbn
        "); 
        $stmt -> execute();
        while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
            // print_r($row);
            ?>
            <tr class="table-light">
                <th scope="row"><?= $row['TITLE'] ?></th>
                <td><?= $row['DATERETURNED'] ?></td>
                <td><?= $row['LASTDATERETURNED'] ?></td>

            </tr>
            <?php
            }
            ?>
            </tbody>
        </table>
        </div>
        <?php

        break;
    default :
?>
<div class="div-record ">
    <h2>통계</h2>
    <table class="table table-hover">
        <thead>
            <tr>
            <th scope="col">통계 질의문 </th>
            <th scope="col">링크</th>
            </tr>
        </thead>
        <tbody>
        <!-- 원하는 통계를 선택하고 이동버튼을 누르면 get방식으로 mode가 선택한 통계에 따라 달라 다른 페이지로 이동하게 되고, 선택한 통계 질의에 대한 출력을 수행한다.  -->
        <tr class="table-light">
            <td>회원이름을 이용해 회원의 대출기록을 찾아서 isbn과 대출한 책의 제목, 대출일, 반납일을 출력</td>
            <td>
                <form class=" g-3 needs-validation" method="post" action="statistics.php?mode=static1"> 
                <input type="text" class="form-control-plaintext " placeholder="회원 이름 입력" name="name">
                <button type="submit" class="btn btn-outline-primary" data-bs-dismiss="modal" >이동</button>
            </form>
            </td>
            </tr>
        <tr class="table-light">
            
            <td>회원 이름, 대출일, 이전 대출 권수를 출력하고, 회원의 총 이전대출 권수도 출력</td>
            <td>
            <a class="btn btn-outline-primary" href="statistics.php?mode=static2" role="button">이동</a>

            </td>
            
        </tr>
        <tr class="table-light">
        <td>이전에 대출한 책 정보에서 대출한 회원의 이름, 책의 이름, 반납한 일자, 그 회원이 마지막으로 책을 반납한 일자를 출력</td>
        <td>
        <a class="btn btn-outline-primary" href="statistics.php?mode=static3" role="button">이동</a>
        </td>
        </tr>
    </tbody>
    </table>
</div>

<?php

}
require './footer.php';
?>