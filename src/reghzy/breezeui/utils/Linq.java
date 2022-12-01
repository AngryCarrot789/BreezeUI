package reghzy.breezeui.utils;


import org.jetbrains.annotations.Contract;
import reghzy.breezeui.core.utils.Maths;
import reghzy.breezeui.utils.function.IntAggregateOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * An ordered ArrayList-based utility class for manipulating (filtering, mapping, etc) collections of items, in a builder/query-styled way
 * <p>
 * This is similar to C# linq
 * </p>
 * <h3>
 * Most of these functions are unforgiving, and do not expect user inputs (e.g suppliers and functions) to throw.
 * If they do, it could corrupt the state of the Linq's items and therefore make them unusable
 * </h3>
 * @param <E> The type of elements this linq instance contains
 * @author REghZy
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public final class Linq<E> implements Iterable<E>, Cloneable {
    private static final Random RANDOM = new Random();

    @Nonnull
    private ArrayList<E> items;

    private Linq(@Nonnull ArrayList<E> items) {
        this.items = items;
    }

    private Linq(@Nonnull E[] items) {
        this(new ArrayList<E>(Arrays.asList(items)));
    }

    /**
     * Returns an empty linq
     */
    public static <E> Linq<E> of() {
        return new Linq<E>(new ArrayList<E>());
    }

    /**
     * Returns a copy of the given linq, copying the underlying collection (but not the items themselves; a shallow list copy)
     * @param linq The linq to copy the list of
     * @param <E>  The linq's element type
     * @return A new linq with a copied list, containing the same elements
     */
    public static <E> Linq<E> of(@Nonnull Linq<E> linq) {
        return new Linq<E>(new ArrayList<E>(linq.items));
    }

    /**
     * Creates an empty linq of the given initial capacity
     */
    public static <E> Linq<E> of(int initialCapacity) {
        return new Linq<E>(new ArrayList<E>(initialCapacity));
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     */
    public static <E> Linq<E> of(@Nullable Collection<? extends E> collection) {
        return new Linq<E>(collection != null ? new ArrayList<E>(collection) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     */
    public static <E> Linq<E> of(@Nullable Iterable<? extends E> iterable) {
        return new Linq<E>(iterable != null ? ListUtils.toList(iterable) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     */
    public static <E> Linq<E> of(@Nullable Iterator<? extends E> iterable) {
        return new Linq<E>(iterable != null ? ListUtils.toList(iterable) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given array of items
     * The array will be copied, so that this linq never affects the array or it's elements
     */
    public static <E> Linq<E> of(@Nullable E[] array) {
        return array != null ? new Linq<E>(array) : of();
    }

    public static Linq<Byte> of(@Nullable byte[] array) { return array != null ? new Linq<Byte>(Memory.wrap(array)) : of(); }

    public static Linq<Short> of(@Nullable short[] array) { return array != null ? new Linq<Short>(Memory.wrap(array)) : of(); }

    public static Linq<Integer> of(@Nullable int[] array) { return array != null ? new Linq<Integer>(Memory.wrap(array)) : of(); }

    public static Linq<Long> of(@Nullable long[] array) { return array != null ? new Linq<Long>(Memory.wrap(array)) : of(); }

    public static Linq<Float> of(@Nullable float[] array) { return array != null ? new Linq<Float>(Memory.wrap(array)) : of(); }

    public static Linq<Double> of(@Nullable double[] array) { return array != null ? new Linq<Double>(Memory.wrap(array)) : of(); }

    public static Linq<Character> of(@Nullable char[] array) { return array != null ? new Linq<Character>(Memory.wrap(array)) : of(); }

    public static Linq<Boolean> of(@Nullable boolean[] array) { return array != null ? new Linq<Boolean>(Memory.wrap(array)) : of(); }

    /**
     * Uses the given arraylist as this linq's backing list. The given array list cannot be null
     * <p>
     * This method is should only really be used if the given array list will not be used else where (because functions such
     * as {@link Linq#where(Predicate)} may modify the list, whereas other functions may not). This function only really
     * exists to squeeze out as much performance as possible, as this function doesn't clone the list (unlike every of() function)
     * </p>
     */
    @SuppressWarnings("ConstantConditions")
    public static <E> Linq<E> wrap(@Nonnull ArrayList<E> arrayList) {
        if (arrayList != null)
            return new Linq<E>(arrayList);

        throw new IllegalArgumentException("Items cannot be null");
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     * <p>
     * This differs from of() such that the generic parameter of the collection is unspecified, thus
     * allowing raw-type collections to be passed, and an explicit generic type can be inferred
     * </p>
     */
    public static <E> Linq<E> ofRaw(@Nullable Collection<?> collection) {
        return new Linq<E>(collection != null ? new ArrayList<E>((Collection) collection) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     * <p>
     * This differs from of() such that the generic parameter of the collection is unspecified, thus
     * allowing raw-type collections to be passed, and an explicit generic type can be inferred
     * </p>
     */
    public static <E> Linq<E> ofRaw(@Nullable Iterable<?> collection) {
        return new Linq<E>(collection != null ? ListUtils.toList((Iterable<? extends E>) collection) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given collection of items.
     * The collection will be copied, so that this linq does not affect the original collection
     * <p>
     * This differs from of() such that the generic parameter of the collection is unspecified, thus
     * allowing raw-type collections to be passed, and an explicit generic type can be inferred
     * </p>
     */
    public static <E> Linq<E> ofRaw(@Nullable Iterator<?> collection) {
        return new Linq<E>(collection != null ? ListUtils.toList((Iterator<? extends E>) collection) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given map's keys
     * The collection will be copied, so that this linq never affects the map
     */
    public static <E> Linq<E> ofKeys(@Nullable Map<? extends E, ?> collection) {
        return new Linq<E>(collection != null ? new ArrayList<E>(collection.keySet()) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given map's values
     * The collection will be copied, so that this linq never affects the map
     */
    public static <E> Linq<E> ofValues(@Nullable Map<?, ? extends E> map) {
        return new Linq<E>(map != null ? new ArrayList<E>(map.values()) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given map's keys
     * The collection will be copied, so that this linq never affects the map
     */
    public static <E> Linq<E> ofRawKeys(@Nullable Map<?, ?> collection) {
        return new Linq<E>(collection != null ? new ArrayList<E>((Collection<? extends E>) collection.keySet()) : new ArrayList<E>());
    }

    /**
     * Creates a new linq instance, using the given map's values
     * The collection will be copied, so that this linq never affects the map
     */
    public static <E> Linq<E> ofRawValues(@Nullable Map<?, ?> collection) {
        return new Linq<E>(collection != null ? new ArrayList<E>((Collection<? extends E>) collection.values()) : new ArrayList<E>());
    }

    /**
     * Creates a linq containing the given enum's constant values
     * @param clazz The enum class type
     * @param <E>   The enum type
     */
    public static <E> Linq<E> ofEnum(@Nonnull Class<E> clazz) {
        return new Linq<E>(ListUtils.toList(clazz.getEnumConstants()));
    }

    /**
     * Creates a linq containing the given varargs
     */
    @SafeVarargs
    public static <E> Linq<E> ofVars(E... array) {
        return new Linq<E>(array != null ? ListUtils.toList(array) : new ArrayList<E>());
    }

    /**
     * Creates a Linq containing a single element
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Linq<T> single(T value) {
        return new Linq<T>(ListUtils.singleElement(value));
    }

    /**
     * If the given optional has a value, then {@link Linq#of(Collection)} is returned with the
     * optional's value. Otherwise, an empty {@link Linq} is returned
     */
    public static <T> Linq<T> optional(Optional<Collection<T>> optional) {
        return optional.map(Linq::of).orElseGet(Linq::of);
    }

    /**
     * If the given optional has a value, then {@link Linq#single(Object)} is returned with the
     * optional's value. Otherwise, an empty {@link Linq} is returned
     */
    public static <T> Linq<T> optionalSingle(Optional<T> optional) {
        return optional.map(Linq::single).orElseGet(Linq::of);
    }

    public static Linq<Integer> ofRange(int startInclusive, int endExclusive) {
        ArrayList<Integer> list = new ArrayList<Integer>(endExclusive - startInclusive);
        for (int i = startInclusive; i < endExclusive; ++i) {
            list.add(i);
        }

        return Linq.wrap(list);
    }

    public Linq<E> append(E e) {
        this.items.add(e);
        return this;
    }

    public Linq<E> append(E a, E b) {
        this.items.add(a);
        this.items.add(b);
        return this;
    }

    public Linq<E> append(E a, E b, E c) {
        this.items.add(a);
        this.items.add(b);
        this.items.add(c);
        return this;
    }

    @SafeVarargs
    public final Linq<E> append(E... a) {
        this.items.addAll(Arrays.asList(a));
        return this;
    }

    public final Linq<E> append(Collection<E> a) {
        this.items.addAll(a);
        return this;
    }

    /**
     * A helper function for {@link Linq#random()}, which throws an {@link IllegalStateException} if no random element could be generated
     */
    @Nonnull
    @Contract(pure = true)
    public E getRandom() {
        return this.getRandom(RANDOM);
    }

    /**
     * A helper function for {@link Linq#random(Random)}, which throws an {@link IllegalStateException} if no random element could be generated
     */
    @Nonnull
    @Contract(pure = true)
    public E getRandom(Random random) {
        Optional<E> optional = random(random);
        if (optional.isPresent())
            return optional.get();

        throw new IllegalStateException("Could not find a non-null element for random selection");
    }

    @Contract(pure = true)
    public Optional<E> random() {
        return random(RANDOM);
    }

    /**
     * Returns a random non-null element, or nothing if this linq is empty or full of null values
     */
    @Contract(pure = true)
    public Optional<E> random(@Nonnull Random random) {
        return random(random, Collections.emptyList());
    }

    @Contract(pure = true)
    public Optional<E> random(@Nonnull Collection<E> exclusion) {
        return random(RANDOM, exclusion);
    }

    @Contract(pure = true)
    public Optional<E> random(@Nonnull Random random, @Nonnull Collection<E> exclusion) {
        if (this.isEmpty()) {
            return Optional.empty();
        }

        ArrayList<E> list = of(this).removeNull().removeAll(exclusion).toList();
        switch (list.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(list.get(0));
            default:
                return Optional.of(list.get(random.nextInt(list.size())));
        }
    }

    /**
     * Returns the element at the specified position in this linq
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    @Contract(pure = true)
    public E get(int index) {
        return this.items.get(index);
    }

    /**
     * Provides a consumer where the parameter is a reference to this linq's add function, allowing the external addition of elements
     * <p>
     * This is basically a lambda alternative to linq's add or addAll functions
     * </p>
     */
    public Linq<E> consume(Consumer<Consumer<E>> thing) {
        thing.accept(this.items::add);
        return this;
    }

    /**
     * Provides a consumer where the parameter is a {@link BiConsumer} (which takes the current element and a reference to this linq's add function)
     * <p>
     * This is basically a lambda alternative to linq's add or addAll functions
     * </p>
     * <p>
     * This will clear the previous values in this linq (replaceAll essentially)
     * </p>
     */
    public Linq<E> consumeAll(BiConsumer<E, Consumer<E>> thing) {
        if (this.isEmpty())
            return this;

        ArrayList<E> list = new ArrayList<E>(this.items.size());
        this.items.forEach(t -> thing.accept(t, list::add));
        this.items = list;

        return this;
    }

    /**
     * Counts the number of items that match the given predicate
     */
    @Contract(pure = true)
    public int count(Predicate<E> predicate) {
        if (this.isEmpty())
            return 0;

        int count = 0;
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (predicate.test(list.get(i))) {
                ++count;
            }
        }

        return count;
    }

    /**
     * Filters the items in this linq
     * <p>
     * True complement:
     *     <ul>
     *         Keeps items where the given predicate match the item, and removes items that do not match (where())
     *     </ul>
     * </p>
     * <p>
     *     False complement:
     *     <ul>
     *         Removes items where the given predicate match the item, and keeps items that match (removeIf())
     *     </ul>
     * </p>
     * @param predicate  The predicate
     * @param complement The complement to compare with the test() result of the predicate
     */
    @SuppressWarnings("ListRemoveInLoop")
    private Linq<E> filter(Predicate<E> predicate, boolean complement) {
        ArrayList<E> list = this.items;
        int size = list.size(), i = 0, cursor = 0;
        while (i < size) {
            E value = list.get(i++);
            if (predicate.test(value) == complement) {
                list.set(cursor++, value); // keep
            }
        }

        if (i != cursor) {
            for (int k = size - 1; k >= cursor; --k) {
                list.remove(k);
            }
        }

        return this;
    }

    private <T> Linq<E> filter(T paramObj, BiPredicate<E, T> predicate, boolean complement) {
        ArrayList<E> list = this.items;
        int size = list.size(), i = 0, cursor = 0;
        while (i < size) {
            E value = list.get(i++);
            if (predicate.test(value, paramObj) == complement) {
                list.set(cursor++, value); // keep
            }
        }

        if (i != cursor) {
            for (int k = size - 1; k >= cursor; --k) {
                list.remove(k);
            }
        }

        return this;
    }

    private Linq<E> filterRaw(IterationFilter<E> consumer) {
        ArrayList<E> items = this.items;
        int size = items.size(), i = 0, cursor = 0;
        while (i < size) {
            E value = items.get(i);
            if (consumer.keepItem(items, value, i)) {
                if (cursor != i) {
                    items.set(cursor, value);
                }

                ++cursor;
            }
            ++i;
        }

        if (i != cursor) {
            for (int k = size - 1; k >= cursor; --k) {
                items.remove(k);
            }
        }

        return this;
    }

    /**
     * Keeps all items which are accepted by the given predicate (removes items that are not accepted)
     */
    public Linq<E> where(Predicate<E> predicate) {
        // this.items.removeIf(a -> predicate.test(a) ^ true); // predicate.test(a) == false
        // var ^ true is a micro-optimisation for !var
        return filter(predicate, true);
    }

    public <T> Linq<E> where(T parameter, BiPredicate<E, T> predicate) {
        // this.items.removeIf(a -> predicate.test(a) ^ true); // predicate.test(a) == false
        // var ^ true is a micro-optimisation for !var
        return filter(parameter, predicate, true);
    }

    /**
     * Removes any items that are accepted by the given predicate
     */
    public Linq<E> removeIf(Predicate<E> predicate) {
        // this.items.removeIf(a -> predicate.test(a)); // predicate.test(a) == true
        return filter(predicate, false);
    }

    public <T> Linq<E> removeIf(T parameter, BiPredicate<E, T> predicate) {
        // this.items.removeIf(a -> predicate.test(a)); // predicate.test(a) == true
        return filter(parameter, predicate, false);
    }

    /**
     * Projects each item into another form
     */
    @SuppressWarnings("unchecked")
    public <T> Linq<T> map(Function<E, T> projector) {
        // these 2 pretty much run at the same speed, but the method reference
        // seems to run a tiny bit faster (not by much though)
        // this.items.replaceAll(a -> (E) projector.apply(a));
        // this.items.replaceAll(((Function<E,E>) projector)::apply);

        // using replaceAll requires apparently allocating some lambda class which
        // probably converts the projector function into a unary operator
        // -- future me for extra knowledge xd, any lambda that captures local variables requires
        // -- creating a new lambda class instance to hold the variables. It's just like using an
        // -- anonymous class, but with cleaner code
        // but the standard for loop seems to actually be faster (by 10% ish)
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            list.set(i, (E) projector.apply(list.get(i)));
        }

        // This solutions seems to be about 6% slower than a standard for loop
        // even after the generated field accessor is ready (pre-generated in static constructor)
        // This is likely due to the reflection overhead
        // And with the above code, the excessive array bound checks by ArrayList.get and ArrayList.set
        // are probably optimised away and the methods inlined
        // Object[] array;
        // try {
        //     array = (Object[]) ELEMENT_DATA_ACCESS.get(this.items);
        // }
        // catch (IllegalAccessException e) {
        //     throw new Error(e);
        // }
        // for (int i = 0; i < array.length; ++i) {
        //     array[i] = projector.apply((E) array[i]);
        // }

        // an iterator based solution which involves creating a new arraylist
        // is about 16% slower than using a standard for loop to replace
        // this is forgiving however, in case projector fails
        // but linq is not supposed to be forgiving, therefore failure is an option
        // ArrayList<R> list = new ArrayList<R>();
        // for (E value : this.items) {
        //     list.add(projector.apply(value));
        // }
        // this.items = (ArrayList<E>) list;
        return (Linq<T>) this;
    }

    /**
     * Projects each item into a new form. If the projector returns null, it will be discarded
     */
    @SuppressWarnings({"rawtypes", "unchecked", "ListRemoveInLoop"})
    public <T> Linq<T> mapNonNull(Function<E, T> projector) {
        ArrayList list = this.items;
        int size = list.size(), i = 0, cursor = 0;
        while (i < size) {
            T value = projector.apply((E) list.get(i++));
            if (value != null) {
                list.set(cursor++, value);
            }
        }

        if (i != cursor) {
            for (int k = size - 1; k >= cursor; --k) {
                list.remove(k);
            }
        }

        return (Linq<T>) this;
    }

    public Linq<E> ifEmpty(Runnable runnable) {
        if (this.isEmpty()) {
            runnable.run();
        }

        return this;
    }

    /**
     * Throws the exception supplied by the supplier if this linq is empty
     * @param supplier The exception supplier
     * @param <X>      The exception type
     * @return The linq, if it is not empty
     * @throws X The exception type to throw if empty
     */
    public <X extends Throwable> Linq<E> throwEmpty(Supplier<? extends X> supplier) throws X {
        if (this.isEmpty()) {
            throw supplier.get();
        }
        else {
            return this;
        }
    }

    public interface OptionalMapper<T, R> extends Function<T, Optional<R>> {
        Optional<R> apply(T value);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "ListRemoveInLoop"})
    public <T> Linq<T> mapPresent(OptionalMapper<E, T> projector) {
        // return this.<Optional<T>>cast().where(Optional::isPresent).map(Optional::get);
        ArrayList list = this.items;
        int size = list.size(), i = 0, cursor = 0;
        while (i < size) {
            Optional<T> value = projector.apply((E) list.get(i++));
            if (value.isPresent()) {
                list.set(cursor++, value.get());
            }
        }

        if (i != cursor) {
            for (int k = size - 1; k >= cursor; --k) {
                list.remove(k);
            }
        }

        return (Linq<T>) this;
    }

    /**
     * Projects each item into a collection, and adds each item from the collection into a single list, resulting in a Linq of the same size or greater
     */
    @SuppressWarnings("unchecked")
    public <R> Linq<R> mapAll(Function<E, Collection<R>> projector) {
        if (this.hasElements()) {
            ArrayList<R> list = new ArrayList<R>(this.items.size());
            for (int i = 0, size = this.items.size(); i < size; ++i) {
                list.addAll(projector.apply(this.items.get(i)));
            }

            this.items = (ArrayList<E>) list;
        }

        return (Linq<R>) this;
    }

    /**
     * Projects each item into a new form, and returns the first non-null projected instance
     */
    public <R> Optional<R> mapFirst(Function<E, R> projector) {
        if (this.isEmpty())
            return Optional.empty();

        for (int i = 0, size = this.items.size(); i < size; ++i) {
            R projected = projector.apply(this.items.get(i));
            if (projected != null) {
                return Optional.of(projected);
            }
        }

        return Optional.empty();
    }

    /**
     * Provides a consumer where the parameter is a {@link BiConsumer} (which takes the current element and a reference to this linq's add function).
     * This allows elements to be externally added
     * <p>
     * This is basically a lambda alternative to linq's add or addAll functions
     * </p>
     * <p>
     * This will clear the previous values in this linq (replaceAll essentially)
     * </p>
     */
    public <T> Linq<T> mapConsumeAll(BiConsumer<E, Consumer<T>> thing) {
        if (this.hasElements()) {
            ArrayList<T> list = new ArrayList<T>(this.items.size());
            this.items.forEach(t -> thing.accept(t, list::add));
            this.items = (ArrayList<E>) list;
        }

        return (Linq<T>) this;
    }

    /**
     * Projects each item into their classes via {@link Object#getClass()}
     * <p>
     * If the element is itself a class, then the element will be used
     * instead of {@link Object#getClass()} (as that would return a class of type class)
     * </p>
     * <p>
     * If the element is null, then null will be used, meaning this function does not remove null elements
     * </p>
     */
    @SuppressWarnings("unchecked")
    public Linq<Class<E>> classes() {
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            E item = list.get(i);
            this.items.set(i, item instanceof Class ? item : (item != null ? (E) item.getClass() : null));
        }

        return (Linq<Class<E>>) this;
    }

    /**
     * Assembles a map, keying each element's class to a list of elements, and returns the map's entry set
     * <p>
     * Simplified: Returns a linq of entries, keying Class[E] to ArrayList[E]
     * </p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Linq<Map.Entry<Class<E>, ArrayList<E>>> pairClasses() {
        return (Linq) pairMap(Object::getClass);
    }

    /**
     * Assembles a map, using the given toKeyFunction to map a custom key to a list of elements, and returns the map's entry set
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <TKey> Linq<Map.Entry<TKey, ArrayList<E>>> pairMap(Function<E, TKey> toKeyFunction) {
        ArrayList<E> items = this.items;
        HashMap<TKey, ArrayList<E>> map = new HashMap<TKey, ArrayList<E>>();
        for (int i = 0, size = items.size(); i < size; ++i) {
            putMultiMap(map, items.get(i), toKeyFunction);
        }

        this.items = (ArrayList) new ArrayList(map.entrySet());
        return (Linq) this;
    }

    private static <T, TKey> void putMultiMap(HashMap<TKey, ArrayList<T>> map, T item, Function<T, TKey> toKeyFunction) {
        map.computeIfAbsent(toKeyFunction.apply(item), k -> new ArrayList<T>()).add(item);
    }

    public <B> Linq<Map.Entry<B, E>> pairAsKey(Function<E, B> toKey) {
        return this.pair(toKey, a -> a);
    }

    public <B> Linq<Map.Entry<E, B>> pairAsValue(Function<E, B> toValue) {
        return this.pair(a -> a, toValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <K, V> Linq<Map.Entry<K, V>> pair(Function<E, K> toKey, Function<E, V> toValue) {
        if (this.hasElements()) {
            ArrayList listA = this.items;
            for (int i = 0, size = listA.size(); i < size; ++i) {
                E item = (E) listA.get(i);
                listA.set(i, new Tuple2<K, V>(toKey.apply(item), toValue.apply(item)));
            }
        }

        return (Linq<Map.Entry<K, V>>) this;
    }

    public interface AsNumberFunction<T> extends Function<T, Number> {
        @Nonnull
        Number apply(T value);
    }

    /**
     * Returns an optional wrapper of a value which returns the smallest number using the given {@link AsNumberFunction}
     * <p>
     * Returns {@link Optional#empty()} if this linq is empty, or all elements are null
     * </p>
     */
    public Optional<E> min(AsNumberFunction<E> consumer) {
        if (this.isEmpty())
            return Optional.empty();

        // use the first object, not null and a max value, because
        // the consumer may return the max number value
        E object = this.items.get(0);
        Number delta = consumer.apply(object);
        for (int i = 1, size = this.items.size(); i < size; ++i) {
            Number value = consumer.apply(this.items.get(i));
            if (value.doubleValue() < delta.doubleValue()) {
                delta = value;
                object = this.items.get(i);
            }
        }

        return Optional.ofNullable(object);
    }

    public Optional<Number> minDelta(AsNumberFunction<E> consumer) {
        if (this.isEmpty())
            return Optional.empty();

        // use the first object, not null and a max value, because
        // the consumer may return the max number value
        Number delta = consumer.apply(this.items.get(0));
        for (int i = 1, size = this.items.size(); i < size; ++i) {
            Number value = consumer.apply(this.items.get(i));
            if (value.doubleValue() < delta.doubleValue()) {
                delta = value;
            }
        }

        return Optional.of(delta);
    }

    /**
     * Returns an optional wrapper of a value which returns the highest number value using the given {@link AsNumberFunction}
     * <p>
     * Returns {@link Optional#empty()} if this linq is empty, or all elements are null
     * </p>
     */
    public Optional<E> max(AsNumberFunction<E> consumer) {
        if (this.isEmpty()) {
            return Optional.empty();
        }

        E object = this.items.get(0);
        Number delta = consumer.apply(object);
        for (int i = 1, size = this.items.size(); i < size; ++i) {
            Number value = consumer.apply(this.items.get(i));
            if (value.doubleValue() > delta.doubleValue()) {
                delta = value;
                object = this.items.get(i);
            }
        }

        return Optional.ofNullable(object);
    }

    public Optional<Number> maxDelta(AsNumberFunction<E> consumer) {
        if (this.isEmpty()) {
            return Optional.empty();
        }

        Number delta = consumer.apply(this.items.get(0));
        for (int i = 1, size = this.items.size(); i < size; ++i) {
            Number value = consumer.apply(this.items.get(i));
            if (value.doubleValue() > delta.doubleValue()) {
                delta = value;
            }
        }

        return Optional.of(delta);
    }

    /**
     * Returns the first non-null element in this linq
     */
    public Optional<E> first() {
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            E item = this.items.get(i);
            if (item != null) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the first non-null element in this linq that matches the predicate
     */
    public Optional<E> first(Predicate<E> predicate) {
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            E item = list.get(i);
            if (item != null && predicate.test(item)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the last element in this linq, or null if there are none
     */
    public Optional<E> last() {
        for (int i = this.items.size() - 1; i >= 0; --i) {
            E item = this.items.get(i);
            if (item != null) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the last element in this linq that matches the predicate, or null if there are none or none are accepted
     */
    public Optional<E> last(Predicate<E> predicate) {
        for (int i = this.items.size() - 1; i >= 0; --i) {
            E item = this.items.get(i);
            if (item != null && predicate.test(item)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    /**
     * Tries to take a maximum of the given count of items
     */
    public Linq<E> take(int count) {
        if (count > 0) {
            int size = this.items.size();
            if (count < size) {
                this.items.subList(count, size).clear();
            }
        }
        else if (count != 0) {
            throw new IndexOutOfBoundsException("Cannot take a negative number: " + count);
        }

        return this;
    }

    public Linq<E> skip(int count) {
        if (count >= this.items.size()) {
            this.items.clear();
        }
        else if (count > 0) {
            this.items.subList(0, count).clear();
        }

        return this;
    }

    public Linq<E> skipWhile(Predicate<E> predicate) {
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (!predicate.test(list.get(i))) {
                this.items.subList(0, i).clear();
                break;
            }
        }

        return this;
    }

    /**
     * Removes the very first element in this linq if there are any values
     */
    public Linq<E> removeStart() {
        if (this.hasElements())
            this.items.remove(0);
        return this;
    }

    /**
     * Removes the last element in this linq if there are any values
     */
    public Linq<E> removeEnd() {
        if (this.hasElements())
            this.items.remove(this.items.size() - 1);
        return this;
    }

    /**
     * Removes the first element which passes the given predicate test. If this linq is empty or nothing matches, then the linq is un-changed
     */
    public Linq<E> removeFirst(Predicate<E> predicate) {
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (predicate.test(list.get(i))) {
                list.remove(i);
                break;
            }
        }

        return this;
    }

    /**
     * Removes the last element which passes the given predicate test. If this linq is empty or nothing matches, then the linq is un-changed
     * <p>
     * This function requires iterating through the entire linq in order to determine the last element to pass the test
     * </p>
     */
    public Linq<E> removeLast(Predicate<E> predicate) {
        int lastPass = -1;
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (predicate.test(list.get(i))) {
                lastPass = i;
            }
        }

        if (lastPass != -1) {
            list.remove(lastPass);
        }

        return this;
    }

    /**
     * Generic cast to another type
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <R> Linq<R> cast() {
        return (Linq<R>) this;
    }

    /**
     * Casts each element into the given type (via {@link Class#cast(Object)}); this linq will not be modified, only traversed
     * <p>
     * If any value in this linq cannot be casted, a {@link ClassCastException} is thrown
     * </p>
     * @see Class#cast(Object)
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <R> Linq<R> cast(Class<R> type) {
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            type.cast(list.get(i));
        }

        return (Linq<R>) this;
    }

    /**
     * Keeps every item that is an instance of the given type
     */
    @SuppressWarnings("unchecked")
    public <T> Linq<T> instanceOf(Class<T> clazz) {
        // removeIf(clazz, (a,b) -> !b.isInstance(a));
        return (Linq<T>) filter(clazz, ClassUtils::istEineInstanz, true);
    }

    /**
     * Keeps every item that is an instance of any of the given types
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <TBase> Linq<TBase> instanceOf(Class<? extends TBase>... types) {
        // removeIf(types, (a,b) -> !ClassUtils.isInstanceOfAny(a, b));
        return (Linq<TBase>) filter(types, ClassUtils::istEineInstanzVonEtwas, true);
    }

    /**
     * Keeps every item that is an instance of any of the given types
     */
    @SuppressWarnings("unchecked")
    public final <TBase> Linq<TBase> instanceOf(Collection<Class<? extends TBase>> types) {
        // removeIf(types, (a,b) -> !ClassUtils.isInstanceOfAny(a, b));
        return (Linq<TBase>) filter(types, ClassUtils::istEineInstanzVonEtwas, true);
    }

    /**
     * Returns whether any elements of this linq match the provided predicate
     * @return True if any item does not match (the predicate.test() function returns true)
     */
    @Contract(pure = true)
    public boolean any(Predicate<E> predicate) {
        // return true if any match returns true
        return ListUtils.evaluate(this.items, predicate, true, true); // match.test() == true -> return true
    }


    /**
     * Returns whether any elements of this linq match the provided predicate
     * @return True if any item does not match (the predicate.test() function returns true)
     */
    @Contract(pure = true)
    public <T> boolean any(T parameter, BiPredicate<E, T> predicate) {
        // return true if any match returns true
        return ListUtils.evaluate(this.items, parameter, predicate, true, true); // match.test() == true -> return true
    }

    /**
     * Returns whether any elements of this linq do not match the provided predicate
     * @return True if any item does not match (the predicate.test() function returns false)
     */
    @Contract(pure = true)
    public boolean anyNot(Predicate<E> match) {
        // return true if any returns false
        return ListUtils.evaluate(this.items, match, false, true); // match.test() == false -> return true
    }

    /**
     * Returns whether all elements of this linq match the provided predicate
     * @return True if all elements match (the predicate.test() function always returns true)
     */
    @Contract(pure = true)
    public boolean all(Predicate<E> match) {
        // return false if any match returns false
        return ListUtils.evaluate(this.items, match, false, false); // match.test() == false -> return false
    }

    /**
     * Returns whether all elements of this linq match the provided predicate
     * @return True if all elements match (the predicate.test() function always returns true)
     */
    @Contract(pure = true)
    public <T> boolean all(T value, BiPredicate<E, T> match) {
        return ListUtils.evaluate(this.items, value, match, false, false); // match.test() == false -> return false
    }

    /**
     * Returns whether none of elements of this linq match the provided predicate
     * @return True if all elements do not match (the predicate.test() function always returns false)
     */
    @Contract(pure = true)
    public boolean none(Predicate<E> match) {
        // alternative name: allNot
        // return false if any match returns true
        return ListUtils.evaluate(this.items, match, true, false); // match.test() == true -> return false
    }

    /**
     * Applies an accumulator function on each item in this linq
     * @param source     The initial accumulator value
     * @param aggregator The aggregator function; taking E and T, and returning an instance of T
     * @param <T>        The type that this function returns
     * @return The aggregated value. If this linq is empty, then the initial given value is returned
     */
    public <T> T aggregate(T source, BiFunction<E, T, T> aggregator) {
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            source = aggregator.apply(items.get(i), source);
        }

        return source;
    }

    /**
     * Merges all of the values into a single output value, starting on the left and iterating to the right
     * <p>
     * This is typically only used for numbers (e.g sub or sub elements from others)
     * </p>
     */
    public Optional<E> reduce(BiFunction<E, E, E> function) {
        if (this.hasElements()) {
            E value = this.items.get(0);
            for (int i = 1, size = this.items.size(); i < size; ++i) {
                value = function.apply(value, this.items.get(i));
            }

            return Optional.ofNullable(value);
        }

        return Optional.empty();
    }

    /**
     * Merges all of the values into a single output value, starting on the right and iterating to the left
     * <p>
     * The order of the given function's parameters are from right to left
     * </p>
     * <p>
     * This is typically only used for numbers (e.g sub or sub elements from others)
     * </p>
     */
    public E foldRightRTL(BiFunction<E, E, E> function) {
        if (this.isEmpty())
            return null;

        E value = this.items.get(this.items.size() - 1);
        for (int i = this.items.size() - 2; i >= 0; i--) {
            value = function.apply(this.items.get(i), value);
        }

        return value;
    }

    /**
     * Merges all of the values into a single output value, starting on the right and iterating to the left
     * <p>
     * The order of the given function's parameters are from left to right
     * </p>
     * <p>
     * This is typically only used for numbers (e.g sub or sub elements from others)
     * </p>
     */
    public E foldRightLTR(BiFunction<E, E, E> function) {
        if (this.isEmpty())
            return null;

        E value = this.items.get(this.items.size() - 1);
        for (int i = this.items.size() - 2; i >= 0; i--) {
            value = function.apply(value, this.items.get(i));
        }

        return value;
    }

    /**
     * Generates a hash code, from all elements of this linq, using a similar algorithm that {@link String#hashCode()} uses
     */
    public int getHashCode() {
        return getHashCode(IntAggregateOperator.hash());
    }

    /**
     * Generates a hash code, from all elements of this linq, using the given integer aggregator
     * operator to combine hash codes of objects (which takes the current hash code and the object)
     */
    public int getHashCode(IntAggregateOperator<E> unary) {
        // this process is called aggregation
        // value;
        // for loop
        //     value = something.apply(value)
        // return value;
        // this is faster than the aggregate function due to number boxing

        int hash = 0;
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            hash = unary.apply(items.get(i), hash);
        }

        return hash;
    }

    public Linq<E> clear() {
        this.items.clear();
        return this;
    }

    /**
     * Sorts this linq's items using the given comparator
     */
    public Linq<E> sort(Comparator<E> comparator) {
        this.items.sort(comparator);
        return this;
    }

    /**
     * Sorts this linq's items using a projector to convert the items to a boolean
     */
    public Linq<E> sortByBool(Function<E, Boolean> provider) {
        this.items.sort((a, b) -> Boolean.compare(provider.apply(a), provider.apply(b)));
        return this;
    }

    /**
     * Sorts this linq's items in reverse order using the given comparator
     */
    public Linq<E> sortByReversed(Comparator<E> comparator) {
        this.items.sort((a, b) -> comparator.compare(b, a));
        return this;
    }

    /**
     * Sorts this linq's items in reverse order using a projector to convert the items to a boolean
     */
    public Linq<E> sortByBoolReverse(Function<E, Boolean> provider) {
        this.items.sort((a, b) -> Boolean.compare(provider.apply(b), provider.apply(a)));
        return this;
    }

    /**
     * Orders this linq based on the smallest number given by the {@link ToIntFunction}
     */
    @SuppressWarnings("ComparatorCombinators")
    public Linq<E> sortByMin(ToIntFunction<E> consumer) {
        return this.sort((a, b) -> Integer.compare(consumer.applyAsInt(a), consumer.applyAsInt(b)));
    }

    /**
     * Orders this linq based on the biggest number given by the {@link ToIntFunction}
     */
    public Linq<E> sortByMax(ToIntFunction<E> consumer) {
        return this.sort((a, b) -> Integer.compare(consumer.applyAsInt(b), consumer.applyAsInt(a)));
    }

    /**
     * Orders this linq based on the smallest number given by the {@link ToIntFunction}, using a pin to subtract from the value: consumer - pin
     */
    @SuppressWarnings("ComparatorCombinators")
    public Linq<E> sortByMinRight(int pin, ToIntFunction<E> consumer) {
        return this.sort((a, b) -> Integer.compare(consumer.applyAsInt(a) - pin, consumer.applyAsInt(b) - pin));
    }

    /**
     * Orders this linq based on the biggest number given by the {@link ToIntFunction}, using a pin to subtract from the value: pin - consumer
     */
    @SuppressWarnings("ComparatorCombinators")
    public Linq<E> sortByMinLeft(int pin, ToIntFunction<E> consumer) {
        return this.sort((a, b) -> Integer.compare(pin - consumer.applyAsInt(a), pin - consumer.applyAsInt(b)));
    }

    /**
     * Gives each of this linq's items to the given consumer
     */
    public Linq<E> foreach(Consumer<? super E> action) {
        this.forEach(action);
        return this;
    }

    public Linq<E> tryForeach(Consumer<? super E> action) {
        this.tryForEach(action);
        return this;
    }

    public Linq<E> tryForeach(Consumer<? super E> action, BiConsumer<? super E, Throwable> exceptionHandler) {
        this.tryForEach(action, exceptionHandler);
        return this;
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            action.accept(items.get(i)); // No need for local, as IOOBE may be thrown before accept()
        }
    }

    public <T> void forEach(T parameter, BiConsumer<? super E, T> action) {
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            action.accept(items.get(i), parameter); // No need for local, as IOOBE may be thrown before accept()
        }
    }

    public void tryForEach(Consumer<? super E> action) {
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            E item = items.get(i); // Possible IOOBE, caused by concurrent modification, can be thrown
            try {
                action.accept(item);
            }
            catch (Throwable ignored) {
            }
        }
    }

    public void tryForEach(Consumer<? super E> action, BiConsumer<? super E, Throwable> exceptionHandler) {
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            E item = items.get(i);
            try {
                action.accept(item);
            }
            catch (Throwable ex) {
                exceptionHandler.accept(item, ex);
            }
        }
    }

    /**
     * Removes all null items from this linq
     */
    @SuppressWarnings("ListRemoveInLoop")
    public Linq<E> removeNull() {
        // if (!this.items.isEmpty())
        //     this.items.removeIf(Objects::isNull);

        // this is about 8% faster than the code above
        // avoid constant getfield before optimisation, and also
        // safer in the event of a concurrent assignment to this.items
        ArrayList<E> items = this.items;
        int size = items.size(), i = 0, cursor = 0;
        while (i < size) {
            E value = items.get(i);
            if (value != null) {
                if (cursor != i) { // cursor == i is always true until a null element is spotted
                    items.set(cursor, value);
                }

                ++cursor;
            }

            ++i;
        }

        // the above solution is roughly 2% faster than below
        // probably because excessive set() calls are avoided when
        // no null objects have been seen yet
        // meaning, the above code assumes mostly non-null elements near the start
        // int size = list.size(), i = 0, cursor = 0;
        // while (i < size) {
        //     Object value = list.get(i);
        //     if (value != null) {
        //         list.set(cursor++, (E) value);
        //     }
        //     ++i;
        // }

        // remove elements at the end of the list, if any
        if (i != cursor) {
            // list.subList(cursor, size).clear();
            // could improve with manual remove, removing sublist allocation?
            // reverse direction, to prevent System.arraycopy on each remove() call
            for (int k = size - 1; k >= cursor; --k) {
                items.remove(k);
            }
        }

        // using reflection for array[i] = value or value = array[i] is pointless, as get() and set()
        // calls to ArrayList are optimised and most likely inlined, meaning it will be done anyway without
        // the overhead of reflection. Same with remove(), size()

        return this;
    }

    /**
     * Removes all duplicated items where {@link Objects#equals(Object, Object)} would return
     * true (basically like wrapping this linq's underlying collection in a set)
     * <p>
     * Any null elements are kept, but there will only be a maximum of 1 null element kept
     * </p>
     */
    public Linq<E> removeEqual() {
        // linked to maintain order
        this.items = new ArrayList<E>(new LinkedHashSet<E>(this.items)); // may be faster than below
        // return filterInternal((list, item, index) -> Lists.indexOf(list, item, 0, index) == -1);
        return this;
    }

    public Linq<E> removeRefEqual() {
        return filterRaw((list, item, index) -> ListUtils.indexOfRef(list, item, 0, index) == -1);
        // for (int i = 0, cursor = 0, size = this.items.size(); i < size; ++i) {
        //     E item = this.items.get(i);
        //     boolean contains = Lists.indexOfRef(this.items, item, 0, i) != -1;
        //     if (!contains) {
        //         this.items.set(cursor++, item);
        //     }
        // }
    }

    private interface IterationFilter<T> {
        boolean keepItem(ArrayList<T> list, T item, int index);
    }

    public Linq<E> removeRange(int startIndex, int endIndex) {
        if (this.hasElements()) {
            this.items.subList(startIndex, endIndex).clear();
            // extremely inefficient if endIndex is != the size of the list
            // for (int k = endIndex - 1; k >= startIndex; --k) {
            //     this.items.remove(k);
            // }
        }

        return this;
    }

    /**
     * Invokes {@link Runnable#run()} once
     */
    public Linq<E> run(Runnable runnable) {
        runnable.run();
        return this;
    }

    /**
     * Returns this Linq's list of items
     */
    public ArrayList<E> toList() {
        return this.items;
    }

    public List<E> collect(List<E> list) {
        list.addAll(this.items);
        return list;
    }

    public Linq<E> collectTo(List<E> list) {
        list.addAll(this.items);
        return this;
    }

    /**
     * Returns a copy of this Linq's list of items
     */
    public ArrayList<E> toListCopy() {
        return new ArrayList<E>(this.items);
    }

    /**
     * Returns a new hash set, containing this linq's items
     */
    public HashSet<E> toHashSet() {
        return new HashSet<E>(this.items);
    }

    /**
     * Returns a new array, containing this linq's items
     */
    public Object[] toArray() {
        return this.items.toArray();
    }

    @Nonnull
    public E[] toArray(@Nonnull E[] a) {
        return this.items.toArray(a);
    }

    // Runtime: returns Object[], but because ... java... it can be casted to the real array of type T (T[])
    @SuppressWarnings("unchecked")
    public E[] toArray(Class<E> clazz) {
        return Memory.toArray(this.items, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(Class<T> clazz, Function<E, T> mapper) {
        T[] array = (T[]) Array.newInstance(clazz, this.items.size());
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            array[i] = mapper.apply(this.items.get(i));
        }

        return array;
    }

    /**
     * Adds all of this linq's elements into a String array, using {@link String#valueOf(Object)}
     */
    public String[] toStringArray() {
        return toArray(String.class, String::valueOf);
    }

    /**
     * Adds all of this linq's elements into a String array
     */
    public String[] toStringArray(Function<E, String> toStringFunction) {
        return toArray(String.class, toStringFunction);
    }

    /**
     * Writes this linq's items into the given array, starting at the given offset
     * @param array  The array which will receive this linq's items
     * @param offset The start index (inclusive, in the array)
     */
    public void copyTo(@Nonnull E[] array, int offset) {
        copyTo(array, offset, this.items.size());
    }

    /**
     * Writes this linq's items into the given array, starting at the given offset
     * @param array      The array which will receive this linq's items
     * @param arrayIndex The start index in the array (inclusive)
     * @param count      The number of elements to copy into the array (max is the size of the linq; no exception thrown for an oversized value)
     */
    public void copyTo(@Nonnull E[] array, int arrayIndex, int count) {
        if (arrayIndex < 0)
            throw new IndexOutOfBoundsException("Offset must be non-negative: " + arrayIndex);
        if ((this.items.size() + arrayIndex) > array.length)
            throw new IndexOutOfBoundsException(MessageFormat.format("Array capacity is too small: linq({0}) + offset({1}) > array({2})", this.items.size(), arrayIndex, array.length));

        for (int i = 0, end = Math.max(count, this.items.size()); i < end; ++i, ++arrayIndex) {
            array[arrayIndex] = this.items.get(i);
        }
    }

    /**
     * Converts this linq to a hash map, using the given key and value providers to convert the linq's elements to keys and values
     */
    public <K, V> HashMap<K, V> toMap(Function<E, K> elementToKey, Function<E, V> elementToValue) {
        return toMap(elementToKey, elementToValue, HashMap::new);
    }

    /**
     * Converts this linq to a linked hash map, using the given key and value providers to convert the linq's elements to keys and values
     */
    public <K, V> LinkedHashMap<K, V> toLinkedMap(Function<E, K> elementToKey, Function<E, V> elementToValue) {
        return toMap(elementToKey, elementToValue, LinkedHashMap::new);
    }

    /**
     * Converts this linq to a map, supplied by the given supplier, using the given key and value providers to convert the linq's elements to keys and values
     */
    public <K, V, TMap extends Map<K, V>> TMap toMap(Function<E, K> elementToKey, Function<E, V> elementToValue, Supplier<TMap> mapSupplier) {
        TMap map = mapSupplier.get();
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            E item = list.get(i);
            map.put(elementToKey.apply(item), elementToValue.apply(item));
        }

        return map;
    }

    /**
     * Converts this linq to a hash map, using the given elements as the map's values, and the keys provided by the given function
     */
    public <K> HashMap<K, E> toMapElementAsValue(Function<E, K> elementToKey) {
        return toMapElementAsValue(elementToKey, HashMap::new);
    }

    /**
     * Converts this linq to a linked hash map, using the given elements as the map's values, and the keys provided by the given function
     */
    public <K> LinkedHashMap<K, E> toLinkedMapElementAsValue(Function<E, K> elementToKey) {
        return toMapElementAsValue(elementToKey, LinkedHashMap::new);
    }

    /**
     * Converts this linq to a map, supplied by the given supplier, using the given elements as the map's values, and the keys provided by the given function
     */
    public <K, TMap extends Map<K, E>> TMap toMapElementAsValue(Function<E, K> elementToKey, Supplier<TMap> mapSupplier) {
        TMap map = mapSupplier.get();
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            E item = list.get(i);
            map.put(elementToKey.apply(item), item);
        }
        return map;
    }

    /**
     * Converts this linq to a hash map, using the given elements as the map's keys, and the values provided by the given function
     */
    public <V> HashMap<E, V> toMapElementAsKey(Function<E, V> elementToValue) {
        return toMapElementAsKey(elementToValue, HashMap::new);
    }

    /**
     * Converts this linq to a linked hash map, using the given elements as the map's keys, and the values provided by the given function
     */
    public <V> LinkedHashMap<E, V> toLinkedMapElementAsKey(Function<E, V> elementToValue) {
        return toMapElementAsKey(elementToValue, LinkedHashMap::new);
    }

    /**
     * Converts this linq to a map, supplied by the given supplier, using the given elements as the map's keys, and the values provided by the given function
     */
    public <V, TMap extends Map<E, V>> TMap toMapElementAsKey(Function<E, V> elementToValue, Supplier<TMap> mapSupplier) {
        TMap map = mapSupplier.get();
        ArrayList<E> list = this.items;
        for (int i = 0, size = list.size(); i < size; ++i) {
            E item = list.get(i);
            map.put(item, elementToValue.apply(item));
        }
        return map;
    }

    /**
     * Converts this linq's elements to strings, via {@link String#valueOf(Object)}
     */
    public Linq<String> toStrings() {
        if (this.hasElements()) {
            ArrayList<String> list = new ArrayList<String>(this.items.size());
            this.items.forEach(e -> list.add(String.valueOf(e)));
            this.items = (ArrayList<E>) list;
        }

        return (Linq<String>) this;
    }

    /**
     * Returns a new list of strings elements, using the {@link String#valueOf(Object)} for each of the linq's elements
     */
    public ArrayList<String> toStringList() {
        return toStringList(String::valueOf);
    }

    /**
     * Returns a new list of strings elements
     */
    public ArrayList<String> toStringList(Function<E, String> toStringFunction) {
        ArrayList<String> list = new ArrayList<String>(this.items.size());
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            list.add(toStringFunction.apply(this.items.get(i)));
        }

        return list;
    }

    /**
     * Returns a new hash set of strings elements, using the {@link String#valueOf(Object)} for each of the linq's elements
     */
    public HashSet<String> toStringSet() {
        return toStringSet(String::valueOf);
    }

    /**
     * Returns a new hash set of strings elements
     */
    public HashSet<String> toStringSet(Function<E, String> toStringFunction) {
        HashSet<String> list = new HashSet<String>(this.items.size());
        ArrayList<E> items = this.items;
        for (int i = 0, size = items.size(); i < size; ++i) {
            list.add(toStringFunction.apply(items.get(i)));
        }

        return list;
    }

    /**
     * Converts this linq's elements to integers
     * @throws NumberFormatException The element wasn't a number, and it could not be parsed to an integer
     */
    public Linq<Integer> toIntegers() {
        return map(e -> Integer.parseInt(String.valueOf(e)));
    }

    /**
     * Converts this linq's elements to doubles
     * @throws NumberFormatException The element wasn't already a number, and it could not be parsed to a double
     */
    public Linq<Double> toDoubles() {
        return map(e -> Double.parseDouble(String.valueOf(e)));
    }

    /**
     * Sums all of this linq's number values as an integer, and ignores elements that aren't numbers or that couldn't be parsed as numbers
     */
    public int sum32() {
        int count = 0;
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            E val = this.items.get(i);
            Integer value = val instanceof Integer ? (Integer) val : (val instanceof Number ? ((Number) val).intValue() : null);
            if (value == null)
                value = Integer.parseInt(String.valueOf(val));

            count += value;
        }

        return count;
    }

    /**
     * Sums all of this linq's number values as a long, and ignores elements that aren't numbers or that couldn't be parsed as numbers
     */
    public long sum64() {
        long count = 0;
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            E val = this.items.get(i);
            Long value = val instanceof Long ? (Long) val : (val instanceof Number ? ((Number) val).longValue() : null);
            if (value == null)
                value = Long.parseLong(String.valueOf(val));

            count += value;
        }

        return count;
    }

    /**
     * Uses the given function to convert all non-null element to an integer, and sums the results
     */
    public int sum32(ToIntFunction<E> procedure) {
        int count = 0;
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            E element = this.items.get(i);
            if (element != null) {
                count += procedure.applyAsInt(element);
            }
        }

        return count;
    }

    /**
     * Uses the given function to convert all non-null element to a long, and sums the results
     */
    public long sum64(ToLongFunction<E> procedure) {
        long count = 0;
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            E element = this.items.get(i);
            if (element != null) {
                count += procedure.applyAsLong(element);
            }
        }

        return count;
    }

    /**
     * Returns the number of elements in this linq
     */
    public int size() {
        return this.items.size();
    }

    /**
     * Executes the given consumer, passing this linq's size to it
     */
    public Linq<E> size(IntConsumer consumeSize) {
        consumeSize.accept(this.items.size());
        return this;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public boolean hasElements() {
        return this.items.size() > 0;
    }

    public boolean contains(Predicate<E> predicate) {
        for (int i = 0, size = this.items.size(); i < size; ++i) {
            if (predicate.test(this.items.get(i))) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(E object) {
        return this.items.contains(object);
    }

    public boolean containsAll(@Nonnull Collection<?> c) {
        return this.items.containsAll(c);
    }

    public <T> Linq<E> remove(Function<E, T> function, T value) {
        return this.removeIf(x -> Objects.equals(function.apply(x), value));
    }

    public <T> Linq<E> remove(Function<E, T> function, T a, T b) {
        return this.removeIf(x -> ObjUtils.equals(function.apply(x), a, b));
    }

    public <T> Linq<E> remove(Function<E, T> function, Object... values) {
        return this.removeIf(x -> ObjUtils.equals(function.apply(x), values));
    }

    public Linq<E> remove(E object) {
        this.items.remove(object);
        return this;
    }

    public Linq<E> removeAll(E a, E b) {
        this.items.remove(a);
        this.items.remove(b);
        return this;
    }

    public Linq<E> removeAll(E a, E b, E c) {
        this.items.remove(a);
        this.items.remove(b);
        this.items.remove(c);
        return this;
    }

    @SafeVarargs
    public final Linq<E> removeAll(E... vars) {
        this.items.removeAll(Arrays.asList(vars));
        return this;
    }

    public Linq<E> removeAll(@Nonnull Collection<? extends E> c) {
        if (this.hasElements() && !c.isEmpty())
            this.items.removeAll(c);

        return this;
    }

    public Linq<E> retainAll(@Nonnull Collection<? extends E> c) {
        if (this.hasElements() && !c.isEmpty())
            this.items.retainAll(c);
        return this;
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        return this.items.iterator();
    }

    public Class<E> getTypeForElements() {
        return getTypeForElements(false);
    }

    public Class<E> getTypeForElements(boolean findCommonSuper) {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot get type from empty linq");
        }

        Linq<Class<E>> linq = of(this).removeNull().classes().removeEqual();
        if (linq.size() == 1) {
            return linq.get(0);
        }
        else if (!findCommonSuper) {
            throw new IllegalStateException("Linq contains multiple types of objects: " + this);
        }
        else {
            throw new UnsupportedOperationException("findCommonSuper not supported: " + this);
            // return (Class<E>) linq.first().orElseThrow(Error::new);
        }
    }

    private static Class<?> getCommonSuperclass(Class<?> class1, Class<?> class2) {
        while (!class1.isAssignableFrom(class2)) {
            class1 = class1.getSuperclass();
        }
        return class1;
    }

    /**
     * Copies this linq's values into a new linq
     */
    @Contract(value = "-> new", pure = true)
    public Linq<E> copy() {
        return new Linq<E>(new ArrayList<E>(this.items));
    }

    /**
     * Joins this linq's elements to a single string, separating the {@link String#valueOf(Object)} of each element by the given delimiter
     * @param delimiter The element delimiter
     * @return A string
     */
    @Nonnull
    public String toString(@Nullable String delimiter) {
        return toString(delimiter, String::valueOf, "");
    }

    /**
     * Joins this linq's elements to a single string, separating the {@link String#valueOf(Object)} of each element by the given delimiter
     * @param emptyValue The value to use if this linq is empty
     * @return A string
     */
    @Nonnull
    public String toString(@Nullable String delimiter, String emptyValue) {
        return toString(delimiter, String::valueOf, emptyValue);
    }

    /**
     * Joins this linq's elements to a single string, using the given converter to get a string for each element, separated by the given delimiter
     * @param delimiter The element delimiter
     * @param mapper    A function that maps an element to a string. Elements passed may be null
     * @return A string
     */
    public String toString(@Nullable String delimiter, @Nonnull Function<E, String> mapper) {
        return toString(delimiter, mapper, "");
    }

    /**
     * Joins this linq's elements to a single string, using the given converter to get a string for each element, separated by the given delimiter
     * @param delimiter  The element delimiter
     * @param mapper     A function that maps an element to a string. Elements passed may be null
     * @param emptyValue The value to use if this linq is empty
     * @return A string
     */
    @Nonnull
    public String toString(@Nullable String delimiter, @Nonnull Function<E, String> mapper, String emptyValue) {
        if (this.isEmpty()) {
            return emptyValue;
        }

        if (delimiter == null) {
            delimiter = ", ";
        }

        int size = this.items.size();
        StringBuilder sb = new StringBuilder(size * 3);
        for (int i = 0, end = (size - 1); i < end; ++i) {
            sb.append(mapper.apply(this.items.get(i))).append(delimiter);
        }

        return sb.append(mapper.apply(this.items.get(size - 1))).toString();
    }

    /**
     * Joins all elements in this linq using the delimiter, apart from the last 2 elements, which are joined with finalDelimiter
     * <p>
     * ["hello", "there", "lol"].toAdvancedString(", ", " & ") -> "hello, there & lol"
     * </p>
     * @param delimiter      The delimiter for all elements apart from the last 2
     * @param finalDelimiter The delimiter for the last 2 elements
     * @return A string
     */
    @Nonnull
    public String toAdvancedString(@Nullable String delimiter, @Nonnull String finalDelimiter) {
        return toAdvancedString(delimiter, finalDelimiter, "", String::valueOf);
    }

    /**
     * Joins all elements in this linq using the delimiter, apart from the last 2 elements, which are joined with finalDelimiter
     * <p>
     * ["hello", "there", "lol"].toAdvancedString(", ", " & ") -> "hello, there & lol"
     * </p>
     * @param delimiter      The delimiter for all elements apart from the last 2
     * @param finalDelimiter The delimiter for the last 2 elements
     * @param emptyValue     The value to use if this linq is empty
     * @return A string
     */
    public String toAdvancedString(@Nullable String delimiter, @Nonnull String finalDelimiter, String emptyValue) {
        return toAdvancedString(delimiter, finalDelimiter, emptyValue, String::valueOf);
    }


    /**
     * Joins all elements in this linq using the delimiter, apart from the last 2 elements, which are joined with finalDelimiter
     * <p>
     * ["hello", "there", "lol"].toAdvancedString(", ", " & ") -> "hello, there & lol"
     * </p>
     * @param delimiter      The delimiter for all elements apart from the last 2
     * @param finalDelimiter The delimiter for the last 2 elements
     * @param mapper         The function that maps elements to a string. Elements passed may be null
     * @return A string
     */
    public String toAdvancedString(@Nullable String delimiter, @Nonnull String finalDelimiter, @Nonnull Function<E, String> mapper) {
        return toAdvancedString(delimiter, finalDelimiter, "", mapper);
    }

    /**
     * Joins all elements in this linq using the delimiter, apart from the last 2 elements, which are joined with finalDelimiter
     * <p>
     * ["hello", "there", "lol"].toAdvancedString(", ", " & ") -> "hello, there & lol"
     * </p>
     * @param delimiter      The delimiter for all elements apart from the last 2
     * @param finalDelimiter The delimiter for the last 2 elements
     * @param emptyValue     The value to use if this linq is empty
     * @param mapper         The function that maps elements to a string. Elements passed may be null
     * @return A string
     */
    public String toAdvancedString(@Nullable String delimiter, @Nonnull String finalDelimiter, String emptyValue, @Nonnull Function<E, String> mapper) {
        int size = this.items.size();
        if (size == 0) {
            return emptyValue;
        }
        else if (size == 1) {
            return mapper.apply(this.items.get(0));
        }
        else {
            if (delimiter == null) {
                delimiter = ", ";
            }

            StringBuilder sb = new StringBuilder(size * 4);
            for (int i = 0, end = (size - 2); i < end; ++i) {
                sb.append(mapper.apply(this.items.get(i))).append(delimiter);
            }

            sb.append(mapper.apply(this.items.get(size - 2))).append(finalDelimiter);
            return sb.append(mapper.apply(this.items.get(size - 1))).toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof Linq && ((Linq<?>) obj).equals(this));
    }

    /**
     * Checks if every element of this linq is equal to event element of the given linq
     * <p>
     * A more appropriate name would be "collectionEquals"
     * </p>
     */
    public boolean equals(Linq<?> linq) {
        if (this.items.size() != linq.size()) {
            return false;
        }

        for (int i = 0, size = this.items.size(); i < size; ++i) {
            if (!Objects.equals(this.items.get(i), linq.items.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Linq<E> clone() {
        return of(this.items);
    }

    /**
     * Used by the {@link Linq#toString()} function to return a representation of this linq, supporting
     * a custom prefix (which is usually "Linq", but it can be changed to represent another type)
     */
    public final String getPrefixedRepresentation(String prefix) {
        if (this.isEmpty()) {
            return prefix + "<>(0 elements)";
        }
        else if (this.items.size() == 1) {
            Object first = this.items.get(0);
            if (first == null) {
                return prefix + "<?>(1 null element)";
            }
            else {
                return MessageFormat.format("{0}<{1}>(1 element)", prefix, ClassUtils.getClassFileName(first.getClass()));
            }
        }
        else {
            ArrayList<String> typeNames = new ArrayList<String>();
            for (Class<E> clazz : of(this).classes().removeRefEqual()) {
                if (clazz != null) {
                    TypeVariable<?>[] params = clazz.getTypeParameters();
                    if (params.length == 0) {
                        typeNames.add(ClassUtils.getClassFileName(clazz));
                    }
                    else {
                        typeNames.add(MessageFormat.format("{0}<{1}>", ClassUtils.getClassFileName(clazz), Linq.of(params).map(TypeVariable::getName).toString(", ")));
                    }
                }
                else {
                    typeNames.add("null");
                }
            }

            return MessageFormat.format("{0}<{1}>({2} elements)", prefix, Linq.wrap(typeNames).toString(", "), this.items.size());
        }
    }

    /**
     * Returns a readable string representation that describes what element types this linq contains and it's size. Does not explain the actual elements
     */
    @Override
    public final String toString() {
        return getPrefixedRepresentation("Linq");
    }

    public Linq<E> validate(Predicate<Linq<E>> validate) {
        if (!validate.test(this))
            throw new IllegalStateException("Validation failed");
        return this;
    }

    public static <E, T> boolean evaluate(ArrayList<E> list, T param, BiPredicate<E, T> predicate, boolean complement, boolean onComplementMatch) {
        return ListUtils.evaluate(list, param, predicate, complement, onComplementMatch);
    }
}
