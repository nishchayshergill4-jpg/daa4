package edu.assignment4;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EditDistanceTest {
    @Test void knownDistancesAgreeAcrossAllImplementations() {
        String[][] cases={{"kitten","sitting","3"},{"","abc","3"},{"same","same","0"},{"flaw","lawn","2"},{"a","","1"}};
        for(String[] c:cases) { int expected=Integer.parseInt(c[2]); assertEquals(expected,EditDistance.recursive(c[0],c[1]).distance()); assertEquals(expected,EditDistance.fullTable(c[0],c[1]).distance()); assertEquals(expected,EditDistance.twoRow(c[0],c[1]).distance()); }
    }
    @Test void tableAndRollingRowsHaveExpectedCellCounts() {
        assertEquals(6,EditDistance.fullTable("ab","xyz").operations());
        assertEquals(6,EditDistance.twoRow("ab","xyz").operations());
        assertEquals(2L*3,EditDistance.twoRow("ab","xyz").allocatedCells());
    }
}
