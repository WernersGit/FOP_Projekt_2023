package projekt;

import java.util.function.Function;

import static org.tudalgo.algoutils.student.Student.crash;
import static org.junit.jupiter.api.Assertions.*;


public class ComparableUnitTests<T extends Comparable<? super T>> {

    private final Function<Integer, T> testObjectFactory;

    private T[] testObjects;

    public ComparableUnitTests(Function<Integer, T> testObjectFactory) {
        this.testObjectFactory = testObjectFactory;
    }

    @SuppressWarnings("unchecked")
    public void initialize(int testObjectCount) {
        crash(); // TODO: H12.1 - remove if implemented
    }

    public void testBiggerThen() {
        crash(); // TODO: H12.1 - remove if implemented
    }

    @SuppressWarnings("EqualsWithItself")
    public void testAsBigAs() {
        crash(); // TODO: H12.1 - remove if implemented
    }

    public void testLessThen() {
        crash(); // TODO: H12.1 - remove if implemented
    }
}
