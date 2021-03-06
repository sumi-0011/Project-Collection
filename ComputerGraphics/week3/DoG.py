import numpy as np
import cv2
from library.filtering import my_filtering

def get_DoG_filter(fsize,sigma=1):
    #TODO
    #DoG maskμμ±

    y,x = np.mgrid[-(fsize//2):(fsize//2)+1,-(fsize//2):(fsize//2)+1]

    DoG_x = (-x/sigma**2) *np.exp(-((x**2 + y**2)/(2*sigma**2)))
    DoG_y = (-y/sigma**2) *np.exp(-((x**2 + y**2)/(2*sigma**2)))

    return DoG_x,DoG_y

def main():
    src = cv2.imread('../image/Lena.png', cv2.IMREAD_GRAYSCALE)

    #DoG mask μμ±.
    DoG_x, DoG_y = get_DoG_filter(fsize=3,sigma=1)

    dst_x = my_filtering(src, DoG_x, 'zero')     # π·ππΊ(x) = πΊβ²(π₯) β πΌ
    dst_y = my_filtering(src, DoG_y, 'zero')     # π·ππΊ(y) = πΊβ²(y) β πΌ


    # π·ππΊ π₯ ,π·ππΊ(π¦)μ magnitudeλ₯Ό κ³μ°.
    dst = np.sqrt(dst_x**2+dst_y**2)
    print('dst',dst)
    print('dst/255' ,dst/255)

    # 0~1 μ¬μ΄μ κ°μΌλ‘ λ³κ²½ ν 0~255λ‘ λ³κ²½
    dst = ((dst - np.min(dst))/np.max(dst-np.min(dst))*255+0.5).astype(np.uint8)
    print('dst',dst)

    cv2.imshow('dst_x', dst_x/255)
    cv2.imshow('dst_y ', dst_y/255)
    cv2.imshow('dst ', dst)

    cv2.waitKey()
    cv2.destroyAllWindows()

main()