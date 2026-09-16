/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@kth.se           */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;
  int closestDistance = -1;

  private int[] prev;
  private int[] cur;

int partDist(String w1, String w2, int w1len, int w2len) {
    final int best = closestDistance;

    for (int j = 0; j <= w1len; j++) {
        prev[j] = j;
    }

    for (int i = 1; i <= w2len; i++) {
        char w2char = w2.charAt(i - 1);
        cur[0] = i;
        int rowMin = Integer.MAX_VALUE;

        int lo = (best < 0) ? 1 : Math.max(1, i - best);
        int hi = (best < 0) ? w1len : Math.min(w1len, i + best);
        if (lo > 1) {
          cur[lo - 1] = Integer.MAX_VALUE / 2;
        }
        if (hi < w1len) {
          cur[hi + 1] = Integer.MAX_VALUE / 2;
        }

        for (int j = lo; j <= hi; j++) {
            int diag = prev[j - 1] + (w2char == w1.charAt(j - 1) ? 0 : 1);
            int del = prev[j] + 1;
            int ins = cur[j - 1] + 1;
            cur[j] = Math.min(diag, Math.min(del, ins));
            if (cur[j] < rowMin) rowMin = cur[j];
        }

        if (best >= 0 && rowMin > best) {
            return best + 1;
        }
        int[] tmp = prev;
        prev = cur;
        cur = tmp;
    }
    return prev[w1len];
}

  public ClosestWords(String w1, List<String> wordList) {
    final int w1len = w1.length();
    prev = new int[w1len + 1];
    cur = new int[w1len + 1];
    for (String w2 : wordList) {
      int w2len = w2.length();
      if (closestDistance != -1 && Math.abs(w1len - w2len) > closestDistance)
        continue;
      int dist = partDist(w1, w2, w1len, w2len);
      // System.out.println("d(" + w1 + "," + w2 + ")=" + dist);
      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords = new LinkedList<String>();
        closestWords.add(w2);
      }
      else if (dist == closestDistance)
        closestWords.add(w2);
    }
  }

  int getMinDistance() {
    return closestDistance;
  }

  List<String> getClosestWords() {
    return closestWords;
  }
}
