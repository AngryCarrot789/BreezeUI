package reghzy.breezeui.utils.function;

import java.util.function.BiFunction;

@FunctionalInterface
public interface AggregateOperator<E, TAccumulate> extends BiFunction<E, TAccumulate, TAccumulate> {
    /**
     * Aggregates the given value, using the given accumulator
     * @param value       The next value
     * @param accumulator The accumulator value, which was returned by the last aggregate function
     * @return The new accumulator value
     */
    TAccumulate apply(E value, TAccumulate accumulator);

    /**
     * Returns an {@link AggregateOperator} from the given {@link BiFunction}
     */
    static <E, T> AggregateOperator<E, T> of(BiFunction<E, T, T> function) {
        return function::apply;
    }
}
