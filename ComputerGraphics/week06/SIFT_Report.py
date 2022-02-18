import cv2
import numpy as np
from cv2 import KeyPoint
from library.all import find_local_maxima,calc_derivatives
import matplotlib.pyplot as plt

def SIFT(src):
    gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY).astype(np.float32)

    print("get keypoint")
    dst = cv2.cornerHarris(gray, 3, 3, 0.04)
    dst[dst < 0.01 * dst.max()] = 0
    dst = find_local_maxima(dst, 21)
    dst = dst / dst.max()
    
    #harris corner에서 keypoint를 추출
    y, x = np.nonzero(dst)

    keypoints = []
    for i in range(len(x)):
        # x, y, size, angle, response, octave, class_id
        pt_x = int(x[i]) #point x
        pt_y = int(y[i]) #point y
        size = None
        key_angle = -1.
        response = dst[y[i], x[i]] # keypoint에서 harris corner의 측정값
        octave = 0 # octave는 scale 변화를 확인하기 위한 값 (현재 과제에서는 사용안함)
        class_id = -1
        keypoints.append(KeyPoint(pt_x, pt_y, size, key_angle, response, octave, class_id))

    print('get Ix and Iy...')
    Ix, Iy = calc_derivatives(gray)

    print('calculate angle and magnitude')
    # magnitude / orientation 계산
    magnitude = np.sqrt((Ix ** 2) + (Iy ** 2))
    ### TODO
    ### numpy.rad2deg 함수는 각도의 단위를 라디안(radian)에서 도(degree) 단위로 변환합니다.
    angle = np.arctan2(Iy, Ix)  # radian 값
    angle = np.rad2deg(angle)  # radian 값을 degree로 변경 > -180 ~ 180도로 표현
    angle = (angle + 360) % 360  # -180 ~ 180을 0 ~ 360의 표현으로 변경
    # keypoint 방향
    print('calculate orientation assignment') #방향 할단

    for i in range(len(keypoints)):
        x, y = keypoints[i].pt
        orient_hist = np.zeros(36, ) #point의 방향을 저장
        for row in range(-8, 8):
            for col in range(-8, 8):
                p_y = int(y + row)
                p_x = int(x + col)
                if p_y < 0 or p_y > src.shape[0] - 1 or p_x < 0 or p_x > src.shape[1] - 1:
                    continue # 이미지를 벗어나는 부분에 대한 처리
                gaussian_weight = np.exp((-1 / 16) * (row ** 2 + col ** 2))
                orient_hist[int(angle[p_y, p_x] // 10)] += magnitude[p_y, p_x] * gaussian_weight
        ###################################################################
        ## ToDo
        ## orient_hist에서 가중치가 가장 큰 값을 추출하여 keypoint의 angle으로 설정
        ## 가장 큰 가중치의 0.8배보다 큰 가중치의 값도 keypoint의 angle로 설정
        ## keypoint 저장에는 KeyPoint module을 사용할 것 
        ## angle은 0 ~ 360도의 표현으로 저장해야 함 //ok
        ## np.max, np.argmax를 활용하면 쉽게 구할 수 있음
        ## keypoints[i].angle = ???
        ###################################################################
        #최대값의 색인 위치 :  np.argmax()
        #최대값(max):np.max()
        # if np.argmax(orient_hist)*10 <0 :
        #     print(np.argmax(orient_hist),"key",i)
        # keypoints[i].angle =float(np.argmax(orient_hist)*10)
        # print("orient_hist max",np.max(orient_hist),np.argmax(orient_hist))
        # show_patch_hist(orient_hist)
        # for i in range(0,len(orient_hist)):
        #     if(i != np.argmax(orient_hist) and np.max(orient_hist)*0.8 < orient_hist[i]):
        #         keypoints.append(KeyPoint(x, y, None, i))
        max = orient_hist.argmax()  # 최대 가중치의 각도
        keypoints[i].angle = max * 10.

        # max * 0.8 이상 고려
        maxWeight = orient_hist.max()  # 최대 가중치
        for j in range(36):  # len  254
            if orient_hist[j] > maxWeight * 0.8:
                keypoints.append(KeyPoint(x, y, size, j * 10, response, octave, class_id))
    print('calculate descriptor')
    descriptors = np.zeros((len(keypoints), 128))  # 8 orientation * 4 * 4 = 128 dimensions

    for i in range(len(keypoints)):
        x, y = keypoints[i].pt
        theta = np.deg2rad(keypoints[i].angle)
        # 키포인트 각도 조정을 위한 cos, sin값
        cos_angle = np.cos(theta)
        sin_angle = np.sin(theta)
        # print(i,"x,y",x,y,"the",theta)
        # print("keypoints",keypoints[i])

        size = 4
        mX = col + size  # mini window X point
        mY = row + size  # mini window Y point

        count = 0  # decriptor 128 array 의 index
        orient_hist_8 = np.zeros(8, )
        for row in range(-8, 8):
            for col in range(-8, 8):
                # 회전을 고려한 point값을 얻어냄
                row_rot = np.round((cos_angle * col) + (sin_angle * row))
                col_rot = np.round((cos_angle * col) - (sin_angle * row))

                p_y = int(y + row_rot)
                p_x = int(x + col_rot)
                if p_y < 0 or p_y > (src.shape[0] - 1) \
                        or p_x < 0 or p_x > (src.shape[1] - 1):
                    continue
                ###################################################################
                ## ToDo
                ## descriptor을 완성
                ## 4×4의 window에서 8개의 orientation histogram으로 표현
                ## 최종적으로 128개 (8개의 orientation * 4 * 4)의 descriptor를 가짐
                ## gaussian_weight = np.exp((-1 / 16) * (row_rot ** 2 + col_rot ** 2))
                ###################################################################
                # if keypoints[i].angle<0 or angle[p_y, p_x] <0:
                #     print("point",i,keypoints[i].angle,angle[p_y, p_x])
                # descriptor_angle = angle[p_y, p_x] - keypoints[i].angle
                # descriptor_angle = descriptor_angle if descriptor_angle > 0 else descriptor_angle+360
                # gaussian_weight = np.exp((-1 / 16) * (row_rot ** 2 + col_rot ** 2))
                #
                #
                # descriptors[i][int((row+8)//4+((col+8)//4)*4+descriptor_angle//45-1)] += magnitude[p_y, p_x]*gaussian_weight
                # print("descriptors",descriptors[i][int((row+8)//4+((col+8)//4)*4+descriptor_angle//45)])
    # print(len(keypoints))
                gaussian_weight = np.exp((-1 / 16) * (row_rot ** 2 + col_rot ** 2))

                orient_hist_8[int((angle[p_y, p_x] - keypoints[i].angle) // 45)] += magnitude[p_y, p_x] * gaussian_weight
                originMx = mX
                originMy = mY

                # 미니 윈도우 탐색을 위한 네비게이터
                if col == mX - 1:  # x 경계 도달
                    if row == mY - 1:  # y 도 경계 도달
                        if mX == 8 and mY == 8:  # 경계가 맨 마지막, -> 마지막 포인트
                            mX = 100
                            mY = 100
                        elif mX == 8:  # mX만 맨 마지막 -> 다음 라인의 윈도우로 이동
                            mX = -8 + size
                            mY += size
                        else:  # 오른쪽 윈도우로 이동
                            mX += size
                            row -= size - 1
                    else:  # y 경계 도달 x  -> 윈도우 내의 다음 라인으로
                        col -= size
                        row += 1

                # 윈도우 이동을 감지해 16개의 angle orient_hist_8에 추
                if (originMx != mX or originMy != mY):  # 윈도우 이동 -> 16개 미니 윈도우 체크
                    descriptors[i][count:count + 8] = orient_hist_8[0:8]
                    orient_hist_8 = np.zeros(8, )
                    count += 8

                col += 1

            col = -8
            mX = col + size
            row += 1
    return keypoints, descriptors


def show_patch_hist(patch_vector):
    index = np.arange(len(patch_vector))
    plt.bar(index, patch_vector)
    plt.title('orientation histogram')
    plt.show()
def main():
    src = cv2.imread("../image/zebra.png")
    src_rotation = cv2.rotate(src, cv2.ROTATE_90_CLOCKWISE)

    kp1, des1 = SIFT(src)
    kp2, des2 = SIFT(src_rotation)

    # ## Matching 부분 ##
    bf = cv2.BFMatcher_create(cv2.NORM_HAMMING, crossCheck=True)
    des1 = des1.astype(np.uint8)
    des2 = des2.astype(np.uint8)
    matches = bf.match(des1, des2)
    matches = sorted(matches, key = lambda x:x.distance)

    result = cv2.drawMatches(src, kp1, src_rotation, kp2, matches[:20], outImg=None, flags=2)

    # 결과의 학번 작성하기!
    cv2.imshow('my_sift', result)
    cv2.waitKey()
    cv2.destroyAllWindows()
    

if __name__ == '__main__':
    main()