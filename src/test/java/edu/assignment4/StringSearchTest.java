package edu.assignment4;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StringSearchTest {
    @Test void publicLpsArrayIsCorrect() { assertArrayEquals(new int[]{0,0,1,2,3,0},StringSearch.lps("ababac")); assertArrayEquals(new int[]{0,1,2,3},StringSearch.lps("aaaa")); assertArrayEquals(new int[0],StringSearch.lps("")); }
    @Test void naiveAndKmpReturnAllOverlappingMatches() { assertEquals(List.of(0,1,2),StringSearch.kmp("aaaaa","aaa").matches()); assertEquals(StringSearch.naive("aaaaa","aaa").matches(),StringSearch.kmp("aaaaa","aaa").matches()); }
    @Test void emptyPatternMatchesEveryBoundary() { assertEquals(List.of(0,1,2,3),StringSearch.kmp("abc","").matches()); assertEquals(List.of(0),StringSearch.naive("","").matches()); }
    @Test void mismatchesReturnNoMatches() { assertTrue(StringSearch.kmp("abcdef","xyz").matches().isEmpty()); }
}
