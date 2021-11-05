import numpy as np

def my_get_Gaussian2D_mask(msize,sigma=1):
    # 2D gaussian filter만들기
    y,x = np.mgrid[-(msize//2):(msize//2)+1, -(msize//2):(msize//2)+1]

    #2차 가우시안 mask생성
    gaus2D = 1/(2*np.pi*sigma**2) * np.exp(-((x**2 + y**2 )/(2*sigma**2)))

    #mask의 총 합 = 1
    gaus2D = gaus2D / np.sum(gaus2D);

    return gaus2D

def my_get_Gaussian1D_mask(msize,sigma=1):
    # 2D gaussian filter만들기
    x = np.full((1,msize),[range(-(msize//2),(msize//2)+1)])

    #1차 가우시안 mask생성
    gaus1D = 1/(np.sqrt(2*np.pi)*sigma ) * np.exp(-((x*x)/(2*sigma*sigma)))

    #mask의 총 합 = 1
    gaus1D /= np.sum(gaus1D)

    return gaus1D