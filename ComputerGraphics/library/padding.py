import numpy as np
def my_padding(src,mask, pad_type = 'zero' ):
    (h,w) = src.shape
    (p_h,p_w) = mask.shape
    p_h = p_h // 2
    p_w = p_w // 2
    pad_img = np.zeros((h+2*p_h,w+2*p_w))
    pad_img[p_h:h+p_h, p_w:w+p_w] = src

    if pad_type == 'repetition':
        print('repetition padding')
        #up
        pad_img[:p_h,p_w:p_w+w] = src[0,:]
        #down
        pad_img[p_h+h:,p_w:p_w+w] = src[h-1,:]

        #left
        pad_img[:,:p_w] = pad_img[:,p_w+w-1:p_w+w]
        #right
        pad_img[:,p_w+w:] = pad_img[:,p_w+w-1:p_w+w]
    else :
        #else is zero padding
        print('zero padding')
    return pad_img

def repetition_padding(src, filter):
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