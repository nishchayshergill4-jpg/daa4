package edu.assignment4;

import java.util.Objects;

/** Levenshtein distance variants. A cell evaluation is one DP state/call; counters are local per invocation. */
public final class EditDistance {
    private EditDistance() {}
    public record Result(int distance, long operations, long allocatedCells) {}

    public static Result recursive(String a, String b) {
        Objects.requireNonNull(a); Objects.requireNonNull(b);
        Counter c = new Counter();
        int d = rec(a, a.length(), b, b.length(), c);
        return new Result(d, c.ops, 0);
    }
    private static int rec(String a, int i, String b, int j, Counter c) {
        c.ops++;
        if (i == 0) return j;
        if (j == 0) return i;
        if (a.charAt(i-1) == b.charAt(j-1)) return rec(a, i-1, b, j-1, c);
        return 1 + Math.min(rec(a,i-1,b,j,c), Math.min(rec(a,i,b,j-1,c), rec(a,i-1,b,j-1,c)));
    }
    public static Result fullTable(String a, String b) {
        Objects.requireNonNull(a); Objects.requireNonNull(b);
        int m=a.length(), n=b.length(); int[][] d=new int[m+1][n+1]; long ops=0;
        for(int i=0;i<=m;i++) d[i][0]=i;
        for(int j=0;j<=n;j++) d[0][j]=j;
        for(int i=1;i<=m;i++) for(int j=1;j<=n;j++) { ops++; int cost=a.charAt(i-1)==b.charAt(j-1)?0:1; d[i][j]=Math.min(Math.min(d[i-1][j]+1,d[i][j-1]+1),d[i-1][j-1]+cost); }
        return new Result(d[m][n],ops,(long)(m+1)*(n+1));
    }
    public static Result twoRow(String a, String b) {
        Objects.requireNonNull(a); Objects.requireNonNull(b);
        // Keep the shorter string on the column axis: memory is O(min(m,n)).
        if(b.length()>a.length()) { String t=a; a=b; b=t; }
        int m=a.length(), n=b.length(); int[] prev=new int[n+1], cur=new int[n+1];
        for(int j=0;j<=n;j++) prev[j]=j;
        long ops=0;
        for(int i=1;i<=m;i++) { cur[0]=i; for(int j=1;j<=n;j++) { ops++; int cost=a.charAt(i-1)==b.charAt(j-1)?0:1; cur[j]=Math.min(Math.min(prev[j]+1,cur[j-1]+1),prev[j-1]+cost); } int[] t=prev;prev=cur;cur=t; }
        return new Result(prev[n],ops,(long)2*(n+1));
    }
    private static final class Counter { long ops; }
}
