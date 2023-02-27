package projekt.h1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;

import java.util.*;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class TutorTests_H1_LocationTest {

    @Test
    public void testCompareToXValueDifferent() {
        Context context = contextBuilder()
            .subject("Location#compareTo(Location)")
            .build();

        Location location1 = new Location(1, 2);
        Location location2 = new Location(2, 2);

        assertTrue(location1.compareTo(location2) < 0, context, TR -> "Method did not return value lower than zero when location1.x < location2.x");
        assertTrue(location2.compareTo(location1) > 0, context, TR -> "Method did not return value higher than zero when location1.x > location2.x");
    }

    @Test
    public void testCompareToEqual() {
        Context context = contextBuilder()
            .subject("Location#compareTo(Location)")
            .build();

        Location location1 = new Location(1, 2);
        Location location2 = new Location(1, 2);

        assertEquals(0, location1.compareTo(location2), context, TR -> "Method did not return zero when location1.x == location2.x");
        assertEquals(0, location2.compareTo(location1), context, TR -> "Method did not return zero when location1.x == location2.x");
    }

    @Test
    public void testCompareToYValueDifferent() {
        Context context = contextBuilder()
            .subject("Location#compareTo(Location)")
            .build();

        Location location1 = new Location(1, 2);
        Location location2 = new Location(1, 3);

        assertTrue(location1.compareTo(location2) < 0, context, TR -> "Method did not return value lower than zero when location1.y < location2.y");
        assertTrue(location2.compareTo(location1) > 0, context, TR -> "Method did not return value higher than zero when location1.y > location2.y");
    }

    @ParameterizedTest
    @CsvSource("-1")
    public void testHashCode(int seed) {
        Random random = seed == -1 ? new Random() : new Random(seed);
        int iterations = 1000;

        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            Location location = new Location(random.nextInt(-1024, 1024), random.nextInt(-1024, 1024));

            boolean contains = false;
            for (Location other : locations) {
                if (location.getX() == other.getX() && location.getY() == other.getY()) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                locations.add(location);
            }
        }

        Set<Integer> hashes = new HashSet<>();

        for (Location location : locations) {
            hashes.add(location.hashCode());
        }

        Context context = contextBuilder()
            .subject("Location#hashCode()")
            .add("seed", seed)
            .add("generated locations", locations.size())
            .build();

        assertEquals(locations.size(), hashes.size(), context, TR -> "Method did not return unique hash codes for all locations");
    }

    @Test
    public void testEquals() {

        Location location = new Location(1, 2);
        testEquals(location, location, true, "Method did not return true for the same locations");

        testEquals(new Location(1, 2), new Location(1, 2), true, "Method did not return true for equal locations");

        testEquals(new Location(1, 2), new Location(1, 3), false, "Method did not return false for different locations");
        testEquals(new Location(2, 2), new Location(1, 2), false, "Method did not return false for different locations");
        testEquals(new Location(2, 2), new Location(1, 1), false, "Method did not return false for different locations");

        testEquals(new Location(1, 2), new Object(), false, "Method did not return false for different types");

        testEquals(new Location(1, 2), null, false, "Method did not return false for null");
    }
    private void testEquals(Object a, Object b, boolean expected, String description) {
        Context context = contextBuilder()
            .subject("Location#equals(Object)")
            .add("LocationA", Objects.toString(a))
            .add("LocationB", Objects.toString(b))
            .build();

        assertEquals(expected, a.equals(b), context, TR -> description);
    }

    @ParameterizedTest
    @CsvSource("1, 2")
    public void testToString(int x, int y) {
        Context context = contextBuilder()
            .add("x", x)
            .add("y", y)
            .subject("Location#equals(Object)")
            .build();

        Location location = new Location(x, y);

        assertEquals("(" + x + "," + y + ")", location.toString(), context, TR -> "Method did not return the correct string");
    }

}
