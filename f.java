  int partDist(String w1, String w2, int w1len, int w2len) {
    final int k = closestDistance;

    if (k >= 0 && Math.abs(w1len - w2len) > k) {
        return k + 1;
    }

    for (int j = 0; j <= w1len; j++) {
        prev[j] = j;
    }

    for (int i = 1; i <= w2len; i++) {
        char w2char = w2.charAt(i - 1);
        cur[0] = i;
        int rowMin = Integer.MAX_VALUE;
        int lo = (k < 0) ? 1 : Math.max(1, i - k);
        int hi = (k < 0) ? w1len : Math.min(w1len, i + k);
      if (lo > 1) {
        cur[lo - 1] = Integer.MAX_VALUE / 2; 
      }
      if (hi < w1len) {
        cur[hi + 1] = Integer.MAX_VALUE / 2;
      }
      for (int j = lo; j <= hi; j++) { 
        int diag = prev[j-1] + (w2char == w1.charAt(j-1) ? 0 : 1);
        int del = prev[j] + 1;
        int ins = cur[j-1] + 1;
        cur[j] = Math.min(diag, Math.min(del, ins));
        if (cur[j] < rowMin) {
          rowMin = cur[j];
        }
      }
      if (k >= 0 && rowMin > k) {
          return k + 1;
      }
      int[] tmp = prev;
      prev = cur;
      cur = tmp;
    }
    return prev[w1len];
  }


    int partDist(String w1, String w2, int w1len, int w2len) {
    for (int j = 0; j <= w1len; j++) {
        prev[j] = j;
    }

    for (int i = 1; i <= w2len; i++) {
        char w2char = w2.charAt(i - 1);
        cur[0] = i;
        int rowMin = Integer.MAX_VALUE;
      for (int j = 1; j <= w1len; j++) { 
        int diag = prev[j-1] + (w2char == w1.charAt(j-1) ? 0 : 1);
        int del = prev[j] + 1;
        int ins = cur[j-1] + 1;
        cur[j] = Math.min(diag, Math.min(del, ins));
        if (cur[j] < rowMin) {
          rowMin = cur[j];
        }
      }
      if (closestDistance != -1 && rowMin > closestDistance) {
          return closestDistance + 1;
      }
      int[] tmp = prev;
      prev = cur;
      cur = tmp;
    }
    return prev[w1len];
  }