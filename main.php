<?php
require 'header.php';


$searchWord = $_GET['searchWord'] ?? '';
//null값이면defualt로 최근순으로 정렬한다. 
$sort = $_GET['sort'] ?? 'YEAR';    

?>
    <table class="table table-hover book-table">
        <thead>
            <tr>
              <th scope="col"></th>
              <th scope="col">
              <div>
                    <ul class="pagination">
                    <!-- 어떤것을 기준으로 정렬할 것인지를 확인, 현재 정렬방법을 active로 표시한다.  -->
                        <li class="page-item <?=($sort=='YEAR')?'active':''?>">
                        <a class="page-link" href="main.php?sort=YEAR">최근순</a>
                        </li>
                        <li class="page-item <?=($sort=='TITLE')?'active':''?>">
                        <a class="page-link" href="main.php?sort=TITLE">제목순</a>
                        </li>
                        <li class="page-item <?=($sort=='AUTHOR')?'active':''?>">
                        <a class="page-link" href="main.php?sort=AUTHOR">저자순</a>
                        </li>
                    </ul>
                    </div>
                </th>
            </tr>
          </thead>
        <tbody>
<?php 

$sort = ($sort=='YEAR')?'BYEAR DESC':$sort;

// 순서대로 정렬하되 null인값은 마지막에 오게 정렬한다. 
$stmt = $conn -> prepare("SELECT *
FROM EBOOK_DETAIL_VEIW  WHERE LOWER(TITLE) LIKE '%' || :searchWord || '%' ORDER BY {$sort} NULLS LAST");

// 다른것과 달리 년도는 최근순이므로 역순이여야 한다. 

$sort = ($sort=='YEAR')?'YEAR DESC':$sort;

// 순서대로 정렬하되 null인값은 마지막에 오게 정렬한다. 
// ebook과 authors를 join한 모든 도서들을 화면에 띄운다. 
$stmt = $conn -> prepare("SELECT EBOOK.ISBN, EBOOK.TITLE, EBOOK.PUBLISHER,EBOOK.YEAR,EBOOK.IMG,authors.author
FROM EBOOK LEFT JOIN AUTHORS
ON EBOOK.ISBN = authors.isbn  WHERE LOWER(TITLE) LIKE '%' || :searchWord || '%' ORDER BY {$sort} NULLS LAST");
$stmt -> execute(array($searchWord));
while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
?>
            <tr>
                
                <td class="book_img_td">
                    <img src="<?=$row['IMG']?>" onerror="this.src='http://www.visioncyber.co.kr/rtimages/n_sub/no_detail_img.gif'" alt="이미지 없음">
                   
                
                </td>
                <td style class="book_td">
                <!-- 제목을 클릭하면 상세페이지로 이동하게 하기 위해 a태그를 사용한다.  -->
                    <h3><a href="book_detail.php?ISBN=<?= $row['ISBN'] ?>"><?= $row['TITLE'] ?></a></h3>
                    <br>
                    <p>저자 : <?= $row['AUTHOR'] ?></p>
                    <p>출판사 : <?= $row['PUBLISHER'] ?></p>
                    <p>발행일 : <?= $row['BYEAR'] ?></p>    
                    
                </td>
                
            </tr>
<?php 
}
?>       
            
        </tbody>
    </table>
    <?php
    
   require './footer.php';
   ?>