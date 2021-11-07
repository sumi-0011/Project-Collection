import numpy as np
import cv2
import random
def my_padding(src, filter):
    (h, w) = src.shape
    if isinstance(filter, tuple):
        (h_pad, w_pad) = filter
    else:
        (h_pad, w_pad) = filter.shape
    h_pad = h_pad // 2
    w_pad = w_pad // 2
    padding_img = np.zeros((h+h_pad*2, w+w_pad*2))
    padding_img[h_pad:h+h_pad, w_pad:w+w_pad] = src

    # repetition padding
    # up
    padding_img[:h_pad, w_pad:w_pad + w] = src[0, :]
    # down
    padding_img[h_pad + h:, w_pad:w_pad + w] = src[h - 1, :]
    # left
    padding_img[:, :w_pad] = padding_img[:, w_pad:w_pad + 1]
    # right
    padding_img[:, w_pad + w:] = padding_img[:, w_pad + w - 1:w_pad + w]

    return padding_img

def my_filtering(src, filter):
    print(src.shape)
    (h, w) = src.shape
    (f_h, f_w) = filter.shape

    #filter 확인
    #print('<filter>')
    #print(filter)

    # 직접 구현한 my_padding 함수를 이용
    pad_img = my_padding(src, filter)

    dst = np.zeros((h, w))
    for row in range(h):
        for col in range(w):
            dst[row, col] = np.sum(pad_img[row:row + f_h, col:col + f_w] * filter)

    return dst

def my_get_Gaussian_filter(fshape,sigma=1):
    (f_h,f_w) = fshape
    y,x = np.mgrid[-(f_h//2):(f_h//2)+1,-(f_w//2):(f_w//2)+1]
    filter_gaus = 1/(2*np.pi*sigma**2) * np.exp(-((x**2 + y**2 )/(2*sigma**2)))
    #mask의 총 합  = 1
    filter_gaus/= np.sum(filter_gaus)
    return filter_gaus

def GaussianFiltering(src,fshape=(3,3),sigma=1):
    gaus = my_get_Gaussian_filter(fshape,sigma)
    dst = my_filtering(src,gaus)
    return dst
# bilinear interpolation
def my_bilinear(img, x, y):
    '''
    :param img: 값을 찾을 img
    :param x: interpolation 할 x좌표
    :param y: interpolation 할 y좌표
    :return: img[x,y]에서의 value (bilinear interpolation으로 구해진)
    '''
    floorX, floorY = int(x), int(y)

    t, s = x - floorX, y - floorY

    zz = (1 - t) * (1 - s)
    zo = t * (1 - s)
    oz = (1 - t) * s
    oo = t * s

    interVal = img[floorY, floorX, :] * zz + img[floorY, floorX + 1, :] * zo + \
               img[floorY + 1, floorX, :] * oz + img[floorY + 1, floorX + 1, :] * oo

    return interVal

def backward(img1, M):
    '''
    :param img1: 변환시킬 이미지
    :param M: 변환 matrix
    :return: 변환된 이미지
    '''
    h, w, c = img1.shape
    result = np.zeros((h, w, c))
    final_result= np.zeros((h, w, c))
    # 추가
    window = 5

    for row in range(h):
        for col in range(w):
            start_x,start_y = col-2,row-2
            M_ = np.zeros((window,window,c))
            for i in range(start_y,start_y+window):
                for j in range(start_x,start_x+window):
                    if start_x < 0 or start_y < 0 or (start_x + 1) >= w or (start_y + 1) >= h:
                        continue
                    xy_prime = np.array([[start_x, start_y, 1]]).T
                    xy = (np.linalg.inv(M)).dot(xy_prime)
                    gaussian_weight = np.exp((-1 / 16) * (start_y ** 2 + start_x ** 2))
                    x_ = xy[0, 0]
                    y_ = xy[1, 0]

                    if x_ < 0 or y_ < 0 or (x_ + 1) >= w or (y_ + 1) >= h:
                        continue
                    M_[i-row+2,j-col+2,:] = my_bilinear(img1, x_, y_)
            for i in range(3):
                gaussain_result = GaussianFiltering(M_[:, :, i], (5, 5), 1)
                result[row,col,i] = gaussain_result
            # result[row, col, :] = M_
            # xy_prime = np.array([[col, row, 1]]).T
            # xy = (np.linalg.inv(M)).dot(xy_prime)
            # gaussian_weight = np.exp((-1 / 16) * (window ** 2 + window ** 2))
            # x_ = xy[0, 0]
            # y_ = xy[1, 0]
            #
            # if x_ < 0 or y_ < 0 or (x_ + 1) >= w or (y_ + 1) >= h:
            #     continue
            #
            # result[row, col, :] = my_bilinear(img1, x_, y_)

    # for i in range(3):
    #     gaussain_result = GaussianFiltering(result[:, :, i], (5, 5), 1)
    #     final_result[:,:,i] = gaussain_result
    return result
    return final_result

# def backward(src, M):
#     h_src,w_src,c_src = src.shape
#     dst = np.zeros((h_src, w_src,c_src))
#     h,w,c = dst.shape
#
#     M_inv = np.linalg.inv(M)
#
#     for row in range(h):
#         for col in range(w):
#             P_dst = np.array([
#                 [col],[row],[1]
#             ])
#             P = np.dot(M_inv,P_dst)
#             src_col = P[0][0]
#             src_row = P[1][0]
#
#             src_col_right = int(np.ceil(src_col))
#             src_col_left = int(src_col)
#
#             src_row_bottom = int(np.ceil(src_row))
#             src_row_top = int(src_row)
#
#             if src_col_right >= w_src or src_row_bottom >= h_src:
#                 continue
#             s = src_col - src_col_left
#             t = src_row - src_row_top
#
#             intensity = (1-s) * (1-t) * src[src_row_top,src_col_left] \
#                 + s * (1 - t) * src[src_row_top, src_col_right] \
#                 + (1 - s) * t * src[src_row_bottom, src_col_left] \
#                 + s * t * src[src_row_bottom, src_col_right]
#             dst[row,col] = intensity
#     dst = dst.astype(np.uint8)
#     return dst

def my_ls(matches, kp1, kp2):
    '''
    :param matches: keypoint matching 정보
    :param kp1: keypoint 정보.
    :param kp2: keypoint 정보2.
    :return: X : 위의 정보를 바탕으로 Least square 방식으로 구해진 Affine
                변환 matrix의 요소 [a, b, c, d, e, f].T
    '''
    A = []
    B = []
    for idx, match in enumerate(matches):
        trainInd = match.trainIdx
        queryInd = match.queryIdx

        x, y = kp1[queryInd].pt
        x_prime, y_prime = kp2[trainInd].pt

        A.append([x, y, 1, 0, 0, 0])
        A.append([0, 0, 0, x, y, 1])
        B.append([x_prime])
        B.append([y_prime])

    A = np.array(A)
    B = np.array(B)

    try:
        ATA = np.dot(A.T, A)
        ATB = np.dot(A.T, B)
        X = np.dot(np.linalg.inv(ATA), ATB)
    except:
        print('can\'t calculate np.linalg.inv((np.dot(A.T, A)) !!!!!')
        X = None
    return X


def get_matching_keypoints(img1, img2, keypoint_num):
    '''
    :param img1: 변환시킬 이미지
    :param img2: 변환 목표 이미지
    :param keypoint_num: 추출한 keypoint의 수
    :return: img1의 특징점인 kp1, img2의 특징점인 kp2, 두 특징점의 매칭 결과
    '''
    sift = cv2.xfeatures2d.SIFT_create(keypoint_num)

    kp1, des1 = sift.detectAndCompute(img1, None)
    kp2, des2 = sift.detectAndCompute(img2, None)

    bf = cv2.BFMatcher(cv2.DIST_L2)

    matches = bf.match(des1, des2)
    matches = sorted(matches, key=lambda x: x.distance)

    """
    matches: List[cv2.DMatch]
    cv2.DMatch의 배열로 구성

    matches[i]는 distance, imgIdx, queryIdx, trainIdx로 구성됨
    trainIdx: 매칭된 img1에 해당하는 index
    queryIdx: 매칭된 img2에 해당하는 index

    kp1[queryIdx]와 kp2[trainIdx]는 매칭된 점
    """
    return kp1, kp2, matches

def feature_matching(img1, img2, keypoint_num=None):
    kp1, kp2, matches = get_matching_keypoints(img1, img2, keypoint_num)

    X = my_ls(matches, kp1, kp2)

    M = np.array([[X[0][0], X[1][0], X[2][0]],
                  [X[3][0], X[4][0], X[5][0]],
                  [0, 0, 1]])

    result = backward(img1, M)

    return result.astype(np.uint8)

def feature_matching_RANSAC(img1, img2, keypoint_num=None, iter_num=500, threshold_distance=10):
    '''
    :param img1: 변환시킬 이미지
    :param img2: 변환 목표 이미지
    :param keypoint_num: sift에서 추출할 keypoint의 수
    :param iter_num: RANSAC 반복횟수
    :param threshold_distance: RANSAC에서 inlier을 정할때의 거리 값
    :return: RANSAC을 이용하여 변환 된 결과
    '''
    kp1, kp2, matches = get_matching_keypoints(img1, img2, keypoint_num)

    matches_shuffle = matches.copy()
    inliers = [] #랜덤하게 고른 n개의 point로 구한 inlier개수 결과를 저장
    M_list = [] #랜덤하게 고른 n개의 point로 만든 affine matrix를 저장
    for i in range(iter_num):
        print('\rcalculate RANSAC ... %d ' % (int((i + 1) / iter_num * 100)) + '%', end='\t')
        #######################################################################
        # ToDo
        # RANSAC을 이용하여 최적의 affine matrix를 찾고 변환하기
        # 1. 랜덤하게 3개의 matches point를 뽑아냄
        # 2. 1에서 뽑은 matches를 이용하여 affine matrix M을 구함
        # 3. 2에서 구한 M을 모든 matches point와 연산하여 inlier의 개수를 파악
        # 4. iter_num 반복하여 가장 많은 inlier를 가지는 M을 최종 affine matrix로 채택
        ########################################################################
        #1. 랜덤하게 3개의 matches point를 뽑아냄
        random.shuffle(matches_shuffle)     #랜덤하게
        three_points = matches_shuffle[:3]  #  3개의 matches point를 뽑아냄
        # 2. 1에서 뽑은 matches를 이용하여 affine matrix M을 구함
        A = []
        B = []
        # 3개의 point만 가지고 M 구하기
        for idx, point in enumerate(three_points):
            trainInd = point.trainIdx
            queryInd = point.queryIdx

            x, y = kp1[queryInd].pt
            x_prime, y_prime = kp2[trainInd].pt
            A.append([x, y, 1, 0, 0, 0])
            A.append([0, 0, 0, x, y, 1])
            B.append([x_prime])
            B.append([y_prime])
        A = np.array(A)
        B = np.array(B)
        try:
            ATA_inv = np.linalg.inv(np.dot(A.T, A))
            ATAT = np.dot(ATA_inv, A.T)
            X = np.dot(ATAT, B).flatten()
        except:
            print('can\'t calculate np.linalg.inv((np.dot(A.T, A)) !!!!!')  #이 오류 뜨면 무시하고 다시 run
            X = None
            continue
        # M 완성
        M = np.array([[X[0], X[1], X[2]],
                      [X[3], X[4], X[5]],
                      [0, 0, 1]])
        M_list.append(M)
        # 3. 2에서 구한 M을 모든 matches point와 연산하여 inlier의 개수를 파악
        count_inliers = 0
        for idx, point in enumerate(matches):
            # 위에서 구한 M으로 모든 point들에 대하여 예상 point를 구해
            # 구해진 예상 point와 실제 point 간의 L2 distance 를 구해서 threshold_distance보다 작은 값이 있는 경우 inlier로 판단
            # inlier로 판단되면 개수를 센다.
            trainInd = point.trainIdx
            queryInd = point.queryIdx

            x, y = kp1[queryInd].pt
            x_prime, y_prime = kp2[trainInd].pt

            a = [x, y, 1]
            real_point = [x_prime,y_prime, 1]           #실제 point
            predict_point = np.dot(M, np.array(a).T)    #예상 point

            if L2_distance(predict_point, real_point) < threshold_distance: #L2 distance를 구해 inlier인지 판단
                count_inliers += 1

        inliers.append(count_inliers)

    # 4. iter_num 반복하여 가장 많은 inlier를 가지는 M을 최종 affine matrix로 채택
    inliers = np.array(inliers)
    max_inliers_idx = np.argmax(inliers)    #argmax : 배열에서 가장 큰 값을 가지는 인덱스를 반환

    best_M = np.array(M_list[max_inliers_idx])

    result = backward(img1, best_M)
    return result.astype(np.uint8)


def L2_distance(vector1, vector2):
    return np.sqrt(np.sum((vector1-vector2)**2))

def main():
    src = cv2.imread('./Lena.png')
    src = cv2.resize(src,None,fx=0.5,fy=0.5)
    src2 = cv2.imread('./LenaFaceShear.png')
    print("src : ",src.shape,src2.shape)
    result_RANSAC = feature_matching_RANSAC(src2, src)
    result_RANSAC2 = feature_matching_RANSAC(src, src2)
    cv2.imshow('input', src)
    cv2.imshow('result RANSAC 201902698', result_RANSAC)
    cv2.imshow('result LS 201902698', result_RANSAC2)
    cv2.imshow('goal', src2)
    cv2.waitKey()
    cv2.destroyAllWindows()



if __name__ == '__main__':
    main()
