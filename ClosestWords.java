/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@kth.se           */
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;
  int closestDistance = -1;

  /**
   * cachedList is the word list the groups were last built from
   * wordsByLength holds the groups themselves
   */
  private static List<String> cachedList = null;
  private static List<List<String>> wordsByLength = null;

  /**
   * groupByLength makes a list of groups (lists) of words that have the same length
   * Rebuilds only when a different list object is passed in
   * The outer list is indexed by length, index 5 holds all 5-letter words
   * 
   * @param wordList the original list
   * 
   * @return wordsByLength the list of the groups of words with the same length
   * 
   */
  private static List<List<String>> groupByLength(List<String> wordList) {
    if (wordList != cachedList) {
      List<List<String>> groups = new ArrayList<List<String>>();
      for (String w : wordList) {
        while (groups.size() <= w.length()) {
          groups.add(new ArrayList<String>());
        }
        groups.get(w.length()).add(w);
      }
      wordsByLength = groups;
      cachedList = wordList;
    }
    return wordsByLength;
  }

  private static final int INF = Integer.MAX_VALUE / 2;

  
  private int[][] d;          // d[i][j] = distance between the i first characters in w2 and the j first characters in w1
  private String prevWord;
  private int validRows;      // rows 0..validRows in d are correct for prevWord
  private boolean prefixDead; // no word in the group with the prefix prevWord[0..validRows) can win

  /** Calculates the distance between w1 and w2
   *  
   * @param w1 the misspelled word
   * @param w2 a potential correct word
   * @param w1len length of w1
   * @param w2len length of w2
   * 
   * @return d[w2len][w1len] the distance between w1 and w2
   * @return (closestDistance + 1) signal that the word is invalid
   */ 
  int partDist(char[] w1, String w2, int w1len, int w2len) {
    int start = 0;
    while (start < validRows && prevWord.charAt(start) == w2.charAt(start)) {
      start++;
    }
    if (prefixDead && start == validRows) {
      return closestDistance + 1; // same prefix as an already rejected word
    }
    prevWord = w2;
    prefixDead = false;

    final int best = closestDistance;
    final int diagonal = w1len - w2len; // used for aborting dead words and for the band
    for (int i = start + 1; i <= w2len; i++) {
      final int[] prev = d[i - 1];
      final int[] cur = d[i];
      final char w2char = w2.charAt(i - 1);

      int lo = 1;
      int hi = w1len;
      if (best != -1) {
        lo = Math.max(lo, Math.max(i - best, i + diagonal - best)); // left edge: at most best columns left of the start line (i) and of the finish line (i + diagonal)
        hi = Math.min(hi, Math.min(i + best, i + diagonal + best)); // right edge: at most best columns right of the start line (i) and of the finish line (i + diagonal)
      }
      cur[0] = i;
      if (lo > 1)
        cur[lo - 1] = INF;
      if (hi < w1len)
        cur[hi + 1] = INF;

      for (int j = lo; j <= hi; j++) {
        int diag = prev[j - 1] + (w2char == w1[j - 1] ? 0 : 1);
        int del = prev[j] + 1;
        int ins = cur[j - 1] + 1;
        cur[j] = Math.min(diag, Math.min(del, ins));
      }

      // Every path through row i costs at least cur[j] + |j - c|, and that is smallest at j = c
      // because neighbouring cells differ by at most 1. So if cur[c] > best, this word, and every word
      // in this group with the same first i letters, cannot beat best.
      final int c = i + diagonal;
      if (best != -1 && c >= 1 && cur[c] > best) {
        validRows = i;
        prefixDead = true;
        return best + 1;
      }
    }
    validRows = w2len;
    return d[w2len][w1len];
  }

  /**
   * ClosestWords finds all words that have the smallest editing distance 
   * between w1 and the words form the wordList
   * 
   * @param w1 the misspelled word
   * @param wordlist the dictionary
   */
  public ClosestWords(String w1, List<String> wordList) {
    final int w1len = w1.length();
    final char[] w1chars = w1.toCharArray();
    final List<List<String>> groups = groupByLength(wordList);
    d = new int[groups.size()][w1len + 1];
    for (int j = 0; j <= w1len; j++) {
      d[0][j] = j;
    }

    /*  Start going through the dictionary with words of equal length
        (or close) and work outwards. 
        This finds a short closestDistance fast. */
    for (int diff = 0; ; diff++) {
      if (closestDistance != -1 && diff > closestDistance) {
        break;
      }
      if (w1len - diff < 0 && w1len + diff >= groups.size()) {
        break;
      }
      searchGroup(w1chars, w1len, w1len - diff, groups);
      if (diff > 0) {
        searchGroup(w1chars, w1len, w1len + diff, groups);
      }
    }

    if (closestWords != null)
      Collections.sort(closestWords);
  }

  /**
   * searchGroup searches a group of length w2len 
   * in the grouped dictionary and calculates the editing distance
   * Updates closestDistance and closestWords
   * 
   * @param w1 the misspelled word
   * @param w1len the length of w1
   * @param w2len the length of words in the group
   * @param groups the list of groups of words
   * 
   */
  private void searchGroup(char[] w1, int w1len, int w2len, List<List<String>> groups) {
    if (w2len < 0 || w2len >= groups.size()) {
      return;
    }
    prevWord = "";
    validRows = 0;
    prefixDead = false;
    for (String w2 : groups.get(w2len)) {
      if (closestDistance != -1 && Math.abs(w1len - w2len) > closestDistance) {
        break;
      }
      int dist = partDist(w1, w2, w1len, w2len);
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
