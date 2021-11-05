import cv2
import numpy as np

def find_local_maxima(src,ksize):
    (h,w) = src.shape
    pad_img = np.zeros((h+ksize,w+ksize))
    pad_img[ksize//2:h+ksize//2,ksize//2:w+ksize//2] = src
    dst = np.zeros((h,w))

    for row in range(h):
        for col in range(w):
            max_val = np.max(pad_img[row:row+ksize,col:col+ksize])
            if max_val == 0:
                continue
            if src[row,col] ==max_val:
                dst[row,col] = src[row,col]

    return dst
def main():
    src = cv2.imread('../image/zebra.png')
    gray = cv2.cvtColor(src,cv2.COLOR_BGRA2GRAY).astype(np.float32)

    dst = cv2.cornerHarris(gray,3,3,0.04)
    dst = cv2.dilate(dst,None)
    cv2.imshow('original',src)
    har = ((dst - np.min(dst))/np.max(dst-np.min(dst)) *255 + 1.5).astype(np.uint8)
    cv2.imshow('har',har)

    dst[dst < 0.01 * dst.max()] = 0 #thresholding
    dst = find_local_maxima(dst,21)

    interest_points = np.zeros((dst.shape[0],dst.shape[1],3))
    interest_points[dst!=0]=[0,0,255]
    cv2.imshow('interest_points',interest_points)

    src[dst!=0]=[0,0,255]
    cv2.imshow('harris',src)

    cv2.waitKey()
    cv2.destroyAllWindows()

if __name__ == '__main__':
    main()