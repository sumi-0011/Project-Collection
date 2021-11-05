import numpy as np
import cv2

def my_get_Gaussian2D_mask(msize, sigma):
    #########################################
    # ToDo
    # 2D gaussian filter 만들기
    #########################################
    y, x = np.mgrid[-(msize // 2): msize // 2 + 1, -(msize // 2): msize // 2 + 1]

    # 2차 gaussian mask 생성
    gaus2D = (1 / (2 * np.pi * np.square(sigma))) * (np.exp(-((np.square(x) + np.square(y)) / (2.0 * np.square(sigma)))))
    print(gaus2D)

    # mask의 총 합 = 1
    gaus2D /= np.sum(gaus2D)
    print(gaus2D)

    return gaus2D

if __name__ == "__main__" :
    gaus = my_get_Gaussian2D_mask(msize=258, sigma=30)
    gaus = ((gaus - np.min(gaus)) / np.max(gaus - np.min(gaus)) * 255).astype(np.uint8)
    cv2.imshow('gaus', gaus)
    cv2.waitKey()
    cv2.decolor()
