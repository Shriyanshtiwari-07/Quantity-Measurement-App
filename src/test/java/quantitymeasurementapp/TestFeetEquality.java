package quantitymeasurementapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestFeetEquality {

    @Test
    void testEquality_SameValue() {
        FeetEquality a = new FeetEquality(1.0);
        FeetEquality b = new FeetEquality(1.0);
        assertTrue(a.equals(b));
    }

    @Test
    void testEquality_DifferentValue() {
        FeetEquality a = new FeetEquality(1.0);
        FeetEquality b = new FeetEquality(2.0);
        assertFalse(a.equals(b));
    }

    @Test
    void testEquality_NullComparison() {
        FeetEquality a = new FeetEquality(1.0);
        assertFalse(a.equals(null));
    }

    @Test
    void testEquality_NonNumericInput() {
        FeetEquality a = new FeetEquality(1.0);
        String str = "1.0";
        assertFalse(a.equals(str));
    }

    @Test
    void testEquality_SameReference() {
        FeetEquality a = new FeetEquality(1.0);
        assertTrue(a.equals(a));
    }
}