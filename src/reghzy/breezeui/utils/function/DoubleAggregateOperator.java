package reghzy.breezeui.utils.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleAggregateOperator<T> {
    /**
     * Aggregates the given object value, using the given double accumulator
     * @param value       The next value
     * @param accumulator The accumulator value, which was returned by the last aggregate function
     * @return The new accumulator value
     */
    double apply(T value, double accumulator);

    /**
     * Returns a {@link DoubleAggregateOperator} that takes a hash code and adds the hash code of an object to it
     * @param <T> Object type
     * @return A new hash code combining the operator and object hash code
     */
    static <T> DoubleAggregateOperator<T> hash() {
        return (o, h) -> Objects.hashCode(o) ^ Double.hashCode(h);
    }
}
