<?php
require 'header.php';

$searchWord = $_GET['searchWord'] ?? '';
//null값이면defualt로 최근순 
$sort = $_GET['sort'] ?? 'YEAR';    

?>
    <table class="table table-hover book-table">
        <thead>
            <tr>
              <th scope="col"></th>
              <th scope="col">
              <div>
                    <ul class="pagination">
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
$stmt -> execute(array($searchWord));
while ($row = $stmt -> fetch(PDO::FETCH_ASSOC)) { 
?>
            <tr>
                <td class="book_img_td">
                    <img src="<?=$row['IMG']?>" onerror="this.src='http://www.visioncyber.co.kr/rtimages/n_sub/no_detail_img.gif'" alt="이미지 없음">
                   
                
                </td>
                <td style class="book_td">
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