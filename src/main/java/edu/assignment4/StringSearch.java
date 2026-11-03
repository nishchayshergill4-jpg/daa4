package edu.assignment4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Exact, case-sensitive search. Comparisons count each attempted text/pattern character equality test. */
public final class StringSearch {
    private StringSearch() {}
    public record Result(List<Integer> matches, long comparisons) { public Result { matches=List.copyOf(matches); } }

    public static Result naive(String text, String pattern) {
        Objects.requireNonNull(text); Objects.requireNonNull(pattern);
        List<Integer> hits=new ArrayList<>(); long c=0; int n=text.length(),m=pattern.length();
        if(m==0) { for(int i=0;i<=n;i++) hits.add(i); return new Result(hits,0); }
        for(int s=0;s<=n-m;s++) { int j=0; while(j<m) { c++; if(text.charAt(s+j)!=pattern.charAt(j)) break; j++; } if(j==m) hits.add(s); }
        return new Result(hits,c);
    }
    /** Public prefix-function / longest-proper-prefix-suffix array. */
    public static int[] lps(String pattern) {
        Objects.requireNonNull(pattern); int[] lps=new int[pattern.length()]; int len=0,i=1;
        while(i<pattern.length()) { if(pattern.charAt(i)==pattern.charAt(len)) lps[i++]=++len; else if(len!=0) len=lps[len-1]; else lps[i++]=0; }
        return lps;
    }
    public static Result kmp(String text, String pattern) {
        Objects.requireNonNull(text); Objects.requireNonNull(pattern);
        List<Integer> hits=new ArrayList<>(); int n=text.length(),m=pattern.length();
        if(m==0) { for(int i=0;i<=n;i++) hits.add(i); return new Result(hits,0); }
        int[] lps=lps(pattern); int i=0,j=0; long c=0;
        while(i<n) { c++; if(text.charAt(i)==pattern.charAt(j)) { i++;j++; if(j==m) { hits.add(i-j); j=lps[j-1]; } } else if(j!=0) j=lps[j-1]; else i++; }
        return new Result(hits,c);
    }
}
