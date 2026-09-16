public class Pseudo {
    int[][] M = new int[w1.length() + 1][w2.length() + 1];
    for (int i = 0; i <= w1.length(); i++) {
        M[i][0] = i;
    }
    for (int j = 0; j <= w2.length(); j++) {
        M[0][j] = j;
    }

    for (int i = 1; i <= w1.length(); i++) {
        for (int j = 1; j <= w2.length(); j++) { 
            int diag = M[i-1][j-1] + (w1.charAt(i-1) == w2.charAt(j-1) ? 0 : 1);
            int del  = M[i-1][j] + 1;
            int ins  = M[i][j-1] + 1;
            M[i][j] = Math.min(diag, Math.min(del, ins));
        }
    }
}
