import cv2
import numpy as np
import sys
sys.path.insert(0, './library/filtering')
from library.filtering import my_filtering


def get_my_sobel():
    sobel_x = np.dot(np.array([[1],[2],[1]]),np.array([[-1,0,1]]))
    sobel_y = np.dot(np.array([[-1],[0],[1]]),np.array([[1,2,1]]))
    return sobel_x,sobel_y
def get_sobel():
    derivative = np.array([[-1,0,1]])
    blur = np.array([[1],[2],[1]])

    x = np.dot(blur,derivative)
    y = np.dot(derivative.T,blur.T)
    return x,y

if __name__ == '__main__':
    src = cv2.imread('../image/edge_detection_img.png', cv2.IMREAD_GRAYSCALE)

    sobel_x,sobel_y = get_sobel()

    dst_x = my_filtering(src, sobel_x, 'zero')
    dst_y = my_filtering(src, sobel_y, 'zero')
    dst = np.hypot(dst_x,dst_y)

    # 0~1 사이의 값으로 변경 후 0~255로 변경
    dst_x_Norm = ((dst_x - np.min(dst_x))/np.max(dst_x-np.min(dst_x))*255+0.5).astype(np.uint8)
    dst_y_Norm = ((dst_y - np.min(dst_y))/np.max(dst_y-np.min(dst_y))*255+0.5).astype(np.uint8)

    dst_x2 = np.sqrt(dst_x**2)
    dst_y2 = np.sqrt(dst_y**2)

    cv2.imshow('src', src)
    cv2.imshow('dst_x ', dst_x)
    cv2.imshow('dst_y ', dst_y)
    cv2.imshow('dst_x2 ', dst_x2)
    cv2.imshow('dst_y2 ', dst_y2)
    cv2.imshow('dst_x_Norm', dst_x_Norm)
    cv2.imshow('dst_y_Norm ', dst_y_Norm)


    cv2.waitKey()
    cv2.destroyAllWindows()