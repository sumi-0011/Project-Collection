import React from 'react'

function Detail({location}) {

    // location.state.postId 만 하면 undefind가 뜰수도 있으므로
    const s_postId = location.state.postId;
    
    console.log(s_postId);
    // postId에 해당하는 게시글을 불러온다. 하나만 찾기위해 find사용
    // const post = posts.find((el) => el.postId === s_postId);

    return (
        <div>
            <h1>{s_postId}</h1>
        </div>
    )
}

export default Detail;
