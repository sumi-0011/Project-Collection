<?php
require './header.php';

$mode = $_GET['mode'] ?? '';
if($mode == 'search') {
    //검색한 내용들을 출력하는 부분
    ?>
    <div class="div-record ">
            <h2>검색</h2>
            <table class="table table-hover">
            <thead>
                <tr>
                <th scope="col">ISBN 번호</th>
                <th scope="col">제목</th>
                <th scope="col">저자</th>
                <th scope="col">출판사</th>
                <th scope="col">발행년도</th>
                <th scope="col">책 정보</th>
                </tr>
            </thead>
            <tbody>
    <?php
    //입력값
    $inputText1 =  $_POST['inputText1'];
    $inputText2 = $_POST['inputText2'];
    $inputText3 = $_POST['inputText3'];
    $inputText4 = $_POST['inputText4'];
    $inputCount=4-(!$inputText1+!$inputText2+!$inputText3+!$inputText4);
    // echo '<br>Counr = '.$inputCount;
    //and or not배열에 넣는다. 
    $arr = array(); //ex) title , inputText, operation
    //첫번째줄 입력
    if($inputText1) {   //inputTitle 입력했고
        array_push($arr,[
            $_POST['input1'],$_POST['inputText1'],$_POST['operation1']
        ]);
    }
    //두번째줄 입력
    if($inputText2) {  
        array_push($arr,[
            $_POST['input2'],$_POST['inputText2'],$_POST['operation2']
        ]);
    }
    //세번째줄 입력
    if($inputText3) {   
        array_push($arr,[
            $_POST['input3'],$_POST['inputText3'],$_POST['operation3']
        ]);
    }
    //네번째줄 입력
    if($inputText4) {   
        array_push($arr,[
            $_POST['input4'],$_POST['inputText4'],'null'
        ]);
    }
    // print_r($arr);
    //0개 입력
    switch($inputCount){  
        case 0:
            //아무것도 입력하지 않은 경우 존재하는 모든 도서를 출력한다. 
            $stmt = $conn -> prepare("
                select *
                from EBOOK_DETAIL_VIEW
                ORDER BY ISBN
            ");
            $stmt -> execute();
            break;
        case 1:
            //and or not 연산자 없이 한 입력값으로 검색한다. 
            $stmt = $conn -> prepare("
            select * 
            from EBOOK_DETAIL_VIEW
            where {$arr[0][0]} like '%' || '{$arr[0][1]}' || '%'
            ");
            $stmt -> execute();
            break;
        case 2:
            //2개 입력 arr의 길이는 2 
            //title , inputText, operation $arr[0][0]
            //operation은 union등을 의미한다. 
            //연산자 조건에 따라 집합들을 합친다
            $stmt = $conn -> prepare("
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[0][0]} like '%' || '{$arr[0][1]}' || '%'
            {$arr[0][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[1][0]} like '%' || '{$arr[1][1]}' || '%'
            ");
            $stmt -> execute();
            break;
        case 3:
             //3개 입력 arr의 길이는 3
             //case 2와 같다
            $stmt = $conn -> prepare("
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[0][0]} like '%' || '{$arr[0][1]}' || '%'
            {$arr[0][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[1][0]} like '%' || '{$arr[1][1]}' || '%'
            {$arr[1][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[2][0]} like '%' || '{$arr[2][1]}' || '%'
            ");
            $stmt -> execute();
            break;
        case 4:
            //case 2와 같다
             //4개 입력 arr의 길이는 4
            $stmt = $conn -> prepare("
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[0][0]} like '%' || '{$arr[0][1]}' || '%'
            {$arr[0][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[1][0]} like '%' || '{$arr[1][1]}' || '%'
            {$arr[1][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[2][0]} like '%' || '{$arr[2][1]}' || '%'
            {$arr[2][2]}
            select * 
            from EBOOK_DETAIL_VEIW
            where {$arr[3][0]} like '%' || '{$arr[3][1]}' || '%'
            ");
            $stmt -> execute();
            break;
        default:
            echo "default";
            break;
        // 그냥리턴
    }
    while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
        // print_r($row);
        $my_isbn = $row['ISBN'];
        ?>
        <tr class="table-light">
            <th scope="row"><?= $row['ISBN'] ?></th>
            <td><?= $row['TITLE'] ?></td>
            <td><?= $row['AUTHOR'] ?></td>
            <td><?= $row['PUBLISHER'] ?></td>
            <td><?= $row['BYEAR'] ?></td>
            <td>
                <a class="btn btn-outline-primary" href="book_detail.php?ISBN=<?= $row['ISBN'] ?>" role="button">정보</a> 
            </td>
        </tr>
        <?php
        }?>
        </tbody>
</table>
</div>
        <?php
    
   

}
else {
    //검색할 내용들을 입력하는 부분
?>
    <div class="div-search">
        <form action="./book_search.php?mode=search" method="post">
            <!-- 도서 검색: 서명, 저자, 출판사, 발행년도 범위에 대한 검색 조건을 이용하여 도서
정보를 검색할 수 있다. 하나의 검색 조건을 이용하여 검색하거나, 각 검색 조건들
간에 AND 나 OR, NOT 연산을 이용하여 검색할 수 있다. -->
            <div class="form-group">
                <select name="input1" id="" class="col-form-label">
                    <option value="TITLE">도서 이름</option>
                    <option value="AUTHOR">저자</option>
                    <option value="PUBLISHER">출판사</option>
                    <option value="BYEAR">발행년도</option>
                </select>
              
                <input type="text" class="form-control" placeholder=" input" id="inputText1" name="inputText1">
            </div>
            <select name="operation1" id="" class="col-form-label">
                    <option value="INTERSECT">AND</option>
                    <option value="UNION">OR</option>
                    <option value="MINUS">NOT</option>
                </select>
            <div class="form-group">
                <select name="input2" id="" class="col-form-label">
                    <option value="AUTHOR">저자</option>
                     <option value="TITLE">도서 이름</option>
                    <option value="PUBLISHER">출판사</option>
                    <option value="BYEAR">발행년도</option>
                </select>
               
                <input type="text" class="form-control" placeholder=" input" id="inputText2" name="inputText2" >
            </div>
            <select name="operation2" id="" class="col-form-label">
                    <option value="INTERSECT">AND</option>
                    <option value="UNION">OR</option>
                    <option value="MINUS">NOT</option>
                </select>
            <div class="form-group">
                <select name="input3" id="" class="col-form-label">
                    <option value="PUBLISHER">출판사</option>
                    <option value="TITLE">도서 이름</option>
                    <option value="AUTHOR">저자</option>
                    <option value="BYEAR">발행년도</option>
                </select>
               
                <input type="text" class="form-control" placeholder=" input" id="inputText3" name="inputText3">
            </div>
            <select name="operation3" id="" class="col-form-label">
                    <option value="INTERSECT">AND</option>
                    <option value="UNION">OR</option>
                    <option value="MINUS">NOT</option>
                </select>
            <div class="form-group">
                <select name="input4" id="" class="col-form-label">
                    <option value="BYEAR">발행년도</option>
                    <option value="TITLE">도서 이름</option>
                    <option value="AUTHOR">저자</option>
                    <option value="PUBLISHER">출판사</option>
                </select>
               
                <input type="text" class="form-control" placeholder=" input" id="inputText4" name="inputText4">
            </div>

            <button type="submit" class="btn btn-primary mt-4">Submit</button>

        </form>
    </div>





    <?php
    
}
require './footer.php';
?>