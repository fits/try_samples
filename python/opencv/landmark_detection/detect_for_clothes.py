
import sys
import os
import math
import itertools
import json
import numpy as np
import cv2

file = sys.argv[1]
n_detect = int(sys.argv[2])
shoulder_mode = len(sys.argv) > 3

def otsu(img):
    th, _ = cv2.threshold(img.copy(), 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return th

def binalize(img, thres):
    _, res = cv2.threshold(img.copy(), thres, 255, cv2.THRESH_BINARY_INV)
    return res

def detect_threshold(img, bth, gradient_thres = 0.5):
    white_area = lambda g: len(g[g > 127])

    areas = [white_area(binalize(img, t)) for t in range(0, 254)]

    grd1 = np.gradient(areas)
    grd2 = np.gradient(grd1)

    start = bth
    end = np.max(np.where(grd2 < gradient_thres))

    mx = np.argmax(grd1[start:end]) + start

    return np.argmin(grd1[mx:end]) + mx

def img_binary(imgfile):
    img = cv2.imread(imgfile, 0)

    img = cv2.GaussianBlur(img, (3, 3), 0)

    base_th = int(otsu(img))

    th = detect_threshold(img, base_th)

    return binalize(img, th)

def contours(imgfile, mth = cv2.CHAIN_APPROX_SIMPLE):
    img = img_binary(imgfile)

    _, c, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, mth)

    return c

def max_contours(cts):
    return cts[np.argmax([len(c) for c in cts])]

def barycenter(cts):
    m = cv2.moments(cts)

    cx = m['m10'] / m['m00']
    cy = m['m01'] / m['m00']

    return (int(cx), int(cy))

def approx(cts, rate = 0.005):
    ep = rate * cv2.arcLength(cts, True) if rate < 1 else rate

    return cv2.approxPolyDP(cts, ep, True)

def approx_threshold(cts, n_thres = 20, start = 0.001, end = 0.05, step = 0.001):
    n = start

    res = []

    while n < end:
        res = approx(cts, n)

        if len(res) < n_thres:
            break

        n += step

    return res.reshape((-1, 2))

def sort_contours(cts, bp):
    ds = np.argsort([np.linalg.norm(t - bp) for t in cts])

    for d in ds:
        if d > 0 and cts[d, 0] <= bp[0]:
            return np.concatenate([cts[d:], cts[:d]])

    return cts

def div_index(cts, cx):
    flag = False

    for i, v in enumerate(cts):
        if not flag and v[0] < cx:
            flag = True

        if flag and v[0] > cx:
            return i

    return -1

def split_contours(cts, cx):
    sep = div_index(cts, cx)

    return (cts[:sep], np.flip(cts[sep:], 0))

def adaptive_points(cts, clr, ba, bb, n = 3):
    arc = lambda x: cv2.arcLength(np.concatenate([cts[x], [bb, ba, clr]]), True)

    limit_x = min(0, clr[0])

    idx = np.where(cts[:, 0] < limit_x)[0]

    if len(idx) <= n:
        return idx

    base_arc = arc(idx)

    arcs = np.array([(x, arc(list(x))) for x in itertools.combinations(idx, n)])

    min_idx = np.argmin(np.abs(arcs[:, 1] - base_arc))

    return list(arcs[min_idx, 0])

def base_points(cts):
    cx, _ = barycenter(cts)

    x_min = np.argmin(cts[:, 0])
    x_max = np.argmax(cts[:, 0])

    idx = (x_min, x_max) if x_min < x_max else (x_max, x_min)

    a = np.concatenate([cts[:idx[0]], cts[idx[1]:]])
    b = cts[idx[0]:idx[1]]

    a_index = np.argmin([abs(t[0] - cx) for t in a])
    b_index = np.argmin([abs(t[0] - cx) for t in b])

    ra = np.array([cx, a[a_index][1]])
    rb = np.array([cx, b[b_index][1]])

    return (ra, rb) if ra[1] < rb[1] else (rb, ra)

def collar(cts):
    cx, _ = barycenter(cts)

    a = cts[cts[:, 0] < cx]
    b = cts[cts[:, 0] > cx]

    return (a[np.argmin(a[:, 1])], b[np.argmin(b[:, 1])])

def shoulders(cts, idx, clr):
    def index(ts, v):
        for i, t in enumerate(ts):
            if t[0] == v[0] and t[1] == v[1]:
                return i

        return len(ts)

    la, ra = cts
    l_clr, r_clr = clr
    l_sl_index, r_sl_index = idx

    l_clr_next = index(la, l_clr) + 1
    r_clr_next = index(ra, r_clr) + 1

    if l_sl_index > l_clr_next and r_sl_index > r_clr_next:
        l_sh = np.argmax([
            np.linalg.norm(t - l_clr) + np.linalg.norm(t - la[l_sl_index]) 
                for t in la[l_clr_next:l_sl_index]
        ]) + l_clr_next

        r_sh = np.argmax([
            np.linalg.norm(t - r_clr) + np.linalg.norm(t - ra[r_sl_index]) 
                for t in ra[r_clr_next:r_sl_index]
        ]) + r_clr_next

        return [la[l_sh], ra[r_sh]]

    return []

def detect_landmarks(imgfile, n = 3, append_shoulder = True):
    cts = max_contours(contours(imgfile, cv2.CHAIN_APPROX_NONE)).reshape((-1, 2))

    c = barycenter(cts)
    cx, cy = c

    ba, bb = base_points(cts)

    ats = sort_contours(approx_threshold(cts), ba)

    l_collar, r_collar = collar(ats)

    la, ra = split_contours(ats, cx)

    la_idx = adaptive_points(la - c, l_collar - c, ba - c, bb - c, n)
    la_res = la[la_idx]

    ra_idx = adaptive_points((ra - c) * [-1, 1], (r_collar - c) * [-1, 1], ba - c, bb - c, n)
    ra_res = ra[ra_idx]

    res = np.array([[ba, bb], [l_collar, r_collar]])

    if append_shoulder:
        shlds = shoulders((la, ra), (la_idx[0], ra_idx[0]), (l_collar, r_collar))
        if len(shlds) > 0:
            res = np.concatenate([res, [shlds]])

    return np.concatenate([
        res, 
        [[l, r] for l, r in zip(list(la_res), list(ra_res))]
    ])


lms = detect_landmarks(file, n_detect - 1, shoulder_mode)

res = json.dumps(lms.tolist())

print(res)
