import React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
function PostList() {
  return (
    <div>
      <h1>Hello</h1>
      <Link
        to={{
          pathname: `/main/posts/10`,
          state: {
            postId: 10
          },
        }}
        key={10}
      >
        10
      </Link>
      <Link
        to={{
          pathname: `/main/posts/20`,
          state: {
            postId: 20
          },
        }}
        key={20}
      >
        20
      </Link>
      <Link
        to={{
          pathname: `/main/posts/30`,
          state: {
            postId: 30
          },
        }}
        key={30}
      >
        30
      </Link>
      
    </div>
  );
}

export default PostList;
 