import cv2
import numpy as np
import matplotlib.pyplot as plt
import math
import sys
import os
# sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from library.sobel import calc_derivatives
def show_patch_hist(patch_vector):
    index = np.arange(len(patch_vector))
    plt.bar(index, patch_vector)
    plt.title('orientation histogram')
    plt.show()


def main():
    src = cv2.imread('../image/Lena.png')
    gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY)
    print('get Ix and Iy...')
    Ix, Iy = calc_derivatives(gray)

    print('calculate angle and magnitude')
    angle = np.rad2deg(np.arctan2(Iy, Ix))  ## -180 ~ 180으로 표현
    angle = (angle + 360) % 360  ## 0 ~ 360으로 표현
    magnitude = np.sqrt(Ix ** 2 + Iy ** 2)

    p_size = 32 #patch size
    p_h = 256   #patch height
    p_w = 256   #patch width

    patch = gray[p_h:p_h+p_size, p_w:p_w+p_size]

    cv2.imshow("patch", patch)
    cv2.waitKey()
    cv2.destroyAllWindows()

    patch_ang = angle[p_h:p_h + p_size, p_w:p_w + p_size]
    patch_mag = magnitude[p_h:p_h + p_size, p_w:p_w + p_size]
    angle_range = 10.

    h, w = patch.shape[:2]
    vector_size = int(360 // angle_range)
    vector = np.zeros(vector_size, )
    for row in range(h):
        for col in range(w):
            vector[int(patch_ang[row, col] // angle_range)] += patch_mag[row, col]

    print('angle')
    print(patch_ang)
    print('magnitude')
    print(patch_mag)
    print('vector')
    print(vector)
    print(np.argmax(vector) * angle_range)

    show_patch_hist(vector)


if __name__ == '__main__':
    main()