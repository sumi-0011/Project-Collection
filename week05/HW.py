import numpy as np
import cv2
import time
import os
import sys

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from library.sobel import calc_derivatives
from library.find_local_maxima import find_local_maxima
from library.library import my_padding
def get_integral_image(src):
    ##########################################################################
    # ToDo
    # src를 integral로 변경하는 함수
    # 실습 때 진행한 내용입니다.
    ##########################################################################
    # assert len(src.shape) == 2
    h, w = src.shape
    dst = np.zeros(src.shape)
    for row in range(h):
        dst[row, 0] = np.sum(src[0:row + 1, 0])

    for col in range(w):
        dst[0, col] = np.sum(src[0, 0:col + 1])

    for row in range(1, h):
        for col in range(1, w):
            dst[row, col] = src[row, col] + dst[row - 1, col] + dst[row, col - 1] - dst[row - 1, col - 1]
    return dst


def calc_local_integral_value(src, left_top, right_bottom):
    ##########################################################################
    # ToDo
    # integral에서 filter의 요소합 구하기
    # 실습 때 진행한 내용입니다.
    ##########################################################################
    # assert len(left_top) == 2
    # assert len(right_bottom) == 2
    # calc_local_integral_value(IxIx_integral_pad, (row, col), (row + fsize - 1, col + fsize - 1))

    assert len(left_top) == 2
    assert len(right_bottom) == 2

    lt_row, lt_col = left_top
    rb_row, rb_col = right_bottom

    lt_val = src[lt_row - 1, lt_col - 1]
    rt_val = src[lt_row - 1, rb_col]
    lb_val = src[rb_row, lt_col - 1]
    rb_val = src[rb_row, rb_col]

    if lt_row == 0:
        lt_val = 0
        lb_val = 0
    if lt_col == 0:
        lt_val = 0
        rt_val = 0

    return lt_val - lb_val - rt_val + rb_val

# 시간 측정을 하는 함수
# integral image를 사용하지 않고 covariance matrix 구하기
def calc_M_harris(IxIx, IxIy, IyIy, fsize=5):
    assert IxIx.shape == IxIy.shape and IxIx.shape == IyIy.shape
    h, w = IxIx.shape
    M = np.zeros((h, w, 2, 2))
    IxIx_pad = my_padding(IxIx, (fsize, fsize))
    IxIy_pad = my_padding(IxIy, (fsize, fsize))
    IyIy_pad = my_padding(IyIy, (fsize, fsize))

    '''for row in range(h):
        for col in range(w):
            M[row, col, 0, 0] = np.sum(IxIx_pad[row:row+fsize, col:col+fsize])
            M[row, col, 0, 1] = np.sum(IxIy_pad[row:row+fsize, col:col+fsize])
            M[row, col, 1, 0] = M[row, col, 0, 1]
            M[row, col, 1, 1] = np.sum(IyIy_pad[row:row+fsize, col:col+fsize])'''

    ##########################################################################
    # ToDo
    # integral을 사용하지 않고 covariance matrix 구하기
    # 4중 for문을 채워서 완성하기
    # 실습 시간에 했던 내용을 생각하면 금방 해결할 수 있음
    ##########################################################################
    for row in range(h):
        for col in range(w):
            t_xx,t_xy,t_yy = 0,0,0
            for f_row in range(fsize):
                for f_col in range(fsize):
                    ##############################
                    # ToDo
                    # 위의 2중 for문을 참고하여 M 완성
                    ## => np.sum을 사용하지 않고 for문을 이용하여서 더한다.
                    ##############################
                    t_xx += IxIx_pad[row+f_row, col+f_col]
                    t_xy += IxIy_pad[row+f_row, col+f_col]
                    t_yy += IyIy_pad[row+f_row, col+f_col]
            M[row, col, 0, 0] = t_xx
            M[row, col, 0, 1] = t_xy
            M[row, col, 1, 0] = M[row, col, 0, 1]
            M[row, col, 1, 1] = t_yy
    return M

## Integral을 사용하지 않은 Harris detector
def harris_detector(src, k=0.04, threshold_rate=0.01, fsize=5):
    harris_img = src.copy()
    h, w, c = src.shape
    gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY) / 255.
    # calculate Ix, Iy
    Ix, Iy = calc_derivatives(gray)

    # Square of derivatives
    IxIx = Ix ** 2
    IyIy = Iy ** 2
    IxIy = Ix * Iy

    start = time.perf_counter()  # 시간 측정 시작
    M_harris = calc_M_harris(IxIx, IyIy, IxIy, fsize)
    end = time.perf_counter()  # 시간 측정 끝
    print('M_harris time : ', end - start)

    R = np.zeros((h, w))
    for row in range(h):
        for col in range(w):
            ##########################################################################
            # ToDo
            # det_M 계산
            # trace_M 계산
            # R 계산 Harris 방정식 구현
            ## har = detM -a* traceM**2= g(x)g(y)-g(xy)**2
            ## har = G_IxIx*G_IyIy - G_IxIy**2 - alpha*(G_IxIx+G_IyIy)**2  # 저번 과제에서 받아옴
            # M[row, col, 0, 0] = np.sum(IxIx_pad[row:row+fsize, col:col+fsize])
            # M[row, col, 0, 1] = np.sum(IxIy_pad[row:row+fsize, col:col+fsize])
            # M[row, col, 1, 0] = M[row, col, 0, 1]
            # M[row, col, 1, 1] = np.sum(IyIy_pad[row:row+fsize, col:col+fsize])
            # # 이번 과제에서는 가우시안을 사용하지 x
            ##########################################################################
            det_M =M_harris[row,col,0,0] * M_harris[row,col,1, 1] - M_harris[row,col,0, 1]**2
            trace_M = M_harris[row,col,0,0] + M_harris[row,col,1, 1]
            R[row, col] = det_M - k * (trace_M * trace_M)

    # thresholding
    R[R < threshold_rate * np.max(R)] = 0

    R = find_local_maxima(R, 21)
    R = cv2.dilate(R, None)
    print(R)

    harris_img[R != 0] = [0, 0, 255]

    return harris_img

def calc_M_integral(IxIx_integral, IxIy_integral, IyIy_integral, fsize=5):
    # assert IxIx_integral.shape == IxIy_integral.shape and IxIx_integral.shape == IyIy_integral.shape
    h, w = IxIx_integral.shape
    M = np.zeros((h, w, 2, 2))

    IxIx_integral_pad = my_padding(IxIx_integral, (fsize, fsize))
    IxIy_integral_pad = my_padding(IxIy_integral, (fsize, fsize))
    IyIy_integral_pad = my_padding(IyIy_integral, (fsize, fsize))

    ##########################################################################
    # ToDo
    # integral 값을 이용하여 covariance matrix 구하기
    # calc_local_integral_value를 사용하면 쉽게 구할 수 있음
    ##########################################################################
    for row in range(h):
        for col in range(w):
            M[row, col, 0, 0] = calc_local_integral_value(IxIx_integral_pad,(row,col),(row+fsize-1,col+fsize-1))
            M[row, col, 0, 1] = calc_local_integral_value(IxIy_integral_pad,(row,col),(row+fsize-1,col+fsize-1))
            M[row, col, 1, 0] = M[row, col, 0, 1]
            M[row, col, 1, 1] = calc_local_integral_value(IyIy_integral_pad,(row,col),(row+fsize-1,col+fsize-1))

    return M

## Integral을 사용하는 Harris detector
# integral을 사용하여 covariance matrix 구하기
def harris_detector_integral(src, k=0.04, threshold_rate=0.01, fsize=5):
    harris_img = src.copy()
    h, w, c = src.shape
    gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY) / 255.
    # calculate Ix, Iy
    Ix, Iy = calc_derivatives(gray)

    # Square of derivatives
    IxIx = Ix ** 2
    IyIy = Iy ** 2
    IxIy = Ix * Iy

    # get_integral_image()를 통해 integral image를 생성한다.
    start = time.perf_counter()  # 시간 측정 시작
    IxIx_integral = get_integral_image(IxIx)
    IxIy_integral = get_integral_image(IxIy)
    IyIy_integral = get_integral_image(IyIy)
    end = time.perf_counter()  # 시간 측정 끝
    print('make integral image time : ', end - start)

    start = time.perf_counter()  # 시간 측정 시작
    ##############################
    # ToDo
    # M_integral 완성시키기
    ##############################
    M_integral = calc_M_integral(IxIx_integral, IxIy_integral, IyIy_integral, fsize)
    end = time.perf_counter()  # 시간 측정 끝
    print('M_harris integral time : ', end - start)

    R = np.zeros((h, w))
    for row in range(h):
        for col in range(w):
            ##########################################################################
            # ToDo
            # det_M 계산
            # trace_M 계산
            # R 계산 Harris 방정식 구현
            ##########################################################################
            det_M = M_integral[row, col, 0, 0] * M_integral[row, col, 1, 1] - M_integral[row, col, 0, 1] ** 2
            trace_M = M_integral[row, col, 0, 0] + M_integral[row, col, 1, 1]
            R[row, col] = det_M - k * (trace_M * trace_M)

    # thresholding
    R[R < threshold_rate * np.max(R)] = 0

    R = find_local_maxima(R, 21)
    R = cv2.dilate(R, None)
    print(R)
    harris_img[R != 0] = [0, 0, 255]

    return harris_img

def main():
    src = cv2.imread('../image/zebra.png')  # shape : (552, 435, 3)
    print('start!')
    harris_img = harris_detector(src)
    harris_integral_img = harris_detector_integral(src)
    cv2.imshow('harris_img ' + '201902698', harris_img)
    cv2.imshow('harris_integral_img ' + '201902698', harris_integral_img)
    cv2.waitKey()
    cv2.destroyAllWindows()

if __name__ == '__main__':
    main()