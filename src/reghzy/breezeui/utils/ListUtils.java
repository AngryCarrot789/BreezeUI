package reghzy.breezeui.utils;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListUtils {
    // value = array[i++ % array.length]

    @Contract("_ -> new")
    public static <E> ArrayList<E> toList(Collection<? extends E> collection) {
        return new ArrayList<E>(collection);
    }

    @Contract("_,_ -> new")
    public static <E> ArrayList<E> toList(Iterable<? extends E> iterable, int initialListSize) {
        ArrayList<E> list = new ArrayList<E>(initialListSize);
        if (iterable instanceof Collection) {
            list.addAll((Collection) iterable);
        }
        else {
            for (E e : iterable) {
                list.add(e);
            }
        }

        return list;
    }

    @Contract("_ -> new")
    public static <E> ArrayList<E> toList(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new ArrayList<E>((Collection<E>) iterable);
        }

        ArrayList<E> list = new ArrayList<E>();
        for (E e : iterable)
            list.add(e);
        return list;
    }

    @Contract("_ -> new")
    public static <E> ArrayList<E> toList(Iterator<? extends E> iterable) {
        ArrayList<E> list = new ArrayList<E>();
        while (iterable.hasNext())
            list.add(iterable.next());
        return list;
    }

    @Contract("_ -> new")
    public static <E> ArrayList<E> toList(E[] array) {
        ArrayList<E> list = new ArrayList<E>(array.length);
        for (int i = 0, length = array.length; i < length; i++)
            list.add(array[i]);
        return list;
    }

    public static <E> ArrayList<E> toListFromObjArray(Object[] array) {
        ArrayList<E> list = new ArrayList<E>(array.length);
        for (Object e : array)
            list.add((E) e);
        return list;
    }

    @Contract("_ -> new")
    public static ArrayList<Integer> ofArray(int[] array) {
        ArrayList<Integer> list = new ArrayList<Integer>(array.length);
        for (int i : array)
            list.add(i);
        return list;
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns the given list, or an empty list (no list instances will be created). It is highly recommended to only use this if the list will not be modified
     */
    @Nonnull
    public static <E> List<E> ofNonNull(List<E> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> T match(Collection<T> collection, Predicate<T> matcher) {
        for (T value : collection) {
            if (matcher.test(value))
                return value;
        }

        return null;
    }

    @Contract("_,_ -> new")
    public static <T> ArrayList<T> sort(ArrayList<T> list, Comparator<T> comparator) {
        Object[] array = list.toArray();
        Arrays.sort(array, (Comparator) comparator);
        return (ArrayList<T>) ListUtils.toList(array);
    }

    @Contract("_,_ -> new")
    public static <T> LinkedList<T> sort(LinkedList<T> list, Comparator<T> comparator) {
        Object[] array = list.toArray();
        Arrays.sort(array, (Comparator) comparator);
        LinkedList<T> linkedList = new LinkedList<T>();
        for (Object obj : array) {
            linkedList.add((T) obj);
        }

        return linkedList;
    }

    /**
     * Adds the element if {@link Collection#contains(Object)} returns false
     */
    public static <T> boolean tryAdd(Collection<T> collection, T value) {
        return !collection.contains(value) && collection.add(value);
    }

    public static <E> List<E> nonNullOr(List<E> list, Supplier<List<E>> supplier) {
        if (list != null) {
            return list;
        }
        else {
            return supplier.get();
        }
    }

    @Contract("_->new")
    public static <T> ArrayList<T> singleElement(T value) {
        ArrayList<T> list = new ArrayList<T>();
        list.add(value);
        return list;
    }

    public static <T> ArrayList<T> reversed(ArrayList<T> list) {
        ArrayList<T> reverse = new ArrayList<T>(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            reverse.add(list.get(i));
        }
        return reverse;
    }

    public static <T> ArrayList<T> of(T[] array) {
        return Linq.of(array).toList();
    }

    public static <E> boolean isEmpty(@Nullable E[] array) {
        return array == null || array.length == 0;
    }

    public static <E> int indexOfRef(ArrayList<E> list, E item) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (list.get(i) == item) {
                return i;
            }
        }

        return -1;
    }

    public static <E> int indexOfRef(ArrayList<E> list, E item, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            if (list.get(i) == item) {
                return i;
            }
        }

        return -1;
    }

    public static <E> int indexOf(ArrayList<E> list, E item, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            if (list.get(i) == item) {
                return i;
            }
        }

        return -1;
    }

    public static <E> ArrayList<E> newArrayList(Collection<? extends E> collection) {
        return new ArrayList<E>(collection);
    }

    /**
     * Evaluate the state of the items
     * @param list              The list to evaluate
     * @param predicate         The predicate to test the linq items on
     * @param complement        A complement value to compare with the test result of the predicate value
     * @param onComplementMatch The return value if the predicate and complement match
     * @param <E>               Element type (of the items in the list)
     * @return onPassValue if the predicate and complement match. Otherwise, returns the opposite of onPassValue if the predicate never matches
     */
    @SuppressWarnings("PointlessBooleanExpression")
    public static <E> boolean evaluate(List<E> list, Predicate<E> predicate, boolean complement, boolean onComplementMatch) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (predicate.test(list.get(i)) == complement) {
                return onComplementMatch;
            }
        }

        return onComplementMatch ^ true;
    }

    /**
     * Evaluate the state of the items
     * @param list              The list to evaluate
     * @param param             A 2nd parameter to pass to the predicate (to prevent local variables being captured, requiring lambda anonymous class instantiation)
     * @param predicate         The predicate to test the linq items on
     * @param complement        A complement value to compare with the test result of the predicate value
     * @param onComplementMatch The return value if the predicate and complement match
     * @param <E>               Element type (of the items in the list)
     * @param <T>               Additional parameter type
     * @return onPassValue if the predicate and complement match. Otherwise, returns the opposite of onPassValue if the predicate never matches
     */
    @SuppressWarnings("PointlessBooleanExpression")
    public static <E, T> boolean evaluate(ArrayList<E> list, T param, BiPredicate<E, T> predicate, boolean complement, boolean onComplementMatch) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            if (predicate.test(list.get(i), param) == complement) {
                return onComplementMatch;
            }
        }

        return onComplementMatch ^ true;
    }
}
