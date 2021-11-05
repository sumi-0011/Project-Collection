
import numpy as np

import os
import sys

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from library.padding import my_padding

def my_filtering(src, mask, pad_type='zero'):
    print(src.shape)
    (h, w, t) = src.shape
    (f_h, f_w) = mask.shape

    #filter 확인
    print('<filter>')
    print(mask)

    # 직접 구현한 my_padding 함수를 이용
    pad_img = my_padding(src, mask,pad_type)

    dst = np.zeros((h, w))
    for row in range(h):
        for col in range(w):
            dst[row, col] = np.sum(pad_img[row:row + f_h, col:col + f_w] * mask)

    return dst