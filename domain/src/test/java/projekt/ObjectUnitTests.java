package projekt;

import java.util.function.Function;

import static org.tudalgo.algoutils.student.Student.crash;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectUnitTests<T> {

    private final Function<Integer, T> testObjectFactory;
    private final Function<T, String> toString;

    private T[] testObjects;
    private T[] testObjectsReferenceEquality;
    private T[] testObjectsContentEquality;

    public ObjectUnitTests(Function<Integer, T> testObjectFactory, Function<T, String> toString) {
        this.testObjectFactory = testObjectFactory;
        this.toString = toString;
    }

    @SuppressWarnings("unchecked")
    public void initialize(int testObjectCount) {
        crash(); // TODO: H12.1 - remove if implemented
    }

    public void testEquals() {
        crash(); // TODO: H12.1 - remove if implemented
    }

    public void testHashCode() {
        crash(); // TODO: H12.1 - remove if implemented
    }

    public void testToString() {
        crash(); // TODO: H12.1 - remove if implemented
    }

}
