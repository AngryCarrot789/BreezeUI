package reghzy.breezeui.utils.function;

import java.util.Objects;

@FunctionalInterface
public interface IntAggregateOperator<T> {
    /**
     * Aggregates the given object value, using the given integer accumulator
     * @param value       The next value
     * @param accumulator The accumulator value, which was returned by the last aggregate function
     * @return The new accumulator value
     */
    int apply(T value, int accumulator);

    /**
     * Returns a {@link IntAggregateOperator} that takes a hash code and adds the hash code of an object to it
     * @param <T> Object type
     * @return A new hash code combining the operator and object hash code
     */
    static <T> IntAggregateOperator<T> hash() {
        return (o, h) -> 31 * h + Objects.hashCode(o);
    }
}
