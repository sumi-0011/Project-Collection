import numpy as np
import cv2
from library.filtering import my_filtering

def get_DoG_filter(fsize,sigma=1):
    #TODO
    #DoG mask완성

    y,x = np.mgrid[-(fsize//2):(fsize//2)+1,-(fsize//2):(fsize//2)+1]

    DoG_x = (-x/sigma**2) *np.exp(-((x**2 + y**2)/(2*sigma**2)))
    DoG_y = (-y/sigma**2) *np.exp(-((x**2 + y**2)/(2*sigma**2)))

    return DoG_x,DoG_y

def main():
    src = cv2.imread('../image/Lena.png', cv2.IMREAD_GRAYSCALE)

    #DoG mask 생성.
    DoG_x, DoG_y = get_DoG_filter(fsize=3,sigma=1)

    dst_x = my_filtering(src, DoG_x, 'zero')     # 𝐷𝑜𝐺(x) = 𝐺′(𝑥) ∗ 𝐼
    dst_y = my_filtering(src, DoG_y, 'zero')     # 𝐷𝑜𝐺(y) = 𝐺′(y) ∗ 𝐼


    # 𝐷𝑜𝐺 𝑥 ,𝐷𝑜𝐺(𝑦)의 magnitude를 계산.
    dst = np.sqrt(dst_x**2+dst_y**2)
    print('dst',dst)
    print('dst/255' ,dst/255)

    # 0~1 사이의 값으로 변경 후 0~255로 변경
    dst = ((dst - np.min(dst))/np.max(dst-np.min(dst))*255+0.5).astype(np.uint8)
    print('dst',dst)

    cv2.imshow('dst_x', dst_x/255)
    cv2.imshow('dst_y ', dst_y/255)
    cv2.imshow('dst ', dst)

    cv2.waitKey()
    cv2.destroyAllWindows()

main()